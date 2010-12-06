package org.seasar.dbflute.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.seasar.dbflute.properties.DfSimpleDtoProperties.doBuildVariableName;

import org.junit.Test;
import org.seasar.dbflute.exception.DfIllegalPropertySettingException;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 */
public class DfSimpleDtoPropertiesTest extends PlainTestCase {

    @Test
    public void test_doBuildVariableName_entry_basic() throws Exception {
        assertEquals("_memberName", doBuildVariableName("MemberName", "BEANS", false));
        assertEquals("_TMemberName", doBuildVariableName("TMemberName", "BEANS", false));
        assertEquals("_memberName", doBuildVariableName("MemberName", "beans", false));
        assertEquals("_TMemberName", doBuildVariableName("TMemberName", "beans", false));
        assertEquals("memberName", doBuildVariableName("MemberName", "BEANS", true));
        assertEquals("TMemberName", doBuildVariableName("TMemberName", "BEANS", true));

        assertEquals("_MemberName", doBuildVariableName("MemberName", "CAP", false));
        assertEquals("_TMemberName", doBuildVariableName("TMemberName", "CAP", false));
        assertEquals("_MemberName", doBuildVariableName("MemberName", "cap", false));
        assertEquals("_TMemberName", doBuildVariableName("TMemberName", "cap", false));
        assertEquals("MemberName", doBuildVariableName("MemberName", "CAP", true));
        assertEquals("TMemberName", doBuildVariableName("TMemberName", "CAP", true));

        assertEquals("_memberName", doBuildVariableName("MemberName", "UNCAP", false));
        assertEquals("_tMemberName", doBuildVariableName("TMemberName", "UNCAP", false));
        assertEquals("_memberName", doBuildVariableName("MemberName", "uncap", false));
        assertEquals("_tMemberName", doBuildVariableName("TMemberName", "uncap", false));
        assertEquals("memberName", doBuildVariableName("MemberName", "UNCAP", true));
        assertEquals("tMemberName", doBuildVariableName("TMemberName", "UNCAP", true));

        try {
            assertEquals("_memberName", doBuildVariableName("MemberName", "detarame", false));

            fail();
        } catch (DfIllegalPropertySettingException e) {
            // OK
            log(e.getMessage());
        }
    }

    @Test
    public void test_doBuildVariableName_manual_basic() throws Exception {
        assertEquals("_memberName", doBuildVariableName("MemberName", true, false, false));
        assertEquals("_TMemberName", doBuildVariableName("TMemberName", true, false, false));
        assertEquals("_MemberName", doBuildVariableName("MemberName", false, true, false));
        assertEquals("_TMemberName", doBuildVariableName("TMemberName", false, true, false));
        assertEquals("memberName", doBuildVariableName("MemberName", false, false, true));
        assertEquals("tMemberName", doBuildVariableName("TMemberName", false, false, true));
        assertEquals("_memberName", doBuildVariableName("MemberName", false, false, false));
        assertEquals("_tMemberName", doBuildVariableName("TMemberName", false, false, false));

        // no way
        assertEquals("_memberName", doBuildVariableName("MemberName", true, true, false));
        assertEquals("_TMemberName", doBuildVariableName("TMemberName", true, true, false));
        assertEquals("memberName", doBuildVariableName("MemberName", true, true, true));
        assertEquals("TMemberName", doBuildVariableName("TMemberName", true, true, true));
    }
}
