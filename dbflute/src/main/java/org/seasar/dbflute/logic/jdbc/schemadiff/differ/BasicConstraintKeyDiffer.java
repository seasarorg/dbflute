package org.seasar.dbflute.logic.jdbc.schemadiff.differ;

import java.util.Collection;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfConstraintDiff;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfDiffAssist;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfNextPreviousDiff;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfTableDiff;
import org.seasar.dbflute.properties.facade.DfDatabaseTypeFacadeProp;

/**
 * @author jflute
 * @param <KEY> The type of constraint key.
 * @param <DIFF> The type of constraint diff.
 */
public abstract class BasicConstraintKeyDiffer<KEY, DIFF extends DfConstraintDiff> implements
        ConstraintKeyDiffer<KEY, DIFF> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DfTableDiff _tableDiff;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BasicConstraintKeyDiffer(DfTableDiff tableDiff) {
        _tableDiff = tableDiff;
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isSameConstraintName(String next, String previous) {
        return isSame(next, previous);
    }

    public boolean isSameStructure(KEY next, KEY previous) {
        return isSame(column(next), column(previous));
    }

    // ===================================================================================
    //                                                                         Same Helper
    //                                                                         ===========
    protected boolean isSame(Object next, Object previous) {
        return DfDiffAssist.isSame(next, previous);
    }

    // ===================================================================================
    //                                                                  Next Previous Diff
    //                                                                  ==================
    protected DfNextPreviousDiff createNextPreviousDiff(String next, String previous) {
        return DfNextPreviousDiff.create(next, previous);
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected String buildCommaString(Collection<String> values) {
        final StringBuilder sb = new StringBuilder();
        int index = 0;
        for (String value : values) {
            if (index > 0) {
                sb.append(", ");
            }
            sb.append(value);
            ++index;
        }
        return sb.toString();
    }

    protected String extractConstraintName(KEY nextKey, KEY previousKey) {
        // either should be not null
        return nextKey != null ? constraintName(nextKey) : constraintName(previousKey);
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected static DfDatabaseTypeFacadeProp getDatabaseTypeFacadeProp() {
        return DfBuildProperties.getInstance().getBasicProperties().getDatabaseTypeFacadeProp();
    }

    protected boolean isDatabaseMySQL() {
        return getDatabaseTypeFacadeProp().isDatabaseMySQL();
    }

    protected boolean isDatabasePostgreSQL() {
        return getDatabaseTypeFacadeProp().isDatabasePostgreSQL();
    }

    protected boolean isDatabaseOracle() {
        return getDatabaseTypeFacadeProp().isDatabaseOracle();
    }

    protected boolean isDatabaseDB2() {
        return getDatabaseTypeFacadeProp().isDatabaseDB2();
    }

    protected boolean isDatabaseSQLServer() {
        return getDatabaseTypeFacadeProp().isDatabaseSQLServer();
    }

    protected boolean isDatabaseH2() {
        return getDatabaseTypeFacadeProp().isDatabaseH2();
    }

    protected boolean isDatabaseDerby() {
        return getDatabaseTypeFacadeProp().isDatabaseDerby();
    }
}
