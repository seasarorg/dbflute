package org.seasar.dbflute.config;

import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.seasar.dbflute.config.DfDatabaseConfig;
import org.seasar.dbflute.unit.DfDBFluteTestCase;

public class DfDatabaseConfigTest extends DfDBFluteTestCase {

    public DfDatabaseConfigTest() {
    }

    @Test
    public void test_getDatabaseBaseInfo() {
        final DfDatabaseConfig config = (DfDatabaseConfig) getComponent(DfDatabaseConfig.class);
        final Map<String, Map<String, String>> databaseBaseInfo = config.analyzeDatabaseBaseInfo();
        Assert.assertNotNull(databaseBaseInfo);
        final Set<String> keySet = databaseBaseInfo.keySet();
        for (String key : keySet) {
            Assert.assertNotNull(key);

            _log.debug("[" + key + "]");

            final Map<String, String> infoElement = (Map<String, String>) databaseBaseInfo.get(key);
            Assert.assertNotNull(infoElement);
            final Set<String> elementKeySet = infoElement.keySet();
            for (String elementKey : elementKeySet) {
                Assert.assertNotNull(elementKey);
                final String elementValue = infoElement.get(elementKey);
                Assert.assertNotNull(elementValue);

                _log.debug("    " + elementKey + "=" + elementValue);
            }
        }
    }

    @Test
    public void test_getDatabaseBaseInfoTest() {
        String real = null;
        {
            final DfDatabaseConfig component = (DfDatabaseConfig) getComponent(DfDatabaseConfig.class);
            final Map<String, Map<String, String>> databaseBaseInfo = component.analyzeDatabaseBaseInfo();
            Assert.assertNotNull(databaseBaseInfo);
            _log.debug("databaseBaseInfoTest=" + databaseBaseInfo);
            real = databaseBaseInfo.toString();
        }
        {
            final DfDatabaseConfig component = (DfDatabaseConfig) getComponent(DfDatabaseConfig.class);
            final Map<String, Map<String, String>> databaseBaseInfo = component.getDatabaseBaseInfoOgnlTest();
            Assert.assertEquals(databaseBaseInfo.toString(), real);
        }
    }
}
