package org.seasar.dbflute.helper.jdbc.schemainitializer;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.jdbc.generatedsql.DfGeneratedSqlExecutor;
import org.seasar.dbflute.helper.jdbc.generatedsql.DfGeneratedSqlExecutorImpl;

/**
 * The schema initializer for Sybase.
 * 
 * @author jflute
 */
public class DfSchemaInitializerSybase implements DfSchemaInitializer {

    protected DataSource _dataSource;

    public void setDataSource(DataSource dataSource) {
        _dataSource = dataSource;
    }

    public void initializeSchema() {
        dropForeignKey();
        dropTable();
    }

    protected void dropForeignKey() {
        final DfGeneratedSqlExecutor generatedSqlExecutor = createGeneratedSqlExecutor();
        generatedSqlExecutor.execute(getDropForeignKeySql(), "sql");
    }

    protected void dropTable() {
        final DfGeneratedSqlExecutor generatedSqlExecutor = createGeneratedSqlExecutor();
        generatedSqlExecutor.execute(getDropTableSql(), "sql");
    }

    protected DfGeneratedSqlExecutor createGeneratedSqlExecutor() {
        final DfGeneratedSqlExecutorImpl generatedSqlExecutorImpl = new DfGeneratedSqlExecutorImpl();
        generatedSqlExecutorImpl.setDataSource(_dataSource);
        return generatedSqlExecutorImpl;
    }

    protected String getDropForeignKeySql() {
        final String lineSeparator = System.getProperty("line.separator");
        final StringBuilder sb = new StringBuilder();
        sb.append("select 'ALTER TABLE ' + parentObj.name + ' DROP CONSTRAINT ' + baseObj.name as sql");
        sb.append(lineSeparator);
        sb.append("  from sysobjects baseObj");
        sb.append(lineSeparator);
        sb.append("    left outer join sysobjects parentObj on baseObj.parent_obj = parentObj.id");
        sb.append(lineSeparator);
        sb.append(" where baseObj.type = 'F'");
        sb.append(lineSeparator);
        return sb.toString();
    }

    protected String getDropTableSql() {
        final String lineSeparator = System.getProperty("line.separator");
        final StringBuilder sb = new StringBuilder();
        sb.append("select 'DROP TABLE ' + name as sql");
        sb.append(lineSeparator);
        sb.append("  from sysobjects");
        sb.append(lineSeparator);
        sb.append(" where type = 'U'");
        sb.append(lineSeparator);
        return sb.toString();
    }
}