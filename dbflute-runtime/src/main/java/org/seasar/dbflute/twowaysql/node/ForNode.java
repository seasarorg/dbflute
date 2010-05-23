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

/**
 * The node for FOR (loop). <br />
 * FOR comment is evaluated before analyzing nodes,
 * so it is not related to container node.
 * @author jflute
 */
public class ForNode {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    public static final String FOR_EXP = "FOR";
    public static final String FOR_MARK = "/*" + FOR_EXP + " ";
    public static final String CLOSE_MARK = "*/";
    public static final String END_COMMENT = "/*END FOR*/";

    // -----------------------------------------------------
    //                                         Loop Variable
    //                                         -------------
    public static final String FIRST_EXP = "FIRST";
    public static final String NEXT_EXP = "NEXT";
    public static final String LAST_EXP = "LAST";

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
        if (comment.startsWith(FOR_MARK)) {
            return true;
        }
        if (comment.startsWith(FIRST_EXP) || comment.startsWith(NEXT_EXP) || comment.startsWith(LAST_EXP)) {
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
        final String startMark = ForNode.FOR_MARK;
        final String closeMark = ForNode.CLOSE_MARK;
        final String endMark = ForNode.END_COMMENT;
        String rear = _dynamicSql;
        final StringBuilder sb = new StringBuilder();
        while (true) {
            final int startIndex = rear.indexOf(startMark);
            if (startIndex < 0) {
                sb.append(rear);
                break;
            }

            sb.append(rear.substring(0, startIndex));
            rear = rear.substring(startIndex + startMark.length());
            final int closeIndex = rear.indexOf(closeMark);
            if (closeIndex < 0) {
                sb.append(rear);
                break;
            }
            final String expression = rear.substring(0, closeIndex);
            final int loopSize = extractLoopSize(_pmb, _dynamicSql, expression);

            if (loopSize > 0) {
                // add IF comment which always returns true
                // to prevent BEGIN comment from removing all
                // and to adjust and/or prefix
                sb.append("/*IF ").append(expression).append(".size() > 0*/");
            }

            rear = rear.substring(closeIndex + closeMark.length());
            final int endIndex = rear.indexOf(endMark);
            assertEndForComment(_pmb, _dynamicSql, expression, endIndex);
            final String content = rear.substring(0, endIndex);

            final LoopVariableInfo loopVariableInfo = extractLoopVariableInfo(content);
            for (int i = 0; i < loopSize; i++) {
                String element = content;
                element = Srl.replace(element, ".get(index)", ".get(" + i + ")");
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
                sb.append(element);
            }
            if (loopSize > 0) {
                sb.append("/*END*/"); // for IF comment
            }
            rear = rear.substring(endIndex + endMark.length()); // to next
        }
        return sb.toString();
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
            br.addElement(ForNode.FOR_MARK + expression + ForNode.END_COMMENT);
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
        final List<String> replacementList = Srl.extractAllScope(content, "/*", "*/");
        for (String replacement : replacementList) {
            final String key = "/*" + replacement + "*/";
            if (replacement.startsWith(FIRST_EXP)) {
                info.addFirst(key, extractVariableValue(replacement));
                info.addNext(key, "");
            } else if (replacement.startsWith(NEXT_EXP)) {
                info.addFirst(key, "");
                info.addNext(key, extractVariableValue(replacement));
            } else if (replacement.startsWith(LAST_EXP)) {
                info.addFirst(key, "");
                info.addNext(key, ""); // LAST replacement should be executed before NEXT
                info.addLast(key, extractVariableValue(replacement));
            }
        }
        return info;
    }

    protected String extractVariableValue(String replacement) {
        return Srl.extractFirstScope(replacement, "'", "'");
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
