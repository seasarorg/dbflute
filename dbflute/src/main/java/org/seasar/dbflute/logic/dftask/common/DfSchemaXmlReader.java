package org.seasar.dbflute.logic.dftask.common;

import org.apache.tools.ant.Project;
import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.transform.XmlToAppData;

public class DfSchemaXmlReader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _schemaXml;
    protected final Project _project;
    protected final String _targetDatabase;
    protected AppData _schemaData;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSchemaXmlReader(String schemaXml, Project project, String targetDatabase) {
        _schemaXml = schemaXml;
        _project = project;
        _targetDatabase = targetDatabase;
    }

    // ===================================================================================
    //                                                                                Read
    //                                                                                ====
    public void read() {
        if (_schemaXml == null) {
            String msg = "The property 'schemaXml' should not be null!";
            throw new IllegalStateException(msg);
        }
        _schemaData = newInstanceXmlToAppData().parseFile(_schemaXml);
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

    protected XmlToAppData newInstanceXmlToAppData() {
        return new XmlToAppData(_targetDatabase);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public AppData getSchemaData() {
        return _schemaData;
    }
}
