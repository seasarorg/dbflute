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

import java.util.Collection;
import java.util.Iterator;

/**
 * The row resource of file-making. <br />
 * You can set one record info to this resource as list of string or map of string with header info. <br />
 * Null resource or null data or empty data means the end of data.
 * <pre>
 * e.g. make() (using Iterator)
 *  final FileMakingRowResource resource = new FileMakingRowResource();
 *  final Iterator&lt;List&lt;String&gt;&gt; iterator = ...
 *  fileToken.make(new FileOutputStream(tsvFile), new FileMakingCallback() {
 *      public FileMakingRowResource getRowResource() { <span style="color: #3F7E5E">// null or empty resource means end of data</span>
 *          return resource.<span style="color: #AD4747">acceptValueListIterator</span>(iterator); <span style="color: #3F7E5E">// row data only here</span>
 *      }
 *  }, new FileMakingOption().delimitateByTab().encodeAsUTF8().headerInfo(columnNameList));
 * 
 * e.g. makeByWriter()
 *  final FileMakingRowResource resource = new FileMakingRowResource();
 *  fileToken.makeByWriter(new FileOutputStream(tsvFile), new FileMakingWriterCallback() {
 *      public void make(FileMakingRowWriter writer) {
 *          for (Member member : ...) { <span style="color: #3F7E5E">// output data loop</span>
 *              resource... <span style="color: #3F7E5E">// convert the member to the row resource</span>
 *              writer.<span style="color: #AD4747">write</span>(resource); <span style="color: #3F7E5E">// Yes, you write!</span>
 *          }
 *      }
 *  }, new FileMakingOption().delimitateByTab().encodeAsUTF8().headerInfo(columnNameList));
 * </pre>
 * </pre>
 * @author jflute
 */
public class FileMakingRowResource {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    // either required, both null means end of data
    protected Collection<String> _valueList;

    // =====================================================================================
    //                                                                           Constructor
    //                                                                           ===========
    public FileMakingRowResource() {
    }

    // =====================================================================================
    //                                                                      Accept ValueList
    //                                                                      ================
    /**
     * Accept the list of value as one record.
     * @param valueList The list of value. (NullAllowed, EmptyAllowed: if null or empty, means end of data)
     * @return this. (NotNull)
     */
    public FileMakingRowResource acceptValueList(Collection<String> valueList) {
        _valueList = valueList;
        return this;
    }

    /**
     * Accept the iterator for value list. {Priority One} <br />
     * If the iterator has the next element, set it to value list. (means next() called) <br />
     * No more element means end of data (no resource is treated as end of data by {@link FileToken}).
     * @param valueListIterator The iterator for value list. (NotNull)
     * @return this. (NotNull)
     */
    public FileMakingRowResource acceptValueListIterator(Iterator<? extends Collection<String>> valueListIterator) {
        _valueList = valueListIterator.hasNext() ? valueListIterator.next() : null;
        return this;
    }

    // =====================================================================================
    //                                                                       Resource Status
    //                                                                       ===============
    /**
     * Does it have row data?
     * @return The determination, true or false.
     */
    public boolean hasRowData() {
        return _valueList != null && !_valueList.isEmpty();
    }

    /**
     * Clear the resources for instance recycle. (called by writing process per one line)
     */
    public void clear() {
        _valueList = null;
    }

    // =====================================================================================
    //                                                                              Accessor
    //                                                                              ========
    public Collection<String> getValueList() {
        return _valueList;
    }
}
