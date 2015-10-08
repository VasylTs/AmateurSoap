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
package vasylcts.soap.work.specwork;

import vasylcts.soap.main.MainSoapServletRequestWrapper;
import vasylcts.soap.util.assisttypes.ActionDescription;
import vasylcts.soap.util.assisttypes.exception.SoapExceptionClient;
import vasylcts.soap.util.assisttypes.exception.SoapExceptionServer;
import vasylcts.soap.util.FakeDatabaseWorker;
import vasylcts.soap.util.xslt.MessageCreator;
import vasylcts.soap.util.IDatabaseWorker;
import vasylcts.soap.util.log.FakeDatabaseLogger;
import vasylcts.soap.util.log.IDatabaseLogger;
import vasylcts.soap.util.log.StandartLogger;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import vasylcts.soap.util.SoapMessageValidator;

/**
 * This realization of ISpecialWorker made just for testing AmateurSoap app Just
 * for fun, nothing more.
 * <p>
 * @author VasylcTS
 */
public class RequestToAlienShipWorker implements ISpecialWorker {

    private static final Logger logger = StandartLogger.getLogger();

    private final IDatabaseWorker dbWorker;
    private final IDatabaseLogger dbLogger;

    private String soapAction;
    private String soapRequest;
    private ActionDescription actionDescription;
    private MainSoapServletRequestWrapper request;

    /**
     * Creates new instance of {@code RequestToAlienShipWorker} with default
     * {@code IDatabaseWorker} and {@code IDatabaseLogger}
     * <p>
     * @see FakeDatabaseLogger
     * @see FakeDatabaseWorker
     */
    public RequestToAlienShipWorker() {
        this.dbLogger = new FakeDatabaseLogger();
        this.dbWorker = new FakeDatabaseWorker();
    }

    /**
     * Creates new instance of {@code RequestToAlienShipWorker} with specified
     * {@code IDatabaseLogger} and {@code IDatabaseWorker}
     * <p>
     * @param dbLogger Logger that will log request and response to database
     * @param dbWorker Worker that will return {@code ActionDescription} for
     * called soap-action
     */
    public RequestToAlienShipWorker(IDatabaseLogger dbLogger, IDatabaseWorker dbWorker) {
        this.dbLogger = dbLogger;
        this.dbWorker = dbWorker;
    }

    @Override
    public Long logSoapServiceRequest() {
        Long logID = null;
        try {
            dbLogger.logSoapRequest(request.getRemoteAddr(), request.getRequestString(), request.getHeader("SOAPAction"));
            logID = dbLogger.getLogID();
        } catch (SQLException | IOException ex) {
            logger.log(Level.SEVERE, "Error while trying to log request. Cause: {0}", MessageCreator.getStackTrace(ex));
        }
        return logID;
    }

    @Override
    public Long logSoapServiceResponse(String content, boolean isError) throws IllegalStateException {
        Long logID = null;
        try {
            logID = dbLogger.logSoapResponse(request.getLocalAddr(), content, isError);
        } catch (SQLException | IOException ex) {
            logger.log(Level.SEVERE, "Error while trying to log response. Cause: {0}", MessageCreator.getStackTrace(ex));
        }
        return logID;
    }

    @Override
    public void setRequest(HttpServletRequest request) throws SoapExceptionServer {
        try {
            this.request = new MainSoapServletRequestWrapper(request);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error while parsing request. Cause: {0}", MessageCreator.getStackTrace(ex));
            throw new SoapExceptionServer(MessageCreator.UNEXPECTED_ERROR, "Error while parsing request.", ex);
        }
    }

    @Override
    public boolean validateRequest() throws SoapExceptionServer, SoapExceptionClient {
        try {
            logger.log(Level.FINE, "Start validating request.");

            ActionDescription methodDescr = getMethodDescription(getSoapAction());

            SoapMessageValidator validator = new SoapMessageValidator();
            validator.validate(getSoapRequest(), methodDescr.getInXSD());
            logger.log(Level.FINE, "Request successfully validated!");
            return true;
        } catch (SoapExceptionServer | SoapExceptionClient mEx) {
            throw mEx;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error while validating request. Cause: {0}", MessageCreator.getStackTrace(ex));
            throw new SoapExceptionClient(MessageCreator.UNEXPECTED_ERROR, "Error while validating request.", ex);
        }
    }

    @Override
    public String getAnswer() throws SoapExceptionServer, SoapExceptionClient {
        logger.log(Level.FINE, "Start processing request.");
        try {
            logger.log(Level.FINE, "Transorming request...");
            String req = MessageCreator.createRequsetToAS(getSoapAction(), getSoapRequest(), getMethodDescription(soapAction).getInXSLT());
            logger.log(Level.FINER, "Created request:\n{0}", req);
            logger.log(Level.FINE, "Making request to alien ship");
            String responseFromAS = doRequestToAlienShip(req);
            logger.log(Level.FINER, "Received answer from alien ship:\n {0}", responseFromAS);

            logger.log(Level.FINE, "Transforming response...");
            ActionDescription actDesc = getMethodDescription(soapAction);
            String response = MessageCreator.createResponse(responseFromAS, actDesc.getOutElementName(), actDesc.getSoapAnswerType(), actDesc.getOutXSLT());
            logger.log(Level.FINER, "Created soapResponse:\n {0}", response);
            logger.log(Level.FINE, "Returning response...");
            return response;
        } catch (SoapExceptionClient | SoapExceptionServer mEx) {
            throw mEx;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error while making request to Alien ship. Cause: {0}", MessageCreator.getStackTrace(ex));
            throw new SoapExceptionServer(MessageCreator.UNEXPECTED_ERROR, "Error while making request to Alien ship.", ex);
        }
    }

    private String doRequestToAlienShip(String request) throws IOException, SoapExceptionClient {
        StringBuilder sb = new StringBuilder("<root>");
        if ("sayHelloToAliens".equals(getSoapAction())) {
            sb.append("<additionalInfo>")
                    .append("<serverID>1253</serverID>")
                    .append("<serverCode>Everything fine</serverCode>")
                    .append("</additionalInfo>")
                    .append("<error>")
                    .append("<code>0</code>")
                    .append("<message>ok</message>")
                    .append("</error>")
                    .append("<alienName>Alyie</alienName>")
                    .append("<responseMessage>Do not send requests to us anymore!</responseMessage>");
        } else {
            sb.append("<additionalInfo>")
                    .append("<serverID>678678</serverID>")
                    .append("<serverCode>You broke our service, shame on you!</serverCode>")
                    .append("</additionalInfo>");
            sb.append("<ololo>");
            Random rand = new Random();
            int size = rand.nextInt(50) + 20;
            byte[] someData = new byte[size];
            for (int i = 0; i < someData.length; i++) {
                someData[i] = (byte) (rand.nextInt(122 - 65) + 65);
            }
            sb.append(new String(someData, "utf-8"));
            sb.append("</ololo>");
        }
        sb.append("</root>");
        return sb.toString();
    }

    private String getSoapAction() throws SoapExceptionClient {
        if (soapAction == null) {
            soapAction = request.getHeader("SOAPAction");
            logger.log(Level.FINE, "Received SOAPAction: {0}", soapAction);
            // No header - return error
            if (soapAction == null) {
                throw new SoapExceptionClient("No SOAPAction in request header.");
            }
            if (soapAction.startsWith("\"")) {
                // delete symbol " at begining and ending 
                soapAction = soapAction.replaceAll("^\"|\"$", "");
                logger.log(Level.FINE, "Replaced by SOAPAction: {0}", soapAction);
            }
        }
        return soapAction;
    }

    private String getSoapRequest() throws SoapExceptionClient {
        if (soapRequest == null) {
            soapRequest = request.getRequestString();
            logger.log(Level.FINE, "Received request body: {0}", soapRequest);
            if (soapRequest == null || soapRequest.trim().length() == 0) {
                throw new SoapExceptionClient("Error with parsing xml: request xml not found or empty!");
            }
        }
        return soapRequest;
    }

    private ActionDescription getMethodDescription(String actionName) throws SoapExceptionClient, SQLException {
        if (actionDescription == null) {
            logger.log(Level.FINE, "Trying to get method info from DB.");
            actionDescription = dbWorker.getActionFullDescriptionForClientFromDB(request.getRemoteAddr(), actionName);
            if (actionDescription == null || actionDescription.getAction() == null) {
                throw new SoapExceptionClient("Error with SOAPAction: " + actionName + ". Not valid SOAPAction or you do not have rights to call this action.");
            }
            logger.log(Level.FINE, "Received method info from DB. In elem = {0} / out elem = {1}", new Object[]{actionDescription.getInElementName(), actionDescription.getOutElementName()});

        }
        return actionDescription;
    }
}
