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

import java.util.Properties;

import org.seasar.dbflute.properties.DfAdditionalForeignKeyProperties;
import org.seasar.dbflute.properties.DfAdditionalPrimaryKeyProperties;
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

/**
 * Build properties.
 * @author jflute
 */
public final class DfBuildProperties {

    /** Singleton-instance. */
    private static final DfBuildProperties _instance = new DfBuildProperties();

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
     * @return Build-properties. (NotNull)
     */
    final public Properties getProperties() {
        return _buildProperties;
    }

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
    //                                Additional Primary Key
    //                                ----------------------
    public DfAdditionalPrimaryKeyProperties getAdditionalPrimaryKeyProperties() {
        return getHandler().getAdditionalPrimaryKeyProperties(getProperties());
    }

    // -----------------------------------------------------
    //                                Additional Foreign Key
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