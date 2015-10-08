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
package vasylcts.soap.util.log;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import oracle.jdbc.OracleCallableStatement;
import oracle.sql.CLOB;

/**
 * It`s a test class. My descriptions of actions were in Oracle database. I
 * created some packages on pl/sql for getting all i needed. You should create
 * your own realization of IDatabaseWorker
 * <p>
 * @author VasylcTS
 */
public abstract class SimpleSoapServiceDatabaseLogger implements IDatabaseLogger {

    private Long logID;

    @Override
    public Long logSoapRequest(String aRemoteIP, String aContent, String soapAction)
            throws SQLException, IOException {
        //Database Connection object
        Connection conn = null;
        //OracleCallableStatement
        OracleCallableStatement oraProc = null;
        //CLOB
        CLOB tempClob = null;
        String sqlProc = "begin "
                + " :result := some_pck.logsoaprequest(remote_ip => :remote_ip, "
                + " req_content => :req_content,"
                + " soap_action_name => :soap_action_name); "
                + "end;";
        try {
            conn = getWorkConnection();
            CallableStatement cs = conn.prepareCall(sqlProc);
            oraProc = cs.unwrap(oracle.jdbc.OracleCallableStatement.class);
            oraProc.registerOutParameter("p_result", Types.BIGINT);
            oraProc.setString("remote_ip", aRemoteIP);
            if (aContent != null) {
                // create a new temporary CLOB
                tempClob = CLOB.createTemporary(conn, true, CLOB.DURATION_SESSION);
                // Open the temporary CLOB in readwrite mode to enable writing
                tempClob.open(CLOB.MODE_READWRITE);
                // Get the output stream to write
                Writer tempClobWriter = tempClob.getCharacterOutputStream();
                // Write the data into the temporary CLOB
                tempClobWriter.write(aContent);
                // Flush and close the stream
                tempClobWriter.flush(); //видимо flush делать при write(String)
                tempClobWriter.close();
                // Close the temporary CLOB
                tempClob.close();
                oraProc.setCLOB("req_content", tempClob);
            } else {
                oraProc.setNull("req_content", oracle.jdbc.OracleTypes.CLOB);
            }
            oraProc.setString("soap_action_name", soapAction);
            oraProc.execute();
            //Получить результат
            logID = oraProc.getLong("p_result");
            return getLogID();
        } finally {
            try {
                // Close Statement
                if (oraProc != null) {
                    oraProc.close();
                    oraProc = null;
                }
                // Free CLOB
                if (tempClob != null) {
                    tempClob.freeTemporary();
                    tempClob = null;
                }
            } catch (Exception e) {
            }
            if (conn != null) {
                if (!conn.isClosed()) {
                    conn.close();
                }
                conn = null;
            }
        }
    }

    @Override
    public Long logSoapResponse(String serverIP, String aContent, boolean isError)
            throws SQLException, IOException {
        //Database Connection object
        Connection conn = null;
        //OracleCallableStatement
        OracleCallableStatement oraProc = null;
        //CLOB
        CLOB tempClob = null;
        String sqlProc = "begin "
                + "  :result := some_pck.logsoapresponse(requestId => :request_id, "
                + " resp_content => :resp_content,"
                + " server_ip => :server_ip,"
                + " state => :state); "
                + "end;";
        try {
            conn = getWorkConnection();
            CallableStatement cs = conn.prepareCall(sqlProc);
            oraProc = cs.unwrap(oracle.jdbc.OracleCallableStatement.class);
            //Установка
            oraProc.registerOutParameter("p_result", Types.BIGINT);
            //Установка значения параметра по его имени
            if (getLogID() != null) {
                oraProc.setBigDecimal("request_id", new BigDecimal(getLogID()));
            } else {
                oraProc.setNull("request_id", Types.DECIMAL);
            }
            if (aContent != null) {
                // create a new temporary CLOB
                tempClob = CLOB.createTemporary(conn, true, CLOB.DURATION_SESSION);
                // Open the temporary CLOB in readwrite mode to enable writing
                tempClob.open(CLOB.MODE_READWRITE);
                // Get the output stream to write
                Writer tempClobWriter = tempClob.getCharacterOutputStream();
                // Write the data into the temporary CLOB
                tempClobWriter.write(aContent);
                // Flush and close the stream
                tempClobWriter.flush(); //видимо flush делать при write(String)
                tempClobWriter.close();
                // Close the temporary CLOB
                tempClob.close();
                oraProc.setCLOB("resp_content", tempClob);
            } else {
                oraProc.setNull("resp_content", oracle.jdbc.OracleTypes.CLOB);
            }
            oraProc.setString("server_ip", serverIP);
            oraProc.setString("state", isError ? "ERROR" : "OK");
            oraProc.execute();
            //Получить результат
            Long lRes = oraProc.getLong("p_result");
            return lRes;
        } finally {
            try {
                // Close Statement
                if (oraProc != null) {
                    oraProc.close();
                    oraProc = null;
                }
                // Free CLOB
                if (tempClob != null) {
                    tempClob.freeTemporary();
                    tempClob = null;
                }
            } catch (Exception e) {
            }
            if (conn != null) {
                if (!conn.isClosed()) {
                    conn.close();
                }
                conn = null;
            }
        }
    }

    /**
     * @return the logID
     */
    @Override
    public Long getLogID() {
        return logID;
    }

    private Connection getWorkConnection() {
        throw new UnsupportedOperationException("Sorry guys, you should do it by your own");
    }
}
