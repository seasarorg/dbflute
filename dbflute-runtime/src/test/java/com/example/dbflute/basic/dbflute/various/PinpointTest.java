package com.example.dbflute.basic.dbflute.various;

import java.util.Date;
import java.util.List;

import org.dbflute.cbean.ListResultBean;
import org.dbflute.cbean.ScalarQuery;
import org.dbflute.cbean.SubQuery;
import org.dbflute.cbean.UnionQuery;
import org.dbflute.dbmeta.info.ColumnInfo;

import com.example.dbflute.basic.dbflute.bsentity.dbmeta.MemberDbm;
import com.example.dbflute.basic.dbflute.cbean.MemberCB;
import com.example.dbflute.basic.dbflute.cbean.MemberLoginCB;
import com.example.dbflute.basic.dbflute.cbean.MemberStatusCB;
import com.example.dbflute.basic.dbflute.exbhv.MemberBhv;
import com.example.dbflute.basic.dbflute.exbhv.MemberStatusBhv;
import com.example.dbflute.basic.dbflute.exentity.Member;
import com.example.dbflute.basic.dbflute.exentity.MemberStatus;
import com.example.dbflute.basic.unit.ContainerTestCase;

/**
 * @author jflute
 * @since 0.6.0 (2008/01/16 Wednesday)
 */
public class PinpointTest extends ContainerTestCase {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private MemberBhv memberBhv;
    private MemberStatusBhv memberStatusBhv;
    
    // ===================================================================================
    //                                                                          ColumnInfo
    //                                                                          ==========
    public void test_columnInfo_columnAlias_Tx() {
        // ## Arrange & Act ##
        ColumnInfo columnInfo = MemberDbm.getInstance().columnMemberName();

        // ## Assert ##
        assertNotNull(columnInfo);
        assertNotNull(columnInfo.getColumnDbName());
        assertNotNull(columnInfo.getPropertyName());
        assertNotNull(columnInfo.getPropertyType());
        assertNull(columnInfo.getColumnAlias()); // because it does not use alias definition.
    }

    // ===================================================================================
    //                                                              SpecifyDerivedReferrer
    //                                                              ======================
    public void test_derivedReferrer_union_Tx() {
        // ## Arrange ##
        List<Member> expectedList = selectListAllWithLatestLoginDatetime();
        MemberCB cb = new MemberCB();
        cb.specify().derivedMemberLoginList().max(new SubQuery<MemberLoginCB>() {
            public void query(MemberLoginCB subCB) {
                subCB.specify().columnLoginDatetime();
                subCB.query().setLoginMobileFlg_Equal_True();
                subCB.union(new UnionQuery<MemberLoginCB>() {
                    public void query(MemberLoginCB unionCB) {
                        unionCB.query().setLoginMobileFlg_Equal_False();
                    }
                });
            }
        }, "LATEST_LOGIN_DATETIME");
        cb.query().addOrderBy_MemberId_Asc();

        // ## Act ##
        ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        int index = 0;
        for (Member member : memberList) {
            Member expectedMember = expectedList.get(index);
            Date latestLoginDatetime = member.getLatestLoginDatetime();
            log(member.getMemberName() + ", " + latestLoginDatetime);
            assertEquals(expectedMember.getLatestLoginDatetime(), latestLoginDatetime);
            ++index;
        }
    }

    protected List<Member> selectListAllWithLatestLoginDatetime() {
        MemberCB cb = new MemberCB();
        cb.specify().derivedMemberLoginList().max(new SubQuery<MemberLoginCB>() {
            public void query(MemberLoginCB subCB) {
                subCB.specify().columnLoginDatetime();
            }
        }, "LATEST_LOGIN_DATETIME");
        cb.query().addOrderBy_MemberId_Asc();
        return memberBhv.selectList(cb);
    }

    public void test_union_derivedReferrer_Tx() {
        // ## Arrange ##
        MemberCB cb = new MemberCB();
        cb.specify().derivedMemberLoginList().max(new SubQuery<MemberLoginCB>() {
            public void query(MemberLoginCB subCB) {
                subCB.specify().columnLoginDatetime();
            }
        }, "LATEST_LOGIN_DATETIME");
        cb.query().setMemberStatusCode_Equal_Formalized();
        cb.union(new UnionQuery<MemberCB>() {
            public void query(MemberCB unionCB) {
                unionCB.query().setMemberStatusCode_Equal_Provisional();
            }
        });

        // ## Act ##
        ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        for (Member member : memberList) {
            Date latestLoginDatetime = member.getLatestLoginDatetime();
            log(member.getMemberName() + ", " + latestLoginDatetime);
        }
    }

    // ===================================================================================
    //                                                                         ScalarQuery
    //                                                                         ===========
    public void test_scalarQuery_max_union_Tx() {
        // ## Arrange ##
        Date expected = memberBhv.scalarSelect(Date.class).max(new ScalarQuery<MemberCB>() {
            public void query(MemberCB cb) {
                cb.specify().columnMemberBirthday();
            }
        });
        MemberCB cb = new MemberCB();
        cb.query().scalar_Equal().max(new SubQuery<MemberCB>() {
            public void query(MemberCB subCB) {
                subCB.specify().columnMemberBirthday();
                subCB.query().setMemberStatusCode_Equal_Formalized();
                subCB.union(new UnionQuery<MemberCB>() {
                    public void query(MemberCB unionCB) {
                        unionCB.query().setMemberStatusCode_Equal_Provisional();
                    }
                });
                subCB.union(new UnionQuery<MemberCB>() {
                    public void query(MemberCB unionCB) {
                        unionCB.query().setMemberStatusCode_Equal_Withdrawal();
                    }
                });
            }
        });

        // ## Act ##
        ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        for (Member member : memberList) {
            Date memberBirthday = member.getMemberBirthday();
            log(member.getMemberName() + ", " + memberBirthday);
            assertEquals(expected, memberBirthday);
        }
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    public void test_update_NoModified_Tx() {
        // ## Arrange ##
        final MemberStatusCB cb = memberStatusBhv.newMyConditionBean();
        cb.query().setMemberStatusCode_Equal_Formalized();
        final MemberStatus memberStatus = memberStatusBhv.selectEntityWithDeletedCheck(cb);

        // ## Act & Assert ##
        memberStatusBhv.update(memberStatus); // Expect no exception!
    }
}
