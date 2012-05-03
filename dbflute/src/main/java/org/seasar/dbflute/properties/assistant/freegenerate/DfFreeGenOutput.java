package org.seasar.dbflute.properties.assistant.freegenerate;

/**
 * @author jflute
 */
public class DfFreeGenOutput {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _templateFile;
    protected final String _outputDirectory;
    protected final String _package;
    protected final String _className;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfFreeGenOutput(String templateFile, String outputDirectory, String pkg, String className) {
        _templateFile = templateFile;
        _outputDirectory = outputDirectory;
        _package = pkg;
        _className = className;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{templateFile=" + _templateFile + ", outputDirectory=" + _outputDirectory + ", package=" + _package
                + ", className=" + _className + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getTemplateFile() {
        return _templateFile;
    }

    public String getOutputDirectory() {
        return _outputDirectory;
    }

    public String getPackage() {
        return _package;
    }

    public String getClassName() {
        return _className;
    }
}
