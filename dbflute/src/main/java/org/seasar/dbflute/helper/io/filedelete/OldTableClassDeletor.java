package org.seasar.dbflute.helper.io.filedelete;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.velocity.texen.Generator;
import org.seasar.dbflute.util.DfStringUtil;

/**
 * 
 * @author jflute
 * @since 0.7.0 (2008/05/07 Wednesday)
 */
public class OldTableClassDeletor {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _packagePath;
    protected String _classPrefix;
    protected String _classSuffix;
    protected String _classExtension;
    protected Set<String> notDeleteClassNameSet;

    // ===================================================================================
    //                                                                              Delete
    //                                                                              ======
    public List<String> deleteOldTableClass() {
        final List<String> deletedClassNameList = new ArrayList<String>();
        final List<File> files = findPackageFileList(_packagePath, _classPrefix, _classSuffix);
        for (File file : files) {
            final String name = file.getName();
            final String nameWithoutExt = name.substring(0, name.lastIndexOf(_classExtension));
            if (notDeleteClassNameSet.contains(nameWithoutExt)) {
                continue;
            }
            deletedClassNameList.add(nameWithoutExt);
            file.delete();
        }
        return deletedClassNameList;
    }

    /**
     * @param packagePath The path of package. (NotNull)
     * @param classPrefix The prefix of classes. (NotNull)
     * @param classSuffix The suffix of classes. (Nullable)
     * @return The list of package files. (NotNull)
     */
    protected List<File> findPackageFileList(String packagePath, final String classPrefix, final String classSuffix) {
        final String dirPath = Generator.getInstance().getOutputPath() + "/"
                + DfStringUtil.replace(packagePath, ".", "/");
        final File dir = new File(dirPath);
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (!name.endsWith(_classExtension)) {
                    return false;
                }
                final String nameWithoutExt = name.substring(0, name.lastIndexOf(_classExtension));
                if (!nameWithoutExt.startsWith(classPrefix)) {
                    return false;
                }
                if (classPrefix != null && classPrefix.trim().length() > 0 && !nameWithoutExt.startsWith(classPrefix)) {
                    return false;
                }
                if (classSuffix != null && classSuffix.trim().length() > 0 && !nameWithoutExt.endsWith(classSuffix)) {
                    return false;
                }
                return false;
            }
        };
        return Arrays.asList(dir.listFiles(filter));
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getPackagePath() {
        return _packagePath;
    }

    public void setPackagePath(String packagePath) {
        _packagePath = packagePath;
    }

    protected String getClassPrefix() {
        return _classPrefix;
    }

    public void setClassPrefix(String classPrefix) {
        _classPrefix = classPrefix;
    }

    public String getClassSuffix() {
        return _classSuffix;
    }

    public void setClassSuffix(String classSuffix) {
        _classSuffix = classSuffix;
    }

    public String getClassExtension() {
        return _classExtension;
    }

    public void setClassExtension(String classExtension) {
        this._classExtension = classExtension;
    }

    public Set<String> getNotDeleteClassNameSet() {
        return notDeleteClassNameSet;
    }

    public void setNotDeleteClassNameSet(Set<String> notDeleteClassNameSet) {
        this.notDeleteClassNameSet = notDeleteClassNameSet;
    }
}
