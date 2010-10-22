/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
import java.util.Set;

import javax.sql.DataSource;

import org.seasar.dbflute.bhv.UpdateOption;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnUpdateModifiedOnlyCommand extends TnUpdateAutoDynamicCommand {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnUpdateModifiedOnlyCommand(DataSource dataSource, StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    // ===================================================================================
    //                                                                 No.1 Point Override
    //                                                                 ===================
    @Override
    protected TnPropertyType[] createUpdatePropertyTypes(TnBeanMetaData bmd, Object bean, String[] propertyNames,
            UpdateOption<ConditionBean> option) {
        final Set<?> modifiedPropertyNames = getBeanMetaData().getModifiedPropertyNames(bean);
        final List<TnPropertyType> types = new ArrayList<TnPropertyType>();
        final String timestampProp = bmd.getTimestampPropertyName();
        final String versionNoProp = bmd.getVersionNoPropertyName();
        for (int i = 0; i < propertyNames.length; ++i) {
            final TnPropertyType pt = bmd.getPropertyType(propertyNames[i]);
            if (pt.isPrimaryKey()) {
                continue;
            }
            final String propertyName = pt.getPropertyName();
            final String columnDbName = pt.getColumnDbName();
            if (isOptimisticLockProperty(timestampProp, versionNoProp, propertyName)
                    || isModifiedProperty(modifiedPropertyNames, propertyName)
                    || isStatementProperty(option, columnDbName)) {
                types.add(pt);
            }
        }
        final TnPropertyType[] propertyTypes = types.toArray(new TnPropertyType[types.size()]);
        return propertyTypes;
    }

    protected boolean isOptimisticLockProperty(String timestampProp, String versionNoProp, String propertyName) {
        return propertyName.equalsIgnoreCase(timestampProp) || propertyName.equalsIgnoreCase(versionNoProp);
    }

    protected boolean isStatementProperty(UpdateOption<ConditionBean> option, String columnDbName) {
        return option != null && option.hasStatement(columnDbName);
    }

    protected boolean isModifiedProperty(Set<?> modifiedPropertyNames, String propertyName) {
        return modifiedPropertyNames.contains(propertyName);
    }
}
