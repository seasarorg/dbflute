package org.seasar.dbflute.logic.sqlfile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.5 (2009/04/10 Friday)
 */
public class SqlFileNameResolverTest extends PlainTestCase {

    // ===================================================================================
    //                                                                              Entity
    //                                                                              ======
    @Test
    public void test_resolveObjectNameIfNeeds_entity_basic() {
        // ## Arrange ##
        SqlFileNameResolver resolver = new SqlFileNameResolver();
        String className = SqlFileNameResolver.ENTITY_MARK;
        String fileName = "MemberBhv_selectSimpleMember.sql";

        // ## Act ##
        String actual = resolver.resolveEntityNameIfNeeds(className, fileName);

        // ## Assert ##
        log(actual);
        assertEquals("SimpleMember", actual);
    }

    @Test
    public void test_resolveObjectNameIfNeeds_entity_with_DBName() {
        // ## Arrange ##
        SqlFileNameResolver resolver = new SqlFileNameResolver();
        String className = SqlFileNameResolver.ENTITY_MARK;
        String fileName = "MemberBhv_selectSimpleMember_oracle.sql";

        // ## Act ##
        String actual = resolver.resolveEntityNameIfNeeds(className, fileName);

        // ## Assert ##
        log(actual);
        assertEquals("SimpleMember", actual);
    }

    @Test
    public void test_resolveObjectNameIfNeeds_entity_nonPrefix() {
        // ## Arrange ##
        SqlFileNameResolver resolver = new SqlFileNameResolver();
        String className = SqlFileNameResolver.ENTITY_MARK;
        String fileName = "MemberBhv_SimpleMember.sql";

        // ## Act ##
        String actual = resolver.resolveEntityNameIfNeeds(className, fileName);

        // ## Assert ##
        log(actual);
        assertEquals("SimpleMember", actual);
    }

    @Test
    public void test_resolveObjectNameIfNeeds_entity_initCap() {
        // ## Arrange ##
        SqlFileNameResolver resolver = new SqlFileNameResolver();
        String className = SqlFileNameResolver.ENTITY_MARK;
        String fileName = "MemberBhv_SelectSimpleMember.sql";

        // ## Act ##
        String actual = resolver.resolveEntityNameIfNeeds(className, fileName);

        // ## Assert ##
        log(actual);
        assertEquals("SelectSimpleMember", actual);
    }

    @Test
    public void test_resolveObjectNameIfNeeds_entity_noCap() {
        // ## Arrange ##
        SqlFileNameResolver resolver = new SqlFileNameResolver();
        String className = SqlFileNameResolver.ENTITY_MARK;
        String fileName = "MemberBhv_selectsimplemember.sql";

        // ## Act ##
        String actual = resolver.resolveEntityNameIfNeeds(className, fileName);

        // ## Assert ##
        log(actual);
        assertEquals("Selectsimplemember", actual);
    }

    @Test
    public void test_resolveObjectNameIfNeeds_entity_fullPath_by_slash() {
        // ## Arrange ##
        SqlFileNameResolver resolver = new SqlFileNameResolver();
        String className = SqlFileNameResolver.ENTITY_MARK;
        String fileName = "foo/bar/MemberBhv_selectSimpleMember.sql";

        // ## Act ##
        String actual = resolver.resolveEntityNameIfNeeds(className, fileName);

        // ## Assert ##
        log(actual);
        assertEquals("SimpleMember", actual);
    }

    @Test
    public void test_resolveObjectNameIfNeeds_entity_fullPath_by_backSlash() {
        // ## Arrange ##
        SqlFileNameResolver resolver = new SqlFileNameResolver();
        String className = SqlFileNameResolver.ENTITY_MARK;
        String fileName = "foo\\bar\\MemberBhv_selectSimpleMember.sql";

        // ## Act ##
        String actual = resolver.resolveEntityNameIfNeeds(className, fileName);

        // ## Assert ##
        log(actual);
        assertEquals("SimpleMember", actual);
    }

    @Test
    public void test_resolveObjectNameIfNeeds_entity_no_BehaviorQueryPath() {
        // ## Arrange ##
        SqlFileNameResolver resolver = new SqlFileNameResolver();
        String className = SqlFileNameResolver.ENTITY_MARK;

        // ## Act ##
        try {
            resolver.resolveEntityNameIfNeeds(className, "Mem_selectSimpleMember.sql");

            // ## Assert ##
            fail();
        } catch (IllegalStateException e) {
            // OK
            log(e.getMessage());
        }

        // ## Act ##
        try {
            resolver.resolveEntityNameIfNeeds(className, "selectSimpleMember.sql");

            // ## Assert ##
            fail();
        } catch (IllegalStateException e) {
            // OK
            log(e.getMessage());
        }
    }

    @Test
    public void test_resolveObjectNameIfNeeds_entity_no_SQLFile() {
        // ## Arrange ##
        SqlFileNameResolver resolver = new SqlFileNameResolver();
        String className = SqlFileNameResolver.ENTITY_MARK;

        // ## Act ##
        try {
            resolver.resolveEntityNameIfNeeds(className, "MemberBhv_selectSimpleMember");

            // ## Assert ##
            fail();
        } catch (IllegalStateException e) {
            // OK
            log(e.getMessage());
        }

        // ## Act ##
        try {
            resolver.resolveEntityNameIfNeeds(className, "MemberBhv_selectSimpleMember");

            // ## Assert ##
            fail();
        } catch (IllegalStateException e) {
            // OK
            log(e.getMessage());
        }
    }

    // ===================================================================================
    //                                                                       ParameterBean
    //                                                                       =============
    @Test
    public void test_resolveObjectNameIfNeeds_pmb_basic() {
        // ## Arrange ##
        SqlFileNameResolver resolver = new SqlFileNameResolver();
        String className = SqlFileNameResolver.PMB_MARK;
        String fileName = "MemberBhv_selectSimpleMember.sql";

        // ## Act ##
        String actual = resolver.resolvePmbNameIfNeeds(className, fileName);

        // ## Assert ##
        log(actual);
        assertEquals("SimpleMemberPmb", actual);
    }

    @Test
    public void test_resolveObjectNameIfNeeds_pmb_with_DBName() {
        // ## Arrange ##
        SqlFileNameResolver resolver = new SqlFileNameResolver();
        String className = SqlFileNameResolver.PMB_MARK;
        String fileName = "MemberBhv_selectSimpleMember_oracle.sql";

        // ## Act ##
        String actual = resolver.resolvePmbNameIfNeeds(className, fileName);

        // ## Assert ##
        log(actual);
        assertEquals("SimpleMemberPmb", actual);
    }

    @Test
    public void test_resolveObjectNameIfNeeds_pmb_nonPrefix() {
        // ## Arrange ##
        SqlFileNameResolver resolver = new SqlFileNameResolver();
        String className = SqlFileNameResolver.PMB_MARK;
        String fileName = "MemberBhv_SimpleMember.sql";

        // ## Act ##
        String actual = resolver.resolvePmbNameIfNeeds(className, fileName);

        // ## Assert ##
        log(actual);
        assertEquals("SimpleMemberPmb", actual);
    }

    @Test
    public void test_resolveObjectNameIfNeeds_pmb_initCap() {
        // ## Arrange ##
        SqlFileNameResolver resolver = new SqlFileNameResolver();
        String className = SqlFileNameResolver.PMB_MARK;
        String fileName = "MemberBhv_SelectSimpleMember.sql";

        // ## Act ##
        String actual = resolver.resolvePmbNameIfNeeds(className, fileName);

        // ## Assert ##
        log(actual);
        assertEquals("SelectSimpleMemberPmb", actual);
    }

    @Test
    public void test_resolveObjectNameIfNeeds_pmb_noCap() {
        // ## Arrange ##
        SqlFileNameResolver resolver = new SqlFileNameResolver();
        String className = SqlFileNameResolver.PMB_MARK;
        String fileName = "MemberBhv_selectsimplemember.sql";

        // ## Act ##
        String actual = resolver.resolvePmbNameIfNeeds(className, fileName);

        // ## Assert ##
        log(actual);
        assertEquals("SelectsimplememberPmb", actual);
    }

    @Test
    public void test_resolveObjectNameIfNeeds_pmb_fullPath_by_slash() {
        // ## Arrange ##
        SqlFileNameResolver resolver = new SqlFileNameResolver();
        String className = SqlFileNameResolver.PMB_MARK;
        String fileName = "foo/bar/MemberBhv_selectSimpleMember.sql";

        // ## Act ##
        String actual = resolver.resolvePmbNameIfNeeds(className, fileName);

        // ## Assert ##
        log(actual);
        assertEquals("SimpleMemberPmb", actual);
    }

    @Test
    public void test_resolveObjectNameIfNeeds_pmb_fullPath_by_backSlash() {
        // ## Arrange ##
        SqlFileNameResolver resolver = new SqlFileNameResolver();
        String className = SqlFileNameResolver.PMB_MARK;
        String fileName = "foo\\bar\\MemberBhv_selectSimpleMember.sql";

        // ## Act ##
        String actual = resolver.resolvePmbNameIfNeeds(className, fileName);

        // ## Assert ##
        log(actual);
        assertEquals("SimpleMemberPmb", actual);
    }

    @Test
    public void test_resolveObjectNameIfNeeds_pmb_no_BehaviorQueryPath() {
        // ## Arrange ##
        SqlFileNameResolver resolver = new SqlFileNameResolver();
        String className = SqlFileNameResolver.PMB_MARK;

        // ## Act ##
        try {
            resolver.resolvePmbNameIfNeeds(className, "Mem_selectSimpleMember.sql");

            // ## Assert ##
            fail();
        } catch (IllegalStateException e) {
            // OK
            log(e.getMessage());
        }

        // ## Act ##
        try {
            resolver.resolvePmbNameIfNeeds(className, "selectSimpleMember.sql");

            // ## Assert ##
            fail();
        } catch (IllegalStateException e) {
            // OK
            log(e.getMessage());
        }
    }

    @Test
    public void test_resolveObjectNameIfNeeds_pmb_no_SQLFile() {
        // ## Arrange ##
        SqlFileNameResolver resolver = new SqlFileNameResolver();
        String className = SqlFileNameResolver.PMB_MARK;

        // ## Act ##
        try {
            resolver.resolvePmbNameIfNeeds(className, "MemberBhv_selectSimpleMember");

            // ## Assert ##
            fail();
        } catch (IllegalStateException e) {
            // OK
            log(e.getMessage());
        }

        // ## Act ##
        try {
            resolver.resolvePmbNameIfNeeds(className, "MemberBhv_selectSimpleMember");

            // ## Assert ##
            fail();
        } catch (IllegalStateException e) {
            // OK
            log(e.getMessage());
        }
    }

    // ===================================================================================
    //                                                                           Irregular
    //                                                                           =========
    @Test
    public void test_resolveObjectNameIfNeeds_nonTarget() {
        // ## Arrange ##
        SqlFileNameResolver resolver = new SqlFileNameResolver();
        String className = "NormalName";
        String fileName = "MemberBhv_selectSimpleMember.sql";

        // ## Act ##
        String actual = resolver.resolveEntityNameIfNeeds(className, fileName);

        // ## Assert ##
        log(actual);
        assertEquals("NormalName", actual);
    }
}
