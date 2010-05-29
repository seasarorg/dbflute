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
package org.seasar.dbflute.twowaysql.node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.twowaysql.exception.EndCommentNotFoundException;
import org.seasar.dbflute.twowaysql.exception.ForCommentIllegalParameterBeanSpecificationException;
import org.seasar.dbflute.twowaysql.exception.ForCommentParameterNotListException;
import org.seasar.dbflute.twowaysql.node.NodeUtil.IllegalParameterBeanHandler;
import org.seasar.dbflute.twowaysql.node.ValueAndTypeSetupper.CommentType;
import org.seasar.dbflute.twowaysql.pmbean.ParameterBean;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;
import org.seasar.dbflute.util.Srl.ScopeInfo;

/**
 * The node for FOR (loop). <br />
 * FOR comment is evaluated before analyzing nodes,
 * so it is not related to container node.
 * @author jflute
 */
public class ForNode extends ContainerNode {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String PREFIX = "FOR ";
    public static final String ELEMENT = "#element";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Object _pmb;
    protected String _dynamicSql;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _expression;
    protected List<String> _nameList;
    protected ElseNode _elseNode;
    protected String _specifiedSql;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ForNode(String expression, String specifiedSql) {
        this._expression = expression;
        this._nameList = Srl.splitList(expression, ".");
        this._specifiedSql = specifiedSql;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    @Override
    public void accept(CommandContext ctx) {
        final String firstName = _nameList.get(0);
        assertFirstName(ctx, firstName);
        final Object value = ctx.getArg(firstName);
        final Class<?> clazz = ctx.getArgType(firstName);
        final ValueAndType valueAndType = new ValueAndType();
        valueAndType.setTargetValue(value);
        valueAndType.setTargetType(clazz);
        setupValueAndType(valueAndType);
        final Object targetValue = valueAndType.getTargetValue();
        if (targetValue == null) {
            return;
        }
        assertParameterList(targetValue);
        final List<?> parameterList = (List<?>) targetValue;
        final int loopSize = parameterList.size();
        final int childSize = getChildSize();
        final LoopInfo loopInfo = new LoopInfo();
        loopInfo.setParameterList(parameterList);
        loopInfo.setLoopSize(loopSize);
        loopInfo.setLikeSearchOption(valueAndType.getLikeSearchOption());
        for (int loopIndex = 0; loopIndex < loopSize; loopIndex++) {
            loopInfo.setLoopIndex(loopIndex);
            for (int childIndex = 0; childIndex < childSize; childIndex++) {
                final Node child = getChild(childIndex);
                if (child instanceof LoopAbstractNode) {
                    ((LoopAbstractNode) child).accept(ctx, loopSize, loopIndex);
                } else if (child instanceof BindVariableNode) {
                    ((BindVariableNode) child).accept(ctx, element, option);
                } else if (child instanceof EmbeddedVariableNode) {
                    ((EmbeddedVariableNode) child).accept(ctx, element, option);
                } else {
                    child.accept(ctx);
                }
            }
        }
        if (loopSize > 0) {
            ctx.setEnabled(true);
        }
    }

    public static class LoopInfo {
        protected List<?> _parameterList;
        protected int _loopSize;
        protected LikeSearchOption _likeSearchOption;
        protected int _loopIndex;

        public List<?> getParameterList() {
            return _parameterList;
        }

        public void setParameterList(List<?> parameterList) {
            this._parameterList = parameterList;
        }

        public int getLoopSize() {
            return _loopSize;
        }

        public void setLoopSize(int loopSize) {
            this._loopSize = loopSize;
        }

        public LikeSearchOption getLikeSearchOption() {
            return _likeSearchOption;
        }

        public void setLikeSearchOption(LikeSearchOption likeSearchOption) {
            this._likeSearchOption = likeSearchOption;
        }

        public int getLoopIndex() {
            return _loopIndex;
        }

        public void setLoopIndex(int loopIndex) {
            this._loopIndex = loopIndex;
        }

        public Object getCurrentParameter() {
            return _parameterList.get(_loopIndex);
        }
    }

    protected void assertFirstName(final CommandContext ctx, String firstName) {
        NodeUtil.assertParameterBeanName(firstName, new ParameterFinder() {
            public Object find(String name) {
                return ctx.getArg(name);
            }
        }, new IllegalParameterBeanHandler() {
            public void handle(ParameterBean pmb) {
                throwForCommentIllegalParameterBeanSpecificationException(pmb);
            }
        });
    }

    protected void throwForCommentIllegalParameterBeanSpecificationException(ParameterBean pmb) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The FOR comment had the illegal parameter-bean specification!");
        br.addItem("Advice");
        br.addElement("Please confirm your FOR comment.");
        br.addElement("For example:");
        br.addElement("  (x) - /*FOR pmb,memberId*/");
        br.addElement("  (x) - /*FOR p mb,memberId*/");
        br.addElement("  (x) - /*FOR pmb:memberId*/");
        br.addElement("  (x) - /*FOR pmb,memberId*/");
        br.addElement("  (o) - /*FOR pmb,memberId*/");
        br.addItem("FOR Comment Expression");
        br.addElement(_expression);
        // *debug to this exception does not need contents of the parameter-bean
        //  (and for security to application data)
        //br.addItem("ParameterBean");
        //br.addElement(pmb);
        br.addItem("Specified SQL");
        br.addElement(_specifiedSql);
        final String msg = br.buildExceptionMessage();
        throw new ForCommentIllegalParameterBeanSpecificationException(msg);
    }

    protected void setupValueAndType(ValueAndType valueAndType) {
        final CommentType type = CommentType.FORCOMMENT;
        final ValueAndTypeSetupper setuper = new ValueAndTypeSetupper(_nameList, _expression, _specifiedSql, type);
        setuper.setupValueAndType(valueAndType);
    }

    protected void assertParameterList(Object targetValue) {
        if (!List.class.isInstance(targetValue)) {
            final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
            br.addNotice("The parameter for FOR coment was not list.");
            br.addItem("FOR Comment");
            br.addElement("/*FOR " + _expression + "*/");
            br.addItem("Parameter");
            br.addElement(targetValue.getClass());
            br.addElement(targetValue);
            br.addItem("Specified SQL");
            br.addElement(_specifiedSql);
            String msg = br.buildExceptionMessage();
            throw new ForCommentParameterNotListException(msg);
        }
    }

    // ===================================================================================
    //                                                                       Loop Variable
    //                                                                       =============
    public enum LoopVariableType {
        FIRST("first", new LoopVariableNodeFactory() {
            public LoopAbstractNode create(String expression, String specifiedSql) {
                return new LoopFirstNode(expression, specifiedSql);
            }
        }), NEXT("next", new LoopVariableNodeFactory() {
            public LoopAbstractNode create(String expression, String specifiedSql) {
                return new LoopNextNode(expression, specifiedSql);
            }
        }), LAST("last", new LoopVariableNodeFactory() {
            public LoopAbstractNode create(String expression, String specifiedSql) {
                return new LoopLastNode(expression, specifiedSql);
            }
        });
        private static final Map<String, LoopVariableType> _codeValueMap = new HashMap<String, LoopVariableType>();
        static {
            for (LoopVariableType value : values()) {
                _codeValueMap.put(value.code().toLowerCase(), value);
            }
        }
        private String _code;
        private LoopVariableNodeFactory _factory;

        private LoopVariableType(String code, LoopVariableNodeFactory factory) {
            _code = code;
            _factory = factory;
        }

        public String code() {
            return _code;
        }

        public static LoopVariableType codeOf(Object code) {
            if (code == null) {
                return null;
            }
            if (code instanceof LoopVariableType) {
                return (LoopVariableType) code;
            }
            return _codeValueMap.get(code.toString().toLowerCase());
        }

        public LoopAbstractNode createNode(String expression, String specifiedSql) {
            return _factory.create(expression, specifiedSql);
        }
    }

    public interface LoopVariableNodeFactory {
        LoopAbstractNode create(String expression, String specifiedSql);
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ForNode(Object pmb, String dynamicSql) {
        _pmb = pmb;
        _dynamicSql = dynamicSql;
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    /**
     * @param comment The comment string, for example 'FOR pmb.memberNameList'. (NotNull)
     * @return Is the comment one of FOR comment elements?
     */
    public static final boolean isForCommentElement(String comment) {
        if (comment.startsWith("FOR")) {
            return true;
        }
        if (comment.startsWith("FIRST") || comment.startsWith("NEXT") || comment.startsWith("LAST")) {
            return true;
        }
        return false;
    }

    // ===================================================================================
    //                                                                             Resolve
    //                                                                             =======
    public String resolveDynamicForComment() {
        if (_pmb == null) {
            return _dynamicSql;
        }
        // *nested FOR comments are unsupported 
        final String beginMark = "/*FOR ";
        final String closeMark = "*/";
        final String endMark = "/*END FOR*/";
        String rear = _dynamicSql;
        final StringBuilder mainSqlSb = new StringBuilder();
        while (true) {
            final int beginIndex = rear.indexOf(beginMark);
            if (beginIndex < 0) {
                mainSqlSb.append(rear);
                break;
            }

            mainSqlSb.append(rear.substring(0, beginIndex));
            rear = rear.substring(beginIndex + beginMark.length());
            final int closeIndex = rear.indexOf(closeMark);
            if (closeIndex < 0) {
                mainSqlSb.append(rear);
                break;
            }
            final String expression = rear.substring(0, closeIndex);
            final int loopSize = extractLoopSize(_pmb, expression);

            final StringBuilder loopClauseSb = new StringBuilder();
            if (loopSize > 0) {
                // add IF comment which always returns true
                // to prevent BEGIN comment from removing all
                // and to adjust and/or prefix
                loopClauseSb.append("/*IF ").append(expression).append(".size() > 0*/");
            }

            rear = rear.substring(closeIndex + closeMark.length());
            final int endIndex = rear.indexOf(endMark);
            assertEndForComment(_pmb, _dynamicSql, expression, endIndex);
            final String content = rear.substring(0, endIndex);

            final String fromIndexStr = ".get(index)";
            final LoopVariableInfo loopVariableInfo = extractLoopVariableInfo(content);
            for (int i = 0; i < loopSize; i++) {
                String element = content;
                final String toIndexStr = ".get(" + i + ")";
                element = Srl.replaceScopeContent(element, fromIndexStr, toIndexStr, "/*", "*/");
                if (i == 0) { // first loop
                    element = Srl.replace(element, loopVariableInfo.getFirstMap());
                }
                // LAST replacement should be executed before NEXT
                if (i == (loopSize - 1)) { // last loop
                    element = Srl.replace(element, loopVariableInfo.getLastMap());
                }
                if (i > 0) { // next loop
                    element = Srl.replace(element, loopVariableInfo.getNextMap());
                }
                loopClauseSb.append(element);
            }
            if (loopSize > 0) {
                loopClauseSb.append("/*END*/"); // for IF comment
            }
            mainSqlSb.append(loopClauseSb); // reflect loop clause to main SQL
            rear = rear.substring(endIndex + endMark.length()); // to next
        }
        return mainSqlSb.toString();
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected int extractLoopSize(Object pmb, String expression) {
        final String dynamicSql = _specifiedSql;
        final List<String> nameList = Srl.splitList(expression, ".");
        final CommentType type = CommentType.FORCOMMENT;
        final ValueAndTypeSetupper setupper = new ValueAndTypeSetupper(nameList, expression, dynamicSql, type);
        final ValueAndType valueAndType = new ValueAndType();
        valueAndType.setTargetValue(pmb);
        valueAndType.setTargetType(pmb.getClass());
        setupper.setupValueAndType(valueAndType);
        final Object targetValue = valueAndType.getTargetValue();
        if (targetValue == null) {
            return 0;
        }
        if (!List.class.isInstance(targetValue)) {
            final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
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

    protected void assertEndForComment(Object pmb, String dynamicSql, String expression, int endIndex) {
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

    protected LoopVariableInfo extractLoopVariableInfo(String content) {
        final LoopVariableInfo info = new LoopVariableInfo();
        final List<ScopeInfo> scopeList = Srl.extractScopeList(content, "/*", "*/");
        for (int i = 0; i < scopeList.size(); i++) {
            final ScopeInfo scope = scopeList.get(i);
            if (scope.getContent().startsWith("FIRST")) {
                final KeyValueContainer keyValue = processKeyValue(content, scope, "FIRST");
                info.addFirst(keyValue.getKey(), keyValue.getValue());
                info.addNext(keyValue.getKey(), "");
            } else if (scope.getContent().startsWith("NEXT")) {
                final KeyValueContainer keyValue = processKeyValue(content, scope, "NEXT");
                info.addFirst(keyValue.getKey(), "");
                info.addNext(keyValue.getKey(), keyValue.getValue());
            } else if (scope.getContent().startsWith("LAST")) {
                final KeyValueContainer keyValue = processKeyValue(content, scope, "LAST");
                info.addFirst(keyValue.getKey(), "");
                info.addNext(keyValue.getKey(), ""); // LAST replacement should be executed before NEXT
                info.addLast(keyValue.getKey(), keyValue.getValue());
            }
        }
        return info;
    }

    protected KeyValueContainer processKeyValue(String content, ScopeInfo scope, String keyword) {
        String key = null;
        String value = null;
        final ScopeInfo next = scope.getNext();
        if (next != null && next.getContent().startsWith("END " + keyword)) {
            key = scope.substringScopeToNext();
            String prefix = "";
            final ScopeInfo prefixScope = Srl.extractScopeFirst(scope.getContent(), "'", "'");
            if (prefixScope != null) {
                prefix = prefixScope.getContent();
            }
            String suffix = "";
            final ScopeInfo suffixScope = Srl.extractScopeFirst(next.getContent(), "'", "'");
            if (suffixScope != null) {
                suffix = suffixScope.getContent();
            }
            final String interspace = scope.substringInterspaceToNext();
            value = prefix + interspace + suffix;
        }
        if (key == null) {
            key = scope.getScope();
        }
        if (value == null) {
            String replacement = "";
            final ScopeInfo replacementScope = Srl.extractScopeFirst(scope.getContent(), "'", "'");
            if (replacementScope != null) {
                replacement = replacementScope.getContent();
            }
            value = replacement;
        }
        final KeyValueContainer container = new KeyValueContainer();
        container.setKey(key);
        container.setValue(value);
        return container;
    }

    protected static class KeyValueContainer {
        protected String _key;
        protected String _value;

        public String getKey() {
            return _key;
        }

        public void setKey(String key) {
            this._key = key;
        }

        public String getValue() {
            return _value;
        }

        public void setValue(String value) {
            this._value = value;
        }
    }

    protected static class LoopVariableInfo {
        protected final Map<String, String> _firstMap = DfCollectionUtil.newHashMap();
        protected final Map<String, String> _nextMap = DfCollectionUtil.newHashMap();
        protected final Map<String, String> _lastMap = DfCollectionUtil.newHashMap();

        public Map<String, String> getFirstMap() {
            return _firstMap;
        }

        public void addFirst(String key, String value) {
            _firstMap.put(key, value);
        }

        public Map<String, String> getNextMap() {
            return _nextMap;
        }

        public void addNext(String key, String value) {
            _nextMap.put(key, value);
        }

        public Map<String, String> getLastMap() {
            return _lastMap;
        }

        public void addLast(String key, String value) {
            _lastMap.put(key, value);
        }
    }
}
