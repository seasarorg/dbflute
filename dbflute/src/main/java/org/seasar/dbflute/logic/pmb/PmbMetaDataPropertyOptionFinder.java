package org.seasar.dbflute.logic.pmb;

import java.util.Map;

import org.seasar.dbflute.task.DfSql2EntityTask.DfParameterBeanMetaData;

/**
 * @author jflute
 * @since 0.6.3 (2008/02/05 Tuesday)
 */
public class PmbMetaDataPropertyOptionFinder {

    protected String _className;
    protected String _propertyName;
    Map<String, DfParameterBeanMetaData> _pmbMetaDataMap;

    public PmbMetaDataPropertyOptionFinder(String className, String propertyName,
            Map<String, DfParameterBeanMetaData> pmbMetaDataMap) {
        _className = className;
        _propertyName = propertyName;
        _pmbMetaDataMap = pmbMetaDataMap;
    }

    public String findPmbMetaDataPropertyOption(String className, String propertyName) {
        final DfParameterBeanMetaData meta = _pmbMetaDataMap.get(_className);
        if (meta == null) {
            return null;
        }
        final Map<String, String> optionMap = meta.getPropertyNameOptionMap();
        return optionMap != null ? optionMap.get(propertyName) : null;
    }
}
