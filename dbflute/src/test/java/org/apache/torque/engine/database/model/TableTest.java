package org.apache.torque.engine.database.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.seasar.dbflute.DfBuildProperties;

public class TableTest {

    @Test
    public void test_Table_extractMinimumRelationIndex() throws Exception {
        final Table table = new Table();
        final Map<String, Integer> relationIndexMap = new LinkedHashMap<String, Integer>();
        {
            relationIndexMap.clear();
            relationIndexMap.put("0", 0);
            relationIndexMap.put("1", 1);
            relationIndexMap.put("2", 2);
            relationIndexMap.put("3", 3);
            final int minimumRelationIndex = table.extractMinimumRelationIndex(relationIndexMap);
            Assert.assertEquals(4, minimumRelationIndex);
        }
        {
            relationIndexMap.clear();
            relationIndexMap.put("1", 1);
            relationIndexMap.put("2", 2);
            relationIndexMap.put("3", 3);
            final int minimumRelationIndex = table.extractMinimumRelationIndex(relationIndexMap);
            Assert.assertEquals(0, minimumRelationIndex);
        }
        {
            relationIndexMap.clear();
            relationIndexMap.put("0", 0);
            relationIndexMap.put("1", 1);
            relationIndexMap.put("3", 3);
            final int minimumRelationIndex = table.extractMinimumRelationIndex(relationIndexMap);
            Assert.assertEquals(2, minimumRelationIndex);
        }
        {
            relationIndexMap.clear();
            relationIndexMap.put("0", 0);
            relationIndexMap.put("1", 1);
            relationIndexMap.put("3", 3);
            relationIndexMap.put("5", 5);
            final int minimumRelationIndex = table.extractMinimumRelationIndex(relationIndexMap);
            Assert.assertEquals(2, minimumRelationIndex);
        }
    }

    @Test
    public void test_Table_buildVersionNoJavaName_JavaNameNotSameDbName() throws Exception {
        // ## Arrange ##
        final Table table = new Table();
        table._javaNamingMethod = NameGenerator.CONV_METHOD_UNDERSCORE;
        final DfBuildProperties prop = DfBuildProperties.getInstance();
        final Properties buildProperties = new Properties();
        buildProperties.setProperty("torque.isAvailableToLowerInGeneratorUnderscoreMethod", "true");// Default
        prop.setProperties(buildProperties);

        // ## Act & Assert ##
        Assert.assertEquals("VersionNo", table.buildVersionNoJavaName("VERSION_NO"));
        Assert.assertEquals("VersionNo", table.buildVersionNoJavaName("version_no"));
        Assert.assertEquals("Versionno", table.buildVersionNoJavaName("versionno"));
        Assert.assertEquals("Versionno", table.buildVersionNoJavaName("versionNo"));
    }

    @Test
    public void test_Table_buildVersionNoJavaName_JavaNameSameDbName() throws Exception {
        // ## Arrange ##
        final Table table = new Table();
        table._javaNamingMethod = NameGenerator.CONV_METHOD_UNDERSCORE;
        final DfBuildProperties prop = DfBuildProperties.getInstance();
        final Properties buildProperties = new Properties();
        buildProperties.setProperty("torque.isAvailableToLowerInGeneratorUnderscoreMethod", "true");// Default
        buildProperties.setProperty("torque.isJavaNameOfColumnSameAsDbName", "true");
        prop.setProperties(buildProperties);

        // ## Act & Assert ##
        Assert.assertEquals("VERSION_NO", table.buildVersionNoJavaName("VERSION_NO"));
        Assert.assertEquals("version_no", table.buildVersionNoJavaName("version_no"));
        Assert.assertEquals("versionno", table.buildVersionNoJavaName("versionno"));
        Assert.assertEquals("versionNo", table.buildVersionNoJavaName("versionNo"));
    }

    @Test
    public void test_Table_buildVersionNoUncapitalisedJavaName() throws Exception {
        // ## Arrange ##
        final Table table = new Table();

        // ## Act & Assert ##
        Assert.assertEquals("versionNo", table.buildVersionNoUncapitalisedJavaName("VersionNo"));
        Assert.assertEquals("versionNo", table.buildVersionNoUncapitalisedJavaName("versionNo"));
        Assert.assertEquals("versionno", table.buildVersionNoUncapitalisedJavaName("versionno"));
    }

    @Test
    public void test_Table_makeJavaName() throws Exception {
        // ## Arrange ##
        final Table table = new Table();
        table._javaNamingMethod = NameGenerator.CONV_METHOD_UNDERSCORE;
        final DfBuildProperties prop = DfBuildProperties.getInstance();
        final Properties buildProperties = new Properties();
        buildProperties.setProperty("torque.isAvailableToLowerInGeneratorUnderscoreMethod", "true");// Default
        prop.setProperties(buildProperties);

        // ## Act & Assert ##
        Assert.assertEquals("VersionNo", table.makeJavaName("VERSION_NO"));
        Assert.assertEquals("VersionNo", table.makeJavaName("version_no"));
        Assert.assertEquals("Versionno", table.makeJavaName("VersionNo"));
        Assert.assertEquals("Versionno", table.makeJavaName("VERSIONNO"));
        Assert.assertEquals("Versionno", table.makeJavaName("VersionNo"));
    }
}
