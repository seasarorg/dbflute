package org.seasar.dbflute.properties.assistant.freegenerate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.texen.util.FileUtil;
import org.seasar.dbflute.friends.velocity.DfGenerator;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfFreeGenManager {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfFreeGenManager.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfFreeGenManager() {
    }

    // ===================================================================================
    //                                                                           Generator
    //                                                                           =========
    public DfGenerator getGeneratorInstance() {
        return DfGenerator.getInstance();
    }

    // ===================================================================================
    //                                                                           Directory
    //                                                                           =========
    public void setOutputDirectory(String outputDirectory) {
        getGeneratorInstance().setOutputPath(outputDirectory);
    }

    public void makeDirectory(String filePath) {
        final String basePath = Srl.substringLastFront(filePath, "/");
        FileUtil.mkdir(getGeneratorInstance().getOutputPath() + "/" + basePath);
    }

    // ===================================================================================
    //                                                                             Logging
    //                                                                             =======
    public void info(String msg) {
        _log.info(msg);
    }
}
