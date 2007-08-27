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

    public String getDBFluteDiconPackageName() {
        return "../source";
    }

    public String getJ2eeDiconResourceName() {
        return "Ado.dicon";
    }

    public String getRequiredTxComponentName() {
        return "LocalRequiredTx";
    }

    public String getRequiresNewTxComponentName() {
        return "LocalRequiresNewTx";
    }

}