package org.seasar.dbflute.properties;

import java.util.Properties;

/**
 * Build properties for Torque.
 * 
 * @author jflute
 */
public final class DfOtherProperties extends DfAbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public DfOtherProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                                                   Stop Generate
    //                                                                   =============
    public boolean isStopGenerateExtendedBhv() {
        return booleanProp("torque.isStopGenerateExtendedBhv", false);
    }

    public boolean isStopGenerateExtendedDao() {
        return booleanProp("torque.isStopGenerateExtendedDao", false);
    }

    public boolean isStopGenerateExtendedEntity() {
        return booleanProp("torque.isStopGenerateExtendedEntity", false);
    }

    // ===============================================================================
    //                                                                  Extract Accept
    //                                                                  ==============
    public String getExtractAcceptStartBrace() {
        return stringProp("torque.extractAcceptStartBrace", "@{");
    }

    public String getExtractAcceptEndBrace() {
        return stringProp("torque.extractAcceptEndBrace", "@}");
    }

    public String getExtractAcceptDelimiter() {
        return stringProp("torque.extractAcceptDelimiter", "@;");
    }

    public String getExtractAcceptEqual() {
        return stringProp("torque.extractAcceptEqual", "@=");
    }
}