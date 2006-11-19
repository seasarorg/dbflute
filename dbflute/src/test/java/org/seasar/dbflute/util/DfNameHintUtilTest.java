package org.seasar.dbflute.util;

import org.junit.Assert;
import org.junit.Test;
import org.seasar.dbflute.util.DfNameHintUtil;


public class DfNameHintUtilTest {

    public DfNameHintUtilTest() {
    }

    @Test
    public void test_isHitByTheHint() {
        Assert.assertTrue(DfNameHintUtil.isHitByTheHint("XXX_YN", DfNameHintUtil.SUFFIX_MARK + "_YN"));
        Assert.assertTrue(DfNameHintUtil.isHitByTheHint("L_XXX", DfNameHintUtil.PREFIX_MARK + "L_"));
        Assert.assertTrue(DfNameHintUtil.isHitByTheHint("XXX", "XXX"));
        Assert.assertFalse(DfNameHintUtil.isHitByTheHint("XXX_YN", DfNameHintUtil.PREFIX_MARK + "_YN"));
        Assert.assertFalse(DfNameHintUtil.isHitByTheHint("L_XXX", DfNameHintUtil.SUFFIX_MARK + "L_"));
    }       
}
