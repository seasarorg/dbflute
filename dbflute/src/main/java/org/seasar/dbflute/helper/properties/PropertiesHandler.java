package org.seasar.dbflute.helper.properties;

import java.util.Properties;

/**
 * Build properties for Torque.
 * 
 * @author mkubo
 */
public final class PropertiesHandler {

    public static final PropertiesHandler _insntace = new PropertiesHandler();

    public PropertiesHandler() {
    }

    public static PropertiesHandler getInstance() {
        return _insntace;
    }

    protected BasicProperties _basicProperties;

    public BasicProperties getBasicProperties(Properties prop) {
        if (_basicProperties == null) {
            _basicProperties = new BasicProperties(prop);
        }
        return _basicProperties;
    }

    protected DaoDiconProperties _daoDiconProperties;

    public DaoDiconProperties getDaoDiconProperties(Properties prop) {
        if (_daoDiconProperties == null) {
            _daoDiconProperties = new DaoDiconProperties(prop);
        }
        return _daoDiconProperties;
    }
    
    protected GeneratedClassPackageProperties _generatedClassPackageProperties;

    public GeneratedClassPackageProperties getGeneratedClassPackageProperties(Properties prop) {
        if (_generatedClassPackageProperties == null) {
            _generatedClassPackageProperties = new GeneratedClassPackageProperties(prop);
        }
        return _generatedClassPackageProperties;
    }
    
    protected OptimisticLockProperties _optimisticLockProperties;

    public OptimisticLockProperties getOptimisticLockProperties(Properties prop) {
        if (_optimisticLockProperties == null) {
            _optimisticLockProperties = new OptimisticLockProperties(prop);
        }
        return _optimisticLockProperties;
    }
    
    protected SelectParamProperties _selectParamProperties;

    public SelectParamProperties getSelectParamProperties(Properties prop) {
        if (_selectParamProperties == null) {
            _selectParamProperties = new SelectParamProperties(prop);
        }
        return _selectParamProperties;
    }
    
    protected AdditionalForeignKeyProperties _additionalForeignKeyProperties;

    public AdditionalForeignKeyProperties getAdditionalForeignKeyProperties(Properties prop) {
        if (_additionalForeignKeyProperties == null) {
            _additionalForeignKeyProperties = new AdditionalForeignKeyProperties(prop);
        }
        return _additionalForeignKeyProperties;
    }
    
    protected OtherProperties _otherProperties;

    public OtherProperties getOtherProperties(Properties prop) {
        if (_otherProperties == null) {
            _otherProperties = new OtherProperties(prop);
        }
        return _otherProperties;
    }
    
    protected Sql2EntityProperties _sql2EntityProperties;

    public Sql2EntityProperties getSql2EntityProperties(Properties prop) {
        if (_sql2EntityProperties == null) {
            _sql2EntityProperties = new Sql2EntityProperties(prop);
        }
        return _sql2EntityProperties;
    }
}