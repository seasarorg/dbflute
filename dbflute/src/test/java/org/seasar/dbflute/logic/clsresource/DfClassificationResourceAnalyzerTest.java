package org.seasar.dbflute.logic.clsresource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.seasar.dbflute.properties.bean.DfClassificationElement;
import org.seasar.dbflute.properties.bean.DfClassificationTop;

/**
 * @author jflute
 * @since 0.8.2 (2008/10/22 Wednesday)
 */
public class DfClassificationResourceAnalyzerTest {

    @Test
    public void test_analuze_by_lineList() {
        // ## Arrange ##
        final DfClassificationResourceAnalyzer analyzer = new DfClassificationResourceAnalyzer();
        final List<String> lineList = new ArrayList<String>();
        lineList.add("<encoding=\"UTF-8\"/>");
        lineList.add("AAAAAAAAAAAA");
        lineList.add("BBBB");
        lineList.add("[会員ステータス]");
        lineList.add("$ MemberStatus, 会員の状態を表す");
        lineList.add("- FML, Formalized, 正式会員, 正式な会員を表す");
        lineList.add("- PVS, Provisinal, 仮会員, 仮の会員を表す");
        lineList.add("- WDL, Withdrawal, 退会会員, 退会した会員を表す");
        lineList.add("CCCCCCCCCCCC");
        lineList.add("[会員ステータス2]");
        lineList.add("$ MemberStatus2, 会員の状態を表す");
        lineList.add("- FML, Formalized, 正式会員, 正式な会員を表す");
        lineList.add("- PVS, Provisinal, 仮会員, 仮の会員を表す");
        lineList.add("DDDDDDDDDD");
        lineList.add("- WDL, Withdrawal, 退会会員, 退会した会員を表す");
        lineList.add("EEEEEEEEEEEEE");
        lineList.add("[Dummy]");
        lineList.add("FFFFFFFFFFFFFFFFFF");
        lineList.add("[Dummy2]");
        lineList.add("$ ABC, DEF");
        lineList.add("HHHHHHHHHHHHHHHHHHHHHH");

        // ## Act ##
        final List<DfClassificationTop> classificationTopList = analyzer.analyze(lineList);

        // ## Assert ##
        assertNotNull(classificationTopList);
        assertFalse(classificationTopList.isEmpty());
        assertEquals(2, classificationTopList.size());
        {
            final DfClassificationTop top = classificationTopList.get(0);
            assertEquals("MemberStatus", top.getClassificationName());
            assertEquals("会員の状態を表す", top.getTopComment());
            assertEquals(3, top.getClassificationElementList().size());
            final List<DfClassificationElement> classificationElementList = top.getClassificationElementList();
            {
                final DfClassificationElement element = classificationElementList.get(0);
                assertEquals("FML", element.getCode());
                assertEquals("Formalized", element.getName());
                assertEquals("正式会員", element.getAlias());
                assertEquals("正式な会員を表す", element.getComment());
            }
            {
                final DfClassificationElement element = classificationElementList.get(1);
                assertEquals("PVS", element.getCode());
                assertEquals("Provisinal", element.getName());
                assertEquals("仮会員", element.getAlias());
                assertEquals("仮の会員を表す", element.getComment());
            }
            {
                final DfClassificationElement element = classificationElementList.get(2);
                assertEquals("WDL", element.getCode());
                assertEquals("Withdrawal", element.getName());
                assertEquals("退会会員", element.getAlias());
                assertEquals("退会した会員を表す", element.getComment());
            }
        }
        {
            final DfClassificationTop top = classificationTopList.get(1);
            assertEquals("MemberStatus2", top.getClassificationName());
            assertEquals("会員の状態を表す", top.getTopComment());
            assertEquals(2, top.getClassificationElementList().size());
            final List<DfClassificationElement> classificationElementList = top.getClassificationElementList();
            {
                final DfClassificationElement element = classificationElementList.get(0);
                assertEquals("FML", element.getCode());
                assertEquals("Formalized", element.getName());
                assertEquals("正式会員", element.getAlias());
                assertEquals("正式な会員を表す", element.getComment());
            }
            {
                final DfClassificationElement element = classificationElementList.get(1);
                assertEquals("PVS", element.getCode());
                assertEquals("Provisinal", element.getName());
                assertEquals("仮会員", element.getAlias());
                assertEquals("仮の会員を表す", element.getComment());
            }
        }
    }
}
