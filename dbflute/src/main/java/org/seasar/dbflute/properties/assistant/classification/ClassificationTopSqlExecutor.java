package org.seasar.dbflute.properties.assistant.classification;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jflute
 * @since 0.9.5.1 (2009/07/03 Friday)
 */
public class ClassificationTopSqlExecutor {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(ClassificationTopSqlExecutor.class);

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public List<Map<String, String>> executeTopSql(Connection conn, String sql) {
        Statement stmt = null;
        ResultSet rs = null;
        final List<Map<String, String>> elementList = new ArrayList<Map<String, String>>();
        try {
            stmt = conn.createStatement();
            _log.debug("/ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
            _log.debug("The classification sql: " + sql);
            rs = stmt.executeQuery(sql);
            final Set<String> classificationNameDuplicateCheckSet = new HashSet<String>();
            while (rs.next()) {
                final String tmpClassificationNameValue = rs.getString("classificationName");
                final String tmpCodeValue = rs.getString(DfClassificationInfo.KEY_CODE);
                if (tmpCodeValue == null) {
                    String msg = "The sql should have 'code' column. But null: sql=" + sql;
                    throw new IllegalStateException(msg);
                }
                String tmpNameValue = rs.getString("name");
                if (tmpNameValue == null) {
                    tmpNameValue = tmpCodeValue;
                }
                String tmpAliasValue = rs.getString("alias");
                if (tmpAliasValue == null) {
                    tmpAliasValue = tmpNameValue;
                }
                final String tmpCommentValue = rs.getString("comment");
                final String tmpTopCodeValue = rs.getString(DfClassificationInfo.KEY_TOP_CODE);
                final String tmpTopCommentValue = rs.getString(DfClassificationInfo.KEY_TOP_COMMENT);

                if (classificationNameDuplicateCheckSet.contains(tmpClassificationNameValue)) {
                    _log.debug("    duplicate: " + tmpClassificationNameValue);
                    continue;
                }

                final Map<String, String> selectedTmpMap = new LinkedHashMap<String, String>();
                selectedTmpMap.put(DfClassificationInfo.KEY_CODE, tmpCodeValue);
                selectedTmpMap.put("name", tmpNameValue);
                selectedTmpMap.put("alias", tmpAliasValue);
                if (tmpCommentValue != null) {
                    selectedTmpMap.put("comment", tmpCommentValue);
                }
                if (tmpTopCodeValue != null) {
                    selectedTmpMap.put(DfClassificationInfo.KEY_TOP_CODE, tmpTopCodeValue);
                }
                if (tmpTopCommentValue != null) {
                    selectedTmpMap.put(DfClassificationInfo.KEY_TOP_COMMENT, tmpTopCommentValue);
                }

                elementList.add(selectedTmpMap);
                classificationNameDuplicateCheckSet.add(tmpClassificationNameValue);
            }
            _log.debug("- - - - - - - - /");
        } catch (SQLException e) {
            throw new RuntimeException("The sql is " + sql, e);
        } finally {
            new ClassificationSqlResourceCloser().closeSqlResource(conn, stmt, rs);
        }
        return elementList;
    }
}
