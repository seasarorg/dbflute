package org.seasar.dbflute.helper.datahandler.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.datahandler.DfXlsDataHandler;
import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.impl.SqlWriter;
import org.seasar.extension.dataset.impl.XlsReader;

public class DfXlsDataHandlerImpl implements DfXlsDataHandler {

    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSeparatedDataHandlerImpl.class);

    public List<DataSet> readSeveralData(String dataDirectoryName) {
        final List<File> xlsList = getXlsList(dataDirectoryName);
        final ArrayList<DataSet> ls = new ArrayList<DataSet>();
        for (File file : xlsList) {
            final XlsReader xlsReader = new XlsReader(file);
            ls.add(xlsReader.read());
        }
        return ls;
    }

    public void writeSeveralData(String dataDirectoryName, DataSource dataSource) {
        final List<File> xlsList = getXlsList(dataDirectoryName);
        for (File file : xlsList) {
            _log.info("/= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = ");
            _log.info("writeData(" + file + ")");
            _log.info("= = = = = = =/");
            final XlsReader xlsReader = new XlsReader(file);
            final DataSet dataSet = xlsReader.read();
            final SqlWriter sqlWriter = new SqlWriter(dataSource);
            sqlWriter.write(dataSet);
        }
    }

    public List<File> getXlsList(String dataDirectoryName) {
        List<File> _xlsList = new ArrayList<File>();
        final File dir = new File(dataDirectoryName);
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".xls");
            }
        };
        final File[] listFiles = dir.listFiles(filter);
        for (File file : listFiles) {
            _xlsList.add(file);
        }
        return _xlsList;
    }
}
