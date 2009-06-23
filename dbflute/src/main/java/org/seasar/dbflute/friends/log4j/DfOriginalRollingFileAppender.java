package org.seasar.dbflute.friends.log4j;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Layout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;

/**
 * DBFlute original appender with rolling-file that extends the RollingFileAppender of Logj4. <br />
 * @author jflute
 * @since 0.9.5.1 (2009/06/23 Tuesday)
 */
public class DfOriginalRollingFileAppender extends RollingFileAppender {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfOriginalRollingFileAppender() {
    }

    public DfOriginalRollingFileAppender(Layout layout, String filename, boolean append) throws IOException {
        super(layout, filename, append);
    }

    // ===================================================================================
    //                                                                    Rolling Override
    //                                                                    ================
    @Override
    public void rollOver() {
        if (qw != null) {
            LogLog.debug("rolling over count=" + ((CountingQuietWriter) qw).getCount());
        }
        LogLog.debug("maxBackupIndex=" + maxBackupIndex);

        if (maxBackupIndex > 0) {
            File file = null;
            File target = null;

            final String oldestBackupFileName = buildBackupFileName(fileName, maxBackupIndex);
            file = new File(oldestBackupFileName);
            if (file.exists()) {
                file.delete();
            }

            for (int i = maxBackupIndex - 1; i >= 1; i--) {
                file = new File(buildBackupFileName(fileName, i));
                if (file.exists()) {
                    target = new File(buildBackupFileName(fileName, (i + 1)));
                    LogLog.debug("Renaming file " + file + " to " + target);
                    file.renameTo(target);
                }
            }

            final String newestBackupFile = buildBackupFileName(fileName, 1);
            target = new File(newestBackupFile);

            closeFile();

            file = new File(fileName);
            LogLog.debug("Renaming file " + file + " to " + target);
            file.renameTo(target);
        }

        try {
            this.setFile(fileName, false, bufferedIO, bufferSize);
        } catch (IOException e) {
            LogLog.error("setFile(" + fileName + ", false) call failed.", e);
        }
    }

    protected String buildBackupFileName(String baseFileName, int index) {
        final String logExt = ".log";
        if (baseFileName.endsWith(logExt) && baseFileName.length() > logExt.length()) {
            final int extIndex = baseFileName.lastIndexOf(logExt);
            return baseFileName.substring(0, extIndex) + "-backup" + index + logExt;
        } else {
            return baseFileName + "." + index;
        }
    }
}
