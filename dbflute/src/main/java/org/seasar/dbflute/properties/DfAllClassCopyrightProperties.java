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

        // All line separator should be CR + LF
        // because Source Code uses CR + LF. (2009/04/28)
        prop = DfStringUtil.replace(prop, "\r\n", "\n");
        prop = DfStringUtil.replace(prop, "\n", "\r\n");
        _copyright = prop;
        return _copyright;
    }
}