package com.example.dbflute.basic.dbflute.howto.jp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.bhv.ConditionBeanSetupper;
import org.seasar.dbflute.bhv.EntityListSetupper;
import org.seasar.dbflute.bhv.LoadReferrerOption;
import org.seasar.dbflute.cbean.ListResultBean;
import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.dbflute.cbean.grouping.GroupingOption;
import org.seasar.dbflute.cbean.grouping.GroupingRowEndDeterminer;
import org.seasar.dbflute.cbean.grouping.GroupingRowResource;
import org.seasar.dbflute.cbean.grouping.GroupingRowSetupper;
import org.seasar.dbflute.cbean.pagenavi.group.PageGroupOption;
import org.seasar.dbflute.cbean.pagenavi.range.PageRangeOption;
import org.seasar.dbflute.exception.BindVariableCommentNotFoundPropertyException;
import org.seasar.dbflute.exception.IfCommentNotBooleanResultException;
import org.seasar.dbflute.exception.IfCommentWrongExpressionException;
import org.seasar.dbflute.exception.OutsideSqlNotFoundException;
import org.seasar.dbflute.jdbc.StatementConfig;

import com.example.dbflute.basic.dbflute.allcommon.CDef;
import com.example.dbflute.basic.dbflute.cbean.MemberCB;
import com.example.dbflute.basic.dbflute.cbean.MemberLoginCB;
import com.example.dbflute.basic.dbflute.cbean.MemberStatusCB;
import com.example.dbflute.basic.dbflute.cbean.PurchaseCB;
import com.example.dbflute.basic.dbflute.exbhv.MemberBhv;
import com.example.dbflute.basic.dbflute.exbhv.MemberStatusBhv;
import com.example.dbflute.basic.dbflute.exbhv.PurchaseBhv;
import com.example.dbflute.basic.dbflute.exbhv.pmbean.PurchaseSummaryMemberPmb;
import com.example.dbflute.basic.dbflute.exbhv.pmbean.ResolvedPackageNamePmb;
import com.example.dbflute.basic.dbflute.exbhv.pmbean.SimpleMemberPmb;
import com.example.dbflute.basic.dbflute.exbhv.pmbean.UnpaidSummaryMemberPmb;
import com.example.dbflute.basic.dbflute.exentity.Member;
import com.example.dbflute.basic.dbflute.exentity.MemberLogin;
import com.example.dbflute.basic.dbflute.exentity.MemberStatus;
import com.example.dbflute.basic.dbflute.exentity.Product;
import com.example.dbflute.basic.dbflute.exentity.Purchase;
import com.example.dbflute.basic.dbflute.exentity.customize.SimpleMember;
import com.example.dbflute.basic.unit.ContainerTestCase;

/**
 * Behaviorの上級編Example実装。
 * <pre>
 * ターゲットは以下の通り：
 *   o DBFluteをとことん極めたい方
 *   o 開発中で難しい要求が出てきて実現方法がわからなくて困っている方
 * 
 * コンテンツは以下の通り：
 *   o ページング検索のページングナビゲーション要素: selectPage(), pageRange(), pageGroup().
 *   o one-to-many-to-one(子テーブルの親テーブル)の取得: loadXxxList().
 *   o one-to-many-to-many(子テーブルの子テーブル(孫テーブル))の取得: loadXxxList().
 *   o many-to-one-to-many(親テーブルの子テーブル(兄弟テーブル))の取得: pulloutXxx(), loadXxxList().
 *   o バッチ登録: batchInsert().
 *   o 排他制御ありバッチ更新: batchUpdate().
 *   o 排他制御なしバッチ更新: batchUpdateNonstrict().
 *   o 排他制御ありバッチ削除: batchDelete().
 *   o 排他制御なしバッチ削除: batchDeleteNonstrict().
 *   o 外だしSQLでBehaviorQueryPathのSubDirectory機能を利用: PATH_xxx_selectXxx.
 *   o 外だしSQLでカーソルの使った検索(大量件数対策): outsideSql().cursorHandling().selectCursor().
 *   o 外だしSQLでStatementのコンフィグを設定: outsideSql().configure(statementConfig).
 *   o 外だしSQLでParameterBeanプロパティのPackage解決: '-- !!Date xxx!!'.
 *   o 外だしSQLでSQLファイルが見つからないときの挙動とメッセージ: OutsideSqlNotFoundException.
 *   o 外だしSQLでBooleanでないIFコメントの場合の挙動と専用メッセージ: IfCommentNotBooleanResultException.
 *   o 外だしSQLで間違ったIFコメントの場合の挙動と専用メッセージ: IfCommentWrongExpressionException.
 *   o 共通カラムの自動設定を無視して明示的に登録(or 更新): disableCommonColumnAutoSetup().
 *   o Entityリストの件数ごとのグルーピング: ListResultBean.groupingList().
 *   o Entityリストのグルーピング(コントロールブレイク): ListResultBean.groupingList(), determine().
 * </pre>
 * @author jflute
 * @since 0.7.3 (2008/05/31 Saturday)
 */
public class BehaviorPlatinumTest extends ContainerTestCase {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The behavior of Member. (Injection Object) */
    private MemberBhv memberBhv;

    /** The behavior of MemberStatus. (Injection Object) */
    private MemberStatusBhv memberStatusBhv;

    /** The behavior of Purchase. (Injection Object) */
    private PurchaseBhv purchaseBhv;

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * ページング検索のページングナビゲーション要素: selectPage(), pageRange(), pageGroup(). 
     * 会員名称の昇順のリストを「１ページ４件」の「２ページ目」と「３ページ目」を検索。
     * <pre>
     * もし総ページ数が膨大な場合に、画面にて全てのページ番号のLinkを表示するのはあまり格好良くない。
     * 
     * [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29...]
     * 
     * その場合に「ある限られたページ番号のみ」を表示するためにDBFluteが幾つかの手法を提供している。
     * 
     * {PageRange}
     * 前後nページだけを表示する。
     *   ex) 総ページ数が「22」の場合で、前後「5」ページを表示
     *     [4]Page目を選択  - [1 2 3 '4' 5 6 7 8 9 次へ]
     *     [13]Page目を選択 - [前へ 8 9 10 11 12 '13' 14 15 16 17 18 次へ]
     *     [21]Page目を選択 - [前へ 16 17 18 19 20 '21' 22]
     * 
     * {PageRange-fillLimit}
     * 前後nページだけを表示するが、固定数表示とする。
     * 端の方のページが選択されている場合で表示していないページ数の分、反対側のページを表示する。
     * デザインが崩れにくいという特徴あり。
     *   ex) 総ページ数が「22」の場合で、前後「5」ページを表示で固定数表示
     *     [4]Page目を選択  - [1 2 3 '4' 5 6 7 8 9 10 11 次へ]
     *     [13]Page目を選択 - [前へ 8 9 10 11 12 '13' 14 15 16 17 18 次へ]
     *     [21]Page目を選択 - [前へ 12 13 14 15 16 17 18 19 20 '21' 22]
     * 
     * {PageGroup}
     * まとまったページ数で一つグループとみなして表示する。
     * そのグループ内のどのページが選択されても表示は変わらない。
     *   ex) 総ページ数が「22」の場合で、「10」ページで一つのグループ
     *     [4]Page目を選択  - [1 2 3 '4' 5 6 7 8 9 10 11 次へ]
     *     [13]Page目を選択 - [前へ 11 12 '13' 14 15 16 17 18 19 20 次へ]
     *     [21]Page目を選択 - [前へ '21' 22]
     * </pre>
     */
    public void test_selectPage_PageRangeOption_PageGroupOption_Tx() {
        // ## Arrange ##
        MemberCB cb = new MemberCB();
        cb.query().addOrderBy_MemberName_Asc();

        // ## Act ##
        cb.paging(4, 2);
        PagingResultBean<Member> page2 = memberBhv.selectPage(cb);
        cb.paging(4, 3);
        PagingResultBean<Member> page3 = memberBhv.selectPage(cb);

        // ## Assert ##
        assertNotSame(0, page3.size());

        log("{PageRange}");
        {
            PageRangeOption pageRangeOption = new PageRangeOption();
            pageRangeOption.setPageRangeSize(2);
            page2.setPageRangeOption(pageRangeOption);
            {
                boolean existsPre = page2.pageRange().isExistPrePageRange();
                boolean existsNext = page2.pageRange().isExistNextPageRange();
                log("    page2: " + existsPre + " " + page2.pageRange().createPageNumberList() + " " + existsNext);
            }
            page3.setPageRangeOption(pageRangeOption);
            {
                boolean existsPre = page3.pageRange().isExistPrePageRange();
                boolean existsNext = page3.pageRange().isExistNextPageRange();
                log("    page3: " + existsPre + " " + page3.pageRange().createPageNumberList() + " " + existsNext);
            }
            log("PagingResultBean.toString():" + getLineSeparator() + " " + page2 + getLineSeparator() + " " + page3);
            log("");
        }
        log("{PageRange-fillLimit}");
        {
            PageRangeOption pageRangeOption = new PageRangeOption();
            pageRangeOption.setPageRangeSize(2);
            pageRangeOption.setFillLimit(true);
            page2.setPageRangeOption(pageRangeOption);
            {
                boolean existsPre = page2.pageRange().isExistPrePageRange();
                boolean existsNext = page2.pageRange().isExistNextPageRange();
                log("    page2: " + existsPre + " " + page2.pageRange().createPageNumberList() + " " + existsNext);
            }
            page3.setPageRangeOption(pageRangeOption);
            {
                boolean existsPre = page3.pageRange().isExistPrePageRange();
                boolean existsNext = page3.pageRange().isExistNextPageRange();
                log("    page3: " + existsPre + " " + page3.pageRange().createPageNumberList() + " " + existsNext);
            }
            log("PagingResultBean.toString():" + getLineSeparator() + " " + page2 + getLineSeparator() + " " + page3);
            log("");
        }
        log("{PageGroup}");
        {
            PageGroupOption pageGroupOption = new PageGroupOption();
            pageGroupOption.setPageGroupSize(2);
            page2.setPageGroupOption(pageGroupOption);
            {
                boolean existsPre = page2.pageGroup().isExistPrePageGroup();
                boolean existsNext = page2.pageGroup().isExistNextPageGroup();
                log("    page2: " + existsPre + " " + page2.pageGroup().createPageNumberList() + " " + existsNext);
            }
            page3.setPageGroupOption(pageGroupOption);
            {
                boolean existsPre = page3.pageGroup().isExistPrePageGroup();
                boolean existsNext = page3.pageGroup().isExistNextPageGroup();
                log("    page3: " + existsPre + " " + page3.pageGroup().createPageNumberList() + " " + existsNext);
            }
            log("PagingResultBean.toString():" + getLineSeparator() + " " + page2 + getLineSeparator() + " " + page3);
            log("");
        }

        // [Description]
        // A. 必ずsetPageRangeOption()でOptionを設定してからpageRange()を呼び出すこと。
        //    設定する前にpageRange()を呼び出すと例外発生。
        //    (他のOption(PageGroupなど)も同様)
        // 
        // B. PageRangeとPageGroupは独立した機能である。
        //    --> Rangeを使っているときに間違ってGroupのメソッドを呼び出したりしないように注意
    }

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============
    /**
     * one-to-many-to-one(子テーブルの親テーブル)の取得: loadXxxList().
     * 検索した会員リストに対して、会員毎の購入カウントが２件以上の購入リストを購入カウントの降順でロード。
     * その時、購入から辿って商品(子テーブルの親テーブル)も取得。
     */
    public void test_loadReferrer_setupSelect_Foreign_Tx() {
        // ## Arrange ##
        final MemberCB cb = new MemberCB();
        final ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Act ##
        memberBhv.loadPurchaseList(memberList, new ConditionBeanSetupper<PurchaseCB>() {
            public void setup(PurchaseCB cb) {
                cb.setupSelect_Product();// *Point!
                cb.query().addOrderBy_PurchaseCount_Desc();
            }
        });

        // ## Assert ##
        assertNotSame(0, memberList.size());
        for (Member member : memberList) {
            List<Purchase> purchaseList = member.getPurchaseList();
            log("[MEMBER]: " + member.getMemberName());
            for (Purchase purchase : purchaseList) {
                Long purchaseId = purchase.getPurchaseId();
                Product product = purchase.getProduct();// *Point!
                log("    [PURCHASE]: purchaseId=" + purchaseId + ", product=" + product);
                assertNotNull(product);
            }
        }
    }

    /**
     * one-to-many-to-many(子テーブルの子テーブル(孫テーブル))の取得: loadXxxList().
     * この例題は「会員ステータス」を基点テーブルとする。
     */
    public void test_loadReferrer_loadReferrerReferrer_Tx() {
        // ## Arrange ##
        // A base table is MemberStatus at this test case.
        MemberStatusCB cb = new MemberStatusCB();
        ListResultBean<MemberStatus> memberStatusList = memberStatusBhv.selectList(cb);

        LoadReferrerOption<MemberCB, Member> loadReferrerOption = new LoadReferrerOption<MemberCB, Member>();

        // Member
        loadReferrerOption.setConditionBeanSetupper(new ConditionBeanSetupper<MemberCB>() {
            public void setup(MemberCB cb) {
                cb.query().addOrderBy_MemberFormalizedDatetime_Desc();
            }
        });

        // Purchase
        loadReferrerOption.setEntityListSetupper(new EntityListSetupper<Member>() {
            public void setup(List<Member> entityList) {
                memberBhv.loadPurchaseList(entityList, new ConditionBeanSetupper<PurchaseCB>() {
                    public void setup(PurchaseCB cb) {
                        cb.query().addOrderBy_PurchaseCount_Desc();
                        cb.query().addOrderBy_ProductId_Desc();
                    }
                });
            }
        });

        // ## Act ##
        memberStatusBhv.loadMemberList(memberStatusList, loadReferrerOption);

        // ## Assert ##
        boolean existsPurchase = false;
        assertNotSame(0, memberStatusList.size());
        for (MemberStatus memberStatus : memberStatusList) {
            List<Member> memberList = memberStatus.getMemberList();
            log("[MEMBER_STATUS]: " + memberStatus.getMemberStatusName());
            for (Member member : memberList) {
                List<Purchase> purchaseList = member.getPurchaseList();
                log("    [MEMBER]: " + member.getMemberName() + ", " + member.getMemberFormalizedDatetime());
                for (Purchase purchase : purchaseList) {
                    log("        [PURCHASE]: " + purchase.getPurchaseId() + ", " + purchase.getPurchaseCount());
                    existsPurchase = true;
                }
            }
            log("");
        }
        assertTrue(existsPurchase);
    }

    /**
     * many-to-one-to-many(親テーブルの子テーブル(兄弟テーブル))の取得: pulloutXxx(), loadXxxList().
     * この例題は「購入」を基点テーブルとする。
     * 「購入」の親テーブル「会員」の子テーブル「会員ログイン」を取得する。
     * 「会員ログイン」はモバイルフラグがtrueで絞り込んでログイン日時の降順で並べる。
     */
    public void test_LoadReferrer_pulloutMember_loadMemberLoginList_Tx() {
        // ## Arrange ##
        PurchaseCB cb = new PurchaseCB();
        cb.setupSelect_Member();// *Point!
        ListResultBean<Purchase> purchaseList = purchaseBhv.selectList(cb);

        // ## Act ##
        List<Member> memberList = purchaseBhv.pulloutMember(purchaseList);// *Point!
        memberBhv.loadMemberLoginList(memberList, new ConditionBeanSetupper<MemberLoginCB>() {
            public void setup(MemberLoginCB cb) {
                cb.query().setLoginMobileFlg_Equal_True();
                cb.query().addOrderBy_LoginDatetime_Desc();
            }
        });

        // ## Assert ##
        boolean existsMemberLogin = false;
        for (Purchase purchase : purchaseList) {
            Member member = purchase.getMember();
            log("[PURCHASE & MEMBER]: " + purchase.getPurchaseId() + ", " + member.getMemberName());
            List<MemberLogin> memberLoginList = member.getMemberLoginList();
            for (MemberLogin memberLogin : memberLoginList) {
                log("    [MEMBER_LOGIN]: " + memberLogin);
                existsMemberLogin = true;
            }
        }
        assertTrue(existsMemberLogin);

        // [Description]
        // A. pulloutMember()で関連づいてる親テーブルのリストを取得。
        //    - for文で回って取り出しているだけである。
        //    - setupSelect_Xxx()し忘れると空のリストが戻る。
    }

    // ===================================================================================
    //                                                                        Batch Update
    //                                                                        ============
    /**
     * バッチ登録: batchInsert().
     */
    public void test_Batch_batchInsert_Tx() {
        // ## Arrange ##
        List<Member> memberList = new ArrayList<Member>();
        {
            Member member = new Member();
            member.setMemberName("testName1");
            member.setMemberAccount("testAccount1");
            member.classifyMemberStatusCodeFormalized();
            memberList.add(member);
        }
        {
            Member member = new Member();
            member.setMemberName("testName2");
            member.setMemberAccount("testAccount2");
            member.classifyMemberStatusCodeProvisional();
            memberList.add(member);
        }
        {
            Member member = new Member();
            member.setMemberName("testName3");
            member.setMemberAccount("testAccount3");
            member.classifyMemberStatusCodeWithdrawal();
            memberList.add(member);
        }

        // ## Act ##
        int[] result = memberBhv.batchInsert(memberList);

        // ## Assert ##
        assertEquals(3, result.length);

        // [Description]
        // A. PreparedStatement.executeBatch()を利用
        //    --> 大量件数に向いている
    }

    /**
     * 排他制御ありバッチ更新: batchUpdate().
     */
    public void test_Batch_batchUpdate_Tx() {
        // ## Arrange ##
        List<Integer> memberIdList = new ArrayList<Integer>();
        memberIdList.add(1);
        memberIdList.add(3);
        memberIdList.add(7);
        MemberCB cb = new MemberCB();
        cb.query().setMemberId_InScope(memberIdList);
        ListResultBean<Member> memberList = memberBhv.selectList(cb);
        int count = 0;
        List<Long> expectedVersionNoList = new ArrayList<Long>();
        for (Member member : memberList) {
            member.setMemberName("testName" + count);
            member.setMemberAccount("testAccount" + count);
            member.classifyMemberStatusCodeProvisional();
            member.setMemberFormalizedDatetime(currentTimestamp());
            member.setMemberBirthday(currentTimestamp());
            expectedVersionNoList.add(member.getVersionNo());
            ++count;
        }

        // ## Act ##
        int[] result = memberBhv.batchUpdate(memberList);

        // ## Assert ##
        assertEquals(3, result.length);
        List<Long> actualVersionNoList = new ArrayList<Long>();
        for (Member member : memberList) {
            actualVersionNoList.add(member.getVersionNo());
        }
        assertEquals(expectedVersionNoList, actualVersionNoList);

        // [Description]
        // A. PreparedStatement.executeBatch()を利用
        //    --> 大量件数に向いている
        // 
        // B. 全てのカラムが更新される。
        //    --> ModifiedProperties機能はない
        // 
        // C. Oracleは排他制御できない可能性がある
        //    --> OracleのJDBCドライバが結果件数を正常に戻さないため
        //    --> DBFlute-0.7.9より可能になった
        // 
        // D. すれ違いが発生した場合は例外発生。{EntityAlreadyUpdatedException}
        // E. 存在しないPK値を指定された場合はすれ違いが発生した場合と同じ。
        //    --> 排他制御と区別が付かないため
        // 
        // F. 更新後のEntityのVersionNoは更新前と全く同じ値がそのまま保持される。
        // 
        // G. 一意制約違反のときは、EntityAlreadyExistsExceptionが発生。(DBFlute-0.7.7より)
        //   ※JDBCドライバ依存であることに注意
        //   ※UniqueConstraintTest参考
    }

    /**
     * 排他制御なしバッチ更新: batchUpdateNonstrict().
     */
    public void test_Batch_batchUpdateNonstrict_Tx() {
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
            member.setVersionNo(null);// *Point!
            ++count;
        }
        // ## Act ##
        int[] result = memberBhv.batchUpdateNonstrict(memberList);

        // ## Assert ##
        assertEquals(3, result.length);
        for (Member member : memberList) {
            assertNull(member.getVersionNo());
        }

        // [Description]
        // A. PreparedStatement.executeBatch()を利用
        //    --> 大量件数に向いている
        // 
        // B. 存在しないPK値を指定された場合は例外発生。{EntityAlreadyDeletedException}
        // 
        // C. バージョンNOは設定不要(OnQueryでインクリメント「VERSION_NO = VERSION + 1」)
        // member.setVersionNo(versionNo);
        // 
        // D. 更新後のEntityのVersionNoは更新前と全く同じ値がそのまま保持される。
        // 
        // E. 一意制約違反のときは、EntityAlreadyExistsExceptionが発生。(DBFlute-0.7.7より)
        //   ※JDBCドライバ依存であることに注意
        //   ※UniqueConstraintTest参考
    }

    /**
     * 排他制御ありバッチ削除: batchDelete().
     */
    public void test_Batch_batchDelete_Tx() {
        // ## Arrange ##
        deleteMemberReferrers();

        List<Integer> memberIdList = new ArrayList<Integer>();
        memberIdList.add(1);
        memberIdList.add(3);
        memberIdList.add(7);
        MemberCB cb = new MemberCB();
        cb.query().setMemberId_InScope(memberIdList);
        ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Act ##
        int[] result = memberBhv.batchDelete(memberList);

        // ## Assert ##
        assertEquals(3, result.length);

        // [Description]
        // A. PreparedStatement.executeBatch()を利用
        //    --> 大量件数に向いている
        // 
        // B. Oracleは排他制御できない可能性がある
        //    --> OracleのJDBCドライバが結果件数を正常に戻さないため
        // 
        // C. すれ違いが発生した場合は例外発生。{EntityAlreadyUpdatedException}
        // D. 存在しないPK値を指定された場合はすれ違いが発生した場合と同じ。
        //    --> 排他制御と区別が付かないため
    }

    /**
     * 排他制御なしバッチ削除: batchDeleteNonstrict().
     */
    public void test_Batch_batchDeleteNonstrict_Tx() {
        // ## Arrange ##
        deleteMemberReferrers();

        List<Member> memberList = new ArrayList<Member>();
        {
            Member member = new Member();
            member.setMemberId(1);
            memberList.add(member);
        }
        {
            Member member = new Member();
            member.setMemberId(3);
            memberList.add(member);
        }
        {
            Member member = new Member();
            member.setMemberId(7);
            memberList.add(member);
        }

        // ## Act ##
        int[] result = memberBhv.batchDeleteNonstrict(memberList);

        // ## Assert ##
        assertEquals(3, result.length);

        // [Description]
        // A. PreparedStatement.executeBatch()を利用
        //    --> 大量件数に向いている
        // 
        // B. 存在しないPK値を指定された場合は例外発生。{EntityAlreadyDeletedException}
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
    // /- - - - - - - - - - - - - - - - - - - - - - - - - - -
    // 中級的なselectList()に関しては、BehaviorMiddleTestにて
    // - - - - - - - - - -/

    /**
     * 外だしSQLでBehaviorQueryPathのSubDirectory機能を利用: PATH_xxx_selectXxx.
     * exbhv配下の任意のSubDirectory(SubPackage)にSQLファイルを配置して利用することが可能。
     * SQLファイルの数があまりに膨大になる場合は、テーブルのカテゴリ毎にDirectoryを作成して、
     * 適度にSQLファイルをカテゴリ分けすると良い。
     */
    public void test_outsideSql_selectList_selectSubDirectory_Tx() {
        // ## Arrange ##
        // SQLのパス
        String path = MemberBhv.PATH_subdirectory_selectSubDirectoryCheck;

        // 検索条件
        SimpleMemberPmb pmb = new SimpleMemberPmb();
        pmb.setMemberName("S");

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

    // -----------------------------------------------------
    //                                                Cursor
    //                                                ------
    /**
     * 外だしSQLでカーソルの使った検索(大量件数対策): outsideSql().cursorHandling().selectCursor().
     * 実処理は、MemberBhv#makeCsvPurchaseSummaryMember()にて実装しているのでそちらを参照。
     * <pre>
     * Entity定義にて以下のようにオプション「+cursor+」を付けることにより、タイプセーフカーソルが利用可能。
     * -- #PurchaseSummaryMember#
     * -- +cursor+
     * </pre>
     */
    public void test_outsideSql_selectCursor_makeCsvSummaryMember_selectPurchaseSummaryMember_Tx() {
        // ## Arrange ##
        // 検索条件
        PurchaseSummaryMemberPmb pmb = new PurchaseSummaryMemberPmb();
        pmb.setMemberStatusCode_Formalized();// 正式会員

        // ## Act & Assert ##
        memberBhv.makeCsvPurchaseSummaryMember(pmb);
    }

    // -----------------------------------------------------
    //                                      Statement Config
    //                                      ----------------
    /**
     * 外だしSQLでStatementのコンフィグを設定: outsideSql().configure(statementConfig).
     */
    public void test_outsideSql_configure_Tx() {
        // ## Arrange ##
        // SQLのパス
        String path = MemberBhv.PATH_selectSimpleMember;

        // 検索条件
        SimpleMemberPmb pmb = new SimpleMemberPmb();
        pmb.setMemberName("S");

        // 戻り値Entityの型
        Class<SimpleMember> entityType = SimpleMember.class;

        // コンフィグ
        StatementConfig statementConfig = new StatementConfig().typeForwardOnly().queryTimeout(7).maxRows(2);

        // ## Act ##
        // SQL実行！
        List<SimpleMember> memberList = memberBhv.outsideSql().configure(statementConfig).selectList(path, pmb,
                entityType);

        // ## Assert ##
        assertNotSame(0, memberList.size());
        assertEquals(2, memberList.size());
        log("{SimpleMember}");
        for (SimpleMember entity : memberList) {
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

    // -----------------------------------------------------
    //                          ParameterBean ResolvePackage
    //                          ----------------------------
    /**
     * 外だしSQLでParameterBeanプロパティのPackage解決: '-- !!Date xxx!!'.
     * MemberBhv_selectResolvedPackageName.sqlを参照。
     */
    public void test_outsideSql_selectResolvedPackageName_Tx() {
        // ## Arrange ##
        // SQLのパス
        String path = MemberBhv.PATH_various_pmbcheck_selectResolvedPackageName;

        // 検索条件
        ResolvedPackageNamePmb pmb = new ResolvedPackageNamePmb();
        pmb.setDate1(new java.util.Date()); // java.util.Dateで検索できることを確認
        List<String> statusList = new ArrayList<String>();
        statusList.add(CDef.MemberStatus.Formalized.code());
        statusList.add(CDef.MemberStatus.Withdrawal.code());
        pmb.setList1(statusList); // java.util.Listで検索できることを確認

        // 戻り値Entityの型
        Class<Member> entityType = Member.class;

        // ## Act ##ß
        // SQL実行！
        List<Member> memberList = memberBhv.outsideSql().selectList(path, pmb, entityType);

        // ## Assert ##
        assertNotSame(0, memberList.size());

        // [Description]
        // A. ListやDateなど良く利用されるクラスのPackageを自動で解決する。
        //    そのためParameterBeanの宣言でパッケージ名を記述する必要はない。
        //    -- !BigDecimal bigDecimal1! *java.math.BigDecimal
        //    -- !Date bigDecimal1! *java.util.Date
        //    -- !Time bigDecimal1! *java.sql.Time
        //    -- !Timestamp bigDecimal1! *java.sql.Timestamp
        //    -- !List<String> list1! * java.util.List<>
        //    -- !Map<String, String> map1! * java.util.Map<>
    }

    // -----------------------------------------------------
    //                                              NotFound
    //                                              --------
    /**
     * 外だしSQLでSQLファイルが見つからないときの挙動とメッセージ: OutsideSqlNotFoundException.
     * 専用メッセージは開発者がデバッグしやすいように。
     */
    public void test_outsideSql_NotFound_Tx() {
        try {
            memberBhv.outsideSql().selectList("sql/noexist/selectByNoExistSql.sql", null, Member.class);
            fail();
        } catch (OutsideSqlNotFoundException e) {
            log(e.getMessage());
        }
    }

    // -----------------------------------------------------
    //                                         Wrong Comment
    //                                         -------------
    /**
     * 外だしSQLで間違ったバインド変数コメントの場合の挙動と専用メッセージ: BindVariableCommentNotFoundPropertyException.
     * 専用メッセージは開発者がデバッグしやすいように。
     */
    public void test_outsideSql_BindVariableNotFoundProperty_Tx() {
        try {
            String path = MemberBhv.PATH_various_wrongexample_selectBindVariableNotFoundProperty;
            UnpaidSummaryMemberPmb pmb = new UnpaidSummaryMemberPmb();
            pmb.setMemberName("S");
            memberBhv.outsideSql().selectList(path, pmb, Member.class);
            fail();
        } catch (BindVariableCommentNotFoundPropertyException e) {
            log(e.getMessage());
            assertTrue(e.getMessage().contains("wrongMemberId"));
        }
    }

    /**
     * 外だしSQLでBooleanでないIFコメントの場合の挙動と専用メッセージ: IfCommentNotBooleanResultException.
     * 専用メッセージは極力開発者がデバッグしやすいように。
     */
    public void test_outsideSql_IfCommentNotBooleanResult_Tx() {
        try {
            String path = MemberBhv.PATH_various_wrongexample_selectIfCommentNotBooleanResult;
            UnpaidSummaryMemberPmb pmb = new UnpaidSummaryMemberPmb();
            pmb.setMemberName("S");
            memberBhv.outsideSql().selectList(path, pmb, Member.class);
            fail();
        } catch (IfCommentNotBooleanResultException e) {
            log(e.getMessage());
        }
    }

    /**
     * 外だしSQLで間違ったIFコメントの場合の挙動と専用メッセージ: IfCommentWrongExpressionException.
     * 専用メッセージは極力開発者がデバッグしやすいように。
     */
    public void test_outsideSql_IfCommentWrongExpression_Tx() {
        try {
            String path = MemberBhv.PATH_various_wrongexample_selectIfCommentWrongExpression;
            UnpaidSummaryMemberPmb pmb = new UnpaidSummaryMemberPmb();
            pmb.setMemberName("S");
            memberBhv.outsideSql().selectList(path, pmb, Member.class);
            fail();
        } catch (IfCommentWrongExpressionException e) {
            log(e.getMessage());
            if (!e.getMessage().contains("wrongMemberId")) {
                fail();
            }
        }
    }

    // ===================================================================================
    //                                                                       Common Column
    //                                                                       =============
    /**
     * 共通カラムの自動設定を無視して明示的に登録(or 更新): disableCommonColumnAutoSetup().
     */
    public void test_insert_disableCommonColumnAutoSetup_Tx() {
        // ## Arrange ##
        Timestamp expectedTimestamp = new Timestamp(currentTimestamp().getTime() - 10000000000l);
        Member member = new Member();
        member.setMemberName("Billy Joel");
        member.setMemberAccount("martinjoel");
        member.setMemberBirthday(currentDate());
        member.setMemberFormalizedDatetime(currentTimestamp());
        member.classifyMemberStatusCodeFormalized();
        member.setRegisterDatetime(expectedTimestamp);
        member.setRegisterUser("suppressRegisterUser");
        member.setRegisterProcess("suppressRegisterProcess");
        member.setUpdateDatetime(expectedTimestamp);
        member.setUpdateUser("suppressUpdateUser");
        member.setUpdateProcess("suppressUpdateProcess");
        member.disableCommonColumnAutoSetup();// *Point!

        // ## Act ##
        memberBhv.insert(member);

        // ## Assert ##
        final MemberCB cb = new MemberCB();
        cb.acceptPrimaryKeyMapString(member.getDBMeta().extractPrimaryKeyMapString(member));
        final Member actualMember = memberBhv.selectEntityWithDeletedCheck(cb);
        final Timestamp registerDatetime = actualMember.getRegisterDatetime();
        final String registerUser = actualMember.getRegisterUser();
        final String registerProcess = actualMember.getRegisterProcess();
        final Timestamp updateDatetime = actualMember.getUpdateDatetime();
        final String updateUser = actualMember.getUpdateUser();
        final String updateProcess = actualMember.getUpdateProcess();
        log("registerDatetime = " + registerDatetime);
        assertNotNull(registerDatetime);
        assertEquals(expectedTimestamp, registerDatetime);
        log("registerUser = " + registerUser);
        assertNotNull(registerUser);
        assertEquals("suppressRegisterUser", registerUser);
        log("registerProcess = " + registerProcess);
        assertNotNull(registerProcess);
        assertEquals("suppressRegisterProcess", registerProcess);
        log("updateDatetime = " + updateDatetime);
        assertNotNull(updateDatetime);
        assertEquals(expectedTimestamp, updateDatetime);
        log("updateUser = " + updateUser);
        assertNotNull(updateUser);
        assertEquals("suppressUpdateUser", updateUser);
        log("updateProcess = " + updateProcess);
        assertNotNull(updateProcess);
        assertEquals("suppressUpdateProcess", updateProcess);
    }
    
    // ===================================================================================
    //                                                                    List Result Bean
    //                                                                    ================
    /**
     * Entityリストの件数ごとのグルーピング: ListResultBean.groupingList().
     * 会員リストを３件ずつのグループリストにする。
     */
    public void test_selectList_ListResultBean_groupingList_count_Tx() {
        // ## Arrange ##
        MemberCB cb = new MemberCB();
        cb.query().addOrderBy_MemberName_Asc();
        ListResultBean<Member> memberList = memberBhv.selectList(cb);
        log("ListResultBean.toString():" + getLineSeparator() + " " + memberList);
        
        // ## Act ##
        GroupingOption<Member> groupingOption = new GroupingOption<Member>(3);
        List<List<Member>> groupingList = memberList.groupingList(new GroupingRowSetupper<List<Member>, Member>() {
            public List<Member> setup(GroupingRowResource<Member> groupingRowResource) {
                return new ArrayList<Member>(groupingRowResource.getGroupingRowList());
            }
        }, groupingOption);
        
        // ## Assert ##
        assertFalse(groupingList.isEmpty());
        int rowIndex = 0;
        for (List<Member> elementList : groupingList) {
            assertTrue(elementList.size() <= 3);
            log("[" + rowIndex + "]");
            for (Member member : elementList) {
                log("  " + member);
            }
            ++rowIndex;
        }
    }
    
    /**
     * Entityリストのグルーピング(コントロールブレイク): ListResultBean.groupingList(), determine().
     * 会員リストを会員名称の先頭文字ごとのグループリストにする。
     * @since 0.8.2
     */
    public void test_selectList_ListResultBean_groupingList_determine_Tx() {
        // ## Arrange ##
        MemberCB cb = new MemberCB();
        cb.query().addOrderBy_MemberName_Asc();
        ListResultBean<Member> memberList = memberBhv.selectList(cb);
        log("ListResultBean.toString():" + getLineSeparator() + " " + memberList);

        // ## Act ##
        GroupingOption<Member> groupingOption = new GroupingOption<Member>(); // The breakCount is unnecessary in this case.
        groupingOption.setGroupingRowEndDeterminer(new GroupingRowEndDeterminer<Member>() {
            public boolean determine(GroupingRowResource<Member> rowResource, Member nextEntity) {
                Member currentEntity = rowResource.getCurrentEntity();
                String currentInitChar = currentEntity.getMemberName().substring(0, 1);
                String nextInitChar = nextEntity.getMemberName().substring(0, 1);
                return !currentInitChar.equalsIgnoreCase(nextInitChar);
            }
        });
        List<List<Member>> groupingList = memberList.groupingList(new GroupingRowSetupper<List<Member>, Member>() {
            public List<Member> setup(GroupingRowResource<Member> groupingRowResource) {
                return new ArrayList<Member>(groupingRowResource.getGroupingRowList());
            }
        }, groupingOption);
        
        // ## Assert ##
        assertFalse(groupingList.isEmpty());
        int entityCount = 0;
        for (List<Member> elementList : groupingList) {
            String currentInitChar = null;
            for (Member member : elementList) {
                if (currentInitChar == null) {
                    currentInitChar = member.getMemberName().substring(0, 1);
                    log("[" + currentInitChar + "]");
                }
                log("  " + member.getMemberName() + ", " + member);
                assertEquals(currentInitChar, member.getMemberName().substring(0, 1));
                ++entityCount;
            }
        }
        assertEquals(memberList.size(), entityCount);
    }
}
