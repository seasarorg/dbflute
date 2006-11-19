package org.apache.torque.task;

import java.io.*;
import java.util.*;
import org.apache.tools.ant.BuildException;
import org.apache.torque.engine.EngineException;
import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.transform.XmlToAppData;
import org.apache.velocity.context.Context;
import org.seasar.dbflute.task.bs.DfAbstractDbMetaTexenTask;

/**
 * An extended Texen task used for ???
 *
 * @author Modified by mkubo
 */
public class TorqueSQLTask extends DfAbstractDbMetaTexenTask {
    private String _database;
    private String _suffix;
    private String _idTableXMLFile;

    public TorqueSQLTask() {
        _suffix = "";
        _idTableXMLFile = null;
    }

    public void setDatabase(String database) {
        this._database = database;
    }

    public String getDatabase() {
        return _database;
    }

    public void setSuffix(String suffix) {
        this._suffix = suffix;
    }

    public String getSuffix() {
        return _suffix;
    }

    public void setIdTableXMLFile(String idXmlFile) {
        _idTableXMLFile = idXmlFile;
    }

    public String getIdTableXMLFile() {
        return _idTableXMLFile;
    }

    public Context initControlContext() throws Exception {
        super.initControlContext();
        try {
            createSqlDbMap();
            String f = getIdTableXMLFile();
            if (f != null && f.length() > 0)
                loadIdBrokerModel();
        } catch (EngineException ee) {
            throw new BuildException(ee);
        }
        return _context;
    }

    private void createSqlDbMap() throws Exception {
        if (getSqlDbMap() == null)
            return;
        Properties sqldbmap = new Properties();
        File file = new File(getSqlDbMap());
        if (file.exists()) {
            final FileInputStream fis = new FileInputStream(file);
            sqldbmap.load(fis);
            fis.close();
        }
        String sqlFile;
        String databaseName;
        for (Iterator i = getDataModelDbMap().keySet().iterator(); i.hasNext(); sqldbmap.setProperty(sqlFile,
                databaseName)) {
            String dataModelName = (String) i.next();
            sqlFile = dataModelName + _suffix + ".sql";
            if (getDatabase() == null)
                databaseName = (String) getDataModelDbMap().get(dataModelName);
            else
                databaseName = getDatabase();
        }

        sqldbmap.store(new FileOutputStream(getSqlDbMap()), "Sqlfile -> Database map");
    }

    public void loadIdBrokerModel() throws EngineException {
        XmlToAppData xmlParser = new XmlToAppData(getTargetDatabase(), null, getBasePathToDbProps());
        AppData ad = xmlParser.parseFile(getIdTableXMLFile());
        ad.setName("idmodel");
        _context.put("idmodel", ad);
    }
}
