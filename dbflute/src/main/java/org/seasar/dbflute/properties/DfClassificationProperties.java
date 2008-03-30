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
import org.seasar.dbflute.helper.flexiblename.DfFlexibleNameMap;
import org.seasar.dbflute.util.DfNameHintUtil;

/**
 * Classification properties.
 * @author jflute
 */
public final class DfClassificationProperties extends DfAbstractHelperProperties {

    private static final Log _log = LogFactory.getLog(DfClassificationProperties.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfClassificationProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                         Properties - Classification
    //                                                         ===========================
    // -----------------------------------------------------
    //                             Classification Definition
    //                             -------------------------
    public static final String KEY_classificationDefinitionMap = "classificationDefinitionMap";

    protected String _classificationTopCodeVariableNamePrefix;
    protected String _classificationCodeVariableNamePrefix;
    protected Map<String, Map<String, String>> _classificationTopDefinitionMap;
    protected Map<String, List<Map<String, String>>> _classificationDefinitionMap;

    public String getClassificationTopCodeVariableNamePrefix() {
        if (_classificationTopCodeVariableNamePrefix != null) {
            return _classificationTopCodeVariableNamePrefix;
        }
        getClassificationDefinitionMap();// Initialize!
        if (_classificationTopCodeVariableNamePrefix == null) {
            _classificationTopCodeVariableNamePrefix = "TOP_CODE_";
        }
        return _classificationTopCodeVariableNamePrefix;
    }

    public String getClassificationCodeVariableNamePrefix() {
        if (_classificationCodeVariableNamePrefix != null) {
            return _classificationCodeVariableNamePrefix;
        }
        getClassificationDefinitionMap();// Initialize!
        if (_classificationCodeVariableNamePrefix == null) {
            _classificationCodeVariableNamePrefix = "CODE_";
        }
        return _classificationCodeVariableNamePrefix;
    }

    /**
     * Get the map of classification TOP definition.
     * 
     * @return The map of classification TOP definition. (NotNull)
     */
    public Map<String, Map<String, String>> getClassificationTopDefinitionMap() {
        if (_classificationTopDefinitionMap != null) {
            return _classificationTopDefinitionMap;
        }
        getClassificationDefinitionMap();// Initialize!
        return _classificationTopDefinitionMap;
    }

    /**
     * Has the map of classification definition.
     * 
     * @return Determination.
     */
    public boolean hasClassificationDefinitionMap() {
        return !getClassificationDefinitionMap().isEmpty();
    }

    /**
     * Get the map of classification definition.
     * 
     * @return The map of classification definition. (NotNull)
     */
    public Map<String, List<Map<String, String>>> getClassificationDefinitionMap() {
        if (_classificationDefinitionMap != null) {
            return _classificationDefinitionMap;
        }
        _classificationTopDefinitionMap = new LinkedHashMap<String, Map<String, String>>();
        _classificationDefinitionMap = new LinkedHashMap<String, List<Map<String, String>>>();

        final String key = "torque." + KEY_classificationDefinitionMap;
        final Map<String, Object> plainClassificationDefinitionMap = mapProp(key, DEFAULT_EMPTY_MAP);
        final Set<String> classificationNameSet = plainClassificationDefinitionMap.keySet();

        for (String classificationName : classificationNameSet) {
            if (_classificationDefinitionMap.containsKey(classificationName)) {
                String msg = "Duplicate classification: " + classificationName;
                throw new IllegalStateException(msg);
            }
            if (classificationName.equals("$$SQL$$")) {
                final String topSql = (String) plainClassificationDefinitionMap.get(classificationName);
                setupFromDatabaseByTopSql(topSql);
                continue;
            }
            if (classificationName.equals("$$topCodeVariableNamePrefix$$")) {
                final String prefix = (String) plainClassificationDefinitionMap.get(classificationName);
                _classificationTopCodeVariableNamePrefix = prefix + "_";
                continue;
            }
            if (classificationName.equals("$$codeVariableNamePrefix$$")) {
                final String prefix = (String) plainClassificationDefinitionMap.get(classificationName);
                _classificationCodeVariableNamePrefix = prefix + "_";
                continue;
            }

            final Object value = plainClassificationDefinitionMap.get(classificationName);
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
                    new ClassificationLiteralSetupper().setup(classificationName, elementMap, elementList);
                }
            }
            _classificationDefinitionMap.put(classificationName, elementList);
        }
        return _classificationDefinitionMap;
    }

    // -----------------------------------------------------
    //                                     Definition Helper
    //                                     -----------------
    protected void setupFromDatabaseByTopSql(String sql) {
        final DfBasicProperties basicProperties = getBasicProperties();
        final ClassificationTopSqlExecutor executor = new ClassificationTopSqlExecutor();
        final List<Map<String, String>> resultList = executor.executeTopSql(basicProperties.getConnection(), sql);

        for (Map<String, String> map : resultList) {
            final String classificationName = map.get("classificationName");

            final String code = map.get(ClassificationInfo.KEY_CODE);
            final String name = map.get("name");
            final String alias = map.get("alias");
            final String comment = map.get("comment");
            final String topCode = map.get(ClassificationInfo.KEY_TOP_CODE);
            final String topComment = map.get(ClassificationInfo.KEY_TOP_COMMENT);

            List<Map<String, String>> elementList = _classificationDefinitionMap.get(classificationName);
            if (elementList == null) {
                elementList = new ArrayList<Map<String, String>>();
                _classificationDefinitionMap.put(classificationName, elementList);
            }

            final Map<String, String> elementMap = new LinkedHashMap<String, String>();
            elementMap.put(ClassificationInfo.KEY_CODE, code);
            elementMap.put("name", name);
            elementMap.put("alias", alias);
            if (comment != null) {
                elementMap.put("comment", comment);
            }
            elementList.add(elementMap);

            if (!_classificationTopDefinitionMap.containsKey(classificationName)) {
                final Map<String, String> topElementMap = new LinkedHashMap<String, String>();
                topElementMap.put("classificationName", classificationName);
                if (topCode != null) {
                    topElementMap.put(ClassificationInfo.KEY_TOP_CODE, topCode);
                }
                if (topComment != null) {
                    topElementMap.put(ClassificationInfo.KEY_TOP_COMMENT, topComment);
                }
                if (topCode != null || topComment != null) {
                    _classificationTopDefinitionMap.put(classificationName, topElementMap);
                }
            }
        }
    }

    protected static class ClassificationTopSqlExecutor {
        public java.util.List<java.util.Map<String, String>> executeTopSql(Connection conn, String sql) {
            Statement stmt = null;
            ResultSet rs = null;
            final java.util.List<java.util.Map<String, String>> elementList = new ArrayList<Map<String, String>>();
            try {
                stmt = conn.createStatement();
                _log.debug("/ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
                _log.debug("The classification sql: " + sql);
                rs = stmt.executeQuery(sql);
                final Set<String> classificationNameDuplicateCheckSet = new HashSet<String>();
                while (rs.next()) {
                    final String tmpClassificationNameValue = rs.getString("classificationName");
                    final String tmpCodeValue = rs.getString(ClassificationInfo.KEY_CODE);
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
                    final String tmpTopCodeValue = rs.getString(ClassificationInfo.KEY_TOP_CODE);
                    final String tmpTopCommentValue = rs.getString(ClassificationInfo.KEY_TOP_COMMENT);

                    if (classificationNameDuplicateCheckSet.contains(tmpClassificationNameValue)) {
                        _log.debug("    duplicate: " + tmpClassificationNameValue);
                        continue;
                    }

                    final Map<String, String> selectedTmpMap = new LinkedHashMap<String, String>();
                    selectedTmpMap.put(ClassificationInfo.KEY_CODE, tmpCodeValue);
                    selectedTmpMap.put("name", tmpNameValue);
                    selectedTmpMap.put("alias", tmpAliasValue);
                    if (tmpCommentValue != null) {
                        selectedTmpMap.put("comment", tmpCommentValue);
                    }
                    if (tmpTopCodeValue != null) {
                        selectedTmpMap.put(ClassificationInfo.KEY_TOP_CODE, tmpTopCodeValue);
                    }
                    if (tmpTopCommentValue != null) {
                        selectedTmpMap.put(ClassificationInfo.KEY_TOP_COMMENT, tmpTopCommentValue);
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
                selectedTmpMap.put(ClassificationInfo.KEY_CODE, tmpCodeValue);
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
            new ClassificationSqlResourceCloser().closeSqlResource(conn, stmt, rs);
        }
    }

    protected String removeAliasPrefixIfNeeds(String name) {
        if (name != null && name.lastIndexOf(".") >= 0) {
            name = name.substring(name.lastIndexOf(".") + 1);
        }
        return name;
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

    protected static class ClassificationLiteralSetupper {

        @SuppressWarnings("unchecked")
        public void setup(String classificationName, Map elementMap, List<Map<String, String>> elementList) {
            final String codeKey = ClassificationInfo.KEY_CODE;
            final String nameKey = ClassificationInfo.KEY_NAME;
            final String aliasKey = ClassificationInfo.KEY_ALIAS;

            final String code = (String) elementMap.get(codeKey);
            if (code == null) {
                String msg = "The code of " + classificationName + " should not be null";
                throw new IllegalStateException(msg + " at " + KEY_classificationDefinitionMap);
            }
            final String name = (String) elementMap.get(nameKey);
            if (name == null) {
                elementMap.put(nameKey, code);
            }
            final String alias = (String) elementMap.get(aliasKey);
            if (alias == null) {
                elementMap.put(aliasKey, name != null ? name : code);
            }
            elementList.add(elementMap);
        }
    }

    public static class ClassificationSqlResourceCloser {
        public void closeSqlResource(Connection conn, Statement stmt, ResultSet rs) {
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
    }

    protected static class ClassificationInfo {

        public static final String KEY_CODE = "code";
        public static final String KEY_NAME = "name";
        public static final String KEY_ALIAS = "alias";
        public static final String KEY_COMMENT = "comment";
        public static final String KEY_TOP_CODE = "topCode";
        public static final String KEY_TOP_COMMENT = "topComment";

        protected String code;
        protected String name;
        protected String alias;
        protected String comment;
        protected boolean group;

        public void acceptClassificationMap(Map elementMap) {
            acceptMap(elementMap, ClassificationInfo.KEY_CODE, "name", "alias", "comment", false);
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

    // -----------------------------------------------------
    //                               Deriving Classification
    //                               -----------------------
    public List<String> getClassificationNameList() {
        return new ArrayList<String>(getClassificationDefinitionMap().keySet());
    }

    public List<String> getClassificationNameListValidNameOnly() {
        final String codeKey = ClassificationInfo.KEY_CODE;
        final String nameKey = ClassificationInfo.KEY_NAME;

        final List<String> resultList = new ArrayList<String>();
        final Set<String> keySet = getClassificationDefinitionMap().keySet();

        classificationNameListLoop: for (String string : keySet) {
            final List<Map<String, String>> list = getClassificationDefinitionMap().get(string);
            for (Map<String, String> map : list) {
                final String code = map.get(codeKey);
                final String name = map.get(nameKey);
                if (!code.equals(name)) {
                    resultList.add(string);
                    continue classificationNameListLoop;
                }
            }
        }
        return resultList;
    }

    public List<String> getClassificationNameListValidAliasOnly() {
        final String codeKey = ClassificationInfo.KEY_CODE;
        final String nameKey = ClassificationInfo.KEY_NAME;
        final String aliasKey = ClassificationInfo.KEY_ALIAS;

        final List<String> resultList = new ArrayList<String>();
        final Set<String> keySet = getClassificationDefinitionMap().keySet();

        classificationNameListLoop: for (String string : keySet) {
            final List<Map<String, String>> list = getClassificationDefinitionMap().get(string);
            for (Map<String, String> map : list) {
                final String code = map.get(codeKey);
                final String name = map.get(nameKey);
                final String alias = map.get(aliasKey);
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

    // --------------------------------------
    //                             Deployment
    //                             ----------
    public static final String KEY_classificationDeploymentMap = "classificationDeploymentMap";
    public static final String MARK_classificationDeploymentAllTable = "$$ALL$$";
    protected Map<String, Map<String, String>> _classificationDeploymentMap;

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
        final Map<String, Map<String, String>> deploymentMap = getClassificationDeploymentMap();
        final DfFlexibleNameMap<String, Map<String, String>> flexibledeploymentMap = new DfFlexibleNameMap<String, Map<String, String>>(
                deploymentMap);
        final Map<String, String> allColumnClassificationMap = getAllColumnClassificationMap();
        if (allColumnClassificationMap == null) {
            return;
        }
        for (Table table : tableList) {
            Map<String, String> columnClassificationMap = (Map<String, String>) flexibledeploymentMap.get(table.getName());
            if (columnClassificationMap == null) {
                columnClassificationMap = new LinkedHashMap<String, String>();
                deploymentMap.put(table.getName(), columnClassificationMap);
            }
            final Set<String> columnNameKeySet = allColumnClassificationMap.keySet();
            for (String columnName : columnNameKeySet) {
                columnClassificationMap.put(columnName, allColumnClassificationMap.get(columnName));
            }
        }
        _classificationDeploymentMap = deploymentMap;
    }

    public String getClassificationDeploymentMapAsStringRemovedLineSeparatorFilteredQuotation() {
        final String property = stringProp("torque." + KEY_classificationDeploymentMap, DEFAULT_EMPTY_MAP_STRING);
        return filterDoubleQuotation(removeNewLine(property));
    }

    public boolean hasClassification(String tableName, String columnName) {
        final Map<String, Map<String, String>> deploymentMap = getClassificationDeploymentMap();
        final DfFlexibleNameMap<String, Map<String, String>> flexibledeploymentMap = new DfFlexibleNameMap<String, Map<String, String>>(
                deploymentMap);
        final Map<String, String> columnClassificationMap = flexibledeploymentMap.get(tableName);
        if (columnClassificationMap == null) {
            return false;
        }
        final DfFlexibleNameMap<String, String> flexibleColumnClassificationMap = new DfFlexibleNameMap<String, String>(
                columnClassificationMap);
        final String classificationName = flexibleColumnClassificationMap.get(columnName);
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
        final DfFlexibleNameMap<String, Map<String, String>> flexibledeploymentMap = new DfFlexibleNameMap<String, Map<String, String>>(
                deploymentMap);
        if (!flexibledeploymentMap.containsKey(tableName)) {
            return null;
        }
        final Map<String, String> columnClassificationMap = flexibledeploymentMap.get(tableName);
        final DfFlexibleNameMap<String, String> flexibleColumnClassificationMap = new DfFlexibleNameMap<String, String>(
                columnClassificationMap);
        final String classificationName = flexibleColumnClassificationMap.get(columnName);
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