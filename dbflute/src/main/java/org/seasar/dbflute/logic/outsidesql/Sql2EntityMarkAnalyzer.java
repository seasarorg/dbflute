package org.seasar.dbflute.logic.outsidesql;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 * @since 0.9.5 (2009/04/10 Friday)
 */
public class Sql2EntityMarkAnalyzer {

    // ===================================================================================
    //                                                                           Analyzing
    //                                                                           =========
    /**
     * @param sql The string of SQL. (NotNull)
     * @return The name of entity. (Nullable: If it's not found, this returns null)
     */
    public String getCustomizeEntityName(String sql) {
        return getTargetString(sql, "#");
    }

    public boolean isCursor(final String sql) {
        final String targetString = getTargetString(sql, "+");
        return targetString != null && (targetString.contains("cursor") || targetString.contains("cursol"));
    }

    public List<String> getCustomizeEntityPropertyTypeList(final String sql) {
        return getTargetList(sql, "##");
    }

    /**
     * @param sql The string of SQL. (NotNull)
     * @return The name of parameter-bean. (Nullable: If it's not found, this returns null)
     */
    public String getParameterBeanName(final String sql) {
        return getTargetString(sql, "!");
    }

    public List<String> getParameterBeanPropertyTypeList(final String sql) {
        return getTargetList(sql, "!!");
    }

    public List<String> getPrimaryKeyColumnNameList(final String sql) {
        if (sql == null || sql.trim().length() == 0) {
            String msg = "The sql is invalid: " + sql;
            throw new IllegalArgumentException(msg);
        }
        final List<String> retLs = new ArrayList<String>();
        String primaryKeyColumnNameSeparatedString = getStringBetweenBeginEndMark(sql, "--*", "*");
        if (primaryKeyColumnNameSeparatedString == null || primaryKeyColumnNameSeparatedString.trim().length() == 0) {
            primaryKeyColumnNameSeparatedString = getStringBetweenBeginEndMark(sql, "-- *", "*");// for MySQL.
        }
        if (primaryKeyColumnNameSeparatedString != null && primaryKeyColumnNameSeparatedString.trim().length() != 0) {
            final StringTokenizer st = new StringTokenizer(primaryKeyColumnNameSeparatedString, ",;/\t");
            while (st.hasMoreTokens()) {
                final String nextToken = st.nextToken();
                retLs.add(nextToken.trim());
            }
        }
        return retLs;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected String getTargetString(final String sql, final String mark) {
        final List<String> targetList = getTargetList(sql, mark);
        return !targetList.isEmpty() ? targetList.get(0) : null;
    }

    protected List<String> getTargetList(final String sql, final String mark) {
        if (sql == null || sql.trim().length() == 0) {
            String msg = "The sql is invalid: " + sql;
            throw new IllegalArgumentException(msg);
        }
        final List<String> betweenBeginEndMarkList = getListBetweenBeginEndMark(sql, "--" + mark, mark);
        if (!betweenBeginEndMarkList.isEmpty()) {
            return betweenBeginEndMarkList;
        } else {
            // for MySQL. 
            return getListBetweenBeginEndMark(sql, "-- " + mark, mark);
        }
    }

    protected String getStringBetweenBeginEndMark(String targetStr, String beginMark, String endMark) {
        return DfStringUtil.extractFirstScope(targetStr, beginMark, endMark);
    }

    protected List<String> getListBetweenBeginEndMark(String targetStr, String beginMark, String endMark) {
        return DfStringUtil.extractAllScope(targetStr, beginMark, endMark);
    }
}
