package com.example.dbflute.basic.dbflute.allcommon;

import org.dbflute.jdbc.LatestSqlProvider;
import org.seasar.extension.jdbc.SqlLog;
import org.seasar.extension.jdbc.SqlLogRegistry;
import org.seasar.extension.jdbc.SqlLogRegistryLocator;
import org.seasar.extension.jdbc.impl.SqlLogRegistryImpl;

import com.example.dbflute.basic.dbflute.cbean.MemberCB;
import com.example.dbflute.basic.dbflute.exbhv.MemberBhv;
import com.example.dbflute.basic.unit.ContainerTestCase;

/**
 * @author jflute
 * @since 0.6.0 (2008/01/16 Wednesday)
 */
public class SqlLogRegistryTest extends ContainerTestCase {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private MemberBhv memberBhv;
    protected LatestSqlProvider latestSqlProvider;
    
    // ===================================================================================
    //                                                                       SqlLogRegistr
    //                                                                       =============
    /**
     * DBFluteにおいて「SqlLogRegistry」を利用。<br />
     * DBFluteは初期化時(Container初期化)に、SqlLogRegistryの設定をOFFにする。<br />
     * SqlLogRegistryは便利ではあるが要件的に不要な場合は、<br />
     * パフォーマンスを劣化させるだけの無駄な処理となってしまうためである。<br />
     * 利用する場合は、「Container初期化後」にアプリケーション側で明示的に設定をONにすること。
     */
    public void test_SqlLogRegistry_HowToUse_Tx() {
        // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
        // DBFlute-0.8.2からSqlLogをコールバックでアプリで自由に扱う帰る仕組みを提供しています。
        // SqlLogRegistryよりもシンプルに広い用途で利用可能です。
        // allcommon.CallbackContextTestをご覧下さい。
        // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
        
        {// Confirm default setting at first
            final SqlLogRegistry sqlLogRegistry = SqlLogRegistryLocator.getInstance();
            assertNull(sqlLogRegistry);
        }
        {// Initialize
            // * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
            SqlLogRegistryLocator.setInstance(new SqlLogRegistryImpl());
            // * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
        }
        {// Nothing before executing SQL
            final SqlLogRegistry sqlLogRegistry = SqlLogRegistryLocator.getInstance();
            assertNotNull(sqlLogRegistry);
            final SqlLog lastSqlLog = sqlLogRegistry.getLast();
            assertNull(lastSqlLog);
        }
        {// Execute SQL
            final MemberCB cb = new MemberCB();
            cb.query().setMemberAccount_PrefixSearch("Sto");
            memberBhv.selectList(cb);
        }
        final String firstSql;
        {// Get sqlLog after executing SQL
            final SqlLogRegistry sqlLogRegistry = SqlLogRegistryLocator.getInstance();
            assertNotNull(sqlLogRegistry);
            final SqlLog lastSqlLog = sqlLogRegistry.getLast();
            assertNotNull(lastSqlLog);
            final String completeSql = lastSqlLog.getCompleteSql();
            final StringBuilder sb = new StringBuilder();
            sb.append(getLineSeparator());
            sb.append("/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ").append(getLineSeparator());
            sb.append(completeSql).append(getLineSeparator());
            sb.append("* * * * * * * * * */");
            log(sb);
            assertNotNull(completeSql);
            firstSql = completeSql;
        }
        {// Execute SQL again
            final MemberCB cb = new MemberCB();
            cb.query().setMemberId_GreaterEqual(123);
            cb.query().addOrderBy_MemberBirthday_Desc();
            cb.fetchFirst(3);
            cb.fetchPage(2);
            memberBhv.selectList(cb);
        }
        final String secondSql;
        {// Get sqlLog again
            final SqlLogRegistry sqlLogRegistry = SqlLogRegistryLocator.getInstance();
            assertNotNull(sqlLogRegistry);
            final SqlLog lastSqlLog = sqlLogRegistry.getLast();
            assertNotNull(lastSqlLog);
            final String completeSql = lastSqlLog.getCompleteSql();
            final StringBuilder sb = new StringBuilder();
            sb.append(getLineSeparator());
            sb.append("/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ").append(getLineSeparator());
            sb.append(completeSql).append(getLineSeparator());
            sb.append("* * * * * * * * * */");
            log(sb);
            assertNotNull(completeSql);
            secondSql = completeSql;
        }
        assertNotSame(firstSql, secondSql);
    }
    
    /**
     * デフォルトではLatestSqlProviderも利用不可なはず。
     */
    public void test_LatestSqlProvider_Tx() {
        // ## Arrange ##
        MemberCB cb = new MemberCB();
        cb.query().setMemberName_PrefixSearch("S");
        memberBhv.selectList(cb);
        
        // ## Act ##
        String displaySql = latestSqlProvider.getDisplaySql();
        
        // ## Assert ##
        log(displaySql);
        assertNull(displaySql);
    }
}
