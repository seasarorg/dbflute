package org.seasar.dbflute.logic.jdbc.metadata.basic;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.seasar.dbflute.logic.jdbc.metadata.info.DfForeignKeyMeta;
import org.seasar.dbflute.unit.core.PlainTestCase;

/**
 * @author jflute
 * @since 1.0.5F (2014/05/07 Wednesday)
 */
public class DfForeignKeyExtractorTest extends PlainTestCase {

    public void test_immobilizeOrder_basic() throws Exception {
        // ## Arrange ##
        DfForeignKeyExtractor extractor = new DfForeignKeyExtractor();
        Map<String, DfForeignKeyMeta> fkMap = newLinkedHashMap();
        {
            DfForeignKeyMeta meta = new DfForeignKeyMeta();
            meta.putColumnNameAll(newLinkedHashMap("b", "1", "c", "2"));
            fkMap.put("foo", meta);
        }
        {
            DfForeignKeyMeta meta = new DfForeignKeyMeta();
            meta.putColumnNameAll(newLinkedHashMap("a", "1", "b", "2"));
            fkMap.put("bar", meta);
        }
        {
            DfForeignKeyMeta meta = new DfForeignKeyMeta();
            meta.putColumnNameAll(newLinkedHashMap("c", "1", "b", "2"));
            fkMap.put("qux", meta);
        }
        {
            DfForeignKeyMeta meta = new DfForeignKeyMeta();
            meta.putColumnNameAll(newLinkedHashMap("c", "7", "a", "2"));
            fkMap.put("corge", meta);
        }
        {
            DfForeignKeyMeta meta = new DfForeignKeyMeta();
            meta.putColumnNameAll(newLinkedHashMap("c", "8"));
            fkMap.put("grault", meta);
        }

        // ## Act ##
        Map<String, DfForeignKeyMeta> sortedMap = extractor.immobilizeOrder(fkMap);

        // ## Assert ##
        assertEquals(5, sortedMap.size());
        Set<Entry<String, DfForeignKeyMeta>> entrySet = sortedMap.entrySet();
        Iterator<Entry<String, DfForeignKeyMeta>> iterator = entrySet.iterator();
        {
            Entry<String, DfForeignKeyMeta> entry = iterator.next();
            assertEquals("bar", entry.getKey());
            assertEquals("1", entry.getValue().getColumnNameMap().get("a"));
            assertEquals("2", entry.getValue().getColumnNameMap().get("b"));
        }
        {
            Entry<String, DfForeignKeyMeta> entry = iterator.next();
            assertEquals("foo", entry.getKey());
            assertEquals("1", entry.getValue().getColumnNameMap().get("b"));
            assertEquals("2", entry.getValue().getColumnNameMap().get("c"));
        }
        {
            Entry<String, DfForeignKeyMeta> entry = iterator.next();
            assertEquals("grault", entry.getKey());
            assertEquals("8", entry.getValue().getColumnNameMap().get("c"));
        }
        {
            Entry<String, DfForeignKeyMeta> entry = iterator.next();
            assertEquals("corge", entry.getKey());
            assertEquals("7", entry.getValue().getColumnNameMap().get("c"));
            assertEquals("2", entry.getValue().getColumnNameMap().get("a"));
        }
        {
            Entry<String, DfForeignKeyMeta> entry = iterator.next();
            assertEquals("qux", entry.getKey());
            assertEquals("1", entry.getValue().getColumnNameMap().get("c"));
            assertEquals("2", entry.getValue().getColumnNameMap().get("b"));
        }
    }
}
