package org.seasar.dbflute.logic.sql2entity.pmbean;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 * @since 0.6.3 (2008/02/05 Tuesday)
 */
public class DfPmbPropertyOptionFinder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _className;
    protected final String _propertyName;
    protected final Map<String, DfPmbMetaData> _pmbMetaDataMap;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfPmbPropertyOptionFinder(String className, String propertyName, Map<String, DfPmbMetaData> pmbMetaDataMap) {
        _className = className;
        _propertyName = propertyName;
        _pmbMetaDataMap = pmbMetaDataMap;
    }

    // ===================================================================================
    //                                                                         Find Option
    //                                                                         ===========
    public String findPmbMetaDataPropertyOption(String className, String propertyName) {
        final DfPmbMetaData meta = _pmbMetaDataMap.get(_className);
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
