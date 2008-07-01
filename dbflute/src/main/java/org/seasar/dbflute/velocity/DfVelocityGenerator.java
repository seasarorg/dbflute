package org.seasar.dbflute.velocity;

import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.context.Context;
import org.apache.velocity.texen.Generator;

/**
 * @author jflute
 * @since 0.7.6 (2008/07/01 Tuesday)
 */
public class DfVelocityGenerator extends DfGenerator {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private Generator _generator = Generator.getInstance();

    /**
     * The list of file name parsed. {DBFlute Original Attribute}
     */
    protected List<String> parseFileNameList = new ArrayList<String>();// [Extension]

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfVelocityGenerator() {
    }

    // ===================================================================================
    //                                                                  Generator's Method
    //                                                                  ==================
    public String getOutputPath() {
        return _generator.getOutputPath();
    }

    public void setOutputPath(String outputPath) {
        _generator.setOutputPath(outputPath);
    }

    public void setInputEncoding(String inputEncoding) {
        _generator.setInputEncoding(inputEncoding);
    }

    public void setOutputEncoding(String outputEncoding) {
        _generator.setOutputEncoding(outputEncoding);
    }

    public void setTemplatePath(String templatePath) {
        _generator.setTemplatePath(templatePath);
    }
    
    public String parse(String inputTemplate, String outputFile, String objectID, Object object) throws Exception {
        parseFileNameList.add(outputFile);
        return _generator.parse(inputTemplate, outputFile, objectID, object);
    }

    public String parse(String inputTemplate, Context controlContext) throws Exception {
        return _generator.parse(inputTemplate, controlContext);
    }

    public void shutdown() {
        _generator.shutdown();
    }

    // ===================================================================================
    //                                                                    Skip Information
    //                                                                    ================
    public List<String> getParseFileNameList() {
        return parseFileNameList;
    }

    public List<String> getSkipFileNameList() {
        return new ArrayList<String>();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return _generator.toString();
    }
}
