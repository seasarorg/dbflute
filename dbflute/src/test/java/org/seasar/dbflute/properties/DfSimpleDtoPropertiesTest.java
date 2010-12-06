package org.seasar.dbflute.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.seasar.dbflute.properties.DfSimpleDtoProperties.doBuildFieldName;

import org.junit.Test;
import org.seasar.dbflute.exception.DfIllegalPropertySettingException;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 */
public class DfSimpleDtoPropertiesTest extends PlainTestCase {

    @Test
    public void test_doBuildFieldName_entry_basic() throws Exception {
        assertEquals("_memberName", doBuildFieldName("MemberName", "BEANS", false));
        assertEquals("_TMemberName", doBuildFieldName("TMemberName", "BEANS", false));
        assertEquals("_memberName", doBuildFieldName("MemberName", "beans", false));
        assertEquals("_TMemberName", doBuildFieldName("TMemberName", "beans", false));
        assertEquals("memberName", doBuildFieldName("MemberName", "BEANS", true));
        assertEquals("TMemberName", doBuildFieldName("TMemberName", "BEANS", true));

        assertEquals("_MemberName", doBuildFieldName("MemberName", "CAP", false));
        assertEquals("_TMemberName", doBuildFieldName("TMemberName", "CAP", false));
        assertEquals("_MemberName", doBuildFieldName("MemberName", "cap", false));
        assertEquals("_TMemberName", doBuildFieldName("TMemberName", "cap", false));
        assertEquals("MemberName", doBuildFieldName("MemberName", "CAP", true));
        assertEquals("TMemberName", doBuildFieldName("TMemberName", "CAP", true));

        assertEquals("_memberName", doBuildFieldName("MemberName", "UNCAP", false));
        assertEquals("_tMemberName", doBuildFieldName("TMemberName", "UNCAP", false));
        assertEquals("_memberName", doBuildFieldName("MemberName", "uncap", false));
        assertEquals("_tMemberName", doBuildFieldName("TMemberName", "uncap", false));
        assertEquals("memberName", doBuildFieldName("MemberName", "UNCAP", true));
        assertEquals("tMemberName", doBuildFieldName("TMemberName", "UNCAP", true));

        try {
            assertEquals("_memberName", doBuildFieldName("MemberName", "detarame", false));

            fail();
        } catch (DfIllegalPropertySettingException e) {
            // OK
            log(e.getMessage());
        }
    }

    @Test
    public void test_doBuildFieldName_manual_basic() throws Exception {
        assertEquals("_memberName", doBuildFieldName("MemberName", true, false, false));
        assertEquals("_TMemberName", doBuildFieldName("TMemberName", true, false, false));
        assertEquals("_MemberName", doBuildFieldName("MemberName", false, true, false));
        assertEquals("_TMemberName", doBuildFieldName("TMemberName", false, true, false));
        assertEquals("memberName", doBuildFieldName("MemberName", false, false, true));
        assertEquals("tMemberName", doBuildFieldName("TMemberName", false, false, true));
        assertEquals("_memberName", doBuildFieldName("MemberName", false, false, false));
        assertEquals("_tMemberName", doBuildFieldName("TMemberName", false, false, false));

        // no way
        assertEquals("_memberName", doBuildFieldName("MemberName", true, true, false));
        assertEquals("_TMemberName", doBuildFieldName("TMemberName", true, true, false));
        assertEquals("memberName", doBuildFieldName("MemberName", true, true, true));
        assertEquals("TMemberName", doBuildFieldName("TMemberName", true, true, true));
    }
}
