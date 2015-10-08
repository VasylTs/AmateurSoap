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

import vasylcts.soap.util.assisttypes.ActionDescription;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import oracle.jdbc.OracleTypes;
import vasylcts.soap.util.assisttypes.EnumAnswerType;

/**
 * It`s a test class. My descriptions of actions were in Oracle database. I
 * created some packages on pl/sql for getting all i needed. You should create
 * your own realization of IDatabaseWorker
 * <p>
 * @author VasylcTS
 */
public abstract class DatabaseWorker implements IDatabaseWorker {

    private static DatabaseWorker dbWorker;

    public static DatabaseWorker getInstance() {
        if (dbWorker == null) {
            //dbWorker = new DatabaseWorker();
        }
        return dbWorker;
    }

    private DatabaseWorker() {
    }

    @Override
    public List<String> getMethodsFromDBForClient(String clientIP) throws SQLException {
        Connection con = null;
        CallableStatement st = null;
        ResultSet rs = null;

        try {
            String query = "BEGIN P_SOMEPCK_SOAP.getGrantedMethods(?,?); END;";
            con = getWorkConnection();
            st = con.prepareCall(query);

            st.setString(1, clientIP);
            st.registerOutParameter(2, OracleTypes.CURSOR); //REF CURSOR
            st.execute();
            rs = (ResultSet) st.getObject(2);

            List<String> actionList = new LinkedList<>();
            while (rs.next()) {
                String actionName = rs.getString(1);
                if (actionName != null) {
                    actionList.add(actionName);
                }

            }
            return actionList;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }
        }

    }

    @Override
    public Map<String, ActionDescription> getMethodsDescriptionForClientFromDB(String clientIP) throws SQLException {
        Connection con = null;
        CallableStatement st = null;
        ResultSet rs = null;

        try {
            String query = "BEGIN P_SOMEPCK_SOAP.getParamsForMethods(?,?); END;";
            con = getWorkConnection();
            st = con.prepareCall(query);

            st.setString(1, clientIP);
            st.registerOutParameter(2, OracleTypes.CURSOR); //REF CURSOR
            st.execute();
            rs = (ResultSet) st.getObject(2);

            Map<String, ActionDescription> paramMap = new HashMap<>();
            while (rs.next()) {
                ActionDescription aDesc = paramMap.getOrDefault(rs.getString(1), new ActionDescription());
                aDesc.setAction(rs.getString(1));

                if (rs.getString(2).equalsIgnoreCase("IN")) {
                    aDesc.setInElementName(rs.getString(3));
                    aDesc.setInXSD(rs.getString(4));
                } else if (rs.getString(2).equalsIgnoreCase("OUT")) {
                    aDesc.setOutElementName(rs.getString(3));
                    aDesc.setOutXSD(rs.getString(4));
                }
                paramMap.put(rs.getString(1), aDesc);
            }

            return paramMap;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }
        }

    }

    @Override
    public ActionDescription getActionFullDescriptionForClientFromDB(String clientIP, String actionName) throws SQLException {
        Connection con = null;
        CallableStatement st = null;
        ResultSet rs = null;

        try {
            String query = "BEGIN P_SOMEPCK_SOAP.getMethodDescription(?,?,?); END;";
            con = getWorkConnection();
            st = con.prepareCall(query);

            st.setString(1, actionName);
            st.setString(2, clientIP);
            st.registerOutParameter(3, OracleTypes.CURSOR); //REF CURSOR
            st.execute();
            rs = (ResultSet) st.getObject(3);

            ActionDescription descr = new ActionDescription();
            while (rs.next()) {
                if (rs.getString(3).equalsIgnoreCase("IN")) {
                    descr.setAction(rs.getString(1));
                    descr.setSoapAnswerType(EnumAnswerType.getAnswerTypeByWrapperName(rs.getString(2)));
                    descr.setInXSD(rs.getString(4));
                    descr.setInElementName(rs.getString(5));
                    descr.setInXSLT(rs.getString(6));
                } else if (rs.getString(3).equalsIgnoreCase("OUT")) {
                    descr.setOutXSD(rs.getString(4));
                    descr.setOutElementName(rs.getString(5));
                    descr.setOutXSLT(rs.getString(6));
                }
            }

            return descr;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }
        }

    }
    
    private Connection getWorkConnection() {
        throw new UnsupportedOperationException("Sorry guys, you should do it by your own");
    }
}
