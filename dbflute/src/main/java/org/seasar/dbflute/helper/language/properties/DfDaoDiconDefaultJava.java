package org.seasar.dbflute.helper.language.properties;

public class DfDaoDiconDefaultJava implements DfDaoDiconDefault {
    public String getJdbcDiconResourceName() {
        return "jdbc.dicon";
    }

    public String getRequiredTxComponentName() {
        return "requiredTx";
    }

    public String getRequiresNewTxComponentName() {
        return "requiresNewTx";
    }
}