package org.seasar.dbflute.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * @author jflute
 */
public class DfSqlStringUtilTest {

    @Test
    public void test_removeLineComment_Lf() throws Exception {
        String sql = "aaaa\n";
        sql = sql + "bbbb\n";
        sql = sql + "--\n";
        sql = sql + "cccc\n";
        String removed = DfSqlStringUtil.removeLineComment(sql);
        System.out.println(removed);
        assertEquals("aaaa\nbbbb\ncccc\n", removed);
    }
    
    @Test
    public void test_removeLineComment_SystemLineSeparator() throws Exception {
        String sql = "aaaa" + getLineSeparator();
        sql = sql + "bbbb" + getLineSeparator();
        sql = sql + "--" + getLineSeparator();
        sql = sql + "cccc" + getLineSeparator();
        String removed = DfSqlStringUtil.removeLineComment(sql);
        System.out.println(removed);
        assertFalse(removed.contains("--"));
        assertFalse(removed.contains("\r"));
        assertEquals("aaaa\nbbbb\ncccc\n", removed);
    }
    
    protected String getLineSeparator() {
        return System.getProperty("line.separator");
    }
}
