package org.seasar.dbflute.properties;

import java.util.Properties;

/**
 * Build properties for Torque.
 * 
 * @author mkubo
 */
public final class DfPropertiesHandler {

    public static final DfPropertiesHandler _insntace = new DfPropertiesHandler();

    public DfPropertiesHandler() {
    }

    public static DfPropertiesHandler getInstance() {
        return _insntace;
    }

    protected DfBasicProperties _basicProperties;

    public DfBasicProperties getBasicProperties(Properties prop) {
        if (_basicProperties == null) {
            _basicProperties = new DfBasicProperties(prop);
        }
        return _basicProperties;
    }

    protected DfDaoDiconProperties _daoDiconProperties;

    public DfDaoDiconProperties getDaoDiconProperties(Properties prop) {
        if (_daoDiconProperties == null) {
            _daoDiconProperties = new DfDaoDiconProperties(prop);
        }
        return _daoDiconProperties;
    }

    protected DfGeneratedClassPackageProperties _generatedClassPackageProperties;

    public DfGeneratedClassPackageProperties getGeneratedClassPackageProperties(Properties prop) {
        if (_generatedClassPackageProperties == null) {
            _generatedClassPackageProperties = new DfGeneratedClassPackageProperties(prop);
        }
        return _generatedClassPackageProperties;
    }

    protected DfOptimisticLockProperties _optimisticLockProperties;

    public DfOptimisticLockProperties getOptimisticLockProperties(Properties prop) {
        if (_optimisticLockProperties == null) {
            _optimisticLockProperties = new DfOptimisticLockProperties(prop);
        }
        return _optimisticLockProperties;
    }

    protected DfSelectParamProperties _selectParamProperties;

    public DfSelectParamProperties getSelectParamProperties(Properties prop) {
        if (_selectParamProperties == null) {
            _selectParamProperties = new DfSelectParamProperties(prop);
        }
        return _selectParamProperties;
    }

    protected DfAdditionalForeignKeyProperties _additionalForeignKeyProperties;

    public DfAdditionalForeignKeyProperties getAdditionalForeignKeyProperties(Properties prop) {
        if (_additionalForeignKeyProperties == null) {
            _additionalForeignKeyProperties = new DfAdditionalForeignKeyProperties(prop);
        }
        return _additionalForeignKeyProperties;
    }

    protected DfOtherProperties _otherProperties;

    public DfOtherProperties getOtherProperties(Properties prop) {
        if (_otherProperties == null) {
            _otherProperties = new DfOtherProperties(prop);
        }
        return _otherProperties;
    }
    
    protected DfSourceReductionProperties _sourceReductionProperties;

    public DfSourceReductionProperties getSourceReductionProperties(Properties prop) {
        if (_sourceReductionProperties == null) {
            _sourceReductionProperties = new DfSourceReductionProperties(prop);
        }
        return _sourceReductionProperties;
    }
    

    protected DfSql2EntityProperties _sql2EntityProperties;

    public DfSql2EntityProperties getSql2EntityProperties(Properties prop) {
        if (_sql2EntityProperties == null) {
            _sql2EntityProperties = new DfSql2EntityProperties(prop);
        }
        return _sql2EntityProperties;
    }

    protected DfReplaceSchemaProperties _replaceSchemaPropertiess;

    public DfReplaceSchemaProperties getReplaceSchemaProperties(Properties prop) {
        if (_replaceSchemaPropertiess == null) {
            _replaceSchemaPropertiess = new DfReplaceSchemaProperties(prop);
        }
        return _replaceSchemaPropertiess;
    }

    protected DfInvokeSqlDirectoryProperties _invokeSqlDirectoryProperties;

    public DfInvokeSqlDirectoryProperties getInvokeSqlDirectoryProperties(Properties prop) {
        if (_invokeSqlDirectoryProperties == null) {
            _invokeSqlDirectoryProperties = new DfInvokeSqlDirectoryProperties(prop);
        }
        return _invokeSqlDirectoryProperties;
    }
}