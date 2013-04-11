package org.seasar.dbflute.logic.doc.lreverse;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author jflute
 */
public class DfLReverseExistingXlsInfo {

    protected final Map<File, List<String>> _existingXlsTableListMap;
    protected final Map<String, File> _tableExistingXlsMap;

    public DfLReverseExistingXlsInfo(Map<File, List<String>> existingXlsTableListMap,
            Map<String, File> tableExistingXlsMap) {
        _existingXlsTableListMap = existingXlsTableListMap;
        _tableExistingXlsMap = tableExistingXlsMap;
    }

    public Map<File, List<String>> getExistingXlsTableListMap() {
        return _existingXlsTableListMap;
    }

    public Map<String, File> getTableExistingXlsMap() {
        return _tableExistingXlsMap;
    }
}
