/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.helper.jdbc.schemainitializer;

import javax.sql.DataSource;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.generatedsql.DfGeneratedSqlExecutor;
import org.seasar.dbflute.helper.jdbc.generatedsql.DfGeneratedSqlExecutorImpl;
import org.seasar.dbflute.helper.jdbc.generatedsql.DfGeneratedSqlExecutor.DfGeneratedSqlExecuteOption;

/**
 * @author jflute
 */
public class DfSchemaInitializerMySQL implements DfSchemaInitializer {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;

    // ===================================================================================
    //                                                                   Initialize Schema
    //                                                                   =================
    public void initializeSchema() {
        truncateTableIfPossible();
        dropForeignKey();
        dropTable();
    }

    // -----------------------------------------------------
    //                                              Truncate
    //                                              --------
    protected void truncateTableIfPossible() {
        final DfGeneratedSqlExecutor generatedSqlExecutor = createGeneratedSqlExecutor();
        final DfGeneratedSqlExecuteOption option = new DfGeneratedSqlExecuteOption();
        option.setErrorContinue(true);
        generatedSqlExecutor.execute(getTruncateTableSql(), "sql", option);
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
            sb.append(" where table_type = 'BASE TABLE';");
        }
        sb.append(lineSeparator);
        return sb.toString();
    }

    // -----------------------------------------------------
    //                                           Foreign Key
    //                                           -----------
    protected void dropForeignKey() {
        final DfGeneratedSqlExecutor generatedSqlExecutor = createGeneratedSqlExecutor();
        generatedSqlExecutor.execute(getDropForeignKeySql(), "sql");
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
            sb.append(" where constraint_type='foreign key';");
        }
        sb.append(lineSeparator);
        return sb.toString();
    }

    // -----------------------------------------------------
    //                                                 Table
    //                                                 -----
    protected void dropTable() {
        final DfGeneratedSqlExecutor generatedSqlExecutor = createGeneratedSqlExecutor();
        generatedSqlExecutor.execute(getDropTableSql(), "sql");
    }

    protected String getDropTableSql() {
        final String lineSeparator = System.getProperty("line.separator");
        final StringBuilder sb = new StringBuilder();
        sb.append("select case when table_type = 'VIEW'").append(lineSeparator);
        sb.append("            then concat('DROP VIEW IF EXISTS ', table_name, ';')").append(lineSeparator);
        sb.append("            else concat('DROP TABLE IF EXISTS ', table_name, ';')").append(lineSeparator);
        sb.append("       end as \"sql\"").append(lineSeparator);
        sb.append("  from information_schema.tables").append(lineSeparator);
        final String schema = DfBuildProperties.getInstance().getBasicProperties().getDatabaseSchema();
        if (schema != null) {
            sb.append(" where table_schema = '" + schema + "' and table_type in ('BASE TABLE', 'VIEW');");
        } else {
            sb.append(" where table_type in ('BASE TABLE', 'VIEW');");
        }
        sb.append(lineSeparator);
        return sb.toString();
    }

    // -----------------------------------------------------
    //                                         Common Helper
    //                                         -------------
    protected DfGeneratedSqlExecutor createGeneratedSqlExecutor() {
        final DfGeneratedSqlExecutorImpl generatedSqlExecutorImpl = new DfGeneratedSqlExecutorImpl();
        generatedSqlExecutorImpl.setDataSource(_dataSource);
        return generatedSqlExecutorImpl;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setDataSource(DataSource dataSource) {
        _dataSource = dataSource;
    }
}