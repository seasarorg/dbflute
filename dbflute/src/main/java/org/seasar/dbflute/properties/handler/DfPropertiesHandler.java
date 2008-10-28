package org.seasar.dbflute.properties.handler;

import java.util.Properties;

import org.seasar.dbflute.properties.DfAdditionalForeignKeyProperties;
import org.seasar.dbflute.properties.DfAdditionalPrimaryKeyProperties;
import org.seasar.dbflute.properties.DfAdditionalTableProperties;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfBehaviorFilterProperties;
import org.seasar.dbflute.properties.DfBuriProperties;
import org.seasar.dbflute.properties.DfCommonColumnProperties;
import org.seasar.dbflute.properties.DfDBFluteDiconProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
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
import org.seasar.dbflute.properties.DfSqlLogRegistryProperties;
import org.seasar.dbflute.properties.DfTypeMappingProperties;

/**
 * Build properties for Torque.
 * 
 * @author jflute
 */
public final class DfPropertiesHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final DfPropertiesHandler _insntace = new DfPropertiesHandler();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfPropertiesHandler() {
    }

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    public static DfPropertiesHandler getInstance() {
        return _insntace;
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    protected DfBasicProperties _basicProperties;

    public DfBasicProperties getBasicProperties(Properties prop) {
        if (_basicProperties == null) {
            _basicProperties = new DfBasicProperties(prop);
        }
        return _basicProperties;
    }

    // -----------------------------------------------------
    //                                      S2Dao Adjustment
    //                                      ----------------
    protected DfS2DaoAdjustmentProperties _s2daoAdjustmentPropertiess;

    public DfS2DaoAdjustmentProperties getS2DaoAdjustmentProperties(Properties prop) {
        if (_s2daoAdjustmentPropertiess == null) {
            _s2daoAdjustmentPropertiess = new DfS2DaoAdjustmentProperties(prop);
        }
        return _s2daoAdjustmentPropertiess;
    }

    // -----------------------------------------------------
    //                                         DBFlute Dicon
    //                                         -------------
    protected DfDBFluteDiconProperties _dbfluteDiconProperties;

    public DfDBFluteDiconProperties getDBFluteDiconProperties(Properties prop) {
        if (_dbfluteDiconProperties == null) {
            _dbfluteDiconProperties = new DfDBFluteDiconProperties(prop);
        }
        return _dbfluteDiconProperties;
    }

    // -----------------------------------------------------
    //                               Generated Class Package
    //                               -----------------------
    protected DfGeneratedClassPackageProperties _generatedClassPackageProperties;

    public DfGeneratedClassPackageProperties getGeneratedClassPackageProperties(Properties prop) {
        if (_generatedClassPackageProperties == null) {
            _generatedClassPackageProperties = new DfGeneratedClassPackageProperties(prop);
        }
        return _generatedClassPackageProperties;
    }

    // -----------------------------------------------------
    //                                         Common Column
    //                                         -------------
    protected DfCommonColumnProperties _commonColumnProperties;

    public DfCommonColumnProperties getCommonColumnProperties(Properties prop) {
        if (_commonColumnProperties == null) {
            _commonColumnProperties = new DfCommonColumnProperties(prop);
        }
        return _commonColumnProperties;
    }

    // -----------------------------------------------------
    //                                       Behavior Filter
    //                                       ---------------
    protected DfBehaviorFilterProperties _behaviorFilterProperties;

    public DfBehaviorFilterProperties getBehaviorFilterProperties(Properties prop) {
        if (_behaviorFilterProperties == null) {
            _behaviorFilterProperties = new DfBehaviorFilterProperties(prop);
        }
        return _behaviorFilterProperties;
    }

    // -----------------------------------------------------
    //                                                  Buri
    //                                                  ----
    protected DfBuriProperties _buriProperties;

    public DfBuriProperties getBuriProperties(Properties prop) {
        if (_buriProperties == null) {
            _buriProperties = new DfBuriProperties(prop);
        }
        return _buriProperties;
    }

    // -----------------------------------------------------
    //                                       Optimistic Lock
    //                                       ---------------
    protected DfOptimisticLockProperties _optimisticLockProperties;

    public DfOptimisticLockProperties getOptimisticLockProperties(Properties prop) {
        if (_optimisticLockProperties == null) {
            _optimisticLockProperties = new DfOptimisticLockProperties(prop);
        }
        return _optimisticLockProperties;
    }

    // -----------------------------------------------------
    //                                          Select Param
    //                                          ------------
    protected DfSelectParamProperties _selectParamProperties;

    public DfSelectParamProperties getSelectParamProperties(Properties prop) {
        if (_selectParamProperties == null) {
            _selectParamProperties = new DfSelectParamProperties(prop);
        }
        return _selectParamProperties;
    }

    // -----------------------------------------------------
    //                                     Sequence Identity
    //                                     -----------------
    protected DfSequenceIdentityProperties _sequenceIdentityProperties;

    public DfSequenceIdentityProperties getSequenceIdentityProperties(Properties prop) {
        if (_sequenceIdentityProperties == null) {
            _sequenceIdentityProperties = new DfSequenceIdentityProperties(prop);
        }
        return _sequenceIdentityProperties;
    }

    // -----------------------------------------------------
    //                                Additional Primary Key
    //                                ----------------------
    protected DfAdditionalTableProperties _additionalTableProperties;

    public DfAdditionalTableProperties getAdditionalTableProperties(Properties prop) {
        if (_additionalTableProperties == null) {
            _additionalTableProperties = new DfAdditionalTableProperties(prop);
        }
        return _additionalTableProperties;
    }

    // -----------------------------------------------------
    //                                Additional Primary Key
    //                                ----------------------
    protected DfAdditionalPrimaryKeyProperties _additionalPrimaryKeyProperties;

    public DfAdditionalPrimaryKeyProperties getAdditionalPrimaryKeyProperties(Properties prop) {
        if (_additionalPrimaryKeyProperties == null) {
            _additionalPrimaryKeyProperties = new DfAdditionalPrimaryKeyProperties(prop);
        }
        return _additionalPrimaryKeyProperties;
    }

    // -----------------------------------------------------
    //                                Additional Foreign Key
    //                                ----------------------
    protected DfAdditionalForeignKeyProperties _additionalForeignKeyProperties;

    public DfAdditionalForeignKeyProperties getAdditionalForeignKeyProperties(Properties prop) {
        if (_additionalForeignKeyProperties == null) {
            _additionalForeignKeyProperties = new DfAdditionalForeignKeyProperties(prop);
        }
        return _additionalForeignKeyProperties;
    }

    // -----------------------------------------------------
    //                                     Little Adjustment
    //                                     -----------------
    protected DfLittleAdjustmentProperties _littleAdjustmentPropertiess;

    public DfLittleAdjustmentProperties getLittleAdjustmentProperties(Properties prop) {
        if (_littleAdjustmentPropertiess == null) {
            _littleAdjustmentPropertiess = new DfLittleAdjustmentProperties(prop);
        }
        return _littleAdjustmentPropertiess;
    }

    // -----------------------------------------------------
    //                                      Other Adjustment
    //                                      ----------------
    protected DfOtherProperties _otherProperties;

    public DfOtherProperties getOtherProperties(Properties prop) {
        if (_otherProperties == null) {
            _otherProperties = new DfOtherProperties(prop);
        }
        return _otherProperties;
    }

    // -----------------------------------------------------
    //                                      Source Reduction
    //                                      ----------------
    protected DfSourceReductionProperties _sourceReductionProperties;

    public DfSourceReductionProperties getSourceReductionProperties(Properties prop) {
        if (_sourceReductionProperties == null) {
            _sourceReductionProperties = new DfSourceReductionProperties(prop);
        }
        return _sourceReductionProperties;
    }

    // -----------------------------------------------------
    //                                         Include Query
    //                                         -------------
    protected DfIncludeQueryProperties _includeQueryProperties;

    public DfIncludeQueryProperties getIncludeQueryProperties(Properties prop) {
        if (_includeQueryProperties == null) {
            _includeQueryProperties = new DfIncludeQueryProperties(prop);
        }
        return _includeQueryProperties;
    }

    // -----------------------------------------------------
    //                                      Sql Log Registry
    //                                      ----------------
    protected DfSqlLogRegistryProperties _sqlLogRegistryProperties;

    public DfSqlLogRegistryProperties getSqlLogRegistryProperties(Properties prop) {
        if (_sqlLogRegistryProperties == null) {
            _sqlLogRegistryProperties = new DfSqlLogRegistryProperties(prop);
        }
        return _sqlLogRegistryProperties;
    }

    // -----------------------------------------------------
    //                                            OutsideSql
    //                                            ----------
    protected DfOutsideSqlProperties _outsideSqlProperties;

    public DfOutsideSqlProperties getOutsideSqlProperties(Properties prop) {
        if (_outsideSqlProperties == null) {
            _outsideSqlProperties = new DfOutsideSqlProperties(prop);
        }
        return _outsideSqlProperties;
    }

    // -----------------------------------------------------
    //                                              Document
    //                                              --------
    protected DfDocumentProperties _documentProperties;

    public DfDocumentProperties getDocumentProperties(Properties prop) {
        if (_documentProperties == null) {
            _documentProperties = new DfDocumentProperties(prop);
        }
        return _documentProperties;
    }

    // -----------------------------------------------------
    //                                         ReplaceSchema
    //                                         -------------
    protected DfReplaceSchemaProperties _replaceSchemaPropertiess;

    public DfReplaceSchemaProperties getReplaceSchemaProperties(Properties prop) {
        if (_replaceSchemaPropertiess == null) {
            _replaceSchemaPropertiess = new DfReplaceSchemaProperties(prop);
        }
        return _replaceSchemaPropertiess;
    }

    // -----------------------------------------------------
    //                                  Invoke Sql Directory
    //                                  --------------------
    protected DfInvokeSqlDirectoryProperties _invokeSqlDirectoryProperties;

    public DfInvokeSqlDirectoryProperties getInvokeSqlDirectoryProperties(Properties prop) {
        if (_invokeSqlDirectoryProperties == null) {
            _invokeSqlDirectoryProperties = new DfInvokeSqlDirectoryProperties(prop);
        }
        return _invokeSqlDirectoryProperties;
    }

    // -----------------------------------------------------
    //                                          Type Mapping
    //                                          ------------
    protected DfTypeMappingProperties _typeMappingProperties;

    public DfTypeMappingProperties getTypeMappingProperties(Properties prop) {
        if (_typeMappingProperties == null) {
            _typeMappingProperties = new DfTypeMappingProperties(prop);
        }
        return _typeMappingProperties;
    }

    // -----------------------------------------------------
    //                                               Refresh
    //                                               -------
    protected DfRefreshProperties _refreshProperties;

    public DfRefreshProperties getRefreshProperties(Properties prop) {
        if (_refreshProperties == null) {
            _refreshProperties = new DfRefreshProperties(prop);
        }
        return _refreshProperties;
    }

    // -----------------------------------------------------
    //                                            Simple DTO
    //                                            ----------
    protected DfSimpleDtoProperties _simpleDtoProperties;

    public DfSimpleDtoProperties getSimpleDtoProperties(Properties prop) {
        if (_simpleDtoProperties == null) {
            _simpleDtoProperties = new DfSimpleDtoProperties(prop);
        }
        return _simpleDtoProperties;
    }

    // -----------------------------------------------------
    //                                              Flex DTO
    //                                              --------
    protected DfFlexDtoProperties _flexDtoProperties;

    public DfFlexDtoProperties getFlexDtoProperties(Properties prop) {
        if (_flexDtoProperties == null) {
            _flexDtoProperties = new DfFlexDtoProperties(prop);
        }
        return _flexDtoProperties;
    }

    // -----------------------------------------------------
    //                                         S2JDBC Entity
    //                                         -------------
    protected DfS2jdbcProperties _s2jdbcProperties;

    public DfS2jdbcProperties getS2JdbcProperties(Properties prop) {
        if (_s2jdbcProperties == null) {
            _s2jdbcProperties = new DfS2jdbcProperties(prop);
        }
        return _s2jdbcProperties;
    }
}