package com.example.dbflute.basic.dbflute.howto.jp;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.seasar.dbflute.bhv.ConditionBeanSetupper;
import org.seasar.dbflute.cbean.ListResultBean;
import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.dbflute.cbean.ScalarQuery;
import org.seasar.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.dbflute.exception.EntityAlreadyDeletedException;

import com.example.dbflute.basic.dbflute.cbean.MemberCB;
import com.example.dbflute.basic.dbflute.cbean.PurchaseCB;
import com.example.dbflute.basic.dbflute.exbhv.MemberBhv;
import com.example.dbflute.basic.dbflute.exbhv.pmbean.OptionMemberPmb;
import com.example.dbflute.basic.dbflute.exbhv.pmbean.PurchaseMaxPriceMemberPmb;
import com.example.dbflute.basic.dbflute.exbhv.pmbean.SimpleMemberPmb;
import com.example.dbflute.basic.dbflute.exbhv.pmbean.UnpaidSummaryMemberPmb;
import com.example.dbflute.basic.dbflute.exentity.Member;
import com.example.dbflute.basic.dbflute.exentity.Purchase;
import com.example.dbflute.basic.dbflute.exentity.customize.OptionMember;
import com.example.dbflute.basic.dbflute.exentity.customize.PurchaseMaxPriceMember;
import com.example.dbflute.basic.dbflute.exentity.customize.UnpaidSummaryMember;
import com.example.dbflute.basic.unit.ContainerTestCase;

/**
 * Behaviorの中級編Example実装。
 * <pre>
 * ターゲットは以下の通り：
 *   o DBFluteってどういう機能があるのかを探っている方
 *   o DBFluteで実装を開始する方・実装している方
 * 
 * コンテンツは以下の通り：
 *   o ページング検索: selectPage().
 *   o カラムの最大値検索(ScalarSelect): scalarSelect(), max().
 *   o one-to-many検索(LoadReferrer): loadXxxList().
 *   o 一件登録もしくは排他制御ありの一件更新: insertOrUpdate().
 *   o 一件登録もしくは排他制御なし一件更新: insertOrUpdateNonstrict().
 *   o Queryを使った更新: queryUpdate().
 *   o Queryを使った削除: queryDelete().
 *   o 外だしSQLでカラム一つだけのリスト検索: outsideSql().selectList().
 *   o 外だしSQLでエスケープ付き曖昧検索のリスト検索: outsideSql().selectList().
 *   o 外だしSQLの自動ページング検索: outsideSql().autoPaging().selectPage().
 *   o 外だしSQLの手動ページング検索: outsideSql().manualPaging().selectPage().
 *   o 外だしSQLで一件検索: outsideSql().entitnHandling().selectEntity().
 *   o 外だしSQLでチェック付き一件検索: outsideSql().entitnHandling().selectEntityWithDeletedCheck().
 *   o 外だしSQLでカラム一つだけの一件検索: outsideSql().entitnHandling().selectEntity().
 * </pre>
 * @author jflute
 * @since 0.7.3 (2008/05/31 Saturday)
 */
public class BehaviorMiddleTest extends ContainerTestCase {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The behavior of Member. (Injection Component) */
    private MemberBhv memberBhv;

    // ===================================================================================
    //                                                                 Paging(Page Select)
    //                                                                 ===================
    /**
     * ページング検索: selectPage().
     * 会員名称の昇順のリストを「１ページ４件」の「３ページ目」(９件目から１２件目)を検索。
     * <pre>
     * ConditionBean.paging(pageSize, pageNumber)にてページング条件を指定：
     *   o pageSize : ページサイズ(１ページあたりのレコード数)
     *   o pageNumber : ページ番号(検索する対象のページ)
     * 
     * selectPage()だけでページングの基本が全て実行される：
     *   1. ページングなし件数取得
     *   2. ページング実データ検索
     *   3. ページング結果計算処理
     * 
     * PagingResultBeanから様々な要素が取得可能：
     *   o ページングなし総件数
     *   o 現在ページ番号
     *   o 総ページ数
     *   o 前ページの有無判定
     *   o 次ページの有無判定
     *   o ページングナビゲーション要素 ※詳しくはBehaviorPlatinumTestにて
     *   o などなど
     * </pre>
     */
    public void test_selectPage_Tx() {
        // ## Arrange ##
        MemberCB cb = new MemberCB();
        cb.query().addOrderBy_MemberName_Asc();
        cb.paging(4, 3);// The page size is 4 records per 1 page, and The page number is 3.

        // ## Act ##
        PagingResultBean<Member> page3 = memberBhv.selectPage(cb);

        // ## Assert ##
        assertNotSame(0, page3.size());
        log("PagingResultBean.toString():" + getLineSeparator() + " " + page3);
        for (Member member : page3) {
            log(member.toString());
        }
        log("allRecordCount=" + page3.getAllRecordCount());
        log("allPageCount=" + page3.getAllPageCount());
        log("currentPageNumber=" + page3.getCurrentPageNumber());
        log("currentStartRecordNumber=" + page3.getCurrentStartRecordNumber());
        log("currentEndRecordNumber=" + page3.getCurrentEndRecordNumber());
        log("isExistPrePage=" + page3.isExistPrePage());
        log("isExistNextPage=" + page3.isExistNextPage());

        // [Description]
        // A. paging()メソッドはDBFlute-0.7.3よりサポート。
        //    DBFlute-0.7.2までは以下の通り：
        //      fetchFirst(4);
        //      fetchPage(3);
        // 
        // B. paging()メソッド第一引数のpageSizeは、マイナス値や０が指定されると例外発生
        //    --> 基本的にシステムで固定で指定する値であるため
        // 
        // C. paging()メソッド第二引数のpageNumberは、マイナス値や０を指定されると「１ページ目」として扱われる。
        //    --> 基本的に画面リクエストの値で、予期せぬ数値が来る可能性があるため。
        // 
        //    ※関連して、総ページ数を超えるページ番号が指定された場合は「最後のページ」として扱われる。
        //     (但し、ここは厳密にはpaging()の機能ではなくselectPage()の機能である)
    }

    // ===================================================================================
    //                                                                       Scalar Select
    //                                                                       =============
    /**
     * カラムの最大値検索(ScalarSelect): scalarSelect(), max().
     * 正式会員で一番最近(最大)の誕生日を検索。
     * @since 0.8.6
     */
    public void test_scalarSelect_max_Tx() {
        // ## Arrange ##
        MemberCB cb = new MemberCB();
        cb.specify().columnMemberBirthday();
        cb.query().setMemberStatusCode_Equal_Formalized();
        cb.query().setMemberBirthday_IsNotNull();
        cb.query().addOrderBy_MemberBirthday_Desc();
        cb.fetchFirst(1);
        Date expected = memberBhv.selectEntityWithDeletedCheck(cb).getMemberBirthday();

        // ## Act ##
        Date birthday = memberBhv.scalarSelect(Date.class).max(new ScalarQuery<MemberCB>() {
            public void query(MemberCB cb) {
                cb.specify().columnMemberBirthday(); // *Point!
                cb.query().setMemberStatusCode_Equal_Formalized();
            }
        });

        // ## Assert ##
        assertEquals(expected, birthday);

        // [Description]
        // A. max()/min()/sum()/avg()をサポート
        // B. サポートされない型を指定された場合は例外発生(例えばsum()に日付型を指定など)
        // C. コールバックのConditionBeanにて対象のカラムを指定。
        //    --> 必ず「一つだけ」を指定すること(無しもしくは複数の場合は例外発生)
    }

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============
    /**
     * one-to-many検索(LoadReferrer): loadXxxList().
     * 検索した会員リストに対して、会員毎の購入カウントが２件以上の購入リストを購入カウントの降順でロード。
     * 子テーブル(Referrer)を取得する「一発」のSQLを発行してマッピングをする(SubSelectフェッチ)。
     * DBFluteではこのone-to-manyをロードする機能を「LoadReferrer」と呼ぶ。
     */
    public void test_loadReferrer_Tx() {
        // ## Arrange ##
        MemberCB cb = new MemberCB();

        // At first, it selects the list of Member.
        ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Act ##
        // And it loads the list of Purchase with its conditions.
        memberBhv.loadPurchaseList(memberList, new ConditionBeanSetupper<PurchaseCB>() {
            public void setup(PurchaseCB cb) {
                cb.query().setPurchaseCount_GreaterEqual(2);
                cb.query().addOrderBy_PurchaseCount_Desc();
            }
        });

        // ## Assert ##
        assertNotSame(0, memberList.size());
        for (Member member : memberList) {
            log("[MEMBER]: " + member.getMemberName());
            List<Purchase> purchaseList = member.getPurchaseList();// *Point!
            for (Purchase purchase : purchaseList) {
                log("    [PURCHASE]: " + purchase.toString());
            }
        }

        // [SQL]
        // {1}: memberBhv.selectList(cb);
        // select ... 
        //   from MEMBER dflocal
        // 
        // {2}: memberBhv.loadPurchaseList(memberList, ...); 
        // select ... 
        //   from PURCHASE dflocal 
        //  where dflocal.MEMBER_ID in (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
        //    and dflocal.PURCHASE_COUNT >= 2 
        //  order by dflocal.MEMBER_ID asc, dflocal.PURCHASE_COUNT desc

        // [Description]
        // A. 基点テーブルが複合PKの場合はサポートされない。
        //    --> このExampleでは会員テーブル。もし複合PKならloadPurchaseList()メソッド自体が生成されない。
        // B. SubSelectフェッチなので「n+1問題」は発生しない。
        // C. 枝分かれの子テーブルを取得することも可能。
        // D. 子テーブルの親テーブルを取得することも可能。詳しくはBehaviorPlatinumTestにて
        // E. 子テーブルの子テーブル(孫テーブル)を取得することも可能。詳しくはBehaviorPlatinumTestにて
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    // -----------------------------------------------------
    //                                        InsertOrUpdate
    //                                        --------------
    /**
     * 一件登録もしくは排他制御ありの一件更新: insertOrUpdate().
     */
    public void test_insertOrUpdate_Tx() {
        // ## Arrange ##
        Member member = new Member();
        member.setMemberName("testName");
        member.setMemberAccount("testAccount");
        member.classifyMemberStatusCodeFormalized();

        // ## Act ##
        memberBhv.insertOrUpdate(member);
        member.setMemberName("testName2");
        memberBhv.insertOrUpdate(member);

        // ## Assert ##
        MemberCB cb = new MemberCB();
        cb.query().setMemberId_Equal(member.getMemberId());
        Member actual = memberBhv.selectEntityWithDeletedCheck(cb);
        log(actual);
        assertEquals("testName2", actual.getMemberName());

        // [Description]
        // A. 登録処理はinsert()、更新処理はupdate()に委譲する。
        // B. PKの値が存在しない場合は、自動採番と判断し問答無用で登録処理となる。
        // C. PK値が存在する場合は、先に更新処理をしてから結果を判断して登録処理をする。
    }

    /**
     * 一件登録もしくは排他制御なし一件更新: insertOrUpdateNonstrict().
     */
    public void test_insertOrUpdateNonstrict_Tx() {
        // ## Arrange ##
        Member member = new Member();
        member.setMemberName("testName");
        member.setMemberAccount("testAccount");
        member.classifyMemberStatusCodeFormalized();

        // ## Act ##
        memberBhv.insertOrUpdateNonstrict(member);
        member.setMemberName("testName2");
        member.setVersionNo(null);
        memberBhv.insertOrUpdateNonstrict(member);

        // ## Assert ##
        MemberCB cb = new MemberCB();
        cb.query().setMemberId_Equal(member.getMemberId());
        Member actual = memberBhv.selectEntityWithDeletedCheck(cb);
        log(actual);
        assertEquals("testName2", actual.getMemberName());

        // [Description]
        // A. 登録処理はinsert()、更新処理はupdateNonstrict()に委譲する。
        // B. PKの値が存在しない場合は、自動採番と判断し問答無用で登録処理となる。
        // C. PK値が存在する場合は、先に更新処理をしてから結果を判断して登録処理をする。
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Queryを使った更新: queryUpdate().
     * 会員ステータスが正式会員の会員を全て仮会員にする。
     * ConditionBeanで設定した条件で一括削除が可能である。(排他制御はない)
     * @since 0.7.5
     */
    public void test_queryUpdate_Tx() {
        // ## Arrange ##
        Member member = new Member();
        member.classifyMemberStatusCodeProvisional();// 会員ステータスを「仮会員」に
        member.setMemberFormalizedDatetime(null);// 正式会員日時を「null」に

        MemberCB cb = new MemberCB();
        cb.query().setMemberStatusCode_Equal_Formalized();// 正式会員

        // ## Act ##
        int updatedCount = memberBhv.queryUpdate(member, cb);

        // ## Assert ##
        assertNotSame(0, updatedCount);
        MemberCB actualCB = new MemberCB();
        actualCB.query().setMemberStatusCode_Equal_Provisional();
        actualCB.query().setMemberFormalizedDatetime_IsNull();
        actualCB.query().setUpdateUser_Equal(accessUser);// Common Column
        ListResultBean<Member> actualList = memberBhv.selectList(actualCB);
        assertEquals(actualList.size(), updatedCount);

        // [Description]
        // A. 条件として、結合先のカラムによる条件やexists句を使ったサブクエリーなども利用可能。
        // B. setupSelect_Xxx()やaddOrderBy_Xxx()を呼び出しても意味はない。
        // C. 排他制御はせずに問答無用で更新する。(バージョンNOは自動インクリメント)
        // D. 更新結果が0件でも特に例外は発生しない。
        // E. 共通カラム(CommonColumn)の自動設定は有効。
    }

    /**
     * Queryを使った削除: queryDelete().
     * 会員ステータスが正式会員の会員を全て削除する。
     * ConditionBeanで設定した条件で一括削除が可能である。(排他制御はない)
     */
    public void test_queryDelete_Tx() {
        // ## Arrange ##
        deleteMemberReferrers();// for Test

        MemberCB cb = new MemberCB();
        cb.query().setMemberStatusCode_Equal_Formalized();// 正式会員

        // ## Act ##
        int deletedCount = memberBhv.queryDelete(cb);

        // ## Assert ##
        assertNotSame(0, deletedCount);
        assertEquals(0, memberBhv.selectCount(cb));

        // [Description]
        // A. 条件として、結合先のカラムによる条件やexists句を使ったサブクエリーなども利用可能。
        // B. setupSelect_Xxx()やaddOrderBy_Xxx()を呼び出しても意味はない。
        // C. 排他制御はせずに問答無用で削除する。
        // D. 削除結果が0件でも特に例外は発生しない。
    }

    // ===================================================================================
    //                                                                          OutsideSql
    //                                                                          ==========
    // -----------------------------------------------------
    //                                                  List
    //                                                  ----
    // /- - - - - - - - - - - - - - - - - - - - - - - - - - -
    // 基本的なselectList()に関しては、BehaviorBasicTestにて
    // - - - - - - - - - -/

    /**
     * 外だしSQLでカラム一つだけのリスト検索: outsideSql().selectList().
     */
    public void test_outsideSql_selectList_selectMemberName_Tx() {
        // ## Arrange ##
        // SQLのパス
        String path = MemberBhv.PATH_selectMemberName;

        // 検索条件
        SimpleMemberPmb pmb = new SimpleMemberPmb();
        pmb.setMemberName("S");

        // 戻り値Entityの型(String)
        Class<String> entityType = String.class;// *Point!

        // ## Act ##
        // SQL実行！
        List<String> memberNameList = memberBhv.outsideSql().selectList(path, pmb, entityType);

        // ## Assert ##
        assertNotSame(0, memberNameList.size());
        log("{MemberName}");
        for (String memberName : memberNameList) {
            log("    " + memberName);
            assertNotNull(memberName);
            assertTrue(memberName.startsWith("S"));
        }
    }

    // -----------------------------------------------------
    //                                                Option
    //                                                ------
    /**
     * 外だしSQLでエスケープ付き曖昧検索のリスト検索: outsideSql().selectList().
     * <pre>
     * ParameterBeanの定義にて、以下のようにオプション「:like」を付与すると利用可能になる。
     * -- !OptionMemberPmb!
     * -- !!Integer memberId!!
     * -- !!String memberName:like!!
     * </pre>
     */
    public void test_outsideSql_selectList_selectOptionMember_LikeSearchOption_Tx() {
        // ## Arrange ##
        // テストのためにワイルドカード含みのテスト会員を作成
        Member testMember1 = new Member();
        testMember1.setMemberId(1);
        testMember1.setMemberName("ストイコ100%ビッチ_その１");
        memberBhv.updateNonstrict(testMember1);
        Member testMember4 = new Member();
        testMember4.setMemberId(4);
        testMember4.setMemberName("ストイコ100%ビッチ_その４");
        memberBhv.updateNonstrict(testMember4);

        // SQLのパス
        String path = "selectOptionMember";

        // 検索条件
        OptionMemberPmb pmb = new OptionMemberPmb();
        pmb.setMemberName("ストイコ100%ビッチ_その", new LikeSearchOption().likePrefix().escapeByPipeLine());

        // 戻り値Entityの型
        Class<OptionMember> entityType = OptionMember.class;

        // ## Act ##
        // SQL実行！
        List<OptionMember> memberList = memberBhv.outsideSql().selectList(path, pmb, entityType);

        // ## Assert ##
        assertNotSame("テストの成立のため１件以上は必ずあること: " + memberList.size(), 0, memberList.size());
        log("{OptionMember}");
        for (OptionMember member : memberList) {
            Integer memberId = member.getMemberId();
            String memberName = member.getMemberName();
            String memberStatusName = member.getMemberStatusName();
            log("    " + memberId + ", " + memberName + ", " + memberStatusName + " - Formalized="
                    + member.isMemberStatusCodeFormalized());// Sql2EntityでもClassification機能が利用可能
            assertNotNull(memberId);
            assertNotNull(memberName);
            assertNotNull(memberStatusName);
            assertTrue(memberName.startsWith("ストイコ100%ビッチ_その"));
        }

        // [Description]
        // A. 外だしSQLにおいては、LikeSearchOptionはlikeXxx()とescapeByXxx()のみ利用可能。
        // B. CustomizeEntity(Sql2Entityで生成したEntity)でも区分値機能は利用可能。
    }

    // -----------------------------------------------------
    //                                                Paging
    //                                                ------
    /**
     * 外だしSQLの自動ページング検索: outsideSql().autoPaging().selectPage().
     * 未払い合計金額の会員一覧を検索。
     * <pre>
     * ParameterBean.paging(pageSize, pageNumber)にてページング条件を指定：
     *   o pageSize : ページサイズ(１ページあたりのレコード数)
     *   o pageNumber : ページ番号(検索する対象のページ)
     *   
     *   ※SQL上のParameterBeanの定義にて、以下のようにSimplePagingBeanを継承すること。
     *      -- !XxxPmb extends SPB!
     * 
     * selectPage()だけでページングの基本が全て実行される：
     *   1. ページングなし件数取得
     *   2. ページング実データ検索
     *   3. ページング結果計算処理
     * 
     * PagingResultBeanから様々な要素が取得可能：
     *   o ページングなし総件数
     *   o 現在ページ番号
     *   o 総ページ数
     *   o 前ページの有無判定
     *   o 次ページの有無判定
     *   o ページングナビゲーション要素ページリスト
     *   o などなど
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public void test_outsideSql_autoPaging_selectPage_Tx() {
        // ## Arrange ##
        // SQLのパス
        String path = MemberBhv.PATH_selectUnpaidSummaryMember;

        // 検索条件
        UnpaidSummaryMemberPmb pmb = new UnpaidSummaryMemberPmb();
        pmb.setMemberStatusCode_Formalized();// 正式会員

        // 戻り値Entityの型
        Class<UnpaidSummaryMember> entityType = UnpaidSummaryMember.class;

        // ## Act ##
        // SQL実行！
        int pageSize = 3;
        pmb.paging(pageSize, 1);// 1st page
        PagingResultBean<UnpaidSummaryMember> page1 = memberBhv.outsideSql().autoPaging().selectPage(path, pmb,
                entityType);

        pmb.paging(pageSize, 2);// 2st page
        PagingResultBean<UnpaidSummaryMember> page2 = memberBhv.outsideSql().autoPaging().selectPage(path, pmb,
                entityType);

        pmb.paging(pageSize, 3);// 3st page
        PagingResultBean<UnpaidSummaryMember> page3 = memberBhv.outsideSql().autoPaging().selectPage(path, pmb,
                entityType);

        pmb.paging(pageSize, page1.getAllPageCount());// latest page
        PagingResultBean<UnpaidSummaryMember> lastPage = memberBhv.outsideSql().autoPaging().selectPage(path, pmb,
                entityType);

        // ## Assert ##
        showPage(page1, page2, page3, lastPage);
        assertEquals(3, page1.size());
        assertEquals(3, page2.size());
        assertEquals(3, page3.size());
        assertNotSame(page1.get(0).getMemberId(), page2.get(0).getMemberId());
        assertNotSame(page2.get(0).getMemberId(), page3.get(0).getMemberId());
        assertEquals(1, page1.getCurrentPageNumber());
        assertEquals(2, page2.getCurrentPageNumber());
        assertEquals(3, page3.getCurrentPageNumber());
        assertEquals(page1.getAllRecordCount(), page2.getAllRecordCount());
        assertEquals(page2.getAllRecordCount(), page3.getAllRecordCount());
        assertEquals(page1.getAllPageCount(), page2.getAllPageCount());
        assertEquals(page2.getAllPageCount(), page3.getAllPageCount());
        assertFalse(page1.isExistPrePage());
        assertTrue(page1.isExistNextPage());
        assertTrue(lastPage.isExistPrePage());
        assertFalse(lastPage.isExistNextPage());

        // [Description]
        // A. paging()メソッドはDBFlute-0.7.3よりサポート。
        //    DBFlute-0.7.2までは以下の通り：
        //      pmb.fetchFirst(3);
        //      pmb.fetchPage(1);
        // 
        // B. 手動ページングをしたい場合は以下の通り。
        //   1. SQL上でページング絞り条件を記述
        //      /*IF pmb.isPaging()*/
        //      limit /*$pmb.pageStartIndex*/80, /*$pmb.fetchSize*/20
        //      /*END*/
        // 
        //   2. autoPaging()をmanualPaging()に変更
        //      memberBhv.outsideSql().manualPaging().selectPage(...);
        // 
        // C. カーソルタイプは「INSENSITIVE」でスキップ処理でResultSet.absolute()を利用。
        //    --> ループでぐるぐる回すわけではない。*但しカーソルをサポートしているDBのみ
        // 
        // D. ParameterBeanでも区分値機能は利用可能 {上級編}
        //    : member.isMemberStatusCodeFormalized()
        // 
        //    ParameterBeanにて
        //    -- !!String memberStatusCode:cls(MemberStatus)!!
        //    と指定
        // 
        // X. ConditionBeanと外だしSQLの境目ポイント！ {上級編}
        //    o Select句の相関サブクエリで子テーブルのsum()はConditionBeanで可能
        //      --> SpecifyDerivedReferrerを利用(詳しくはConditionBeanPlatinumTestにて)
        // 
        //    o Select句の相関サブクエリで導出した値で並び替えるのはConditionBeanで可能
        //      --> SpecifyDerivedReferrerで利用(詳しくはConditionBeanPlatinumTestにて)
        // 
        //    o Where句の相関サブクエリで子テーブルの条件で絞り込みをするのはConditionBeanで可能
        //      --> ExistsSubQueryを利用(詳しくはConditionBeanMiddleTestにて)
        // 
        //    o Select句の相関サブクエリで導出した値で絞り込みをするのは外だしSQL
        //      --> 今回の例題では導出値を利用した絞り込みはやっていない
        //      
        //        ※実はこの例題のSQLはConditionBeanで実装可能
        //        /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
        //        MemberCB cb = new MemberCB();
        //        cb.setupSelect_MemberStatus();
        //        cb.specify().derivedPurchaseList().sum(new SubQuery<PurchaseCB>() {
        //            public void query(PurchaseCB subCB) {
        //                subCB.specify().columnPurchasePrice();
        //                subCB.query().setPaymentCompleteFlg_Equal_False();
        //            }
        //        }, "UNPAID_PRICE_SUMMARY");
        //        cb.query().setMemberId_Equal(3);
        //        cb.query().setMemberName_PrefixSearch("S");
        //        cb.query().setMemberStatusCode_NotEqual_Formalized();
        //        cb.query().existsPurchaseList(new SubQuery<PurchaseCB>() {
        //            public void query(PurchaseCB subCB) {
        //                subCB.query().setPaymentCompleteFlg_Equal_False();
        //            }
        //        });
        //        cb.query().addSpecifiedDerivedOrderBy_Desc("UNPAID_PRICE_SUMMARY");
        //        cb.query().addOrderBy_MemberId_Asc();
        //        cb.paging(3, 1);
        //        PagingResultBean<Member> page = memberBhv.selectPage(cb);
        //        - - - - - - - - - -/
    }

    /**
     * 外だしSQLの手動ページング検索: outsideSql().manualPaging().selectPage().
     * 最大購入価格の会員一覧を検索。
     * <pre>
     * ParameterBean.paging(pageSize, pageNumber)にてページング条件を指定：
     *   o pageSize : ページサイズ(１ページあたりのレコード数)
     *   o pageNumber : ページ番号(検索する対象のページ)
     *   
     *   ※SQL上のParameterBeanの定義にて、以下のようにSimplePagingBeanを継承すること。
     *      -- !XxxPmb extends SPB!
     * 
     * selectPage()だけでページングの基本が全て実行される：
     *   1. ページングなし件数取得
     *   2. ページング実データ検索
     *   3. ページング結果計算処理
     * 
     * PagingResultBeanから様々な要素が取得可能：
     *   o ページングなし総件数
     *   o 現在ページ番号
     *   o 総ページ数
     *   o 前ページの有無判定
     *   o 次ページの有無判定
     *   o ページングナビゲーション要素ページリスト
     *   o などなど
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public void test_outsideSql_manualPaging_selectPage_Tx() {
        // ## Arrange ##
        // SQLのパス
        String path = MemberBhv.PATH_selectPurchaseMaxPriceMember;

        // 検索条件(絞り込み条件は特になし)
        PurchaseMaxPriceMemberPmb pmb = new PurchaseMaxPriceMemberPmb();

        // 戻り値Entityの型
        Class<PurchaseMaxPriceMember> entityType = PurchaseMaxPriceMember.class;

        // ## Act ##
        // SQL実行！
        int pageSize = 3;
        pmb.paging(pageSize, 1);// 1st page
        PagingResultBean<PurchaseMaxPriceMember> page1 = memberBhv.outsideSql().manualPaging().selectPage(path, pmb,
                entityType);

        pmb.paging(pageSize, 2);// 2st page
        PagingResultBean<PurchaseMaxPriceMember> page2 = memberBhv.outsideSql().manualPaging().selectPage(path, pmb,
                entityType);

        pmb.paging(pageSize, 3);// 3st page
        PagingResultBean<PurchaseMaxPriceMember> page3 = memberBhv.outsideSql().manualPaging().selectPage(path, pmb,
                entityType);

        pmb.paging(pageSize, page1.getAllPageCount());// latest page
        PagingResultBean<PurchaseMaxPriceMember> lastPage = memberBhv.outsideSql().manualPaging().selectPage(path, pmb,
                entityType);

        // ## Assert ##
        showPage(page1, page2, page3, lastPage);
        assertEquals(3, page1.size());
        assertEquals(3, page2.size());
        assertEquals(3, page3.size());
        assertNotSame(page1.get(0).getMemberId(), page2.get(0).getMemberId());
        assertNotSame(page2.get(0).getMemberId(), page3.get(0).getMemberId());
        assertEquals(1, page1.getCurrentPageNumber());
        assertEquals(2, page2.getCurrentPageNumber());
        assertEquals(3, page3.getCurrentPageNumber());
        assertEquals(page1.getAllRecordCount(), page2.getAllRecordCount());
        assertEquals(page2.getAllRecordCount(), page3.getAllRecordCount());
        assertEquals(page1.getAllPageCount(), page2.getAllPageCount());
        assertEquals(page2.getAllPageCount(), page3.getAllPageCount());
        assertFalse(page1.isExistPrePage());
        assertTrue(page1.isExistNextPage());
        assertTrue(lastPage.isExistPrePage());
        assertFalse(lastPage.isExistNextPage());

        // [Description]
        // A. paging()メソッドはDBFlute-0.7.3よりサポート。
        //    DBFlute-0.7.2までは以下の通り：
        //      pmb.fetchFirst(3);
        //      pmb.fetchPage(1);
        // 
        // B. 自動ページングをしたい場合は以下の通り。
        //   1. SQL上でページング絞り条件を削除
        //   2. manualPaging()をautoPaging()に変更
        // 
        // X. ConditionBeanと外だしSQLの境目ポイント！ {上級編}
        //    o ページング絞りをSQLで行うのはConditionBeanで可能
        //      --> ConditionBeanのページングはSQLでのページング絞りを実現
        // 
        //    o Select句の相関サブクエリで子テーブルのmax()はConditionBeanで可能
        //      --> SpecifyDerivedReferrerを利用(詳しくはConditionBeanPlatinumTestにて)
        // 
        //    o Select句の相関サブクエリで導出した値で並び替えるのはConditionBeanで可能
        //      --> SpecifyDerivedReferrerで利用(詳しくはConditionBeanPlatinumTestにて)
        // 
        //        ※実はこの例題のSQLはConditionBeanで実装可能
        //        /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
        //        MemberCB cb = new MemberCB();
        //        cb.setupSelect_MemberStatus();
        //        cb.specify().derivedPurchaseList().max(new SubQuery<PurchaseCB>() {
        //            public void query(PurchaseCB subCB) {
        //                subCB.specify().columnPurchasePrice();
        //                subCB.query().setPaymentCompleteFlg_Equal_False();
        //            }
        //        }, "PURCHASE_MAX_PRICE");
        //        cb.query().setMemberId_Equal(3);
        //        cb.query().setMemberName_PrefixSearch("S");
        //        cb.query().addSpecifiedDerivedOrderBy_Desc("PURCHASE_MAX_PRICE");
        //        cb.query().addOrderBy_MemberId_Asc();
        //        cb.paging(3, 1);
        //        PagingResultBean<Member> page = memberBhv.selectPage(cb);
        //        - - - - - - - - - -/
    }

    // -----------------------------------------------------
    //                                                Entity
    //                                                ------
    /**
     * 外だしSQLで一件検索: outsideSql().entitnHandling().selectEntity().
     */
    public void test_outsideSql_selectEntity_selectUnpaidSummaryMember_Tx() {
        // ## Arrange ##
        // SQLのパス
        String path = MemberBhv.PATH_selectUnpaidSummaryMember;

        // 検索条件
        UnpaidSummaryMemberPmb pmb = new UnpaidSummaryMemberPmb();
        pmb.setMemberId(3);

        // 戻り値Entityの型
        Class<UnpaidSummaryMember> entityType = UnpaidSummaryMember.class;

        // ## Act ##
        UnpaidSummaryMember member = memberBhv.outsideSql().entityHandling().selectEntity(path, pmb, entityType);

        // ## Assert ##
        log("unpaidSummaryMember=" + member);
        assertNotNull(member);
        assertEquals(3, member.getMemberId().intValue());

        // [Description]
        // A. 存在しないIDを指定した場合はnullが戻る。
        // B. 結果が複数件の場合は例外発生。{EntityDuplicatedException}
    }

    /**
     * 外だしSQLでチェック付き一件検索: outsideSql().entitnHandling().selectEntityWithDeletedCheck().
     */
    public void test_outsideSql_selectEntityWithDeletedCheck_selectUnpaidSummaryMember_Tx() {
        // ## Arrange ##
        // SQLのパス
        String path = MemberBhv.PATH_selectUnpaidSummaryMember;

        // 検索条件
        UnpaidSummaryMemberPmb pmb = new UnpaidSummaryMemberPmb();
        pmb.setMemberId(99999);

        // 戻り値Entityの型
        Class<UnpaidSummaryMember> entityType = UnpaidSummaryMember.class;

        // ## Act & Assert ##
        try {
            memberBhv.outsideSql().entityHandling().selectEntityWithDeletedCheck(path, pmb, entityType);
            fail();
        } catch (EntityAlreadyDeletedException e) {
            // OK
            log(e.getMessage());
        }

        // 【Description】
        // A. 存在しないIDを指定した場合は例外発生。{EntityAlreadyDeletedException}
        // B. 結果が複数件の場合は例外発生。{EntityDuplicatedException}
    }

    /**
     * 外だしSQLでカラム一つだけの一件検索: outsideSql().entitnHandling().selectEntity().
     */
    public void test_outsideSql_SelectEntityWithDeletedCheck_selectLatestFormalizedDatetime_Tx() {
        // ## Arrange ##
        // SQLのパス
        String path = MemberBhv.PATH_selectLatestFormalizedDatetime;

        // 検索条件
        Object pmb = null;

        // 戻り値Entityの型
        Class<Timestamp> entityType = Timestamp.class;// *Point!

        // ## Act ##
        Timestamp maxValue = memberBhv.outsideSql().entityHandling().selectEntity(path, pmb, entityType);

        // ## Assert ##
        log("maxValue=" + maxValue);
        assertNotNull(maxValue);
    }
}
