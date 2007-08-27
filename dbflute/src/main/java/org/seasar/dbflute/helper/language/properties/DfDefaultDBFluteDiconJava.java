package org.seasar.dbflute.helper.language.properties;

/**
 * @author jflute
 */
public class DfDefaultDBFluteDiconJava implements DfDefaultDBFluteDicon {

    public String getDBFluteDiconFileName() {
        return "dbflute.dicon";
    }

    public String getDBFluteDiconNamespace() {
        return "dbflute";
    }

    public String getDBFluteDiconPackageName() {
        return "../resources";
    }

    public String getJ2eeDiconResourceName() {
        return "j2ee.dicon";
    }

    public String getRequiredTxComponentName() {
        return "requiredTx";
    }

    public String getRequiresNewTxComponentName() {
        return "requiresNewTx";
    }
}