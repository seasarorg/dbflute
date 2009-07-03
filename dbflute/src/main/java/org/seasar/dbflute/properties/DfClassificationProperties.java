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
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.ForeignKey;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.logic.clsresource.DfClassificationResourceAnalyzer;
import org.seasar.dbflute.properties.assistant.classification.DfClassificationElement;
import org.seasar.dbflute.properties.assistant.classification.DfClassificationIllegalPropertyTypeException;
import org.seasar.dbflute.properties.assistant.classification.DfClassificationInfo;
import org.seasar.dbflute.properties.assistant.classification.DfClassificationLiteralSetupper;
import org.seasar.dbflute.properties.assistant.classification.DfClassificationSqlResourceCloser;
import org.seasar.dbflute.properties.assistant.classification.DfClassificationTop;
import org.seasar.dbflute.properties.assistant.classification.DfClassificationTopSqlExecutor;

/**
 * The properties for classification.
 * @author jflute
 */
public final class DfClassificationProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfClassificationProperties.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final List<DfClassificationInfo> _tableClassificationList = new ArrayList<DfClassificationInfo>();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfClassificationProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                           Classification Definition
    //                                                           =========================
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
     * @return Determination.
     */
    public boolean hasClassificationDefinitionMap() {
        return !getClassificationDefinitionMap().isEmpty();
    }

    /**
     * Get the map of classification definition.
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
            // - - - - - - - - - - - - - - - - -
            // Check a duplicate classification.
            // - - - - - - - - - - - - - - - - -
            if (_classificationDefinitionMap.containsKey(classificationName)) {
                String msg = "Duplicate classification: " + classificationName;
                throw new IllegalStateException(msg);
            }

            // - - - - - - - - - - - - -
            // Handle special elements.
            // - - - - - - - - - - - - -
            if (classificationName.equalsIgnoreCase("$$SQL$$")) {
                final String topSql = (String) plainClassificationDefinitionMap.get(classificationName);
                setupAllInOneTableClassification(topSql);
                continue;
            }
            if (classificationName.equalsIgnoreCase("$$topCodeVariableNamePrefix$$")) {
                final String prefix = (String) plainClassificationDefinitionMap.get(classificationName);
                _classificationTopCodeVariableNamePrefix = prefix + "_";
                continue;
            }
            if (classificationName.equalsIgnoreCase("$$codeVariableNamePrefix$$")) {
                final String prefix = (String) plainClassificationDefinitionMap.get(classificationName);
                _classificationCodeVariableNamePrefix = prefix + "_";
                continue;
            }

            // - - - - - - - - - - - - - - - -
            // Handle classification elements.
            // - - - - - - - - - - - - - - - -
            final Object value = plainClassificationDefinitionMap.get(classificationName);
            if (!(value instanceof List)) {
                String msg = "A value of map for classification definition should be list for classification:";
                msg = msg + " classificationName=" + classificationName;
                msg = msg + " type=" + value.getClass() + " value=" + value;
                throw new DfClassificationIllegalPropertyTypeException(msg);
            }
            final List<?> plainList = (List<?>) value;
            final List<Map<String, String>> elementList = new ArrayList<Map<String, String>>();
            for (Object element : plainList) {
                if (!(element instanceof Map)) {
                    String msg = "An element of list for classification should be map for classification element:";
                    msg = msg + " classificationName=" + classificationName;
                    msg = msg + " type=" + element.getClass() + " element=" + element;
                    throw new DfClassificationIllegalPropertyTypeException(msg);
                }
                final Map<?, ?> elementMap = (Map<?, ?>) element;

                // - - - - - -
                // from Table
                // - - - - - -
                final String table = (String) elementMap.get(DfClassificationInfo.KEY_TABLE);
                if (table != null) {
                    // Classification
                    final DfClassificationInfo classificationInfo = new DfClassificationInfo();
                    classificationInfo.setClassificationName(classificationName);
                    classificationInfo.setTable(table);
                    classificationInfo.acceptClassificationBasicElementMap(elementMap);
                    final String where = (String) elementMap.get("where");
                    final String orderBy = (String) elementMap.get("orderBy");
                    final String sql = buildSql(classificationInfo, table, where, orderBy);
                    final List<?> exceptCodeList = extractExceptCodeList(elementMap);
                    setupTableClassification(elementList, sql, classificationInfo, exceptCodeList);

                    // Save for auto deployment if it is NOT suppressAutoDeploy.
                    if (!isTableClassificationSuppressAutoDeploy(elementMap)) {
                        _tableClassificationList.add(classificationInfo);
                    }
                    continue;
                }

                // - - - - - - -
                // from Literal
                // - - - - - - -
                if (isElementMapClassificationMeta(elementMap)) {
                    setupClassificationMetaFromLiteralIfNeeds(classificationName, elementMap);
                } else {
                    new DfClassificationLiteralSetupper().setup(classificationName, elementMap, elementList);
                }
            }
            _classificationDefinitionMap.put(classificationName, elementList);
        }
        reflectClassificationResourceToDefinition(); // *Classification Resource Point!
        return _classificationDefinitionMap;
    }

    protected boolean isElementMapClassificationMeta(Map<?, ?> elementMap) {
        final String classificationTopCode = (String) elementMap.get(DfClassificationInfo.KEY_TOP_CODE);
        final String classificationTopComment = (String) elementMap.get(DfClassificationInfo.KEY_TOP_COMMENT);
        return classificationTopCode != null || classificationTopComment != null;
    }

    protected boolean isTableClassificationSuppressAutoDeploy(Map<?, ?> elementMap) {
        final String suppressAutoDeploy = (String) elementMap.get("suppressAutoDeploy");
        return suppressAutoDeploy != null && suppressAutoDeploy.equalsIgnoreCase("true");
    }

    // -----------------------------------------------------
    //                                            Initialize
    //                                            ----------
    public void initializeClassificationDefinition() {
        getClassificationDefinitionMap(); // Initialize
    }

    // -----------------------------------------------------
    //                                     Definition Helper
    //                                     -----------------
    protected void setupAllInOneTableClassification(String sql) {
        final DfClassificationTopSqlExecutor executor = new DfClassificationTopSqlExecutor();
        final Connection conn = getDatabaseProperties().getConnection();
        final List<Map<String, String>> resultList = executor.executeTopSql(conn, sql);

        for (Map<String, String> map : resultList) {
            final String classificationName = map.get("classificationName");

            final String code = map.get(DfClassificationInfo.KEY_CODE);
            final String name = map.get(DfClassificationInfo.KEY_NAME);
            final String alias = map.get(DfClassificationInfo.KEY_ALIAS);
            final String comment = map.get(DfClassificationInfo.KEY_COMMENT);
            final String topCode = map.get(DfClassificationInfo.KEY_TOP_CODE);
            final String topComment = map.get(DfClassificationInfo.KEY_TOP_COMMENT);

            final List<Map<String, String>> elementList;
            {
                List<Map<String, String>> tmpElementList = _classificationDefinitionMap.get(classificationName);
                if (tmpElementList == null) {
                    tmpElementList = new ArrayList<Map<String, String>>();
                    _classificationDefinitionMap.put(classificationName, tmpElementList);
                }
                elementList = tmpElementList;
            }

            final Map<String, String> elementMap = newLinkedHashMap();
            elementMap.put(DfClassificationInfo.KEY_CODE, code);
            elementMap.put(DfClassificationInfo.KEY_NAME, name);
            elementMap.put(DfClassificationInfo.KEY_ALIAS, alias);
            if (comment != null) {
                elementMap.put(DfClassificationInfo.KEY_COMMENT, comment);
            }
            elementList.add(elementMap);

            if (!_classificationTopDefinitionMap.containsKey(classificationName)) {
                final Map<String, String> topElementMap = new LinkedHashMap<String, String>();
                topElementMap.put("classificationName", classificationName);
                if (topCode != null) {
                    topElementMap.put(DfClassificationInfo.KEY_TOP_CODE, topCode);
                }
                if (topComment != null) {
                    topElementMap.put(DfClassificationInfo.KEY_TOP_COMMENT, topComment);
                }
                if (topCode != null || topComment != null) {
                    _classificationTopDefinitionMap.put(classificationName, topElementMap);
                }
            }
        }
    }

    protected List<?> extractExceptCodeList(final Map<?, ?> elementMap) {
        List<?> exceptCodeList = new ArrayList<Object>(); // Default Empty
        {
            final Object exceptCodeObj = (String) elementMap.get("exceptCodeList");
            if (exceptCodeObj != null) {
                if (!(exceptCodeObj instanceof java.util.List)) {
                    String msg = "'exceptCodeList' should be java.util.List! But: " + exceptCodeObj.getClass();
                    msg = msg + " value=" + exceptCodeObj + " " + _classificationDefinitionMap;
                    throw new IllegalStateException(msg);
                }
                exceptCodeList = (List<?>) exceptCodeObj;
            }
        }
        return exceptCodeList;
    }

    protected String buildSql(DfClassificationInfo info, String table, String where, String orderBy) {
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

    protected void setupTableClassification(List<Map<String, String>> elementList, String sql,
            DfClassificationInfo info, List<?> exceptCodeList) {
        final String code = info.getCode();
        final String name = info.getName();
        final String alias = info.getAlias();
        final String comment = info.getComment();
        doSetupTableClassification(elementList, sql, code, name, alias, comment, exceptCodeList);
    }

    protected void doSetupTableClassification(List<Map<String, String>> elementList, String sql, String code,
            String name, String alias, String comment, List<?> exceptCodeList) {
        code = removeAliasPrefixIfNeeds(code);
        name = removeAliasPrefixIfNeeds(name);
        alias = removeAliasPrefixIfNeeds(alias);
        comment = removeAliasPrefixIfNeeds(comment);

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getDatabaseProperties().getConnection();
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
                selectedTmpMap.put(DfClassificationInfo.KEY_CODE, tmpCodeValue);
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
            new DfClassificationSqlResourceCloser().closeSqlResource(conn, stmt, rs);
        }
    }

    protected String removeAliasPrefixIfNeeds(String name) {
        if (name != null && name.lastIndexOf(".") >= 0) {
            name = name.substring(name.lastIndexOf(".") + 1);
        }
        return name;
    }

    protected void setupClassificationMetaFromLiteralIfNeeds(String classificationName, Map<?, ?> elementMap) {
        if (_classificationTopDefinitionMap.containsKey(classificationName)) {
            return;
        }
        final DfClassificationInfo classificationInfo = new DfClassificationInfo();
        classificationInfo.acceptClassificationMetaElementMap(elementMap);
        final Map<String, String> groupMap = new LinkedHashMap<String, String>();
        groupMap.put("classificationName", classificationName);
        if (classificationInfo.getCode() != null) {
            groupMap.put(DfClassificationInfo.KEY_TOP_CODE, classificationInfo.getCode());
        }
        if (classificationInfo.getComment() != null) {
            groupMap.put(DfClassificationInfo.KEY_TOP_COMMENT, classificationInfo.getComment());
        }
        if (classificationInfo.getCode() != null || classificationInfo.getComment() != null) {
            _classificationTopDefinitionMap.put(classificationName, groupMap);
        }
    }

    // -----------------------------------------------------
    //                               Deriving Classification
    //                               -----------------------
    public List<String> getClassificationNameList() {
        return new ArrayList<String>(getClassificationDefinitionMap().keySet());
    }

    public List<String> getClassificationNameListValidNameOnly() {
        final String codeKey = DfClassificationInfo.KEY_CODE;
        final String nameKey = DfClassificationInfo.KEY_NAME;

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
        final String codeKey = DfClassificationInfo.KEY_CODE;
        final String nameKey = DfClassificationInfo.KEY_NAME;
        final String aliasKey = DfClassificationInfo.KEY_ALIAS;

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
        return filterDoubleQuotation(removeLineSeparator(property));
    }

    public List<java.util.Map<String, String>> getClassificationMapList(String classificationName) {
        return getClassificationDefinitionMap().get(classificationName);
    }

    public String buildClassificationApplicationComment(Map<String, String> classificationMap) {
        final StringBuilder sb = new StringBuilder();
        final String alias = classificationMap.get("alias");
        final String comment = classificationMap.get("comment");
        if (alias != null && alias.trim().length() > 0) {
            sb.append(alias);
        }
        if (comment != null && comment.trim().length() > 0) {
            if (sb.length() > 0) {
                sb.append(": ");
            }
            sb.append(comment);
        }
        return sb.toString();
    }

    public String buildClassificationCodeAliasVariables(Map<String, String> classificationMap) {
        final StringBuilder sb = new StringBuilder();
        final String code = classificationMap.get("code");
        final String alias = classificationMap.get("alias");
        sb.append("\"").append(code).append("\", ");
        if (alias != null && alias.trim().length() > 0) {
            sb.append("\"").append(alias).append("\"");
        } else {
            sb.append("null");
        }
        return sb.toString();
    }

    public String buildClassificationCodeNameAliasVariables(Map<String, String> classificationMap) {
        final StringBuilder sb = new StringBuilder();
        final String code = classificationMap.get("code");
        final String name = classificationMap.get("name");
        final String alias = classificationMap.get("alias");
        sb.append("\"").append(code).append("\", ").append("\"").append(name).append("\", ");
        if (alias != null && alias.trim().length() > 0) {
            sb.append("\"").append(alias).append("\"");
        } else {
            sb.append("null");
        }
        return sb.toString();
    }

    // ===================================================================================
    //                                                           Classification Deployment
    //                                                           =========================
    public static final String KEY_classificationDeploymentMap = "classificationDeploymentMap";
    public static final String MARK_allColumnClassification = "$$ALL$$";
    protected Map<String, Map<String, String>> _classificationDeploymentMap;

    // --------------------------------------
    //                                 Getter
    //                                 ------
    public Map<String, Map<String, String>> getClassificationDeploymentMap() {
        if (_classificationDeploymentMap != null) {
            return _classificationDeploymentMap;
        }
        final Map<String, Object> map = mapProp("torque." + KEY_classificationDeploymentMap, DEFAULT_EMPTY_MAP);

        // It's flexible because table name should be treated as flexible/
        _classificationDeploymentMap = StringKeyMap.createAsFlexibleOrder();
        final Set<String> deploymentMapkeySet = map.keySet();
        for (String tableName : deploymentMapkeySet) {
            final Object value = map.get(tableName);
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                final Map<String, String> tmpMap = (Map<String, String>) value;
                final Set<String> tmpMapKeySet = tmpMap.keySet();

                // It's normal map because this column name key contains hint.
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
        reflectClassificationResourceToDeployment(); // *Classification Resource Point!
        return _classificationDeploymentMap;
    }

    // --------------------------------------
    //                             Initialize
    //                             ----------
    /**
     * Initialize classification deployment. <br />
     * Resolving all column classifications and table classifications. <br />
     * You can call this several times with other database objects. <br />
     * This method calls initializeClassificationDefinition() internally.
     * @param database The database object. (NotNull)
     */
    public void initializeClassificationDeployment(Database database) { // This should be called when the task start.
        final Map<String, Map<String, String>> deploymentMap = getClassificationDeploymentMap();
        final Map<String, String> allColumnClassificationMap = getAllColumnClassificationMap();
        if (allColumnClassificationMap != null) {
            final List<Table> tableList = database.getTableList();
            for (Table table : tableList) {
                final Map<String, String> columnClsMap = getColumnClsMap(deploymentMap, table.getName());
                final Set<String> columnNameKeySet = allColumnClassificationMap.keySet();
                for (String columnName : columnNameKeySet) {
                    final String classificationName = allColumnClassificationMap.get(columnName);
                    columnClsMap.put(columnName, classificationName);
                }
            }
        }
        initializeClassificationDefinition();
        for (DfClassificationInfo info : _tableClassificationList) {
            final Map<String, String> columnClsMap = getColumnClsMap(deploymentMap, info.getTable());
            final String classificationName = info.getClassificationName();
            registerColumnClsIfNeeds(columnClsMap, info.getCode(), classificationName);
            final Table table = database.getTable(info.getTable());
            if (table == null || table.hasTwoOrMorePrimaryKeys()) {
                continue;
            }
            final Column column = table.getColumn(info.getCode());
            if (column == null || !column.isPrimaryKey()) {
                continue;
            }
            final List<ForeignKey> referrers = column.getReferrers();
            for (ForeignKey referrer : referrers) {
                if (!referrer.isSimpleKeyFK()) {
                    continue;
                }
                final Table referrerTable = referrer.getTable();
                final Map<String, String> referrerClsMap = getColumnClsMap(deploymentMap, referrerTable.getName());
                final Column localColumnAsOne = referrer.getLocalColumnAsOne();
                registerColumnClsIfNeeds(referrerClsMap, localColumnAsOne.getName(), classificationName);
            }
        }
        _classificationDeploymentMap = deploymentMap;
    }

    protected Map<String, String> getColumnClsMap(Map<String, Map<String, String>> deploymentMap, String tableName) {
        Map<String, String> columnClassificationMap = deploymentMap.get(tableName);
        if (columnClassificationMap == null) {
            // It's normal map because this column name key contains hint.
            columnClassificationMap = new LinkedHashMap<String, String>();
            deploymentMap.put(tableName, columnClassificationMap);
        }
        return columnClassificationMap;
    }

    protected void registerColumnClsIfNeeds(Map<String, String> columnClsMap, String columnName,
            String classificationName) {
        final String value = columnClsMap.get(columnName);
        if (value != null) {
            return;
        }
        columnClsMap.put(columnName, classificationName);
    }

    public String getClassificationDeploymentMapAsStringRemovedLineSeparatorFilteredQuotation() {
        final String property = stringProp("torque." + KEY_classificationDeploymentMap, DEFAULT_EMPTY_MAP_STRING);
        return filterDoubleQuotation(removeLineSeparator(property));
    }

    // --------------------------------------
    //                Classification Handling
    //                -----------------------
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
                if (isHitByTheHint(columnName, columnNameHint)) {
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

        // Because columnClassificationMap is not flexible map.
        final String classificationName = getByFlexibleKey(columnClassificationMap, columnName);

        if (classificationName == null) {
            final Set<String> columnClassificationMapKeySet = columnClassificationMap.keySet();
            for (String columnNameHint : columnClassificationMapKeySet) {
                if (isHitByTheHint(columnName, columnNameHint)) {
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

    // --------------------------------------
    //              All Column Classification
    //              -------------------------
    /**
     * Get the map of all column classification.
     * @return The map of all column classification. (Nullable: If the mark would be not found)
     */
    public Map<String, String> getAllColumnClassificationMap() {
        return (Map<String, String>) getClassificationDeploymentMap().get(MARK_allColumnClassification);
    }

    /**
     * Is the column target of all column classification?
     * @param columnName The name of column. (NotNull)
     * @return Determination. (If all table classification does not exist, it returns false.)
     */
    public boolean isAllClassificationColumn(String columnName) {
        return getAllClassificationName(columnName) != null;
    }

    /**
     * Get the name of classification for all column.
     * @param columnName The name of column. (NotNull)
     * @return The name of classification for all column. (Nullable: If NotFound)
     */
    public String getAllClassificationName(String columnName) {
        final Map<String, String> allColumnClassificationMap = getAllColumnClassificationMap();
        if (allColumnClassificationMap == null) {
            return null;
        }

        // Because allColumnClassificationMap is not flexible map.
        final String classificationName = getByFlexibleKey(allColumnClassificationMap, columnName);
        if (classificationName == null) {
            final Set<String> columnNameHintSet = allColumnClassificationMap.keySet();
            for (String columnNameHint : columnNameHintSet) {
                if (isHitByTheHint(columnName, columnNameHint)) {
                    return allColumnClassificationMap.get(columnNameHint);
                }
            }
        }
        return null;
    }

    protected void setupAllColumnClassificationEmptyMapIfNeeds() { // for Using Classification Resource
        if (getAllColumnClassificationMap() != null) {
            return;
        }
        final Map<String, Map<String, String>> classificationDeploymentMap = getClassificationDeploymentMap();
        classificationDeploymentMap.put(MARK_allColumnClassification, new LinkedHashMap<String, String>());
    }

    // ===================================================================================
    //                                                             Classification Resource
    //                                                             =======================
    protected static final String NAME_CLASSIFICATION_RESOURCE = "classificationResource";
    protected List<DfClassificationTop> _classificationResourceList;

    protected List<DfClassificationTop> getClassificationResourceList() {
        if (_classificationResourceList != null) {
            return _classificationResourceList;
        }
        _classificationResourceList = extractClassificationResource();
        return _classificationResourceList;
    }

    protected List<DfClassificationTop> extractClassificationResource() {
        final DfClassificationResourceAnalyzer analyzer = new DfClassificationResourceAnalyzer();
        final String dirBaseName = "./dfprop";
        final String resource = NAME_CLASSIFICATION_RESOURCE;
        final String extension = "dfprop";
        if (isEnvironmentDefault()) {
            return analyzer.analyze(dirBaseName, resource, extension);
        }
        final String dirEnvName = dirBaseName + "/" + getEnvironmentType();
        final List<DfClassificationTop> ls = analyzer.analyze(dirEnvName, resource, extension);
        if (!ls.isEmpty()) {
            return ls;
        }
        return analyzer.analyze(dirBaseName, resource, extension);
    }

    protected void reflectClassificationResourceToDefinition() {
        final List<DfClassificationTop> classificationTopList = getClassificationResourceList();
        for (DfClassificationTop classificationTop : classificationTopList) {
            final String classificationName = classificationTop.getClassificationName();
            if (_classificationDefinitionMap.containsKey(classificationName)) {
                continue;
            }

            // Reflect to classification top definition.
            final Map<String, String> topElementMap = new LinkedHashMap<String, String>();
            topElementMap.put(DfClassificationInfo.KEY_TOP_COMMENT, classificationTop.getTopComment());
            _classificationTopDefinitionMap.put(classificationName, topElementMap);

            // Reflect to classification definition.
            final List<Map<String, String>> elementList = new ArrayList<Map<String, String>>();
            final List<DfClassificationElement> classificationElementList = classificationTop
                    .getClassificationElementList();
            for (DfClassificationElement classificationElement : classificationElementList) {
                final Map<String, String> elementMap = new LinkedHashMap<String, String>();
                elementMap.put(DfClassificationInfo.KEY_CODE, classificationElement.getCode());
                elementMap.put(DfClassificationInfo.KEY_NAME, classificationElement.getName());
                final String alias = classificationElement.getAlias();
                if (alias != null) {
                    elementMap.put(DfClassificationInfo.KEY_ALIAS, alias);
                }
                final String comment = classificationElement.getComment();
                if (comment != null) {
                    elementMap.put(DfClassificationInfo.KEY_COMMENT, comment);
                }
                elementList.add(elementMap);
            }
            _classificationDefinitionMap.put(classificationName, elementList);
        }
    }

    protected void reflectClassificationResourceToDeployment() {
        final List<DfClassificationTop> classificationTopList = getClassificationResourceList();
        for (DfClassificationTop classificationTop : classificationTopList) {
            final String classificationName = classificationTop.getClassificationName();
            final String relatedColumnName = classificationTop.getRelatedColumnName();
            if (relatedColumnName == null) {
                continue;
            }
            setupAllColumnClassificationEmptyMapIfNeeds();
            final Map<String, String> allColumnClassificationMap = getAllColumnClassificationMap();
            if (allColumnClassificationMap.containsKey(relatedColumnName)) {
                continue;
            }
            allColumnClassificationMap.put(relatedColumnName, classificationName);
        }
    }
}