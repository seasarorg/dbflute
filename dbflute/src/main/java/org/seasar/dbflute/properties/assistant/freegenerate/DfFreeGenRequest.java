package org.seasar.dbflute.properties.assistant.freegenerate;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.logic.generate.packagepath.DfPackagePathHandler;
import org.seasar.dbflute.properties.DfBasicProperties;

public class DfFreeGenRequest {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DfFreeGenManager _manager;
    protected final String _requestName;
    protected final DfFreeGenerateResourceType _resourceType;
    protected final String _resourceFile;
    protected String _templateFile;
    protected String _outputDirectory;
    protected String _package;
    protected String _className;
    protected Map<String, Object> _tableMap;
    protected List<Map<String, String>> _attributeList;
    protected DfPackagePathHandler _packagePathHandler;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfFreeGenRequest(DfFreeGenManager manager, String requestName, DfFreeGenerateResourceType resourceType,
            String resourceFile) {
        _manager = manager;
        _requestName = requestName;
        _resourceType = resourceType;
        _resourceFile = resourceFile;
    }

    // ===================================================================================
    //                                                                        ResourceType
    //                                                                        ============
    public enum DfFreeGenerateResourceType {
        XLS
    }

    // ===================================================================================
    //                                                                                Path
    //                                                                                ====
    public void enableOutputDirectory() {
        _manager.setOutputDirectory(_outputDirectory);
    }

    public String getOutputPath() {
        final String packageAsPath = getPackageAsPath(_package);
        final DfBasicProperties basicProp = DfBuildProperties.getInstance().getBasicProperties();
        final String classExt = basicProp.getLanguageDependencyInfo().getGrammarInfo().getClassFileExtension();
        return packageAsPath + "/" + _className + "." + classExt;
    }

    public String getTemplatePath() {
        return _templateFile;
    }

    public String getPackageAsPath(String pkg) {
        return _packagePathHandler.getPackageAsPath(pkg);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final String attrExp = (_tableMap != null ? _tableMap.keySet().toString() : "");
        return "{" + _requestName + ", " + _resourceType + ", " + attrExp + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getRequestName() {
        return _requestName;
    }

    public String getResourceFile() {
        return _resourceFile;
    }

    public DfFreeGenerateResourceType getResourceType() {
        return _resourceType;
    }

    public String getTemplateFile() {
        return _templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this._templateFile = templateFile;
    }

    public String getOutputDirectory() {
        return _outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this._outputDirectory = outputDirectory;
    }

    public String getPackage() {
        return _package;
    }

    public void setPackage(String pkg) {
        this._package = pkg;
    }

    public String getClassName() {
        return _className;
    }

    public void setClassName(String className) {
        this._className = className;
    }

    public List<Map<String, String>> getAttributeList() {
        return _attributeList;
    }

    public void setAttributeList(List<Map<String, String>> attributeList) {
        this._attributeList = attributeList;
    }

    public Map<String, Object> getTableMap() {
        return _tableMap;
    }

    public void setTableMap(Map<String, Object> tableMap) {
        _tableMap = tableMap;
    }

    public DfPackagePathHandler getPackagePathHandler() {
        return _packagePathHandler;
    }

    public void setPackagePathHandler(DfPackagePathHandler packagePathHandler) {
        this._packagePathHandler = packagePathHandler;
    }
}
