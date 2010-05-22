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
package org.seasar.dbflute.bhv.core.execution;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.dbflute.exception.EndCommentNotFoundException;
import org.seasar.dbflute.exception.ForCommentParameterNotListException;
import org.seasar.dbflute.exception.ForCommentParameterNullValueException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.beans.DfBeanDesc;
import org.seasar.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.dbflute.s2dao.sqlcommand.TnAbstractDynamicCommand;
import org.seasar.dbflute.s2dao.sqlhandler.TnBasicSelectHandler;
import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.twowaysql.node.ValueAndType;
import org.seasar.dbflute.twowaysql.node.ValueAndTypeSetupper;
import org.seasar.dbflute.twowaysql.node.ValueAndTypeSetupper.CommentType;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class OutsideSqlSelectExecution extends TnAbstractDynamicCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The handler of resultSet. */
    protected TnResultSetHandler resultSetHandler;

    /** Is it forced to enable the dynamic binding? */
    protected boolean _forcedDynamicBinding;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param dataSource Data source.
     * @param statementFactory The factory of statement.
     * @param resultSetHandler The handler of resultSet.
     */
    public OutsideSqlSelectExecution(DataSource dataSource, StatementFactory statementFactory,
            TnResultSetHandler resultSetHandler) {
        super(dataSource, statementFactory);
        this.resultSetHandler = resultSetHandler;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    /**
     * @param args The array of argument. (NotNull, The first element should be the instance of Pmb)
     * @return The object of execution result. (Nullable)
     */
    public Object execute(Object[] args) {
        final OutsideSqlContext outsideSqlContext = OutsideSqlContext.getOutsideSqlContextOnThread();
        if (isDynamicBinding(outsideSqlContext)) { // basically to use FOR comment
            return executeOutsideSqlAsDynamic(args, outsideSqlContext);
        } else { // main case
            return executeOutsideSqlAsStatic(args, outsideSqlContext);
        }
    }

    protected boolean isDynamicBinding(OutsideSqlContext outsideSqlContext) {
        return _forcedDynamicBinding || outsideSqlContext.isDynamicBinding();
    }

    // -----------------------------------------------------
    //                                               Dynamic
    //                                               -------
    /**
     * Execute outside-SQL as Dynamic.
     * @param args The array of argument. (NotNull, The first element should be the instance of Pmb)
     * @param outsideSqlContext The context of outside-SQL. (NotNull)
     * @return Result. (Nullable)
     */
    protected Object executeOutsideSqlAsDynamic(Object[] args, OutsideSqlContext outsideSqlContext) {
        final Object firstArg = args[0];
        String dynamicSql = getSql();
        if (firstArg != null) {
            dynamicSql = resolveDynamicForComment(firstArg, dynamicSql);
            dynamicSql = resolveDynamicEmbedded(firstArg, dynamicSql);
        }

        final OutsideSqlSelectExecution outsideSqlCommand = createDynamicSqlFactory();
        outsideSqlCommand.setArgNames(getArgNames());
        outsideSqlCommand.setArgTypes(getArgTypes());
        outsideSqlCommand.setSql(dynamicSql);

        final CommandContext ctx = outsideSqlCommand.apply(args);
        final List<Object> bindVariableList = new ArrayList<Object>();
        final List<Class<?>> bindVariableTypeList = new ArrayList<Class<?>>();
        addBindVariableInfo(ctx, bindVariableList, bindVariableTypeList);
        final TnBasicSelectHandler selectHandler = createBasicSelectHandler(ctx.getSql(), this.resultSetHandler);
        final Object[] bindVariableArray = bindVariableList.toArray();
        selectHandler.setExceptionMessageSqlArgs(bindVariableArray);
        return selectHandler.execute(bindVariableArray, toClassArray(bindVariableTypeList));
    }

    protected String resolveDynamicForComment(Object firstArg, String dynamicSql) {
        if (firstArg == null) {
            return dynamicSql;
        }
        // *nested FOR comments are unsupported 
        final String beginMark = "/*FOR ";
        final String closeMark = "*/";
        final String endMark = "/*END FOR*/";
        final String andNext = "/*$$AndNext$$*/";
        final String orNext = "/*$$OrNext$$*/";
        String rear = dynamicSql;
        final StringBuilder sb = new StringBuilder();
        while (true) {
            final int beginIndex = rear.indexOf(beginMark);
            if (beginIndex < 0) {
                sb.append(rear);
                break;
            }

            sb.append(rear.substring(0, beginIndex));
            rear = rear.substring(beginIndex + beginMark.length());
            final int closeIndex = rear.indexOf(closeMark);
            if (closeIndex < 0) {
                sb.append(rear);
                break;
            }
            final String expression = rear.substring(0, closeIndex);
            final int loopSize = extractLoopSize(firstArg, dynamicSql, expression);

            rear = rear.substring(closeIndex + closeMark.length());
            final int endIndex = rear.indexOf(endMark);
            assertEndForComment(firstArg, dynamicSql, expression, endIndex);
            final String content = rear.substring(0, endIndex);
            for (int i = 0; i < loopSize; i++) {
                String element = content;
                element = Srl.replace(element, ".get(index)", ".get(" + i + ")");
                if (i > 0) {
                    // with rear space
                    element = Srl.replace(element, andNext, "and ");
                    element = Srl.replace(element, orNext, "or ");
                } else {
                    element = Srl.replace(element, andNext, "");
                    element = Srl.replace(element, orNext, "");
                }
                sb.append(element);
            }
            rear = rear.substring(endIndex + endMark.length()); // to next
        }
        return sb.toString();
    }

    protected int extractLoopSize(Object firstArg, String dynamicSql, String expression) {
        final List<String> nameList = Srl.splitList(expression, ".");
        final CommentType type = CommentType.FORCOMMENT;
        final ValueAndTypeSetupper setupper = new ValueAndTypeSetupper(nameList, expression, dynamicSql, type);
        final ValueAndType valueAndType = new ValueAndType();
        valueAndType.setTargetValue(firstArg);
        valueAndType.setTargetType(firstArg.getClass());
        setupper.setupValueAndType(valueAndType);
        final Object targetValue = valueAndType.getTargetValue();
        if (targetValue == null) {
            ExceptionMessageBuilder br = new ExceptionMessageBuilder();
            br.addNotice("The parameter for FOR coment was null value.");
            br.addItem("FOR Comment");
            br.addElement("/*FOR " + expression + "*/");
            br.addItem("Specified SQL");
            br.addElement(dynamicSql);
            String msg = br.buildExceptionMessage();
            throw new ForCommentParameterNullValueException(msg);
        }
        if (!List.class.isInstance(targetValue)) {
            ExceptionMessageBuilder br = new ExceptionMessageBuilder();
            br.addNotice("The parameter for FOR coment was not list.");
            br.addItem("FOR Comment");
            br.addElement("/*FOR " + expression + "*/");
            br.addItem("Parameter");
            br.addElement(targetValue.getClass());
            br.addElement(targetValue);
            br.addItem("Specified SQL");
            br.addElement(dynamicSql);
            String msg = br.buildExceptionMessage();
            throw new ForCommentParameterNotListException(msg);
        }
        final List<?> loopList = (List<?>) targetValue;
        return loopList.size();
    }

    protected void assertEndForComment(Object firstArg, String dynamicSql, String expression, int endIndex) {
        if (endIndex < 0) {
            ExceptionMessageBuilder br = new ExceptionMessageBuilder();
            br.addNotice("Not found the end comment for FOR coment.");
            br.addItem("Advice");
            br.addElement("FOR comment needs its END comment like this:");
            br.addElement("  (x) - /*FOR pmb.xxxList*/...");
            br.addElement("  (o) - /*FOR pmb.xxxList*/.../*END FOR*/");
            br.addItem("FOR Comment");
            br.addElement("/*FOR " + expression + "*/");
            br.addItem("Specified SQL");
            br.addElement(dynamicSql);
            String msg = br.buildExceptionMessage();
            throw new EndCommentNotFoundException(msg);
        }
    }

    protected String resolveDynamicEmbedded(Object firstArg, String dynamicSql) {
        if (firstArg == null) {
            return dynamicSql;
        }
        // *nested properties are unsupported 
        final DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(firstArg.getClass());
        final List<String> proppertyNameList = beanDesc.getProppertyNameList();
        for (String proppertyName : proppertyNameList) {
            final DfPropertyDesc propertyDesc = beanDesc.getPropertyDesc(proppertyName);
            final Class<?> propertyType = propertyDesc.getPropertyType();
            if (!propertyType.equals(String.class)) {
                continue;
            }
            final String outsideSqlPiece = (String) propertyDesc.getValue(firstArg);
            if (outsideSqlPiece == null) {
                continue;
            }
            final String embeddedComment = "/*$pmb." + propertyDesc.getPropertyName() + "*/";
            dynamicSql = replaceString(dynamicSql, embeddedComment, outsideSqlPiece);
        }
        return dynamicSql;
    }

    // -----------------------------------------------------
    //                                                Static
    //                                                ------
    /**
     * Execute outside-SQL as static.
     * @param args The array of argument. (NotNull, The first element should be the instance of Pmb)
     * @param outsideSqlContext The context of outside-SQL. (NotNull)
     * @return Result. (Nullable)
     */
    protected Object executeOutsideSqlAsStatic(Object[] args, OutsideSqlContext outsideSqlContext) {
        final CommandContext ctx = apply(args);
        final TnBasicSelectHandler selectHandler = createBasicSelectHandler(ctx.getSql(), this.resultSetHandler);
        final Object[] bindVariableArray = ctx.getBindVariables();
        selectHandler.setExceptionMessageSqlArgs(bindVariableArray);
        return selectHandler.execute(bindVariableArray, ctx.getBindVariableTypes());
    }

    // ===================================================================================
    //                                                                 Dynamic SQL Factory
    //                                                                 ===================
    protected OutsideSqlSelectExecution createDynamicSqlFactory() {
        return new OutsideSqlSelectExecution(getDataSource(), getStatementFactory(), resultSetHandler);
    }

    // ===================================================================================
    //                                                                      Select Handler
    //                                                                      ==============
    protected TnBasicSelectHandler createBasicSelectHandler(String realSql, TnResultSetHandler rsh) {
        return new TnBasicSelectHandler(getDataSource(), realSql, rsh, getStatementFactory());
    }

    // ===================================================================================
    //                                                                       Parser Option
    //                                                                       =============
    @Override
    protected boolean isBlockNullParameter() {
        return true; // Because the SQL is select.
    }

    // ===================================================================================
    //                                                                        Setup Helper
    //                                                                        ============
    protected Class<?>[] toClassArray(List<Class<?>> bindVariableTypeList) {
        final Class<?>[] bindVariableTypesArray = new Class<?>[bindVariableTypeList.size()];
        for (int i = 0; i < bindVariableTypeList.size(); i++) {
            final Class<?> bindVariableType = (Class<?>) bindVariableTypeList.get(i);
            bindVariableTypesArray[i] = bindVariableType;
        }
        return bindVariableTypesArray;
    }

    protected void addBindVariableInfo(CommandContext ctx, List<Object> bindVariableList,
            List<Class<?>> bindVariableTypeList) {
        final Object[] bindVariables = ctx.getBindVariables();
        addBindVariableList(bindVariableList, bindVariables);
        final Class<?>[] bindVariableTypes = ctx.getBindVariableTypes();
        addBindVariableTypeList(bindVariableTypeList, bindVariableTypes);
    }

    protected void addBindVariableList(List<Object> bindVariableList, Object[] bindVariables) {
        for (int i = 0; i < bindVariables.length; i++) {
            bindVariableList.add(bindVariables[i]);
        }
    }

    protected void addBindVariableTypeList(List<Class<?>> bindVariableTypeList, Class<?>[] bindVariableTypes) {
        for (int i = 0; i < bindVariableTypes.length; i++) {
            bindVariableTypeList.add(bindVariableTypes[i]);
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected final String replaceString(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    protected String getLineSeparator() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public boolean isForcedDynamicBinding() {
        return _forcedDynamicBinding;
    }

    public void setForcedDynamicBinding(boolean forcedDynamicBinding) {
        this._forcedDynamicBinding = forcedDynamicBinding;
    }
}
