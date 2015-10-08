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
package vasylcts.soap.work;

import vasylcts.soap.util.assisttypes.exception.SoapExceptionClient;
import vasylcts.soap.util.assisttypes.exception.SoapExceptionServer;
import vasylcts.soap.util.FakeDatabaseWorker;
import vasylcts.soap.util.IDatabaseWorker;
import vasylcts.soap.util.xslt.MessageCreator;
import vasylcts.soap.util.log.FakeDatabaseLogger;
import vasylcts.soap.util.log.IDatabaseLogger;
import vasylcts.soap.work.specwork.ISpecialWorker;
import vasylcts.soap.work.specwork.RequestToAlienShipWorker;
import vasylcts.soap.work.specwork.WSDLCreatorWorker;
import vasylcts.soap.work.specwork.XSDCreatorWorker;
import javax.servlet.http.HttpServletRequest;

/**
 * It is just test class to work with specific realization of ISpecialWorker
 * @author VasylcTS
 */
final public class SoapService {
    private ISpecialWorker worker;
    private HttpServletRequest request;

    public SoapService(HttpServletRequest req) {
        this.request = req;
    }

    public String doWork() {
        String answer;
        try {
            worker.setRequest(request);
            worker.logSoapServiceRequest();
            worker.validateRequest();
            try {
                answer = worker.getAnswer();
                worker.logSoapServiceResponse(answer, false);
            } catch (SoapExceptionServer ex) {
                answer = MessageCreator.createErrorResponseServer(ex);
                worker.logSoapServiceResponse(answer, false);
            }
        } catch (SoapExceptionClient ex) {
            answer = MessageCreator.createErrorResponseClient(ex);
        } catch (SoapExceptionServer ex) {
            answer = MessageCreator.createErrorResponseServer(ex);
        }
        return answer;
    }

    private ISpecialWorker createSpecialWorker(IDatabaseLogger dbLogger, IDatabaseWorker dbWorker) throws SoapExceptionServer {
        return createSpecialWorker(this.request, dbLogger, dbWorker);
    }

    private ISpecialWorker createSpecialWorker(HttpServletRequest req, IDatabaseLogger dbLogger, IDatabaseWorker dbWorker) throws SoapExceptionServer {

        ISpecialWorker tempWorker;

        if (req.getParameter("wsdl") != null) {
            tempWorker = new WSDLCreatorWorker(dbLogger, dbWorker);
        } else if (req.getParameter("xsd") != null) {
            tempWorker = new XSDCreatorWorker(dbLogger, dbWorker);
        } else {
            tempWorker = new RequestToAlienShipWorker(dbLogger, dbWorker);
            //tempWorker = new RequestToAnotherServerWorker(dbLogger, dbWorker, new URL("aaa"));
        }

        tempWorker.setRequest(req);
        return tempWorker;
    }

    /**
     * @return the worker using standard IDatabaseLogger and IDatabaseWorker
     * <p>
     * @see FakeDatabaseLogger
     * @see FakeDatabaseWorker
     */
    public ISpecialWorker getWorker() throws SoapExceptionServer {
        IDatabaseLogger dbLogger = new FakeDatabaseLogger();
        IDatabaseWorker dbWorker = new FakeDatabaseWorker();
        worker = createSpecialWorker(dbLogger, dbWorker);
        return worker;
    }

    /**
     * @param dbLogger specific DB logger
     * @param dbWorker specific DB worker
     * <p>
     * @return the worker
     */
    public ISpecialWorker getWorker(IDatabaseLogger dbLogger, IDatabaseWorker dbWorker) throws SoapExceptionServer {
        worker = createSpecialWorker(dbLogger, dbWorker);
        return worker;
    }
}
