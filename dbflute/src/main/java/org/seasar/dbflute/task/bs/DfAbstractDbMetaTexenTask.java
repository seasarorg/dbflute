/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.types.FileSet;
import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.transform.XmlToAppData.XmlReadingTableFilter;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.seasar.dbflute.friends.velocity.DfVelocityContextFactory;
import org.seasar.dbflute.logic.jdbc.schemaxml.DfSchemaXmlReader;

/**
 * @author jflute
 */
public abstract class DfAbstractDbMetaTexenTask extends DfAbstractTexenTask {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final List<FileSet> _filesets = new ArrayList<FileSet>();
    protected AppData _schemaData;
    protected Context _context;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfAbstractDbMetaTexenTask() {
    }

    // ===================================================================================
    //                                                                 Initialize Override
    //                                                                 ===================
    @Override
    public Context initControlContext() throws Exception {
        final DfSchemaXmlReader schemaFileReader = createSchemaFileReader();
        schemaFileReader.read();
        _schemaData = schemaFileReader.getSchemaData();
        _context = createVelocityContext(_schemaData);
        return _context;
    }

    protected DfSchemaXmlReader createSchemaFileReader() {
        final String filePath = getBasicProperties().getProejctSchemaXMLFilePath();
        final String targetDatabase = getTargetDatabase();
        final XmlReadingTableFilter tableFilter = createXmlReadingTableFilter();
        return new DfSchemaXmlReader(filePath, targetDatabase, tableFilter);
    }

    protected abstract XmlReadingTableFilter createXmlReadingTableFilter();

    protected VelocityContext createVelocityContext(final AppData appData) {
        final DfVelocityContextFactory factory = new DfVelocityContextFactory();
        return factory.create(appData);
    }

    // ===================================================================================
    //                                                                    Execute Override
    //                                                                    ================
    @Override
    protected void doExecute() {
        fireVelocityProcess();
    }
}