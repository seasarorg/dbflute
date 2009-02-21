package org.seasar.dbflute.logic.jdbcdl;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;

import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.util.net.DfURLUtil;

/**
 * @author jflute
 * @since 0.9.3 (2009/02/21 Saturday)
 */
public class DfJDBCDriverDownloadHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    //    /** Log instance. */
    //    private static final Log _log = LogFactory.getLog(JDBCDriverDownloadHandler.class);
    protected static final String urlBase = "http://dbflute.sandbox.seasar.org/meta/jdbc";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private DfBasicProperties basicProperties;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfJDBCDriverDownloadHandler(DfBasicProperties basicProperties) {
        this.basicProperties = basicProperties;
    }

    // ===================================================================================
    //                                                                          Initialize
    //                                                                          ==========
    public void downloadJDBCDriverIfNeeds(String jdbcDriverFileName, String downloadDir) {
        // * * * *
        // Making
        // * * * *
        if (0 == 0) {
            throw new UnsupportedOperationException("Now making");
        }

        if (!needsDownload(jdbcDriverFileName, downloadDir)) {
            return;
        }
        final String urlString = urlBase + "/" + jdbcDriverFileName;
        final String outputFileName = downloadDir + "/" + jdbcDriverFileName;
        final URL url = DfURLUtil.create(urlString);
        DfURLUtil.makeFileAndClose(url, outputFileName);
    }

    protected boolean needsDownload(String jdbcDriverFileName, String downloadDir) {
        // * * * * * * * * * * * 
        // This needs to review!
        // * * * * * * * * * * * 
        final String dbMark;
        if (basicProperties.isDatabaseMySQL()) {
            dbMark = "mysql";
        } else if (basicProperties.isDatabasePostgreSQL()) {
            dbMark = "postgresql";
        } else if (basicProperties.isDatabaseOracle()) {
            dbMark = "ojdbc";
        } else if (basicProperties.isDatabaseDB2()) {
            dbMark = "db2jcc";
        } else if (basicProperties.isDatabaseSqlServer()) {
            dbMark = "sqljdbc";
        } else if (basicProperties.isDatabaseH2()) {
            dbMark = "h2";
        } else if (basicProperties.isDatabaseDerby()) {
            dbMark = "derby";
        } else {
            return false;
        }

        final File[] listFiles = new File(downloadDir).listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.getName().endsWith(".jar");
            }
        });
        if (listFiles != null) {
            for (File jarfile : listFiles) {
                if (jarfile.getName().contains(dbMark)) {
                    return false;
                }
            }
        }
        final String outputFileName = downloadDir + "/" + jdbcDriverFileName;
        if (new File(outputFileName).exists()) {
            return false;
        }
        return true;
    }
}
