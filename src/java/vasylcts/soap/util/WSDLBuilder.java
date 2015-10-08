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
import vasylcts.soap.util.log.StandartLogger;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
public class WSDLBuilder {

    private static final Logger logger = StandartLogger.getLogger();

    private static String PORT_NAMESPACE = "http://www.somemyrandomsitewiththissoap.com.ua/AmauterSoap";
    private static String ADDRESS_LOCATION;
    private final String PORT_NAME;
    private final String BINDER_NAME;
    private final String SERVICE_NAME;

    private DocumentBuilder db;

    private static WSDLBuilder wsdlBuilder;

    private static WSDLBuilder getInstance() {
        if (wsdlBuilder == null) {
            wsdlBuilder = new WSDLBuilder();
        }
        return wsdlBuilder;
    }

    public static WSDLBuilder getInstance(URL address_location) {
        if (wsdlBuilder == null || !Objects.equals(address_location.toString(), ADDRESS_LOCATION)) {
            wsdlBuilder = new WSDLBuilder(address_location);
        }
        return wsdlBuilder;
    }

    private WSDLBuilder() {
        PORT_NAME = "AmauterSoapPortType";
        BINDER_NAME = "AmauterSoapBinder";
        SERVICE_NAME = "AmauterSoap";
    }

    private WSDLBuilder(URL address_location) {
        ADDRESS_LOCATION = address_location.toString();
        PORT_NAME = "AmauterSoapPortType";
        BINDER_NAME = "AmauterSoapBinder";
        SERVICE_NAME = "AmauterSoap";
    }

    private WSDLBuilder(String port_namespace, URL address_location) {
        PORT_NAMESPACE = port_namespace;
        ADDRESS_LOCATION = address_location.toString();
        PORT_NAME = "AmauterSoapPortType";
        BINDER_NAME = "AmauterSoapBinder";
        SERVICE_NAME = "AmauterSoap";
    }

    private WSDLBuilder(String port_namespace, URL address_location, String port_name, String binder_name, String service_name) {
        PORT_NAMESPACE = port_namespace;
        ADDRESS_LOCATION = address_location.toString();
        PORT_NAME = port_name;
        BINDER_NAME = binder_name;
        SERVICE_NAME = service_name;
    }

    private WSDLBuilder(String port_namespace, URL address_location, String port_name, String binder_name, String service_name, String request_name_ending, String response_name_ending) {
        PORT_NAMESPACE = port_namespace;
        ADDRESS_LOCATION = address_location.toString();
        PORT_NAME = port_name;
        BINDER_NAME = binder_name;
        SERVICE_NAME = service_name;
    }

    final public String makeWSDLFromInOutMaps(Map<String, ActionDescription> params, List<String> actionNames) throws SoapExceptionServer {
        try {
            List<String> copyOfActionNames = new LinkedList<>(actionNames);
            Collections.copy(copyOfActionNames, actionNames);
            StringBuilder sb = new StringBuilder();

            // <wsdl:definitions name=...
            appendHeader(sb);
            // <wsdl:types><xsd:schema>...
            appendXsd(sb);
            // <wsdl:message name=...
            appendMessages(sb, params, copyOfActionNames);
            // <wsdl:portType name=...
            // <wsdl:binding ...>
            appendPortAndBinding(sb, params, copyOfActionNames);
            // <wsdl:service ...>
            appendService(sb);
            // </wsdl:definitions>
            appendEnding(sb);

            return sb.toString();
        } //        catch (SoapExceptionServer ex)
        //        {
        //            throw ex;
        //        }
        catch (Exception ex) {
            throw new SoapExceptionServer("Error while generating wsdl.", ex.getMessage(), ex);
        }
    }

    private void appendHeader(StringBuilder sb) {
        sb.append("<?xml version='1.0' encoding='UTF-8'?>\n")
                .append("<wsdl:definitions name=\"AmauterSoap\" ")
                .append("targetNamespace=\"").append(getPORT_NAMESPACE()).append("\" ")
                .append("xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" ")
                .append("xmlns:http=\"http://schemas.xmlsoap.org/wsdl/http/\" ")
                .append("xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" ")
                .append("xmlns:tns=\"").append(getPORT_NAMESPACE()).append("\" ")
                .append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ")
                .append("xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\" ")
                .append("xmlns:mime=\"http://schemas.xmlsoap.org/wsdl/mime/\" ")
                .append("xmlns:soap12=\"http://schemas.xmlsoap.org/wsdl/soap12/\">\n");
    }

    private void appendXsd(StringBuilder sb) {
        sb.append("<wsdl:types>\n")
                .append("<xsd:schema>\n")
                .append("<xsd:import namespace=\"").append(getPORT_NAMESPACE()).append("\" ")
                .append("schemaLocation=\"").append(getADDRESS_LOCATION()).append("?xsd\"/>\n")
                .append("</xsd:schema>")
                .append("</wsdl:types>\n");
    }

    private void appendMessages(StringBuilder sb, Map<String, ActionDescription> params, List<String> copyOfActionNames) {
        for (Iterator<String> actionNameIter = copyOfActionNames.iterator(); actionNameIter.hasNext();) {
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
                            logger.log(Level.SEVERE, "Error while creating wsdl. Invalid xsd for element. Action name - ''{0}'' caused by: {1}", new Object[]{actionName, ex.toString()});
                            actionNameIter.remove();
                            //throw new SoapExceptionServerCreateWsdlXsd("Error while creating wsdl", "Invalid xsd for element. Action name - '" + actionName + "' caused by: " + ex.getLocalizedMessage(), ex);
                        }
                    }
                    if (isAppendMsg) {
                        appendRequestResponseMessageToWsdl(sb, actionDesc.getInElementName());
                        appendRequestResponseMessageToWsdl(sb, actionDesc.getOutElementName());
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

    private void appendPortAndBinding(StringBuilder sb, Map<String, ActionDescription> params, List<String> copyOfActionNames) {
        StringBuilder messageSB = new StringBuilder();
        StringBuilder operationSB = new StringBuilder();

        sb.append("<wsdl:portType name=\"").append(getPORT_NAME()).append("\">\n");
        for (String actionName : copyOfActionNames) {
            messageSB.append("<wsdl:operation name=\"").append(actionName).append("\">\n");
            appendWSDLMessage(messageSB, params.get(actionName).getInElementName(), true);
            appendWSDLMessage(messageSB, params.get(actionName).getOutElementName(), false);
            messageSB.append("</wsdl:operation>\n");

            appendWSDLOpearation(operationSB, actionName);
        }
        sb.append(messageSB);
        sb.append("</wsdl:portType>\n");

        // <wsdl:binding ...>
        appendBinding(sb, operationSB.toString());
    }

    private void appendService(StringBuilder sb) {
        sb.append("<wsdl:service name=\"").append(getSERVICE_NAME()).append("\">\n")
                .append("<wsdl:port name=\"").append(getPORT_NAME())
                .append("\" binding=\"tns:").append(getBINDER_NAME()).append("\">\n")
                .append("<soap:address location=\"").append(getADDRESS_LOCATION()).append("\"/>\n")
                .append("</wsdl:port>\n")
                .append("</wsdl:service>\n");
    }

    private void appendBinding(StringBuilder sb, String operations) {
        sb.append("<wsdl:binding name=\"").append(getBINDER_NAME()).append("\" type=\"tns:").append(getPORT_NAME()).append("\">\n")
                .append("<soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/>\n");
        sb.append(operations);
        sb.append("</wsdl:binding>\n");
    }

    private void appendRequestResponseMessageToWsdl(StringBuilder sb, String actionReqRespName) {
        sb.append("<wsdl:message name=\"").append(getPORT_NAME()).append("_").append(actionReqRespName).append("\">\n");
        sb.append("<wsdl:part name=\"parameters\" element=\"tns:").append(actionReqRespName).append("\"/>\n");
        sb.append("</wsdl:message>\n");
    }

    private void appendWSDLMessage(StringBuilder sb, String messageName, boolean isInput) {
        sb.append("<wsdl:")
                .append(isInput ? "input" : "output")
                .append(" message=\"tns:")
                .append(getPORT_NAME())
                .append("_")
                .append(messageName)
                .append("\"/>\n");
    }

    private void appendWSDLOpearation(StringBuilder sb, String operationName) {
        sb.append("<wsdl:operation name=\"").append(operationName).append("\">\n")
                .append("<soap:operation style=\"document\" soapAction=\"").append(operationName).append("\"/>\n")
                .append("<wsdl:input>\n")
                .append("<soap:body use=\"literal\"/>\n")
                .append("</wsdl:input>\n")
                .append("<wsdl:output>\n")
                .append("<soap:body use=\"literal\"/>\n")
                .append("</wsdl:output>\n")
                .append("</wsdl:operation>\n");
    }

    private void appendEnding(StringBuilder sb) {
        sb.append("</wsdl:definitions>");
    }

    private void parseXSD(String xsd) throws ParserConfigurationException, UnsupportedEncodingException, SAXException, IOException {
        if (xsd == null) {
            throw new NullPointerException("There is no xsd for element!");
        }
        if (this.db == null) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
        }

        db.parse(new ByteArrayInputStream(("<root xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">" + xsd + "</root>").getBytes("utf-8")));
    }

    /**
     * @return the PORT_NAMESPACE
     */
    public static String getPORT_NAMESPACE() {
        return PORT_NAMESPACE;
    }

    /**
     * @return the ADDRESS_LOCATION
     */
    public static String getADDRESS_LOCATION() {
        return ADDRESS_LOCATION;
    }

    /**
     * @return the PORT_NAME
     */
    public String getPORT_NAME() {
        return PORT_NAME;
    }

    /**
     * @return the BINDER_NAME
     */
    public String getBINDER_NAME() {
        return BINDER_NAME;
    }

    /**
     * @return the SERVICE_NAME
     */
    public String getSERVICE_NAME() {
        return SERVICE_NAME;
    }

}
