/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.seasar.dbflute.cbean.cipher.ColumnFunctionCipher;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.resource.DBFluteSystem;
import org.seasar.dbflute.resource.ResourceContext;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public abstract class TnAbstractEntityDynamicCommand extends TnAbstractBasicSqlCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected TnBeanMetaData _beanMetaData;
    protected DBMeta _targetDBMeta;
    protected String[] _propertyNames;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractEntityDynamicCommand(DataSource dataSource, StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    // ===================================================================================
    //                                                                       Cipher Helper
    //                                                                       =============
    protected String encrypt(String tableDbName, String columnDbName, String valueExp) {
        final ColumnFunctionCipher cipher = ResourceContext.findColumnFunctionCipher(tableDbName, columnDbName);
        return cipher != null ? cipher.encrypt(valueExp) : valueExp;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DBFluteSystem.getBasicLn();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setBeanMetaData(TnBeanMetaData beanMetaData) {
        this._beanMetaData = beanMetaData;
    }

    public void setTargetDBMeta(DBMeta targetDBMeta) {
        this._targetDBMeta = targetDBMeta;
    }

    public void setPropertyNames(String[] propertyNames) {
        this._propertyNames = propertyNames;
    }
}
