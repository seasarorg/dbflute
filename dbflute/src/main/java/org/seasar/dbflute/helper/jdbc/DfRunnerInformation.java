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
package org.seasar.dbflute.helper.jdbc;

/**
 * @author jflute
 */
public class DfRunnerInformation {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _driver;
    protected String _url;
    protected String _user;
    protected String _password;
    protected String _encoding;
    protected String _delimiter = ";";
    protected boolean _isErrorContinue;
    protected boolean _isAutoCommit;
    protected boolean _isRollbackOnly;

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getDriver() {
        return _driver;
    }

    public void setDriver(String driver) {
        this._driver = driver;
    }

    public String getUrl() {
        return _url;
    }

    public void setUrl(String url) {
        this._url = url;
    }

    public String getUser() {
        return _user;
    }

    public void setUser(String user) {
        this._user = user;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(String password) {
        this._password = password;
    }

    public String getEncoding() {
        return _encoding;
    }

    public void setEncoding(String encoding) {
        this._encoding = encoding;
    }

    public boolean isEncodingNull() {
        return (_encoding == null);
    }

    public String getDelimiter() {
        return _delimiter;
    }

    public void setDelimiter(String _delimiter) {
        this._delimiter = _delimiter;
    }

    public boolean isErrorContinue() {
        return _isErrorContinue;
    }

    public void setErrorContinue(boolean isErrorContinue) {
        this._isErrorContinue = isErrorContinue;
    }

    public boolean isAutoCommit() {
        return _isAutoCommit;
    }

    public void setAutoCommit(boolean isAutoCommit) {
        this._isAutoCommit = isAutoCommit;
    }

    public boolean isRollbackOnly() {
        return _isRollbackOnly;
    }

    public void setRollbackOnly(boolean isRollbackOnly) {
        this._isRollbackOnly = isRollbackOnly;
    }
}
