package org.seasar.dbflute.logic.doc.lreverse;

import java.io.File;
import java.util.List;

import org.apache.torque.engine.database.model.Table;

/**
 * @author jflute
 */
public class DfLReverseOutputResource {

    protected final File _xlsFile;
    protected final List<Table> _tableList;
    protected final Integer _sectionNo;
    protected final String _mainName;

    public DfLReverseOutputResource(File xlsFile, List<Table> tableList, Integer sectionNo, String mainName) {
        _xlsFile = xlsFile;
        _tableList = tableList;
        _sectionNo = sectionNo;
        _mainName = mainName;
    }

    public File getXlsFile() {
        return _xlsFile;
    }

    public List<Table> getTableList() {
        return _tableList;
    }

    public void addTable(Table table) {
        _tableList.add(table);
    }

    public int getSectionNo() {
        return _sectionNo;
    }

    public String getMainName() {
        return _mainName;
    }
}
