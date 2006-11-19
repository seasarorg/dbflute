package org.seasar.dbflute.helper.properties;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.torque.helper.stateless.NameHintUtil;

/**
 * Build properties for Torque.
 * 
 * @author mkubo
 */
public final class OptimisticLockProperties extends AbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public OptimisticLockProperties(Properties prop) {
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

        final List exceptList = getUpdateDateExceptTableList();
        if (exceptList == null) {
            throw new IllegalStateException("getUpdateDateExceptTableList() must not return null: + " + tableName);
        }

        for (final Iterator ite = exceptList.iterator(); ite.hasNext();) {
            final String tableHint = (String) ite.next();
            if (NameHintUtil.isHitByTheHint(tableName, tableHint)) {
                return true;
            }
        }
        return false;
    }

    public String getVersionNoFieldName() {
        return stringProp("torque.versionNoFieldName", "");
    }
}