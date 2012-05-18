package org.seasar.dbflute.properties.assistant.freegen;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.logic.generate.packagepath.DfPackagePathHandler;
import org.seasar.dbflute.properties.DfBasicProperties;

/**
 * @author jflute
 */
public class DfFreeGenRequest {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DfFreeGenManager _manager;
    protected final String _requestName;
    protected final DfFreeGenResource _resource;
    protected final DfFreeGenOutput _output;
    protected DfFreeGenTable _table;
    protected DfPackagePathHandler _packagePathHandler;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfFreeGenRequest(DfFreeGenManager manager, String requestName, DfFreeGenResource resource,
            DfFreeGenOutput output) {
        _manager = manager;
        _requestName = requestName;
        _resource = resource;
        _output = output;
    }

    // ===================================================================================
    //                                                                        ResourceType
    //                                                                        ============
    public enum DfFreeGenerateResourceType {
        PROP, XLS, SOLR
    }

    public boolean isResourceTypeProp() {
        return _resource.isResourceTypeProp();
    }

    public boolean isResourceTypeXls() {
        return _resource.isResourceTypeXls();
    }

    public boolean isResourceTypeSolr() {
        return _resource.isResourceTypeSolr();
    }

    // ===================================================================================
    //                                                                                Path
    //                                                                                ====
    public void enableOutputDirectory() {
        _manager.setOutputDirectory(_output.getOutputDirectory());
    }

    public String getGenerateDirPath() {
        return getPackageAsPath(_output.getPackage());
    }

    public String getGenerateFilePath() {
        final DfBasicProperties basicProp = DfBuildProperties.getInstance().getBasicProperties();
        final String classExt = basicProp.getLanguageDependencyInfo().getGrammarInfo().getClassFileExtension();
        return getGenerateDirPath() + "/" + _output.getClassName() + "." + classExt;
    }

    protected String getPackageAsPath(String pkg) {
        return _packagePathHandler.getPackageAsPath(pkg);
    }

    public String getTemplatePath() {
        return _output.getTemplateFile();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{" + _requestName + ", " + _resource + ", " + _output + ", " + _table + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getRequestName() {
        return _requestName;
    }

    public DfFreeGenResource getResource() {
        return _resource;
    }

    public DfFreeGenerateResourceType getResourceType() {
        return _resource.getResourceType();
    }

    public String getResourceFile() {
        return _resource.getResourceFile();
    }

    public DfFreeGenOutput getOutput() {
        return _output;
    }

    public String getTemplateFile() {
        return _output.getTemplateFile();
    }

    public String getOutputDirectory() {
        return _output.getOutputDirectory();
    }

    public String getPackage() {
        return _output.getPackage();
    }

    public String getClassName() {
        return _output.getClassName();
    }

    public Map<String, Object> getTableMap() {
        return _table.getTableMap();
    }

    public String getTableName() {
        return _table.getTableName();
    }

    public List<Map<String, Object>> getColumnList() {
        return _table.getColumnList();
    }

    public void setTable(DfFreeGenTable _table) {
        this._table = _table;
    }

    public void setPackagePathHandler(DfPackagePathHandler packagePathHandler) {
        this._packagePathHandler = packagePathHandler;
    }
}
