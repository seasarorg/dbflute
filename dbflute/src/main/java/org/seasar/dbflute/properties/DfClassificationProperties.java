package org.seasar.dbflute.properties;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.util.DfNameHintUtil;

/**
 * Build properties for Torque.
 * 
 * @author mkubo
 */
public final class DfClassificationProperties extends DfAbstractHelperProperties {

    private static final Log _log = LogFactory.getLog(DfClassificationProperties.class);

    public DfClassificationProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                                     Properties - Classification
    //                                                     ===========================
    // --------------------------------------
    //              Classification Definition
    //                             ----------
    public static final String KEY_classificationDefinitionMap = "classificationDefinitionMap";
    protected Map<String, Map<String, String>> _classificationTopDefinitionMap;
    protected Map<String, List<Map<String, String>>> _classificationDefinitionMap;

    public boolean hasClassificationDefinitionMap() {
        return !getClassificationDefinitionMap().isEmpty();
    }

    public Map<String, Map<String, String>> getClassificationTopDefinitionMap() {
        if (_classificationTopDefinitionMap != null) {
            return _classificationTopDefinitionMap;
        }
        getClassificationDefinitionMap();// Initialize!
        return _classificationTopDefinitionMap;
    }

    public Map<String, List<Map<String, String>>> getClassificationDefinitionMap() {
        if (_classificationDefinitionMap != null) {
            return _classificationDefinitionMap;
        }
        _classificationTopDefinitionMap = new LinkedHashMap<String, Map<String, String>>();
        _classificationDefinitionMap = new LinkedHashMap<String, List<Map<String, String>>>();

        final String key = "torque." + KEY_classificationDefinitionMap;
        final Map<String, Object> plainMap = mapProp(key, DEFAULT_EMPTY_MAP);
        final Set<String> definitionKeySet = plainMap.keySet();

        for (String classificationName : definitionKeySet) {
            if (classificationName.equals("$$SQL$$")) {
                final String superSql = (String) plainMap.get(classificationName);

                // TODO:
                System.out.println("superSql: " + superSql);

                continue;
            }

            final Object value = plainMap.get(classificationName);
            if (!(value instanceof List)) {
                String msg = "The value of Map should be List but " + value.getClass();
                msg = msg + " value = " + value;
                throw new IllegalStateException(msg + " at " + KEY_classificationDefinitionMap);
            }
            final List plainList = (List) value;
            final List<Map<String, String>> elementList = new ArrayList<Map<String, String>>();
            for (Object element : plainList) {
                if (!(element instanceof Map)) {
                    String msg = "The element of List should be Map but " + element.getClass();
                    msg = msg + " element = " + element;
                    throw new IllegalStateException(msg + " at " + KEY_classificationDefinitionMap);
                }
                final Map elementMap = (Map) element;

                // ********
                // revising
                // ********
                final String table = (String) elementMap.get("table");

                // - - - - - -
                // from Table
                // - - - - - -
                if (table != null) {
                    // Classification
                    final ClassificationInfo classificationInfo = new ClassificationInfo();
                    classificationInfo.acceptClassificationMap(elementMap);
                    final String where = (String) elementMap.get("where");
                    final String orderBy = (String) elementMap.get("orderBy");
                    final String sql = buildSql(classificationInfo, table, where, orderBy);
                    final java.util.List exceptCodeList = extractExceptCodeList(elementMap);
                    setupFromDatabase(elementList, sql, classificationInfo, exceptCodeList);

                    // Classification Meta
                    setupClassificationTopFromLiteralIfNeeds(classificationName, elementMap);
                    continue;
                }

                // - - - - - - -
                // from Literal
                // - - - - - - -
                final String classificationCode = (String) elementMap.get(ClassificationInfo.KEY_TOP_CODE);
                final String classificationComment = (String) elementMap.get(ClassificationInfo.KEY_TOP_COMMENT);
                if (classificationCode != null || classificationComment != null) {
                    setupClassificationTopFromLiteralIfNeeds(classificationName, elementMap);
                } else {
                    setupClassificationFromLiteral(classificationName, elementMap, elementList);
                }
            }
            _classificationDefinitionMap.put(classificationName, elementList);
        }
        return _classificationDefinitionMap;
    }

    protected void setupClassificationFromLiteral(String classificationName, Map elementMap,
            List<Map<String, String>> elementList) {
        final String code = (String) elementMap.get("code");
        if (code == null) {
            String msg = "The code of " + classificationName + " should not be null";
            throw new IllegalStateException(msg + " at " + KEY_classificationDefinitionMap);
        }
        final String name = (String) elementMap.get("name");
        if (name == null) {
            elementMap.put("name", code);
        }
        final String alias = (String) elementMap.get("alias");
        if (alias == null) {
            elementMap.put("alias", name != null ? name : code);
        }
        elementList.add(elementMap);
    }

    public List<String> getClassificationNameList() {
        return new ArrayList<String>(getClassificationDefinitionMap().keySet());
    }

    public List<String> getClassificationNameListValidNameOnly() {
        final List<String> resultList = new ArrayList<String>();
        final Set<String> keySet = getClassificationDefinitionMap().keySet();

        classificationNameListLoop: for (String string : keySet) {
            final List<Map<String, String>> list = getClassificationDefinitionMap().get(string);
            for (Map<String, String> map : list) {
                final String code = map.get("code");
                final String name = map.get("name");
                if (!code.equals(name)) {
                    resultList.add(string);
                    continue classificationNameListLoop;
                }
            }
        }
        return resultList;
    }

    public List<String> getClassificationNameListValidAliasOnly() {
        final List<String> resultList = new ArrayList<String>();
        final Set<String> keySet = getClassificationDefinitionMap().keySet();

        classificationNameListLoop: for (String string : keySet) {
            final List<Map<String, String>> list = getClassificationDefinitionMap().get(string);
            for (Map<String, String> map : list) {
                final String code = map.get("code");
                final String name = map.get("name");
                final String alias = map.get("alias");
                if (!code.equals(alias) && !name.equals(alias)) {
                    resultList.add(string);
                    continue classificationNameListLoop;
                }
            }
        }
        return resultList;
    }

    public String getClassificationDefinitionMapAsStringRemovedLineSeparatorFilteredQuotation() {
        final String property = stringProp("torque." + KEY_classificationDefinitionMap, DEFAULT_EMPTY_MAP_STRING);
        return filterDoubleQuotation(removeNewLine(property));
    }

    public List<java.util.Map<String, String>> getClassificationMapList(String classificationName) {
        return getClassificationDefinitionMap().get(classificationName);
    }

    // -----------------------------------------------------
    //                                     Definition Helper
    //                                     -----------------
    protected java.util.List extractExceptCodeList(final Map elementMap) {
        java.util.List exceptCodeList = new java.util.ArrayList();// Default Empty
        {
            final Object exceptCodeObj = (String) elementMap.get("exceptCodeList");
            if (exceptCodeObj != null) {
                if (!(exceptCodeObj instanceof java.util.List)) {
                    String msg = "'exceptCodeList' should be java.util.List! But: " + exceptCodeObj.getClass();
                    msg = msg + " value=" + exceptCodeObj + " " + _classificationDefinitionMap;
                    throw new IllegalStateException(msg);
                }
                exceptCodeList = (java.util.List) exceptCodeObj;
            }
        }
        return exceptCodeList;
    }

    protected String buildSql(ClassificationInfo info, String table, String where, String orderBy) {
        final String code = info.getCode();
        final String name = info.getName();
        final String alias = info.getAlias();
        final String comment = info.getComment();
        return buildSql(code, name, alias, comment, table, where, orderBy);
    }

    protected String buildSql(String code, String name, String alias, String comment, String table, String where,
            String orderBy) {
        final StringBuffer sb = new StringBuffer();
        sb.append("select ").append(code).append(", ").append(name).append(", ").append(alias);
        if (comment != null && comment.trim().length() != 0) {
            sb.append(", ").append(comment);
        }
        sb.append(" from ").append(table);
        if (where != null && where.trim().length() != 0) {
            sb.append(" where ").append(where);
        }
        if (orderBy != null && orderBy.trim().length() != 0) {
            sb.append(" order by ").append(orderBy);
        }
        return sb.toString();
    }

    protected void setupFromDatabase(java.util.List<Map<String, String>> elementList, String sql,
            ClassificationInfo info, java.util.List exceptCodeList) {
        final String code = info.getCode();
        final String name = info.getName();
        final String alias = info.getAlias();
        final String comment = info.getComment();
        setupFromDatabase(elementList, sql, code, name, alias, comment, exceptCodeList);
    }

    protected void setupFromDatabase(java.util.List<Map<String, String>> elementList, String sql, String code,
            String name, String alias, String comment, java.util.List exceptCodeList) {
        code = removeAliasPrefixIfNeeds(code);
        name = removeAliasPrefixIfNeeds(name);
        alias = removeAliasPrefixIfNeeds(alias);
        comment = removeAliasPrefixIfNeeds(comment);

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getBasicProperties().getConnection();
            stmt = conn.createStatement();
            _log.debug("/ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
            _log.debug("The classification sql: " + sql);
            rs = stmt.executeQuery(sql);
            final Set<String> codeDuplicateCheckSet = new HashSet<String>();
            while (rs.next()) {
                final String tmpCodeValue = rs.getString(code);
                final String tmpNameValue = rs.getString(name);
                final String tmpAliasValue = rs.getString(alias);
                String tmpCommentValue = null;
                if (comment != null && comment.trim().length() != 0) {
                    tmpCommentValue = rs.getString(comment);
                }

                if (exceptCodeList.contains(tmpCodeValue)) {
                    _log.debug("    exceptCode: " + tmpCodeValue);
                    continue;
                }

                if (codeDuplicateCheckSet.contains(tmpCodeValue)) {
                    _log.debug("    duplicate: " + tmpCodeValue);
                    continue;
                }

                final Map<String, String> selectedTmpMap = new LinkedHashMap<String, String>();
                selectedTmpMap.put("code", tmpCodeValue);
                selectedTmpMap.put("name", tmpNameValue);
                selectedTmpMap.put("alias", tmpAliasValue);
                if (tmpCommentValue != null) {
                    selectedTmpMap.put("comment", tmpCommentValue);
                }
                elementList.add(selectedTmpMap);
                codeDuplicateCheckSet.add(tmpCodeValue);
            }
            _log.debug("- - - - - - - - /");
        } catch (SQLException e) {
            throw new RuntimeException("The sql is " + sql, e);
        } finally {
            closeSqlResource(conn, stmt, rs);
        }
    }

    protected String removeAliasPrefixIfNeeds(String name) {
        if (name.lastIndexOf(".") >= 0) {
            name = name.substring(name.lastIndexOf(".") + 1);
        }
        return name;
    }

    protected void closeSqlResource(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (conn != null) {
                conn.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ignored) {
            _log.warn("The close() threw the exception: ", ignored);
        }
    }

    protected void setupClassificationTopFromLiteralIfNeeds(String classificationName, Map elementMap) {
        if (_classificationTopDefinitionMap.containsKey(classificationName)) {
            return;
        }
        final ClassificationInfo classificationInfo = new ClassificationInfo();
        classificationInfo.acceptClassificationMetaMap(elementMap);
        final Map<String, String> groupMap = new LinkedHashMap<String, String>();
        groupMap.put("classificationName", classificationName);
        if (classificationInfo.getCode() != null) {
            groupMap.put(ClassificationInfo.KEY_TOP_CODE, classificationInfo.getCode());
        }
        if (classificationInfo.getComment() != null) {
            groupMap.put(ClassificationInfo.KEY_TOP_COMMENT, classificationInfo.getComment());
        }
        if (classificationInfo.getCode() != null || classificationInfo.getComment() != null) {
            _classificationTopDefinitionMap.put(classificationName, groupMap);
        }
    }

    protected static class ClassificationInfo {

        public static final String KEY_TOP_CODE = "topCode";
        public static final String KEY_TOP_COMMENT = "topComment";

        protected String code;
        protected String name;
        protected String alias;
        protected String comment;
        protected boolean group;

        public void acceptClassificationMap(Map elementMap) {
            acceptMap(elementMap, "code", "name", "alias", "comment", false);
        }

        public void acceptClassificationMetaMap(Map elementMap) {
            group = true;
            acceptMap(elementMap, KEY_TOP_CODE, null, null, KEY_TOP_COMMENT, true);
        }

        protected void acceptMap(Map elementMap, String codeKey, String nameKey, String aliasKey, String commentKey,
                boolean group) {
            final String code = (String) elementMap.get(codeKey);
            if (!group && code == null) {
                String msg = "The elementMap should have " + codeKey + ".";
                throw new IllegalStateException(msg);
            }
            this.code = code;

            // name
            String name = (String) elementMap.get(nameKey);
            name = (name != null ? name : code);
            this.name = name;

            // alias
            String alias = (String) elementMap.get(aliasKey);
            alias = (alias != null ? alias : name);
            this.alias = alias;

            // comment
            final String comment = (String) elementMap.get(commentKey);
            this.comment = comment;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isGroup() {
            return group;
        }

        public void setGroup(boolean group) {
            this.group = group;
        }
    }

    // --------------------------------------
    //                             Deployment
    //                             ----------
    public static final String KEY_classificationDeploymentMap = "classificationDeploymentMap";
    public static final String MARK_classificationDeploymentAllTable = "$$ALL$$";
    protected Map<String, Map<String, String>> _classificationDeploymentMap;

    // TODO: 列名の大文字小文字を区別しないようにする。CaseInsensitiveMapかな？

    public Map<String, Map<String, String>> getClassificationDeploymentMap() {
        if (_classificationDeploymentMap == null) {
            final Map<String, Object> map = mapProp("torque." + KEY_classificationDeploymentMap, DEFAULT_EMPTY_MAP);
            _classificationDeploymentMap = new LinkedHashMap<String, Map<String, String>>();
            final Set<String> deploymentMapkeySet = map.keySet();
            for (String tableName : deploymentMapkeySet) {
                final Object value = map.get(tableName);
                if (value instanceof Map) {
                    final Map tmpMap = (Map) value;
                    final Set tmpMapKeySet = tmpMap.keySet();
                    final Map<String, String> columnClassificationMap = new LinkedHashMap<String, String>();
                    for (Object columnNameObj : tmpMapKeySet) {
                        final String columnName = (String) columnNameObj;
                        final String classificationName = (String) tmpMap.get(columnName);
                        columnClassificationMap.put(columnName, classificationName);
                    }
                    _classificationDeploymentMap.put(tableName, columnClassificationMap);
                } else {
                    String msg = "The value should be columnClassificationMap: ";
                    throw new IllegalStateException(msg + "type=" + value.getClass() + " value=" + value);
                }
            }
        }
        return _classificationDeploymentMap;
    }

    public void initializeClassificationDeploymentMap(List<Table> tableList) {
        final Map<String, Map<String, String>> map = getClassificationDeploymentMap();
        final Map<String, String> allColumnClassificationMap = getAllColumnClassificationMap();
        if (allColumnClassificationMap == null) {
            return;
        }
        for (Table table : tableList) {
            Map<String, String> columnClassificationMap = (Map<String, String>) map.get(table.getName());
            if (columnClassificationMap == null) {
                columnClassificationMap = new LinkedHashMap<String, String>();
                map.put(table.getName(), columnClassificationMap);
            }
            final Set<String> columnNameKeySet = allColumnClassificationMap.keySet();
            for (String columnName : columnNameKeySet) {
                columnClassificationMap.put(columnName, allColumnClassificationMap.get(columnName));
            }
        }
        _classificationDeploymentMap = map;
    }

    public String getClassificationDeploymentMapAsStringRemovedLineSeparatorFilteredQuotation() {
        final String property = stringProp("torque." + KEY_classificationDeploymentMap, DEFAULT_EMPTY_MAP_STRING);
        return filterDoubleQuotation(removeNewLine(property));
    }

    public boolean hasClassification(String tableName, String columnName) {
        final Map<String, Map<String, String>> deploymentMap = getClassificationDeploymentMap();
        final Map<String, String> columnClassificationMap = deploymentMap.get(tableName);
        if (columnClassificationMap == null) {
            return false;
        }
        final String classificationName = columnClassificationMap.get(columnName);
        if (classificationName == null) {
            final Set<String> columnClassificationMapKeySet = columnClassificationMap.keySet();
            for (String columnNameHint : columnClassificationMapKeySet) {
                if (DfNameHintUtil.isHitByTheHint(columnName, columnNameHint)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public String getClassificationName(String tableName, String columnName) {
        final Map<String, Map<String, String>> deploymentMap = getClassificationDeploymentMap();
        if (!deploymentMap.containsKey(tableName)) {
            return null;
        }
        final Map<String, String> columnClassificationMap = deploymentMap.get(tableName);
        final String classificationName = columnClassificationMap.get(columnName);
        if (classificationName == null) {
            final Set<String> columnClassificationMapKeySet = columnClassificationMap.keySet();
            for (String columnNameHint : columnClassificationMapKeySet) {
                if (DfNameHintUtil.isHitByTheHint(columnName, columnNameHint)) {
                    return columnClassificationMap.get(columnNameHint);
                }
            }
            return null;
        } else {
            return classificationName;
        }
    }

    public boolean hasClassificationName(String tableName, String columnName) {
        final String classificationName = getClassificationName(tableName, columnName);
        if (classificationName == null) {
            return false;
        }
        return getClassificationNameListValidNameOnly().contains(classificationName);
    }

    public boolean hasClassificationAlias(String tableName, String columnName) {
        final String classificationName = getClassificationName(tableName, columnName);
        if (classificationName == null) {
            return false;
        }
        return getClassificationNameListValidAliasOnly().contains(classificationName);
    }

    public Map<String, String> getAllColumnClassificationMap() {
        return (Map<String, String>) getClassificationDeploymentMap().get(MARK_classificationDeploymentAllTable);
    }

    public boolean isAllClassificationColumn(String columnName) {
        if (getAllColumnClassificationMap() == null) {
            return false;
        }
        return getAllColumnClassificationMap().containsKey(columnName);
    }

    public String getAllClassificationName(String columnName) {
        if (getAllColumnClassificationMap() == null) {
            return null;
        }
        return getAllColumnClassificationMap().get(columnName);
    }
}