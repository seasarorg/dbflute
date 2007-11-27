package org.seasar.dbflute.helper.jdbc.sqlfile;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class DfSqlFileGetter {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected FileFilter _sqlFileFileter;
    
    protected FileFilter _directoryOnlyFilter;

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public List<File> getSqlFileList(String sqlDirectory) {
        final List<File> fileList = new ArrayList<File>();
        {
            final File file = new File(sqlDirectory);
            if (!file.exists()) {
                String msg = "The sqlDirectory does not exist: " + file;
                throw new IllegalStateException(msg);
            }
            if (!file.isDirectory()) {
                String msg = "The sqlDirectory should be directory. but file...: " + file;
                throw new IllegalStateException(msg);
            }
            registerFile(fileList, file);
        }
        return fileList;
    }

    protected void registerFile(List<File> fileList, File file) {
        final FileFilter sqlFileFileter;
        if (_sqlFileFileter != null) {
            sqlFileFileter = _sqlFileFileter;
        } else {
            sqlFileFileter = createDefaultSqlFileFileFilter();
        }
        final File[] sqlFiles = file.listFiles(sqlFileFileter);
        final FileFilter directoryOnlyFilter;
        if (_sqlFileFileter != null) {
            directoryOnlyFilter = _directoryOnlyFilter;
        } else {
            directoryOnlyFilter = createDefaultDirectoryOnlyFileFilter();
        }
        final File[] directories = file.listFiles(directoryOnlyFilter);
        for (final File sqlFile : sqlFiles) {
            fileList.add(sqlFile);
        }
        for (final File dir : directories) {
            registerFile(fileList, dir);
        }
    }

    // ===================================================================================
    //                                                                  Default FileFilter
    //                                                                  ==================
    protected FileFilter createDefaultSqlFileFileFilter() {
        return new FileFilter() {
            public boolean accept(File file) {
                return acceptSqlFile(file);
            }
        };
    }
    
    protected boolean acceptSqlFile(File file) {
        return file.getName().toLowerCase().endsWith(".sql");
    }

    protected FileFilter createDefaultDirectoryOnlyFileFilter() {
        return new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public FileFilter getSqlFileFileter() {
        return _sqlFileFileter;
    }

    public void setSqlFileFileter(FileFilter sqlFileFileter) {
        this._sqlFileFileter = sqlFileFileter;
    }
}
