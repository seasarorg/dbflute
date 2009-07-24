package org.seasar.dbflute.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 */
public final class DfAdditionalForeignKeyProperties extends DfAbstractHelperProperties {
    
    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String KEY_LOCAL_TABLE_NAME = "localTableName";
    public static final String KEY_FOREIGN_TABLE_NAME = "foreignTableName";
    public static final String KEY_LOCAL_COLUMN_NAME = "localColumnName";
    public static final String KEY_FOREIGN_COLUMN_NAME = "foreignColumnName";
    public static final String KEY_FIXED_CONDITION = "fixedCondition";
    public static final String KEY_FIXED_SUFFIX = "fixedSuffix";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param prop Properties. (NotNull)
     */
    public DfAdditionalForeignKeyProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                             additionalForeignKeyMap
    //                                                             =======================
    public static final String KEY_additionalForeignKeyMap = "additionalForeignKeyMap";
    protected Map<String, Map<String, String>> _additionalForeignKeyMap;

    public Map<String, Map<String, String>> getAdditionalForeignKeyMap() {
        if (_additionalForeignKeyMap == null) {
            _additionalForeignKeyMap = newLinkedHashMap();
            final Map<String, Object> generatedMap = mapProp("torque." + KEY_additionalForeignKeyMap, DEFAULT_EMPTY_MAP);
            final Set<String> fisrtKeySet = generatedMap.keySet();
            for (Object foreignName : fisrtKeySet) {// FK Loop!
                final Object firstValue = generatedMap.get(foreignName);
                if (!(firstValue instanceof Map<?, ?>)) {
                    String msg = "The value type should be Map: tableName=" + foreignName + " property=CustomizeDao";
                    msg = msg + " actualType=" + firstValue.getClass() + " actualValue=" + firstValue;
                    throw new IllegalStateException(msg);
                }
                final Map<?, ?> foreignDefinitionMap = (Map<?, ?>) firstValue;
                final Set<?> secondKeySet = foreignDefinitionMap.keySet();
                final Map<String, String> genericForeignDefinitiontMap = newLinkedHashMap();
                for (Object componentName : secondKeySet) {// FK Component Loop!
                    final Object secondValue = foreignDefinitionMap.get(componentName);
                    if (secondValue == null) {
                        continue;
                    }
                    if (!(componentName instanceof String)) {
                        String msg = "The key type should be String: foreignName=" + foreignName;
                        msg = msg + " property=AdditionalForeignKey";
                        msg = msg + " actualType=" + componentName.getClass() + " actualKey=" + componentName;
                        throw new IllegalStateException(msg);
                    }
                    if (!(secondValue instanceof String)) {
                        String msg = "The value type should be String: foreignName=" + foreignName;
                        msg = msg + " property=AdditionalForeignKey";
                        msg = msg + " actualType=" + secondValue.getClass() + " actualValue=" + secondValue;
                        throw new IllegalStateException(msg);
                    }
                    genericForeignDefinitiontMap.put((String) componentName, (String) secondValue);
                }
                _additionalForeignKeyMap.put((String) foreignName, genericForeignDefinitiontMap);
            }
        }
        return _additionalForeignKeyMap;
    }

    // ===================================================================================
    //                                                                      Finding Helper
    //                                                                      ==============
    public String findLocalTableName(String foreignName) {
        final Map<String, String> componentMap = getAdditionalForeignKeyMap().get(foreignName);
        return componentMap.get(KEY_LOCAL_TABLE_NAME);
    }

    public String findForeignTableName(String foreignName) {
        final Map<String, String> componentMap = getAdditionalForeignKeyMap().get(foreignName);
        return componentMap.get(KEY_FOREIGN_TABLE_NAME);
    }

    protected String findLocalColumnName(String foreignName) {
        final Map<String, String> componentMap = getAdditionalForeignKeyMap().get(foreignName);
        return componentMap.get(KEY_LOCAL_COLUMN_NAME);
    }

    protected String findForeignColumnName(String foreignName) {
        final Map<String, String> componentMap = getAdditionalForeignKeyMap().get(foreignName);
        return componentMap.get(KEY_FOREIGN_COLUMN_NAME);
    }

    public String findFixedCondition(String foreignName) {
        final Map<String, String> componentMap = getAdditionalForeignKeyMap().get(foreignName);
        String fixedCondition = componentMap.get(KEY_FIXED_CONDITION);
        if (fixedCondition != null && fixedCondition.trim().length() > 0) {
            fixedCondition = DfStringUtil.replace(fixedCondition, "$$ALIAS$$", "$$alias$$");
            fixedCondition = DfStringUtil.replace(fixedCondition, "$$ForeignAlias$$", "$$foreignAlias$$");
            fixedCondition = DfStringUtil.replace(fixedCondition, "$$LocalAlias$$", "$$localAlias$$");
        }
        return fixedCondition;
    }

    public String findFixedSuffix(String foreignName) {
        final Map<String, String> componentMap = getAdditionalForeignKeyMap().get(foreignName);
        return componentMap.get(KEY_FIXED_SUFFIX);
    }
    
    public List<String> findLocalColumnNameList(String foreignName) {
        final String property = findLocalColumnName(foreignName);
        if (property == null || property.trim().length() == 0) {
            return null;
        }
        final List<String> localColumnNameList = new ArrayList<String>();
        final StringTokenizer st = new StringTokenizer(property, "/");
        while (st.hasMoreElements()) {
            localColumnNameList.add(st.nextToken());
        }
        return localColumnNameList;
    }

    public List<String> findForeignColumnNameList(String foreignName) {
        final String property = findForeignColumnName(foreignName);
        if (property == null || property.trim().length() == 0) {
            return null;
        }
        final List<String> foreignColumnNameList = new ArrayList<String>();
        final StringTokenizer st = new StringTokenizer(property, "/");
        while (st.hasMoreElements()) {
            foreignColumnNameList.add(st.nextToken());
        }
        return foreignColumnNameList;
    }
}