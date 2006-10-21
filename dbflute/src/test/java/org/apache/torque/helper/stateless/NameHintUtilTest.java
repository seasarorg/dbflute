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
        
        Assert.assertTrue(NameHintUtil.isHitByTheHint("XXX_YN", NameHintUtil.SUFFIX_MARK + "_YN"));
        Assert.assertTrue(NameHintUtil.isHitByTheHint("L_XXX", NameHintUtil.PREFIX_MARK + "L_"));
        Assert.assertTrue(NameHintUtil.isHitByTheHint("XXX", "XXX"));
        Assert.assertFalse(NameHintUtil.isHitByTheHint("XXX_YN", NameHintUtil.PREFIX_MARK + "_YN"));
        Assert.assertFalse(NameHintUtil.isHitByTheHint("L_XXX", NameHintUtil.SUFFIX_MARK + "L_"));
    }       
}
