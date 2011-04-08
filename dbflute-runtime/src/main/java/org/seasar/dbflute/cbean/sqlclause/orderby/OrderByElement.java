/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.cbean.sqlclause.orderby;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.cbean.ManualOrderBean;
import org.seasar.dbflute.cbean.ManualOrderBean.CaseWhenElement;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 */
public class OrderByElement implements Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The value of alias name. */
    protected String _aliasName;

    /** The value of column name. */
    protected String _columnName;

    /** The value of ascDesc. */
    protected String _ascDesc = "asc";

    /** The set-upper of order-by nulls. */
    protected transient OrderByClause.OrderByNullsSetupper _orderByNullsSetupper;

    /** Is nulls ordered first? */
    protected boolean _nullsFirst;

    /** The bean of manual order. */
    protected transient ManualOrderBean _manualOrderBean;

    // ===================================================================================
    //                                                                        Manipulation
    //                                                                        ============
    public void setupAsc() {
        _ascDesc = "asc";
    }

    public void setupDesc() {
        _ascDesc = "desc";
    }

    public void reverse() {
        if (_ascDesc == null) {
            String msg = "The attribute[ascDesc] should not be null.";
            throw new IllegalStateException(msg);
        }
        if (_ascDesc.equals("asc")) {
            _ascDesc = "desc";
        } else if (_ascDesc.equals("desc")) {
            _ascDesc = "asc";
        } else {
            String msg = "The attribute[ascDesc] should be asc or desc: but ascDesc=" + _ascDesc;
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                 Order-By Expression
    //                                                                 ===================
    public boolean isAsc() {
        if (_ascDesc == null) {
            String msg = "The attribute[ascDesc] should not be null.";
            throw new IllegalStateException(msg);
        }
        if (_ascDesc.equals("asc")) {
            return true;
        } else if (_ascDesc.equals("desc")) {
            return false;
        } else {
            String msg = "The attribute[ascDesc] should be asc or desc: but ascDesc=" + _ascDesc;
            throw new IllegalStateException(msg);
        }
    }

    public String getColumnFullName() {
        final StringBuilder sb = new StringBuilder();
        if (_aliasName != null) {
            sb.append(_aliasName).append(".");
        }
        if (_columnName == null) {
            String msg = "The attribute[columnName] should not be null.";
            throw new IllegalStateException(msg);
        }
        sb.append(_columnName);
        return sb.toString();
    }

    public String getElementClause() {
        if (_ascDesc == null) {
            String msg = "The attribute[ascDesc] should not be null.";
            throw new IllegalStateException(msg);
        }
        final StringBuilder sb = new StringBuilder();
        if (_manualOrderBean != null && _manualOrderBean.hasManualOrder()) {
            setupManualOrderClause(sb, getColumnFullName());
            return sb.toString();
        } else {
            sb.append(getColumnFullName()).append(" ").append(_ascDesc);
            if (_orderByNullsSetupper != null) {
                return _orderByNullsSetupper.setup(getColumnFullName(), sb.toString(), _nullsFirst);
            } else {
                return sb.toString();
            }
        }
    }

    public String getElementClause(Map<String, String> selectClauseRealColumnAliasMap) {
        if (selectClauseRealColumnAliasMap == null) {
            String msg = "The argument[selectClauseRealColumnAliasMap] should not be null.";
            throw new IllegalArgumentException(msg);
        }
        if (_ascDesc == null) {
            String msg = "The attribute[ascDesc] should not be null.";
            throw new IllegalStateException(msg);
        }
        final String columnAlias = selectClauseRealColumnAliasMap.get(getColumnFullName());
        if (columnAlias == null || columnAlias.trim().length() == 0) {
            throwOrderByColumnNotFoundException(getColumnFullName(), selectClauseRealColumnAliasMap);
        }
        final StringBuilder sb = new StringBuilder();
        if (_manualOrderBean != null && _manualOrderBean.hasManualOrder()) {
            setupManualOrderClause(sb, columnAlias);
            return sb.toString();
        } else {
            sb.append(columnAlias).append(" ").append(_ascDesc);
            if (_orderByNullsSetupper != null) {
                return _orderByNullsSetupper.setup(columnAlias, sb.toString(), _nullsFirst);
            } else {
                return sb.toString();
            }
        }
    }

    protected void setupManualOrderClause(StringBuilder sb, String columnAlias) {
        final List<CaseWhenElement> caseWhenList = _manualOrderBean.getCaseWhenBoundList();
        sb.append(ln()).append("   case").append(ln());
        int index = 0;
        for (CaseWhenElement element : caseWhenList) {
            final ConditionKey conditionKey = element.getConditionKey();
            final String keyExp = conditionKey.getConditionKey();
            if (isManualOrderConditionKeyNullHandling(conditionKey)) {
                sb.append("     when ");
                sb.append(columnAlias).append(" ").append(keyExp);
                sb.append(" then ").append(index).append(ln());
            } else {
                final Object bindExp = element.getOrderValue();
                if (bindExp == null) {
                    continue; // ignores null value
                }
                sb.append("     when ");
                sb.append(columnAlias).append(" ").append(keyExp).append(" ").append(bindExp);
                sb.append(" then ").append(index).append(ln());
            }
            ++index;
        }
        sb.append("     else ").append(index).append(ln());
        sb.append("   end ").append(_ascDesc);
    }

    protected boolean isManualOrderConditionKeyNullHandling(ConditionKey conditionKey) {
        return conditionKey.equals(ConditionKey.CK_IS_NULL) || conditionKey.equals(ConditionKey.CK_IS_NOT_NULL);
    }

    protected void throwOrderByColumnNotFoundException(String columnName,
            Map<String, String> selectClauseRealColumnAliasMap) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The column for order-by was not found in select-clause!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "If you use 'union()' or 'unionAll()', check your condition-bean!" + ln();
        msg = msg + "You can use only order-by columns on select-clause if union." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    [before (x)]" + ln();
        msg = msg + "    AaaCB cb = new AaaCB();" + ln();
        msg = msg + "    cb.query().setXxx...();" + ln();
        msg = msg + "    cb.union(new UnionQuery<AaaCB>() {" + ln();
        msg = msg + "        public void query(AaaCB unionCB) {" + ln();
        msg = msg + "            unionCB.query().setXxx...();" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }" + ln();
        msg = msg + "    cb.query().queryBbb().addOrderBy_BbbName_Asc();// *NG!" + ln();
        msg = msg + "    " + ln();
        msg = msg + "    [after (o)]" + ln();
        msg = msg + "    AaaCB cb = new AaaCB();" + ln();
        msg = msg + "    cb.setupSelect_Bbb();// *Point!" + ln();
        msg = msg + "    cb.query().setXxx...();" + ln();
        msg = msg + "    cb.union(new UnionQuery<AaaCB>() {" + ln();
        msg = msg + "        public void query(AaaCB unionCB) {" + ln();
        msg = msg + "            unionCB.query().setXxx...();" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }" + ln();
        msg = msg + "    cb.query().queryBbb().addOrderBy_BbbName_Asc();// *OK!" + ln();
        msg = msg + "    " + ln();
        msg = msg + "Or else if you DON'T use 'union()' or 'unionAll()', This is the Framework Exception!" + ln();
        msg = msg + ln();
        msg = msg + "[Target Column]" + ln();
        msg = msg + columnName + ln();
        msg = msg + ln();
        msg = msg + "[Internal Object]" + ln();
        msg = msg + "selectClauseRealColumnAliasMap=" + selectClauseRealColumnAliasMap + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IllegalStateException(msg);
    }

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * This method overrides the method that is declared at super.
     * @return The view-string of all-columns value. (NotNull)
     */
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{aliasName=").append(_aliasName);
        sb.append(" columnName=").append(_columnName);
        sb.append(" ascDesc=").append(_ascDesc).append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getAliasName() {
        return _aliasName;
    }

    public void setAliasName(String value) {
        _aliasName = value;
    }

    public String getColumnName() {
        return _columnName;
    }

    public void setColumnName(String value) {
        _columnName = value;
    }

    public String getAscDesc() {
        return _ascDesc;
    }

    public void setAscDesc(String value) {
        _ascDesc = value;
    }

    public void setOrderByNullsSetupper(OrderByClause.OrderByNullsSetupper value, boolean nullsFirst) {
        _orderByNullsSetupper = value;
        _nullsFirst = nullsFirst;
    }

    public void setManualOrderBean(ManualOrderBean manualOrderBean) {
        _manualOrderBean = manualOrderBean;
    }
}
