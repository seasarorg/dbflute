package com.example.dbflute.basic.dbflute.various;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbflute.cbean.coption.LikeSearchOption;

import com.example.dbflute.basic.dbflute.cbean.MemberCB;
import com.example.dbflute.basic.dbflute.exbhv.MemberBhv;
import com.example.dbflute.basic.dbflute.exbhv.pmbean.MapLikeSearchPmb;
import com.example.dbflute.basic.dbflute.exentity.Member;
import com.example.dbflute.basic.dbflute.exentity.customize.SimpleMember;
import com.example.dbflute.basic.unit.ContainerTestCase;
/**
 * 「諸刃の刃」機能のExample実装。
 * <pre>
 * コンテンツは以下の通り：
 *   o 外だしSQLでMapParameterBeanを利用した検索: new HashMap().
 *   o ParameterBeanのMap型プロパティでLikeSearchOption: setXxxMap(map, likeSearchOption).
 *   o 固定条件one-to-oneの検索: additionalForeignKey, fixedCondition.
 * </pre>
 * ※「諸刃の刃」機能とは、いざってときに役立つが注意深く利用する必要がある機能である。
 * @author jflute
 * @since 0.7.5 (2008/06/26 Thursday)
 */
public class TwoEdgedSwordTest extends ContainerTestCase {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The behavior of Member. (Injection Object) */
    private MemberBhv memberBhv;

    // ===================================================================================
    //                                                                          OutsideSql
    //                                                                          ==========
    // -----------------------------------------------------
    //                                      MapParameterBean
    //                                      ----------------
    /**
     * 外だしSQLでMapParameterBeanを利用した検索: new HashMap().
     * ParameterBeanとしてMap(MapParameterBean)をそのまま利用。
     * <p>
     * キー値を指定しないものはOGNL上null扱いになるが、Boolean値だけは
     * 必ずtrueかfalseかの指定が必須である(OGNLが正常に判定ができないため)。
     * この例題ではBoolean値は存在していない。
     * </p>
     */
    public void test_outsideSql_selectList_selectSimpleMember_UsingMapParameterBean_Tx() {
        // ## Arrange ##
        // SQLのパス
        String path = MemberBhv.PATH_selectSimpleMember;

        // 検索条件
        // - - - - - - - - - - - - - - - - - - - - - - - - - 
        // 通常のParameterBeanではなくMapParameterBeanを利用
        // - - - - - - - - - - - - - - - - - - - - - - - - -
        // SimpleMemberPmb pmb = new SimpleMemberPmb();
        Map<String, Object> pmb = new HashMap<String, Object>();
        pmb.put("memberName", "S");

        // 戻り値Entityの型
        Class<SimpleMember> entityType = SimpleMember.class;

        // ## Act ##
        // SQL実行！
        List<SimpleMember> resultList = memberBhv.outsideSql().selectList(path, pmb, entityType);

        // ## Assert ##
        assertNotSame(0, resultList.size());
        log("{SimpleMember}");
        for (SimpleMember entity : resultList) {
            Integer memberId = entity.getMemberId();
            String memberName = entity.getMemberName();
            String memberStatusName = entity.getMemberStatusName();
            log("    " + memberId + ", " + memberName + ", " + memberStatusName);
            assertNotNull(memberId);
            assertNotNull(memberName);
            assertNotNull(memberStatusName);
            assertTrue(memberName.startsWith("S"));
        }
    }
    
    /**
     * ParameterBeanのMap型プロパティでLikeSearchOption: setXxxMap(map, likeSearchOption).
     * ParameterBeanにMap型のプロパティを定義してLikeSearchOptionを利用。
     */
    public void test_outsideSql_selectList_selectMapLikeSearch_Tx() {
        // ## Arrange ##
        String keyword = "100%ジュース|和歌山_テ";
        String expectedMemberName = "果汁" + keyword + "スト";
        String dummyMemberName = "果汁100パーセントジュース|和歌山Aテスト";

        // escape処理の必要な会員がいなかったので、ここで一時的に登録
        Member escapeMember = new Member();
        escapeMember.setMemberName(expectedMemberName);
        escapeMember.setMemberAccount("temporaryAccount");
        escapeMember.classifyMemberStatusCodeFormalized();
        memberBhv.insert(escapeMember);

        // escape処理をしない場合にHITする会員も登録
        Member nonEscapeOnlyMember = new Member();
        nonEscapeOnlyMember.setMemberName(dummyMemberName);
        nonEscapeOnlyMember.setMemberAccount("temporaryAccount2");
        nonEscapeOnlyMember.classifyMemberStatusCodeFormalized();
        memberBhv.insert(nonEscapeOnlyMember);

        // 一時的に登録した会員が想定しているものかどうかをチェック
        MemberCB checkCB = new MemberCB();

        // *Point!
        checkCB.query().setMemberName_LikeSearch(keyword, new LikeSearchOption().likeContain());
        assertEquals("escapeなしで2件ともHITすること", 2, memberBhv.selectList(checkCB).size());

        // SQLのパス
        String path = MemberBhv.PATH_various_pmbcheck_selectMapLikeSearch;

        // 検索条件
        MapLikeSearchPmb pmb = new MapLikeSearchPmb();
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("memberName", keyword);
        pmb.setConditionMap(conditionMap, new LikeSearchOption().likeContain().escapeByPipeLine());

        // 戻り値Entityの型
        Class<SimpleMember> entityType = SimpleMember.class;

        // ## Act ##
        // SQL実行！
        List<SimpleMember> memberList = memberBhv.outsideSql().selectList(path, pmb, entityType);

        // ## Assert ##
        assertNotNull(memberList);
        assertEquals(1, memberList.size());// このキーワードにHITする人は１人しかいない
        SimpleMember actualMember = memberList.get(0);
        log(actualMember);
        assertEquals(expectedMemberName, actualMember.getMemberName());
    }
}
