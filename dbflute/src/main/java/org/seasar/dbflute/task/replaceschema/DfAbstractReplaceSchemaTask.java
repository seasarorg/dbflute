package org.seasar.dbflute.task.replaceschema;

import org.seasar.dbflute.task.bs.DfAbstractTask;

/**
 * @author jflute
 * @since 0.7.9 (2008/08/22 Friday)
 */
public abstract class DfAbstractReplaceSchemaTask extends DfAbstractTask {

    // ===================================================================================
    //                                                                 DataSource Override
    //                                                                 ===================
    @Override
    protected boolean isUseDataSource() {
        return true;
    }

    // ===================================================================================
    //                                                                      Various Common
    //                                                                      ==============
    protected String resolveTerminater4Tool() {
        return getBasicProperties().isDatabaseOracle() ? "/" : null;
    }

    protected boolean isDbCommentLineForIrregularPattern(String line) {
        // for irregular pattern
        line = line.trim().toLowerCase();
        if (getBasicProperties().isDatabaseMySQL()) {
            if (line.contains("comment='") || line.contains("comment = '") || line.contains(" comment '")) {
                return true;
            }
        }
        if (getBasicProperties().isDatabaseSQLServer()) {
            if (line.startsWith("exec sys.sp_addextendedproperty @name=n'ms_description'")) {
                return true;
            }
        }
        return false;
    }
}
