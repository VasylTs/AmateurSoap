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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

/**
 * 
 * @author VasylcTS
 */
public class SoapServletContextListener  implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            if (false) // you should create your own jndi
            {
                ServletContext servletContext = sce.getServletContext();
                // get JNDI path
                String jndiPath = sce.getServletContext().getInitParameter("DATABASE");
                Context ctx = new InitialContext();
                DataSource ds = (DataSource) ctx.lookup(jndiPath);
                // set our datasource 
                servletContext.setAttribute("DATASOURCE", ds);
            }
        }
        catch(Exception ex) { }    
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
    }
    
}
