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
package org.seasar.dbflute.bhv.core.execution;

import java.util.Map;

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.sqlcommand.TnAbstractTwoWaySqlCommand;
import org.seasar.dbflute.twowaysql.node.Node;

/**
 * @author jflute
 */
public abstract class AbstractFixedSqlExecution extends TnAbstractTwoWaySqlCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Node _rootNode;
    protected final Map<String, Class<?>> _argNameTypeMap;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractFixedSqlExecution(DataSource dataSource, StatementFactory statementFactory, String twoWaySql,
            Map<String, Class<?>> argNameTypeMap) {
        super(dataSource, statementFactory);
        _rootNode = analyzeTwoWaySql(twoWaySql);
        _argNameTypeMap = argNameTypeMap;
    }

    // ===================================================================================
    //                                                                            Resource
    //                                                                            ========
    @Override
    protected Node getRootNode(Object[] args) {
        return _rootNode;
    }

    @Override
    protected Map<String, Class<?>> getArgNameTypeMap(Object[] args) {
        return _argNameTypeMap;
    }
}
