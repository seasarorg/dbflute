/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.dbflute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.properties.DfAdditionalForeignKeyProperties;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfBehaviorFilterProperties;
import org.seasar.dbflute.properties.DfClassificationProperties;
import org.seasar.dbflute.properties.DfCommonColumnProperties;
import org.seasar.dbflute.properties.DfDBFluteDiconProperties;
import org.seasar.dbflute.properties.DfFlexDtoProperties;
import org.seasar.dbflute.properties.DfGeneratedClassPackageProperties;
import org.seasar.dbflute.properties.DfIncludeQueryProperties;
import org.seasar.dbflute.properties.DfInvokeSqlDirectoryProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;
import org.seasar.dbflute.properties.DfOptimisticLockProperties;
import org.seasar.dbflute.properties.DfOtherProperties;
import org.seasar.dbflute.properties.DfOutsideSqlProperties;
import org.seasar.dbflute.properties.DfRefreshProperties;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.properties.DfS2DaoAdjustmentProperties;
import org.seasar.dbflute.properties.DfS2jdbcProperties;
import org.seasar.dbflute.properties.DfSelectParamProperties;
import org.seasar.dbflute.properties.DfSequenceIdentityProperties;
import org.seasar.dbflute.properties.DfSimpleDtoProperties;
import org.seasar.dbflute.properties.DfSourceReductionProperties;
import org.seasar.dbflute.properties.DfTypeMappingProperties;
import org.seasar.dbflute.properties.handler.DfPropertiesHandler;
import org.seasar.dbflute.util.DfPropertyUtil;
import org.seasar.dbflute.util.DfPropertyUtil.PropertyBooleanFormatException;
import org.seasar.dbflute.util.DfPropertyUtil.PropertyNotFoundException;

/**
 * Build properties.
 * <pre>
 * Singletonでbuild.propertiesの情報を保持する。
 * ProcessのInitialize時にsetProperties()でPropertiesを設定されることが前提である。
 * </pre>
 * @author jflute
 */
public final class DfBuildProperties {

    /** Log-instance. */
    private static final Log _log = LogFactory.getLog(DfBuildProperties.class);

    /** Singleton-instance. */
    private static final DfBuildProperties _instance = new DfBuildProperties();

    /** The delimiter of map-list-string. */
    private static final String MAP_LIST_STRING_DELIMITER = ";";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Build properties. */
    private Properties _buildProperties;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor. (Private for Singleton)
     */
    private DfBuildProperties() {
    }

    /**
     * Get singleton-instance.
     * 
     * @return Singleton-instance. (NotNull)
     */
    public synchronized static DfBuildProperties getInstance() {
        return _instance;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Set build-properties.
     * <pre>
     * This method should be invoked at first initialization.
     * Don't invoke with build-properties null.
     * </pre>
     * @param buildProperties Build-properties. (NotNull)
     */
    final public void setProperties(Properties buildProperties) {
        _buildProperties = buildProperties;
    }

    /**
     * Get Build-properties.
     * 
     * @return Build-properties. (NotNull)
     */
    final public Properties getProperties() {
        return _buildProperties;
    }

    // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
    // TODO: @jflute - DfAbstractHelperPropertiesに移行中。移行完了後は削除
    // ===================================================================================
    //                                                                    Property Utility
    //                                                                    ================
    /**
     * Get property as string. {Delegate method}
     * 
     * @param key Property-key. (NotNull)
     * @param defaultValue Default value. (Nullable)
     * @return Property as string. (Nullable: If the default-value is null)
     * @deprecated
     */
    final public String stringProp(String key, String defaultValue) {
        try {
            return DfPropertyUtil.stringProp(_buildProperties, key);
        } catch (PropertyNotFoundException e) {
            return defaultValue;
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#stringProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    /**
     * Get property as boolean. {Delegate method}
     * 
     * @param key Property-key. (NotNull)
     * @param defaultValue Default value.
     * @return Property as boolean.
     * @deprecated
     */
    final public boolean booleanProp(String key, boolean defaultValue) {
        try {
            return DfPropertyUtil.booleanProp(_buildProperties, key);
        } catch (PropertyNotFoundException e) {
            return defaultValue;
        } catch (PropertyBooleanFormatException e) {
            return defaultValue;
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#intProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    /**
     * Get property as list. {Delegate method}
     * 
     * @param key Property-key. (NotNull)
     * @param defaultValue Default value. (Nullable)
     * @return Property as list. (Nullable: If the default-value is null)
     * @deprecated
     */
    final public List<Object> listProp(String key, List<Object> defaultValue) {
        try {
            final List<Object> result = DfPropertyUtil.listProp(_buildProperties, key, MAP_LIST_STRING_DELIMITER);
            if (result.isEmpty()) {
                return defaultValue;
            } else {
                return result;
            }
        } catch (PropertyNotFoundException e) {
            return defaultValue;
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#intProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    /**
     * Get property as map. {Delegate method}
     * 
     * @param key Property-key. (NotNull)
     * @param defaultValue Default value. (Nullable)
     * @return Property as map. (Nullable: If the default-value is null)
     * @deprecated
     */
    final public Map<String, Object> mapProp(String key, Map<String, Object> defaultValue) {
        try {
            final Map<String, Object> result = DfPropertyUtil.mapProp(_buildProperties, key, MAP_LIST_STRING_DELIMITER);
            if (result.isEmpty()) {
                return defaultValue;
            } else {
                return result;
            }
        } catch (PropertyNotFoundException e) {
            return defaultValue;
        } catch (RuntimeException e) {
            _log.warn("FlPropertyUtil#intProp() threw the exception with The key[" + key + "]", e);
            throw e;
        }
    }

    // ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★s

    // ===================================================================================
    //                                                                  Default Definition
    //                                                                  ==================
    public static final Map<String, Object> DEFAULT_EMPTY_MAP = new LinkedHashMap<String, Object>();
    public static final List<Object> DEFAULT_EMPTY_LIST = new ArrayList<Object>();
    public static final String DEFAULT_EMPTY_MAP_STRING = "map:{}";
    public static final String DEFAULT_EMPTY_LIST_STRING = "list:{}";

    // ===================================================================================
    //                                                                    Property Handler
    //                                                                    ================
    public DfPropertiesHandler getHandler() {
        return DfPropertiesHandler.getInstance();
    }

    // ===================================================================================
    //                                                                     Property Object
    //                                                                     ===============
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    public DfBasicProperties getBasicProperties() {
        return getHandler().getBasicProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                      S2Dao Adjustment
    //                                      ----------------
    public DfS2DaoAdjustmentProperties getS2DaoAdjustmentProperties() {
        return getHandler().getS2DaoAdjustmentProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                         DBFlute Dicon
    //                                         -------------
    public DfDBFluteDiconProperties getDBFluteDiconProperties() {
        return getHandler().getDBFluteDiconProperties(getProperties());
    }

    // -----------------------------------------------------
    //                               Generated Class Package
    //                               -----------------------
    public DfGeneratedClassPackageProperties getGeneratedClassPackageProperties() {
        return getHandler().getGeneratedClassPackageProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                 Sequence and Identity
    //                                 ---------------------
    public DfSequenceIdentityProperties getSequenceIdentityProperties() {
        return getHandler().getSequenceIdentityProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                       Optimistic Lock
    //                                       ---------------
    public DfOptimisticLockProperties getOptimisticLockProperties() {
        return getHandler().getOptimisticLockProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                         Common Column
    //                                         -------------
    public DfCommonColumnProperties getCommonColumnProperties() {
        return getHandler().getCommonColumnProperties(getProperties());
    }
    
    // -----------------------------------------------------
    //                                       Behavior Filter
    //                                       ---------------
    public DfBehaviorFilterProperties getBehaviorFilterProperties() {
        return getHandler().getBehaviorFilterProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                        Classification
    //                                        --------------
    protected DfClassificationProperties _classificationProperties;

    public DfClassificationProperties getClassificationProperties() {
        if (_classificationProperties == null) {
            _classificationProperties = new DfClassificationProperties(_buildProperties);
        }
        return _classificationProperties;
    }

    // -----------------------------------------------------
    //                                          Select Param
    //                                          ------------
    public DfSelectParamProperties getSelectParamProperties() {
        return getHandler().getSelectParamProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                Additional Foreign-Key
    //                                ----------------------
    public DfAdditionalForeignKeyProperties getAdditionalForeignKeyProperties() {
        return getHandler().getAdditionalForeignKeyProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                         Making Option
    //                                         -------------
    public DfSourceReductionProperties getSourceReductionProperties() {
        return getHandler().getSourceReductionProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                         Include Query
    //                                         -------------
    public DfIncludeQueryProperties getIncludeQueryProperties() {
        return getHandler().getIncludeQueryProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                     Little Adjustment
    //                                     -----------------
    public DfLittleAdjustmentProperties getLittleAdjustmentProperties() {
        return getHandler().getLittleAdjustmentProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                                 Other
    //                                                 -----
    public DfOtherProperties getOtherProperties() {
        return getHandler().getOtherProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                          Table Except
    //                                          ------------
    protected List<String> _tableExceptList;

    public List<String> getTableExceptList() {
        if (_tableExceptList == null) {
            final List<Object> tmpLs = listProp("torque.table.except.list", DEFAULT_EMPTY_LIST);
            _tableExceptList = new ArrayList<String>();
            for (Object object : tmpLs) {
                _tableExceptList.add((String) object);
            }
            _tableExceptList.addAll(getTableExceptInformation().getTableExceptList());
        }
        return _tableExceptList;
    }

    protected TableExceptInformation _tableExceptInformation;

    public TableExceptInformation getTableExceptInformation() {
        if (_tableExceptInformation == null) {
            if ("mssql".equals(getBasicProperties().getDatabaseName())) {
                _tableExceptInformation = new TableExceptSQLServer();
            } else {
                _tableExceptInformation = new TableExceptDefault();
            }
        }
        return _tableExceptInformation;
    }

    public static interface TableExceptInformation {
        public List<String> getTableExceptList();
    }

    public static class TableExceptSQLServer implements TableExceptInformation {
        public List<String> getTableExceptList() {
            return Arrays.asList(new String[] { "sysconstraints", "syssegments", "dtproperties" });
        }
    }

    public static class TableExceptDefault implements TableExceptInformation {
        public List<String> getTableExceptList() {
            return Arrays.asList(new String[] {});
        }
    }

    public DfTypeMappingProperties getTypeMappingProperties() {
        return getHandler().getTypeMappingProperties(getProperties());
    }

    public DfRefreshProperties getRefreshProperties() {
        return getHandler().getRefreshProperties(getProperties());
    }
    
    public DfSimpleDtoProperties getSimpleDtoProperties() {
        return getHandler().getSimpleDtoProperties(getProperties());
    }

    public DfFlexDtoProperties getFlexDtoProperties() {
        return getHandler().getFlexDtoProperties(getProperties());
    }

    public DfS2jdbcProperties getS2jdbcProperties() {
        return getHandler().getS2JdbcProperties(getProperties());
    }

    // -----------------------------------------------------
    //         ToLowerInGeneratorUnderscoreMethod (Internal)
    //         ---------------------------------------------
    public boolean isAvailableToLowerInGeneratorUnderscoreMethod() {
        return booleanProp("torque.isAvailableToLowerInGeneratorUnderscoreMethod", true);
    }

    // -----------------------------------------------------
    //                            ReplaceSchemaDefinitionMap
    //                            --------------------------
    public DfReplaceSchemaProperties getReplaceSchemaProperties() {
        return getHandler().getReplaceSchemaProperties(getProperties());
    }

    // -----------------------------------------------------
    //                       invokeSqlDirectoryDefinitionMap
    //                       -------------------------------
    public DfInvokeSqlDirectoryProperties getInvokeSqlDirectoryProperties() {
        return getHandler().getInvokeSqlDirectoryProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                  outsideSqlProperties
    //                                  --------------------
    public DfOutsideSqlProperties getOutsideSqlProperties() {
        return getHandler().getOutsideSqlProperties(getProperties());
    }
}