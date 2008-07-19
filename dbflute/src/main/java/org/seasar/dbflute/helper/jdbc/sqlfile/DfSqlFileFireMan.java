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

    /**
     * Load the sql file and then execute it.
     */
    public void execute(DfSqlFileRunner runner, List<File> fileList) {
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
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}
