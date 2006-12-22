package org.seasar.dbflute.helper.datahandler.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.seasar.dbflute.helper.datahandler.DfSeparatedDataHandler;

public class DfSeparatedDataHandlerImplTest {

    @Test
    public void test_arrangeValueList_Basic() {
        final List<String> valueList = new ArrayList<String>();
        valueList.add("\"aaa\"");
        valueList.add("\"bbb\"");
        valueList.add("\"ccc\"");
        final List<String> expectedValueList = new ArrayList<String>();
        expectedValueList.add("aaa");
        expectedValueList.add("bbb");
        expectedValueList.add("ccc");
        final DfSeparatedDataHandler handler = new DfSeparatedDataHandlerImpl() {
            @Override
            public String toString() {
                final ValueLineInfo valueLineInfo = arrangeValueList(valueList, "\t");
                Assert.assertEquals(expectedValueList, valueLineInfo.getValueList());
                return null;
            }
        };
        handler.toString();
    }
    
    @Test
    public void test_arrangeValueList_Connect() {
        final List<String> valueList = new ArrayList<String>();
        valueList.add("\"aaa\"");
        valueList.add("\"bbb\"");
        valueList.add("\"ccc");
        valueList.add("ccc\"");
        valueList.add("\"ddd");
        valueList.add("ddd");
        valueList.add("ddd\"");
        valueList.add("\"eee");
        valueList.add("eee");
        valueList.add("\"eee");
        valueList.add("eee\"");
        final List<String> expectedValueList = new ArrayList<String>();
        expectedValueList.add("aaa");
        expectedValueList.add("bbb");
        expectedValueList.add("cccccc");
        expectedValueList.add("ddddddddd");
        expectedValueList.add("eeeeee\"eeeeee");
        final DfSeparatedDataHandler handler = new DfSeparatedDataHandlerImpl() {
            @Override
            public String toString() {
                final ValueLineInfo valueLineInfo = arrangeValueList(valueList, "\t");
                Assert.assertEquals(expectedValueList, valueLineInfo.getValueList());
                return null;
            }
        };
        handler.toString();
    }

    protected String removeDoubleQuotation(String target) {
        return target.substring(1, target.length() - 1);
    }
}
