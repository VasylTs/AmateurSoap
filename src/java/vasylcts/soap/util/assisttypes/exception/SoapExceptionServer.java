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
package vasylcts.soap.util.assisttypes.exception;

/**
 * The class {@code SoapExceptionClient} indicates conditions when error
 * happened because of server error
 * <p>
 * @author VasylcTS
 * @see vasylcts.soap.util.assisttypes.exception.SoapExceptionServerCreateWsdlXsd
 */
public class SoapExceptionServer extends Exception {

    private final String detail;

    public SoapExceptionServer(String message) {
        super(message);
        detail = null;
    }

    public SoapExceptionServer(String message, Throwable cause) {
        super(message, cause);
        detail = null;
    }

    public SoapExceptionServer(String message, String detail) {
        super(message);
        this.detail = detail;
    }

    public SoapExceptionServer(String message, String detail, Throwable cause) {
        super(message, cause);
        this.detail = detail;
    }

    public String getDetail() {
        return this.detail;
    }

}
