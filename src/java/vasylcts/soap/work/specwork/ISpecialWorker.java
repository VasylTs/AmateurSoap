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

import vasylcts.soap.util.assisttypes.exception.SoapExceptionClient;
import vasylcts.soap.util.assisttypes.exception.SoapExceptionServer;
import javax.servlet.http.HttpServletRequest;

/**
 * Every class that will process soapService request should implement this
 * interface For example: wsdl/xsd calls, soap action calls
 * <p>
 * @author VasylcTS
 */
public interface ISpecialWorker {

    /**
     * Logging received request to this worker with specific IDatabaseLogger
     * <p>
     * @return Should return log row ID in DB
     * <p>
     * @see IDatabaseLogger
     */
    public Long logSoapServiceRequest();

    /**
     * Set HttpServletRequest with should be processing in this ISpecialWorker
     * <p>
     * @param request HttpServletRequest that should be processed
     * <p>
     * @throws SoapExceptionServer if can not parse request
     * @see HttpServletRequest
     */
    public void setRequest(HttpServletRequest request) throws SoapExceptionServer;

    /**
     * Checks if request has right parameters for this ISpecialWorker
     * <p>
     * @return Should return true if everything ok
     * <p>
     * @throws SoapExceptionServer If error happened while parsing request
     * @throws SoapExceptionClient If request does not meets specified
     * parameters
     */
    public boolean validateRequest() throws SoapExceptionServer, SoapExceptionClient;

    /**
     * Returning work result of this ISpecialWorker
     * <p>
     * @return Work result of this ISpecialWorker
     * <p>
     * @throws SoapExceptionServer If error happened because of some server
     * error
     * @throws SoapExceptionClient If error happened because of request does
     * not meets specified parameters
     */
    public String getAnswer() throws SoapExceptionServer, SoapExceptionClient;

    /**
     * Logging work result(or error) of this ISpecialWorker with specific
     * IDatabaseLogger
     * <p>
     * @param content What need to log
     * @param isError Is error happened
     * <p>
     * @return Should return log row ID in DB
     * <p>
     * @see IDatabaseLogger
     */
    public Long logSoapServiceResponse(String content, boolean isError);
}
