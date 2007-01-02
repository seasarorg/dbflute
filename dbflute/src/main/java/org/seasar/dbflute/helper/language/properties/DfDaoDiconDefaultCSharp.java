package org.seasar.dbflute.helper.language.properties;

public class DfDaoDiconDefaultCSharp implements DfDaoDiconDefault {
    public String getJdbcDiconResourceName() {
        return "ado.dicon";
    }

    public String getRequiredTxComponentName() {
        return "LocalRequiredTx";
    }

    public String getRequiresNewTxComponentName() {
        return "LocalRequiresNewTx";
    }
}