package org.seasar.dbflute.task.replaceschema;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializer;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerDB2;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerJdbc;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerMySQL;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerOracle;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerSqlServer;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunner;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;

public class DfCreateSchemaTask extends DfAbstractReplaceSchemaTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfCreateSchemaTask.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean validTaskEndInformation = true;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        _log.info("");
        _log.info("{Replace Schema Properties}");
        _log.info("autoCommit        = " + getMyProperties().isAutoCommit());
        _log.info("rollbackOnly      = " + getMyProperties().isRollbackOnly());
        _log.info("errorContinue     = " + getMyProperties().isErrorContinue());
        _log.info("sqlFileEncoding   = " + getMyProperties().getSqlFileEncoding());
        initializeSchema();

        final DfRunnerInformation runInfo = createRunnerInformation();
        createSchema(runInfo);
    }

    @Override
    protected boolean isValidTaskEndInformation() {
        return validTaskEndInformation;
    }

    // --------------------------------------------
    //                            Initialize Schema
    //                            -----------------
    protected void initializeSchema() {
        _log.info("");
        _log.info("* * * * * * * * * * *");
        _log.info("*                   *");
        _log.info("* Initialize Schema *");
        _log.info("*                   *");
        _log.info("* * * * * * * * * * *");
        final DfSchemaInitializer initializer = createSchemaInitializer(false);
        if (initializer != null) {
            initializer.initializeSchema();
        }
        _log.info("");
        initializeSchemaOnceMore();
    }

    protected void initializeSchemaOnceMore() {
        final String schema = getMyProperties().getOnceMoreDropDefinitionSchema();
        if (schema == null || schema.trim().length() == 0) {
            return;
        }
        // /= = = = = = = = = = = = = = = = = 
        // Unsupported at MySQL and SQLServer
        // = = = = = = = = = =/
        if (getBasicProperties().isDatabaseMySQL() || getBasicProperties().isDatabaseSqlServer()) {
            String msg = "OnceMoreDropDefinitionSchema is unsupported at MySQL and SQLServer!";
            throw new UnsupportedOperationException(msg);
        }
        _log.info("* * * * * * * * * * * * * * * *");
        _log.info("*                             *");
        _log.info("* Initialize Schema Once More *");
        _log.info("*                             *");
        _log.info("* * * * * * * * * * * * * * * *");
        final DfSchemaInitializer initializer = createSchemaInitializer(true);
        if (initializer != null) {
            initializer.initializeSchema();
        }
        _log.info("");
    }

    protected DfSchemaInitializer createSchemaInitializer(boolean onceMore) {
        final DfSchemaInitializerFactory factory = createSchemaInitializerFactory(onceMore);
        return factory.createSchemaInitializer();
    }

    protected DfSchemaInitializerFactory createSchemaInitializerFactory(boolean onceMore) {
        return new DfSchemaInitializerFactory(getDataSource(), getBasicProperties(), getMyProperties(), onceMore);
    }

    protected static class DfSchemaInitializerFactory {
        protected DataSource _dataSource;
        protected DfBasicProperties _basicProperties;
        protected DfReplaceSchemaProperties _replaceSchemaProperties;
        protected boolean _onceMore;

        public DfSchemaInitializerFactory(DataSource dataSource, DfBasicProperties basicProperties,
                DfReplaceSchemaProperties replaceSchemaProperties, boolean onceMore) {
            _dataSource = dataSource;
            _basicProperties = basicProperties;
            _replaceSchemaProperties = replaceSchemaProperties;
            _onceMore = onceMore;
        }

        protected DfSchemaInitializer createSchemaInitializer() {
            final DfSchemaInitializer initializer;
            if (_basicProperties.isDatabaseMySQL()) {
                initializer = createSchemaInitializerMySQL();
            } else if (_basicProperties.isDatabaseSqlServer()) {
                initializer = createSchemaInitializerSqlServer();
            } else if (_basicProperties.isDatabaseOracle()) {
                initializer = createSchemaInitializerOracle();
            } else if (_basicProperties.isDatabaseDB2()) {
                initializer = createSchemaInitializerDB2();
            } else {
                initializer = createSchemaInitializerJdbc();
            }
            return initializer;
        }

        protected DfSchemaInitializer createSchemaInitializerMySQL() {
            final DfSchemaInitializerMySQL initializer = new DfSchemaInitializerMySQL();
            initializer.setDataSource(_dataSource);
            return initializer;
        }

        protected DfSchemaInitializer createSchemaInitializerSqlServer() {
            final DfSchemaInitializerSqlServer initializer = new DfSchemaInitializerSqlServer();
            initializer.setDataSource(_dataSource);
            return initializer;
        }

        protected DfSchemaInitializer createSchemaInitializerOracle() {
            final DfSchemaInitializerOracle initializer = new DfSchemaInitializerOracle();
            setupSchemaInitializerJdbcProperties(initializer);
            return initializer;
        }
        
        protected DfSchemaInitializer createSchemaInitializerDB2() {
            final DfSchemaInitializerDB2 initializer = new DfSchemaInitializerDB2();
            setupSchemaInitializerJdbcProperties(initializer);
            return initializer;
        }

        protected DfSchemaInitializer createSchemaInitializerJdbc() {
            final DfSchemaInitializerJdbc initializer = new DfSchemaInitializerJdbc();
            setupSchemaInitializerJdbcProperties(initializer);
            return initializer;
        }

        protected void setupSchemaInitializerJdbcProperties(DfSchemaInitializerJdbc initializer) {
            initializer.setDataSource(_dataSource);
            if (!_onceMore) {// Normal
                initializer.setSchema(_basicProperties.getDatabaseSchema());
                return;
            }
            final String schema = getOnceMoreSchema();
            if (schema == null || schema.trim().length() == 0) {
                String msg = "Once More Schema should not be null or empty: schema=" + schema;
                throw new IllegalStateException(msg);
            }
            final List<String> targetDatabaseTypeList = getOnceMoreTargetDatabaseTypeList();
            initializer.setSchema(schema);
            initializer.setTableNameWithSchema(true);
            initializer.setDropTargetDatabaseTypeList(targetDatabaseTypeList);
        }

        protected String getOnceMoreSchema() {
            return _replaceSchemaProperties.getOnceMoreDropDefinitionSchema();
        }

        protected List<String> getOnceMoreTargetDatabaseTypeList() {
            return _replaceSchemaProperties.getOnceMoreDropDefinitionTargetDatabaseTypeList();
        }
    }

    // --------------------------------------------
    //                                Create Schema
    //                                -------------
    protected DfRunnerInformation createRunnerInformation() {
        final DfRunnerInformation runInfo = new DfRunnerInformation();
        runInfo.setDriver(_driver);
        runInfo.setUrl(_url);
        runInfo.setUser(_userId);
        runInfo.setPassword(_password);
        runInfo.setEncoding(getReplaceSchemaSqlFileEncoding());
        runInfo.setAutoCommit(getMyProperties().isAutoCommit());
        runInfo.setErrorContinue(getMyProperties().isErrorContinue());
        runInfo.setRollbackOnly(getMyProperties().isRollbackOnly());
        return runInfo;
    }

    protected String getReplaceSchemaSqlFileEncoding() {
        return getMyProperties().getSqlFileEncoding();
    }

    protected void createSchema(DfRunnerInformation runInfo) {
        if (_log.isInfoEnabled()) {
            _log.info("* * * * * * * * *");
            _log.info("*               *");
            _log.info("* Create Schema *");
            _log.info("*               *");
            _log.info("* * * * * * * * *");
        }
        final DfSqlFileFireMan fireMan = new DfSqlFileFireMan();
        fireMan.execute(getSqlFileRunner(runInfo), getReplaceSchemaSqlFileList());
        if (_log.isInfoEnabled()) {
            _log.info("");
        }
    }

    protected DfSqlFileRunner getSqlFileRunner(final DfRunnerInformation runInfo) {
        return new DfSqlFileRunnerExecute(runInfo, getDataSource()) {
            @Override
            protected boolean isSqlTrimAndRemoveLineSeparator() {
                return true;
            }

            @Override
            protected String getTerminater4Tool() {
                return resolveTerminater4Tool();
            }
        };
    }

    protected List<File> getReplaceSchemaSqlFileList() {
        final List<File> fileList = new ArrayList<File>();
        fileList.addAll(getReplaceSchemaNextSqlFileList());
        return fileList;
    }

    protected List<File> getReplaceSchemaNextSqlFileList() {
        final String replaceSchemaSqlFileDirectoryName = getReplaceSchemaSqlFileDirectoryName();
        final File baseDir = new File(replaceSchemaSqlFileDirectoryName);
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.startsWith(getReplaceSchemaSqlFileNameWithoutExt())) {
                    if (name.endsWith("." + getReplaceSchemaSqlFileExt())) {
                        return true;
                    }
                }
                return false;
            }
        };

        // Order by FileName Asc
        final Comparator<File> fileNameAscComparator = new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        final TreeSet<File> treeSet = new TreeSet<File>(fileNameAscComparator);

        final String[] targetList = baseDir.list(filter);
        if (targetList == null) {
            return new ArrayList<File>();
        }
        for (String targetFileName : targetList) {
            final String targetFilePath = replaceSchemaSqlFileDirectoryName + "/" + targetFileName;
            treeSet.add(new File(targetFilePath));
        }
        return new ArrayList<File>(treeSet);
    }

    protected String getReplaceSchemaSqlFileDirectoryName() {
        final String sqlFileName = getMyProperties().getReplaceSchemaSqlFile();
        return sqlFileName.substring(0, sqlFileName.lastIndexOf("/"));
    }

    protected String getReplaceSchemaSqlFileNameWithoutExt() {
        final String sqlFileName = getMyProperties().getReplaceSchemaSqlFile();
        final String tmp = sqlFileName.substring(sqlFileName.lastIndexOf("/") + 1);
        return tmp.substring(0, tmp.lastIndexOf("."));
    }

    protected String getReplaceSchemaSqlFileExt() {
        final String sqlFileName = getMyProperties().getReplaceSchemaSqlFile();
        return sqlFileName.substring(sqlFileName.lastIndexOf(".") + 1);
    }

    protected DfReplaceSchemaProperties getMyProperties() {
        return DfBuildProperties.getInstance().getReplaceSchemaProperties();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setValidTaskEndInformation(String validTaskEndInformation) {
        this.validTaskEndInformation = validTaskEndInformation != null
                && validTaskEndInformation.trim().equalsIgnoreCase("true");
        ;
    }
}
