package org.seasar.dbflute.logic.jdbc.schemaxml;

import java.io.IOException;

import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.transform.XmlToAppData;
import org.apache.torque.engine.database.transform.XmlToAppData.XmlReadingTableFilter;
import org.seasar.dbflute.DfBuildProperties;

public class DfSchemaXmlReader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _schemaXml;
    protected final String _databaseType;
    protected final XmlReadingTableFilter _tableFilter;
    protected AppData _schemaData; // not null after reading

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param schemaXml The path of SchemaXML file relative to DBFlute client. (NotNull)
     * @param databaseType The type of database for the application.
     * @param tableFilter The filter of table by name when reading XML. (NullAllowed)
     */
    protected DfSchemaXmlReader(String schemaXml, String databaseType, XmlReadingTableFilter tableFilter) {
        _schemaXml = schemaXml;
        _databaseType = databaseType;
        _tableFilter = tableFilter;
    }

    // ===================================================================================
    //                                                                             Factory
    //                                                                             =======
    /**
     * @param databaseType The type of database for the application.
     * @param tableFilter The filter of table by name when reading XML. (NullAllowed)
     * @return The instance of this. (NotNull)
     */
    public static DfSchemaXmlReader createAsMain(String databaseType, XmlReadingTableFilter tableFilter) {
        final String schemaXml = DfBuildProperties.getInstance().getBasicProperties().getProejctSchemaXMLFilePath();
        return new DfSchemaXmlReader(schemaXml, databaseType, tableFilter);
    }

    /**
     * @param schemaXml The path of SchemaXML file relative to DBFlute client. (NotNull)
     * @param databaseType The type of database for the application.
     * @param tableFilter The filter of table by name when reading XML. (NullAllowed)
     * @return The instance of this. (NotNull)
     */
    public static DfSchemaXmlReader createAsPlain(String schemaXml, String databaseType,
            XmlReadingTableFilter tableFilter) {
        return new DfSchemaXmlReader(schemaXml, databaseType, tableFilter);
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
            final int dotLastIndex = xmlFile.lastIndexOf('.');
            if (fileSeparatorLastIndex < dotLastIndex) { // mainly (removing extension)
                // ./schema/project-schema-exampledb.xml to project-schema-exampledb
                name = xmlFile.substring(fileSeparatorLastIndex, dotLastIndex);
            } else {
                name = xmlFile.substring(fileSeparatorLastIndex);
            }
        } else {
            name = "schema"; // as default
        }
        return name;
    }

    protected XmlToAppData createXmlToAppData() {
        return new XmlToAppData(_databaseType, _tableFilter);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getSchemaXml() {
        return _schemaXml;
    }

    public AppData getSchemaData() {
        return _schemaData;
    }
}
