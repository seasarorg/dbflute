package org.apache.torque.task;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Properties;

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
