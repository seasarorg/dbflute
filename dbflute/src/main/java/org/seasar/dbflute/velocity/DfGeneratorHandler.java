package org.seasar.dbflute.velocity;

import org.apache.velocity.context.Context;

/**
 * @author jflute
 */
public class DfGeneratorHandler {

    private static final DfGeneratorHandler _instance = new DfGeneratorHandler();

    // private org.apache.velocity.texen.Generator _generator = org.apache.velocity.texen.Generator.getInstance();
    private DfGenerator _generator = DfGenerator.getInstance();

    private DfGeneratorHandler() {
    }

    public static DfGeneratorHandler getInstance() {
        return _instance;
    }

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

    public void parse(String controlTemplate, Context controlContext) throws Exception {
        _generator.parse(controlTemplate, controlContext);
    }

    public void shutdown() {
        _generator.shutdown();
    }
}
