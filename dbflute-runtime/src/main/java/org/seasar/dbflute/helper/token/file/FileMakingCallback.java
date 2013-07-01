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

/**
 * The callback of file-making.
 * <pre>
 * File tsvFile = ... <span style="color: #3F7E5E">// output file</span>
 * List&lt;String&gt; columnNameList = ... <span style="color: #3F7E5E">// columns for header</span>
 * final Iterator&lt;List&lt;String&gt;&gt; iterator = ...
 * final FileMakingRowResource resource = new FileMakingRowResource();
 * FileToken fileToken = new FileToken();
 * <span style="color: #3F7E5E">// or final Iterator&lt;LinkedHashMap&lt;String, String&gt;&gt; iterator = ...</span>
 * fileToken.makeFromIterator(new FileOutputStream(tsvFile), new FileMakingCallback() {
 *     public FileMakingRowResource getRowResource() { <span style="color: #3F7E5E">// null or empty resource means end of data</span>
 *         return resource.<span style="color: #AD4747">acceptValueListIterator</span>(iterator); <span style="color: #3F7E5E">// row data only here</span>
 *     }
 * }, new FileMakingOption().delimitateByTab().encodeAsUTF8().headerInfo(columnNameList));
 * </pre>
 * @author jflute
 */
public interface FileMakingCallback {

    /**
     * Get the row resource of file-making. <br />
     * You should return your row resource for file-making.
     * It continues invoking until this method returns null.
     * @return The row resource of file-making. (NullAllowed)
     */
    FileMakingRowResource getRowResource();
}
