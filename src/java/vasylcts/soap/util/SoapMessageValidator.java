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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import javax.xml.XMLConstants;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;
import vasylcts.soap.util.assisttypes.exception.SoapExceptionClient;
import vasylcts.soap.util.assisttypes.exception.SoapExceptionServer;

/**
 *
 * @author VasylcTS
 */
public class SoapMessageValidator {

    public void validate(String request, String xsd) throws SoapExceptionServer, SoapExceptionClient {
        try {
            SOAPBody soapBody = getSOAPBody(request);
            Source xmlSource = createSource(soapBody);
            Validator validator = createValidator(xsd);

            validator.validate(xmlSource);
        } catch (SAXException saxParseEx) {
            throw new SoapExceptionClient(saxParseEx.getLocalizedMessage(), saxParseEx);
        } catch (SoapExceptionClient clientEx) {
            throw clientEx;
        } catch (IOException | TransformerException ex) {
            throw new SoapExceptionServer("Error while validating soap-message.", ex.getMessage(), ex);
        }
    }

    private SOAPBody getSOAPBody(String request) throws UnsupportedEncodingException, SoapExceptionClient, IOException {
        SOAPMessage soapMessage = null;
        SOAPBody soapBody = null;
        try {
            MessageFactory factory = MessageFactory.newInstance();
            soapMessage = factory.createMessage(new MimeHeaders(), new ByteArrayInputStream(request.getBytes("UTF-8")));
            soapBody = soapMessage.getSOAPBody();
        } catch (SOAPException e) {
            throw new SoapExceptionClient(e.getLocalizedMessage() + " Cause: " + e.getCause().toString(), e);
        }
        return soapBody;
    }

    private Source createSource(SOAPBody soapBody) throws TransformerConfigurationException, TransformerException {
        DOMSource source = new DOMSource(soapBody.getFirstChild());
        StringWriter stringResult = new StringWriter();
        TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
        Source xmlSource = new StreamSource(new StringReader(stringResult.toString()));

        return xmlSource;
    }

    private Validator createValidator(String actionXSD) throws SAXException {
        String readyXSD = createXsdForSpecificAction(actionXSD);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(new StreamSource(new StringReader(readyXSD)));

        return schema.newValidator();
    }

    /**
     * Adding schema, specific types to ActionXSD
     * <p>
     * @param ActionXSD xsd for action with only description of element and
     * complexType
     * <p>
     * @return ready for validating xsd
     */
    private String createXsdForSpecificAction(String ActionXSD) {
        StringBuilder sb = new StringBuilder();
        XSDBuilder xsdCreator = XSDBuilder.getInstance();
        xsdCreator.appendHeader(sb);
        xsdCreator.appendSysinfo(sb);
        sb.append(ActionXSD);
        xsdCreator.appendEnd(sb);

        return sb.toString();
    }
}