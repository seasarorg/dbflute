package org.seasar.dbflute.logic.jdbc.schemaxml;

import java.io.IOException;

import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.transform.XmlToAppData;
import org.apache.torque.engine.database.transform.XmlToAppData.XmlReadingTableFilter;

public class DfSchemaXmlReader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _schemaXml;
    protected final String _targetDatabase;
    protected final XmlReadingTableFilter _tableFilter;
    protected AppData _schemaData; // not null after reading

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSchemaXmlReader(String schemaXml, String targetDatabase, XmlReadingTableFilter tableFilter) {
        _schemaXml = schemaXml;
        _targetDatabase = targetDatabase;
        _tableFilter = tableFilter;
    }

    // ===================================================================================
    //                                                                                Read
    //                                                                                ====
    public void read() throws IOException {
        if (_schemaXml == null) {
            String msg = "The property 'schemaXml' should not be null!";
            throw new IllegalStateException(msg);
        }
        _schemaData = createXmlToAppData().parseFile(_schemaXml);
        _schemaData.setName(grokName(_schemaXml));
    }

    protected String grokName(String xmlFile) {
        final String name;
        int fileSeparatorLastIndex = xmlFile.lastIndexOf("/");
        if (fileSeparatorLastIndex > -1) { // basically true
            fileSeparatorLastIndex++;
            final int commaLastIndex = xmlFile.lastIndexOf('.');
            if (fileSeparatorLastIndex < commaLastIndex) {
                name = xmlFile.substring(fileSeparatorLastIndex, commaLastIndex);
            } else {
                name = xmlFile.substring(fileSeparatorLastIndex);
            }
        } else {
            name = "schema"; // as default
        }
        return name;
    }

    protected XmlToAppData createXmlToAppData() {
        return new XmlToAppData(_targetDatabase, _tableFilter);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public AppData getSchemaData() {
        return _schemaData;
    }
}
