package org.seasar.dbflute.logic.doc.dataxls;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;
import org.seasar.dbflute.logic.doc.ldreverse.DfLoadDataExtractor;
import org.seasar.dbflute.unit.PlainTestCase;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 */
public class DfTemplateDataExtractorTest extends PlainTestCase {

    @Test
    public void test_formatDate_basic() {
        // ## Arrange ##
        DfLoadDataExtractor extractor = new DfLoadDataExtractor(null);
        Date date = DfTypeUtil.toDate("0001/01/01 00:00:00.000");

        // ## Act ##
        String actual = extractor.formatDate(date, "yyyy-MM-dd HH:mm:ss.SSS");

        // ## Assert ##
        assertEquals("0001-01-01 00:00:00.000", actual);
    }

    @Test
    public void test_formatDate_BC() {
        // ## Arrange ##
        DfLoadDataExtractor extractor = new DfLoadDataExtractor(null);
        Date date = DfTypeUtil.toDate("BC0001/12/31 23:59:59.999");

        // ## Act ##
        String actual = extractor.formatDate(date, "yyyy-MM-dd HH:mm:ss.SSS");

        // ## Assert ##
        assertEquals("BC0001-12-31 23:59:59.999", actual);
    }
}
