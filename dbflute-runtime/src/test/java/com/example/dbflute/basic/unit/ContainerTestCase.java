package com.example.dbflute.basic.unit;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.AccessContext;
import org.seasar.dbflute.BehaviorSelector;
import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.extension.unit.S2TestCase;

import com.example.dbflute.basic.dbflute.exbhv.MemberAddressBhv;
import com.example.dbflute.basic.dbflute.exbhv.MemberLoginBhv;
import com.example.dbflute.basic.dbflute.exbhv.MemberSecurityBhv;
import com.example.dbflute.basic.dbflute.exbhv.MemberWithdrawalBhv;
import com.example.dbflute.basic.dbflute.exbhv.PurchaseBhv;

/**
 * The test base of the application.
 * @author jflute
 * @since 0.5.6 (2007/10/22 Monday)
 */
public abstract class ContainerTestCase extends S2TestCase {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance for sub class. */
    private static final Log _log = LogFactory.getLog(ContainerTestCase.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Timestamp accessTimestamp;

    protected String accessUser;

    protected String accessProcess;

    // ===================================================================================
    //                                                                            Settings
    //                                                                            ========
    @Override
    public void setUp() {
        include("dbflute.dicon");

        initializeAccessContext();
    }

    protected void initializeAccessContext() {
        accessTimestamp = currentTimestamp();
        accessUser = "testUser";
        accessProcess = getClass().getSimpleName();
        AccessContext context = new AccessContext();
        context.setAccessTimestamp(accessTimestamp);
        context.setAccessUser(accessUser);
        context.setAccessProcess(accessProcess);
        AccessContext.setAccessContextOnThread(context);
    }

    @Override
    public void tearDown() {
        destroyAccessContext();
    }

    protected void destroyAccessContext() {
        AccessContext.clearAccessContextOnThread();
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    private BehaviorSelector _behaviorSelector;

    protected void deleteMemberReferrers() {
        {
            PurchaseBhv bhv = _behaviorSelector.select(PurchaseBhv.class);
            bhv.queryDelete(bhv.newMyConditionBean());
        }
        {
            MemberWithdrawalBhv bhv = _behaviorSelector.select(MemberWithdrawalBhv.class);
            bhv.queryDelete(bhv.newMyConditionBean());
        }
        {
            MemberSecurityBhv bhv = _behaviorSelector.select(MemberSecurityBhv.class);
            bhv.queryDelete(bhv.newMyConditionBean());
        }
        {
            MemberLoginBhv bhv = _behaviorSelector.select(MemberLoginBhv.class);
            bhv.queryDelete(bhv.newMyConditionBean());
        }
        {
            MemberAddressBhv bhv = _behaviorSelector.select(MemberAddressBhv.class);
            bhv.queryDelete(bhv.newMyConditionBean());
        }
    }

    protected void showPage(PagingResultBean<? extends Object>... pages) {
        for (PagingResultBean<? extends Object> page : pages) {
            log(page);
        }
        int count = 1;
        for (PagingResultBean<? extends Object> page : pages) {
            log("[page" + count + "]");
            for (Object entity : page) {
                log("  " + entity);
            }
            ++count;
        }
    }

    protected void showList(List<? extends Object>... lss) {
        for (List<? extends Object> ls : lss) {
            log(ls);
        }
        int count = 1;
        for (List<? extends Object> ls : lss) {
            log("[page" + count + "]");
            for (Object entity : ls) {
                log("  " + entity);
            }
            ++count;
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected void log(Object msg) {
        _log.debug(msg);
    }

    protected Date currentDate() {
        return new Date(System.currentTimeMillis());
    }

    protected Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    protected static String getLineSeparator() {
        return System.getProperty("line.separator");
    }
}
