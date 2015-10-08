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

import vasylcts.soap.util.assisttypes.ActionDescription;
import vasylcts.soap.util.assisttypes.exception.SoapExceptionClient;
import vasylcts.soap.util.assisttypes.exception.SoapExceptionServer;
import vasylcts.soap.util.FakeDatabaseWorker;
import vasylcts.soap.util.xslt.MessageCreator;
import vasylcts.soap.util.IDatabaseWorker;
import vasylcts.soap.util.WSDLBuilder;
import vasylcts.soap.util.log.FakeDatabaseLogger;
import vasylcts.soap.util.log.IDatabaseLogger;
import vasylcts.soap.util.log.StandartLogger;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

/**
 * Class {@code WSDLCreatorWorker} is realization of ISpecialWorker and it is
 * designed for processing requests that asks WSDL. It log req/resp and returning
 * WSDL generated by {@code WSDLBuilder}
 * <p>
 * @author VasylcTS
 * @see vasylcts.soap.util.WSDLBuilder
 */
public class WSDLCreatorWorker implements ISpecialWorker {

    private static final Logger logger = StandartLogger.getLogger();
    private final IDatabaseLogger dbLogger;
    private final IDatabaseWorker dbWorker;

    private HttpServletRequest request;
    private List methodList = null;
    private Map methodDescription = null;

    private String workResult;

    public WSDLCreatorWorker() {
        this.dbWorker = new FakeDatabaseWorker();
        this.dbLogger = new FakeDatabaseLogger();
    }

    public WSDLCreatorWorker(IDatabaseLogger dbLogger, IDatabaseWorker dbWorker) {
        this.dbWorker = dbWorker;
        this.dbLogger = dbLogger;
    }

    @Override
    public Long logSoapServiceRequest() {
        Long logID = null;
        try {
            dbLogger.logSoapRequest(request.getRemoteAddr(), "", "GET_WSDL");
            logID = dbLogger.getLogID();
        } catch (SQLException | IOException ex) {
            logger.log(Level.SEVERE, "Error while trying to log request. Cause: {0}", MessageCreator.getStackTrace(ex));
        }
        return logID;
    }

    @Override
    public Long logSoapServiceResponse(String content, boolean isError) {
        Long logID = null;
        try {
            String logContent = "WSDL successfully genereted";
            if (isError) {
                logContent = content;
            }
            logID = dbLogger.logSoapResponse(request.getLocalAddr(), logContent, isError);
        } catch (SQLException | IOException ex) {
            logger.log(Level.SEVERE, "Error while trying to log response. Cause: {0}", MessageCreator.getStackTrace(ex));
        }
        return logID;
    }

    @Override
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public boolean validateRequest() throws SoapExceptionServer, SoapExceptionClient {
        if (request.getParameter("wsdl") != null) {
            try {
                logger.log(Level.FINE, "Received header \"wsdl\".");
                methodDescription = getMethodDescription();
                methodList = getMethodList();
                return true;
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error while getting params from DB. Cause {0}", MessageCreator.getStackTrace(ex));
                throw new SoapExceptionServer(MessageCreator.UNEXPECTED_ERROR, "Error while getting params from DB.", ex);
            }
        } else {
            return false;
        }
    }

    @Override
    public String getAnswer() throws SoapExceptionServer {
        try {
            URL alienShip = new URL("http://" + this.request.getLocalAddr() + ":" + request.getLocalPort() + request.getContextPath() + request.getServletPath());
            WSDLBuilder wsdlBuilder = WSDLBuilder.getInstance(alienShip);
            workResult = wsdlBuilder.makeWSDLFromInOutMaps(methodDescription, methodList);
            return workResult;
        } catch (MalformedURLException ex) {
            logger.log(Level.SEVERE, "Bad url", ex);
            throw new SoapExceptionServer(MessageCreator.UNEXPECTED_ERROR, "Bad url", ex);
        }
    }

    /**
     * @return the methodList
     * <p>
     * @throws java.sql.SQLException
     */
    private List getMethodList() throws SQLException {
        if (methodList == null) {
            logger.log(Level.FINE, "Trying to get granted methods for ip {0} from database...", request.getRemoteAddr());
            methodList = dbWorker.getMethodsFromDBForClient(request.getRemoteAddr());
            logger.log(Level.FINE, "Received list from database with size = {0}", methodList.size());
            if (logger.getLevel().intValue() < Level.FINER.intValue()) {
                logger.log(Level.FINEST, "Received list from database:");
                for (Object methodName : methodList) {
                    logger.log(Level.FINEST, methodName.toString());
                }
            }
        }
        return methodList;
    }

    /**
     * @return the methodDescription
     * <p>
     * @throws java.sql.SQLException
     */
    private Map getMethodDescription() throws SQLException {
        if (methodDescription == null) {
            logger.log(Level.FINE, "Trying to get granted methods` descriptions for ip {0} from database...", request.getRemoteAddr());
            methodDescription = dbWorker.getMethodsDescriptionForClientFromDB(request.getRemoteAddr());
            logger.log(Level.FINE, "Received methods` descriptions from database with size = {0}", methodDescription.size());
            if (logger.getLevel().intValue() < Level.FINER.intValue()) {
                logger.log(Level.FINEST, "Received methods` descriptions from database:");
                for (Object methodDescrObj : methodDescription.values()) {
                    ActionDescription methodDescr = (ActionDescription) methodDescrObj;

                    logger.log(Level.FINEST, methodDescr.toString());
                }
            }
        }
        return methodDescription;
    }
}