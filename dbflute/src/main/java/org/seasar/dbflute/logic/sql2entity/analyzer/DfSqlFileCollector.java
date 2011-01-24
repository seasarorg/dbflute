package org.seasar.dbflute.logic.sql2entity.analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileGetter;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfoJava;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.task.DfSql2EntityTask;

/**
 * @author jflute
 * @since 0.7.9 (2008/08/29 Friday)
 */
public class DfSqlFileCollector {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSql2EntityTask.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _sqlDirectory;
    protected DfBasicProperties _basicProperties;
    protected boolean _suppressDirectoryCheck;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSqlFileCollector(String sqlDirectory, DfBasicProperties basicProperties) {
        _sqlDirectory = sqlDirectory;
        _basicProperties = basicProperties;
    }

    // ===================================================================================
    //                                                                             Collect
    //                                                                             =======
    public List<File> collectSqlFileList() {
        final String sqlDirectory = _sqlDirectory;
        final File dir = new File(sqlDirectory);
        final List<File> sqlFileList;
        if (dir.exists()) {
            sqlFileList = collectSqlFile(sqlDirectory);
            final String srcMainResources = replaceSrcMainJavaToSrcMainResources(sqlDirectory);
            if (!sqlDirectory.equals(srcMainResources)) {
                try {
                    sqlFileList.addAll(collectSqlFile(srcMainResources));
                } catch (Exception e) {
                    _log.info("Not found sql directory on resources: " + srcMainResources);
                }
            }
        } else {
            if (containsSrcMainJava(sqlDirectory)) {
                sqlFileList = new ArrayList<File>();
                final String srcMainResources = replaceSrcMainJavaToSrcMainResources(sqlDirectory);
                if (!sqlDirectory.equals(srcMainResources)) {
                    sqlFileList.addAll(collectSqlFile(srcMainResources));
                }
            } else {
                if (_suppressDirectoryCheck) {
                    return new ArrayList<File>();
                } else {
                    String msg = "The sqlDirectory does not exist: " + dir;
                    throw new IllegalStateException(msg);
                }
            }
        }
        return sqlFileList;
    }

    protected List<File> collectSqlFile(String sqlDirectory) {
        return createSqlFileGetter().getSqlFileList(sqlDirectory);
    }

    protected DfSqlFileGetter createSqlFileGetter() {
        final DfLanguageDependencyInfo dependencyInfo = _basicProperties.getLanguageDependencyInfo();
        return new DfSqlFileGetter() {
            @Override
            protected boolean acceptSqlFile(File file) {
                if (!dependencyInfo.isCompileTargetFile(file)) {
                    return false;
                }
                return super.acceptSqlFile(file);
            }
        };
    }

    protected boolean containsSrcMainJava(String sqlDirectory) {
        return DfLanguageDependencyInfoJava.containsSrcMainJava(sqlDirectory);
    }

    protected String replaceSrcMainJavaToSrcMainResources(String sqlDirectory) {
        return DfLanguageDependencyInfoJava.replaceSrcMainJavaToSrcMainResources(sqlDirectory);
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    public void suppressDirectoryCheck() {
        _suppressDirectoryCheck = true;
    }
}
