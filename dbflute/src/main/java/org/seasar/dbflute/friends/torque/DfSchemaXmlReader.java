package org.seasar.dbflute.friends.torque;

import java.util.Hashtable;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.torque.engine.EngineException;
import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.transform.XmlToAppData;

public class DfSchemaXmlReader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _schemaXml;
    protected final Project _project;
    protected final String _targetDatabase;
    protected AppData _schemaData;
    protected final Map<String, String> _databaseNames = new Hashtable<String, String>();

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
    public void read() throws Exception {
        if (_schemaXml == null) {
            throw new BuildException("You must specify schemaXml property!");
        }
        try {
            _schemaData = newInstanceXmlToAppData().parseFile(_schemaXml);
            _schemaData.setName(grokName(_schemaXml));
            final Database database = _schemaData.getDatabase();
            _databaseNames.put(database.getName(), database.getName());
        } catch (EngineException e) {
            throw new BuildException(e);
        }
    }

    protected String grokName(String xmlFile) {
        final String name;
        int fileSeparatorLastIndex = xmlFile.lastIndexOf(System.getProperty("file.separator"));
        if (fileSeparatorLastIndex != -1) {
            fileSeparatorLastIndex++;
            final int commaLastIndex = xmlFile.lastIndexOf('.');
            if (fileSeparatorLastIndex < commaLastIndex) {
                name = xmlFile.substring(fileSeparatorLastIndex, commaLastIndex);
            } else {
                name = xmlFile.substring(fileSeparatorLastIndex);
            }
        } else {
            name = "data-model"; // Default-name
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

    public Map<String, String> getDatabaseNames() {
        return _databaseNames;
    }
}
