package org.seasar.dbflute.helper.jdbc.sqlfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;

public class DfSqlFileFireMan {

    /** Log instance. */
    private static Log _log = LogFactory.getLog(DfSqlFileFireMan.class);

    /**
     * Load the sql file and then execute it.
     * @throws BuildException
     */
    public void execute(DfSqlFileRunner runner, List<File> fileList) throws BuildException {
        try {
            int goodSqlCount = 0;
            int totalSqlCount = 0;
            for (final File file : fileList) {
                if (!file.exists()) {
                    throw new FileNotFoundException("The file '" + file.getPath() + "' does not exist.");
                }

                if (_log.isInfoEnabled()) {
                    _log.info("[SQL File] " + file);
                }

                runner.setSrc(file);
                runner.runTransaction();

                goodSqlCount = goodSqlCount + runner.getGoodSqlCount();
                totalSqlCount = totalSqlCount + runner.getTotalSqlCount();
            }
            String msg = "[Fired SQL] success=" + goodSqlCount + " failure=" + (totalSqlCount - goodSqlCount);
            msg = msg + " (in " + fileList.size() + " files)";
            _log.debug(msg);
        } catch (Exception e) {
            _log.warn(getClass().getName() + "#execute() threw the exception!", e);
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}
