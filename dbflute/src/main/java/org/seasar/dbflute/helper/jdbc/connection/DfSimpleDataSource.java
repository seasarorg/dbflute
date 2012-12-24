/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.helper.jdbc.connection;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 */
public class DfSimpleDataSource implements DataSource {

    protected final DfConnectionProvider _dataSourceProvider;

    public DfSimpleDataSource(DfDataSourceHandler dataSourceProvider) {
        _dataSourceProvider = dataSourceProvider;
    }

    public Connection getConnection() throws SQLException {
        return _dataSourceProvider.getConnection();
    }

    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException("Use getConnection()");
    }

    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException("getLogWriter()");
    }

    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("getLoginTimeout()");
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException("setLoginTimeout()");
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException("setLoginTimeout()");
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("unwrap()");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("isWrapperFor()");
    }

    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":" + _dataSourceProvider;
    }
}
