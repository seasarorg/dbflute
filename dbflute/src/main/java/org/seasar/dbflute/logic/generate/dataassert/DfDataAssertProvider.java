package org.seasar.dbflute.logic.generate.dataassert;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.exception.DfTakeFinallyAssertionFailureCountNotExistsException;
import org.seasar.dbflute.exception.DfTakeFinallyAssertionFailureCountNotZeroException;
import org.seasar.dbflute.exception.DfTakeFinallyAssertionFailureListNotExistsException;
import org.seasar.dbflute.exception.DfTakeFinallyAssertionFailureListNotZeroException;
import org.seasar.dbflute.exception.DfAssertionInvalidMarkException;
import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 * @since 0.9.5.4 (2009/08/07 Friday)
 */
public class DfDataAssertProvider {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static Log _log = LogFactory.getLog(DfDataAssertProvider.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Map<String, DfDataAssertHandler> _assertHandlerMap = new LinkedHashMap<String, DfDataAssertHandler>();
    {
        _assertHandlerMap.put("assertCountZero", new DfDataAssertHandler() {
            public void handle(File sqlFile, Statement statement, String sql) throws SQLException {
                assertCountZero(sqlFile, statement, sql);
            }
        });
        _assertHandlerMap.put("assertCountExists", new DfDataAssertHandler() {
            public void handle(File sqlFile, Statement statement, String sql) throws SQLException {
                assertCountExists(sqlFile, statement, sql);
            }
        });
        _assertHandlerMap.put("assertListZero", new DfDataAssertHandler() {
            public void handle(File sqlFile, Statement statement, String sql) throws SQLException {
                assertListZero(sqlFile, statement, sql);
            }
        });
        _assertHandlerMap.put("assertListExists", new DfDataAssertHandler() {
            public void handle(File sqlFile, Statement statement, String sql) throws SQLException {
                assertListExists(sqlFile, statement, sql);
            }
        });
    }
    protected String _dataLoadingType;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfDataAssertProvider(String dataLoadingType) {
        _dataLoadingType = dataLoadingType;
    }

    // ===================================================================================
    //                                                                             Provide
    //                                                                             =======
    /**
     * @param sql SQL string. (NotNull)
     * @return The handle of data assert. (NullAllowed)
     */
    public DfDataAssertHandler provideDataAssertHandler(String sql) {
        if (!sql.contains("--")) {
            return null;
        }
        final String starter = "#df:";
        final String terminator = "#";
        final String typeAtMark = "@";

        // Resolve comment spaces.
        sql = DfStringUtil.replace(sql, "-- #", "--#");

        final Set<Entry<String, DfDataAssertHandler>> entrySet = _assertHandlerMap.entrySet();
        DfDataAssertHandler defaultHandler = null;
        for (Entry<String, DfDataAssertHandler> entry : entrySet) {
            final String key = entry.getKey();

            // Find plain mark.
            final String firstMark = "--" + starter + key + terminator;
            if (sql.contains(firstMark)) {
                return entry.getValue();
            }

            // Find with data loading type.
            final String secondMark = "--" + starter + key + typeAtMark + _dataLoadingType + terminator;
            if (sql.contains(secondMark)) {
                return entry.getValue();
            }

            // Set up default handler with check.
            final String thirdMark = "--" + starter + key;
            final int keyIndex = sql.indexOf(thirdMark);
            if (keyIndex < 0) {
                continue;
            }
            String rearString = sql.substring(keyIndex + thirdMark.length());
            if (rearString.contains(ln())) {
                rearString = rearString.substring(0, rearString.indexOf(ln()));
            }
            if (!rearString.contains(terminator)) {
                String msg = "The data assert mark should ends '" + terminator + "':" + ln() + sql;
                throw new DfAssertionInvalidMarkException(msg);
            }
            final String option = rearString.substring(0, rearString.indexOf(terminator));
            if (option.startsWith(typeAtMark)) {
                defaultHandler = new DfDataAssertHandler() {
                    public void handle(File sqlFile, Statement stmt, String sql) throws SQLException {
                        String msg = "...Skipping for the different dataLoadingType:";
                        msg = msg + " " + thirdMark + option + terminator;
                        _log.info(msg);
                    }
                };
            } else {
                String msg = "Unknown option '" + option + "':" + ln() + sql;
                throw new DfAssertionInvalidMarkException(msg);
            }
        }
        return defaultHandler; // when not found
    }

    // ===================================================================================
    //                                                                              Assert
    //                                                                              ======
    protected void assertCountZero(File sqlFile, Statement statement, String sql) throws SQLException {
        assertCount(sqlFile, statement, sql, false);
    }

    protected void assertCountExists(File sqlFile, Statement statement, String sql) throws SQLException {
        assertCount(sqlFile, statement, sql, true);
    }

    protected void assertCount(File sqlFile, Statement statement, String sql, boolean exists) throws SQLException {
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(sql);
            int count = 0;
            while (rs.next()) {// One loop only!
                count = rs.getInt(1);
                break;
            }
            if (exists) {
                if (count == 0) {
                    throwAssertionFailureCountNotExistsException(sqlFile, sql, count);
                } else {
                    String result = "[RESULT]: count=" + count;
                    _log.info(result);
                }
            } else {
                if (count > 0) {
                    throwAssertionFailureCountNotZeroException(sqlFile, sql, count);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    protected void assertListZero(File sqlFile, Statement statement, String sql) throws SQLException {
        assertList(sqlFile, statement, sql, false);
    }

    protected void assertListExists(File sqlFile, Statement statement, String sql) throws SQLException {
        assertList(sqlFile, statement, sql, true);
    }

    protected void assertList(File sqlFile, Statement statement, String sql, boolean exists) throws SQLException {
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(sql);
            final ResultSetMetaData metaData = rs.getMetaData();
            final int columnCount = metaData.getColumnCount();
            final List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
            int count = 0;
            while (rs.next()) { // One loop only!
                Map<String, String> recordMap = new LinkedHashMap<String, String>();
                for (int i = 1; i <= columnCount; i++) {
                    recordMap.put(metaData.getColumnName(i), rs.getString(i));
                }
                resultList.add(recordMap);
                ++count;
            }
            if (exists) {
                if (count == 0) {
                    throwAssertionFailureListNotExistsException(sqlFile, sql, count, resultList);
                } else {
                    String result = "[RESULT]: count=" + count + ln();
                    for (Map<String, String> recordMap : resultList) {
                        result = result + recordMap + ln();
                    }
                    _log.info(result.trim());
                }
            } else {
                if (count > 0) {
                    throwAssertionFailureListNotZeroException(sqlFile, sql, count, resultList);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    protected void throwAssertionFailureCountNotZeroException(File sqlFile, String sql, int resultCount) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The SQL expects ZERO but the result was NOT ZERO!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your test data!" + ln();
        msg = msg + ln();
        msg = msg + "[SQL File]" + ln() + sqlFile + ln();
        msg = msg + ln();
        msg = msg + "[Executed SQL]" + ln() + sql + ln();
        msg = msg + ln();
        msg = msg + "[Result Count]" + ln() + resultCount + ln();
        msg = msg + "* * * * * * * * * */";
        throw new DfTakeFinallyAssertionFailureCountNotZeroException(msg);
    }

    protected void throwAssertionFailureCountNotExistsException(File sqlFile, String sql, int resultCount) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The SQL expects EXISTS but the result was NOT EXISTS!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your test data!" + ln();
        msg = msg + ln();
        msg = msg + "[SQL File]" + ln() + sqlFile + ln();
        msg = msg + ln();
        msg = msg + "[Executed SQL]" + ln() + sql + ln();
        msg = msg + ln();
        msg = msg + "[Result Count]" + ln() + resultCount + ln();
        msg = msg + "* * * * * * * * * */";
        throw new DfTakeFinallyAssertionFailureCountNotExistsException(msg);
    }

    protected void throwAssertionFailureListNotZeroException(File sqlFile, String sql, int resultCount,
            List<Map<String, String>> resultList) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The SQL expects ZERO but the result was NOT ZERO!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your test data!" + ln();
        msg = msg + ln();
        msg = msg + "[SQL File]" + ln() + sqlFile + ln();
        msg = msg + ln();
        msg = msg + "[Executed SQL]" + ln() + sql + ln();
        msg = msg + ln();
        msg = msg + "[Result Count]" + ln() + resultCount + ln();
        msg = msg + ln();
        msg = msg + "[Result List]" + ln();
        for (Map<String, String> recordMap : resultList) {
            msg = msg + recordMap + ln();
        }
        msg = msg + "* * * * * * * * * */";
        throw new DfTakeFinallyAssertionFailureListNotZeroException(msg);
    }

    protected void throwAssertionFailureListNotExistsException(File sqlFile, String sql, int resultCount,
            List<Map<String, String>> resultList) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The SQL expects EXISTS but the result was NOT EXISTS!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your test data!" + ln();
        msg = msg + ln();
        msg = msg + "[SQL File]" + ln() + sqlFile + ln();
        msg = msg + ln();
        msg = msg + "[Executed SQL]" + ln() + sql + ln();
        msg = msg + ln();
        msg = msg + "[Result Count]" + ln() + resultCount + ln();
        msg = msg + ln();
        msg = msg + "[Result List]" + ln();
        for (Map<String, String> recordMap : resultList) {
            msg = msg + recordMap + ln();
        }
        msg = msg + "* * * * * * * * * */";
        throw new DfTakeFinallyAssertionFailureListNotExistsException(msg);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return "\n";
    }
}
