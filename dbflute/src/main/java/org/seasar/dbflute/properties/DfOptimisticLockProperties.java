package org.seasar.dbflute.properties;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.seasar.dbflute.util.DfNameHintUtil;

/**
 * Build properties for Torque.
 * 
 * @author mkubo
 */
public final class DfOptimisticLockProperties extends DfAbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public DfOptimisticLockProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                                    Properties - Optimistic Lock
    //                                                    ============================
    public String getUpdateDateFieldName() {
        return stringProp("torque.updateDateFieldName", "");
    }

    public List<Object> getUpdateDateExceptTableList() {
        return listProp("torque.updateDateExceptTableList", DEFAULT_EMPTY_LIST);
    }

    public boolean isUpdateDateExceptTable(String tableName) {
        if (tableName == null) {
            throw new NullPointerException("Argument[tableName] is required.");
        }

        final List<Object> exceptList = getUpdateDateExceptTableList();
        if (exceptList == null) {
            throw new IllegalStateException("getUpdateDateExceptTableList() must not return null: + " + tableName);
        }

        for (final Iterator<Object> ite = exceptList.iterator(); ite.hasNext();) {
            final String tableHint = (String) ite.next();
            if (DfNameHintUtil.isHitByTheHint(tableName, tableHint)) {
                return true;
            }
        }
        return false;
    }

    public String getVersionNoFieldName() {
        return stringProp("torque.versionNoFieldName", "");
    }
}