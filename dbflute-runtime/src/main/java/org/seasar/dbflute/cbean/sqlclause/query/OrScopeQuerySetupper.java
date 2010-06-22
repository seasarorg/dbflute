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
package org.seasar.dbflute.cbean.sqlclause.query;

import java.util.List;

import org.seasar.dbflute.cbean.sqlclause.where.StringQueryClause;
import org.seasar.dbflute.cbean.sqlclause.where.QueryClause;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class OrScopeQuerySetupper {

    public static final String AND_PART_MARK = "$$df:AndPart$$";

    public void setupOrScopeQuery(List<OrScopeQueryClauseGroup> clauseGroupList, List<QueryClause> realList,
            boolean line) {
        if (clauseGroupList == null || clauseGroupList.isEmpty()) {
            return;
        }
        final String or = " or ";
        final String and = " and ";
        final String lnIndentOr = line ? ln() + "   " : "";
        final String lnIndentAnd = ""; // no line separator either way
        final String andPartMark = getOrScopeQueryAndPartMark();
        final StringBuilder sb = new StringBuilder();
        boolean exists = false;
        int validCount = 0;
        int groupListIndex = 0;
        for (OrScopeQueryClauseGroup clauseGroup : clauseGroupList) {
            final List<QueryClause> orClauseList = clauseGroup.getOrClauseList();
            if (orClauseList == null || orClauseList.isEmpty()) {
                continue; // not increment index
            }
            int listIndex = 0;
            boolean inAndPart = false;
            for (QueryClause clauseElement : orClauseList) {
                String orClause = clauseElement.toString();
                final boolean currentAndPart = orClause.startsWith(andPartMark);
                final boolean beginAndPart;
                final boolean secondAndPart;
                if (currentAndPart) {
                    if (inAndPart) { // already begin
                        beginAndPart = false;
                        secondAndPart = true;
                    } else {
                        beginAndPart = true;
                        secondAndPart = false;
                        inAndPart = true;
                    }
                    orClause = orClause.substring(andPartMark.length());
                } else {
                    if (inAndPart) {
                        sb.append(")");
                        inAndPart = false;
                    }
                    beginAndPart = false;
                    secondAndPart = false;
                }
                if (groupListIndex == 0) { // first list
                    if (listIndex == 0) {
                        sb.append("(");
                    } else {
                        sb.append(secondAndPart ? lnIndentAnd : lnIndentOr);
                        sb.append(secondAndPart ? and : or);
                    }
                } else { // second or more list
                    if (listIndex == 0) {
                        // always 'or' here
                        sb.append(lnIndentOr);
                        sb.append(or);
                        sb.append("(");
                    } else {
                        sb.append(secondAndPart ? lnIndentAnd : lnIndentOr);
                        sb.append(secondAndPart ? and : or);
                    }
                }
                sb.append(beginAndPart ? "(" : "");
                sb.append(orClause);
                ++validCount;
                if (!exists) {
                    exists = true;
                }
                ++listIndex;
            }
            if (inAndPart) {
                sb.append(")");
                inAndPart = false;
            }
            if (groupListIndex > 0) { // second or more list
                sb.append(")");
            }
            ++groupListIndex;
        }
        if (exists) {
            sb.append(line && validCount > 1 ? ln() + "       " : "").append(")");
            realList.add(new StringQueryClause(sb.toString()));
        }
    }

    protected String getOrScopeQueryAndPartMark() {
        return AND_PART_MARK;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replace(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
