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
public class ActionDescription {

    private String action;
    private EnumAnswerType soapAnswerType;
    private String inXSD;
    private String inElementName;
    private String inXSLT;
    private String outXSD;
    private String outElementName;
    private String outXSLT;

    public boolean isMinParamsSetted() {
        return (inXSD != null
                && inElementName != null
                && outXSD != null
                && outElementName != null);
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the soapAnswerType
     */
    public EnumAnswerType getSoapAnswerType() {
        return soapAnswerType;
    }

    /**
     * @param soapAnswerType the soapAnswerType to set
     */
    public void setSoapAnswerType(EnumAnswerType soapAnswerType) {
        this.soapAnswerType = soapAnswerType;
    }

    /**
     * @return the inXSD
     */
    public String getInXSD() {
        return inXSD;
    }

    /**
     * @param inXSD the inXSD to set
     */
    public void setInXSD(String inXSD) {
        this.inXSD = inXSD;
    }

    /**
     * @return the inElementName
     */
    public String getInElementName() {
        return inElementName;
    }

    /**
     * @param inElementName the inElementName to set
     */
    public void setInElementName(String inElementName) {
        this.inElementName = inElementName;
    }

    /**
     * @return the inXSLT
     */
    public String getInXSLT() {
        return inXSLT;
    }

    /**
     * @param inXSLT the inXSLT to set
     */
    public void setInXSLT(String inXSLT) {
        this.inXSLT = inXSLT;
    }

    /**
     * @return the outXSD
     */
    public String getOutXSD() {
        return outXSD;
    }

    /**
     * @param outXSD the outXSD to set
     */
    public void setOutXSD(String outXSD) {
        this.outXSD = outXSD;
    }

    /**
     * @return the outElementName
     */
    public String getOutElementName() {
        return outElementName;
    }

    /**
     * @param outElementName the outElementName to set
     */
    public void setOutElementName(String outElementName) {
        this.outElementName = outElementName;
    }

    /**
     * @return the outXSLT
     */
    public String getOutXSLT() {
        return outXSLT;
    }

    /**
     * @param outXSLT the outXSLT to set
     */
    public void setOutXSLT(String outXSLT) {
        this.outXSLT = outXSLT;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.action)
                .append(" = [")
                .append("in_element_name = ").append(this.inElementName).append(";\n")
                .append("in_xsd = ").append(this.inXSD).append(";\n")
                .append("in_xslt = ").append(this.inXSLT).append(";\n")
                .append("out_element_name = ").append(this.outElementName).append(";\n")
                .append("out_xsd = ").append(this.outXSD).append(";\n")
                .append("in_xslt = ").append(this.outXSLT).append(";\n")
                .append("soap_answer_type = ").append(this.soapAnswerType).append(";\n")
                .append(" ]\n");
        return sb.toString();
    }

}
