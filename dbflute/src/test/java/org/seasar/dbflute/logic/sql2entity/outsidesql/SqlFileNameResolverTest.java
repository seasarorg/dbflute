package org.seasar.dbflute.logic.sql2entity.outsidesql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.seasar.dbflute.logic.sql2entity.analyzer.DfSqlFileNameResolver;
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
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.ENTITY_MARK;
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
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.ENTITY_MARK;
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
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.ENTITY_MARK;
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
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.ENTITY_MARK;
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
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.ENTITY_MARK;
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
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.ENTITY_MARK;
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
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.ENTITY_MARK;
        String fileName = "foo\\bar\\MemberBhv_selectSimpleMember.sql";

        // ## Act ##
        String actual = resolver.resolveEntityNameIfNeeds(className, fileName);

        // ## Assert ##
        log(actual);
        assertEquals("SimpleMember", actual);
    }

    @Test
    public void test_resolveObjectNameIfNeeds_entity_startsWithUnderScore() {
        // ## Arrange ##
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.ENTITY_MARK;
        String fileName = "MemberBhv__selectSimpleMember.sql";

        // ## Act ##
        try {
            resolver.resolveEntityNameIfNeeds(className, fileName);

            // ## Assert ##
            fail();
        } catch (IllegalStateException e) {
            // OK
            log(e.getMessage());
        }
    }

    @Test
    public void test_resolveObjectNameIfNeeds_entity_no_BehaviorQueryPath() {
        // ## Arrange ##
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.ENTITY_MARK;

        // ## Act ##
        String actual = resolver.resolveEntityNameIfNeeds(className, "selectSimpleMember.sql");

        // ## Assert ##
        log(actual);
        assertEquals("SimpleMember", actual);
    }

    @Test
    public void test_resolveObjectNameIfNeeds_entity_no_BehaviorQueryPath_DBSuffix() {
        // ## Arrange ##
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.ENTITY_MARK;

        // ## Act ##
        String actual = resolver.resolveEntityNameIfNeeds(className, "selectSimpleMember_oracle.sql");

        // ## Assert ##
        log(actual);
        assertEquals("SimpleMember", actual);
    }

    @Test
    public void test_resolveObjectNameIfNeeds_entity_no_BehaviorQueryPath_Unsupport() {
        // ## Arrange ##
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.ENTITY_MARK;

        // ## Act ##
        String actual = resolver.resolveEntityNameIfNeeds(className, "Member_selectSimpleMember.sql");

        // ## Assert ##
        log(actual);
        assertEquals("Member", actual);
    }

    @Test
    public void test_resolveObjectNameIfNeeds_entity_no_BehaviorQueryPath_startsWithUnderScore() {
        // ## Arrange ##
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.ENTITY_MARK;

        // ## Act ##
        try {
            resolver.resolveEntityNameIfNeeds(className, "_selectSimpleMember.sql");

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
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.ENTITY_MARK;

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
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.PMB_MARK;
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
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.PMB_MARK;
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
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.PMB_MARK;
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
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.PMB_MARK;
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
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.PMB_MARK;
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
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.PMB_MARK;
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
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.PMB_MARK;
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
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.PMB_MARK;

        // ## Act ##
        String actual = resolver.resolvePmbNameIfNeeds(className, "selectSimpleMember.sql");

        // ## Assert ##
        log(actual);
        assertEquals("SimpleMemberPmb", actual);
    }

    @Test
    public void test_resolveObjectNameIfNeeds_pmb_no_SQLFile() {
        // ## Arrange ##
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = DfSqlFileNameResolver.PMB_MARK;

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
        DfSqlFileNameResolver resolver = new DfSqlFileNameResolver();
        String className = "NormalName";
        String fileName = "MemberBhv_selectSimpleMember.sql";

        // ## Act ##
        String actual = resolver.resolveEntityNameIfNeeds(className, fileName);

        // ## Assert ##
        log(actual);
        assertEquals("NormalName", actual);
    }
}
