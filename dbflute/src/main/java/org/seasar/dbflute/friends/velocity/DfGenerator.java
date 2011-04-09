package org.seasar.dbflute.friends.velocity;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.context.Context;
import org.seasar.dbflute.DfBuildProperties;

/**
 * @author jflute
 * @since 0.7.6 (2008/07/01 Tuesday)
 */
public abstract class DfGenerator {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    public static final Log _log = LogFactory.getLog(DfGenerator.class);

    /** The implementation instance of generator. (Singleton) */
    private static volatile DfGenerator _instance;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfGenerator() {
    }

    // ===================================================================================
    //                                                                  Singleton Instance
    //                                                                  ==================
    public static DfGenerator getInstance() {
        if (_instance != null) {
            return _instance;
        }
        synchronized (DfGenerator.class) {
            if (_instance != null) {
                return _instance;
            }
            if (isSkipGenerateIfSameFile()) {
                _instance = DfFlutistGenerator.getInstance();
            } else {
                _instance = new DfVelocityGenerator();
            }
        }
        return _instance;
    }

    protected static boolean isSkipGenerateIfSameFile() {
        return DfBuildProperties.getInstance().getLittleAdjustmentProperties().isSkipGenerateIfSameFile();
    }

    // ===================================================================================
    //                                                                  Generator's Method
    //                                                                  ==================
    public abstract String getOutputPath();

    public abstract void setOutputPath(String outputPath);

    public abstract void setInputEncoding(String inputEncoding);

    public abstract void setOutputEncoding(String outputEncoding);

    public abstract void setTemplatePath(String templatePath);

    public abstract String parse(String inputTemplate, String outputFile, String objectID, Object object)
            throws Exception;

    public abstract String parse(String controlTemplate, Context controlContext) throws Exception;

    public abstract void shutdown();

    // ===================================================================================
    //                                                                    Skip Information
    //                                                                    ================
    public abstract List<String> getParseFileNameList();

    public abstract List<String> getSkipFileNameList();

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return getInstance().toString();
    }
}
