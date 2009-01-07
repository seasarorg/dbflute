package org.seasar.dbflute.properties.handler;

import java.util.Properties;

import org.seasar.dbflute.properties.DfAdditionalForeignKeyProperties;
import org.seasar.dbflute.properties.DfAdditionalPrimaryKeyProperties;
import org.seasar.dbflute.properties.DfAdditionalTableProperties;
import org.seasar.dbflute.properties.DfAllClassCopyrightProperties;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfBehaviorFilterProperties;
import org.seasar.dbflute.properties.DfBuriProperties;
import org.seasar.dbflute.properties.DfCommonColumnProperties;
import org.seasar.dbflute.properties.DfDBFluteDiconProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.properties.DfFlexDtoProperties;
import org.seasar.dbflute.properties.DfIncludeQueryProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;
import org.seasar.dbflute.properties.DfMultipleFKPropertyProperties;
import org.seasar.dbflute.properties.DfOptimisticLockProperties;
import org.seasar.dbflute.properties.DfOutsideSqlProperties;
import org.seasar.dbflute.properties.DfRefreshProperties;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.properties.DfS2jdbcProperties;
import org.seasar.dbflute.properties.DfSequenceIdentityProperties;
import org.seasar.dbflute.properties.DfSimpleDtoProperties;
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
    //                                      Additional Table
    //                                      ----------------
    protected DfAdditionalTableProperties _additionalTableProperties;

    public DfAdditionalTableProperties getAdditionalTableProperties(Properties prop) {
        if (_additionalTableProperties == null) {
            _additionalTableProperties = new DfAdditionalTableProperties(prop);
        }
        return _additionalTableProperties;
    }

    // -----------------------------------------------------
    //                                   All Class Copyright
    //                                   -------------------
    protected DfAllClassCopyrightProperties _allClassCopyrightProperties;

    public DfAllClassCopyrightProperties getAllClassCopyrightProperties(Properties prop) {
        if (_allClassCopyrightProperties == null) {
            _allClassCopyrightProperties = new DfAllClassCopyrightProperties(prop);
        }
        return _allClassCopyrightProperties;
    }

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
    //                                              Database
    //                                              --------
    protected DfDatabaseProperties _databaseProperties;

    public DfDatabaseProperties getDatabaseProperties(Properties prop) {
        if (_databaseProperties == null) {
            _databaseProperties = new DfDatabaseProperties(prop);
        }
        return _databaseProperties;
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
    //                                  Multiple FK Property
    //                                  --------------------
    protected DfMultipleFKPropertyProperties _multipleFKPropertyProperties;

    public DfMultipleFKPropertyProperties getMultipleFKPropertyProperties(Properties prop) {
        if (_multipleFKPropertyProperties == null) {
            _multipleFKPropertyProperties = new DfMultipleFKPropertyProperties(prop);
        }
        return _multipleFKPropertyProperties;
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
    //                                         S2JDBC Entity
    //                                         -------------
    protected DfS2jdbcProperties _s2jdbcProperties;

    public DfS2jdbcProperties getS2JdbcProperties(Properties prop) {
        if (_s2jdbcProperties == null) {
            _s2jdbcProperties = new DfS2jdbcProperties(prop);
        }
        return _s2jdbcProperties;
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
    //                                      SQL Log Registry
    //                                      ----------------
    protected DfSqlLogRegistryProperties _sqlLogRegistryProperties;

    public DfSqlLogRegistryProperties getSqlLogRegistryProperties(Properties prop) {
        if (_sqlLogRegistryProperties == null) {
            _sqlLogRegistryProperties = new DfSqlLogRegistryProperties(prop);
        }
        return _sqlLogRegistryProperties;
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
}