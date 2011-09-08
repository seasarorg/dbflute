package org.seasar.dbflute.logic.jdbc.schemadiff;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.facade.DfDatabaseTypeFacadeProp;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public abstract class DfAbstractDiff {

    // ===================================================================================
    //                                                                         Create Diff
    //                                                                         ===========
    protected DfTableDiff createTableDiff(Map<String, Object> tableDiffMap) {
        return DfTableDiff.createFromDiffMap(tableDiffMap);
    }

    protected DfColumnDiff createColumnDiff(Map<String, Object> columnDiffMap) {
        return DfColumnDiff.createFromDiffMap(columnDiffMap);
    }

    protected DfPrimaryKeyDiff createPrimaryKeyDiff(Map<String, Object> primaryKeyDiffMap) {
        return DfPrimaryKeyDiff.createFromDiffMap(primaryKeyDiffMap);
    }

    protected DfForeignKeyDiff createForeignKeyDiff(Map<String, Object> foreignKeyDiffMap) {
        return DfForeignKeyDiff.createFromDiffMap(foreignKeyDiffMap);
    }

    protected DfUniqueKeyDiff createUniqueKeyDiff(Map<String, Object> uniqueKeyDiffMap) {
        return DfUniqueKeyDiff.createFromDiffMap(uniqueKeyDiffMap);
    }

    protected DfIndexDiff createIndexDiff(Map<String, Object> indexDiffMap) {
        return DfIndexDiff.createFromDiffMap(indexDiffMap);
    }

    // ===================================================================================
    //                                                                  Next Previous Diff
    //                                                                  ==================
    protected DfNextPreviousDiff createNextPreviousDiff(String next, String previous) {
        return DfNextPreviousDiff.create(next, previous);
    }

    protected DfNextPreviousDiff createNextPreviousDiff(Integer next, Integer previous) {
        return DfNextPreviousDiff.create(next.toString(), previous.toString());
    }

    protected DfNextPreviousDiff createNextPreviousDiff(Boolean next, Boolean previous) {
        return DfNextPreviousDiff.create(next.toString(), previous.toString());
    }

    protected DfNextPreviousDiff restoreNextPreviousDiff(Map<String, Object> diffMap, String key) {
        final Object value = diffMap.get(key);
        if (value == null) {
            return null;
        }
        assertElementValueMap(key, value, diffMap);
        @SuppressWarnings("unchecked")
        final Map<String, Object> nextPreviousDiffMap = (Map<String, Object>) value;
        return DfNextPreviousDiff.create(nextPreviousDiffMap);
    }

    protected static interface NextPreviousDiffer<OBJECT, DIFF, TYPE> {
        TYPE provide(OBJECT obj);

        boolean isMatch(TYPE next, TYPE previous);

        void diff(DIFF diff, DfNextPreviousDiff nextPreviousDiff);

        String disp(TYPE obj, boolean next);
    }

    protected abstract class StringNextPreviousDiffer<OBJECT, DIFF> implements NextPreviousDiffer<OBJECT, DIFF, String> {
        public boolean isMatch(String next, String previous) {
            return isSame(next, previous);
        }

        public String disp(String obj, boolean next) {
            return obj.toString();
        }
    }

    protected abstract class BooleanNextPreviousDiffer<OBJECT, DIFF> implements
            NextPreviousDiffer<OBJECT, DIFF, Boolean> {
        public boolean isMatch(Boolean next, Boolean previous) {
            return isSame(next, previous);
        }

        public String disp(Boolean obj, boolean next) {
            return obj.toString();
        }
    }

    public static interface NextPreviousHandler { // accessed from Velocity template
        String titleName();

        String propertyName();

        DfNextPreviousDiff provide();

        void restore(Map<String, Object> diffMap);
    }

    // ===================================================================================
    //                                                                           Nest Diff
    //                                                                           =========
    protected void restoreNestDiff(Map<String, Object> parentDiffMap, NestDiffSetupper setupper) {
        final String key = setupper.propertyName();
        final Object value = parentDiffMap.get(key);
        if (value == null) {
            return;
        }
        assertElementValueMap(key, value, parentDiffMap);
        @SuppressWarnings("unchecked")
        final Map<String, Object> diffAllMap = (Map<String, Object>) value;
        final Set<Entry<String, Object>> entrySet = diffAllMap.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            final String name = entry.getKey();
            final Object diffObj = entry.getValue();
            assertElementValueMap(name, diffObj, diffAllMap);
            @SuppressWarnings("unchecked")
            final Map<String, Object> nestDiffMap = (Map<String, Object>) diffObj;
            setupper.setup(nestDiffMap);
        }
    }

    protected static interface NestDiffSetupper {
        String propertyName();

        List<? extends DfNestDiff> provide();

        void setup(Map<String, Object> diff);
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

    // ===================================================================================
    //                                                                         Same Helper
    //                                                                         ===========
    protected boolean isSame(Object next, Object previous) {
        if (next == null && previous == null) {
            return true;
        }
        if (next == null || previous == null) {
            return false;
        }
        return next.equals(previous);
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertElementValueMap(String key, Object value, Map<String, Object> diffMap) {
        if (!(value instanceof Map<?, ?>)) { // basically no way
            String msg = "The element in diff-map should be Map:";
            msg = msg + " key=" + key + " value=" + value + " diffMap=" + diffMap;
            throw new IllegalStateException(msg);
        }
    }
}
