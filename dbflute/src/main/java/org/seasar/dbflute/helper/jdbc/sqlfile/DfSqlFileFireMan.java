/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.helper.jdbc.sqlfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jflute
 */
public class DfSqlFileFireMan {

    /** Log instance. */
    private static Log _log = LogFactory.getLog(DfSqlFileFireMan.class);

    private String _executorName;

    /**
     * Load the SQL files and then execute them.
     * @return The result about firing SQL. (NotNull)
     */
    public FireResult execute(DfSqlFileRunner runner, List<File> fileList) {
        final FireResult fireResult = new FireResult();
        try {
            int goodSqlCount = 0;
            int totalSqlCount = 0;
            for (final File file : fileList) {
                if (!file.exists()) {
                    throw new FileNotFoundException("The file '" + file.getPath() + "' does not exist.");
                }

                if (_log.isInfoEnabled()) {
                    _log.info("{SQL File}: " + file);
                }

                runner.setSrc(file);
                runner.runTransaction();

                goodSqlCount = goodSqlCount + runner.getGoodSqlCount();
                totalSqlCount = totalSqlCount + runner.getTotalSqlCount();
            }
            String title = _executorName != null ? _executorName : "Fired SQL";
            String msg = "{" + title + "}: success=" + goodSqlCount + " failure=" + (totalSqlCount - goodSqlCount);
            msg = msg + " (in " + fileList.size() + " files)";
            _log.info(msg);
            fireResult.setResultMessage(msg);
            fireResult.setExistsError(totalSqlCount > goodSqlCount);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
        return fireResult;
    }

    public static class FireResult {
        protected String resultMessage;
        protected boolean existsError;

        public String getResultMessage() {
            return resultMessage;
        }

        public void setResultMessage(String resultMessage) {
            this.resultMessage = resultMessage;
        }

        public boolean isExistsError() {
            return existsError;
        }

        public void setExistsError(boolean existsError) {
            this.existsError = existsError;
        }
    }

    public String getExecutorName() {
        return _executorName;
    }

    public void setExecutorName(String executorName) {
        this._executorName = executorName;
    }
}
