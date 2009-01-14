package com.example.dbflute.basic.dbflute.allcommon;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.CallbackContext;
import org.seasar.dbflute.jdbc.SqlLogHandler;
import org.seasar.extension.jdbc.SqlLogRegistryLocator;

import com.example.dbflute.basic.dbflute.cbean.MemberCB;
import com.example.dbflute.basic.dbflute.exbhv.MemberBhv;
import com.example.dbflute.basic.unit.ContainerTestCase;

/**
 * @author jflute
 * @since 0.8.2 (2008/10/18 Saturday)
 */
public class CallbackContextTest extends ContainerTestCase {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private MemberBhv memberBhv;

    // ===================================================================================
    //                                                                                Test
    //                                                                                ====
    public void test_SqlLogHandler_HowToUse_Tx() {
        // ## Arrange ##
        final List<String> displaySqlList = new ArrayList<String>();
        CallbackContext callbackContext = new CallbackContext();
        callbackContext.setSqlLogHandler(new SqlLogHandler() {
            public void handle(String executedSql, String displaySql, Object[] args, Class<?>[] argTypes) {
                assertNotNull(executedSql);
                assertNotNull(displaySql);
                displaySqlList.add(displaySql);
            }
        });
        CallbackContext.setCallbackContextOnThread(callbackContext);

        try {
            // ## Act ##
            MemberCB cb = new MemberCB();
            cb.query().setMemberName_PrefixSearch("AAA");
            memberBhv.selectCount(cb);
            cb.query().setMemberName_PrefixSearch("BBB");
            memberBhv.selectCount(cb);
            cb.query().setMemberName_PrefixSearch("CCC");
            memberBhv.selectCount(cb);
            cb.query().setMemberName_PrefixSearch("DDD");
            memberBhv.selectCount(cb);

            // ## Assert ##
            log("[Display SQL]");
            log("- - - - - - - - - - - - - - - - - - ");
            for (String displaySql : displaySqlList) {
                log(displaySql);
                log("- - - - - - - - - - - - - - - - - - ");
            }
            assertEquals(4, displaySqlList.size());
            assertNull(SqlLogRegistryLocator.getInstance()); // This doesn't use SqlLogRegistry 
        } finally {
            CallbackContext.clearCallbackContextOnThread();
        }
    }
}
