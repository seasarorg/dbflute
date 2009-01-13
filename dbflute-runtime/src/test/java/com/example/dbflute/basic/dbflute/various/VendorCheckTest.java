package com.example.dbflute.basic.dbflute.various;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.dbflute.cbean.ListResultBean;
import org.dbflute.cbean.coption.LikeSearchOption;
import org.dbflute.exception.BatchEntityAlreadyUpdatedException;
import org.dbflute.exception.EntityAlreadyDeletedException;
import org.dbflute.exception.EntityAlreadyUpdatedException;

import com.example.dbflute.basic.dbflute.cbean.MemberCB;
import com.example.dbflute.basic.dbflute.cbean.VendorCheckCB;
import com.example.dbflute.basic.dbflute.exbhv.MemberBhv;
import com.example.dbflute.basic.dbflute.exbhv.VendorCheckBhv;
import com.example.dbflute.basic.dbflute.exentity.Member;
import com.example.dbflute.basic.dbflute.exentity.VendorCheck;
import com.example.dbflute.basic.dbflute.exentity.customize.SimpleVendorCheck;
import com.example.dbflute.basic.dbflute.exentity.customize.VendorCheckDecimalSum;
import com.example.dbflute.basic.dbflute.exentity.customize.VendorCheckIntegerSum;
import com.example.dbflute.basic.unit.ContainerTestCase;

/**
 * @author jflute
 * @since 0.6.1 (2008/01/23 Wednesday)
 */
public class VendorCheckTest extends ContainerTestCase {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private VendorCheckBhv vendorCheckBhv;
    private MemberBhv memberBhv;

    // ===================================================================================
    //                                                                         Number Type
    //                                                                         ===========
    /**
     * Numeric型の全てのカラムがBigDecimalであることを確認。<br />
     * NumericのAutoMappingを使っていないのでこのようになるはず。<br />
     * AutoMappingのExampleは「dbflute-oracle-example」を参照
     */
    public void test_Sql2Entity_All_Numeric_is_BigDecimal_Because_of_non_AutoMapping_Tx() {
        final BigDecimal decimalDigit = new SimpleVendorCheck().getDecimalDigit();
        final Integer integerNonDigit = new SimpleVendorCheck().getIntegerNonDigit();
        final BigDecimal decimalDigitSum = new VendorCheckDecimalSum().getDecimalDigitSum();
        final Integer integerNonDigitSum = new VendorCheckIntegerSum().getIntegerNonDigitSum();
        assertNull(decimalDigit);
        assertNull(integerNonDigit);
        assertNull(decimalDigitSum);
        assertNull(integerNonDigitSum);
    }
    // ===================================================================================
    //                                                                        Boolean Type
    //                                                                        ============
    public void test_delete_insert_select_Type_of_Boolean_Tx() {
        // ## Arrange ##
        VendorCheckCB deleteCB = new VendorCheckCB();
        deleteCB.query().setTypeOfBoolean_Equal(true);
        log("deleted(true)=" + vendorCheckBhv.queryDelete(deleteCB));
        deleteCB.query().setTypeOfBoolean_Equal(false);
        log("deleted(false)=" + vendorCheckBhv.queryDelete(deleteCB));

        VendorCheck vendorCheck = new VendorCheck();
        vendorCheck.setVendorCheckId(new Long(8881));
        vendorCheck.setDecimalDigit(new BigDecimal(12.23));
        vendorCheck.setIntegerNonDigit(new Integer(123));
        vendorCheck.setTypeOfText("abc");
        vendorCheck.setTypeOfBoolean(true);

        // ## Act ##
        vendorCheckBhv.insert(vendorCheck);
        {
            VendorCheck twice = new VendorCheck();
            twice.setVendorCheckId(new Long(8882));
            twice.setDecimalDigit(new BigDecimal(13.34));
            twice.setIntegerNonDigit(new Integer(41));
            twice.setTypeOfText("abc");
            twice.setTypeOfBoolean(false);
            vendorCheckBhv.insert(twice);
        }

        // ## Assert ##
        VendorCheckCB cb = new VendorCheckCB();
        cb.query().setTypeOfBoolean_Equal(true);
        VendorCheck actual = vendorCheckBhv.selectEntityWithDeletedCheck(cb);
        log(actual);
        assertEquals(vendorCheck.getVendorCheckId(), actual.getVendorCheckId());
        assertEquals(vendorCheck.getTypeOfBoolean(), actual.getTypeOfBoolean());
    }

    // ===================================================================================
    //                                                              Batch Update Exception
    //                                                              ======================
    public void test_batchUpdate_and_batchDelete_AlreadyUpdated_Tx() {
        // ## Arrange ##
        List<Integer> memberIdList = new ArrayList<Integer>();
        memberIdList.add(1);
        memberIdList.add(3);
        memberIdList.add(7);
        MemberCB cb = new MemberCB();
        cb.query().setMemberId_InScope(memberIdList);
        ListResultBean<Member> memberList = memberBhv.selectList(cb);
        int count = 0;
        for (Member member : memberList) {
            member.setMemberName("testName" + count);
            member.setMemberAccount("testAccount" + count);
            member.classifyMemberStatusCodeProvisional();
            member.setMemberFormalizedDatetime(currentTimestamp());
            member.setMemberBirthday(currentTimestamp());
            if (count == 1) {
                member.setVersionNo(999999999L);
            }
            ++count;
        }

        // ## Act & Assert ##
        try {
            memberBhv.batchUpdate(memberList);
            fail();
        } catch (EntityAlreadyUpdatedException e) {
            // OK
            log(e.getMessage());
            assertTrue(e instanceof BatchEntityAlreadyUpdatedException);
            log("batchUpdateCount=" + ((BatchEntityAlreadyUpdatedException) e).getBatchUpdateCount());
        }
        deleteMemberReferrers();
        try {
            memberBhv.batchDelete(memberList);
            fail();
        } catch (EntityAlreadyUpdatedException e) {
            // OK
            log(e.getMessage());
            assertTrue(e instanceof BatchEntityAlreadyUpdatedException);
            log("batchUpdateCount=" + ((BatchEntityAlreadyUpdatedException) e).getBatchUpdateCount());
        }
    }

    public void test_batchUpdateNonstrict_and_batchDeleteNonstrict_AlreadyDeleted_Tx() {
        // ## Arrange ##
        List<Integer> memberIdList = new ArrayList<Integer>();
        memberIdList.add(1);
        memberIdList.add(3);
        memberIdList.add(7);
        MemberCB cb = new MemberCB();
        cb.query().setMemberId_InScope(memberIdList);
        ListResultBean<Member> memberList = memberBhv.selectList(cb);
        int count = 0;
        for (Member member : memberList) {
            member.setMemberName("testName" + count);
            member.setMemberAccount("testAccount" + count);
            member.classifyMemberStatusCodeProvisional();
            member.setMemberFormalizedDatetime(currentTimestamp());
            member.setMemberBirthday(currentTimestamp());
            if (count == 1) {
                member.setMemberId(9999999);
            }
            ++count;
        }

        // ## Act & Assert ##
        try {
            memberBhv.batchUpdateNonstrict(memberList);
            fail();
        } catch (EntityAlreadyDeletedException e) {
            // OK
            log(e.getMessage());
        }
        deleteMemberReferrers();
        try {
            memberBhv.batchDeleteNonstrict(memberList);
            fail();
        } catch (EntityAlreadyDeletedException e) {
            // OK
            log(e.getMessage());
        }
    }

    // ===================================================================================
    //                                                                     Multiple Thread
    //                                                                     ===============
    public void test_select_on_multiple_thread_Tx() {
        // Try Five Times!
        select_on_multiple_thread();
        select_on_multiple_thread();
        select_on_multiple_thread();
        select_on_multiple_thread();
        select_on_multiple_thread();
    }
    
    protected void select_on_multiple_thread() {
        // ## Arrange ##
        ExecutorService service = Executors.newCachedThreadPool();

        // ## Act & Assert ##
        // Expect no exception!
        Future<?> future1 = service.submit(createTestRunnable());
        Future<?> future2 = service.submit(createTestRunnable());
        Future<?> future3 = service.submit(createTestRunnable());
        Future<?> future4 = service.submit(createTestRunnable());
        Future<?> future5 = service.submit(createTestRunnable());
        Future<?> future6 = service.submit(createTestRunnable());
        Future<?> future7 = service.submit(createTestRunnable());
        Future<?> future8 = service.submit(createTestRunnable());
        Future<?> future9 = service.submit(createTestRunnable());
        Future<?> future10 = service.submit(createTestRunnable());

        while (true) {
            if (isDone(future1, future2, future3, future4, future5, future6, future7, future8, future9, future10)) {
                log("All threads are finished!");
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    protected boolean isDone(Future<?>... futures) {
        for (Future<?> future : futures) {
            if (!future.isDone()) {
                return false;
            }
        }
        return true;
    }

    protected Runnable createTestRunnable() {
        return new Runnable() {
            public void run() {
                MemberCB cb = new MemberCB();
                cb.setupSelect_MemberStatus();
                cb.query().setMemberName_PrefixSearch("S");
                cb.query().addOrderBy_MemberBirthday_Desc().addOrderBy_MemberId_Asc();
                ListResultBean<Member> memberList = memberBhv.selectList(cb);
                assertFalse(memberList.isEmpty());
                for (Member member : memberList) {
                    assertTrue(member.getMemberName().startsWith("S"));
                }
            }
        };
    }

    // ===================================================================================
    //                                                               Like Search Wild Card
    //                                                               =====================
    public void test_LikeSearch_DoubleByte_WildCard_Tx() {
        // ## Arrange ##
        LikeSearchOption option = new LikeSearchOption();
        option.escapeByPipeLine();

        // ## Act & Assert ##
        assertEquals("ABC％CBA", option.generateRealValue("ABC％CBA"));
        assertEquals("ABC＿CBA", option.generateRealValue("ABC＿CBA"));
        assertEquals("ABC％CB|%A", option.generateRealValue("ABC％CB%A"));
        assertEquals("ABC＿CB|_A", option.generateRealValue("ABC＿CB_A"));
    }

    // ===================================================================================
    //                                                                     Optimistic Lock
    //                                                                     ===============
    public void test_VersionNoNotIncremented_after_EntityAlreadUpdated_Tx() {
        // ## Arrange ##
        Member member = new Member();
        member.setMemberId(3);
        member.setMemberName("Test");
        member.setVersionNo(99999L); // The version no is not existing.

        // ## Act ##
        try {
            memberBhv.update(member);
            fail();
        } catch (EntityAlreadyUpdatedException e) {
            // OK
            log(e.getMessage());
            log("member.getVersionNo() = " + member.getVersionNo());
            assertEquals(new Long(99999L), member.getVersionNo());
        }
    }
}
