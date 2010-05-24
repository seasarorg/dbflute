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

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.twowaysql.exception.EndCommentNotFoundException;
import org.seasar.dbflute.twowaysql.exception.ForCommentParameterNotListException;
import org.seasar.dbflute.twowaysql.node.ValueAndTypeSetupper.CommentType;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;
import org.seasar.dbflute.util.Srl.ScopeInfo;

/**
 * The node for FOR (loop). <br />
 * FOR comment is evaluated before analyzing nodes,
 * so it is not related to container node.
 * @author jflute
 */
public class ForNode {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Object _pmb;
    protected String _dynamicSql;

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
            final int loopSize = extractLoopSize(_pmb, _dynamicSql, expression);

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
    protected int extractLoopSize(Object pmb, String dynamicSql, String expression) {
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
