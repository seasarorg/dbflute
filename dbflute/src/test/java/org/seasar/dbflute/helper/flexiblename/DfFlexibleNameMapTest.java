package org.seasar.dbflute.helper.flexiblename;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class DfFlexibleNameMapTest {

    public DfFlexibleNameMapTest() {
    }

    @Test
    public void test_isHitByTheHint() {

        final Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("RTIME", "");
        map.put("RUSER", "");
        map.put("UTIME", "");
        map.put("UUSER", "");

        final DfFlexibleNameMap<String, String> flexibleNameMap = new DfFlexibleNameMap<String, String>(map);
        Assert.assertTrue(flexibleNameMap.containsKey("U_TIME"));
    }
}
