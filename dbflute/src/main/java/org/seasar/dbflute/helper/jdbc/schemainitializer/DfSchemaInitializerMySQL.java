package org.seasar.dbflute.helper.jdbc.schemainitializer;

import javax.sql.DataSource;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.generatedsql.DfGeneratedSqlExecutor;
import org.seasar.dbflute.helper.jdbc.generatedsql.DfGeneratedSqlExecutorImpl;
import org.seasar.dbflute.helper.jdbc.generatedsql.DfGeneratedSqlExecutor.DfGeneratedSqlExecuteOption;

public class DfSchemaInitializerMySQL implements DfSchemaInitializer {

    protected DataSource _dataSource;

    public void setDataSource(DataSource dataSource) {
        _dataSource = dataSource;
    }

    public void initializeSchema() {
        truncateTableIfPossible();
        dropForeignKey();
        dropTable();
    }

    protected void truncateTableIfPossible() {
        final DfGeneratedSqlExecutor generatedSqlExecutor = createGeneratedSqlExecutor();
        final DfGeneratedSqlExecuteOption option = new DfGeneratedSqlExecuteOption();
        option.setErrorContinue(true);
        generatedSqlExecutor.execute(getTruncateTableSql(), "sql", option);
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

    protected String getTruncateTableSql() {
        final String lineSeparator = System.getProperty("line.separator");
        final StringBuilder sb = new StringBuilder();
        sb.append("select concat('TRUNCATE TABLE ', table_name, ';') as \"sql\"");
        sb.append(lineSeparator);
        sb.append("  from information_schema.tables");
        sb.append(lineSeparator);
        final String schema = DfBuildProperties.getInstance().getBasicProperties().getDatabaseSchema();
        if (schema != null) {
            sb.append(" where table_schema = '" + schema + "' and table_type = 'BASE TABLE';");
        } else {
            // TODO: Should it use user value?
            sb.append(" where table_type = 'BASE TABLE';");
        }
        sb.append(lineSeparator);
        return sb.toString();
    }

    protected String getDropForeignKeySql() {
        final String lineSeparator = System.getProperty("line.separator");
        final StringBuilder sb = new StringBuilder();
        sb.append("select concat('ALTER TABLE ', table_name, ' DROP FOREIGN KEY ', constraint_name, ';') as \"sql\"");
        sb.append(lineSeparator);
        sb.append("  from information_schema.table_constraints");
        sb.append(lineSeparator);
        final String schema = DfBuildProperties.getInstance().getBasicProperties().getDatabaseSchema();
        if (schema != null) {
            sb.append(" where table_schema = '" + schema + "' and constraint_type='foreign key';");
        } else {
            // TODO: Should it use user value?
            sb.append(" where constraint_type='foreign key';");
        }
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
        final String schema = DfBuildProperties.getInstance().getBasicProperties().getDatabaseSchema();
        if (schema != null) {
            sb.append(" where table_schema = '" + schema + "' and table_type = 'BASE TABLE';");
        } else {
            // TODO: Should it use user value?
            sb.append(" where table_type = 'BASE TABLE';");
        }
        sb.append(lineSeparator);
        return sb.toString();
    }
}