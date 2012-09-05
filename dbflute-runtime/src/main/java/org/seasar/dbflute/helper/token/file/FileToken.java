/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.helper.token.line.LineMakingOption;
import org.seasar.dbflute.helper.token.line.LineToken;
import org.seasar.dbflute.helper.token.line.LineTokenizingOption;
import org.seasar.dbflute.util.Srl;

/**
 * The handler of token file. <br />
 * You can read/write the token file.
 * <pre>
 * e.g. Reading
 *  File tsvFile = ...
 *  FileToken fileToken = new FileToken();
 *  fileToken.tokenize(new FileInputStream(tsvFile), new FileTokenizingCallback() {
 *      public void handleRowResource(FileTokenizingRowResource rowResource) {
 *          ... = rowResource.getFileTokenizingHeaderInfo();
 *          ... = rowResource.getValueList();
 *      }
 *  }, new FileTokenizingOption().delimitateByTab().encodeAsUTF8());
 * 
 * e.g. Writing
 *  File tsvFile = ...
 *  List&lt;String&gt; columnNameList = ...
 *  FileToken fileToken = new FileToken();
 *  final Iterator&lt;List&lt;String&gt;&gt; iterator = ...
 *  // or final Iterator&lt;LinkedHashMap&lt;String, String&gt;&gt; iterator = ...
 *  fileToken.make(new FileOutputStream(tsvFile), new FileMakingCallback() {
 *      public FileMakingRowResource getRowResource() { // null or empty resource means end of data
 *          return new FileMakingRowResource().acceptValueListIterator(iterator); // data only here
 *          // or return new FileMakingRowResource().acceptNameValueMapIterator(iterator); // with header
 *      }
 *  }, new FileMakingOption().delimitateByTab().encodeAsUTF8().headerInfo(columnNameList));
 * </pre>
 * @author jflute
 */
public class FileToken {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The handler of line token for help. */
    protected final LineToken _lineToken = new LineToken();

    // ===================================================================================
    //                                                                      Tokenize(Read)
    //                                                                      ==============
    /**
     * Read the token data from the specified file. (file-tokenizing) <br />
     * CR + LF is treated as LF.
     * <pre>
     * File tsvFile = ...
     * FileToken fileToken = new FileToken();
     * fileToken.tokenize(new FileInputStream(tsvFile), new FileTokenizingCallback() {
     *     public void handleRowResource(FileTokenizingRowResource rowResource) {
     *         ... = rowResource.getFileTokenizingHeaderInfo();
     *         ... = rowResource.getValueList();
     *     }
     * }, new FileTokenizingOption().delimitateByTab().encodeAsUTF8().handleEmptyAsNull());
     * </pre>
     * @param filePath The path of file name to read. (NotNull)
     * @param callback The callback for file-tokenizing. (NotNull)
     * @param option The option for file-tokenizing. (NotNull, Required{delimiter, encoding})
     * @throws java.io.FileNotFoundException When the file was not found.
     * @throws java.io.IOException When the file reading failed.
     */
    public void tokenize(String filePath, FileTokenizingCallback callback, FileTokenizingOption option)
            throws FileNotFoundException, IOException {
        assertStringNotNullAndNotTrimmedEmpty("filePath", filePath);

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            tokenize(fis, callback, option);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (fis != null) {
                    fis.close(); // just in case
                }
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Write token data to specified file. (named file-tokenizing) <br />
     * CR + LF is treated as LF. <br />
     * This method uses {@link java.io.InputStreamReader} and {@link java.io.BufferedReader} that wrap the stream.
     * And these objects are closed. (close() called finally)
     * @param ins The input stream for writing. This stream is closed after writing automatically. (NotNull)
     * @param callback The callback for file-tokenizing. (NotNull)
     * @param option The option for file-tokenizing. (NotNull, Required{delimiter, encoding})
     * @throws java.io.FileNotFoundException When the file was not found.
     * @throws java.io.IOException When the file reading failed.
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
            FileTokenizingHeaderInfo fileTokenizingHeaderInfo = null;
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
                        fileTokenizingHeaderInfo = new FileTokenizingHeaderInfo();// As empty
                    } else {
                        fileTokenizingHeaderInfo = analyzeHeaderInfo(delimiter, lineString);
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
                    final FileTokenizingRowResource fileTokenizingRowResource = new FileTokenizingRowResource();
                    fileTokenizingRowResource.setFirstLineInfo(fileTokenizingHeaderInfo);

                    if (option.isHandleEmptyAsNull()) {
                        for (final Iterator<String> ite = temporaryValueList.iterator(); ite.hasNext();) {
                            final String value = (String) ite.next();
                            if ("".equals(value)) {
                                filteredValueList.add(null);
                            } else {
                                filteredValueList.add(value);
                            }
                        }
                        fileTokenizingRowResource.setValueList(filteredValueList);
                    } else {
                        fileTokenizingRowResource.setValueList(temporaryValueList);
                    }

                    final String realRowString = realRowStringSb.toString();
                    realRowStringSb.setLength(0);
                    fileTokenizingRowResource.setRowString(realRowString);
                    fileTokenizingRowResource.setRowNumber(rowNumber);
                    fileTokenizingRowResource.setLineNumber(lineNumber);
                    callback.handleRowResource(fileTokenizingRowResource);
                } finally {
                    ++rowNumber;
                    temporaryValueList.clear();
                    filteredValueList.clear();
                    preContinueString = "";
                }
            }
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
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
        final List<String> columnNameList = new ArrayList<String>();
        final String[] values = lineString.split(delimiter);
        for (int i = 0; i < values.length; i++) {
            final String value = values[i].trim();// Trimming is Header Only!;
            if (value.startsWith("\"") && value.endsWith("\"")) {
                columnNameList.add(value.substring(1, value.length() - 1));
            } else {
                columnNameList.add(value);
            }
        }
        final FileTokenizingHeaderInfo fileTokenizingHeaderInfo = new FileTokenizingHeaderInfo();
        fileTokenizingHeaderInfo.setColumnNameList(columnNameList);
        fileTokenizingHeaderInfo.setColumnNameRowString(lineString);
        return fileTokenizingHeaderInfo;
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
     * Make token file from specified row resources.
     * @param filePath The path of token file to write. (NotNull)
     * @param callback The callback for file-making. (NotNull)
     * @param option The option for file-making. (NotNull, Required{delimiter, encoding})
     * @throws java.io.FileNotFoundException When the file was not found.
     * @throws java.io.IOException When the file reading failed.
     */
    public void make(String filePath, FileMakingCallback callback, FileMakingOption option)
            throws FileNotFoundException, IOException {
        assertStringNotNullAndNotTrimmedEmpty("filename", filePath);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            make(fos, callback, option);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    /**
     * Make token-file from specified row resources. <br />
     * This method uses {@link java.io.OutputStreamWriter} and {@link java.io.BufferedWriter} that wrap the stream.
     * And these objects are closed. (close() called finally)
     * <pre>
     * File tsvFile = ...
     * List&lt;String&gt; columnNameList = ...
     * FileToken fileToken = new FileToken();
     * final Iterator&lt;List&lt;String&gt;&gt; iterator = ...
     * // or final Iterator&lt;LinkedHashMap&lt;String, String&gt;&gt; iterator = ...
     * fileToken.make(new FileOutputStream(tsvFile), new FileMakingCallback() {
     *     public FileMakingRowResource getRowResource() { // null or empty resource means end of data
     *         return new FileMakingRowResource().acceptValueListIterator(iterator); // data only here
     *         // or return new FileMakingRowResource().acceptNameValueMapIterator(iterator); // with header
     *     }
     * }, new FileMakingOption().delimitateByTab().encodeAsUTF8().headerInfo(columnNameList));
     * </pre>
     * @param ous The output stream for writing. This stream is closed after writing automatically. (NotNull)
     * @param callback The callback for file-making. (NotNull)
     * @param option The option for file-making. (NotNull, Required{delimiter, encoding})
     * @throws java.io.FileNotFoundException When the file was not found.
     * @throws java.io.IOException When the file reading failed.
     */
    public void make(OutputStream ous, FileMakingCallback callback, FileMakingOption option)
            throws FileNotFoundException, IOException {
        assertObjectNotNull("ous", ous);
        assertObjectNotNull("callback", callback);
        assertObjectNotNull("option", option);
        final String encoding = option.getEncoding();
        final String delimiter = option.getDelimiter();
        assertObjectNotNull("delimiter", delimiter);
        assertStringNotNullAndNotTrimmedEmpty("encoding", encoding);
        final String lineSeparator;
        if (option.getLineSeparator() != null && !option.getLineSeparator().equals("")) {
            lineSeparator = option.getLineSeparator();
        } else {
            lineSeparator = "\n"; // default
        }

        Writer writer = null; // is interface not to use newLine() for fixed line separator
        try {
            writer = new BufferedWriter(new OutputStreamWriter(ous, encoding));
            boolean headerDone = false;

            // make header
            final FileMakingHeaderInfo headerInfo = option.getFileMakingHeaderInfo();
            if (headerInfo != null) {
                final List<String> columnNameList = headerInfo.getColumnNameList();
                if (columnNameList != null && !columnNameList.isEmpty()) {
                    final LineMakingOption lineMakingOption = new LineMakingOption();
                    lineMakingOption.setDelimiter(delimiter);
                    lineMakingOption.trimSpace(); // trimming is header only
                    reflectQuoteMinimally(option, lineMakingOption);
                    final String columnHeaderString = _lineToken.make(columnNameList, lineMakingOption);
                    writer.write(columnHeaderString + lineSeparator);
                    headerDone = true;
                }
            }

            // make row
            FileMakingRowResource rowResource = null;
            while (true) {
                rowResource = callback.getRowResource();
                if (rowResource == null || !rowResource.hasResource()) {
                    break; // the end
                }
                final List<String> valueList;
                if (rowResource.getValueList() != null) {
                    valueList = rowResource.getValueList();
                } else {
                    final Map<String, String> nameValueMap = rowResource.getNameValueMap(); // not null here
                    if (!headerDone) {
                        final List<String> columnNameList = new ArrayList<String>(nameValueMap.keySet());
                        final LineMakingOption lineMakingOption = new LineMakingOption();
                        lineMakingOption.setDelimiter(delimiter);
                        lineMakingOption.trimSpace(); // trimming is header only
                        reflectQuoteMinimally(option, lineMakingOption);
                        final String columnHeaderString = _lineToken.make(columnNameList, lineMakingOption);
                        writer.write(columnHeaderString + lineSeparator);
                        headerDone = true;
                    }
                    valueList = new ArrayList<String>(nameValueMap.values());
                }
                final LineMakingOption lineMakingOption = new LineMakingOption();
                lineMakingOption.setDelimiter(delimiter);
                reflectQuoteMinimally(option, lineMakingOption);
                final String lineString = _lineToken.make(valueList, lineMakingOption);
                writer.write(lineString + lineSeparator);
            }
            writer.flush();
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    protected void reflectQuoteMinimally(FileMakingOption fileMakingOption, LineMakingOption lineMakingOption) {
        if (fileMakingOption.isQuoteMinimally()) {
            lineMakingOption.quoteMinimally();
        } else {
            lineMakingOption.quoteAll(); // default
        }
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