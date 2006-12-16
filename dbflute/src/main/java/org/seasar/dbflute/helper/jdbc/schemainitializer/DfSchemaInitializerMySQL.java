package org.seasar.dbflute.helper.jdbc.schemainitializer;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.jdbc.generatedsql.DfGeneratedSqlExecutor;
import org.seasar.dbflute.helper.jdbc.generatedsql.DfGeneratedSqlExecutorImpl;

public class DfSchemaInitializerMySQL implements DfSchemaInitializer {

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
        sb.append("select concat('ALTER TABLE ', table_name, ' DROP FOREIGN KEY ', constraint_name, ';') as \"sql\"");
        sb.append(lineSeparator);
        sb.append("  from information_schema.table_constraints");
        sb.append(lineSeparator);
        sb.append(" where constraint_type='foreign key';");
        sb.append(lineSeparator);
        return sb.toString();
    }

    protected String getDropTableSql() {
        final String lineSeparator = System.getProperty("line.separator");
        final StringBuilder sb = new StringBuilder();
        sb.append("select concat('DROP TABLE IF EXISTS ', table_name, ';') as \"sql\"");
        sb.append(lineSeparator);
        sb.append("  from information_schema.tables");
        sb.append(lineSeparator);
        sb.append(" where table_type = 'BASE TABLE';");
        sb.append(lineSeparator);
        return sb.toString();
    }
}