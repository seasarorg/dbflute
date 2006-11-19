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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.texen.Generator;
import org.apache.velocity.texen.ant.TexenTask;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.torque.DfAntTaskUtil;

/**
 * Abstract DB meta texen task for Torque.
 * 
 * @author mkubo
 */
public abstract class DfAbstractTexenTask extends TexenTask {

    /** Log instance. */
    public static final Log _log = LogFactory.getLog(DfAbstractTexenTask.class);

    private String _targetDatabase;

    public String getTargetDatabase() {
        return _targetDatabase;
    }

    public void setTargetDatabase(String v) {
        _targetDatabase = v;
    }

    protected void fireSuperExecute() {
        // /----------------------------------------------
        // Set up the encoding of templates from property.
        // -----/
        setInputEncoding(getBasicProperties().getTemplateFileEncoding());
        setOutputEncoding(getBasicProperties().getSourceFileEncoding());

        try {
            super.execute();
        } catch (Exception e) {
            _log.info("/ * * * * * * * * * * * * * * * * * * * * * * * * * * *");
            _log.error("super#execute() threw the exception!", e);
            _log.info("/ * * * * * * * * * /");
        }
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
            final Properties prop = DfAntTaskUtil.getBuildProperties(file, super.project);
            DfBuildProperties.getInstance().setProperties(prop);

        } catch (Exception e) {
            _log.warn("setContextProperties() threw the exception!!!", e);
        }
    }

    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }
}