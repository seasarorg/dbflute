package org.apache.torque.engine.database.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

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
    public void test_Table_buildVersionNoUncapitalisedJavaName() throws Exception {
        // ## Arrange ##
        final Table table = new Table();

        // ## Act & Assert ##
        Assert.assertEquals("versionNo", table.buildVersionNoUncapitalisedJavaName("VersionNo"));
        Assert.assertEquals("versionNo", table.buildVersionNoUncapitalisedJavaName("versionNo"));
        Assert.assertEquals("versionno", table.buildVersionNoUncapitalisedJavaName("versionno"));
    }
}
