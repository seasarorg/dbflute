package org.seasar.dbflute.properties.assistant.classification;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.exception.DfClassificationRequiredAttributeNotFoundException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;

/**
 * @author jflute
 * @since 0.9.5.1 (2009/07/03 Friday)
 */
public class DfClassificationLiteralArranger {

    public void arrange(String classificationName, Map<String, String> elementMap, List<Map<String, String>> elementList) {
        final String codeKey = DfClassificationElement.KEY_CODE;
        final String nameKey = DfClassificationElement.KEY_NAME;
        final String aliasKey = DfClassificationElement.KEY_ALIAS;

        final String code = (String) elementMap.get(codeKey);
        if (code == null) {
            throwClassificationLiteralCodeNotFoundException(classificationName, elementMap);
        }
        final String name = (String) elementMap.get(nameKey);
        if (name == null) {
            elementMap.put(nameKey, code);
        }
        final String alias = (String) elementMap.get(aliasKey);
        if (alias == null) {
            elementMap.put(aliasKey, name != null ? name : code);
        }
        elementList.add(elementMap);
    }

    protected void throwClassificationLiteralCodeNotFoundException(String classificationName,
            Map<String, String> elementMap) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The code attribute of the classification was not found.");
        br.addItem("Advice");
        br.addElement("The classification should have the code attribute.");
        br.addElement("See the document for the DBFlute property.");
        br.addItem("Classification");
        br.addElement(classificationName);
        br.addItem("Element Map");
        br.addElement(elementMap);
        final String msg = br.buildExceptionMessage();
        throw new DfClassificationRequiredAttributeNotFoundException(msg);
    }
}
