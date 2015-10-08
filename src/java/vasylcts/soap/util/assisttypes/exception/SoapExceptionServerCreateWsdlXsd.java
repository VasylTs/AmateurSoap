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
 * The class {@code SoapExceptionServerCreateWsdlXsd} extends class
 * {@code SoapExceptionServer} and it indicates conditions when error
 * happened while creating WSDL or XSD.
 * <p>
 * @author VasylcTS
 * @see vasylcts.soap.util.assisttypes.exception.SoapExceptionServer
 */
public class SoapExceptionServerCreateWsdlXsd extends SoapExceptionServer {

    public SoapExceptionServerCreateWsdlXsd(String message, String detail) {
        super(message, "<![CDATA[" + detail + "]]>");
    }

    public SoapExceptionServerCreateWsdlXsd(String message) {
        super(message);
    }

}
