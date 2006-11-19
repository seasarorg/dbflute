package org.apache.torque.helper.stateless;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.texen.Generator;
import org.apache.velocity.texen.util.FileUtil;
import org.apache.velocity.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.seasar.dbflute.util.DfNameHintUtil;


public class NameHintUtilTest {

    public NameHintUtilTest() {
    }

    @Test
    public void test_isHitByTheHint() {
        try {
            Velocity.init();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        Assert.assertTrue(DfNameHintUtil.isHitByTheHint("XXX_YN", DfNameHintUtil.SUFFIX_MARK + "_YN"));
        Assert.assertTrue(DfNameHintUtil.isHitByTheHint("L_XXX", DfNameHintUtil.PREFIX_MARK + "L_"));
        Assert.assertTrue(DfNameHintUtil.isHitByTheHint("XXX", "XXX"));
        Assert.assertFalse(DfNameHintUtil.isHitByTheHint("XXX_YN", DfNameHintUtil.PREFIX_MARK + "_YN"));
        Assert.assertFalse(DfNameHintUtil.isHitByTheHint("L_XXX", DfNameHintUtil.SUFFIX_MARK + "L_"));
    }       
}
