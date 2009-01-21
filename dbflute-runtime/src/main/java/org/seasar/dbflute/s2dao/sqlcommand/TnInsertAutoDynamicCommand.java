/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.dbflute.bhv.core.SqlExecution;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.identity.TnIdentifierGenerator;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.dbflute.s2dao.sqlhandler.TnInsertAutoHandler;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnInsertAutoDynamicCommand implements TnSqlCommand, SqlExecution {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DataSource dataSource;
    protected StatementFactory statementFactory;
    protected TnBeanMetaData beanMetaData;
    protected String[] propertyNames;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnInsertAutoDynamicCommand() {
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(Object[] args) {
        final Object bean = args[0];
        final TnBeanMetaData bmd = getBeanMetaData();
        final TnPropertyType[] propertyTypes = createInsertPropertyTypes(bmd, bean, getPropertyNames());
        final String sql = createInsertSql(bmd, propertyTypes);
        final TnInsertAutoHandler handler = new TnInsertAutoHandler(getDataSource(), getStatementFactory(),
                bmd, propertyTypes);
        handler.setSql(sql);
        handler.setLoggingMessageSqlArgs(args);
        final int rows = handler.execute(args);
        return new Integer(rows);
    }

    protected String createInsertSql(TnBeanMetaData bmd, TnPropertyType[] propertyTypes) {
        StringBuffer buf = new StringBuffer(100);
        buf.append("insert into ");
        buf.append(bmd.getTableName());
        buf.append(" (");
        for (int i = 0; i < propertyTypes.length; ++i) {
            TnPropertyType pt = propertyTypes[i];
            final String columnName = pt.getColumnName();
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(columnName);
        }
        buf.append(") values (");
        for (int i = 0; i < propertyTypes.length; ++i) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append("?");
        }
        buf.append(")");
        return buf.toString();
    }

    protected TnPropertyType[] createInsertPropertyTypes(TnBeanMetaData bmd, Object bean, String[] propertyNames) {
        if (0 == propertyNames.length) {
            String msg = "The property name was not found in the bean: " + bean;
            throw new IllegalStateException(msg);
        }
        List<TnPropertyType> types = new ArrayList<TnPropertyType>();
        final String timestampPropertyName = bmd.getTimestampPropertyName();
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();

        for (int i = 0; i < propertyNames.length; ++i) {
            TnPropertyType pt = bmd.getPropertyType(propertyNames[i]);
            if (pt.isPrimaryKey()) {
                final TnIdentifierGenerator generator = bmd.getIdentifierGenerator(pt.getPropertyName());
                if (!generator.isSelfGenerate()) {
                    continue;
                }
            } else {
                if (pt.getPropertyDesc().getValue(bean) == null) {
                    final String propertyName = pt.getPropertyName();
                    if (!propertyName.equalsIgnoreCase(timestampPropertyName)
                            && !propertyName.equalsIgnoreCase(versionNoPropertyName)) {
                        continue;
                    }
                }
            }
            types.add(pt);
        }
        if (types.isEmpty()) {
            String msg = "The target property type was not found in the bean: " + bean;
            throw new IllegalStateException(msg);
        }
        TnPropertyType[] propertyTypes = (TnPropertyType[]) types.toArray(new TnPropertyType[types.size()]);
        return propertyTypes;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    protected DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected StatementFactory getStatementFactory() {
        return statementFactory;
    }

    public void setStatementFactory(StatementFactory statementFactory) {
        this.statementFactory = statementFactory;
    }

    protected TnBeanMetaData getBeanMetaData() {
        return beanMetaData;
    }

    public void setBeanMetaData(TnBeanMetaData beanMetaData) {
        this.beanMetaData = beanMetaData;
    }

    protected String[] getPropertyNames() {
        return propertyNames;
    }

    public void setPropertyNames(String[] propertyNames) {
        this.propertyNames = propertyNames;
    }
}
