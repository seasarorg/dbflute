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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.token.file.exception.FileMakingInvalidValueCountException;
import org.seasar.dbflute.helper.token.line.LineMakingOption;
import org.seasar.dbflute.helper.token.line.LineToken;
import org.seasar.dbflute.helper.token.line.LineTokenizingOption;
import org.seasar.dbflute.util.Srl;

/**
 * The handler of token file. <br />
 * You can read/write the token file.
 * <pre>
 * e.g. Reading
 *  File tsvFile = ... <span style="color: #3F7E5E">// input file</span>
 *  FileToken fileToken = new FileToken();
 *  fileToken.tokenize(new FileInputStream(tsvFile), new FileTokenizingCallback() {
 *      public void handleRowResource(FileTokenizingRowResource resource) {
 *          ... = resource.getHeaderInfo();
 *          ... = resource.<span style="color: #AD4747">getValueList()</span>;
 *      }
 *  }, new FileTokenizingOption().delimitateByTab().encodeAsUTF8());
 * 
 * e.g. Writing (using Iterator)
 *  File tsvFile = ... <span style="color: #3F7E5E">// output file</span>
 *  List&lt;String&gt; columnNameList = ... <span style="color: #3F7E5E">// columns for header</span>
 *  final Iterator&lt;List&lt;String&gt;&gt; iterator = ...
 *  final FileMakingRowResource resource = new FileMakingRowResource();
 *  FileToken fileToken = new FileToken();
 *  fileToken.make(new FileOutputStream(tsvFile), new FileMakingCallback() {
 *      public FileMakingRowResource getRowResource() { <span style="color: #3F7E5E">// null or empty resource means end of data</span>
 *          return resource.<span style="color: #AD4747">acceptValueListIterator</span>(iterator); <span style="color: #3F7E5E">// row data only here</span>
 *      }
 *  }, new FileMakingOption().delimitateByTab().encodeAsUTF8().headerInfo(columnNameList));
 * 
 * e.g. Writing (using Writer)
 *  File tsvFile = ... <span style="color: #3F7E5E">// output file</span>
 *  List&lt;String&gt; columnNameList = ... <span style="color: #3F7E5E">// columns for header</span>
 *  final FileMakingRowResource resource = new FileMakingRowResource();
 *  FileToken fileToken = new FileToken();
 *  fileToken.makeByWriter(tsvFile, new FileMakingWriterCallback() {
 *      public void make(FileMakingRowWriter writer) {
 *          for (Member member : ...) { <span style="color: #3F7E5E">// output data loop</span>
 *              resource... <span style="color: #3F7E5E">// convert the member to the row resource</span>
 *              writer.<span style="color: #AD4747">write</span>(resource); <span style="color: #3F7E5E">// Yes, you write!</span>
 *          }
 *      }
 *  }, new FileMakingOption().delimitateByTab().encodeAsUTF8().headerInfo(columnNameList));
 * </pre>
 * @author jflute
 */
public class FileToken {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** The mark that means header done for writing process. */
    protected static final String HEADER_DONE_MARK = "headerDone";

    /** The mark that means first line done for writing process. */
    protected static final String FIRST_LINE_DONE_MARK = "firstLineDone";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The handler of line token for help. */
    protected final LineToken _lineToken = new LineToken();

    // ===================================================================================
    //                                                                      Tokenize(Read)
    //                                                                      ==============
    /**
     * Tokenize (read) the token data from the specified file. (file-tokenizing) <br />
     * CR + LF is treated as LF.
     * <pre>
     * File tsvFile = ... <span style="color: #3F7E5E">// input file</span>
     * FileToken fileToken = new FileToken();
     * fileToken.tokenize(new FileInputStream(tsvFile), new FileTokenizingCallback() {
     *     public void handleRowResource(FileTokenizingRowResource resource) {
     *         ... = resource.getHeaderInfo();
     *         ... = resource.<span style="color: #AD4747">getValueList()</span>;
     *     }
     * }, new FileTokenizingOption().delimitateByTab().encodeAsUTF8().handleEmptyAsNull());
     * </pre>
     * @param filePath The path of file name to read. (NotNull)
     * @param callback The callback for file-tokenizing. (NotNull)
     * @param option The option for file-tokenizing. (NotNull, Required{delimiter, encoding})
     * @throws FileNotFoundException When the file was not found.
     * @throws IOException When the file reading failed.
     */
    public void tokenize(String filePath, FileTokenizingCallback callback, FileTokenizingOption option)
            throws FileNotFoundException, IOException {
        assertStringNotNullAndNotTrimmedEmpty("filePath", filePath);

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            tokenize(fis, callback, option);
        } finally {
            if (fis != null) {
                try {
                    fis.close(); // basically no needed but just in case
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Tokenize (read) token data to specified file. (named file-tokenizing) <br />
     * CR + LF is treated as LF. <br />
     * This method uses {@link InputStreamReader} and {@link BufferedReader} that wrap the stream. <br />
     * And these objects are closed. (close() called finally)
     * <pre>
     * File tsvFile = ... <span style="color: #3F7E5E">// input file</span>
     * FileToken fileToken = new FileToken();
     * fileToken.tokenize(new FileInputStream(tsvFile), new FileTokenizingCallback() {
     *     public void handleRowResource(FileTokenizingRowResource resource) {
     *         ... = resource.getHeaderInfo();
     *         ... = resource.<span style="color: #AD4747">getValueList()</span>;
     *     }
     * }, new FileTokenizingOption().delimitateByTab().encodeAsUTF8().handleEmptyAsNull());
     * </pre>
     * @param ins The input stream for writing. This stream is closed after writing automatically. (NotNull)
     * @param callback The callback for file-tokenizing. (NotNull)
     * @param option The option for file-tokenizing. (NotNull, Required{delimiter, encoding})
     * @throws FileNotFoundException When the file was not found.
     * @throws IOException When the file reading failed.
     */
    public void tokenize(InputStream ins, FileTokenizingCallback callback, FileTokenizingOption option)
            throws FileNotFoundException, IOException {
        assertObjectNotNull("ins", ins);
        assertObjectNotNull("callback", callback);
        assertObjectNotNull("option", option);
        final String delimiter = option.getDelimiter();
        final String encoding = option.getEncoding();
        assertObjectNotNull("delimiter", delimiter);
        assertStringNotNullAndNotTrimmedEmpty("encoding", encoding);

        String lineString = null;
        String preContinueString = "";
        final List<String> temporaryValueList = new ArrayList<String>();
        final List<String> filteredValueList = new ArrayList<String>();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(ins, encoding));

            final StringBuilder realRowStringSb = new StringBuilder();
            FileTokenizingHeaderInfo headerInfo = null;
            int count = -1;
            int rowNumber = 1;
            int lineNumber = 0;
            while (true) {
                ++count;
                if ("".equals(preContinueString)) {
                    lineNumber = count + 1;
                }

                lineString = br.readLine();
                if (lineString == null) {
                    break;
                }
                if (count == 0) {
                    if (option.isBeginFirstLine()) {
                        headerInfo = new FileTokenizingHeaderInfo(); // as empty
                    } else {
                        headerInfo = analyzeHeaderInfo(delimiter, lineString);
                        continue;
                    }
                }
                final String rowString;
                if (preContinueString.equals("")) {
                    rowString = lineString;
                    realRowStringSb.append(lineString);
                } else {
                    final String lineSeparator = "\n";
                    rowString = preContinueString + lineSeparator + lineString;
                    realRowStringSb.append(lineSeparator).append(lineString);
                }
                final ValueLineInfo valueLineInfo = arrangeValueList(rowString, delimiter);
                final List<String> ls = valueLineInfo.getValueList();
                if (valueLineInfo.isContinueNextLine()) {
                    preContinueString = (String) ls.remove(ls.size() - 1);
                    temporaryValueList.addAll(ls);
                    continue;
                }
                temporaryValueList.addAll(ls);

                try {
                    final FileTokenizingRowResource resource = new FileTokenizingRowResource();
                    resource.setHeaderInfo(headerInfo);

                    if (option.isHandleEmptyAsNull()) {
                        for (final Iterator<String> ite = temporaryValueList.iterator(); ite.hasNext();) {
                            final String value = (String) ite.next();
                            if ("".equals(value)) {
                                filteredValueList.add(null);
                            } else {
                                filteredValueList.add(value);
                            }
                        }
                        resource.setValueList(filteredValueList);
                    } else {
                        resource.setValueList(temporaryValueList);
                    }

                    final String realRowString = realRowStringSb.toString();
                    realRowStringSb.setLength(0);
                    resource.setRowString(realRowString);
                    resource.setRowNumber(rowNumber);
                    resource.setLineNumber(lineNumber);
                    callback.handleRowResource(resource);
                } finally {
                    ++rowNumber;
                    temporaryValueList.clear();
                    filteredValueList.clear();
                    preContinueString = "";
                }
            }
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    protected ValueLineInfo arrangeValueList(final String lineString, String delimiter) {
        final List<String> valueList = new ArrayList<String>();

        // Don't use split!
        //final String[] values = lineString.split(delimiter);
        final LineTokenizingOption tokenizingOption = new LineTokenizingOption();
        tokenizingOption.setDelimiter(delimiter);
        final List<String> list = _lineToken.tokenize(lineString, tokenizingOption);
        final String[] values = (String[]) list.toArray(new String[list.size()]);
        for (int i = 0; i < values.length; i++) {
            valueList.add(values[i]);
        }
        return arrangeValueList(valueList, delimiter);
    }

    protected ValueLineInfo arrangeValueList(List<String> valueList, String delimiter) {
        final ValueLineInfo valueLineInfo = new ValueLineInfo();
        final ArrayList<String> resultList = new ArrayList<String>();
        String preString = "";
        for (int i = 0; i < valueList.size(); i++) {
            final String value = (String) valueList.get(i);
            if (value == null) {
                continue;
            }
            if (i == valueList.size() - 1) { // The last loop
                if (preString.equals("")) {
                    if (isFrontQOnly(value)) {
                        valueLineInfo.setContinueNextLine(true);
                        resultList.add(value);
                    } else if (isRearQOnly(value)) {
                        resultList.add(value);
                    } else if (isNotBothQ(value)) {
                        resultList.add(value);
                    } else {
                        resultList.add(removeDoubleQuotation(value));
                    }
                } else {
                    if (endsQuote(value, false)) {
                        resultList.add(removeDoubleQuotation(connectPreString(preString, delimiter, value)));
                    } else {
                        valueLineInfo.setContinueNextLine(true);
                        resultList.add(connectPreString(preString, delimiter, value));
                    }
                }
                break; // because it's the last loop
            }

            if (preString.equals("")) {
                if (isFrontQOnly(value)) {
                    preString = value;
                    continue;
                } else if (isRearQOnly(value)) {
                    preString = value;
                    continue;
                } else if (isNotBothQ(value)) {
                    resultList.add(value);
                } else {
                    resultList.add(removeDoubleQuotation(value));
                }
            } else {
                if (endsQuote(value, false)) {
                    resultList.add(removeDoubleQuotation(connectPreString(preString, delimiter, value)));
                } else {
                    preString = connectPreString(preString, delimiter, value);
                    continue;
                }
            }
            preString = "";
        }
        valueLineInfo.setValueList(resultList);
        return valueLineInfo;
    }

    protected String connectPreString(String preString, String delimiter, String value) {
        if (preString.equals("")) {
            return value;
        } else {
            return preString + delimiter + value;
        }
    }

    protected boolean isNotBothQ(final String value) {
        return !isQQ(value) && !value.startsWith("\"") && !endsQuote(value, false);
    }

    protected boolean isRearQOnly(final String value) {
        return !isQQ(value) && !value.startsWith("\"") && endsQuote(value, false);
    }

    protected boolean isFrontQOnly(final String value) {
        return !isQQ(value) && value.startsWith("\"") && !endsQuote(value, true);
    }

    protected boolean isQQ(final String value) {
        return value.equals("\"\"");
    }

    protected boolean endsQuote(String value, boolean startsQuote) {
        value = startsQuote ? value.substring(1) : value;
        final int length = value.length();
        int count = 0;
        for (int i = 0; i < length; i++) {
            char ch = value.charAt(length - (i + 1));
            if (ch == '\"') {
                ++count;
            } else {
                break;
            }
        }
        return count > 0 && isOddNumber(count);
    }

    protected boolean isOddNumber(int number) {
        return (number % 2) != 0;
    }

    protected String removeDoubleQuotation(String value) {
        if (!value.startsWith("\"") && !value.endsWith("\"")) {
            return value;
        }
        if (value.startsWith("\"")) {
            value = value.substring(1);
        }
        if (value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
        }
        value = Srl.replace(value, "\"\"", "\"");
        return value;
    }

    protected String removeRightDoubleQuotation(String value) {
        if (value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    protected FileTokenizingHeaderInfo analyzeHeaderInfo(String delimiter, final String lineString) {
        final FileTokenizingHeaderInfo headerInfo = new FileTokenizingHeaderInfo();
        final String[] values = lineString.split(delimiter);
        for (int i = 0; i < values.length; i++) {
            final String value = values[i].trim();// Trimming is Header Only!;
            final String columnName;
            if (value.startsWith("\"") && value.endsWith("\"")) {
                columnName = value.substring(1, value.length() - 1);
            } else {
                columnName = value;
            }
            headerInfo.addColumnName(columnName);
        }
        headerInfo.setColumnNameRowString(lineString);
        return headerInfo;
    }

    public static class ValueLineInfo {
        protected List<String> _valueList;
        protected boolean _continueNextLine;

        public List<String> getValueList() {
            return _valueList;
        }

        public void setValueList(List<String> valueList) {
            this._valueList = valueList;
        }

        public boolean isContinueNextLine() {
            return _continueNextLine;
        }

        public void setContinueNextLine(boolean continueNextLine) {
            this._continueNextLine = continueNextLine;
        }
    }

    // ===================================================================================
    //                                                                         Make(Write)
    //                                                                         ===========
    /**
     * Make (write) token file from specified row resources.
     * <pre>
     * String tsvFile = ... <span style="color: #3F7E5E">// output file</span>
     * List&lt;String&gt; columnNameList = ... <span style="color: #3F7E5E">// columns for header</span>
     * final Iterator&lt;List&lt;String&gt;&gt; iterator = ...
     * final FileMakingRowResource resource = new FileMakingRowResource();
     * FileToken fileToken = new FileToken();
     * fileToken.make(tsvFile, new FileMakingCallback() {
     *     public FileMakingRowResource getRowResource() { <span style="color: #3F7E5E">// null or empty resource means end of data</span>
     *         return resource.<span style="color: #AD4747">acceptValueListIterator</span>(iterator); <span style="color: #3F7E5E">// row data only here</span>
     *     }
     * }, new FileMakingOption().delimitateByTab().encodeAsUTF8().headerInfo(columnNameList));
     * </pre>
     * @param filePath The path of token file to write. (NotNull)
     * @param callback The callback for file-making. (NotNull)
     * @param option The option for file-making. (NotNull, Required{delimiter, encoding})
     * @throws FileNotFoundException When the file was not found.
     * @throws IOException When the file writing failed.
     */
    public void make(String filePath, FileMakingCallback callback, FileMakingOption option)
            throws FileNotFoundException, IOException {
        assertStringNotNullAndNotTrimmedEmpty("filePath", filePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            doMake(fos, callback, option);
        } finally {
            if (fos != null) {
                try {
                    fos.close(); // basically no needed but just in case
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Make (write) token-file from specified row resources. <br />
     * This method uses {@link OutputStreamWriter} and {@link BufferedWriter} that wrap the stream. <br />
     * And these objects are closed. (close() called finally)
     * <pre>
     * File tsvFile = ... <span style="color: #3F7E5E">// output file</span>
     * List&lt;String&gt; columnNameList = ... <span style="color: #3F7E5E">// columns for header</span>
     * final Iterator&lt;List&lt;String&gt;&gt; iterator = ...
     * final FileMakingRowResource resource = new FileMakingRowResource();
     * FileToken fileToken = new FileToken();
     * fileToken.make(new FileOutputStream(tsvFile), new FileMakingCallback() {
     *     public FileMakingRowResource getRowResource() { <span style="color: #3F7E5E">// null or empty resource means end of data</span>
     *         return resource.<span style="color: #AD4747">acceptValueListIterator</span>(iterator); <span style="color: #3F7E5E">// row data only here</span>
     *     }
     * }, new FileMakingOption().delimitateByTab().encodeAsUTF8().headerInfo(columnNameList));
     * </pre>
     * @param ous The output stream for writing. This stream is closed after writing automatically. (NotNull)
     * @param callback The callback for file-making. (NotNull)
     * @param option The option for file-making. (NotNull, Required{delimiter, encoding})
     * @throws FileNotFoundException When the file was not found.
     * @throws IOException When the file writing failed.
     */
    public void make(OutputStream ous, final FileMakingCallback callback, FileMakingOption option)
            throws FileNotFoundException, IOException {
        doMake(ous, callback, option);
    }

    protected void doMake(OutputStream ous, final FileMakingCallback callback, FileMakingOption option)
            throws FileNotFoundException, IOException {
        doMakeByWriter(ous, new FileMakingWriterCallback() {
            public void make(FileMakingRowWriter writer) throws IOException {
                FileMakingRowResource resource = null;
                while (true) {
                    resource = callback.getRowResource();
                    if (resource == null || !resource.hasRowData()) {
                        break; // the end
                    }
                    writer.write(resource);
                }
            }
        }, option);
    }

    /**
     * Make (write) token file by row writer that accepts row resources.
     * <pre>
     * String tsvFile = ... <span style="color: #3F7E5E">// output file</span>
     * List&lt;String&gt; columnNameList = ... <span style="color: #3F7E5E">// columns for header</span>
     * final FileMakingRowResource resource = new FileMakingRowResource();
     * FileToken fileToken = new FileToken();
     * fileToken.makeByWriter(tsvFile, new FileMakingWriterCallback() {
     *     public void make(FileMakingRowWriter writer) {
     *         for (Member member : ...) { <span style="color: #3F7E5E">// output data loop</span>
     *             resource... <span style="color: #3F7E5E">// convert the member to the row resource</span>
     *             writer.<span style="color: #AD4747">write</span>(resource); <span style="color: #3F7E5E">// Yes, you write!</span>
     *         }
     *     }
     * }, new FileMakingOption().delimitateByTab().encodeAsUTF8().headerInfo(columnNameList));
     * </pre>
     * @param filePath The path of token file to write. (NotNull)
     * @param callback The callback for file-making with writer. (NotNull)
     * @param option The option for file-making. (NotNull, Required{delimiter, encoding})
     * @throws FileNotFoundException When the file was not found.
     * @throws IOException When the file writing failed.
     */
    public void makeByWriter(String filePath, FileMakingWriterCallback callback, FileMakingOption option)
            throws FileNotFoundException, IOException {
        assertStringNotNullAndNotTrimmedEmpty("filePath", filePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            doMakeByWriter(fos, callback, option);
        } finally {
            if (fos != null) {
                try {
                    fos.close(); // basically no needed but just in case
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Make (write) token file by row writer that accepts row resources. <br />
     * This method uses {@link OutputStreamWriter} and {@link BufferedWriter} that wrap the stream. <br />
     * And these objects are closed. (close() called finally)
     * <pre>
     * File tsvFile = ... <span style="color: #3F7E5E">// output file</span>
     * List&lt;String&gt; columnNameList = ... <span style="color: #3F7E5E">// columns for header</span>
     * final FileMakingRowResource resource = new FileMakingRowResource();
     * FileToken fileToken = new FileToken();
     * fileToken.makeByWriter(new FileOutputStream(tsvFile), new FileMakingWriterCallback() {
     *     public void make(FileMakingRowWriter writer) {
     *         for (Member member : ...) { <span style="color: #3F7E5E">// output data loop</span>
     *             resource... <span style="color: #3F7E5E">// convert the member to the row resource</span>
     *             writer.<span style="color: #AD4747">write</span>(resource); <span style="color: #3F7E5E">// Yes, you write!</span>
     *         }
     *     }
     * }, new FileMakingOption().delimitateByTab().encodeAsUTF8().headerInfo(columnNameList));
     * </pre>
     * @param ous The output stream for writing. This stream is closed after writing automatically. (NotNull)
     * @param callback The callback for file-making with writer. (NotNull)
     * @param option The option for file-making. (NotNull, Required{delimiter, encoding})
     * @throws FileNotFoundException When the file was not found.
     * @throws IOException When the file writing failed.
     */
    public void makeByWriter(OutputStream ous, FileMakingWriterCallback callback, FileMakingOption option)
            throws FileNotFoundException, IOException {
        doMakeByWriter(ous, callback, option);
    }

    protected void doMakeByWriter(OutputStream ous, FileMakingWriterCallback callback, final FileMakingOption option)
            throws FileNotFoundException, IOException {
        assertObjectNotNull("ous", ous);
        assertObjectNotNull("callback", callback);
        assertObjectNotNull("option", option);
        final String encoding = option.getEncoding();
        assertStringNotNullAndNotTrimmedEmpty("encoding", encoding);
        assertObjectNotNull("delimiter", option.getDelimiter());
        final String lineSep = prepareWritingLineSeparator(option);
        final LineMakingOption lineOption = prepareWritingLineOption(option);

        Writer writer = null; // is interface not to use newLine() for fixed line separator
        try {
            writer = new BufferedWriter(new OutputStreamWriter(ous, encoding));
            final Set<String> headerDoneMarkSet = new HashSet<String>(1);
            final Set<String> firstLineDoneMarkSet = new HashSet<String>(1);

            // write header
            final FileMakingHeaderInfo headerInfo = option.getFileMakingHeaderInfo();
            if (headerInfo != null) {
                final List<String> columnNameList = headerInfo.getColumnNameList();
                doWriterHeader(writer, columnNameList, option, headerDoneMarkSet, firstLineDoneMarkSet);
            }

            // write data row
            callbackDataRowWriter(callback, option, lineSep, lineOption, writer, firstLineDoneMarkSet);
            writer.flush();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    protected void doWriterHeader(Writer writer, List<String> columnNameList, FileMakingOption option,
            Set<String> headerDoneMarkSet, Set<String> firstLineDoneMarkSet) throws IOException {
        if (columnNameList != null && !columnNameList.isEmpty()) {
            final LineMakingOption lineMakingOption = new LineMakingOption();
            lineMakingOption.setDelimiter(option.getDelimiter());
            lineMakingOption.trimSpace(); // trimming is header only
            reflectQuoteMinimally(option, lineMakingOption);
            final String columnHeaderString = _lineToken.make(columnNameList, lineMakingOption);
            writer.write(columnHeaderString);
            headerDoneMarkSet.add(HEADER_DONE_MARK);
            firstLineDoneMarkSet.add(FIRST_LINE_DONE_MARK);
        }
    }

    protected void callbackDataRowWriter(FileMakingWriterCallback callback, final FileMakingOption option,
            final String lineSep, final LineMakingOption lineOption, final Writer writer,
            final Set<String> firstLineDoneMarkSet) throws IOException {
        callback.make(new FileMakingRowWriter() {
            public void write(FileMakingRowResource resource) throws IOException {
                assertRowResourceOfWriter(resource);
                doWriteDataRow(writer, resource, option, lineOption, lineSep, firstLineDoneMarkSet);
            }

            protected void assertRowResourceOfWriter(FileMakingRowResource resource) {
                if (resource == null || !resource.hasRowData()) {
                    String msg = "The argument 'resource' of row writer should not be null.";
                    throw new IllegalArgumentException(msg);
                }
            }
        });
    }

    protected void doWriteDataRow(Writer writer, FileMakingRowResource resource, FileMakingOption option,
            LineMakingOption lineOption, String lineSep, Set<String> firstLineDoneMarkSet) throws IOException {
        if (!resource.hasRowData()) {
            return;
        }
        final Collection<String> valueList = resource.getValueList(); // not null here
        checkValueCount(option, valueList);
        final String lineString = _lineToken.make(valueList, lineOption);
        final String actualLine;
        if (!firstLineDoneMarkSet.isEmpty()) { // second or more line
            actualLine = lineSep + lineString;
        } else {
            actualLine = lineString;
        }
        writer.write(actualLine);
        firstLineDoneMarkSet.add(FIRST_LINE_DONE_MARK);
        resource.clear();
    }

    protected String prepareWritingLineSeparator(final FileMakingOption option) {
        final String lineSep;
        if (option.getLineSeparator() != null && !option.getLineSeparator().equals("")) {
            lineSep = option.getLineSeparator();
        } else {
            lineSep = "\n"; // default
        }
        return lineSep;
    }

    protected LineMakingOption prepareWritingLineOption(FileMakingOption option) {
        // create line option here to recycle instance only for data row
        // (header has original line option so not use this)
        final LineMakingOption lineOption = new LineMakingOption();
        lineOption.setDelimiter(option.getDelimiter());
        reflectQuoteMinimally(option, lineOption);
        return lineOption;
    }

    protected void reflectQuoteMinimally(FileMakingOption fileMakingOption, LineMakingOption lineMakingOption) {
        if (fileMakingOption.isQuoteMinimally()) {
            lineMakingOption.quoteMinimally();
        } else {
            lineMakingOption.quoteAll(); // default
        }
    }

    protected void checkValueCount(FileMakingOption option, final Collection<String> valueList) {
        if (option.isSuppressValueCountCheck()) {
            return;
        }
        final FileMakingHeaderInfo headerInfo = option.getFileMakingHeaderInfo();
        if (headerInfo == null) {
            return;
        }
        final List<String> columnNameList = headerInfo.getColumnNameList();
        if (columnNameList == null || columnNameList.isEmpty()) {
            return;
        }
        final int columnSize = columnNameList.size();
        final int valueSize = valueList.size();
        if (columnSize != valueSize) {
            throwFileMakingInvalidValueCountException(columnNameList, valueList);
        }
    }

    protected void throwFileMakingInvalidValueCountException(List<String> columnNameList, Collection<String> valueList) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The value count or thr row does not match column count of header.");
        br.addItem("Column List");
        br.addElement(columnNameList);
        br.addElement("column count: " + columnNameList.size());
        br.addItem("Value List");
        br.addElement(valueList);
        br.addElement("value count: " + valueList.size());
        final String msg = br.buildExceptionMessage();
        throw new FileMakingInvalidValueCountException(msg);
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    // -----------------------------------------------------
    //                                         Assert Object
    //                                         -------------
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    // -----------------------------------------------------
    //                                         Assert String
    //                                         -------------
    protected void assertStringNotNullAndNotTrimmedEmpty(String variableName, String value) {
        assertObjectNotNull("variableName", variableName);
        assertObjectNotNull(variableName, value);
        if (value.trim().length() == 0) {
            String msg = "The value should not be empty: variableName=" + variableName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
    }
}