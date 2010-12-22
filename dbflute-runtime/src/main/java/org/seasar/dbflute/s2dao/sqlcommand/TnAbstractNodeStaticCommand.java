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

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.resource.ResourceContext;
import org.seasar.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.dbflute.twowaysql.node.Node;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public abstract class TnAbstractNodeStaticCommand extends TnAbstractBasicSqlCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Node _rootNode;
    protected String[] _argNames = new String[0]; // as default
    protected Class<?>[] _argTypes = new Class[0]; // as default

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractNodeStaticCommand(DataSource dataSource, StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    // ===================================================================================
    //                                                                        SQL Handling
    //                                                                        ============
    public void acceptSql(String sql) {
        this._rootNode = createSqlAnalyzer(sql).analyze();
    }

    protected SqlAnalyzer createSqlAnalyzer(String sql) {
        return ResourceContext.createSqlAnalyzer(sql, isBlockNullParameter());
    }

    protected boolean isBlockNullParameter() { // Extension Point!
        return false;
    }

    // ===================================================================================
    //                                                                   Argument Handling
    //                                                                   =================
    public CommandContext apply(Object[] args) { // It is necessary to be public!
        final CommandContext ctx = createCommandContext(args);
        _rootNode.accept(ctx);
        return ctx;
    }

    protected CommandContext createCommandContext(Object[] args) {
        return createCommandContextCreator().createCommandContext(args);
    }

    protected CommandContextCreator createCommandContextCreator() {
        return new CommandContextCreator(_argNames, _argTypes);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String[] getArgNames() {
        return _argNames;
    }

    public void setArgNames(String[] argNames) {
        this._argNames = argNames;
    }

    public Class<?>[] getArgTypes() {
        return _argTypes;
    }

    public void setArgTypes(Class<?>[] argTypes) {
        this._argTypes = argTypes;
    }
}
