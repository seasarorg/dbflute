/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.helper.datahandler;

import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

public interface DfSeparatedDataHandler {

    public void writeSeveralData(String basePath, String typeName, String delimter, DataSource dataSource,
            Map<String, Set<String>> notFoundColumnMap);

    /**
     * Write data from separated-file.
     * 
     * @param filename Name of the file. (NotNull and NotEmpty)
     * @param encoding Encoding of the file. (NotNull and NotEmpty)
     * @param delimiter Delimiter of the file. (NotNull and NotEmpty)
     * @param notFoundColumnMap Not found column map. (NotNUl)
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void writeData(String filename, String encoding, String delimiter, DataSource dataSource,
            Map<String, Set<String>> notFoundColumnMap) throws java.io.FileNotFoundException, java.io.IOException;
}
