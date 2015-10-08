/*
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
package vasylcts.soap.util.assisttypes;

/**
 *
 * @author VasylcTS
 */
public enum EnumAnswerType {

    NORMAL(null),
    XML("typeXML"), 
    CDATA("typeCDATA"),
    HEX("typeHEX"),
    BASE64("typeBASE64"),
    STRING("typeSimple"); // child element will be "SimpleString"

    private final String basicTypesElementEnding = "String";
    private final String wrapperName;

    EnumAnswerType(String wrapName) {
        wrapperName = wrapName;
    }

    /**
     * Get standard element ending
     * <p>
     * @return standard element ending. By default: "String".
     */
    public String getBasicTypesElementEnding() {
        return basicTypesElementEnding;
    }

    /**
     * Get child element name for this ENUM
     * <p>
     * @return Get child element name for this ENUM. For example:
     * "SimpleString", "CDATAString", "BASE64String".
     */
    public String getChildWrapperType() {
        return getWrapperType() == null ? null : getWrapperType() + basicTypesElementEnding;
    }

    public String getWrapperType() {
        return wrapperName == null ? null : wrapperName;
    }
    
    static public EnumAnswerType getAnswerTypeByWrapperName(String name)
    {
        if (name == null || name.isEmpty())
            return EnumAnswerType.NORMAL;
        else
            return EnumAnswerType.valueOf(name);
    }
}
