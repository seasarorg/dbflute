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
package org.seasar.dbflute.helper.token.file;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * The simple facade for file-making. <br />
 * For example, the facade has on-memory making methods. 
 * @author jflute
 */
public class FileMakingSimpleFacade {

    /** The handler of file token for help. */
    protected final FileToken fileToken = new FileToken();

    /**
     * Make token file from row list.
     * @param filePath The path of token file to write. (NotNull)
     * @param rowList The list of row (value-list). (NotNull)
     * @param option The option for file-making. (NotNull, Required{delimiter, encoding})
     * @throws java.io.FileNotFoundException When the file was not found.
     * @throws java.io.IOException When the file reading failed.
     */
    public void makeFromRowList(final String filePath, final List<List<String>> rowList, final FileMakingOption option)
            throws FileNotFoundException, IOException {
        final FileMakingRowResource resource = new FileMakingRowResource();
        final FileMakingCallback fileMakingCallback = new FileMakingCallback() {
            protected int rowCount = 0; // old style here (you can use iterator)

            public FileMakingRowResource getRowResource() {
                ++rowCount;
                if (rowList.size() < rowCount) {
                    return null; // the end
                }
                final List<String> valueList = (List<String>) rowList.get(rowCount - 1);
                resource.acceptValueList(valueList);
                return resource;
            }
        };
        final FileToken fileToken = new FileToken();
        fileToken.make(filePath, fileMakingCallback, option);
    }

    /**
     * Make bytes from row list.
     * @param rowList The list of row (value-list). (NotNull)
     * @param option The option for file-making. (NotNull, Required{delimiter, encoding})
     * @return The array of byte of the row list. (NotNull)
     * @throws java.io.FileNotFoundException When the file was not found. (no way here)
     * @throws java.io.IOException When the file reading failed.
     */
    public byte[] makeFromRowList(final List<List<String>> rowList, final FileMakingOption option)
            throws FileNotFoundException, IOException {
        final FileMakingRowResource resource = new FileMakingRowResource();
        final FileMakingCallback fileMakingCallback = new FileMakingCallback() {
            protected int rowCount = 0; // old style here (you can use iterator)

            public FileMakingRowResource getRowResource() {
                ++rowCount;
                if (rowList.size() < rowCount) {
                    return null;// The End!
                }
                final List<String> valueList = (List<String>) rowList.get(rowCount - 1);
                resource.acceptValueList(valueList);
                return resource;
            }
        };
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final FileToken fileToken = new FileToken();
        fileToken.make(baos, fileMakingCallback, option);
        return baos.toByteArray();
    }
}
