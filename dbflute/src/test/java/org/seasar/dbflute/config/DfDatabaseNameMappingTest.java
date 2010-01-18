package org.seasar.dbflute.config;

import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.seasar.dbflute.unit.PlainTestCase;

public class DfDatabaseNameMappingTest extends PlainTestCase {

    public DfDatabaseNameMappingTest() {
    }

    @Test
    public void test_getDatabaseBaseInfo() {
        final DfDatabaseNameMapping config = DfDatabaseNameMapping.getInstance();
        final Map<String, Map<String, String>> databaseBaseInfo = config.analyze();
        Assert.assertNotNull(databaseBaseInfo);
        final Set<String> keySet = databaseBaseInfo.keySet();
        for (String key : keySet) {
            Assert.assertNotNull(key);

            log("[" + key + "]");

            final Map<String, String> infoElement = (Map<String, String>) databaseBaseInfo.get(key);
            Assert.assertNotNull(infoElement);
            final Set<String> elementKeySet = infoElement.keySet();
            for (String elementKey : elementKeySet) {
                Assert.assertNotNull(elementKey);
                final String elementValue = infoElement.get(elementKey);
                Assert.assertNotNull(elementValue);

                log("    " + elementKey + "=" + elementValue);
            }
        }
    }

    @Test
    public void test_getDatabaseBaseInfoTest() {
        final DfDatabaseNameMapping config = DfDatabaseNameMapping.getInstance();
        final Map<String, Map<String, String>> databaseBaseInfo = config.analyze();
        Assert.assertNotNull(databaseBaseInfo);
        log("databaseBaseInfoTest=" + databaseBaseInfo);
    }
}
