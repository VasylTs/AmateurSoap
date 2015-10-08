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
package vasylcts.soap.util.xslt;

import vasylcts.soap.util.assisttypes.EnumAnswerType;
import vasylcts.soap.util.assisttypes.exception.SoapExceptionClient;
import vasylcts.soap.util.assisttypes.exception.SoapExceptionServer;
import vasylcts.soap.util.log.StandartLogger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;
import vasylcts.soap.util.WSDLBuilder;

/**
 * Class for transformations some xml-type requests and responses with help of
 * XSLT or for generation errors
 * <p>
 * @author VasylcTS
 */
public class MessageCreator {

    public static final String UNEXPECTED_ERROR = "Unexpected error.";

    /**
     * Transforms given soapRequest to some other XML with help of XSL
     * transformation
     * <p>
     * @param action Soap-action name. It used by default XSLT. It set like
     * "actionName" parameter
     * @param soapRequest Request that need to transform
     * @param custom_xslt Your custom XSLT with help of wich soapRequest will be
     * transformed. It can be null(will use standard XSLT)
     * <p>
     * @return Transformed XML
     * <p>
     * @throws FileNotFoundException If did not find default XSLT
     * @throws TransformerException
     */
    public static String createRequsetToAS(String action, String soapRequest, String custom_xslt) throws FileNotFoundException, TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt;
        if (custom_xslt == null || custom_xslt.equals("")) {
            String resource = "/vasylcts/soap/util/xslt/FromSoapToSomething.xsl";
            InputStream inputStream = MessageCreator.class.getClassLoader().getResourceAsStream(resource);
            if (inputStream == null) {
                throw new FileNotFoundException("Can`t find file:" + resource);
            }
            xslt = new StreamSource(inputStream, "UTF-8");
        } else {
            xslt = new StreamSource(new StringReader(custom_xslt));
        }
        Transformer transformer = factory.newTransformer(xslt);
        transformer.setParameter("actionName", action);
        Source text = new StreamSource(new StringReader(soapRequest));
        StringWriter writer = new StringWriter();
        transformer.transform(text, new StreamResult(writer));
        String readyRequest = writer.toString();
        //String readyRequest = StringEscapeUtils.unescapeHtml4(writer.toString());
        return readyRequest;
    }

    /**
     * Transforms given XML to some soapResponse with help of XSL transformation
     * <p>
     * @param answer some XML wich will be transformed
     * @param element_name Wrapper tag name (it`s often builds like: soapAction
     * + "Response"). It goes after "Body" tag in soap envelope
     * @param wrapperType If you have no idea what will be in parameter "answer"
     * you can choose specific wrapper type.
     * @param custom_xslt Your custom XSLT with help of wich soapRequest will be
     * transformed. It can be null(will use standard XSLT)
     * <p>
     * @return Transformed XML
     * <p>
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws TransformerException
     * @throws SoapExceptionServer
     * @see vasylcTS.soap.util.assisttypes.AnswerType
     */
    public static String createResponse(String answer, String element_name, EnumAnswerType wrapperType, String custom_xslt) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException, SoapExceptionServer {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt;
        if (custom_xslt == null || custom_xslt.equals("")) {
            String resource = "/vasylcts/soap/util/xslt/FromSomethingToSoap.xsl";
            InputStream inputStream = MessageCreator.class.getClassLoader().getResourceAsStream(resource);
            if (inputStream == null) {
                throw new FileNotFoundException("Can`t find file: " + resource);
            }
            xslt = new StreamSource(inputStream, "UTF-8");
        } else {
            xslt = new StreamSource(new StringReader(custom_xslt));
        }
        Transformer transformer = factory.newTransformer(xslt);
        transformer.setParameter("namespace", WSDLBuilder.getPORT_NAMESPACE());
        transformer.setParameter("responseElementName", element_name);
        if (wrapperType == null) {
            wrapperType = EnumAnswerType.NORMAL;
        }
        String responseWrapperTag = wrapperType.getWrapperType() == null ? "" : wrapperType.getWrapperType();
        String responseWrapperChildTag = wrapperType.getChildWrapperType() == null ? "" : wrapperType.getChildWrapperType();
        transformer.setParameter("undefinedResponseTagParent", responseWrapperTag);
        transformer.setParameter("undefinedResponseTagChild", responseWrapperChildTag);

        Source text = new StreamSource(new StringReader(answer));
        StringWriter writer = new StringWriter();
        transformer.transform(text, new StreamResult(writer));
        String readyResponse = writer.toString();
        //String readyResponse = StringEscapeUtils.unescapeHtml4(writer.toString());
        return readyResponse;
    }

    /**
     * Create soap-fault message with help of XSLT
     * <p>
     * @param isServerError Is server or client error
     * @param faultString Short description of error. It can not be null.
     * @param detail Detailed description of error. It can not be null(but can
     * be empty String).
     * @param errorXML Detailed description of error in XML. It can not be
     * null(but can be empty String).
     * <p>
     * @return Soap fault message
     * <p>
     * @throws java.lang.NullPointerException
     */
    public static String createSoapFault(boolean isServerError, String faultString, String detail, String errorXML) {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Source xslt;
            String resource = "/vasylcts/soap/util/xslt/SoapFault.xsl";
            InputStream inputStream = MessageCreator.class.getClassLoader().getResourceAsStream(resource);
            if (inputStream == null) {
                throw new FileNotFoundException("Can`t find file:" + resource);
            }
            xslt = new StreamSource(inputStream, "UTF-8");

            Transformer transformer = factory.newTransformer(xslt);
            transformer.setParameter("faultStringParam", faultString);
            transformer.setParameter("detailParam", detail);
            transformer.setParameter("isServerFault", isServerError);
            String tempErrorXml = "<rootTempElement>" + errorXML + "</rootTempElement>";
            Source text = new StreamSource(new StringReader(tempErrorXml));
            StringWriter writer = new StringWriter();
            transformer.transform(text, new StreamResult(writer));
            return writer.toString();
        } catch (Exception ex) {
            StandartLogger.log(Level.SEVERE, MessageCreator.getStackTrace(ex));
            return "<ERROR>Unexpected error</ERROR>";
        }
    }

    /**
     * Create soap fault message
     * <p>
     * @param faultString Short description of error
     * <p>
     * @return Soap-fault message like:
     * <p>
     */
    public static String createErrorResponseClient(String faultString) {
        return createSoapFault(false, faultString, "", "");
    }

    /**
     * Create soap fault message
     * <p>
     * @param client_ex .
     * <p>
     * @return Soap-fault message
     * <p>
     * @see SoapExceptionClient
     */
    public static String createErrorResponseClient(SoapExceptionClient client_ex) {
        String detail = client_ex.getDetail();
        if (detail == null) {
            detail = "";
        }
        return createSoapFault(false, client_ex.getLocalizedMessage(), detail, "");
    }

    /**
     * Create soap fault message
     * <p>
     * @param faultString Short description of error
     * @param detail Detailed description of error(can be null)
     * <p>
     * @return Soap-fault message
     */
    public static String createErrorResponseServer(String faultString, String detail) {
        return createSoapFault(true, faultString, detail, "");
    }

    /**
     * Create soap fault message with StackTrace of Throwable in detail block
     * <p>
     * @param faultString Short description of error
     * @param th StackTrace of this Throwable will be inserted in soap fault
     * message
     * <p>
     * @return Soap-fault message
     */
    public static String createErrorResponseServer(String faultString, Throwable th) {
        return createSoapFault(true, faultString, MessageCreator.getStackTrace(th), "");
    }

    /**
     * Create soap fault message.
     * <p>
     * @param server_ex if has no details it will insert StackTrace in soap
     * fault message in details
     * <p>
     * @return Soap-fault message
     * <p>
     * @see SoapExceptionServer
     */
    public static String createErrorResponseServer(SoapExceptionServer server_ex) {
        String detail = server_ex.getDetail();
        if (detail == null) {
            detail = MessageCreator.getStackTrace(server_ex);
        }
        return createSoapFault(true, server_ex.getLocalizedMessage(), detail, "");
    }

    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }
}
