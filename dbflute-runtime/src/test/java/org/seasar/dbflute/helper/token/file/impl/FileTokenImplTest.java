package org.seasar.dbflute.helper.token.file.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.helper.token.file.FileMakingCallback;
import org.seasar.dbflute.helper.token.file.FileMakingOption;
import org.seasar.dbflute.helper.token.file.FileMakingRowResource;
import org.seasar.dbflute.helper.token.file.FileTokenizingCallback;
import org.seasar.dbflute.helper.token.file.FileTokenizingOption;
import org.seasar.dbflute.helper.token.file.FileTokenizingRowResource;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.4 (2009/03/14 Saturday)
 */
public class FileTokenImplTest extends PlainTestCase {

    public void test_tokenize() throws Exception {
        // ## Arrange ##
        FileTokenImpl impl = new FileTokenImpl();
        String first = "\"a\",\"b\",\"cc\",\"d\",\"e\"";
        String second = "\"a\",\"b\",\"c\"\"c\",\"d\",\"e\"";
        String third = "\"a\",\"b\",\"c\"\"\"\"c\",\"d\",\"e\"";
        String all = first + getLineSeparator() + second + getLineSeparator() + third;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(all.getBytes("UTF-8"));

        // ## Act ##
        impl.tokenize(inputStream, new FileTokenizingCallback() {
            int index = 0;

            public void handleRowResource(FileTokenizingRowResource fileTokenizingRowResource) {
                // ## Assert ##
                List<String> valueList = fileTokenizingRowResource.getValueList();
                log(valueList);
                if (index == 0) {
                    assertEquals("a", valueList.get(0));
                    assertEquals("b", valueList.get(1));
                    assertEquals("cc", valueList.get(2));
                    assertEquals("d", valueList.get(3));
                    assertEquals("e", valueList.get(4));
                } else if (index == 1) {
                    assertEquals("a", valueList.get(0));
                    assertEquals("b", valueList.get(1));
                    assertEquals("c\"c", valueList.get(2));
                    assertEquals("d", valueList.get(3));
                    assertEquals("e", valueList.get(4));
                } else if (index == 2) {
                    assertEquals("a", valueList.get(0));
                    assertEquals("b", valueList.get(1));
                    assertEquals("c\"\"c", valueList.get(2));
                    assertEquals("d", valueList.get(3));
                    assertEquals("e", valueList.get(4));
                }
                ++index;
            }
        }, new FileTokenizingOption().beginFirstLine().delimitateByComma().encodeAsUTF8());
    }

    public void test_make() throws Exception {
        // ## Arrange ##
        FileTokenImpl impl = new FileTokenImpl();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // ## Act ##
        impl.make(outputStream, new FileMakingCallback() {
            int index = 0;

            public FileMakingRowResource getRowResource() {
                if (index > 2) {
                    return null;
                }
                FileMakingRowResource rowResource = new FileMakingRowResource();
                List<String> valueList = new ArrayList<String>();
                if (index == 0) {
                    valueList.add("a");
                    valueList.add("b");
                    valueList.add("cc");
                    valueList.add("d");
                    valueList.add("e");
                } else if (index == 1) {
                    valueList.add("a");
                    valueList.add("b");
                    valueList.add("c\"c");
                    valueList.add("d");
                    valueList.add("e");
                } else if (index == 2) {
                    valueList.add("a");
                    valueList.add("b");
                    valueList.add("c\"\"c");
                    valueList.add("d");
                    valueList.add("e");
                }
                rowResource.setValueList(valueList);
                ++index;
                return rowResource;
            }
        }, new FileMakingOption().delimitateByComma().encodeAsUTF8().separateLf());

        // ## Assert ##
        String actual = outputStream.toString();
        log(actual);
        String[] split = actual.split("\n");
        assertEquals("\"a\",\"b\",\"cc\",\"d\",\"e\"", split[0]);
        assertEquals("\"a\",\"b\",\"c\"\"c\",\"d\",\"e\"", split[1]);
        assertEquals("\"a\",\"b\",\"c\"\"\"\"c\",\"d\",\"e\"", split[2]);
    }

    public void test_make_goodByeDoubleQuotation() throws Exception {
        // ## Arrange ##
        FileTokenImpl impl = new FileTokenImpl();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // ## Act ##
        impl.make(outputStream, new FileMakingCallback() {
            int index = 0;

            public FileMakingRowResource getRowResource() {
                if (index > 2) {
                    return null;
                }
                FileMakingRowResource rowResource = new FileMakingRowResource();
                List<String> valueList = new ArrayList<String>();
                if (index == 0) {
                    valueList.add("a");
                    valueList.add("b");
                    valueList.add("cc");
                    valueList.add("d");
                    valueList.add("e");
                } else if (index == 1) {
                    valueList.add("a");
                    valueList.add("b");
                    valueList.add("c\"c");
                    valueList.add("d");
                    valueList.add("e");
                } else if (index == 2) {
                    valueList.add("a");
                    valueList.add("b");
                    valueList.add("c\"\"c");
                    valueList.add("d");
                    valueList.add("e");
                }
                rowResource.setValueList(valueList);
                ++index;
                return rowResource;
            }
        }, new FileMakingOption().delimitateByComma().encodeAsUTF8().separateLf().goodByeDoubleQuotation());

        // ## Assert ##
        String actual = outputStream.toString();
        log(actual);
        String[] split = actual.split("\n");
        assertEquals("a,b,cc,d,e", split[0]);
        assertEquals("a,b,c\"c,d,e", split[1]);
        assertEquals("a,b,c\"\"c,d,e", split[2]);
    }
}
