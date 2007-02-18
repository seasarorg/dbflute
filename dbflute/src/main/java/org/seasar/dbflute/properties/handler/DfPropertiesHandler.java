package org.seasar.dbflute.properties.handler;

import java.util.Properties;

import org.seasar.dbflute.properties.DfAdditionalForeignKeyProperties;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfCommonColumnProperties;
import org.seasar.dbflute.properties.DfDBFluteDiconProperties;
import org.seasar.dbflute.properties.DfGeneratedClassPackageProperties;
import org.seasar.dbflute.properties.DfIncludeQueryProperties;
import org.seasar.dbflute.properties.DfInvokeSqlDirectoryProperties;
import org.seasar.dbflute.properties.DfOptimisticLockProperties;
import org.seasar.dbflute.properties.DfOtherProperties;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.properties.DfSelectParamProperties;
import org.seasar.dbflute.properties.DfSourceReductionProperties;
import org.seasar.dbflute.properties.DfSql2EntityProperties;

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

    protected DfCommonColumnProperties _commonColumnProperties;

    public DfCommonColumnProperties getCommonColumnProperties(Properties prop) {
        if (_commonColumnProperties == null) {
            _commonColumnProperties = new DfCommonColumnProperties(prop);
        }
        return _commonColumnProperties;
    }

    protected DfDBFluteDiconProperties _dbfluteDiconProperties;

    public DfDBFluteDiconProperties getDBFluteDiconProperties(Properties prop) {
        if (_dbfluteDiconProperties == null) {
            _dbfluteDiconProperties = new DfDBFluteDiconProperties(prop);
        }
        return _dbfluteDiconProperties;
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

    protected DfIncludeQueryProperties _includeQueryProperties;

    public DfIncludeQueryProperties getIncludeQueryProperties(Properties prop) {
        if (_includeQueryProperties == null) {
            _includeQueryProperties = new DfIncludeQueryProperties(prop);
        }
        return _includeQueryProperties;
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