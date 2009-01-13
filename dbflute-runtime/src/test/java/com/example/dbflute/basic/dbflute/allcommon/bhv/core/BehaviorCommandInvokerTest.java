package com.example.dbflute.basic.dbflute.allcommon.bhv.core;

import com.example.dbflute.basic.dbflute.cbean.MemberCB;
import com.example.dbflute.basic.dbflute.exbhv.MemberBhv;
import com.example.dbflute.basic.unit.ContainerTestCase;

/**
 * @author jflute
 * @since 0.8.0 (2008/09/22 Monday)
 */
public class BehaviorCommandInvokerTest extends ContainerTestCase {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private MemberBhv memberBhv;

    // ===================================================================================
    //                                                                       LogInvocation
    //                                                                       =============
    public void test_LogInvocation_Page_to_Service_and_Facade_Tx() {
        // ## Arrange ##
        AaaPage aaaPage = new AaaPage();

        // ## Act & Assert ##
        // Confirm the log.
        aaaPage.service();
        aaaPage.facade();
    }

    public void test_LogInvocation_Action_to_Service_and_Facade_Tx() {
        // ## Arrange ##
        CccAction cccAction = new CccAction();

        // ## Act & Assert ##
        // Confirm the log.
        cccAction.service();
        cccAction.facade();
    }

    public void test_LogInvocation_Service_Tx() {
        // ## Arrange ##
        BbbService bbbService = new BbbService();

        // ## Act & Assert ##
        // Confirm the log.
        bbbService.bbb();
    }

    public void test_LogInvocation_Facade_Tx() {
        // ## Arrange ##
        DddFacade dddFacade = new DddFacade();

        // ## Act & Assert ##
        // Confirm the log.
        dddFacade.ddd();
    }

    public void test_LogInvocation_Service_to_Page_but_the_Service_is_invalid_Tx() {
        // ## Arrange ##
        BbbService bbbService = new BbbService();

        // ## Act & Assert ##
        // Confirm the log.
        bbbService.page();
    }

    public void test_LogInvocation_Facade_to_Action_but_the_Facade_is_invalid_Tx() {
        // ## Arrange ##
        DddFacade dddFacade = new DddFacade();

        // ## Act & Assert ##
        // Confirm the log.
        dddFacade.action();
    }

    // ===================================================================================
    //                                                                        Helper Class
    //                                                                        ============
    protected class AaaPage {
        public void service() {
            new BbbService().bbb();
        }

        public void facade() {
            new DddFacade().ddd();
        }

        public void aaa() {
            memberBhv.selectList(new MemberCB());
        }
    }

    protected class BbbService {
        public void bbb() {
            memberBhv.selectList(new MemberCB());
        }

        public void page() {
            new AaaPage().aaa();
        }
    }

    protected class CccAction {
        public void service() {
            new BbbService().bbb();
        }

        public void facade() {
            new DddFacade().ddd();
        }

        public void ccc() {
            memberBhv.selectList(new MemberCB());
        }
    }

    protected class DddFacade {
        public void ddd() {
            memberBhv.selectList(new MemberCB());
        }

        public void action() {
            new CccAction().ccc();
        }
    }
}
