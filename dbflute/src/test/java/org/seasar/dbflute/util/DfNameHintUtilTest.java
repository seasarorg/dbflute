package org.seasar.dbflute.util;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class DfNameHintUtilTest {

    public DfNameHintUtilTest() {
    }

    @Test
    public void test_isTargetByHint() {
        {
            List<String> targetList = Arrays.asList(new String[] { "prefix:MY_" });
            List<String> exceptList = Arrays.asList(new String[] {});
            Assert.assertTrue(DfNameHintUtil.isTargetByHint("MY_NAME", targetList, exceptList));
            Assert.assertFalse(DfNameHintUtil.isTargetByHint("MO_NAME", targetList, exceptList));
        }
        {
            List<String> targetList = Arrays.asList(new String[] {});
            List<String> exceptList = Arrays.asList(new String[] {"prefix:MY_"});
            Assert.assertFalse(DfNameHintUtil.isTargetByHint("MY_NAME", targetList, exceptList));
            Assert.assertTrue(DfNameHintUtil.isTargetByHint("MO_NAME", targetList, exceptList));
        }
        {
            List<String> targetList = Arrays.asList(new String[] {"prefix:MY_"});
            List<String> exceptList = Arrays.asList(new String[] {"prefix:MY_"});
            Assert.assertTrue(DfNameHintUtil.isTargetByHint("MY_NAME", targetList, exceptList));
            Assert.assertFalse(DfNameHintUtil.isTargetByHint("MO_NAME", targetList, exceptList));
        }
        {
            List<String> targetList = Arrays.asList(new String[] {"prefix:MY_"});
            List<String> exceptList = Arrays.asList(new String[] {"prefix:MO_"});
            Assert.assertTrue(DfNameHintUtil.isTargetByHint("MY_NAME", targetList, exceptList));
            Assert.assertFalse(DfNameHintUtil.isTargetByHint("MO_NAME", targetList, exceptList));
        }
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
