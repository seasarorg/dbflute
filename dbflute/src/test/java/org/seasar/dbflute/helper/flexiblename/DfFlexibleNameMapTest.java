package org.seasar.dbflute.helper.flexiblename;

import org.junit.Assert;
import org.junit.Test;

public class DfFlexibleNameMapTest {

    @Test
    public void test_Basic() {
        // ## Arrange ##
        final DfFlexibleNameMap<String, String> map = new DfFlexibleNameMap<String, String>();
        
        // ## Act ##
        map.put("R_TIME", "aaa");
        map.put("Ruser", "bbb");
        map.put("UTIME", "ccc");
        map.put("uuser", "ddd");
        
        // ## Assert ##
        // {R_TIME}
        Assert.assertTrue(map.containsKey("R_TIME"));
        Assert.assertTrue(map.containsKey("RTIME"));
        Assert.assertTrue(map.containsKey("rTime"));
        Assert.assertTrue(map.containsKey("RTime"));
        Assert.assertEquals("aaa", map.get("R_TIME"));
        Assert.assertEquals("aaa", map.get("RTIME"));
        Assert.assertEquals("aaa", map.get("rTime"));
        Assert.assertEquals("aaa", map.get("RTime"));
        
        // {Ruser}
        Assert.assertTrue(map.containsKey("R_USER"));
        Assert.assertTrue(map.containsKey("RUSER"));
        Assert.assertTrue(map.containsKey("rUser"));
        Assert.assertTrue(map.containsKey("RUser"));
        Assert.assertEquals("bbb", map.get("R_USER"));
        Assert.assertEquals("bbb", map.get("RUSER"));
        Assert.assertEquals("bbb", map.get("rUser"));
        Assert.assertEquals("bbb", map.get("RUser"));
    }
}
