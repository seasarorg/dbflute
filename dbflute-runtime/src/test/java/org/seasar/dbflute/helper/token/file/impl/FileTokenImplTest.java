package org.seasar.dbflute.helper.token.file.impl;

import java.io.ByteArrayInputStream;
import java.util.List;

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
        String third = "\"a\",\"b\",\"cc\",\"d\",\"e\"";
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
                    assertEquals("c\"\"c", valueList.get(2));
                    assertEquals("d", valueList.get(3));
                    assertEquals("e", valueList.get(4));
                } else if (index == 2) {
                    assertEquals("a", valueList.get(0));
                    assertEquals("b", valueList.get(1));
                    assertEquals("cc", valueList.get(2));
                    assertEquals("d", valueList.get(3));
                    assertEquals("e", valueList.get(4));
                }
                ++index;
            }
        }, new FileTokenizingOption().beginFirstLine().delimitateByComma().encodeAsUTF8());
    }
}
