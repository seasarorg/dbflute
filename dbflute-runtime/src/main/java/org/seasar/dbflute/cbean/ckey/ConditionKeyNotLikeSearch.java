package org.seasar.dbflute.cbean.ckey;

import java.util.List;

import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;


/**
 * The condition-key of notLikeSearch.
 * @author DBFlute(AutoGenerator)
 */
public class ConditionKeyNotLikeSearch extends ConditionKey {

    /**
     * Constructor.
     */
    protected ConditionKeyNotLikeSearch() {
        _conditionKey = "notLikeSearch";
        _operand = "not like";
    }

    /**
     * Is valid registration?
     * @param conditionValue Condition value. (NotNull)
     * @param value Value. (NotNull)
     * @param callerName Caller name. (NotNull)
     * @return Determination.
     */
    public boolean isValidRegistration(ConditionValue conditionValue, Object value, String callerName) {
        if (value == null) {
            return false;
        }
        return true;
    }

    /**
     * This method implements super#doAddWhereClause().
     * @param conditionList Condition list. (NotNull)
     * @param columnName Column name. (NotNull)
     * @param value Condition value. (NotNull)
     */
    protected void doAddWhereClause(List<String> conditionList, String columnName, ConditionValue value) {
        throw new UnsupportedOperationException("doAddWhereClause without condition-option is unsupported!!!");
    }

    /**
     * This method implements super#doAddWhereClause().
     * @param conditionList Condition list. (NotNull)
     * @param columnName Column name. (NotNull)
     * @param value Condition value. (NotNull)
     * @param option Condition option. (NotNull)
     */
    protected void doAddWhereClause(List<String> conditionList, String columnName, ConditionValue value, ConditionOption option) {
        if (option == null) {
            String msg = "The argument[option] should not be null: columnName=" + columnName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (!(option instanceof LikeSearchOption)) {
            String msg = "The argument[option] should be LikeSearchOption: columnName=" + columnName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
        final LikeSearchOption myOption = (LikeSearchOption)option;
        conditionList.add(buildBindClauseWithRearOption(columnName, value.getNotLikeSearchLocation(), myOption.getRearOption()));
    }

    /**
     * This method implements super#doSetupConditionValue().
     * @param conditionValue Condition value. (NotNull)
     * @param value Value. (NotNull)
     * @param location Location. (NotNull)
     */
    protected void doSetupConditionValue(ConditionValue conditionValue, Object value, String location) {
        throw new UnsupportedOperationException("doSetupConditionValue without condition-option is unsupported!!!");
    }

    /**
     * This method implements super#doSetupConditionValue().
     * @param conditionValue Condition value. (NotNull)
     * @param value Value. (NotNull)
     * @param location Location. (NotNull)
     * @param option Condition option. (NotNull)
     */
    protected void doSetupConditionValue(ConditionValue conditionValue, Object value, String location, ConditionOption option) {
        conditionValue.setNotLikeSearch((String)value, (LikeSearchOption)option).setNotLikeSearchLocation(location);
    }
}
