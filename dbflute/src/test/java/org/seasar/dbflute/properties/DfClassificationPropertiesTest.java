package org.seasar.dbflute.properties;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author jflute
 * @since 0.6.2 (2008/01/26 Saturday)
 */
public class DfClassificationPropertiesTest {

    @Test
    public void test_hasClassification_SameCase() throws Exception {
        // ## Arrange ##
        final DfClassificationProperties prop = new DfClassificationProperties(new Properties());
        final Map<String, Map<String, String>> deploymentMap = new LinkedHashMap<String, Map<String, String>>();
        {
            final Map<String, String> columnClassificationMap = new LinkedHashMap<String, String>();
            columnClassificationMap.put("MEMBER_STATUS_CODE", "MemberStatus");
            deploymentMap.put("MEMBER_STATUS", columnClassificationMap);
        }
        prop._classificationDeploymentMap = deploymentMap;
        
        // ## Act ##
        final boolean actual = prop.hasClassification("MEMBER_STATUS", "MEMBER_STATUS_CODE");
        
        // ## Assert ##
        Assert.assertTrue(actual);
    }
    
    @Test
    public void test_hasClassification_IgnoreCase() throws Exception {
        // ## Arrange ##
        final DfClassificationProperties prop = new DfClassificationProperties(new Properties());
        final Map<String, Map<String, String>> deploymentMap = new LinkedHashMap<String, Map<String, String>>();
        {
            final Map<String, String> columnClassificationMap = new LinkedHashMap<String, String>();
            columnClassificationMap.put("MEMBER_STATUS_CODE", "MemberStatus");
            deploymentMap.put("MEMBER_STATUS", columnClassificationMap);
        }
        prop._classificationDeploymentMap = deploymentMap;
        
        // ## Act ##
        final boolean actual = prop.hasClassification("member_status", "member_status_code");
        
        // ## Assert ##
        Assert.assertTrue(actual);
    }
}
