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
package vasylcts.soap.main;

import vasylcts.soap.util.FakeWorkDatabaseConnection;
import vasylcts.soap.work.SoapService;
import vasylcts.soap.work.specwork.ISpecialWorker;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

/**
 *
 * @author VasylcTS
 */
public class MainSoapServletFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) {        
        if (!FakeWorkDatabaseConnection.isInit()) {
            DataSource ds = (DataSource) filterConfig.getServletContext().getAttribute("DATASOURCE");
            FakeWorkDatabaseConnection.InitConnection(ds);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            SoapService sService = new SoapService((HttpServletRequest) request);
            ISpecialWorker worker = sService.getWorker();
            worker.logSoapServiceRequest();
            request.setAttribute("worker", worker);
            chain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Something happened bad", e);
        }
        //NO doFilter chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
