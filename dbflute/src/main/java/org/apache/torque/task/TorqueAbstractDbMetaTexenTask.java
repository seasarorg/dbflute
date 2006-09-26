package org.apache.torque.task;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.torque.engine.EngineException;
import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.transform.XmlToAppData;
import org.apache.torque.helper.TorqueBuildProperties;
import org.apache.torque.helper.TorqueTaskUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.texen.ant.TexenTask;

/**
 * Abstract DB meta texen task for Torque.
 * 
 * @author mkubo
 */
public abstract class TorqueAbstractDbMetaTexenTask extends TexenTask {

    /** Log instance. */
    public static final Log _log = LogFactory.getLog(TorqueAbstractDbMetaTexenTask.class);

    protected String _xmlFile;

    protected List<FileSet> _filesets;

    protected List<AppData> _dataModels;

    protected Context _context;

    protected Hashtable<String, String> _dataModelDbMap;

    protected Hashtable<String, String> _databaseNames;

    protected String _sqldbmap;

    private String _basePathToDbProps;

    private String _targetDatabase;

    private String _targetPackage;

    public TorqueAbstractDbMetaTexenTask() {
        _filesets = new ArrayList<FileSet>();
        _dataModels = new ArrayList<AppData>();
    }

    public void setSqlDbMap(String sqldbmap) {
        this._sqldbmap = project.resolveFile(sqldbmap).toString();
    }

    public String getSqlDbMap() {
        return _sqldbmap;
    }

    public List getDataModels() {
        return _dataModels;
    }

    public Hashtable getDataModelDbMap() {
        return _dataModelDbMap;
    }

    public String getXmlFile() {
        return _xmlFile;
    }

    public void setXmlFile(String xmlFile) {
        this._xmlFile = project.resolveFile(xmlFile).toString();
    }

    public void addFileset(FileSet set) {
        _filesets.add(set);
    }

    public String getTargetDatabase() {
        return _targetDatabase;
    }

    public void setTargetDatabase(String v) {
        _targetDatabase = v;
    }

    public String getTargetPackage() {
        return _targetPackage;
    }

    public void setTargetPackage(String v) {
        _targetPackage = v;
    }

    public String getBasePathToDbProps() {
        return _basePathToDbProps;
    }

    public void setBasePathToDbProps(String v) {
        _basePathToDbProps = v;
    }

    public void setContextProperties(String file) {
        try {
            // /------------------------------------------------------------
            // Initialize internal context properties as ExtendedProperties.
            //   This property is used by Velocity Framework. 
            // -------/
            super.setContextProperties(file);
            {
                final Hashtable env = super.getProject().getProperties();
                for (final Iterator ite = env.keySet().iterator(); ite.hasNext();) {
                    final String key = (String) ite.next();
                    if (key.startsWith("torque.")) {
                        String newKey = key.substring("torque.".length());
                        for (int j = newKey.indexOf("."); j != -1; j = newKey.indexOf(".")) {
                            newKey = newKey.substring(0, j) + StringUtils.capitalise(newKey.substring(j + 1));
                        }
                        contextProperties.setProperty(newKey, (String) env.get(key));
                    }
                }
            }

            // /---------------------------------------------------------------------------------------------------
            // Initialize torque properties as Properties and set up singleton class that saves 'build.properties'.
            //   This property is used by You. 
            // -------/
            final Properties prop = TorqueTaskUtil.getBuildProperties(file, super.project);
            TorqueBuildProperties.getInstance().setContextProperties(prop);

        } catch (Exception e) {
            _log.warn("setContextProperties() threw the exception!!!", e);
        }
    }

    public Context initControlContext() throws Exception {
        if (_xmlFile == null && _filesets.isEmpty()) {
            throw new BuildException("You must specify an XML schema or fileset of XML schemas!");
        }
        try {
            if (_xmlFile != null) {
                final AppData appData = newInstanceXmlToAppData().parseFile(_xmlFile);
                appData.setName(grokName(_xmlFile));
                _dataModels.add(appData);
            } else {
                for (int i = 0; i < _filesets.size(); i++) {
                    final FileSet fs = (FileSet) _filesets.get(i);
                    final File srcDir = fs.getDir(project);
                    final DirectoryScanner directoryScanner = fs.getDirectoryScanner(project);
                    final String dataModelFiles[] = directoryScanner.getIncludedFiles();
                    for (int j = 0; j < dataModelFiles.length; j++) {
                        final File file = new File(srcDir, dataModelFiles[j]);
                        final AppData appData = newInstanceXmlToAppData().parseFile(file.toString());
                        appData.setName(grokName(file.toString()));
                        _dataModels.add(appData);
                    }
                }
            }
            _databaseNames = new Hashtable<String, String>();
            _dataModelDbMap = new Hashtable<String, String>();
            for (final Iterator ite = _dataModels.iterator(); ite.hasNext();) {
                final AppData appData = (AppData) ite.next();
                final Database database = appData.getDatabase();
                _dataModelDbMap.put(appData.getName(), database.getName());
                _databaseNames.put(database.getName(), database.getName());
            }

        } catch (EngineException e) {
            throw new BuildException(e);
        }

        // Initialize velocity-context.
        _context = new VelocityContext();
        _context.put("dataModels", _dataModels);
        _context.put("databaseNames", _databaseNames);
        _context.put("targetDatabase", _targetDatabase);
        _context.put("targetPackage", _targetPackage);
        return _context;
    }

    protected XmlToAppData newInstanceXmlToAppData() {
        return new XmlToAppData(getTargetDatabase(), getTargetPackage(), getBasePathToDbProps());
    }

    protected String grokName(String xmlFile) {
        String name = "data-model"; // Default-name
        int fileSeparatorLastIndex = xmlFile.lastIndexOf(System.getProperty("file.separator"));
        if (fileSeparatorLastIndex != -1) {
            fileSeparatorLastIndex++;
            final int commaLastIndex = xmlFile.lastIndexOf('.');
            if (fileSeparatorLastIndex < commaLastIndex)
                name = xmlFile.substring(fileSeparatorLastIndex, commaLastIndex);
            else
                name = xmlFile.substring(fileSeparatorLastIndex);
        }
        return name;
    }

    public void execute() throws BuildException {
        // /----------------------------------------------
        // Set up the encoding of templates from property.
        // -----/
        final String templateEncoding = TorqueBuildProperties.getInstance().getTemplateFileEncoding();
        Velocity.setProperty("input.encoding", templateEncoding);

        try {
            super.execute();
        } catch (Exception e) {
            _log.error("/ * * * * * * * * * * * * * * * * * * * * * * * * * * *");
            _log.error("super#execute() threw the exception!", e);
            _log.error("/ * * * * * * * * * /");
        }
    }

}