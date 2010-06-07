package org.seasar.dbflute.logic.doc.historyhtml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.infra.schemadiff.SchemaDiffFile;
import org.seasar.dbflute.logic.jdbc.diff.DfSchemaDiff;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/07 Monday)
 */
public class DfSchemaHistory {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                          Load History
    //                                          ------------
    protected final List<DfSchemaDiff> _schemaDiffList = DfCollectionUtil.newArrayList();
    protected boolean _existsSchemaDiff;

    // ===================================================================================
    //                                                                           Serialize
    //                                                                           =========
    public void serializeSchemaDiff(DfSchemaDiff schemaDiff) throws IOException {
        final String path = getSchemaDiffFilePath();
        final SchemaDiffFile diffFile = createSchemaDiffFile();
        final File file = new File(path);

        // ordered by DIFF_DATE desc
        final Map<String, Object> serializedMap = DfCollectionUtil.newLinkedHashMap();
        final Map<String, Object> schemaDiffMap = schemaDiff.createDiffMap();
        serializedMap.put((String) schemaDiffMap.get(DfSchemaDiff.DIFF_DATE_KEY), schemaDiffMap);

        if (file.exists()) {
            FileInputStream ins = null;
            try {
                ins = new FileInputStream(file);
                final Map<String, Object> existingMap = diffFile.readMap(ins);
                final Set<Entry<String, Object>> entrySet = existingMap.entrySet();
                int count = 0;
                final int historyLimit = getHistoryLimit();
                final boolean historyLimitValid = historyLimit >= 0;
                for (Entry<String, Object> entry : entrySet) {
                    if (historyLimitValid && count >= historyLimit) {
                        break;
                    }
                    serializedMap.put(entry.getKey(), entry.getValue());
                    ++count;
                }
            } finally {
                if (ins != null) {
                    ins.close();
                }
            }
        } else {
            file.createNewFile();
        }

        FileOutputStream ous = null;
        try {
            ous = new FileOutputStream(path);
            diffFile.writeMap(ous, serializedMap);
        } finally {
            if (ous != null) {
                ous.close();
            }
        }
    }

    protected int getHistoryLimit() {
        return -1; // as default (no limit)
    }

    // ===================================================================================
    //                                                                        Load History
    //                                                                        ============
    public void loadHistory() {
        final File file = new File(getSchemaDiffFilePath());
        if (!file.exists()) {
            _existsSchemaDiff = false;
        }
        final SchemaDiffFile schemaDiffFile = createSchemaDiffFile();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            final Map<String, Object> diffMap = schemaDiffFile.readMap(fis);
            acceptDiffMap(diffMap);
        } catch (FileNotFoundException ignored) {
            _existsSchemaDiff = false;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignored) {
                }
            }
        }
        _existsSchemaDiff = true;
    }

    protected void acceptDiffMap(Map<String, Object> diffMap) {
        final Set<Entry<String, Object>> entrySet = diffMap.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            final String key = entry.getKey(); // diffDate
            final Object value = entry.getValue();
            assertDiffElementMap(key, value);
            @SuppressWarnings("unchecked")
            final Map<String, Object> schemaDiffMap = (Map<String, Object>) value;
            DfSchemaDiff schemaDiff = new DfSchemaDiff();
            schemaDiff.acceptDiffMap(schemaDiffMap);
            _schemaDiffList.add(schemaDiff);
        }
    }

    protected void assertDiffElementMap(String key, Object value) {
        if (!(value instanceof Map<?, ?>)) { // basically no way
            String msg = "The elements of diff should be Map:";
            msg = msg + " date=" + key + " value=" + value;
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                              Status
    //                                                                              ======
    public boolean existsHistory() {
        return _existsSchemaDiff && !_schemaDiffList.isEmpty();
    }

    // ===================================================================================
    //                                                                         Schema Diff
    //                                                                         ===========
    protected SchemaDiffFile createSchemaDiffFile() {
        return new SchemaDiffFile();
    }

    public String getSchemaDiffFilePath() {
        return getBasicProperties().getSchemaDiffFilePath();
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBasicProperties getBasicProperties() {
        return DfBuildProperties.getInstance().getBasicProperties();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<DfSchemaDiff> getSchemaDiffList() {
        return _schemaDiffList;
    }
}
