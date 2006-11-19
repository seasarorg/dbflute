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
     *
     * @throws BuildException
     */
    public void execute(DfSqlFileRunner runner, List<File> fileList) throws BuildException {
        try {
            _log.debug("/************************************************************************************");

            int goodSqlCount = 0;
            int totalSqlCount = 0;

            for (final File file : fileList) {
                if (!file.exists()) {
                    throw new FileNotFoundException("The file '" + file.getPath() + "' does not exist.");
                }

                if (_log.isDebugEnabled()) {
                    final String mitameJushi = "_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/";
                    _log.debug("");
                    _log.debug(mitameJushi + mitameJushi);
                    _log.debug("sqlFile: " + file);
                    _log.debug("_/_/_/_/");
                }
                runner.setSrc(file);
                runner.runTransaction();

                goodSqlCount = goodSqlCount + runner.getGoodSqlCount();
                totalSqlCount = totalSqlCount + runner.getTotalSqlCount();
            }
            _log.debug("*****************/ {" + goodSqlCount + " of " + totalSqlCount + "}");
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
