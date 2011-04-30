package org.seasar.dbflute.properties.assistant;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/30 Saturday)
 */
public class DfReplaceSchemaResourceFinder {

    protected final Set<String> _prefixSet = new LinkedHashSet<String>();
    protected final Set<String> _suffixSet = new LinkedHashSet<String>();
    protected boolean _oneLevelNested;

    public List<File> findResourceFileList(String directoryPath) {
        final File baseDir = new File(directoryPath);
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (matchName(name, _prefixSet, false) && matchName(name, _suffixSet, true)) {
                    return true;
                }
                return false;
            }
        };

        // order by FileName ascend
        final Comparator<File> fileNameAscComparator = new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        final TreeSet<File> treeSet = new TreeSet<File>(fileNameAscComparator);

        final List<File> resourceFileList;
        final String[] targetList = baseDir.list(filter);
        if (targetList != null) {
            for (String targetFileName : targetList) {
                final String targetFilePath = directoryPath + "/" + targetFileName;
                treeSet.add(new File(targetFilePath));
            }
            resourceFileList = new ArrayList<File>(treeSet);

            // searching one-level nested files
            if (_oneLevelNested) {
                final File[] listDirs = baseDir.listFiles(new FileFilter() {
                    public boolean accept(File file) {
                        return file.isDirectory();
                    }
                });
                if (listDirs != null) {
                    for (File dir : listDirs) {
                        final String nestedDir = directoryPath + "/" + dir.getName();
                        final List<File> nestedFileList = findResourceFileList(nestedDir);
                        resourceFileList.addAll(nestedFileList);
                    }
                }
            }
        } else {
            resourceFileList = DfCollectionUtil.emptyList();
        }
        return resourceFileList;
    }

    protected boolean matchName(String name, Set<String> set, boolean suffix) {
        if (set.isEmpty()) {
            return true;
        }
        for (String key : set) {
            if (suffix ? name.endsWith(key) : name.startsWith(key)) {
                return true;
            }
        }
        return false;
    }

    public void addPrefix(String prefix) {
        if (prefix != null) {
            _prefixSet.add(prefix);
        }
    }

    public void addSuffix(String suffix) {
        if (suffix != null) {
            _suffixSet.add(suffix);
        }
    }

    public void containsOneLevelNested() {
        _oneLevelNested = true;
    }
}
