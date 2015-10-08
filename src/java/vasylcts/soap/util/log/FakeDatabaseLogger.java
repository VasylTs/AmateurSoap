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
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It`s realization of interface IDatabaseLogger without real logging to DB
 * <p>
 * @author VasylcTS
 */
public class FakeDatabaseLogger implements IDatabaseLogger {

    private static final Logger logger = StandartLogger.getLogger();

    @Override
    public Long logSoapRequest(String aRemoteIP, String aContent, String soapAction) throws SQLException, IOException {
        logger.log(Level.FINE, "Fake db log request.");
        return 1l;
    }

    @Override
    public Long logSoapResponse(String serverIP, String aContent, boolean isError) throws SQLException, IOException {
        logger.log(Level.FINE, "Fake db log response.");
        return 1l;
    }

    @Override
    public Long getLogID() {
        return 1l;
    }

}
