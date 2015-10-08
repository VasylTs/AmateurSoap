/*
 * Copyright (C) 2015 VasylcTS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package vasylcts.soap.util;

import vasylcts.soap.util.assisttypes.exception.SoapExceptionServer;
import vasylcts.soap.util.assisttypes.ActionDescription;
import vasylcts.soap.util.assisttypes.EnumAnswerType;
import vasylcts.soap.util.assisttypes.exception.SoapExceptionServerCreateWsdlXsd;
import vasylcts.soap.util.log.StandartLogger;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author VasylcTS
 */
public class XSDBuilder {

    private static final Logger logger = StandartLogger.getLogger();

    private static XSDBuilder builder;
    private DocumentBuilder db;

    private final String SERVICE_NAMESPACE;
    static private final String SERVICE_NAMESPACE_PREFIX = "tns";
    static private final String SCHEMA_NAMESPACE_PREFIX = "xs";

    public static XSDBuilder getInstance() {
        if (builder == null) {
            builder = new XSDBuilder();
        }
        return builder;
    }

    private XSDBuilder() {
        if (WSDLBuilder.getPORT_NAMESPACE() == null) {
            WSDLBuilder.getInstance(null); // Init satic param
            SERVICE_NAMESPACE = WSDLBuilder.getPORT_NAMESPACE();
        } else {
            SERVICE_NAMESPACE = WSDLBuilder.getPORT_NAMESPACE();
        }
    }

    /**
     *
     * @param params {@code Map<String, ActionDescription>}, where:
     * <br> {@code String} - action name
     * <br> {@code ActionDescription} - action description
     * <br>
     * @param methodList list of all granted actions to user. If there is no
     * description for action, it would not be added to XSD.
     * <p>
     * @return ready XSD.
     */
    public String makeXSDFromInOutMaps(Map<String, ActionDescription> params, List<String> methodList) throws SoapExceptionServer {
        StringBuilder sb = new StringBuilder();

        appendHeader(sb);
        appendSysinfo(sb);
        appendActionsXSDs(sb, params, methodList);
        appendEnd(sb);

        return sb.toString();
    }

    public void appendHeader(StringBuilder sb) {
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
                .append("<").append(SCHEMA_NAMESPACE_PREFIX).append(":schema xmlns:").append(SERVICE_NAMESPACE_PREFIX).append("=\"").append(SERVICE_NAMESPACE).append("\"")
                .append(" xmlns=\"").append(SERVICE_NAMESPACE).append("\"")
                .append(" targetNamespace=\"").append(SERVICE_NAMESPACE).append("\"")
                .append(" xmlns:").append(SCHEMA_NAMESPACE_PREFIX).append("=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\"\n")
                .append(" attributeFormDefault=\"unqualified\">");
    }

    public void appendSysinfo(StringBuilder sb) {
        sb.append("<").append(SCHEMA_NAMESPACE_PREFIX).append(":simpleType name=\"sNumber10\">\n")
                .append("  <").append(SCHEMA_NAMESPACE_PREFIX).append(":restriction base=\"").append(SCHEMA_NAMESPACE_PREFIX).append(":integer\">\n")
                .append("    <").append(SCHEMA_NAMESPACE_PREFIX).append(":minInclusive value=\"0\"/>\n")
                .append("    <").append(SCHEMA_NAMESPACE_PREFIX).append(":maxInclusive value=\"9999999999\"/>\n")
                .append("  </").append(SCHEMA_NAMESPACE_PREFIX).append(":restriction>\n")
                .append("</").append(SCHEMA_NAMESPACE_PREFIX).append(":simpleType>\n");

        for (EnumAnswerType wrapType : EnumAnswerType.values()) {
            if (wrapType.getWrapperType() != null) {
                appendStandartComplexType(sb, wrapType.getWrapperType(), wrapType.getChildWrapperType());
            }
        }

        String schemaString = SCHEMA_NAMESPACE_PREFIX + ":string";
        appendComplexType(sb, "error", new SimpleParam("code", SCHEMA_NAMESPACE_PREFIX + ":int"),
                new SimpleParam("message", schemaString),
                new SimpleParam("detail", schemaString));

        appendComplexType(sb, "additionalInfoRequestObj", new SimpleParam("someClientID", "sNumber10", true),
                new SimpleParam("someClientCode", schemaString));

        appendComplexType(sb, "additionalInfoResponseObj", new SimpleParam("serverID", "sNumber10"),
                new SimpleParam("serverCode", schemaString));

        appendComplexType(sb, "additionalInfoRequest", new SimpleParam("additionalInfo", SERVICE_NAMESPACE_PREFIX + ":additionalInfoRequestObj", true));
        appendComplexType(sb, "additionalInfoResponse", new SimpleParam("additionalInfo", SERVICE_NAMESPACE_PREFIX + ":additionalInfoResponseObj", true),
                new SimpleParam("error", SERVICE_NAMESPACE_PREFIX + ":error"));

    }

    /**
     * Append complexType with String mandatory elements
     * <p>
     * @param sb Where to append
     * @param typeName Element name
     * @param elementNames Mandatory String elements
     */
    private void appendStandartComplexType(StringBuilder sb, String typeName, String... elementNames) {
        sb.append("<").append(SCHEMA_NAMESPACE_PREFIX).append(":complexType name=\"").append(typeName).append("\">\n");
        sb.append("  <").append(SCHEMA_NAMESPACE_PREFIX).append(":sequence>\n");
        for (String elementName : elementNames) {
            sb.append("    <").append(SCHEMA_NAMESPACE_PREFIX).append(":element name=\"").append(elementName).append("\" minOccurs=\"1\" maxOccurs=\"1\" type=\"").append(SCHEMA_NAMESPACE_PREFIX).append(":string\"/>\n");
        }
        sb.append("  </").append(SCHEMA_NAMESPACE_PREFIX).append(":sequence>\n");
        sb.append("</").append(SCHEMA_NAMESPACE_PREFIX).append(":complexType>\n");
    }

    /**
     * Append complexType with any elements
     * <p>
     * @param sb Where to append
     * @param typeName Element name
     * @param elementNames elements and their description
     */
    private void appendComplexType(StringBuilder sb, String typeName, SimpleParam... elementDescr) {
        sb.append("<").append(SCHEMA_NAMESPACE_PREFIX).append(":complexType name=\"").append(typeName).append("\">\n");
        sb.append("  <").append(SCHEMA_NAMESPACE_PREFIX).append(":sequence>\n");
        for (SimpleParam element : elementDescr) {
            sb.append("    <").append(SCHEMA_NAMESPACE_PREFIX).append(":element name=\"")
                    .append(element.paramName)
                    .append("\" type=\"")
                    .append(element.paramValue)
                    .append("\" minOccurs=\"")
                    .append(element.isRequired ? "1" : "0")
                    .append("\" maxOccurs=\"1\"/>\n");
        }
        sb.append("  </").append(SCHEMA_NAMESPACE_PREFIX).append(":sequence>\n");
        sb.append("</").append(SCHEMA_NAMESPACE_PREFIX).append(":complexType>\n");
    }

    public void appendEnd(StringBuilder sb) {
        sb.append("</").append(SCHEMA_NAMESPACE_PREFIX).append(":schema>");
    }

    private void parseXSD(String xsd) throws ParserConfigurationException, UnsupportedEncodingException, SAXException, IOException {
        if (xsd == null) {
            throw new NullPointerException("There is no xsd for element!");
        }
        if (this.db == null) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
        }

        db.parse(new ByteArrayInputStream(("<root xmlns:" + SCHEMA_NAMESPACE_PREFIX + "=\"http://www.w3.org/2001/XMLSchema\">" + xsd + "</root>").getBytes("utf-8")));
    }

    /**
     * @return the service namaspace from wsdl
     */
    public String getServiceNamespace() {
        return SERVICE_NAMESPACE;
    }

    private void appendActionsXSDs(StringBuilder sb, Map<String, ActionDescription> params, List<String> methodList) throws SoapExceptionServerCreateWsdlXsd {
        for (Iterator<String> actionNameIter = methodList.iterator(); actionNameIter.hasNext();) {
            String actionName = actionNameIter.next();
            if (params.containsKey(actionName)) {
                ActionDescription actionDesc = params.get(actionName);
                if (actionDesc.isMinParamsSetted()) {
                    boolean isAppendMsg = true;
                    if (logger.getLevel().intValue() < Level.FINE.intValue()) {
                        try {
                            logger.log(Level.FINER, "Trying to validate XSD for action {0}. xsd = {1}", new Object[]{actionDesc.getAction(), actionDesc.getInXSD()});
                            parseXSD(actionDesc.getInXSD());
                            logger.log(Level.FINE, "XSD for element {0} successfully validated!", actionDesc.getInElementName());
                            logger.log(Level.FINER, "Trying to validate XSD for action {0}. xsd = {1}", new Object[]{actionDesc.getAction(), actionDesc.getOutXSD()});
                            parseXSD(actionDesc.getOutXSD());
                            logger.log(Level.FINE, "XSD for element {0} successfully validated!", actionDesc.getOutElementName());
                        } catch (ParserConfigurationException | SAXException | IOException ex) {
                            isAppendMsg = false;
                            logger.log(Level.SEVERE, "Error while creating xsd. Invalid xsd for element. Action name - ''{0}'' caused by: {1}", new Object[]{actionName, ex.toString()});
                            actionNameIter.remove();
                            //throw new SoapExceptionServerCreateWsdlXsd("Error while creating wsdl", "Invalid xsd for element. Action name - '" + actionName + "' caused by: " + ex.getLocalizedMessage(), ex);
                        }
                    }
                    if (isAppendMsg) {
                        sb.append(actionDesc.getInXSD()).append("\n");
                        sb.append(actionDesc.getOutXSD()).append("\n");
                    }
                } else // 
                {
                    logger.log(Level.WARNING, "Params not setted for action {0}.", actionName);
                    logger.log(Level.FINEST, "Received params: {0}", actionDesc.toString());
                    actionNameIter.remove();
                }
            } else // There is no such action in Map
            {
                //logger.log(Level.WARNING, "There is no action description for {0}", actionName);
                actionNameIter.remove();
            }
        }
    }

    public static String getSERVICE_NAMESPACE_PREFIX() {
        return SERVICE_NAMESPACE_PREFIX;
    }

    public static String getSCHEMA_NAMESPACE_PREFIX() {
        return SCHEMA_NAMESPACE_PREFIX;
    }
}

/**
 * Class for describing element in xsd
 * <p>
 * @author VasylcTS
 */
class SimpleParam {

    public final String paramName, paramValue;
    public final boolean isRequired;

    SimpleParam(String paramName, String paramValue, boolean isRequired) {
        this.paramName = paramName;
        this.paramValue = paramValue;
        this.isRequired = isRequired;
    }

    SimpleParam(String paramName, String paramValue) {
        this.paramName = paramName;
        this.paramValue = paramValue;
        this.isRequired = false;
    }
}
