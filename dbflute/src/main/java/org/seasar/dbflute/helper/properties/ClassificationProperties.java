package org.seasar.dbflute.helper.properties;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.Table;
import org.apache.torque.helper.stateless.NameHintUtil;

/**
 * Build properties for Torque.
 * 
 * @author mkubo
 */
public final class ClassificationProperties extends AbstractHelperProperties {

    private static final Log _log = LogFactory.getLog(ClassificationProperties.class);

    public ClassificationProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                                     Properties - Classification
    //                                                     ===========================
    // --------------------------------------
    //                             Definition
    //                             ----------
    public static final String KEY_classificationDefinitionMap = "classificationDefinitionMap";
    protected Map<String, List<Map<String, String>>> _classificationDefinitionMap;

    public boolean hasClassificationDefinitionMap() {
        return !getClassificationDefinitionMap().isEmpty();
    }

    public Map<String, List<Map<String, String>>> getClassificationDefinitionMap() {
        if (_classificationDefinitionMap == null) {
            _classificationDefinitionMap = new LinkedHashMap<String, List<Map<String, String>>>();

            final String key = "torque." + KEY_classificationDefinitionMap;
            final Map<String, Object> tmpMap = mapProp(key, DEFAULT_EMPTY_MAP);
            final Set<String> definitionKeySet = tmpMap.keySet();

            for (String classificationName : definitionKeySet) {
                final Object value = tmpMap.get(classificationName);

                if (value instanceof List) {
                    final List<Map<String, String>> elementList = new ArrayList<Map<String, String>>();
                    final List tmpList = (List) value;
                    for (Object element : tmpList) {
                        if (element instanceof Map) {
                            final Map elementMap = (Map) element;

                            // ********
                            // revising
                            // ********
                            final String table = (String) elementMap.get("table");
                            if (table != null) {
                                final String code = (String) elementMap.get("code");
                                if (code == null) {
                                    String msg = "The code of " + classificationName + " should not be null";
                                    throw new IllegalStateException(msg + " at " + KEY_classificationDefinitionMap);
                                }

                                String name = (String) elementMap.get("name");
                                name = (name != null ? name : code);
                                String alias = (String) elementMap.get("alias");
                                alias = (alias != null ? alias : name);
                                java.util.List exceptCodeList = new java.util.ArrayList();
                                {
                                    final Object exceptCodeObj = (String) elementMap.get("exceptCodeList");
                                    if (exceptCodeObj != null) {
                                        if (exceptCodeObj instanceof java.util.List) {
                                            exceptCodeList = (java.util.List) exceptCodeObj;
                                        } else {
                                            String msg = "'exceptCodeList' should be java.util.List! But: "
                                                    + exceptCodeObj.getClass();
                                            msg = msg + " value=" + exceptCodeObj + " " + _classificationDefinitionMap;
                                            throw new IllegalStateException(msg);
                                        }
                                    }
                                }

                                final StringBuffer sb = new StringBuffer();
                                sb.append("select ").append(code).append(", ").append(name).append(", ").append(alias);
                                sb.append(" from ").append(table);

                                Connection conn = null;
                                Statement stmt = null;
                                ResultSet rs = null;
                                try {
                                    conn =  getBasicProperties().getConnection();
                                    stmt = conn.createStatement();
                                    _log.debug("/ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
                                    _log.debug("The classification sql: " + sb.toString());
                                    rs = stmt.executeQuery(sb.toString());
                                    while (rs.next()) {
                                        final String tmpCodeValue = rs.getString(code);
                                        final String tmpNameValue = rs.getString(name);
                                        final String tmpAliasValue = rs.getString(alias);

                                        if (exceptCodeList.contains(tmpCodeValue)) {
                                            _log.debug("    exceptCode: " + tmpCodeValue);
                                            continue;
                                        }

                                        final Map<String, String> selectedTmpMap = new LinkedHashMap<String, String>();
                                        selectedTmpMap.put("code", tmpCodeValue);
                                        selectedTmpMap.put("name", tmpNameValue);
                                        selectedTmpMap.put("alias", tmpAliasValue);
                                        _log.debug("    code: " + tmpCodeValue);
                                        _log.debug("    name: " + tmpNameValue);
                                        _log.debug("    alias: " + tmpAliasValue);
                                        elementList.add(selectedTmpMap);
                                    }
                                    _log.debug("- - - - - - - - /");
                                } catch (SQLException e) {
                                    throw new RuntimeException("The sql is " + sb.toString(), e);
                                } finally {
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
                            } else {
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
                                    elementMap.put("alias", code);
                                }

                                elementList.add(elementMap);
                            }
                        } else {
                            String msg = "The element of List should be Map but " + element.getClass();
                            msg = msg + " element = " + element;
                            throw new IllegalStateException(msg + " at " + KEY_classificationDefinitionMap);
                        }
                    }

                    _classificationDefinitionMap.put(classificationName, elementList);
                } else {
                    String msg = "The value of Map should be List but " + value.getClass();
                    msg = msg + " value = " + value;
                    throw new IllegalStateException(msg + " at " + KEY_classificationDefinitionMap);
                }
            }
        }
        return _classificationDefinitionMap;
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
                if (NameHintUtil.isHitByTheHint(columnName, columnNameHint)) {
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
                if (NameHintUtil.isHitByTheHint(columnName, columnNameHint)) {
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