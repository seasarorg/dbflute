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

/**
 * @author jflute
 */
public class DfSchemaInitializerOracle implements DfSchemaInitializer {

    public void setDataSource(DataSource dataSource) {
        throw new UnsupportedOperationException("Please use " + DfSchemaInitializerJdbc.class);
    }

    public void initializeSchema() {
        throw new UnsupportedOperationException("Please use " + DfSchemaInitializerJdbc.class);
    }

    // * * * * * * * * * * * * * * * * * * * * * * * *  
    // USER_TABLES.STATUSが「Oracle 10g」からなので、
    // VERSION依存してしまわないようにした。
    // * * * * * * * * * * * * * * * * * * * * * * * * 
    //    protected DataSource _dataSource;
    //
    //    public void setDataSource(DataSource dataSource) {
    //        _dataSource = dataSource;
    //    }
    //
    //    public void initializeSchema() {
    //        dropForeignKey();
    //        dropTable();
    //    }
    //
    //    protected void dropForeignKey() {
    //        final DfGeneratedSqlExecutor generatedSqlExecutor = createGeneratedSqlExecutor();
    //        generatedSqlExecutor.execute(getDropForeignKeySql(), "sql");
    //    }
    //
    //    protected void dropTable() {
    //        final DfGeneratedSqlExecutor generatedSqlExecutor = createGeneratedSqlExecutor();
    //        generatedSqlExecutor.execute(getDropTableSql(), "sql");
    //    }
    //
    //    protected DfGeneratedSqlExecutor createGeneratedSqlExecutor() {
    //        final DfGeneratedSqlExecutorImpl generatedSqlExecutorImpl = new DfGeneratedSqlExecutorImpl();
    //        generatedSqlExecutorImpl.setDataSource(_dataSource);
    //        return generatedSqlExecutorImpl;
    //    }
    //
    //    protected String getDropForeignKeySql() {
    //        final String lineSeparator = System.getProperty("line.separator");
    //        final StringBuilder sb = new StringBuilder();
    //        sb.append("select 'ALTER TABLE ' || TABLE_NAME || ' DROP FOREIGN KEY ' || CONSTRAINT_NAME as sql");
    //        sb.append(lineSeparator);
    //        sb.append("  from USER_CONSTRAINTS");
    //        sb.append(lineSeparator);
    //        sb.append(" where CONSTRAINT_TYPE = 'R'");
    //        sb.append(lineSeparator);
    //        return sb.toString();
    //    }
    //
    //    protected String getDropTableSql() {
    //        final String lineSeparator = System.getProperty("line.separator");
    //        final StringBuilder sb = new StringBuilder();
    //        sb.append("select 'DROP TABLE ' || TABLE_NAME as sql");
    //        sb.append(lineSeparator);
    //        sb.append("  from USER_TABLES");
    //        sb.append(lineSeparator);
    //        sb.append(" where STATUS = 'VALID'");
    //        sb.append(lineSeparator);
    //        return sb.toString();
    //    }
}