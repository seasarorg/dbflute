package org.seasar.dbflute.config;

import java.util.Map;

import org.junit.Test;
import org.seasar.dbflute.config.DfAdditionalForeignKeyConfig;
import org.seasar.dbflute.unit.DfDBFluteTestCase;


public class DfAdditionalForeignKeyConfigTest extends DfDBFluteTestCase {

    public DfAdditionalForeignKeyConfigTest() {
    }

    @Test
    public void test_dynamic() {
        final DfAdditionalForeignKeyConfig component = (DfAdditionalForeignKeyConfig)getComponent(DfAdditionalForeignKeyConfig.class);
        final Map<String, Map<String, String>> additionalForeignKey = component.getAdditionalForeignKey();
        _log.debug("map: " + additionalForeignKey);
    }       
}
