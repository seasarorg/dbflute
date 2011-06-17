package org.seasar.dbflute.logic.replaceschema.process;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.util.FileUtils;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.8.5 (2011/06/17 Friday)
 */
public class DfArrangeBeforeRepsProcess extends DfAbstractReplaceSchemaProcess {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfArrangeBeforeRepsProcess.class);

    // ===================================================================================
    //                                                                             Process
    //                                                                             =======
    public void arrangeBeforeReps() {
        final DfReplaceSchemaProperties prop = getReplaceSchemaProperties();
        final Map<String, String> copyMap = prop.getArrangeBeforeRepsCopyMap();
        for (Entry<String, String> entry : copyMap.entrySet()) {
            final String src = entry.getKey();
            final String dest = entry.getValue();
            arrangeCopy(src, dest);
        }
    }

    // ===================================================================================
    //                                                                                Copy
    //                                                                                ====
    protected void arrangeCopy(String src, String dest) {
        final File destFile = new File(dest);
        if (destFile.exists() && destFile.isDirectory()) {
            throwRepsArrangeCopyDestDirectoryException(src, dest);
        }
        if (!src.contains("/")) {
            throwRepsArrangeCopySrcNotPathException(src, dest);
        }
        final String pureName = Srl.substringLastRear(src, "/");
        if (pureName.startsWith("*.")) {
            final String ext = Srl.substringFirstRear(pureName, "*.");
            final File baseDir = new File(Srl.substringLastFront(src, "/*."));
            if (!baseDir.exists()) {
                _log.info("*Not existing the copy src directory: " + baseDir.getPath());
                return;
            }
            final String extSuffix = "." + ext;
            final String[] elementList = baseDir.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return Srl.endsWith(name, extSuffix);
                }
            });
            if (elementList == null) { // no file in the directory
                _log.info("*Not found the file in the copy src directory: " + baseDir.getPath());
                return;
            }
            String targetElement = null;
            for (String element : elementList) {
                if (element.endsWith(extSuffix)) {
                    if (targetElement == null) {
                        targetElement = element; // found the only one file
                    } else { // duplicate
                        throwRepsArrangeCopySrcDuplicateFileException(src, dest, targetElement, element);
                    }
                }
            }
            if (targetElement != null) {
                copyFile(new File(baseDir.getPath() + "/" + targetElement), destFile);
            } else {
                _log.info("*Not found the corresponding copy src file: " + src);
            }
        } else {
            final File srcFile = new File(src);
            if (!srcFile.exists()) {
                _log.info("*Not existing the copy src file: " + src);
                return;
            }
            copyFile(srcFile, destFile);
        }
    }

    protected void copyFile(File src, File dest) {
        _log.info("...Copying " + src.getPath() + " to " + dest.getPath());
        if (dest.exists()) {
            dest.delete();
        }
        try {
            FileUtils.getFileUtils().copyFile(src, dest);
        } catch (IOException e) {
            String msg = "Failed to copy file: " + src + " to " + dest;
            throw new IllegalStateException(msg, e);
        }
    }

    protected void throwRepsArrangeCopyDestDirectoryException(String src, String dest) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The path in dest was directory.");
        br.addItem("Advice");
        br.addElement("The path in copy dest should be a file.");
        br.addItem("Source");
        br.addElement(src);
        br.addItem("Destination");
        br.addElement(dest);
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg);
    }

    protected void throwRepsArrangeCopySrcNotPathException(String src, String dest) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The path in src was not a path expression.");
        br.addItem("Advice");
        br.addElement("The path in src should be a path expression.");
        br.addElement("For example, './foo.txt' (should contain '/')");
        br.addItem("Source");
        br.addElement(src);
        br.addItem("Destination");
        br.addElement(dest);
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg);
    }

    protected void throwRepsArrangeCopySrcDuplicateFileException(String src, String dest, String first, String second) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The file corresponding to the extension was duplicate.");
        br.addItem("Advice");
        br.addElement("The file that has the extension should be the only one");
        br.addElement("when you use wild-card, e.g. '/*.sql', in copy src");
        br.addElement("and you specify a file in desc");
        br.addItem("Source");
        br.addElement(src);
        br.addItem("Destination");
        br.addElement(dest);
        br.addItem("Found Files");
        br.addElement(first);
        br.addElement(second);
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replaceString(String text, String fromText, String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }
}
