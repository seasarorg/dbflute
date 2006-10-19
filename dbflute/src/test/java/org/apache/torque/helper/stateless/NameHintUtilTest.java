package org.apache.torque.helper.stateless;

import org.junit.Assert;
import org.junit.Test;


public class NameHintUtilTest {

    public NameHintUtilTest() {
    }

    @Test
    public void test_isHitByTheHint() {
        Assert.assertTrue(NameHintUtil.isHitByTheHint("XXX_YN", NameHintUtil.SUFFIX_MARK + "_YN"));
        Assert.assertTrue(NameHintUtil.isHitByTheHint("L_XXX", NameHintUtil.PREFIX_MARK + "L_"));
        Assert.assertTrue(NameHintUtil.isHitByTheHint("XXX", "XXX"));
        Assert.assertFalse(NameHintUtil.isHitByTheHint("XXX_YN", NameHintUtil.PREFIX_MARK + "_YN"));
        Assert.assertFalse(NameHintUtil.isHitByTheHint("L_XXX", NameHintUtil.SUFFIX_MARK + "L_"));
    }
}
