package com.example.dbflute.basic.dbflute.allcommon.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbflute.util.ValueLabelUtil;

import com.example.dbflute.basic.unit.PlainTestCase;

/**
 * The test of valueLabelUtil for Basic Example.
 * 
 * @author jflute
 * @since 0.6.2 (2008/01/26 Wednesday)
 */
public class ValueLabelUtilTest extends PlainTestCase {
    
    public void test_ValueLabelUtil_findLabel() throws Exception {
        final List<Map<String, Object>> valueLabelList = new ArrayList<Map<String, Object>>();
        {
            final Map<String, Object> map = new HashMap<String, Object>();
            map.put("value", 1);
            map.put("label", "ichi");
            valueLabelList.add(map);
        }
        {
            final Map<String, Object> map = new HashMap<String, Object>();
            map.put("value", 2);
            map.put("label", "ni");
            valueLabelList.add(map);
        }
        {
            final Map<String, Object> map = new HashMap<String, Object>();
            map.put("value", 3);
            map.put("label", "San");
            valueLabelList.add(map);
        }
        assertEquals("ichi", ValueLabelUtil.findLabel(valueLabelList, 1));
        assertEquals("ni", ValueLabelUtil.findLabel(valueLabelList, 2));
        assertEquals("San", ValueLabelUtil.findLabel(valueLabelList, 3));
    }
    
    public void test_ValueLabelUtil_createValueLabelMap() throws Exception {
        // ## Arrange ##
        final List<Map<String, Object>> valueLabelList = new ArrayList<Map<String, Object>>();
        {
            final Map<String, Object> map = new HashMap<String, Object>();
            map.put("value", 1);
            map.put("label", "ichi");
            valueLabelList.add(map);
        }
        {
            final Map<String, Object> map = new HashMap<String, Object>();
            map.put("value", 2);
            map.put("label", "ni");
            valueLabelList.add(map);
        }
        {
            final Map<String, Object> map = new HashMap<String, Object>();
            map.put("value", 3);
            map.put("label", "San");
            valueLabelList.add(map);
        }
        
        // ## Act ##
        final Map<Object, String> valueLabelMap = ValueLabelUtil.createValueLabelMap(valueLabelList);
        
        // ## Assert ##
        assertEquals("ichi", valueLabelMap.get(1));
        assertEquals("ni", valueLabelMap.get(2));
        assertEquals("San", valueLabelMap.get(3));
    }
    
    public void test_ValueLabelUtil_createLabelValueMap() throws Exception {
        // ## Arrange ##
        final List<Map<String, Object>> valueLabelList = new ArrayList<Map<String, Object>>();
        {
            final Map<String, Object> map = new HashMap<String, Object>();
            map.put("value", 1);
            map.put("label", "ichi");
            valueLabelList.add(map);
        }
        {
            final Map<String, Object> map = new HashMap<String, Object>();
            map.put("value", 2);
            map.put("label", "ni");
            valueLabelList.add(map);
        }
        {
            final Map<String, Object> map = new HashMap<String, Object>();
            map.put("value", 3);
            map.put("label", "San");
            valueLabelList.add(map);
        }
        
        // ## Act ##
        final Map<String, Object> labelValueMap = ValueLabelUtil.createLabelValueMap(valueLabelList);
        
        // ## Assert ##
        assertEquals(1, labelValueMap.get("ichi"));
        assertEquals(2, labelValueMap.get("ni"));
        assertEquals(3, labelValueMap.get("San"));
    }
}
