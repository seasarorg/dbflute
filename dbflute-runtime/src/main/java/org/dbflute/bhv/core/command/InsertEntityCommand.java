package org.dbflute.bhv.core.command;

import java.util.List;

import org.dbflute.bhv.core.SqlExecution;
import org.dbflute.bhv.core.SqlExecutionCreator;
import org.dbflute.dbmeta.DBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.dbflute.s2dao.sqlcommand.TnInsertAutoDynamicCommand;


/**
 * @author DBFlute(AutoGenerator)
 */
public class InsertEntityCommand extends AbstractEntityCommand {

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "insert";
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    public SqlExecutionCreator createSqlExecutionCreator() {
        assertStatus("createSqlExecutionCreator");
        return new SqlExecutionCreator() {
            public SqlExecution createSqlExecution() {
                final TnBeanMetaData bmd = createBeanMetaData();
                return createInsertEntitySqlExecution(bmd);
            }
        };
    }

    protected SqlExecution createInsertEntitySqlExecution(TnBeanMetaData bmd) {
        final SqlExecution nonPrimaryKeySqlExecution = createNonPrimaryInsertSqlExecution(bmd);
        if (nonPrimaryKeySqlExecution != null) {
            return nonPrimaryKeySqlExecution;
        }
        final String[] propertyNames = getPersistentPropertyNames(bmd);
        return createInsertAutoDynamicCommand(bmd, propertyNames);
    }

    protected TnInsertAutoDynamicCommand createInsertAutoDynamicCommand(TnBeanMetaData bmd, String[] propertyNames) {
        final TnInsertAutoDynamicCommand cmd = new TnInsertAutoDynamicCommand();
        cmd.setBeanMetaData(bmd);
        cmd.setDataSource(_dataSource);
        cmd.setPropertyNames(propertyNames);
        cmd.setStatementFactory(_statementFactory);
        return cmd;
    }

    /**
     * @param bmd The meta data of bean. (NotNull)
     * @return Whether the method is target. (For example if it has primary key, returns false.)
     */
    protected SqlExecution createNonPrimaryInsertSqlExecution(TnBeanMetaData bmd) {
        final DBMeta dbmeta = findDBMeta();
        if (dbmeta.hasPrimaryKey()) {
            return null;
        }
        final List<ColumnInfo> columnInfoList = dbmeta.getColumnInfoList();
        final StringBuilder columnDefSb = new StringBuilder();
        for (org.dbflute.dbmeta.info.ColumnInfo columnInfo : columnInfoList) {
            columnDefSb.append(", ").append(columnInfo.getColumnDbName());
        }
        columnDefSb.delete(0, ", ".length()).insert(0, "(").append(")");
        final StringBuilder columnValuesSb = new StringBuilder();
        for (org.dbflute.dbmeta.info.ColumnInfo columnInfo : columnInfoList) {
            columnValuesSb.append(", /*dto.").append(columnInfo.getPropertyName()).append("*/null");
        }
        columnValuesSb.delete(0, ", ".length()).insert(0, "(").append(")");
        final String sql = "insert into " + dbmeta.getTableSqlName() + columnDefSb + " values" + columnValuesSb;
        return createUpdateDynamicCommand(new String[]{"dto"}, new Class<?>[]{ _entityType }, sql);
    }
}
