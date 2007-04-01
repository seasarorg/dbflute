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
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.texen.Generator;
import org.apache.velocity.texen.ant.TexenTask;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.connection.DfDataSourceCreator;
import org.seasar.dbflute.helper.jdbc.connection.DfSimpleDataSourceCreator;
import org.seasar.dbflute.helper.jdbc.context.DfDataSourceContext;
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

    // =========================================================================================
    //                                                                                 Attribute
    //                                                                                 =========
    protected String _targetDatabase;

    /** DB driver. */
    protected String _driver = null;

    /** DB url. */
    protected String _url = null;

    /** User name. */
    protected String _userId = null;

    /** Password */
    protected String _password = null;

    protected DfDataSourceCreator _dataSourceCreator = new DfSimpleDataSourceCreator();

    // =========================================================================================
    //                                                                                      Main
    //                                                                                      ====
    // -----------------------------------
    //                             Execute
    //                             -------
    @Override
    final public void execute() {
        try {
            if (isUseDataSource()) {
                setupDataSource();
            }
            doExecute();
            if (isUseDataSource()) {
                closingDataSource();
            }
        } catch (RuntimeException e) {
            _log.error("execute() threw the exception!", e);
            throw e;
        }
    }

    abstract protected void doExecute();

    protected void fireSuperExecute() {
        // /----------------------------------------------
        // Set up the encoding of templates from property.
        // -----/
        setInputEncoding(getBasicProperties().getTemplateFileEncoding());
        setOutputEncoding(getBasicProperties().getSourceFileEncoding());

        try {
            doExecuteAlmostSameAsSuper();
        } catch (Exception e) {
            _log.info("/ * * * * * * * * * * * * * * * * * * * * * * * * * * *");
            _log.error("super#execute() threw the exception!", e);
            _log.info("/ * * * * * * * * * /");
        }
    }

    // -----------------------------------
    //                  Copy from Velocity
    //                  ------------------
    // Copy from super.execute() and Modify a little.
    private void doExecuteAlmostSameAsSuper() {
        if (templatePath == null && !useClasspath) {
            throw new IllegalStateException(
                    "The template path needs to be defined if you are not using the classpath for locating templates!");
        }
        if (controlTemplate == null) {
            throw new IllegalStateException("The control template needs to be defined!");
        }
        if (outputDirectory == null) {
            throw new IllegalStateException("The output directory needs to be defined!");
        }
        if (outputFile == null) {
            throw new IllegalStateException("The output file needs to be defined!");
        }
        try {
            if (templatePath != null) {
                log("Using templatePath: " + templatePath, 3);
                Velocity.setProperty("file.resource.loader.path", templatePath);
            }
            if (useClasspath) {
                log("Using classpath");
                Velocity.addProperty("resource.loader", "classpath");
                Velocity.setProperty("classpath.resource.loader.class",
                        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
                Velocity.setProperty("classpath.resource.loader.cache", "false");
                Velocity.setProperty("classpath.resource.loader.modificationCheckInterval", "2");
            }
            Velocity.init();
            final Generator generator = Generator.getInstance();
            generator.setOutputPath(outputDirectory);
            generator.setInputEncoding(inputEncoding);
            generator.setOutputEncoding(outputEncoding);
            if (templatePath != null) {
                generator.setTemplatePath(templatePath);
            }
            
            // - - - - - - - - - - - - - - - - - - - - 
            // Remove writing output file of velocity.
            // - - - - - - - - - - - - - - - - - - - - 
            // final File file = new File(outputDirectory);
            // if (!file.exists()) {
            //     file.mkdirs();
            // }
            // String path = outputDirectory + File.separator + outputFile;
            // log("Generating to file " + path, 2);
            // Writer writer = generator.getWriter(path, outputEncoding);

            Context c = initControlContext();
            populateInitialContext(c);
            if (contextProperties != null) {
                for (Iterator i = contextProperties.getKeys(); i.hasNext();) {
                    String property = (String) i.next();
                    String value = contextProperties.getString(property);
                    try {
                        c.put(property, new Integer(value));
                    } catch (NumberFormatException nfe) {
                        String booleanString = contextProperties.testBoolean(value);
                        if (booleanString != null) {
                            c.put(property, new Boolean(booleanString));
                        } else {
                            if (property.endsWith("file.contents")) {
                                value = fileContentsToString(super.project.resolveFile(value).getCanonicalPath());
                                property = property.substring(0, property.indexOf("file.contents") - 1);
                            }
                            c.put(property, value);
                        }
                    }
                }
            }
            generator.parse(controlTemplate, c);
            
            // - - - - - - - - - - - - - - - - - - - - 
            // Remove writing output file of velocity.
            // - - - - - - - - - - - - - - - - - - - - 
            // final String parsedString = generator.parse(controlTemplate, c);
            // writer.write(parsedString);
            // writer.flush();
            // writer.close();

            generator.shutdown();
            cleanup();
        } catch (BuildException e) {
            throw e;
        } catch (MethodInvocationException e) {
            throw new IllegalStateException("Exception thrown by '" + e.getReferenceName() + "." + e.getMethodName()
                    + "'" + ". For more information consult the velocity log, or invoke ant with the -debug flag.", e
                    .getWrappedThrowable());
        } catch (ParseErrorException e) {
            throw new IllegalStateException(
                    "Velocity syntax error. For more information consult the velocity log, or invoke ant with the -debug flag.",
                    e);
        } catch (ResourceNotFoundException e) {
            throw new IllegalStateException(
                    "Resource not found. For more information consult the velocity log, or invoke ant with the -debug flag.",
                    e);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Generation failed. For more information consult the velocity log, or invoke ant with the -debug flag.",
                    e);
        }
    }

    // Copy from velocity.
    private static String fileContentsToString(String file) {
        String contents = "";
        File f = new File(file);
        if (f.exists())
            try {
                FileReader fr = new FileReader(f);
                char template[] = new char[(int) f.length()];
                fr.read(template);
                contents = new String(template);
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        return contents;
    }

    // -----------------------------------
    //                         Data Source
    //                         -----------
    abstract protected boolean isUseDataSource();

    protected void setupDataSource() {
        _dataSourceCreator.setUserId(_userId);
        _dataSourceCreator.setPassword(_password);
        _dataSourceCreator.setDriver(_driver);
        _dataSourceCreator.setUrl(_url);
        _dataSourceCreator.setAutoCommit(true);
        _dataSourceCreator.create();
    }

    protected void closingDataSource() {
        _dataSourceCreator.commit();
        _dataSourceCreator.destroy();
    }

    protected DataSource getDataSource() {
        return DfDataSourceContext.getDataSource();
    }

    // -----------------------------------
    //                  Context Properties
    //                  ------------------
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

    // =========================================================================================
    //                                                                                  Accessor
    //                                                                                  ========
    /**
     * Set the JDBC driver to be used.
     *
     * @param driver driver class name
     */
    public void setDriver(String driver) {
        this._driver = driver;
    }

    /**
     * Set the DB connection url.
     *
     * @param url connection url
     */
    public void setUrl(String url) {
        this._url = url;
    }

    /**
     * Set the user name for the DB connection.
     *
     * @param userId database user
     */
    public void setUserId(String userId) {
        this._userId = userId;
    }

    /**
     * Set the password for the DB connection.
     *
     * @param password database password
     */
    public void setPassword(String password) {
        this._password = password;
    }

    public String getTargetDatabase() {
        return _targetDatabase;
    }

    public void setTargetDatabase(String v) {
        _targetDatabase = v;
    }

    // =========================================================================================
    //                                                                                Properties
    //                                                                                ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }

    // =========================================================================================
    //                                                                                    Helper
    //                                                                                    ======
    protected String getLineSeparator() {
        return System.getProperty("line.separator");
    }
}