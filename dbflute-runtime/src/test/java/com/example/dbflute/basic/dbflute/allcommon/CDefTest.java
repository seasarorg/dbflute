package com.example.dbflute.basic.dbflute.allcommon;


import com.example.dbflute.basic.dbflute.allcommon.CDef.MemberStatus;
import com.example.dbflute.basic.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.8.0 (2008/09/22 Monday)
 */
public class CDefTest extends PlainTestCase {

    public void test_CDef_valueOf_Success() {
        // ## Arrange & Act ##
        final MemberStatus memberStatus = CDef.MemberStatus.valueOf("Formalized");

        // ## Assert ##
        assertNotNull(memberStatus);
        assertEquals("FML", memberStatus.code());
        assertEquals("Formalized", memberStatus.name());
        assertEquals("正式会員", memberStatus.alias());
        assertTrue(memberStatus.equals(CDef.MemberStatus.Formalized));
        assertTrue(memberStatus.equals(CDef.MemberStatus.codeOf("FML")));
    }

    public void test_CDef_valueOf_NotFound() {
        // ## Arrange & Act & Assert ##
        try {
            CDef.MemberStatus.valueOf("NotFound");
        } catch (IllegalArgumentException e) {
            // OK
            log(e.getMessage());
        }
    }
}
