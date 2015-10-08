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

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * It`s fake class for getting DB connection from server`s DataSourse You should
 * create realization for getting DB connection
 * <p>
 * @author VasylcTS
 */
public class FakeWorkDatabaseConnection {

    //private static DataSource dataSource = null;
    private static boolean isInitialized = false;

    public static boolean isInit() {
        return isInitialized;
    }

    public static void InitConnection(DataSource someDatasource) {
        isInitialized = true; //it`s a fake!!!

//        synchronized(FakeWorkDatabaseConnection.class) 
//        {
//           dataSource = someDatasource;
//           isInitialized = (dataSource != null);
//        }
    }

    public static Connection getWorkConnection() throws SQLException {
        return null;
    }
}
