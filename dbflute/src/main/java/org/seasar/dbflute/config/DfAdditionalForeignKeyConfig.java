package org.seasar.dbflute.config;

import java.util.Map;

public class DfAdditionalForeignKeyConfig {

    protected Map<String, Map<String, String>> _additionalForeignKey;

    public Map<String, Map<String, String>> getAdditionalForeignKey() {
        return _additionalForeignKey;
    }

    public void setAdditionalForeignKey(Map<String, Map<String, String>> additionalForeignKey) {
        _additionalForeignKey = additionalForeignKey;
    }
}
