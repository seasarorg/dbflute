package org.seasar.dbflute.helper.datahandler.impl;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author jflute
 * @since 0.5.9 (2007/12/21 Friday)
 */
public class DfSeparatedDataHandlerImplTest {
    @Test
    public void test_isUnsupportedEncodingDirectory() throws Exception {
        final DfSeparatedDataHandlerImpl target = new DfSeparatedDataHandlerImpl();
        Assert.assertFalse(target.isUnsupportedEncodingDirectory("UTF-8"));
        Assert.assertFalse(target.isUnsupportedEncodingDirectory("Windows-31J"));
        Assert.assertTrue(target.isUnsupportedEncodingDirectory("UTF-8sss"));
    }
}
