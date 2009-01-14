package org.seasar.dbflute.cbean.ckey;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.coption.InScopeOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;


/**
 * The condition-key of inScope.
 * @author DBFlute(AutoGenerator)
 */
public class ConditionKeyInScope extends ConditionKey {

    /** Log-instance. */
    private static final Log _log = LogFactory.getLog(ConditionKeyInScope.class);

    /**
     * Constructor.
     */
    protected ConditionKeyInScope() {
        _conditionKey = "inScope";
        _operand = "in";
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
        if (value instanceof java.util.List && ((java.util.List<?>)value).isEmpty()) {
            return false;
        }
        if (value instanceof java.util.List) {
            if (conditionValue.hasInScope()) {
                if (conditionValue.equalInScope(((java.util.List<?>)value))) {
                    _log.debug("The value has already registered at " + callerName + ": value=" + value);
                    return false;
                } else {
                    conditionValue.overrideInScope(((java.util.List<?>)value));
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * This method implements super#doAddWhereClause().
     * @param conditionList Condition list. (NotNull)
     * @param columnName Column name. (NotNull)
     * @param value Condition value. (NotNull)
     */
    protected void doAddWhereClause(java.util.List<String> conditionList, String columnName, ConditionValue value) {
        if (value.getInScope() == null) {
            return;
        }
        conditionList.add(buildBindClause(columnName, value.getInScopeLocation(), "('a1', 'a2')"));
    }

    /**
     * This method implements super#doAddWhereClause().
     * @param conditionList Condition list. (NotNull)
     * @param columnName Column name. (NotNull)
     * @param value Condition value. (NotNull)
     * @param option Condition option. (NotNull)
     */
    protected void doAddWhereClause(java.util.List<String> conditionList, String columnName, ConditionValue value, ConditionOption option) {
        if (option == null) {
            String msg = "The argument[option] should not be null: columnName=" + columnName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (!(option instanceof InScopeOption)) {
            String msg = "The argument[option] should be InScopeOption: columnName=" + columnName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
        conditionList.add(buildBindClause(columnName, value.getInScopeLocation(), "('a1', 'a2')"));
    }

    /**
     * This method implements super#doSetupConditionValue().
     * @param conditionValue Condition value. (NotNull)
     * @param value Value. (NotNull)
     * @param location Location. (NotNull)
     */
    protected void doSetupConditionValue(ConditionValue conditionValue, Object value, String location) {
        conditionValue.setInScope((java.util.List<?>)value).setInScopeLocation(location);
    }

    /**
     * This method implements super#doSetupConditionValue().
     * @param conditionValue Condition value. (NotNull)
     * @param value Value. (NotNull)
     * @param location Location. (NotNull)
     * @param option Condition option. (NotNull)
     */
    protected void doSetupConditionValue(ConditionValue conditionValue, Object value, String location, ConditionOption option) {
        conditionValue.setInScope((java.util.List<?>)value, (InScopeOption)option).setInScopeLocation(location);
    }
}
