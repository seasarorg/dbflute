package org.dbflute.cbean.ckey;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbflute.cbean.coption.ConditionOption;
import org.dbflute.cbean.cvalue.ConditionValue;


/**
 * The condition-key of equal.
 * 
 * @author DBFlute(AutoGenerator)
 */
public class ConditionKeyEqual extends ConditionKey {

    /** Log-instance. */
    private static final Log _log = LogFactory.getLog(ConditionKeyEqual.class);

    /**
     * Constructor.
     */
    protected ConditionKeyEqual() {
        _conditionKey = "equal";
        _operand = "=";
    }

    /**
     * Is valid registration?
     * 
     * @param conditionValue Condition value. (NotNull)
     * @param value Value. (NotNull)
     * @param callerName Caller name. (NotNull)
     * @return Determination.
     */
    public boolean isValidRegistration(ConditionValue conditionValue, Object value, String callerName) {
        if (value == null) {
            return false;
        }
        if (conditionValue.hasEqual()) {
            if (conditionValue.equalEqual(value)) {
                _log.debug("The value has already registered at " + callerName + ": value=" + value);
                return false;
            } else {
                conditionValue.overrideEqual(value);
                return false;
            }
        }
        return true;
    }

    /**
     * This method implements super#doAddWhereClause().
     * 
     * @param conditionList Condition list. (NotNull)
     * @param columnName Column name. (NotNull)
     * @param value Condition value. (NotNull)
     */
    protected void doAddWhereClause(java.util.List<String> conditionList, String columnName, ConditionValue value) {
        if (value.getEqual() == null) {
            return;
        }
        conditionList.add(buildBindClause(columnName, value.getEqualLocation()));
    }

    /**
     * This method implements super#doAddWhereClause().
     * 
     * @param conditionList Condition list. (NotNull)
     * @param columnName Column name. (NotNull)
     * @param value Condition value. (NotNull)
     * @param option Condition option. (NotNull)
     */
    protected void doAddWhereClause(java.util.List<String> conditionList, String columnName, ConditionValue value, ConditionOption option) {
        throw new UnsupportedOperationException("doAddWhereClause with condition-option is unsupported!!!");
    }

    /**
     * This method implements super#doSetupConditionValue().
     * 
     * @param conditionValue Condition value. (NotNull)
     * @param value Value. (NotNull)
     * @param location Location. (NotNull)
     */
    protected void doSetupConditionValue(ConditionValue conditionValue, Object value, String location) {
        conditionValue.setEqual(value).setEqualLocation(location);
    }

    /**
     * This method implements super#doSetupConditionValue().
     * 
     * @param conditionValue Condition value. (NotNull)
     * @param value Value. (NotNull)
     * @param location Location. (NotNull)
     * @param option Condition option. (NotNull)
     */
    protected void doSetupConditionValue(ConditionValue conditionValue, Object value, String location, ConditionOption option) {
        throw new UnsupportedOperationException("doSetupConditionValue with condition-option is unsupported!!!");
    }
}
