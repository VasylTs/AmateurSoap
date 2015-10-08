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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author VasylcTS
 */
public class StandartLogger {

    private final static Logger logger = Logger.getLogger(StandartLogger.class.getName());

    static {
        logger.setLevel(Level.FINER);
        logger.log(Level.INFO, "This is logger {0} with log level {1}, disable it or change log level if you want. ", new Object[]{StandartLogger.class.getName(), logger.getLevel().toString()});
    }

    private StandartLogger() { }

    public static Logger getLogger() {
        if (logger == null) {
            new StandartLogger();
        }
        return logger;
    }

    public static void log(Level level, String msg) {
        getLogger().log(level, msg);
    }
}
