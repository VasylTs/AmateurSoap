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
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author VasylcTS
 */
public interface IDatabaseWorker {

    public List<String> getMethodsFromDBForClient(String clientIP) throws SQLException;

    public Map<String, ActionDescription> getMethodsDescriptionForClientFromDB(String clientIP) throws SQLException;

    public ActionDescription getActionFullDescriptionForClientFromDB(String clientIP, String actionName) throws SQLException;

}
