package org.seasar.dbflute.logic.doc.lreverse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfCollectionUtil.AccordingToOrderIdExtractor;
import org.seasar.dbflute.util.DfCollectionUtil.AccordingToOrderResource;

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

    public void acceptTableOrder(List<String> tableNameList) {
        final List<String> lowerList = new ArrayList<String>();
        for (String tableName : tableNameList) {
            lowerList.add(tableName.toLowerCase());
        }
        final AccordingToOrderResource<Table, String> resource = new AccordingToOrderResource<Table, String>();
        resource.setupResource(lowerList, new AccordingToOrderIdExtractor<Table, String>() {
            public String extractId(Table element) {
                return element.getTableDbName().toLowerCase();
            }
        });
        DfCollectionUtil.orderAccordingTo(_tableList, resource);
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
