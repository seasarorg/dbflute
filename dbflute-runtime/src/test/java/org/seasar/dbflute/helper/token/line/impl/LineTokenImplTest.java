package org.seasar.dbflute.helper.token.line.impl;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.helper.token.line.LineMakingOption;
import org.seasar.dbflute.helper.token.line.LineTokenizingOption;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.4 (2009/03/14 Saturday)
 */
public class LineTokenImplTest extends PlainTestCase {

    public void test_tokenize_trimDoubleQuotation_basic() {
        // ## Arrange ##
        LineTokenImpl impl = new LineTokenImpl();
        LineTokenizingOption option = new LineTokenizingOption();
        option.delimitateByComma();
        option.trimDoubleQuotation();

        // ## Act ##
        List<String> list = impl.tokenize("\"a\",\"b\",\"c\",\"d\",\"e\"", option);

        // ## Assert ##
        log(list);
        assertEquals(5, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
        assertEquals("d", list.get(3));
        assertEquals("e", list.get(4));
    }

    public void test_tokenize_trimDoubleQuotation_escape() {
        // ## Arrange ##
        LineTokenImpl impl = new LineTokenImpl();
        LineTokenizingOption option = new LineTokenizingOption();
        option.delimitateByComma();
        option.trimDoubleQuotation();

        // ## Act ##
        List<String> list = impl.tokenize("\"a\",\"b\",\"c\"\"c\",\"d\",\"e\"", option);

        // ## Assert ##
        log(list);
        assertEquals(5, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c\"c", list.get(2));
        assertEquals("d", list.get(3));
        assertEquals("e", list.get(4));
    }

    public void test_make_quoteByDoubleQuotation_basic() {
        // ## Arrange ##
        LineTokenImpl impl = new LineTokenImpl();
        LineMakingOption option = new LineMakingOption();
        option.delimitateByComma();
        option.quoteByDoubleQuotation();
        List<String> valueList = new ArrayList<String>();
        valueList.add("a");
        valueList.add("b");
        valueList.add("cc");
        valueList.add("d");
        valueList.add("e");

        // ## Act ##
        String line = impl.make(valueList, option);

        // ## Assert ##
        log(line);
        assertEquals("\"a\",\"b\",\"cc\",\"d\",\"e\"", line);
    }

    public void test_make_quoteByDoubleQuotation_escape() {
        // ## Arrange ##
        LineTokenImpl impl = new LineTokenImpl();
        LineMakingOption option = new LineMakingOption();
        option.delimitateByComma();
        option.quoteByDoubleQuotation();
        List<String> valueList = new ArrayList<String>();
        valueList.add("a");
        valueList.add("b");
        valueList.add("c\"c");
        valueList.add("d");
        valueList.add("e");

        // ## Act ##
        String line = impl.make(valueList, option);

        // ## Assert ##
        log(line);
        assertEquals("\"a\",\"b\",\"c\"\"c\",\"d\",\"e\"", line);
    }
}
