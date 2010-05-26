package org.seasar.dbflute.logic.sql2entity.pmbean;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 * @since 0.6.3 (2008/02/05 Tuesday)
 */
public class PmbMetaDataPropertyOptionFinder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _className;
    protected String _propertyName;
    protected Map<String, DfParameterBeanMetaData> _pmbMetaDataMap;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public PmbMetaDataPropertyOptionFinder(String className, String propertyName,
            Map<String, DfParameterBeanMetaData> pmbMetaDataMap) {
        _className = className;
        _propertyName = propertyName;
        _pmbMetaDataMap = pmbMetaDataMap;
    }

    // ===================================================================================
    //                                                                         Find Option
    //                                                                         ===========
    public String findPmbMetaDataPropertyOption(String className, String propertyName) {
        final DfParameterBeanMetaData meta = _pmbMetaDataMap.get(_className);
        if (meta == null) {
            return null;
        }
        final Map<String, String> optionMap = meta.getPropertyNameOptionMap();
        return optionMap != null ? optionMap.get(propertyName) : null;
    }

    // ===================================================================================
    //                                                                         Option Util
    //                                                                         ===========
    public static List<String> splitOption(String option) {
        final String delimiter = "|";
        return DfStringUtil.splitList(option, delimiter);
    }
}
