package org.seasar.dbflute.properties;

import java.util.Properties;

import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 */
public final class DfAllClassCopyrightProperties extends DfAbstractHelperProperties {

    protected String _copyright;

    public DfAllClassCopyrightProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                           Copyright
    //                                                                           =========
    public String getAllClassCopyright() {
        if (_copyright != null) {
            return _copyright;
        }
        String prop = stringProp("torque.allClassCopyright", "");

        final String sourceCodeLineSeparator = getBasicProperties().getSourceCodeLineSeparator();
        prop = DfStringUtil.replace(prop, "\r\n", "\n");
        prop = DfStringUtil.replace(prop, "\n", sourceCodeLineSeparator);

        _copyright = prop;
        return _copyright;
    }
}