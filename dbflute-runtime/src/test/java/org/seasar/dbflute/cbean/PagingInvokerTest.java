package org.seasar.dbflute.cbean;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.5 (2009/05/27 Wednesday)
 */
public class PagingInvokerTest extends PlainTestCase {

    // ===================================================================================
    //                                                                      invokePaging()
    //                                                                      ==============
    public void test_invokePaging_emtpy() {
        // ## Arrange ##
        final List<String> selectedList = new ArrayList<String>();
        final SimplePagingBean pagingBean = new SimplePagingBean();
        pagingBean.getSqlClause().registerOrderBy("aaa", "bbb", true);
        pagingBean.fetchFirst(20);
        PagingInvoker<String> tgt = createTarget();

        // ## Act ##
        final List<String> markList = new ArrayList<String>();
        PagingResultBean<String> rb = tgt.invokePaging(new PagingHandler<String>() {
            public PagingBean getPagingBean() {
                return pagingBean;
            }

            public int count() {
                markList.add("count");
                return 0;
            }

            public List<String> paging() {
                markList.add("paging");
                return selectedList;
            }
        });

        // ## Assert ##
        assertEquals(0, rb.size());
        assertEquals(0, rb.getAllRecordCount());
        assertEquals(1, rb.getAllPageCount());
        assertEquals(1, rb.getOrderByClause().getOrderByList().size());
        assertEquals("count", markList.get(0));
        assertEquals(1, markList.size());
    }

    public void test_invokePaging_emtpy_countLater() {
        // ## Arrange ##
        final List<String> selectedList = new ArrayList<String>();
        final SimplePagingBean pagingBean = new SimplePagingBean();
        pagingBean.getSqlClause().registerOrderBy("aaa", "bbb", true);
        pagingBean.fetchFirst(20);
        PagingInvoker<String> tgt = createTarget();
        tgt.countLater();

        // ## Act ##
        final List<String> markList = new ArrayList<String>();
        PagingResultBean<String> rb = tgt.invokePaging(new PagingHandler<String>() {
            public PagingBean getPagingBean() {
                return pagingBean;
            }

            public int count() {
                markList.add("count");
                return 0;
            }

            public List<String> paging() {
                markList.add("paging");
                return selectedList;
            }
        });

        // ## Assert ##
        assertEquals(0, rb.size());
        assertEquals(0, rb.getAllRecordCount());
        assertEquals(1, rb.getAllPageCount());
        assertEquals(1, rb.getOrderByClause().getOrderByList().size());
        assertEquals("paging", markList.get(0));
        assertEquals(1, markList.size());
    }

    public void test_invokePaging_onePage() {
        // ## Arrange ##
        final List<String> selectedList = new ArrayList<String>();
        fillList(selectedList, 19);
        final SimplePagingBean pagingBean = new SimplePagingBean();
        pagingBean.getSqlClause().registerOrderBy("aaa", "bbb", true);
        pagingBean.fetchFirst(20);
        PagingInvoker<String> tgt = createTarget();

        // ## Act ##
        final List<String> markList = new ArrayList<String>();
        PagingResultBean<String> rb = tgt.invokePaging(new PagingHandler<String>() {
            public PagingBean getPagingBean() {
                return pagingBean;
            }

            public int count() {
                markList.add("count");
                return 19;
            }

            public List<String> paging() {
                markList.add("paging");
                return selectedList;
            }
        });

        // ## Assert ##
        assertEquals(19, rb.size());
        assertEquals(19, rb.getAllRecordCount());
        assertEquals(1, rb.getAllPageCount());
        assertEquals(1, rb.getOrderByClause().getOrderByList().size());
        assertEquals("count", markList.get(0));
        assertEquals("paging", markList.get(1));
    }
    
    public void test_invokePaging_onePage_countLater() {
        // ## Arrange ##
        final List<String> selectedList = new ArrayList<String>();
        fillList(selectedList, 19);
        final SimplePagingBean pagingBean = new SimplePagingBean();
        pagingBean.getSqlClause().registerOrderBy("aaa", "bbb", true);
        pagingBean.fetchFirst(20);
        PagingInvoker<String> tgt = createTarget();
        tgt.countLater();
        
        // ## Act ##
        final List<String> markList = new ArrayList<String>();
        PagingResultBean<String> rb = tgt.invokePaging(new PagingHandler<String>() {
            public PagingBean getPagingBean() {
                return pagingBean;
            }
            
            public int count() {
                markList.add("count");
                return 19;
            }
            
            public List<String> paging() {
                markList.add("paging");
                return selectedList;
            }
        });
        
        // ## Assert ##
        assertEquals(19, rb.size());
        assertEquals(19, rb.getAllRecordCount());
        assertEquals(1, rb.getAllPageCount());
        assertEquals(1, rb.getOrderByClause().getOrderByList().size());
        assertEquals("paging", markList.get(0));
        assertEquals(1, markList.size());
    }

    public void test_invokePaging_twoPage() {
        // ## Arrange ##
        final List<String> selectedList = new ArrayList<String>();
        fillList(selectedList, 20);
        final SimplePagingBean pagingBean = new SimplePagingBean();
        pagingBean.getSqlClause().registerOrderBy("aaa", "bbb", true);
        pagingBean.fetchFirst(20);
        PagingInvoker<String> tgt = createTarget();

        // ## Act ##
        final List<String> markList = new ArrayList<String>();
        PagingResultBean<String> rb = tgt.invokePaging(new PagingHandler<String>() {
            public PagingBean getPagingBean() {
                return pagingBean;
            }

            public int count() {
                markList.add("count");
                return 38;
            }

            public List<String> paging() {
                markList.add("paging");
                return selectedList;
            }
        });

        // ## Assert ##
        assertEquals(20, rb.size());
        assertEquals(38, rb.getAllRecordCount());
        assertEquals(2, rb.getAllPageCount());
        assertEquals(1, rb.getOrderByClause().getOrderByList().size());
        assertEquals("count", markList.get(0));
        assertEquals("paging", markList.get(1));
    }

    public void test_invokePaging_threePage_just() {
        // ## Arrange ##
        final List<String> selectedList = new ArrayList<String>();
        fillList(selectedList, 20);
        final SimplePagingBean pagingBean = new SimplePagingBean();
        pagingBean.getSqlClause().registerOrderBy("aaa", "bbb", true);
        pagingBean.fetchFirst(20);
        PagingInvoker<String> tgt = createTarget();

        // ## Act ##
        final List<String> markList = new ArrayList<String>();
        PagingResultBean<String> rb = tgt.invokePaging(new PagingHandler<String>() {
            public PagingBean getPagingBean() {
                return pagingBean;
            }

            public int count() {
                markList.add("count");
                return 60;
            }

            public List<String> paging() {
                markList.add("paging");
                return selectedList;
            }
        });

        // ## Assert ##
        assertEquals(20, rb.size());
        assertEquals(60, rb.getAllRecordCount());
        assertEquals(3, rb.getAllPageCount());
        assertEquals(1, rb.getOrderByClause().getOrderByList().size());
        assertEquals("count", markList.get(0));
        assertEquals("paging", markList.get(1));
    }

    // ===================================================================================
    //                                                       isNecessaryToReadCountLater()
    //                                                       =============================
    public void test_isNecessaryToReadCountLater() {
        // ## Arrange ##
        List<String> selectedList = new ArrayList<String>();
        PagingBean pagingBean = new SimplePagingBean();
        pagingBean.fetchFirst(30);
        PagingInvoker<String> tgt = createTarget();

        // ## Act & Assert ##
        fillList(selectedList, 28);
        assertFalse(tgt.isNecessaryToReadCountLater(selectedList, pagingBean));
        fillList(selectedList, 29);
        assertFalse(tgt.isNecessaryToReadCountLater(selectedList, pagingBean));
        fillList(selectedList, 30);
        assertTrue(tgt.isNecessaryToReadCountLater(selectedList, pagingBean));
        fillList(selectedList, 31);
        assertTrue(tgt.isNecessaryToReadCountLater(selectedList, pagingBean));
        fillList(selectedList, 60);
        assertTrue(tgt.isNecessaryToReadCountLater(selectedList, pagingBean));
        fillList(selectedList, 61);
        assertTrue(tgt.isNecessaryToReadCountLater(selectedList, pagingBean));
    }

    // ===================================================================================
    //                                                        isNecessaryToReadPageAgain()
    //                                                        ============================
    public void test_isNecessaryToReadPageAgain() {
        // ## Arrange ##
        List<String> selectedList = new ArrayList<String>();
        PagingResultBean<String> rb = new PagingResultBean<String>();
        rb.setSelectedList(selectedList);
        PagingInvoker<String> tgt = createTarget();

        // ## Act & Assert ##
        rb.setAllRecordCount(0);
        assertFalse(tgt.isNecessaryToReadPageAgain(rb));
        rb.setAllRecordCount(1);
        assertTrue(tgt.isNecessaryToReadPageAgain(rb));
        selectedList.add("one");
        assertFalse(tgt.isNecessaryToReadPageAgain(rb));
        rb.setAllRecordCount(0);
        assertFalse(tgt.isNecessaryToReadPageAgain(rb));
    }

    // ===================================================================================
    //                                                                         Test Helper
    //                                                                         ===========
    protected void fillList(List<String> selectedList, int size) {
        selectedList.clear();
        for (int i = 0; i < size; i++) {
            selectedList.add("element" + i);
        }
    }

    protected PagingInvoker<String> createTarget() {
        return new PagingInvoker<String>("dummy");
    }
}
