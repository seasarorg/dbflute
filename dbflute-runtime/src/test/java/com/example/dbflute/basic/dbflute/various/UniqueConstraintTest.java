package com.example.dbflute.basic.dbflute.various;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.exception.EntityAlreadyExistsException;
import org.seasar.dbflute.exception.SQLFailureException;

import com.example.dbflute.basic.dbflute.cbean.MemberCB;
import com.example.dbflute.basic.dbflute.exbhv.MemberBhv;
import com.example.dbflute.basic.dbflute.exentity.Member;
import com.example.dbflute.basic.unit.ContainerTestCase;

/**
 * @author jflute
 * @since 0.7.7 (2008/07/23 Wednesday)
 */
public class UniqueConstraintTest extends ContainerTestCase {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final String MY_SQLSTATE = "23001";
    private static final int MY_ERRORCODE = 23001;
    private static final String MY_NOTNULL_SQLSTATE = "90006";
    private static final int MY_NOTNULL_ERRORCODE = 90006;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private MemberBhv memberBhv;
    private final int memberIdTwo = 2;
    private final int memberIdThree = 3;

    // ===================================================================================
    //                                                                   Unique Constraint
    //                                                                   =================
    // -----------------------------------------------------
    //                                                Insert
    //                                                ------
    public void test_insert_unique_constraint_OriginalException_Tx() {
        // ## Arrange ##
        Member member = new Member();
        member.setMemberName("testName");
        member.setMemberAccount("testAccount");
        member.classifyMemberStatusCodeFormalized(); // 正式会員

        // ## Act & Assert ##
        memberBhv.insert(member);
        try {
            memberBhv.insert(member);
            fail();
        } catch (EntityAlreadyExistsException e) {
            SQLException cause = e.getSQLException();
            log(e.getMessage());
            log("/* * * * * * * * * * * * * * * * *");
            log("SQLState=" + cause.getSQLState() + ", ErrorCode=" + cause.getErrorCode());
            log("* * * * * * * * * */");
            assertEquals(MY_SQLSTATE, cause.getSQLState());
            assertEquals(MY_ERRORCODE, cause.getErrorCode());
        }
    }

    public void test_batchInsert_unique_constraint_OriginalException_Tx() {
        // ## Arrange ##
        List<Member> memberList = new ArrayList<Member>();
        {
            Member member = new Member();
            member.setMemberName("testName1");
            member.setMemberAccount("testAccount1");
            member.classifyMemberStatusCodeFormalized(); // 正式会員
            memberList.add(member);
        }
        {
            Member member = new Member();
            member.setMemberName("testName2");
            member.setMemberAccount("testAccount2");
            member.classifyMemberStatusCodeFormalized(); // 正式会員
            memberList.add(member);
        }

        // ## Act & Assert ##
        memberBhv.batchInsert(memberList);
        try {
            memberBhv.batchInsert(memberList);
            fail();
        } catch (EntityAlreadyExistsException e) {
            SQLException cause = e.getSQLException();
            log(e.getMessage());
            log("/* * * * * * * * * * * * * * * * *");
            log("SQLState=" + cause.getSQLState() + ", ErrorCode=" + cause.getErrorCode());
            log("* * * * * * * * * */");
            assertEquals(MY_SQLSTATE, cause.getSQLState());
            assertEquals(MY_ERRORCODE, cause.getErrorCode());
        }
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    public void test_update_unique_constraint_OriginalException_Tx() {
        // ## Arrange ##
        Member member = memberBhv.selectByPKValueWithDeletedCheck(memberIdThree);
        member.setMemberAccount("Pixy");

        // ## Act & Assert ##
        try {
            memberBhv.update(member);
            fail();
        } catch (EntityAlreadyExistsException e) {
            SQLException cause = e.getSQLException();
            log(e.getMessage());
            log("/* * * * * * * * * * * * * * * * *");
            log("SQLState=" + cause.getSQLState() + ", ErrorCode=" + cause.getErrorCode());
            log("* * * * * * * * * */");
            assertEquals(MY_SQLSTATE, cause.getSQLState());
            assertEquals(MY_ERRORCODE, cause.getErrorCode());
        }
    }

    public void test_updateNonstrict_unique_constraint_OriginalException_Tx() {
        // ## Arrange ##
        Member member = new Member();
        member.setMemberId(memberIdThree);
        member.setMemberAccount("Pixy");

        // ## Act & Assert ##
        try {
            memberBhv.updateNonstrict(member);
            fail();
        } catch (EntityAlreadyExistsException e) {
            SQLException cause = e.getSQLException();
            log(e.getMessage());
            log("/* * * * * * * * * * * * * * * * *");
            log("SQLState=" + cause.getSQLState() + ", ErrorCode=" + cause.getErrorCode());
            log("* * * * * * * * * */");
            assertEquals(MY_SQLSTATE, cause.getSQLState());
            assertEquals(MY_ERRORCODE, cause.getErrorCode());
        }
    }

    public void test_queryUpdate_unique_constraint_OriginalException_Tx() {
        // ## Arrange ##
        Member member = new Member();
        member.setMemberAccount("Pixy");

        MemberCB cb = new MemberCB();
        cb.query().setMemberName_PrefixSearch("S");

        // ## Act & Assert ##
        try {
            memberBhv.queryUpdate(member, cb);
            fail();
        } catch (EntityAlreadyExistsException e) {
            SQLException cause = e.getSQLException();
            log(e.getMessage());
            log("/* * * * * * * * * * * * * * * * *");
            log("SQLState=" + cause.getSQLState() + ", ErrorCode=" + cause.getErrorCode());
            log("* * * * * * * * * */");
            assertEquals(MY_SQLSTATE, cause.getSQLState());
            assertEquals(MY_ERRORCODE, cause.getErrorCode());
        }
    }

    public void test_batchUpdate_unique_constraint_OriginalException_Tx() {
        // ## Arrange ##
        List<Member> memberList = new ArrayList<Member>();
        {
            Member member = memberBhv.selectByPKValueWithDeletedCheck(memberIdTwo);
            member.setMemberAccount("AAA");
            memberList.add(member);
        }
        {
            Member member = memberBhv.selectByPKValueWithDeletedCheck(memberIdThree);
            member.setMemberAccount("Pixy");
            memberList.add(member);
        }

        // ## Act & Assert ##
        try {
            memberBhv.batchUpdate(memberList);
            fail();
        } catch (EntityAlreadyExistsException e) {
            SQLException cause = e.getSQLException();
            log(e.getMessage());
            log("/* * * * * * * * * * * * * * * * *");
            log("SQLState=" + cause.getSQLState() + ", ErrorCode=" + cause.getErrorCode());
            log("* * * * * * * * * */");
            assertEquals(MY_SQLSTATE, cause.getSQLState());
            assertEquals(MY_ERRORCODE, cause.getErrorCode());
        }
    }

    public void test_batchUpdateNonstrict_unique_constraint_OriginalException_Tx() {
        // ## Arrange ##
        List<Member> memberList = new ArrayList<Member>();
        {
            Member member = memberBhv.selectByPKValueWithDeletedCheck(memberIdTwo);
            member.setMemberAccount("AAA");
            memberList.add(member);
        }
        {
            Member member = memberBhv.selectByPKValueWithDeletedCheck(memberIdThree);
            member.setMemberAccount("Pixy");
            memberList.add(member);
        }

        // ## Act & Assert ##
        try {
            memberBhv.batchUpdateNonstrict(memberList);
            fail();
        } catch (EntityAlreadyExistsException e) {
            SQLException cause = e.getSQLException();
            log(e.getMessage());
            log("/* * * * * * * * * * * * * * * * *");
            log("SQLState=" + cause.getSQLState() + ", ErrorCode=" + cause.getErrorCode());
            log("* * * * * * * * * */");
            assertEquals(MY_SQLSTATE, cause.getSQLState());
            assertEquals(MY_ERRORCODE, cause.getErrorCode());
        }
    }

    // -----------------------------------------------------
    //                                               NotNull
    //                                               -------
    public void test_insert_notnull_constraint_OriginalException_Tx() {
        // ## Arrange ##
        Member member = new Member();
        member.setMemberName("testName");
        member.setMemberAccount("testAccount");

        // ## Act & Assert ##
        try {
            memberBhv.insert(member);
            fail();
        } catch (SQLFailureException e) {
            SQLException cause = e.getSQLException();
            log(e.getMessage());
            log("/* * * * * * * * * * * * * * * * *");
            log("SQLState=" + cause.getSQLState() + ", ErrorCode=" + cause.getErrorCode());
            log("* * * * * * * * * */");
            assertEquals(MY_NOTNULL_SQLSTATE, cause.getSQLState());
            assertEquals(MY_NOTNULL_ERRORCODE, cause.getErrorCode());
        }
    }

    // -----------------------------------------------------
    //                                           Foreign Key
    //                                           -----------
    public void test_insert_foreign_constraint_OriginalException_Tx() {
        // ## Arrange ##
        Member member = new Member();
        member.setMemberName("testName");
        member.setMemberAccount("testAccount");
        member.setMemberStatusCode("NO_EXIST");

        // ## Act & Assert ##
        try {
            memberBhv.insert(member);
            fail();
        } catch (SQLFailureException e) {
            SQLException cause = e.getSQLException();
            log(e.getMessage());
            log("/* * * * * * * * * * * * * * * * *");
            log("SQLState=" + cause.getSQLState() + ", ErrorCode=" + cause.getErrorCode());
            log("* * * * * * * * * */");
        }
    }
}
