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

import java.util.List;

import org.seasar.dbflute.bhv.InsertOption;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.dbflute.s2dao.sqlhandler.TnBatchInsertHandler;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnBatchInsertAutoDynamicCommand extends TnInsertAutoDynamicCommand {

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    protected Object doExecute(Object bean, TnPropertyType[] propertyTypes, String sql,
            InsertOption<ConditionBean> option) {
        final List<?> beanList;
        if (bean instanceof List<?>) {
            beanList = (List<?>) bean;
        } else {
            String msg = "The argument 'args[0]' should be list: " + bean;
            throw new IllegalArgumentException(msg);
        }
        final TnBatchInsertHandler handler = createBatchInsertHandler(propertyTypes, sql, option);
        handler.setExceptionMessageSqlArgs(new Object[] { beanList });
        return handler.executeBatch(beanList);
    }

    // ===================================================================================
    //                                                                       Insert Column
    //                                                                       =============
    @Override
    protected boolean isExceptProperty(Object bean, TnPropertyType pt, String timestampPropertyName,
            String versionNoPropertyName) {
        return false; // all columns are target
    }

    // ===================================================================================
    //                                                                             Handler
    //                                                                             =======
    protected TnBatchInsertHandler createBatchInsertHandler(TnPropertyType[] boundPropTypes, String sql,
            InsertOption<ConditionBean> option) {
        final TnBatchInsertHandler handler = new TnBatchInsertHandler(getDataSource(), getStatementFactory(),
                _beanMetaData, boundPropTypes);
        handler.setSql(sql);
        handler.setInsertOption(option);
        return handler;
    }
}
