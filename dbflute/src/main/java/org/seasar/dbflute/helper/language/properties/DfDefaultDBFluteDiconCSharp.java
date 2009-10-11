package org.seasar.dbflute.helper.language.properties;

/**
 * @author jflute
 */
public class DfDefaultDBFluteDiconCSharp implements DfDefaultDBFluteDicon {
    public String getDBFluteDiconFileName() {
        return "DBFlute.dicon";
    }

    public String getDBFluteDiconNamespace() {
        return "DBFlute";
    }

    public String getJ2eeDiconResourceName() {
        return "${topNamespace}/Resources/Ado.dicon";
    }

    public String getRequiredTxComponentName() {
        return "LocalRequiredTx";
    }

    public String getRequiresNewTxComponentName() {
        return "LocalRequiresNewTx";
    }

}