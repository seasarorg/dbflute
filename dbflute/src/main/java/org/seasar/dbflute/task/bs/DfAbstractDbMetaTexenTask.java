/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.task.bs;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.torque.engine.EngineException;
import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.transform.XmlToAppData;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfS2jdbcProperties;

/**
 * @author jflute
 */
public abstract class DfAbstractDbMetaTexenTask extends DfAbstractTexenTask {

    /** Log instance. */
    public static final Log _log = LogFactory.getLog(DfAbstractDbMetaTexenTask.class);

    protected String _xmlFile;

    protected List<FileSet> _filesets;

    protected List<AppData> _dataModels;

    protected Context _context;

    protected Hashtable<String, String> _dataModelDbMap;

    protected Hashtable<String, String> _databaseNames;

    protected String _sqldbmap;

    private String _basePathToDbProps;

    private String _targetPackage;

    public DfAbstractDbMetaTexenTask() {
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
        _context.put("targetDatabase", getTargetDatabase());
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

    @Override
    protected void doExecute() {
        fireSuperExecute();
    }
}