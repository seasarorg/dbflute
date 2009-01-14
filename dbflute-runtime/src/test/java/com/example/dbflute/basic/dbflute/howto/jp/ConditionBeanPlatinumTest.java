package com.example.dbflute.basic.dbflute.howto.jp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.seasar.dbflute.bhv.ConditionBeanSetupper;
import org.seasar.dbflute.cbean.ListResultBean;
import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.dbflute.cbean.SubQuery;
import org.seasar.dbflute.cbean.UnionQuery;
import org.seasar.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.dbflute.jdbc.StatementConfig;
import org.seasar.dbflute.util.DfStringUtil;

import com.example.dbflute.basic.dbflute.cbean.MemberAddressCB;
import com.example.dbflute.basic.dbflute.cbean.MemberCB;
import com.example.dbflute.basic.dbflute.cbean.MemberLoginCB;
import com.example.dbflute.basic.dbflute.cbean.MemberStatusCB;
import com.example.dbflute.basic.dbflute.cbean.MemberWithdrawalCB;
import com.example.dbflute.basic.dbflute.cbean.PurchaseCB;
import com.example.dbflute.basic.dbflute.exbhv.MemberBhv;
import com.example.dbflute.basic.dbflute.exbhv.MemberWithdrawalBhv;
import com.example.dbflute.basic.dbflute.exentity.Member;
import com.example.dbflute.basic.dbflute.exentity.MemberAddress;
import com.example.dbflute.basic.dbflute.exentity.MemberWithdrawal;
import com.example.dbflute.basic.dbflute.exentity.Product;
import com.example.dbflute.basic.dbflute.exentity.Purchase;
import com.example.dbflute.basic.unit.ContainerTestCase;

/**
 * ConditionBeanの上級編Example実装。
 * <pre>
 * ターゲットは以下の通り：
 *   o DBFluteをとことん極めたい方
 *   o 開発中で難しい要求が出てきて実現方法がわからなくて困っている方
 * 
 * コンテンツは以下の通り：
 *   o 空白区切りの連結曖昧検索(And条件): option.likeContain().splitBySpace().
 *   o 空白区切りの連結曖昧検索(Or条件): option.likeContain().splitBySpace().asOrSplit().
 *   o Exists句の中でUnion: existsXxxList(), union().
 *   o nullを最初に並べる: addOrderBy_Xxx_Asc().withNullsFirst().
 *   o nullを最後に並べる: addOrderBy_Xxx_Asc().withNullsLast().
 *   o nullを最後に並べる(Unionと共演): addOrderBy_Xxx_Asc().withNullsLast(), union().
 *   o Unionのループによる不定数設定: for { cb.union() }.
 *   o Unionを使ったページング検索: union(), selectPage().
 *   o OnClause(On句)に条件を追加: queryXxx().on().
 *   o 取得カラムの指定(SpecifyColumn): specify().columnXxx().
 *   o 子テーブル導出カラムの指定(SpecifyDerivedReferrer)-Max: specify().derivedXxxList().max().
 *   o 子テーブル導出カラムで並び替え(SpecifiedDerivedOrderBy)-Count: addSpecifiedDerivedOrderBy_Desc().
 *   o 子テーブルカラムの種類数取得(SpecifyDerivedReferrer)-CountDistinct: specify().derivedXxxList().countDistinct().
 *   o 子テーブル導出カラムで絞り込み(QueryDerivedReferrer)-Max: query().derivedXxx().max().greaterEqual().
 *   o 最大値レコードの検索(ScalarSubQuery)-Max: scalar_Equal(), max().
 *   o 固定条件を加えたone-to-oneの取得：fixedCondition, selectSelect_Xxx(target).
 *   o 固定条件を加えたone-to-oneの絞り込み：fixedCondition, queryXxx(target).
 *   o Statementのコンフィグを設定: cb.configure(statementConfig).
 *   o どんなにSubQueryやUnionの連打をしてもSQLが綺麗にフォーマット: toDisplaySql().
 * </pre>
 * @author jflute
 * @since 0.7.3 (2008/06/01 Sunday)
 */
public class ConditionBeanPlatinumTest extends ContainerTestCase {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The behavior of Member. (Injection Object) */
    private MemberBhv memberBhv;

    /** The behavior of MemberWithdrawal. (Injection Object) */
    private MemberWithdrawalBhv memberWithdrawalBhv;

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    // -----------------------------------------------------
    //                                            LikeSearch
    //                                            ----------
    /**
     * 空白区切りの連結曖昧検索(And条件): option.likeContain().splitBySpace().
     * 画面から空白区切りでキーワードを入力して検索する場合などに有効な機能。
     * デフォルトでは「And条件」で連結する。
     */
    public void test_query_LikeSearch_likeContain_splitBySpace_Tx() {
        // ## Arrange ##
        String keyword = "S iko ic";
        MemberCB cb = new MemberCB();

        // *Point!
        LikeSearchOption option = new LikeSearchOption().likeContain().splitBySpace();
        cb.query().setMemberName_LikeSearch(keyword, option);

        // ## Act ##
        List<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        assertNotNull(memberList);
        assertNotSame(0, memberList.size());
        for (Member member : memberList) {
            log("memberName=" + member.getMemberName());
            assertTrue(member.getMemberName().contains("S"));
            assertTrue(member.getMemberName().contains("iko"));
            assertTrue(member.getMemberName().contains("ic"));
        }

        // [Description]
        // A. 全角空白も区切りとしたい場合は、splitBySpaceContainsDoubleByte()を利用。
        // new LikeSearchOption().likeContain().splitBySpaceContainsDoubleByte();
        // 
        // B. スプリットする有効数を限定したい場合は、splitBySpace(int limit)を利用。
        //    --> 指定された数以降の条件要素は無視される
        // new LikeSearchOption().likeContain().splitBySpace(5);
        // 
        // C. Or条件で連結したいときは、splitByXxx()の後に続いてasOrSplit()を呼び出す。
        // new LikeSearchOption().likeContain().splitBySpace().asOrSplit();
    }

    /**
     * 空白区切りの連結曖昧検索(Or条件): option.likeContain().splitBySpace().asOrSplit().
     * 画面から空白区切りでキーワードを入力して検索する場合などに有効な機能。
     * 明示的に指定することによって「Or条件」で連結することができる。
     */
    public void test_query_setMember_LikeSearch_likeContain_splitBySpace_asOrSplit_Tx() {
        // ## Arrange ##
        String keyword = "Sto avi uke";
        MemberCB cb = new MemberCB();

        // *Point!
        LikeSearchOption option = new LikeSearchOption().likeContain().splitBySpace().asOrSplit();
        cb.query().setMemberName_LikeSearch(keyword, option);

        // ## Act ##
        List<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        assertNotNull(memberList);
        assertNotSame(0, memberList.size());
        for (Member member : memberList) {
            String memberName = member.getMemberName();
            log("memberName=" + memberName);
            assertTrue(memberName.contains("Sto") || memberName.contains("avi") || memberName.contains("uke"));
        }

        // [Description]
        // A. 全角空白も区切りとしたい場合は、splitBySpaceContainsDoubleByte()を利用
    }

    // 
    // 以下、LikeSearchで指定された条件値を変換して比較するオプション：
    // 
    // [大文字小文字]
    // new LikeSearchOption().toLowerCase() --> 小文字に変換して比較
    // new LikeSearchOption().toUpperCase() --> 大文字に変換して比較
    // 
    // ※大文字小文字を区別しない検索をする場合は、あらかじめDB上の該当カラムのデータを
    //  大文字統一もしくは小文字統一しておく必要がある。
    //  (大文字小文字を区別しない検索専用のカラムを作成)
    // 
    // [半角変換]
    // new LikeSearchOption().toSingleByteSpace() --> 全角空白を半角空白に変換して比較
    // new LikeSearchOption().toSingleByteAlphabetNumber() --> 全角のアルファベットと数字を半角に変換して比較
    // 
    // [日本語]
    // new LikeSearchOption().localJapanese().removeLastLongVowel() --> 例えば「オーダー」の最後の「ー」を除去して「オーダ」として比較
    // new LikeSearchOption().localJapanese().toDoubleByteKatakana() --> 半角カタカナを全角カタカナに変換して比較
    //

    // -----------------------------------------------------
    //                                        ExistsSubQuery
    //                                        --------------
    /**
     * Exists句の中でUnion: existsXxxList(), union().
     * 商品名称に's'もしくは'a'が含まれる商品を購入したことのある会員を検索。
     */
    public void test_query_exists_union_Tx() {
        final MemberCB cb = new MemberCB();
        final LikeSearchOption option = new LikeSearchOption().likeContain();
        cb.query().existsPurchaseList(new SubQuery<PurchaseCB>() {
            public void query(PurchaseCB purchaseCB) {
                purchaseCB.query().setPurchaseCount_GreaterThan(2);
                purchaseCB.union(new UnionQuery<PurchaseCB>() {
                    public void query(PurchaseCB purchaseUnionCB) {
                        purchaseUnionCB.query().queryProduct().setProductName_LikeSearch("s", option);
                    }
                });
                purchaseCB.union(new UnionQuery<PurchaseCB>() {
                    public void query(PurchaseCB purchaseUnionCB) {
                        purchaseUnionCB.query().queryProduct().setProductName_LikeSearch("a", option);
                    }
                });
            }
        });

        // ## Act ##
        final ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        final ConditionBeanSetupper<PurchaseCB> setuppper = new ConditionBeanSetupper<PurchaseCB>() {
            public void setup(PurchaseCB cb) {
                cb.setupSelect_Product();
            }
        };
        memberBhv.loadPurchaseList(memberList, setuppper);
        for (Member member : memberList) {
            log("[member] " + member.getMemberId() + ", " + member.getMemberName());
            final List<Purchase> purchaseList = member.getPurchaseList();
            boolean existsPurchase = false;
            for (Purchase purchase : purchaseList) {
                final Product product = purchase.getProduct();
                final Integer purchaseCount = purchase.getPurchaseCount();
                final String productName = product.getProductName();
                log("  [purchase] " + purchase.getPurchaseId() + ", " + purchaseCount + ", " + productName);
                if (purchaseCount > 2 || productName.contains("s") || productName.contains("a")) {
                    existsPurchase = true;
                }
            }
            assertTrue(existsPurchase);
        }
    }

    // -----------------------------------------------------
    //                                      Nulls First/Last
    //                                      ----------------
    /**
     * nullを最初に並べる: addOrderBy_Xxx_Asc().withNullsFirst().
     * 生年月日の昇順でnullのものは最初に並べる。
     */
    public void test_query_addOrderBy_withNullsFirst_Tx() {
        // ## Arrange ##
        final MemberCB cb = new MemberCB();
        cb.query().addOrderBy_MemberBirthday_Asc().withNullsFirst();

        // ## Act ##
        final ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        boolean nulls = true;
        boolean border = false;
        for (Member member : memberList) {
            final Date birthday = member.getMemberBirthday();
            log(member.getMemberId() + ", " + member.getMemberName() + ", " + birthday);
            if (nulls) {
                if (birthday != null && !border) {
                    nulls = false;
                    border = true;
                    continue;
                }
                assertNull(birthday);
            } else {
                assertNotNull(birthday);
            }
        }
        assertTrue(border);
    }

    /**
     * nullを最後に並べる: addOrderBy_Xxx_Asc().withNullsLast().
     * 生年月日の昇順でnullのものは最後に並べる。
     */
    public void test_query_addOrderBy_withNullsLast_Tx() {
        // ## Arrange ##
        final MemberCB cb = new MemberCB();
        cb.query().addOrderBy_MemberBirthday_Asc().withNullsLast();

        // ## Act ##
        final ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        boolean nulls = false;
        boolean border = false;
        for (Member member : memberList) {
            final Date birthday = member.getMemberBirthday();
            log(member.getMemberId() + ", " + member.getMemberName() + ", " + birthday);
            if (nulls) {
                assertNull(birthday);
            } else {
                if (birthday == null && !border) {
                    nulls = true;
                    border = true;
                    continue;
                }
                assertNotNull(birthday);
            }
        }
        assertTrue(border);
    }

    /**
     * nullを最後に並べる(Unionと共演): addOrderBy_Xxx_Asc().withNullsLast(), union().
     * 生年月日のあるなしでUnionでがっちゃんこして、生年月日の昇順でnullのものは最後に並べる。
     */
    public void test_query_addOrderBy_withNullsLast_and_union_Tx() {
        // ## Arrange ##
        final MemberCB cb = new MemberCB();
        cb.query().setMemberBirthday_IsNull();
        cb.union(new UnionQuery<MemberCB>() {
            public void query(MemberCB unionCB) {
                unionCB.query().setMemberBirthday_IsNotNull();
            }
        });
        cb.query().addOrderBy_MemberBirthday_Asc().withNullsLast();

        // ## Act ##
        final ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        boolean nulls = false;
        boolean border = false;
        for (Member member : memberList) {
            final Date birthday = member.getMemberBirthday();
            log(member.getMemberId() + ", " + member.getMemberName() + ", " + birthday);
            if (nulls) {
                assertNull(birthday);
            } else {
                if (birthday == null && !border) {
                    nulls = true;
                    border = true;
                    continue;
                }
                assertNotNull(birthday);
            }
        }
        assertTrue(border);
    }

    // ===================================================================================
    //                                                                               Union
    //                                                                               =====
    /**
     * Unionのループによる不定数設定: for { cb.union() }.
     */
    public void test_selectList_union_LoopIndefiniteSetting_Tx() {
        // ## Arrange ##
        String keywordDelimiterString = "S M D";
        List<String> keywordList = Arrays.asList(keywordDelimiterString.split(" "));

        MemberCB cb = new MemberCB();
        cb.setupSelect_MemberStatus();

        final LikeSearchOption prefixOption = new LikeSearchOption().likePrefix();
        boolean first = true;
        for (final String keyword : keywordList) {
            if (first) {
                cb.query().setMemberAccount_LikeSearch(keyword, prefixOption);
                first = false;
                continue;
            }
            cb.union(new UnionQuery<MemberCB>() {
                public void query(MemberCB unionCB) {
                    unionCB.query().setMemberAccount_LikeSearch(keyword, prefixOption);
                }
            });
        }

        // ## Act ##
        ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        log("/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ");
        assertFalse(memberList.isEmpty());
        for (Member member : memberList) {
            String memberName = member.getMemberName();
            String memberAccount = member.getMemberAccount();
            log(memberName + "(" + memberAccount + ")");
            assertTrue("Unexpected memberAccount = " + memberAccount, memberAccount.startsWith("S")
                    || memberAccount.startsWith("M") || memberAccount.startsWith("D"));
        }
        log("* * * * * * * * * */");
    }

    /**
     * Unionを使ったページング検索: union(), selectPage().
     * 絞り込み条件は「退会会員であること」もしくは「１５００円以上の購入をしたことがある」。
     * 「誕生日の降順＆会員IDの昇順」で並べて、１ページを３件としてページング検索。
     * <pre>
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
    public void test_selectPage_union_existsSubQuery_Tx() {
        // ## Arrange ##
        int fetchSize = 3;
        MemberCB cb = new MemberCB();
        cb.query().setMemberStatusCode_Equal_Withdrawal();
        cb.union(new UnionQuery<MemberCB>() {
            public void query(MemberCB unionCB) {
                unionCB.query().existsPurchaseList(new SubQuery<PurchaseCB>() {
                    public void query(PurchaseCB subCB) {
                        subCB.query().setPurchasePrice_GreaterEqual(1500);
                    }
                });
            }
        });
        cb.query().addOrderBy_MemberBirthday_Desc().addOrderBy_MemberId_Asc();

        // ## Act ##
        cb.paging(fetchSize, 1);
        PagingResultBean<Member> page1 = memberBhv.selectPage(cb);
        cb.paging(fetchSize, 2);
        PagingResultBean<Member> page2 = memberBhv.selectPage(cb);
        cb.paging(fetchSize, 3);
        PagingResultBean<Member> page3 = memberBhv.selectPage(cb);
        cb.paging(fetchSize, page1.getAllPageCount());// Last Page
        PagingResultBean<Member> lastPage = memberBhv.selectPage(cb);

        // ## Assert ##
        showPage(page1, page2, page3, lastPage);
        assertEquals(fetchSize, page1.size());
        assertEquals(fetchSize, page2.size());
        assertEquals(fetchSize, page3.size());
        assertNotSame(page1.get(0).getMemberId(), page2.get(0).getMemberId());
        assertNotSame(page2.get(0).getMemberId(), page3.get(0).getMemberId());
        assertNotSame(page3.get(0).getMemberId(), lastPage.get(0).getMemberId());
        assertEquals(1, page1.getCurrentPageNumber());
        assertEquals(2, page2.getCurrentPageNumber());
        assertEquals(3, page3.getCurrentPageNumber());
        assertEquals(page1.getAllPageCount(), lastPage.getCurrentPageNumber());
        assertEquals(page1.getAllRecordCount(), page2.getAllRecordCount());
        assertEquals(page2.getAllRecordCount(), page3.getAllRecordCount());
        assertEquals(page3.getAllRecordCount(), lastPage.getAllRecordCount());
        assertEquals(page1.getAllPageCount(), page2.getAllPageCount());
        assertEquals(page2.getAllPageCount(), page3.getAllPageCount());
        assertEquals(page3.getAllPageCount(), lastPage.getAllPageCount());
        assertFalse(page1.isExistPrePage());
        assertTrue(page1.isExistNextPage());
        assertTrue(lastPage.isExistPrePage());
        assertFalse(lastPage.isExistNextPage());

        ConditionBeanSetupper<PurchaseCB> setupper = new ConditionBeanSetupper<PurchaseCB>() {
            public void setup(PurchaseCB cb) {
                cb.query().setPurchasePrice_GreaterEqual(1500);
            }
        };
        memberBhv.loadPurchaseList(page1, setupper);
        memberBhv.loadPurchaseList(page2, setupper);
        memberBhv.loadPurchaseList(page3, setupper);
        memberBhv.loadPurchaseList(lastPage, setupper);
        SelectPageUnionExistsSbuQueryAssertBoolean bl = new SelectPageUnionExistsSbuQueryAssertBoolean();
        findTarget_of_selectPage_union_existsSubQuery(page1, bl);
        findTarget_of_selectPage_union_existsSubQuery(page2, bl);
        findTarget_of_selectPage_union_existsSubQuery(page3, bl);
        findTarget_of_selectPage_union_existsSubQuery(lastPage, bl);
        assertTrue(bl.isExistsWithdrawalOnly());
        assertTrue(bl.isExistsPurchasePriceOnly());

        // 最後に目で確認するためにSQLをログへ
        log("/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ");
        String displaySql = cb.toDisplaySql();
        String newLine = getLineSeparator();
        log(newLine + DfStringUtil.replace(displaySql, " union ", newLine + " union "));
        log("* * * * * * * * * */");
    }

    protected void findTarget_of_selectPage_union_existsSubQuery(PagingResultBean<Member> memberPage,
            SelectPageUnionExistsSbuQueryAssertBoolean bl) {
        for (Member member : memberPage) {
            List<Purchase> purchaseList = member.getPurchaseList();
            boolean existsPurchaseTarget = false;
            for (Purchase purchase : purchaseList) {
                if (purchase.getPurchasePrice() >= 1500) {
                    existsPurchaseTarget = true;
                }
            }
            if (!existsPurchaseTarget && member.isMemberStatusCodeWithdrawal()) {
                bl.setExistsWithdrawalOnly(true);
            } else if (existsPurchaseTarget && !member.isMemberStatusCodeWithdrawal()) {
                bl.setExistsPurchasePriceOnly(true);
            }
        }
    }

    protected static class SelectPageUnionExistsSbuQueryAssertBoolean {
        protected boolean existsWithdrawalOnly = false;
        protected boolean existsPurchasePriceOnly = false;

        public boolean isExistsWithdrawalOnly() {
            return existsWithdrawalOnly;
        }

        public void setExistsWithdrawalOnly(boolean existsWithdrawalOnly) {
            this.existsWithdrawalOnly = existsWithdrawalOnly;
        }

        public boolean isExistsPurchasePriceOnly() {
            return existsPurchasePriceOnly;
        }

        public void setExistsPurchasePriceOnly(boolean existsPurchasePriceOnly) {
            this.existsPurchasePriceOnly = existsPurchasePriceOnly;
        }
    }

    // ===================================================================================
    //                                                                            OnClause
    //                                                                            ========
    /**
     * OnClause(On句)に条件を追加: queryXxx().on().
     * <code>{left outer join xxx on xxx = xxx and [column] = ?}</code>
     * <p>
     * 「会員退会情報が存在している会員一覧」に対して、「退会理由コードがnullでない会員退会情報」を結合して取得。
     * 会員退会情報が存在していても退会理由コードがnullの会員は、会員退会情報が取得されないようにする。
     * </p>
     * <p>
     * OnClauseに条件を追加すると「条件に合致しない結合先レコードは結合しない」という感じになる。
     * よく使われるのは「従属しない関係の結合先テーブルで論理削除されたものは結合しない」というような場合。
     * </p>
     * <p>
     * OnClauseを使わないでWhere句に条件を入れると、条件に合致しない結合先レコードを
     * 参照している基点レコードが検索対象外になってしまう。<br />
     * <code>{left outer join xxx on xxx = xxx where [column] = ?}</code>
     * </p>
     * <pre>
     * 例えば、会員{1,2,3,4,5}に対して会員退会情報{A,B,C}があり、それぞれ{1-A, 2-B, 3-C, 4-null, 5-null}
     * というような関係で、「B」が退会理由コードを持っていない会員退会情報であった場合：
     * 
     * 素直に「会員 left outer join 会員退会情報 on ...」すると結果は以下のようになる。
     * 
     * 　　検索結果：{1-A, 2-B, 3-C, 4-null, 5-null}
     * 
     * これを「会員 left outer join 会員退会情報 on ... and 会員退会情報.退会理由コード is not null」
     * というようにOn句の中で「退会理由コードが存在すること」という条件を付与すると以下のようになる。
     * 
     * 　　検索結果：{1-A, 2-null, 3-C, 4-null, 5-null}
     * 
     * 退会理由コードを持っていない「B」が弾かれて結合されないのである。
     * だからといって「2」の会員自体が検索結果から外れることはない。
     * 
     * これを「会員 left outer join 会員退会情報 on ... where 会員退会情報.退会理由コード is not null」
     * というようにWhere句にて「退会理由コードが存在すること」という条件を付与すると以下のようになる。
     * 
     * 　　検索結果：{1-A, 3-C}
     * 
     * これは今回やりたい検索とは全く違うものである。
     * </pre>
     * <p>
     * OnClauseでなくInlineViewを使っても同じ動きを実現することは可能である。
     * しかし、条件によってはInlineViewの中でフルスキャンが走ってしまう可能性もあるので、
     * パフォーマンスの観点からOnClauseの方が良いかと思われる。(実行計画が異なる)
     * 但し、これはオプティマイザ次第なので、気になったらどちらかに調整するのが良いと思われる。<br />
     * <code>{left outer join (select * from xxx where [column] = ?) xxx on xxx = xxx}</code>
     * </p>
     */
    public void test_selectList_query_queryForeign_on_Tx() {
        // ## Arrange ##
        MemberCB cb = new MemberCB();
        cb.setupSelect_MemberWithdrawalAsOne();

        // 「退会理由コードがnullでない会員退会情報」のレコードは結合されてないようにする
        // left outer join xxx on xxx = xxx and WithdrawalReasonCode is not null
        cb.query().queryMemberWithdrawalAsOne().on().setWithdrawalReasonCode_IsNotNull();

        // left outer join (select * from xxx where WithdrawalReasonCode is not null) xxx on xxx = xxx
        // cb.query().queryMemberWithdrawalAsOne().inline().setWithdrawalReasonCode_IsNotNull();

        // 会員退会情報が存在する会員だけを取得するようにする
        cb.query().inScopeMemberWithdrawalAsOne(new SubQuery<MemberWithdrawalCB>() {
            public void query(MemberWithdrawalCB subCB) {
            }
        });
        cb.query().queryMemberWithdrawalAsOne().addOrderBy_WithdrawalDatetime_Desc();

        // ## Act ##
        ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        assertNotSame(0, memberList.size());
        boolean existsMemberWithdrawal = false;// 会員退会情報があってWithdrawalReasonCodeも存在する会員がいるか否か
        boolean notExistsMemberWithdrawal = false;// 会員退会情報はあるけどWithdrawalReasonCodeがない会員がいるか否か
        List<Integer> notExistsMemberIdList = new ArrayList<Integer>();
        for (Member member : memberList) {
            MemberWithdrawal memberWithdrawal = member.getMemberWithdrawalAsOne();
            if (memberWithdrawal != null) {
                log(member.getMemberName() + " -- " + memberWithdrawal.getWithdrawalReasonCode() + ", "
                        + memberWithdrawal.getWithdrawalDatetime());
                String withdrawalReasonCode = memberWithdrawal.getWithdrawalReasonCode();
                assertNotNull(withdrawalReasonCode);
                existsMemberWithdrawal = true;
            } else {
                // 会員退会情報は存在するけどWithdrawalReasonCodeが存在しない会員も取得できていること
                log(member.getMemberName() + " -- " + memberWithdrawal);
                notExistsMemberWithdrawal = true;
                notExistsMemberIdList.add(member.getMemberId());
            }
        }
        // 両方のパターンのデータがないとテストにならないので確認
        assertTrue(existsMemberWithdrawal);
        assertTrue(notExistsMemberWithdrawal);
        // MemberWithdrawalを取得できなかった会員の会員退会情報がちゃんとあるかどうか確認
        for (Integer memberId : notExistsMemberIdList) {
            memberWithdrawalBhv.selectByPKValueWithDeletedCheck(memberId);// Expected no exception
        }
    }

    // ===================================================================================
    //                                                                      Specify Column
    //                                                                      ==============
    /**
     * 取得カラムの指定(SpecifyColumn): specify().columnXxx().
     * 会員名称と会員ステータス名称だけの一覧を検索する。
     * <p>
     * パフォーマンス上の考慮で１ミリ秒でも速くしたいシビアな検索処理の場合のために、
     * 取得カラムを指定することができる。１テーブルのカラム数がやたら多いときに有効。
     * </p>
     * @since 0.7.4
     */
    public void test_specifyColumn_Tx() {
        // ## Arrange ##
        MemberCB cb = new MemberCB();
        cb.setupSelect_MemberStatus();
        cb.specify().columnMemberName();
        cb.specify().specifyMemberStatus().columnMemberStatusName();

        // ## Act ##
        ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        assertNotSame(0, memberList.size());
        for (Member member : memberList) {
            assertNotNull(member.getMemberId()); // PK
            assertNotNull(member.getMemberName()); // Specified
            assertNull(member.getMemberAccount());
            assertNull(member.getMemberBirthday());
            assertNull(member.getMemberFormalizedDatetime());
            assertNull(member.getRegisterDatetime());
            assertNull(member.getRegisterProcess());
            assertNull(member.getRegisterUser());
            assertNull(member.getUpdateDatetime());
            assertNull(member.getUpdateProcess());
            assertNull(member.getUpdateUser());
            assertNull(member.getVersionNo());
            assertNotNull(member.getMemberStatusCode()); // SetupSelect FK
            assertNotNull(member.getMemberStatus().getMemberStatusCode()); // PK
            assertNotNull(member.getMemberStatus().getMemberStatusName()); // Specified
            assertNull(member.getMemberStatus().getDisplayOrder());
        }

        // [Description]
        // A. 結合先テーブルに関しては、setupSelect_Xxx()を呼び出すことに変わりはなく、
        //    setupSelectしたテーブルの中から取得するカラムを指定する。
        //    --> setupSelectしてないテーブルのカラムを指定すると例外となる。
        // 
        // B. カラムが指定されたテーブルのみカラムが絞り込まれ、カラムが指定されないテーブルは
        //    通常通り全てのカラムが取得される。
        //    --> 例えば、ある一つの結合先テーブルだけカラム指定にすることも可能
        // 
        // C. PKは、カラムが指定されなくても必ず取得される。(暗黙の指定カラム)
        // D. SetupSelectされたFKは、カラムが指定されなくても必ず取得される。(暗黙の指定カラム)
    }

    // ===================================================================================
    //                                                            (Specify)DerivedReferrer
    //                                                            ========================
    /**
     * 子テーブル導出カラムの指定(SpecifyDerivedReferrer)-Max: specify().derivedXxxList().max().
     * 会員の最終ログイン日時を取得。但し、モバイル端末からのログインは除く。
     * <p>
     * 子テーブルの導出カラムを指定することができる。
     * 例えば、子テーブルのとあるカラムの合計値や最大値などを取得することが可能である。
     * 例題のSQL文のイメージは以下の通り：
     * </p>
     * <pre>
     * ex) 最終ログイン日時を取得するSQL
     * select member.*
     *      , (select max(LOGIN_DATETIME)
     *           from MEMBER_LOGIN
     *          where MEMBER_ID = member.MEMBER_ID
     *            and LOGIN_MOBILE_FLG = 0
     *        ) as LATEST_LOGIN_DATETIME
     *   from MEMBER member
     * </pre>
     * @since 0.7.4
     */
    public void test_sepcify_derivedReferrer_max_Tx() {
        // ## Arrange ##
        MemberCB cb = new MemberCB();
        cb.specify().derivedMemberLoginList().max(new SubQuery<MemberLoginCB>() {
            public void query(MemberLoginCB subCB) {
                subCB.specify().columnLoginDatetime();// *Point!
                subCB.query().setLoginMobileFlg_Equal_False();// Except Mobile
            }
        }, "LATEST_LOGIN_DATETIME");

        // ## Act ##
        ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        assertNotSame(0, memberList.size());
        boolean existsLoginDatetime = false;
        boolean existsNullLoginDatetime = false;
        for (Member member : memberList) {
            String memberName = member.getMemberName();
            Date latestLoginDatetime = member.getLatestLoginDatetime();
            if (latestLoginDatetime != null) {
                existsLoginDatetime = true;
            } else {
                // ログインを一度もしていない会員、もしくは、モバイルでしかログイン
                // したことのない会員の最終ログイン日時はnullになる。
                existsNullLoginDatetime = true;
            }
            log("memberName=" + memberName + ", latestLoginDatetime=" + latestLoginDatetime);
        }
        assertTrue(existsLoginDatetime);
        assertTrue(existsNullLoginDatetime);

        // [Description]
        // A. 実装前に導出カラムを受け取るためのプロパティをEntityに定義する必要がある。
        // 
        //    ex) ExtendedのEntity(ExEntity)に最終ログイン日時のプロパティを手動で実装
        //    protected Date _latestLoginDatetime;
        //    public Date getLatestLoginDatetime() {
        //        return _latestLoginDatetime;
        //    }
        //    public void setLatestLoginDatetime(Date latestLoginDatetime) {
        //        _latestLoginDatetime = latestLoginDatetime;
        //    }
        // 
        // B. 関数には、{max, min, sum, avg, count}が利用可能である。
        //    --> sumとavgは数値型のみ利用可能
        //    --> countの場合は子テーブルのPKを導出カラムとすることが基本
        // 
        // C. 必ずSubQueryの中で導出カラムを「一つ」指定すること。
        //    --> 何も指定しない、もしくは、二つ以上の指定で例外発生
        // 
        // D. 導出カラムは子テーブルのカラムのみサポートされる。
        //    --> 子テーブルの別の親テーブル(もしくはone-to-one)のカラムを導出カラムにはできない。
        // 
        // E. 基点テーブルが複合主キーの場合はサポートされない。
        // 
        // F. one-to-oneの子テーブルの場合はサポートされない。(そもそも不要である)
        // 
        // G. SubQueryの中でsetupSelectやaddOrderByを指定しても無意味である。
        // 
        // H. SpecifyColumnやUnionとも組み合わせて利用することが可能である。
        // 
        // X. Eclipse(3.3)でのSubQuery実装手順：
        //    X-1. cb.sp まで書いて補完してEnter!
        //         --> cb.specify() になる
        // 
        //    X-2. cb.specify().der まで書いて補完して子テーブルを選択してEnter!
        //         --> cb.specify().deriveMemberLoginList() になる
        // 
        //    X-3. cb.specify().deriveMemberLoginList().ma まで書いて補完してEnter!
        //         --> cb.specify().deriveMemberLoginList().max(subQuery, aliasName) になる
        //             このとき[subQuery]部分が選択状態である
        // 
        //    X-4. cb.specify().deriveMemberLoginList().max(new , aliasName) まで書いて補完してEnter!
        //         --> カーソル位置から入力する文字は「new 」
        //         --> cb.specify().deriveMemberLoginList().max(new SubQuery<MemberLoginCB>, aliasName) になる
        // 
        //    X-5. cb.specify().deriveMemberLoginList().max(new SubQuery<MemberLoginCB>() {, aliasName) まで書いて補完してEnter!
        //         --> カーソル位置から入力する文字は「() {」
        //         --> cb.specify().deriveMemberLoginList().max(new SubQuery<MemberLoginCB>() {
        //             } , aliasName)
        //             になる
        //         --> [SubQuery<MemberLoginCB>]部分がコンパイルエラーになる
        // 
        //    X-6. コンパイルエラーの[SubQuery<MemberLoginCB>]にカーソルを合わせてctrl + 1を押してEnter!
        //         --> cb.specify().deriveMemberLoginList().max(new SubQuery<MemberLoginCB>() {
        //                 public void query(MemberLoginCB subCB) {
        //                     // todo Auto-generated method stub
        //                 }
        //             } , aliasName)
        //             になる
        //         --> [aliasName]部分とセミコロンがないことでまだコンパイルエラーである
        // 
        //    X-7. aliasNameを指定して、セミコロンも付ける
        //         --> cb.specify().deriveMemberLoginList().max(new SubQuery<MemberLoginCB>() {
        //                 public void query(MemberLoginCB subCB) {
        //                     // todo Auto-generated method stub
        //                 }
        //             } , "LATEST_LOGIN_DATETIME");
        // 
        //    X-8. TODOのコメント消して子テーブルの「導出カラムの指定」と「絞り込み条件」を実装する
    }

    /**
     * 子テーブル導出カラムで並び替え(SpecifiedDerivedOrderBy)-Count: addSpecifiedDerivedOrderBy_Desc().
     * 会員のログイン回数を取得する際に、ログイン回数の多い順そして会員IDの昇順で並べる。但し、モバイル端末からのログインは除く。
     * <p>
     * 子テーブルの導出カラムで並び替えをすることができる。
     * SQL文のイメージは以下の通り：
     * </p>
     * <pre>
     * ex) ログイン回数を取得するSQL
     * select member.*
     *      , (select count(MEMBER_LOGIN_ID)
     *           from MEMBER_LOGIN
     *          where MEMBER_ID = member.MEMBER_ID
     *        ) as LOGIN_COUNT
     *   from MEMBER member
     *  order by LOGIN_COUNT desc, member.MEMBER_ID asc
     * </pre>
     * @since 0.7.4
     */
    public void test_query_addSepcifidDerivedOrderBy_count_Tx() {
        // ## Arrange ##
        MemberCB cb = new MemberCB();
        cb.specify().derivedMemberLoginList().count(new SubQuery<MemberLoginCB>() {
            public void query(MemberLoginCB subCB) {
                subCB.specify().columnMemberLoginId();// *Point!
                subCB.query().setLoginMobileFlg_Equal_False();// Except Mobile
            }
        }, "LOGIN_COUNT");
        cb.query().addSpecifiedDerivedOrderBy_Desc("LOGIN_COUNT");
        cb.query().addOrderBy_MemberId_Asc();

        // ## Act ##
        ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        assertNotSame(0, memberList.size());
        for (Member member : memberList) {
            String memberName = member.getMemberName();
            Integer loginCount = member.getLoginCount();
            assertNotNull(loginCount);// count()なので0件の場合は0になる(DB次第かも...)
            log("memberName=" + memberName + ", loginCount=" + loginCount);
        }

        // [Description]
        // A. SpecifyDerivedReferrerで指定されていないAliasNameを指定すると例外発生。
        // 
        // B. withNullsFirst/Last()と組み合わせることも可能
        //    cb.query().addSpecifiedDerivedOrderBy_Desc("LOGIN_COUNT").withNullsLast();
    }

    /**
     * 子テーブルカラムの種類数取得(SpecifyDerivedReferrer)-CountDisticnt: derivedXxxList(), countDistinct().
     * それぞれの会員の「購入済みプロダクトの種類数」を検索。
     * @since 0.8.8
     */
    public void test_SepcifyDerivedReferrer_countDistinct_Tx() {
        // ## Arrange ##
        MemberCB cb = new MemberCB();
        cb.specify().derivedPurchaseList().countDistinct(new SubQuery<PurchaseCB>() {
            public void query(PurchaseCB subCB) {
                subCB.specify().columnProductId();
                subCB.query().setPaymentCompleteFlg_Equal_True();
            }
        }, "PRODUCT_KIND_COUNT");
        cb.query().addSpecifiedDerivedOrderBy_Desc("PRODUCT_KIND_COUNT");
        cb.query().addOrderBy_MemberId_Asc();

        // ## Act ##
        ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        assertNotSame(0, memberList.size());
        for (Member member : memberList) {
            String memberName = member.getMemberName();
            Integer productKindCount = member.getProductKindCount();
            assertNotNull(productKindCount);// count()なので0件の場合は0になる(DB次第かも...)
            log("memberName=" + memberName + ", productKindCount=" + productKindCount);
        }
        assertTrue(cb.toDisplaySql().contains("count(distinct"));
    }
    
    // ===================================================================================
    //                                                              (Query)DerivedReferrer
    //                                                              ======================
    /**
     * 子テーブル導出カラムで絞り込み(QueryDerivedReferrer)-Max: derivedXxx(), max(), greaterEqual().
     * 1800円以上の支払済み購入のある会員一覧を検索。
     * @since 0.8.8.1
     */
    public void test_query_derivedReferrer_max_greaterEqual_Tx() {
        // ## Arrange ##
        Integer expected = 1800;

        MemberCB cb = new MemberCB();
        cb.query().setMemberStatusCode_Equal_Formalized();
        cb.query().derivedPurchaseList().max(new SubQuery<PurchaseCB>() {
            public void query(PurchaseCB subCB) {
                subCB.specify().columnPurchasePrice(); // *Point!
                subCB.query().setPaymentCompleteFlg_Equal_True();
            }
        }).greaterEqual(expected); // *Don't forget!

        // ## Act ##
        ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        memberBhv.loadPurchaseList(memberList, new ConditionBeanSetupper<PurchaseCB>() {
            public void setup(PurchaseCB cb) {
                cb.query().setPaymentCompleteFlg_Equal_True();
            }
        });
        assertNotSame(0, memberList.size());
        for (Member member : memberList) {
            log(member);
            List<Purchase> purchaseList = member.getPurchaseList();
            boolean exists = false;
            for (Purchase purchase : purchaseList) {
                Integer purchasePrice = purchase.getPurchasePrice();
                if (purchasePrice >= expected) {
                    exists = true;
                }
            }
            assertTrue(exists);
        }
        
        // [SQL]
        // select dflocal.MEMBER_NAME as MEMBER_NAME, ... 
        //   from MEMBER dflocal 
        //  where dflocal.MEMBER_STATUS_CODE = 'FML'
        //    and (select max(dfsublocal_0.PURCHASE_PRICE)
        //           from PURCHASE dfsublocal_0 
        //          where dfsublocal_0.MEMBER_ID = dflocal.MEMBER_ID
        //            and dfsublocal_0.PAYMENT_COMPLETE_FLG = 1
        //        ) >= 1800
        
        // [Description]
        // A. 比較演算子には、{=, >=, >, <=, <}が利用可能である。
        // 
        // B. 関数には、{max, min, sum, avg, count, countDistinct}が利用可能である。
        //    --> sumとavgとcountとcountDistinctは数値型のみ利用可能
        //    --> countの場合は子テーブルのPKを導出カラムとすることが基本
        // 
        // C. 必ずSubQueryの中で導出カラムを「一つ」指定すること。
        //    --> 何も指定しない、もしくは、二つ以上の指定で例外発生
        // 
        // D. 導出カラムは基点テーブルのカラムのみサポートされる。
        // 
        // E. 基点テーブルが複合主キーの場合はサポートされない。
        // 
        // F. 必ずカラムの型とパラメータの型を合わせること！(count()とcountDistinct()は除く)
    }

    // ===================================================================================
    //                                                                      ScalarSubQuery
    //                                                                      ==============
    /**
     * 最大値レコードの検索(ScalarSubQuery)-Max: scalar_Equal(), max().
     * 正式会員の中で一番若い(誕生日が最大値である)会員を検索。
     * @since 0.8.8
     */
    public void test_scalarSubQuery_Equal_max_Tx() {
        // ## Arrange ##
        Date expected = selectExpectedMaxBirthdayOnFormalized();

        MemberCB cb = new MemberCB();
        cb.query().setMemberStatusCode_Equal_Formalized();
        cb.query().scalar_Equal().max(new SubQuery<MemberCB>() {
            public void query(MemberCB subCB) {
                subCB.specify().columnMemberBirthday(); // *Point!
                subCB.query().setMemberStatusCode_Equal_Formalized();
            }
        });

        // ## Act ##
        ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        assertNotSame(0, memberList.size());
        for (Member member : memberList) {
            Date memberBirthday = member.getMemberBirthday();
            assertEquals(expected, memberBirthday);
        }

        // [SQL]
        // select dflocal.MEMBER_NAME as MEMBER_NAME, ... 
        //   from MEMBER dflocal 
        //  where dflocal.MEMBER_STATUS_CODE = 'FML'
        //    and dflocal.MEMBER_BIRTHDAY = (select max(dfsublocal_0.MEMBER_BIRTHDAY)
        //                                     from MEMBER dfsublocal_0 
        //                                    where dfsublocal_0.MEMBER_STATUS_CODE = 'FML'
        //        )

        // [Description]
        // A. 比較演算子には、{=, >=, >, <=, <}が利用可能である。
        // 
        // B. 関数には、{max, min, sum, avg}が利用可能である。
        //    --> sumとavgは数値型のみ利用可能
        // 
        // C. 必ずSubQueryの中で導出カラムを「一つ」指定すること。
        //    --> 何も指定しない、もしくは、二つ以上の指定で例外発生
        // 
        // D. 導出カラムは基点テーブルのカラムのみサポートされる。
        // 
        // E. 基点テーブルが複合主キーの場合はサポートされない。
        // 
        // F. 「とあるカラムの値が平均値を超えるレコードを検索」というのも可能である。
        //    cb.query().scalar_GreaterThan().avg(new SubQuery<Xxx>) {...
    }

    protected Date selectExpectedMaxBirthdayOnFormalized() {
        Date expected = null;
        {
            MemberCB cb = new MemberCB();
            cb.query().setMemberStatusCode_Equal_Formalized();
            ListResultBean<Member> listAll = memberBhv.selectList(cb);
            for (Member member : listAll) {
                Date day = member.getMemberBirthday();
                if (day != null && (expected == null || expected.getTime() < day.getTime())) {
                    expected = day;
                }
            }
        }
        return expected;
    }

    // ===================================================================================
    //                                                                     Fixed Condition
    //                                                                     ===============
    /**
     * 固定条件を加えたone-to-oneの取得：fixedCondition, selectSelect_Xxx(target).
     * <p>
     * 会員と会員住所は構造的にはone-to-manyだが、固定条件を加えることによってone-to-oneになる
     * という業務的な制約が存在する。その業務的な制約を活用して、会員を基点に会員住所を取得。
     * </p>
     * <p>
     * 「何かしら固定条件を付与することによってone-to-manyがone-to-oneになる」というような場合、
     * 「{DBFluteClient}/dfprop/additionalForeignKeyMap.dfprop」にて固定条件付きの疑似FK
     * を設定し自動生成し直すことで、アプリケーション上でそのRelationを扱うことができる。
     * </p>
     * <pre>
     * ; FK_MEMBER_MEMBER_ADDRESS_VALID = map:{
     *     ; localTableName  = MEMBER    ; foreignTableName  = MEMBER_ADDRESS
     *     ; localColumnName = MEMBER_ID ; foreignColumnName = MEMBER_ID
     *     ; fixedCondition = 
     *      $$foreignAlias$$.VALID_BEGIN_DATE <= /[*]targetDate(Date)[*]/null
     *  and $$foreignAlias$$.VALID_END_DATE >= /[*]targetDate(Date)[*]/null
     *     ; fixedSuffix = AsValid
     * }
     * ※バインド変数コメントの「/[*]」の「[]」は実際には不要。JavaDoc上での記述の都合のために付けている。
     * </pre>
     * <p>
     * localTableName/foreignTableName/localColumnName/foreignColumnNameは通常の
     * additionalForeignKeyMapでの設定方法と特に変わらないが、foreignTableが構造的には
     * one-to-manyのmany側が指定されているのが特徴的である。
     * </p>
     * <p>
     * fixedConditionが注目ポイントである。fixedConditionには固定条件を指定。
     * これは「left outer join」のon句部分の結合条件としてそのまま展開される。
     * 「$$foreignAlias$$」はforeignTableのAlias名として実行時に置換される。
     * 「/[*]targetDate(Date)[*]/null」はバインド変数コメントとして解釈され、
     * 自動生成時にsetupSelect_Xxx()やqueryXxx()の引数として展開される。
     * その際、アプリケーション上の型は「(Date)」で指定された型となる。
     * 「Date」なら「java.util.Date」、「Integer」なら「java.lang.Integer」となる。
     * (ParameterBeanの型の自動解釈と同じである)
     * <p>
     * バインド変数コメントを使わずにベタッと値を指定することも可能。
     * 今回のExampleのような「有効期間」という概念で「固定条件に動的値」というのではなく、
     * 「有効フラグがtrueのものを指定するとone-to-oneになる」というような
     * 「固定条件に固定値」というパターンの場合はバインド変数コメントを使う必要はない。
     * その場合、setupSelect_Xxx()やqueryXxx()の引数は無しで通常通りである。
     * </p>
     * <p>
     * fixedSuffixは任意ではあるが、Relation名のユニーク性を厳密にするために
     * 何かしら意味のあるSuffixを付けることが推奨される。今回のExampleだと、
     * 「(会員を基点とした場合の)有効な会員住所」ということなので、「AsValid」
     * というSuffixを付けている。
     * </p>
     * @since 0.8.7
     */
    public void test_fixedCondition_setupSelect_Tx() {
        // ## Arrange ##
        Calendar cal = Calendar.getInstance();
        cal.set(2005, 11, 12); // 2005/12/12
        Date targetDate = cal.getTime();

        MemberCB cb = new MemberCB();
        cb.setupSelect_MemberAddressAsValid(targetDate);
        cb.query().addOrderBy_MemberId_Asc();

        // ## Act ##
        ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        assertNotSame(0, memberList.size());
        boolean existsAddress = false;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        String formattedTargetDate = fmt.format(targetDate);
        log("[" + formattedTargetDate + "]");
        for (Member member : memberList) {
            String memberName = member.getMemberName();
            MemberAddress memberAddressAsValid = member.getMemberAddressAsValid();
            if (memberAddressAsValid != null) {
                assertNotNull(memberAddressAsValid.getValidBeginDate());
                assertNotNull(memberAddressAsValid.getValidEndDate());
                String validBeginDate = fmt.format(memberAddressAsValid.getValidBeginDate());
                String validEndDate = fmt.format(memberAddressAsValid.getValidEndDate());
                assertTrue(validBeginDate.compareTo(formattedTargetDate) <= 0);
                assertTrue(validEndDate.compareTo(formattedTargetDate) >= 0);
                String address = memberAddressAsValid.getAddress();
                log(memberName + ", " + validBeginDate + ", " + validEndDate + ", " + address);
                existsAddress = true;
            } else {
                log(memberName + ", null");
            }
        }
        assertTrue(existsAddress);

        // [SQL]
        // select dflocal.MEMBER_NAME as MEMBER_NAME, ... 
        //   from MEMBER dflocal
        //     left outer join MEMBER_ADDRESS dfrelation_1
        //       on dflocal.MEMBER_ID = dfrelation_1.MEMBER_ID
        //         and dfrelation_1.VALID_BEGIN_DATE <= '2005-12-12'
        //         and dfrelation_1.VALID_END_DATE >= '2005-12-12'  
        //  order by dflocal.MEMBER_ID asc

        // [Description]
        // A. selectSelect_Xxx(target)で別の値のtargetを指定して二回以上呼び出した時は最後の値が有効
        //    --> 「2007/01/01の会員住所」と「2008/01/01の会員住所」を同時に取り扱うことはできない
        //        (additionalForeignKeyにてSuffixだけ変えたリレーションをもう一つ設定すれば可能)
        // 
        // B. fixedConditionを使ったRelationではReferrer関連のメソッドは生成されない
        //    ex) 会員住所のBehaviorにて会員に対するloadReferrerは生成されない
    }

    /**
     * 固定条件を加えたone-to-oneの絞り込み：fixedCondition, queryXxx(target).
     * <p>
     * 会員と会員住所は構造的にはone-to-manyだが、固定条件を加えることによってone-to-oneになる
     * という業務的な制約が存在する。その業務的な制約を活用して、会員を基点に会員住所にて絞り込み。
     * </p>
     * @since 0.8.7
     */
    public void test_fixedCondition_query_Tx() {
        // ## Arrange ##
        Calendar cal = Calendar.getInstance();
        cal.set(2005, 11, 12); // 2005/12/12
        final Date targetDate = cal.getTime();
        final String targetChar = "i";

        MemberCB cb = new MemberCB();
        LikeSearchOption likeSearchOption = new LikeSearchOption().likeContain();
        cb.query().queryMemberAddressAsValid(targetDate).setAddress_LikeSearch(targetChar, likeSearchOption);
        cb.query().queryMemberAddressAsValid(targetDate).addOrderBy_Address_Asc();
        cb.query().addOrderBy_MemberId_Asc();

        // ## Act ##
        ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        assertNotSame(0, memberList.size());
        memberBhv.loadMemberAddressList(memberList, new ConditionBeanSetupper<MemberAddressCB>() {
            public void setup(MemberAddressCB cb) {
                cb.query().setAddress_LikeSearch(targetChar, new LikeSearchOption().likeContain());
                cb.query().setValidBeginDate_LessEqual(targetDate);
                cb.query().setValidEndDate_GreaterEqual(targetDate);
            }
        });
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        String formattedTargetDate = fmt.format(targetDate);
        log("[" + formattedTargetDate + "]");
        for (Member member : memberList) {
            MemberAddress memberAddressAsValid = member.getMemberAddressAsValid();
            assertNull(memberAddressAsValid); // because of no setup-select.
            List<MemberAddress> memberAddressList = member.getMemberAddressList();
            assertEquals(1, memberAddressList.size());
            MemberAddress memberAddress = memberAddressList.get(0);
            String memberName = member.getMemberName();
            Date validBeginDate = memberAddress.getValidBeginDate();
            Date validEndDate = memberAddress.getValidEndDate();
            String address = memberAddress.getAddress();
            log(memberName + ", " + validBeginDate + ", " + validEndDate + ", " + address);
            assertTrue(memberAddress.getAddress().contains("a"));
        }

        // [Description]
        // A. queryXxx(target)で別の値のtargetを指定して二回以上呼び出した時は最後の値が有効
        //    --> 「2007/01/01の会員住所」と「2008/01/01の会員住所」を同時に取り扱うことはできない
        // 
        // B. fixedConditionを使ったRelationではReferrer関連のメソッドは生成されない
        //    ex) 会員住所のBehaviorにて会員に対するloadReferrerは生成されない
    }

    // ===================================================================================
    //                                                                    Statement Config
    //                                                                    ================
    /**
     * Statementのコンフィグを設定: cb.configure(statementConfig).
     */
    public void test_configure_statementConfig_Tx() {
        // ## Arrange ##
        MemberCB cb = new MemberCB();
        cb.configure(new StatementConfig().typeForwardOnly().queryTimeout(7).fetchSize(4).maxRows(3));

        // ## Act ##
        ListResultBean<Member> memberList = memberBhv.selectList(cb);

        // ## Assert ##
        assertEquals(3, memberList.size());
    }

    // ===================================================================================
    //                                                                         Display SQL
    //                                                                         ===========
    /**
     * どんなにSubQueryやUnionの連打をしてもSQLが綺麗にフォーマット: toDisplaySql().
     * ログでSQLが綺麗にフォーマットされていることを確認するだけ。
     * <p>
     * デバッグのし易さの徹底と、ConditionBeanから外だしSQLへの移行時にスムーズにできるように
     * ログのフォーマットを重視している。相関サブクエリなどはConditionBeanで書いてから出力された
     * SQLをベースに実装した方が外だしSQLでありがちなケアレスバグも無くなる。
     * </p>
     */
    public void test_toDisplaySql_Check_FormattedSQL_Tx() {
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // 単にフォーマットされていることがみたいだけなので条件はかなり無茶苦茶
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        final MemberStatusCB cb = new MemberStatusCB();
        cb.query().setDisplayOrder_Equal(3);
        cb.query().existsMemberList(new SubQuery<MemberCB>() {
            public void query(MemberCB memberCB) {
                memberCB.query().setMemberBirthday_LessEqual(new Date());
                memberCB.query().existsPurchaseList(new SubQuery<PurchaseCB>() {
                    public void query(PurchaseCB purchaseCB) {
                        purchaseCB.query().setPurchaseCount_GreaterEqual(2);
                    }
                });
                memberCB.query().existsMemberWithdrawalAsOne(new SubQuery<MemberWithdrawalCB>() {
                    public void query(MemberWithdrawalCB subCB) {
                        final LikeSearchOption option = new LikeSearchOption().likeContain().escapeByPipeLine();
                        subCB.query().queryWithdrawalReason().setWithdrawalReasonCode_LikeSearch("xxx", option);
                        subCB.union(new UnionQuery<MemberWithdrawalCB>() {
                            public void query(MemberWithdrawalCB unionCB) {
                                unionCB.query().setWithdrawalReasonInputText_IsNotNull();
                            }
                        });
                    }
                });
            }
        });
        cb.query().setMemberStatusCode_Equal_Formalized();
        cb.query().existsMemberLoginList(new SubQuery<MemberLoginCB>() {
            public void query(MemberLoginCB subCB) {
                subCB.query().inScopeMember(new SubQuery<MemberCB>() {
                    public void query(MemberCB subCB) {
                        subCB.query().setMemberBirthday_GreaterEqual(new Date());
                    }
                });
            }
        });
        cb.query().addOrderBy_DisplayOrder_Asc().addOrderBy_MemberStatusCode_Desc();
        log(getLineSeparator() + cb.toDisplaySql());
    }
}
