package org.seasar.dbflute.properties.facade;

import org.seasar.dbflute.properties.DfBasicProperties;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/29 Friday)
 */
public class DfSchemaXmlFacadeProp {

    protected final DfBasicProperties _basicProp;

    public DfSchemaXmlFacadeProp(DfBasicProperties basicProp) {
        _basicProp = basicProp;
    }

    public String getProejctSchemaXMLEncoding() {
        return _basicProp.getProejctSchemaXMLEncoding();
    }

    public String getProejctSchemaXMLFile() {
        return _basicProp.getProejctSchemaXMLFile();
    }

    public String getProjectSchemaHistoryFile() {
        return _basicProp.getProjectSchemaHistoryFile();
    }
}
