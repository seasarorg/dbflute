package org.seasar.dbflute.logic.replaceschema.loaddata.impl;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author jflute
 * @since 0.5.9 (2007/12/21 Friday)
 */
public class DfDelimiterDataHandlerImplTest {
    
    @Test
    public void test_isUnsupportedEncodingDirectory() throws Exception {
        final DfDelimiterDataHandlerImpl target = new DfDelimiterDataHandlerImpl();
        Assert.assertFalse(target.isUnsupportedEncodingDirectory("UTF-8"));
        Assert.assertFalse(target.isUnsupportedEncodingDirectory("Windows-31J"));
        Assert.assertTrue(target.isUnsupportedEncodingDirectory("UTF-8sss"));
    }
}
