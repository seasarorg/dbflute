/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.infra.reps;

import java.io.File;
import java.util.List;

import org.seasar.dbflute.infra.core.logic.DfSchemaResourceFinder;

/**
 * @author jflute
 * @since 1.0.4G (2013/07/13 Saturday)
 */
public class DfRepsReplaceSchemaSqlCollector {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String SQL_TITLE = "replace-schema";
    public static final String FILE_EXT = ".sql";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _sqlRootDir;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfRepsReplaceSchemaSqlCollector(String sqlRootDir) {
        _sqlRootDir = sqlRootDir;
    }

    // ===================================================================================
    //                                                                        Collect File
    //                                                                        ============
    public List<File> collectReplaceSchemaSqlFileList() {
        return findSchemaResourceFileList(getReplaceSchemaSqlTitle(), getReplaceSchemaFileExt());
    }

    protected String getReplaceSchemaSqlTitle() {
        return SQL_TITLE;
    }

    protected String getReplaceSchemaFileExt() {
        return FILE_EXT;
    }

    protected List<File> findSchemaResourceFileList(String prefix, String suffix) {
        final DfSchemaResourceFinder finder = createSchemaResourceFinder();
        finder.addPrefix(prefix);
        finder.addSuffix(suffix);
        return finder.findResourceFileList(_sqlRootDir);
    }

    protected DfSchemaResourceFinder createSchemaResourceFinder() {
        return new DfSchemaResourceFinder();
    }
}
