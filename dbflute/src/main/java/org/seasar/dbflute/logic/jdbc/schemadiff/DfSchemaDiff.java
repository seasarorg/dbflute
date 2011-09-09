package org.seasar.dbflute.logic.jdbc.schemadiff;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.ForeignKey;
import org.apache.torque.engine.database.model.Index;
import org.apache.torque.engine.database.model.Table;
import org.apache.torque.engine.database.model.Unique;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.logic.jdbc.schemaxml.DfSchemaXmlReader;
import org.seasar.dbflute.resource.DBFluteSystem;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public class DfSchemaDiff extends DfAbstractDiff {

    //[diff-date] = map:{
    //    ; diffDate = 2010/12/12 12:34:56
    //    ; tableCount = map:{ next = 123 ; previous = 145}
    //    ; tableDiff = map:{
    //        ; [table-name] = map:{
    //            ; diffType = [ADD or CHANGE or DELETE]
    //            ; unifiedSchemaDiff = map:{ next = [schema] ; previous = [schema] }
    //            ; objectTypeDiff = map:{ next = [type] ; previous = [type] }
    //            ; columnDefOrderDiff = map:{ next = [column-index-exp] ; previous = [column-index-exp] }
    //            ; columnDiff = map:{
    //                [column-name] = map:{
    //                    ; diffType = [ADD or CHANGE or DELETE]
    //                    ; dbTypeDiff = map:{next = [db-type-name]; previous = [db-type-name]}
    //                    ; columnSizeDiff = map:{next = [column-size&digit]; previous = [column-size&digit]}
    //                    ; defaultValueDiff = map:{next = [default-value]; previous = [default-value]}
    //                    ; notNullDiff = map:{next = [true or false] ; previous = [true or false]}
    //                    ; autoIncrementDiff = map:{next = [true or false] ; previous = [true or false]}
    //                }
    //            }
    //            ; primaryKeyDiff = map:{
    //                ; [pk-name] = map:{
    //                    ; diffType = [ADD or CHANGE or DELETE]
    //                    ; nameDiff = map:{
    //                        ; next = [constraint-name]
    //                        ; previous = [constraint-name]
    //                    }
    //                    ; columnDiff = map:{
    //                        ; next = [column-name, ...]
    //                        ; previous = [column-name, ...]
    //                    }
    //                }
    //            }
    //            ; foreingkKeyDiff = map:{
    //                ; [fk-name] = map:{
    //                    ; diffType = [ADD or CHANGE or DELETE]
    //                    ; nameDiff = map:{
    //                        ; next = [constraint-name]
    //                        ; previous = [constraint-name]
    //                    }
    //                    ; columnDiff = map:{
    //                        ; next = [column-name, ...]
    //                        ; previous = [column-name, ...]
    //                    }
    //                    ; foreignTableDiff = map:{
    //                        ; next = [table-name]
    //                        ; previous = [table-name]
    //                    }
    //                }
    //            }
    //            ; uniqueKeyDiff = map:{
    //                ; [uq-name] = map:{
    //                    ; diffType = [ADD or CHANGE or DELETE]
    //                    ; nameDiff = map:{
    //                        ; next = [constraint-name]
    //                        ; previous = [constraint-name]
    //                    }
    //                    ; columnDiff = map:{
    //                        ; next = [column-name, ...]
    //                        ; previous = [column-name, ...]
    //                    }
    //                }
    //            }
    //            ; indexDiff = map:{
    //                ; [index-name] = map:{
    //                    ; diffType = [ADD or CHANGE or DELETE]
    //                    ; nameDiff = map:{
    //                        ; next = [constraint-name]
    //                        ; previous = [constraint-name]
    //                    }
    //                    ; columnDiff = map:{
    //                        ; next = [column-name, ...]
    //                        ; previous = [column-name, ...]
    //                    }
    //                }
    //            }
    //        }
    //    }
    //}

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String DIFF_DATE_KEY = "diffDate";
    public static final String DIFF_DATE_PATTERN = "yyyy/MM/dd HH:mm:ss";
    public static final String COMMENT_KEY = "comment";
    public static final String TABLE_COUNT_KEY = "tableCount";
    public static final String TABLE_DIFF_KEY = "tableDiff";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                           Load Schema
    //                                           -----------
    protected final DfSchemaXmlReader _nextReader;
    protected final DfSchemaXmlReader _previousReader;
    protected Database _nextDb; // not null after next loading
    protected Database _previousDb; // not null after previous loading
    protected Integer _previousTableCount; // not null after previous loading
    protected boolean _firstTime; // judged when loading previous schema
    protected boolean _loadingFailure; // judged when loading previous schema

    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    protected Date _diffDate; // not null after loading next schema
    protected String _comment; // after restoring
    protected DfNextPreviousDiff _tableCountDiff; // not null after next loading

    // -----------------------------------------------------
    //                                                Option
    //                                                ------
    protected boolean _checkColumnDefOrder; // depends on DBFlute property
    protected boolean _suppressUnifiedSchema; // basically for SchemaSyncCheck

    // -----------------------------------------------------
    //                                            Table Diff
    //                                            ----------
    protected final List<DfTableDiff> _tableDiffAllList = DfCollectionUtil.newArrayList();
    protected final List<DfTableDiff> _addedTableDiffList = DfCollectionUtil.newArrayList();
    protected final List<DfTableDiff> _changedTableDiffList = DfCollectionUtil.newArrayList();
    protected final List<DfTableDiff> _deletedTableDiffList = DfCollectionUtil.newArrayList();

    protected List<NestDiffSetupper> _nestDiffList = DfCollectionUtil.newArrayList();
    {
        _nestDiffList.add(new NestDiffSetupper() {
            public String propertyName() {
                return TABLE_DIFF_KEY;
            }

            public List<? extends DfNestDiff> provide() {
                return _tableDiffAllList;
            }

            public void setup(Map<String, Object> diff) {
                addTableDiff(createTableDiff(diff));
            }
        });
    }

    // -----------------------------------------------------
    //                                             Meta Info
    //                                             ---------
    protected boolean _latest;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfSchemaDiff(DfSchemaXmlReader previousReader, DfSchemaXmlReader nextReader) {
        _previousReader = previousReader;
        _nextReader = nextReader;
    }

    public static DfSchemaDiff createAsCore() {
        // no need to filter when reading here
        final DfSchemaXmlReader reader = DfSchemaXmlReader.createAsCoreToManage();
        return new DfSchemaDiff(reader, reader);
    }

    public static DfSchemaDiff createAsFlexible(String schemaXml) {
        // no need to filter when reading here
        final DfSchemaXmlReader reader = DfSchemaXmlReader.createAsFlexibleToManage(schemaXml);
        return new DfSchemaDiff(reader, reader);
    }

    public static DfSchemaDiff createAsFlexible(String previousXml, String nextXml) {
        // no need to filter when reading here
        final DfSchemaXmlReader previousReader = DfSchemaXmlReader.createAsFlexibleToManage(previousXml);
        final DfSchemaXmlReader nextReader = DfSchemaXmlReader.createAsFlexibleToManage(nextXml);
        return new DfSchemaDiff(previousReader, nextReader);
    }

    public static DfSchemaDiff createAsPlain(DfSchemaXmlReader previousReader, DfSchemaXmlReader nextReader) {
        return new DfSchemaDiff(previousReader, nextReader);
    }

    // ===================================================================================
    //                                                                         Load Schema
    //                                                                         ===========
    public void loadPreviousSchema() { // before loading next schema
        final DfSchemaXmlReader reader = _previousReader;
        if (!reader.exists()) {
            _firstTime = true;
            return;
        }
        try {
            _previousDb = reader.read().getDatabase();
        } catch (RuntimeException e) {
            _loadingFailure = true;
            handleReadingException(e, reader);
        }
        _previousTableCount = _previousDb.getTableList().size();
    }

    public void loadNextSchema() { // after loading previous schema
        if (isFirstTime()) {
            String msg = "You should not call this because of first time.";
            throw new IllegalStateException(msg);
        }
        if (_previousDb == null) {
            String msg = "You should not call this because of previous not loaded.";
            throw new IllegalStateException(msg);
        }
        final DfSchemaXmlReader reader = _nextReader;
        try {
            _nextDb = reader.read().getDatabase();
        } catch (RuntimeException e) {
            handleReadingException(e, reader);
        }
        _diffDate = new Date(DBFluteSystem.currentTimeMillis());
        final int nextTableCount = _nextDb.getTableList().size();
        _tableCountDiff = createNextPreviousDiff(nextTableCount, _previousTableCount);
    }

    protected void handleReadingException(Exception e, DfSchemaXmlReader reader) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to load schema XML.");
        br.addItem("SchemaXML");
        br.addElement(reader.getSchemaXml());
        br.addItem("Exception");
        br.addElement(e.getClass().getName());
        br.addElement(e.getMessage());
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg, e);
    }

    // ===================================================================================
    //                                                                        Analyze Diff
    //                                                                        ============
    public void analyzeDiff() {
        processAddedTable();
        processChangedTable();
        processDeletedTable();
    }

    // ===================================================================================
    //                                                                       Table Process
    //                                                                       =============
    // -----------------------------------------------------
    //                                                 Added
    //                                                 -----
    protected void processAddedTable() {
        final List<Table> tableList = _nextDb.getTableList();
        for (Table table : tableList) {
            final Table found = findPreviousTable(table);
            if (found == null || !isSameTableName(table, found)) { // added
                addTableDiff(DfTableDiff.createAdded(table.getName()));
            }
        }
    }

    // -----------------------------------------------------
    //                                               Changed
    //                                               -------
    protected void processChangedTable() {
        final List<Table> tableList = _nextDb.getTableList();
        for (Table next : tableList) {
            final Table previous = findPreviousTable(next);
            if (previous == null || !isSameTableName(next, previous)) {
                continue;
            }
            // found
            final DfTableDiff tableDiff = DfTableDiff.createChanged(next.getName());

            // direct attributes
            processUnifiedSchema(next, previous, tableDiff);
            processObjectType(next, previous, tableDiff);
            processColumnDefOrder(next, previous, tableDiff);

            // nested attributes
            processColumn(tableDiff, next, previous);
            processPrimaryKey(tableDiff, next, previous);
            processForeignKey(tableDiff, next, previous);
            processUniqueKey(tableDiff, next, previous);
            processIndex(tableDiff, next, previous);

            if (tableDiff.hasDiff()) { // changed
                addTableDiff(tableDiff);
            }
        }
    }

    protected void processUnifiedSchema(Table next, Table previous, DfTableDiff tableDiff) {
        if (_suppressUnifiedSchema) {
            return;
        }
        diffNextPrevious(next, previous, tableDiff, new StringNextPreviousDiffer<Table, DfTableDiff>() {
            public String provide(Table obj) {
                return obj.getUnifiedSchema().getCatalogSchema();
            }

            public void diff(DfTableDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                diff.setUnifiedSchemaDiff(nextPreviousDiff);
            }
        });
    }

    protected void processObjectType(Table next, Table previous, DfTableDiff tableDiff) {
        diffNextPrevious(next, previous, tableDiff, new StringNextPreviousDiffer<Table, DfTableDiff>() {
            public String provide(Table obj) {
                return obj.getType();
            }

            public void diff(DfTableDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                diff.setObjectTypeDiff(nextPreviousDiff);
            }
        });
    }

    protected void processColumnDefOrder(Table next, Table previous, DfTableDiff tableDiff) {
        if (!_checkColumnDefOrder) {
            return;
        }
        diffNextPrevious(next, previous, tableDiff, new ColumnDefOrderDiffer());
    }

    protected static class ColumnDefOrderDiffer implements NextPreviousDiffer<Table, DfTableDiff, Table> {

        private static final String KEY_NEXT_NAME = "nextName";
        private static final String KEY_NEXT_NUMBER = "nextNumber";
        private static final String KEY_PREVIOUS_NAME = "previousName";
        private static final String KEY_PREVIOUS_NUMBER = "previousNumber";

        protected final List<Map<String, Object>> _diffList = DfCollectionUtil.newArrayList();

        public Table provide(Table obj) {
            return obj;
        }

        public boolean isMatch(Table next, Table previous) {
            // compare without added or deleted columns
            // (and renamed columns cannot be cached by DBFlute)
            final List<String> nextList = createCompareColumnList(next, previous);
            final List<String> previousList = createCompareColumnList(previous, next);
            filterCompareColumnList(next, previous, nextList, previousList);
            while (true) {
                final Map<String, Object> foundDiffMap = findColumnDefOrder(next, previous, nextList, previousList);
                if (foundDiffMap == null) {
                    break;
                }
                _diffList.add(foundDiffMap);

                // remove the found column finished being compared
                nextList.remove(foundDiffMap.get(KEY_NEXT_NAME));
                previousList.remove(foundDiffMap.get(KEY_PREVIOUS_NAME));
            }
            return _diffList.isEmpty();
        }

        public void diff(DfTableDiff diff, DfNextPreviousDiff nextPreviousDiff) {
            diff.setColumnDefOrderDiff(nextPreviousDiff);
        }

        public String disp(Table obj, boolean next) {
            if (next) {
                return buildDisp(KEY_NEXT_NAME, KEY_NEXT_NUMBER);
            } else {
                return buildDisp(KEY_PREVIOUS_NAME, KEY_PREVIOUS_NUMBER);
            }
        }

        protected String buildDisp(String nameKey, String numberKey) {
            final StringBuilder sb = new StringBuilder();
            for (Map<String, Object> diffMap : _diffList) {
                final Object name = diffMap.get(nameKey);
                final Object number = diffMap.get(numberKey);
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(name).append("(").append(number).append(")");
            }
            return sb.toString();
        }

        protected List<String> createCompareColumnList(Table main, Table target) {
            // *uses LinkedList because the lists are removed so many times
            final List<String> mainList = new LinkedList<String>();
            for (Column column : main.getColumnList()) {
                final Column corresponding = target.getColumn(column.getName());
                if (corresponding == null) {
                    continue;
                }
                mainList.add(column.getName());
            }
            return mainList;
        }

        protected void filterCompareColumnList(Table next, Table previous, List<String> nextList,
                List<String> previousList) {
            final List<String> removedNextList = DfCollectionUtil.newArrayList();
            final List<String> removedPreviousList = DfCollectionUtil.newArrayList();
            for (int i = 0; i < nextList.size(); i++) {
                final String nextName = nextList.get(i);
                final String previousSameOrderName = previousList.get(i);
                if (nextName.equalsIgnoreCase(previousSameOrderName)) {
                    removedNextList.add(nextName);
                    removedPreviousList.add(previousSameOrderName);
                }
            }
            for (String removedName : removedNextList) {
                nextList.remove(next.getColumn(removedName).getName());
            }
            for (String removedName : removedPreviousList) {
                previousList.remove(previous.getColumn(removedName).getName());
            }
        }

        protected Map<String, Object> findColumnDefOrder(Table next, Table previous, List<String> nextList,
                List<String> previousList) {
            Map<String, Object> resultMap = null;
            for (int i = 0; i < nextList.size(); i++) {
                final String nextName = nextList.get(i);
                final String previousSameOrderName = previousList.get(i);
                if (nextName.equalsIgnoreCase(previousSameOrderName)) {
                    continue; // basically no way because of filtered already
                }
                resultMap = new HashMap<String, Object>();
                resultMap.put(KEY_NEXT_NAME, next.getColumn(nextName).getName());
                resultMap.put(KEY_NEXT_NUMBER, next.getColumnIndex(nextName) + 1);
                final String previousCorrespondingName = previous.getColumn(nextName).getName();
                resultMap.put(KEY_PREVIOUS_NAME, previousCorrespondingName);
                resultMap.put(KEY_PREVIOUS_NUMBER, previous.getColumnIndex(previousCorrespondingName) + 1);
                break;
            }
            return resultMap;
        }
    }

    protected <TYPE> void diffNextPrevious(Table next, Table previous, DfTableDiff diff,
            NextPreviousDiffer<Table, DfTableDiff, TYPE> differ) {
        final TYPE nextValue = differ.provide(next);
        final TYPE previousValue = differ.provide(previous);
        if (!differ.isMatch(nextValue, previousValue)) {
            final String nextDisp = differ.disp(nextValue, true);
            final String previousDisp = differ.disp(previousValue, false);
            differ.diff(diff, createNextPreviousDiff(nextDisp, previousDisp));
        }
    }

    // -----------------------------------------------------
    //                                               Deleted
    //                                               -------
    protected void processDeletedTable() {
        final List<Table> tableList = _previousDb.getTableList();
        for (Table table : tableList) {
            final Table found = findNextTable(table);
            if (found == null || !isSameTableName(table, found)) { // deleted
                addTableDiff(DfTableDiff.createDeleted(table.getName()));
            }
        }
    }

    // -----------------------------------------------------
    //                                           Same Helper
    //                                           -----------
    protected boolean isSameTableName(Table next, Table previous) {
        return isSame(next.getName(), previous.getName());
    }

    // ===================================================================================
    //                                                                      Column Process
    //                                                                      ==============
    // -----------------------------------------------------
    //                                                  Main
    //                                                  ----
    protected void processColumn(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        processAddedColumn(tableDiff, nextTable, previousTable);
        processChangedColumn(tableDiff, nextTable, previousTable);
        processDeletedColumn(tableDiff, nextTable, previousTable);
    }

    // -----------------------------------------------------
    //                                                 Added
    //                                                 -----
    protected void processAddedColumn(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        final List<Column> columnList = nextTable.getColumnList();
        for (Column column : columnList) {
            final Column found = previousTable.getColumn(column.getName());
            if (found == null || !isSameColumnName(column, found)) { // added
                tableDiff.addColumnDiff(DfColumnDiff.createAdded(column.getName()));
            }
        }
    }

    // -----------------------------------------------------
    //                                               Changed
    //                                               -------
    protected void processChangedColumn(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        final List<Column> columnList = nextTable.getColumnList();
        for (Column next : columnList) {
            final Column previous = previousTable.getColumn(next.getName());
            if (previous == null || !isSameColumnName(next, previous)) {
                continue;
            }
            // found
            final DfColumnDiff columnDiff = DfColumnDiff.createChanged(next.getName());
            diffNextPrevious(next, previous, columnDiff, new StringNextPreviousDiffer<Column, DfColumnDiff>() {
                public String provide(Column obj) {
                    return obj.getDbType();
                }

                public void diff(DfColumnDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                    diff.setDbTypeDiff(nextPreviousDiff);
                }
            });
            diffNextPrevious(next, previous, columnDiff, new StringNextPreviousDiffer<Column, DfColumnDiff>() {
                public String provide(Column obj) {
                    return obj.getColumnSize();
                }

                public void diff(DfColumnDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                    diff.setColumnSizeDiff(nextPreviousDiff);
                }
            });
            diffNextPrevious(next, previous, columnDiff, new StringNextPreviousDiffer<Column, DfColumnDiff>() {
                public String provide(Column obj) {
                    return obj.getDefaultValue();
                }

                @Override
                public boolean isMatch(String next, String previous) {
                    if (super.isMatch(next, previous)) {
                        return true;
                    }
                    final boolean bothValid = next != null && previous != null;
                    if (bothValid && isDatabaseH2()) {
                        if (Srl.hasKeywordAllIgnoreCase("SYSTEM_SEQUENCE", next, previous)) {
                            return true;
                        }
                    }
                    return false;
                }

                public void diff(DfColumnDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                    diff.setDefaultValueDiff(nextPreviousDiff);
                }
            });
            diffNextPrevious(next, previous, columnDiff, new BooleanNextPreviousDiffer<Column, DfColumnDiff>() {
                public Boolean provide(Column obj) {
                    return obj.isNotNull();
                }

                public void diff(DfColumnDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                    diff.setNotNullDiff(nextPreviousDiff);
                }
            });
            diffNextPrevious(next, previous, columnDiff, new BooleanNextPreviousDiffer<Column, DfColumnDiff>() {
                public Boolean provide(Column obj) {
                    return obj.isAutoIncrement();
                }

                public void diff(DfColumnDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                    diff.setAutoIncrementDiff(nextPreviousDiff);
                }
            });
            if (columnDiff.hasDiff()) { // changed
                tableDiff.addColumnDiff(columnDiff);
            }
        }
    }

    protected <ITEM, TYPE> void diffNextPrevious(Column next, Column previous, DfColumnDiff diff,
            NextPreviousDiffer<Column, DfColumnDiff, TYPE> differ) {
        final TYPE nextValue = differ.provide(next);
        final TYPE previousValue = differ.provide(previous);
        if (!differ.isMatch(nextValue, previousValue)) {
            final String nextStr = nextValue != null ? nextValue.toString() : null;
            final String previousStr = previousValue != null ? previousValue.toString() : null;
            differ.diff(diff, createNextPreviousDiff(nextStr, previousStr));
        }
    }

    // -----------------------------------------------------
    //                                               Deleted
    //                                               -------
    protected void processDeletedColumn(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        final List<Column> columnList = previousTable.getColumnList();
        for (Column column : columnList) {
            final Column found = nextTable.getColumn(column.getName());
            if (found == null || !isSameColumnName(column, found)) { // deleted
                tableDiff.addColumnDiff(DfColumnDiff.createDeleted(column.getName()));
            }
        }
    }

    // -----------------------------------------------------
    //                                           Same Helper
    //                                           -----------
    protected boolean isSameColumnName(Column next, Column previous) {
        return isSame(next.getName(), previous.getName());
    }

    // ===================================================================================
    //                                                                  PrimaryKey Process
    //                                                                  ==================
    protected void processPrimaryKey(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        if (!nextTable.hasPrimaryKey() && !previousTable.hasPrimaryKey()) {
            return; // both no PK
        }
        final String noNamePKName = "(PK)";
        final String nextName = nextTable.getPrimaryKeyConstraintName();
        final String previousName = previousTable.getPrimaryKeyConstraintName();
        if (nextName == null && previousName == null) { // has PK but both no name
            if (hasSameStructurePrimaryKey(nextTable, previousTable)) {
                return; // no changed
            } else {
                final String constraintName = noNamePKName;
                final DfPrimaryKeyDiff primaryKeyDiff = DfPrimaryKeyDiff.createChanged(constraintName);
                processPrimaryKeyColumnDiff(tableDiff, nextTable, previousTable, primaryKeyDiff, constraintName);
            }
        }
        final String constraintName = nextName != null ? nextName : noNamePKName;
        if (isSame(nextName, previousName)) {
            final DfPrimaryKeyDiff primaryKeyDiff = DfPrimaryKeyDiff.createChanged(constraintName);
            processPrimaryKeyColumnDiff(tableDiff, nextTable, previousTable, primaryKeyDiff, constraintName);
        } else if (hasSameStructurePrimaryKey(nextTable, previousTable)) {
            return; // treated as no changed because only a name-change means nothing for developers
            //final DfPrimaryKeyDiff primaryKeyDiff = DfPrimaryKeyDiff.createChanged(constraintName);
            //final DfNextPreviousDiff nameDiff = createNextPreviousDiff(nextName, previousName);
            //primaryKeyDiff.setNameDiff(nameDiff);
            //tableDiff.addPrimaryKeyDiff(primaryKeyDiff);
        } else {
            if (nextName == null) { // deleted
                tableDiff.addPrimaryKeyDiff(DfPrimaryKeyDiff.createDeleted(previousName));
                return;
            } else if (previousName == null) { // added
                tableDiff.addPrimaryKeyDiff(DfPrimaryKeyDiff.createAdded(nextName));
                return;
            } else { // both are not null and different structure
                final DfPrimaryKeyDiff primaryKeyDiff = DfPrimaryKeyDiff.createChanged(constraintName);
                final DfNextPreviousDiff nameDiff = createNextPreviousDiff(nextName, previousName);
                primaryKeyDiff.setNameDiff(nameDiff);
                processPrimaryKeyColumnDiff(tableDiff, nextTable, previousTable, primaryKeyDiff, constraintName);
            }
        }
    }

    protected boolean hasSameStructurePrimaryKey(Table nextTable, Table previousTable) {
        final String nextCommaString = nextTable.getPrimaryKeyNameCommaString();
        final String previousCommaString = previousTable.getPrimaryKeyNameCommaString();
        return Srl.equalsPlain(nextCommaString, previousCommaString);
    }

    protected void processPrimaryKeyColumnDiff(DfTableDiff tableDiff, Table nextTable, Table previousTable,
            DfPrimaryKeyDiff primaryKeyDiff, String constraintName) {
        final String nextColumn = nextTable.getPrimaryKeyNameCommaString();
        final String previousColumn = previousTable.getPrimaryKeyNameCommaString();
        if (!isSame(nextColumn, previousColumn)) {
            final DfNextPreviousDiff columnDiff = createNextPreviousDiff(nextColumn, previousColumn);
            primaryKeyDiff.setColumnDiff(columnDiff);
        }
        if (primaryKeyDiff.hasDiff()) { // changed
            tableDiff.addPrimaryKeyDiff(primaryKeyDiff);
        }
    }

    // ===================================================================================
    //                                                                  ForeignKey Process
    //                                                                  ==================
    protected void processForeignKey(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        processConstraintKey(nextTable, previousTable, new ForeignKeyDiffer(tableDiff));
    }

    protected class ForeignKeyDiffer extends BasicConstraintKeyDiffer<ForeignKey, DfForeignKeyDiff> {

        public ForeignKeyDiffer(DfTableDiff tableDiff) {
            super(tableDiff);
        }

        public String constraintName(ForeignKey key) {
            return key.getName();
        }

        public List<ForeignKey> keyList(Table table) {
            return DfCollectionUtil.newArrayList(table.getForeignKeys());
        }

        public String column(ForeignKey key) {
            return key.getLocalColumnNameCommaString();
        }

        @Override
        public boolean isSameStructure(ForeignKey next, ForeignKey previous) {
            if (isSame(column(next), column(previous))) {
                if (isSame(next.getForeignTable().getName(), previous.getForeignTable().getName())) {
                    return true;
                }
            }
            return false;
        }

        public void diff(DfForeignKeyDiff diff, ForeignKey nextKey, ForeignKey previousKey) {
            // foreignTable
            if (nextKey != null && previousKey != null) {
                final String nextFKTable = nextKey.getForeignTableName();
                final String previousFKTable = previousKey.getForeignTableName();
                if (!isSame(nextFKTable, previousFKTable)) {
                    final DfNextPreviousDiff fkTableDiff = createNextPreviousDiff(nextFKTable, previousFKTable);
                    diff.setForeignTableDiff(fkTableDiff);
                }
            }
            if (diff.hasDiff()) {
                _tableDiff.addForeignKeyDiff(diff);
            }
        }

        public DfForeignKeyDiff createAddedDiff(String constraintName) {
            return DfForeignKeyDiff.createAdded(constraintName);
        }

        public DfForeignKeyDiff createChangedDiff(String constraintName) {
            return DfForeignKeyDiff.createChanged(constraintName);
        }

        public DfForeignKeyDiff createDeletedDiff(String constraintName) {
            return DfForeignKeyDiff.createDeleted(constraintName);
        }
    }

    // ===================================================================================
    //                                                                   UniqueKey Process
    //                                                                   =================
    protected void processUniqueKey(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        processConstraintKey(nextTable, previousTable, new UniqueKeyDiffer(tableDiff));
    }

    protected class UniqueKeyDiffer extends BasicConstraintKeyDiffer<Unique, DfUniqueKeyDiff> {

        public UniqueKeyDiffer(DfTableDiff tableDiff) {
            super(tableDiff);
        }

        public String constraintName(Unique key) {
            return key.getName();
        }

        public List<Unique> keyList(Table table) {
            return table.getUniqueList();
        }

        public String column(Unique key) {
            return buildCommaString(key.getIndexColumnMap().values());
        }

        public void diff(DfUniqueKeyDiff diff, Unique nextKey, Unique previousKey) {
            if (diff.hasDiff()) {
                _tableDiff.addUniqueKeyDiff(diff);
            }
        }

        public DfUniqueKeyDiff createAddedDiff(String constraintName) {
            return DfUniqueKeyDiff.createAdded(constraintName);
        }

        public DfUniqueKeyDiff createChangedDiff(String constraintName) {
            return DfUniqueKeyDiff.createChanged(constraintName);
        }

        public DfUniqueKeyDiff createDeletedDiff(String constraintName) {
            return DfUniqueKeyDiff.createDeleted(constraintName);
        }
    }

    // ===================================================================================
    //                                                                       Index Process
    //                                                                       =============
    protected void processIndex(final DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        processConstraintKey(nextTable, previousTable, new IndexDiffer(tableDiff));
    }

    protected class IndexDiffer extends BasicConstraintKeyDiffer<Index, DfIndexDiff> {

        public IndexDiffer(DfTableDiff tableDiff) {
            super(tableDiff);
        }

        public String constraintName(Index key) {
            return key.getName();
        }

        public List<Index> keyList(Table table) {
            return table.getIndexList();
        }

        public String column(Index key) {
            return buildCommaString(key.getIndexColumnMap().values());
        }

        public void diff(DfIndexDiff diff, Index nextKey, Index previousKey) {
            if (diff.hasDiff()) {
                _tableDiff.addIndexDiff(diff);
            }
        }

        public DfIndexDiff createAddedDiff(String constraintName) {
            return DfIndexDiff.createAdded(constraintName);
        }

        public DfIndexDiff createChangedDiff(String constraintName) {
            return DfIndexDiff.createChanged(constraintName);
        }

        public DfIndexDiff createDeletedDiff(String constraintName) {
            return DfIndexDiff.createDeleted(constraintName);
        }
    }

    // ===================================================================================
    //                                                                  Constraint Process
    //                                                                  ==================
    protected <KEY, DIFF extends DfConstraintDiff> void processConstraintKey(Table nextTable, Table previousTable,
            ConstraintKeyDiffer<KEY, DIFF> differ) { // for except PK
        final List<KEY> keyList = differ.keyList(nextTable);
        final Set<String> sameStructureNextSet = DfCollectionUtil.newHashSet();
        final Map<String, KEY> nextPreviousMap = DfCollectionUtil.newLinkedHashMap();
        final Map<String, KEY> previousNextMap = DfCollectionUtil.newLinkedHashMap();
        nextLoop: for (KEY nextKey : keyList) {
            final String nextName = differ.constraintName(nextKey);
            if (nextName == null) {
                continue;
            }
            for (KEY previousKey : differ.keyList(previousTable)) {
                final String previousName = differ.constraintName(previousKey);
                if (differ.isSameConstraintName(nextName, previousName)) { // found
                    nextPreviousMap.put(nextName, previousKey);
                    previousNextMap.put(previousName, nextKey);
                    continue nextLoop;
                }
            }
        }
        nextLoop: for (KEY nextKey : keyList) {
            final String nextName = differ.constraintName(nextKey);
            if (nextName == null || nextPreviousMap.containsKey(nextName)) {
                continue;
            }
            for (KEY previousKey : differ.keyList(previousTable)) {
                final String previousName = differ.constraintName(previousKey);
                if (previousNextMap.containsKey(previousName)) {
                    continue;
                }
                if (differ.isSameStructure(nextKey, previousKey)) { // found
                    nextPreviousMap.put(nextName, previousKey);
                    previousNextMap.put(previousName, nextKey);
                    sameStructureNextSet.add(nextName);
                    continue nextLoop;
                }
            }
        }
        final Set<Entry<String, KEY>> entrySet = nextPreviousMap.entrySet();
        for (Entry<String, KEY> entry : entrySet) {
            final String nextName = entry.getKey();
            if (sameStructureNextSet.contains(nextName)) {
                // treated as no changed because only a name-change means nothing for developers
                continue;
            }
            final KEY previousKey = entry.getValue();
            final String previousName = differ.constraintName(previousKey);
            final KEY nextKey = previousNextMap.get(previousName);
            processChangedConstraintKeyDiff(nextKey, previousKey, nextName, previousName, differ);
        }

        processAddedConstraintKey(nextTable, differ, nextPreviousMap);
        processDeletedConstraintKey(previousTable, differ, previousNextMap);
    }

    protected <KEY, DIFF extends DfConstraintDiff> void processChangedConstraintKeyDiff(KEY nextKey, KEY previousKey,
            String nextName, String previousName, ConstraintKeyDiffer<KEY, DIFF> differ) {
        if (differ.isSameConstraintName(nextName, previousName)) { // same name, different structure
            final String nextColumn = differ.column(nextKey);
            final String previousColumn = differ.column(previousKey);
            DfNextPreviousDiff columnDiff = null;
            if (!isSame(nextColumn, previousColumn)) {
                columnDiff = createNextPreviousDiff(nextColumn, previousColumn);
            }
            final DIFF diff = differ.createChangedDiff(nextName);
            diff.setColumnDiff(columnDiff);
            differ.diff(diff, nextKey, previousKey);
        } else { // different name, same structure (*no way because of skipped)
            final DfNextPreviousDiff nameDiff = createNextPreviousDiff(nextName, previousName);
            final DIFF diff = differ.createChangedDiff(nextName);
            diff.setNameDiff(nameDiff);
            differ.diff(diff, nextKey, previousKey);
        }
    }

    protected <KEY, DIFF extends DfConstraintDiff> void processAddedConstraintKey(Table nextTable,
            ConstraintKeyDiffer<KEY, DIFF> differ, Map<String, KEY> nextPreviousMap) {
        final List<KEY> keyList = differ.keyList(nextTable);
        for (KEY nextKey : keyList) {
            final String nextName = differ.constraintName(nextKey);
            if (nextPreviousMap.containsKey(nextName)) {
                continue;
            }
            // added
            final DIFF diff = differ.createAddedDiff(nextName);
            differ.diff(diff, nextKey, null);
        }
    }

    protected <KEY, DIFF extends DfConstraintDiff> void processDeletedConstraintKey(Table previousTable,
            ConstraintKeyDiffer<KEY, DIFF> differ, Map<String, KEY> previousNextMap) { // for except PK
        final List<KEY> keyList = differ.keyList(previousTable);
        for (KEY previousKey : keyList) {
            final String previousName = differ.constraintName(previousKey);
            if (previousNextMap.containsKey(previousName)) {
                continue;
            }
            // deleted
            final DIFF diff = differ.createDeletedDiff(previousName);
            differ.diff(diff, null, previousKey);
        }
    }

    protected static interface ConstraintKeyDiffer<KEY, DIFF extends DfConstraintDiff> {
        List<KEY> keyList(Table table);

        String constraintName(KEY key);

        String column(KEY key);

        boolean isSameConstraintName(String next, String previous);

        boolean isSameStructure(KEY next, KEY previous);

        void diff(DIFF diff, KEY nextKey, KEY previousKey);

        DIFF createAddedDiff(String constraintName);

        DIFF createChangedDiff(String constraintName);

        DIFF createDeletedDiff(String constraintName);
    }

    protected abstract class BasicConstraintKeyDiffer<KEY, DIFF extends DfConstraintDiff> implements
            ConstraintKeyDiffer<KEY, DIFF> {

        protected DfTableDiff _tableDiff;

        public BasicConstraintKeyDiffer(DfTableDiff tableDiff) {
            _tableDiff = tableDiff;
        }

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

        public boolean isSameConstraintName(String next, String previous) {
            return isSame(next, previous);
        }

        public boolean isSameStructure(KEY next, KEY previous) {
            return isSame(column(next), column(previous));
        }

        protected String extractConstraintName(KEY nextKey, KEY previousKey) {
            // either should be not null
            return nextKey != null ? constraintName(nextKey) : constraintName(previousKey);
        }
    }

    // *unused after all
    // because it skips constraints that are both different name and same structure
    // (there is problem that Derby switches an other structure as same-name)
    //protected boolean isAutoGeneratedConstraintName(String... names) { // for all constraints
    //    if (names == null || names.length == 0) {
    //        return false;
    //    }
    //    if (isDatabaseOracle()) {
    //        if (Srl.hasPrefixAllIgnoreCase("SYS_", names)) {
    //            return true;
    //        }
    //    }
    //    if (isDatabaseDB2() || isDatabaseDerby()) {
    //        if (Srl.hasPrefixAllIgnoreCase("SQL", names)) {
    //            return true;
    //        }
    //    }
    //    if (isDatabaseSQLServer()) {
    //        if (Srl.hasPrefixAllIgnoreCase("PK__", names)) {
    //            return true;
    //        }
    //    }
    //    if (isDatabaseH2()) {
    //        if (Srl.hasPrefixAllIgnoreCase("CONSTRAINT_", names)) {
    //            return true;
    //        }
    //    }
    //    return false;
    //}

    // ===================================================================================
    //                                                                         Find Object
    //                                                                         ===========
    protected Table findNextTable(Table table) {
        return _nextDb.getTable(table.getName());
    }

    protected Table findPreviousTable(Table table) {
        return _previousDb.getTable(table.getName());
    }

    // ===================================================================================
    //                                                                            Diff Map
    //                                                                            ========
    public Map<String, Object> createSchemaDiffMap() {
        final Map<String, Object> schemaDiffMap = DfCollectionUtil.newLinkedHashMap();
        schemaDiffMap.put(DIFF_DATE_KEY, DfTypeUtil.toString(_diffDate, DIFF_DATE_PATTERN));
        schemaDiffMap.put(TABLE_COUNT_KEY, _tableCountDiff.createNextPreviousDiffMap());

        final List<NestDiffSetupper> nestDiffList = _nestDiffList;
        for (NestDiffSetupper setupper : nestDiffList) {
            final List<? extends DfNestDiff> diffAllList = setupper.provide();
            if (!diffAllList.isEmpty()) {
                final Map<String, Map<String, Object>> diffMap = DfCollectionUtil.newLinkedHashMap();
                schemaDiffMap.put(setupper.propertyName(), diffMap);
                for (DfNestDiff nestDiff : diffAllList) {
                    if (nestDiff.hasDiff()) {
                        diffMap.put(nestDiff.getKeyName(), nestDiff.createDiffMap());
                    }
                }
            }
        }
        return schemaDiffMap;
    }

    public void acceptSchemaDiffMap(Map<String, Object> schemaDiffMap) {
        final Set<Entry<String, Object>> entrySet = schemaDiffMap.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            if (DIFF_DATE_KEY.equals(key)) {
                _diffDate = DfTypeUtil.toDate(value, DIFF_DATE_PATTERN);
                assertDiffDateExists(key, _diffDate, schemaDiffMap);
            } else if (COMMENT_KEY.equals(key)) {
                _comment = (String) value; // nullable
            } else if (TABLE_COUNT_KEY.equals(key)) {
                _tableCountDiff = restoreNextPreviousDiff(schemaDiffMap, key);
                assertTableCountExists(key, _tableCountDiff, schemaDiffMap);
            } else {
                final List<NestDiffSetupper> nestDiffList = _nestDiffList;
                for (NestDiffSetupper setupper : nestDiffList) {
                    if (setupper.propertyName().equals(key)) {
                        restoreNestDiff(schemaDiffMap, setupper);
                    }
                }
            }
        }
    }

    protected void assertDiffDateExists(String key, Date diffDate, Map<String, Object> schemaDiffMap) {
        if (diffDate == null) { // basically no way
            String msg = "The diff-date of diff-map is required:";
            msg = msg + " key=" + key + " schemaDiffMap=" + schemaDiffMap;
            throw new IllegalStateException(msg);
        }
    }

    protected void assertTableCountExists(String key, DfNextPreviousDiff nextPreviousDiff,
            Map<String, Object> schemaDiffMap) {
        if (nextPreviousDiff == null) { // basically no way
            String msg = "The table count of diff-map is required:";
            msg = msg + " key=" + key + " schemaDiffMap=" + schemaDiffMap;
            throw new IllegalStateException(msg);
        }
    }

    protected void assertNextTableCountExists(String key, String nextTableCount, Map<String, Object> schemaDiffMap) {
        if (nextTableCount == null) { // basically no way
            String msg = "The next table count of diff-map is required:";
            msg = msg + " key=" + key + " schemaDiffMap=" + schemaDiffMap;
            throw new IllegalStateException(msg);
        }
    }

    protected void assertPreviousTableCountExists(String key, String previousTableCount,
            Map<String, Object> schemaDiffMap) {
        if (previousTableCount == null) { // basically no way
            String msg = "The previous table count of diff-map is required:";
            msg = msg + " key=" + key + " schemaDiffMap=" + schemaDiffMap;
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                              Status
    //                                                                              ======
    public boolean hasDiff() {
        final List<NestDiffSetupper> nestDiffList = _nestDiffList;
        for (NestDiffSetupper setupper : nestDiffList) {
            final List<? extends DfNestDiff> diffAllList = setupper.provide();
            for (DfNestDiff nestDiff : diffAllList) {
                if (nestDiff.hasDiff()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canReadNext() {
        return !isFirstTime() && !isLoadingFailure();
    }

    public boolean isFirstTime() {
        return _firstTime;
    }

    public boolean isLoadingFailure() {
        return _loadingFailure;
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    public void checkColumnDefOrder() {
        _checkColumnDefOrder = true;
    }

    public void suppressUnifiedSchema() {
        _suppressUnifiedSchema = true;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    public String getDiffDate() {
        return DfTypeUtil.toString(_diffDate, DIFF_DATE_PATTERN);
    }

    public boolean hasComment() {
        return Srl.is_NotNull_and_NotTrimmedEmpty(_comment);
    }

    public String getComment() {
        return _comment;
    }

    public DfNextPreviousDiff getTableCount() {
        return _tableCountDiff;
    }

    // -----------------------------------------------------
    //                                            Table Diff
    //                                            ----------
    public List<DfTableDiff> getTableDiffAllList() {
        return _tableDiffAllList;
    }

    public List<DfTableDiff> getAddedTableDiffList() {
        return _addedTableDiffList;
    }

    public List<DfTableDiff> getChangedTableDiffList() {
        return _changedTableDiffList;
    }

    public List<DfTableDiff> getDeletedTableDiffList() {
        return _deletedTableDiffList;
    }

    public void addTableDiff(DfTableDiff tableDiff) {
        _tableDiffAllList.add(tableDiff);
        if (tableDiff.isAdded()) {
            _addedTableDiffList.add(tableDiff);
        } else if (tableDiff.isChanged()) {
            _changedTableDiffList.add(tableDiff);
        } else if (tableDiff.isDeleted()) {
            _deletedTableDiffList.add(tableDiff);
        } else {
            String msg = "Unknown diff-type of table: ";
            msg = msg + " diffType=" + tableDiff.getDiffType();
            msg = msg + " tableDiff=" + tableDiff;
            throw new IllegalStateException(msg);
        }
    }

    // -----------------------------------------------------
    //                                             Meta Info
    //                                             ---------
    public void setLatest(boolean latest) {
        _latest = latest;
    }

    public boolean isLatest() { // called by the template 'diffmodel.vm'
        return _latest;
    }
}
