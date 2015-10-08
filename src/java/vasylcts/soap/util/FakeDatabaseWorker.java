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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vasylcts.soap.util.assisttypes.EnumAnswerType;

/**
 * It`s a test class for getting XSDs.
 * <p>
 * @author VasylcTS
 */
public class FakeDatabaseWorker implements IDatabaseWorker {

    private Map<String, ActionDescription> methodsDescr;
    private List<String> methodList;

    public FakeDatabaseWorker() {
        XSDBuilder.getInstance();
        initMap();
    }

    @Override
    public List<String> getMethodsFromDBForClient(String clientIP) throws SQLException {
        return methodList;
    }

    @Override
    public Map<String, ActionDescription> getMethodsDescriptionForClientFromDB(String clientIP) throws SQLException {
        return methodsDescr;
    }

    @Override
    public ActionDescription getActionFullDescriptionForClientFromDB(String clientIP, String actionName) throws SQLException {
        return methodsDescr.get(actionName);
    }

    private void initMap() {
        methodList = new ArrayList<>();
        methodsDescr = new HashMap<>();
        putActionSayHelloToAliens();
        putUnknownMethod();
        methodList.add("sendTaskToAliens");
        methodList.add("sayGoodbyeToAliens");

        methodList.add("destroyAlienShip");
    }

    private void putUnknownMethod() {
        String actionName = "unknownMethod";
        methodList.add(actionName);

        String schemaString = XSDBuilder.getSCHEMA_NAMESPACE_PREFIX() + ":string";
        ActionDescription aDesc = new ActionDescription();
        aDesc.setAction(actionName);
        // set "in" params
        aDesc.setInElementName(actionName + "Request");
        aDesc.setInXSD(createComplexType(true, actionName + "Request",
                new SimpleParam("someData", schemaString)));
        // set "out" params
        aDesc.setOutElementName(actionName + "Response");
        aDesc.setOutXSD(createComplexType(false, actionName + "Response",
                new SimpleParam("typeCDATA", XSDBuilder.getSERVICE_NAMESPACE_PREFIX() + ":typeCDATA")));
        
        
        aDesc.setSoapAnswerType(EnumAnswerType.CDATA);
        methodsDescr.put(actionName, aDesc);
    }
    
    private void putActionSayHelloToAliens() {
        String actionName = "sayHelloToAliens";
        methodList.add(actionName);

        String schemaString = XSDBuilder.getSCHEMA_NAMESPACE_PREFIX() + ":string";
        ActionDescription aDesc = new ActionDescription();
        aDesc.setAction(actionName);
        // set "in" params
        aDesc.setInElementName(actionName + "Request");
        aDesc.setInXSD(createComplexType(true, actionName + "Request",
                new SimpleParam("name", schemaString),
                new SimpleParam("helloMessage", schemaString)));
        // set "out" params
        aDesc.setOutElementName(actionName + "Response");
        aDesc.setOutXSD(createComplexType(false, actionName + "Response",
                new SimpleParam("alienName", schemaString),
                new SimpleParam("responseMessage", schemaString),
                new SimpleParam("responseDate", XSDBuilder.getSCHEMA_NAMESPACE_PREFIX() + ":date")));

        methodsDescr.put(actionName, aDesc);
    }

    private String createComplexType(boolean isInParam, String typeName, SimpleParam... elementDescr) {
        String serviceNS = XSDBuilder.getSERVICE_NAMESPACE_PREFIX();
        String schemaNS = XSDBuilder.getSCHEMA_NAMESPACE_PREFIX();

        StringBuilder sb = new StringBuilder();
        sb.append("<").append(schemaNS).append(":element name=\"").append(typeName)
                .append("\" type=\"").append(serviceNS).append(":").append(typeName).append("\"/>\n\n");

        sb.append("<").append(schemaNS).append(":complexType name=\"").append(typeName).append("\">\n");
        sb.append("  <").append(schemaNS).append(":complexContent>\n");
        sb.append("   <").append(schemaNS).append(":extension base=\"").append(serviceNS).append(":").append(isInParam ? "additionalInfoRequest" : "additionalInfoResponse").append("\">\n");
        sb.append("    <").append(schemaNS).append(":sequence>\n");
        for (SimpleParam element : elementDescr) {
            sb.append("        <").append(schemaNS).append(":element name=\"")
                    .append(element.paramName)
                    .append("\" type=\"")
                    .append(element.paramValue)
                    .append("\" minOccurs=\"")
                    .append(element.isRequired ? "1" : "0")
                    .append("\" maxOccurs=\"1\"/>\n");
        }
        sb.append("      </").append(schemaNS).append(":sequence>\n");
        sb.append("   </").append(schemaNS).append(":extension>\n");
        sb.append("  </").append(schemaNS).append(":complexContent>\n");
        sb.append("</").append(schemaNS).append(":complexType>\n");

        return sb.toString();
    }
}
