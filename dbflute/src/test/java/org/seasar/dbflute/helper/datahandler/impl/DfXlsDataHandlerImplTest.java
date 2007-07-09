package org.seasar.dbflute.helper.datahandler.impl;

import org.junit.Assert;
import org.junit.Test;

public class DfXlsDataHandlerImplTest {
    @Test
    public void test_DfXlsDataHandlerImpl_filterTimestampValue() throws Exception {
        final DfXlsDataHandlerImpl dfXlsDataHandlerImpl = new DfXlsDataHandlerImpl();
        final String filteredTimestampValue = dfXlsDataHandlerImpl.filterTimestampValue("2007/01/01");
        Assert.assertEquals("2007-01-01 00:00:00", filteredTimestampValue);
    }
}
