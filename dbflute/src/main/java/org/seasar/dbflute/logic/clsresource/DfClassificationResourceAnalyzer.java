package org.seasar.dbflute.logic.clsresource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.token.DfLineToken;
import org.seasar.dbflute.helper.token.DfLineTokenImpl;
import org.seasar.dbflute.helper.token.DfLineTokenizingOption;
import org.seasar.dbflute.properties.bean.DfClassificationElement;
import org.seasar.dbflute.properties.bean.DfClassificationTop;
import org.seasar.dbflute.util.DfNameHintUtil;

/**
 * @author jflute
 * @since 0.8.2 (2008/10/22 Wednesday)
 */
public class DfClassificationResourceAnalyzer {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfClassificationResourceAnalyzer.class);
    
    private static final String DEFAULT_ENCODING = "UTF-8";

    // ===================================================================================
    //                                                                             Analyze
    //                                                                             =======
    public List<DfClassificationTop> analyze(final String dirName, final String resourceName, final String extension) {
        final File dir = new File(dirName);
        if (!dir.exists()) {
            return new ArrayList<DfClassificationTop>();
        }
        if (!dir.isDirectory()) {
            return new ArrayList<DfClassificationTop>();
        }
        final File[] listFiles = dir.listFiles(new FileFilter() {
            public boolean accept(File currentFile) {
                if (currentFile.isDirectory()) {
                    return false;
                }
                final String currentFileName = currentFile.getName();
                if (!currentFileName.startsWith(resourceName)) {
                    return false;
                }
                if (!currentFileName.endsWith(extension)) {
                    return false;
                }
                return true;
            }
        });
        final List<DfClassificationTop> topList = new ArrayList<DfClassificationTop>();
        for (File file : listFiles) {
            final List<String> lineList;
            try {
                String encoding = extractEncoding(file);
                if (encoding == null) {
                    encoding = DEFAULT_ENCODING;
                }
                _log.info("...Analyzing classification resources: encoding=" + encoding);
                lineList = createLineList(file, encoding);
            } catch (RuntimeException ignored) {
                String msg = "Failed to analyze classification resources: ";
                msg = msg + " " + dirName + "/" + resourceName + "." + extension;
                _log.info(msg, ignored);
                continue;
            }
            final List<DfClassificationTop> classificationTopList = analyze(lineList);
            for (DfClassificationTop top : classificationTopList) {
                _log.info("    " + top.getClassificationName() + ", " + top.getTopComment());
            }
            topList.addAll(classificationTopList);
        }
        return topList;
    }

    protected List<DfClassificationTop> analyze(final List<String> lineList) {
        final List<DfClassificationTop> classificationList = new ArrayList<DfClassificationTop>();
        String relatedColumnName = null;
        boolean inGroup = false;
        final int size = lineList.size();
        int index = -1;
        for (String line : lineList) {
            ++index;
            if (inGroup) {
                if (isTopLine(line)) {
                    final DfClassificationTop classificationTop = extractClassificationTop(line);
                    classificationList.add(classificationTop);
                    if (relatedColumnName != null) {
                        classificationTop.setRelatedColumnName(relatedColumnName);
                    }
                    continue;
                } else if (isElementLine(line)) {
                    final DfClassificationElement classificationElement = extractClassificationElement(line);
                    final DfClassificationTop classificationTop = classificationList.get(classificationList.size() - 1);
                    classificationTop.addClassificationElement(classificationElement);
                    continue;
                } else {
                    inGroup = false;
                    continue;
                }
            }
            if (!isTitleLine(line)) {
                continue;
            }
            final int nextIndex = index + 1;
            if (nextIndex >= size) {
                break;
            }
            final String nextLine = lineList.get(nextIndex);
            if (!isTopLine(nextLine)) {
                continue;
            }
            final int nextNextIndex = nextIndex + 1;
            if (nextNextIndex >= size) {
                break;
            }
            final String nextNextLine = lineList.get(nextNextIndex);
            if (!isElementLine(nextNextLine)) {
                continue;
            }
            relatedColumnName = extractRelatedColumnNameFronTitleLine(line);
            inGroup = true;
        }
        return classificationList;
    }

    protected List<String> createLineList(File file, String encoding) {
        BufferedReader reader = null;
        final List<String> lineList = new ArrayList<String>();
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (containsLineSeparatorMark(line)) {
                    List<String> nestedLineList = tokenizedLineSeparatorMark(line);
                    lineList.addAll(nestedLineList);
                    continue;
                } else {
                    lineList.add(line);
                }
            }
            return lineList;
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                    ignored.printStackTrace();
                }
            }
        }
    }

    protected String extractEncoding(File file) {
        final String encodingBegin = "encoding=\"";
        final String encodingEnd = "\"";
        BufferedReader reader = null;
        try {
            final String temporaryEncoding = "UTF-8";
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), temporaryEncoding));
            String encoding = null;
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (line.trim().length() == 0) {
                    continue;
                }
                if (!line.contains(encodingBegin)) {
                    break;
                }
                line = line.substring(line.indexOf(encodingBegin) + encodingBegin.length());
                if (!line.contains(encodingEnd)) {
                    break;
                }
                encoding = line.substring(0, line.indexOf(encodingEnd)).trim();
                break;
            }
            return encoding;
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                    ignored.printStackTrace();
                }
            }
        }
    }

    // ===================================================================================
    //                                                             Classification Analyzer
    //                                                             =======================
    protected boolean isTitleLine(String line) {
        return line.contains("[") && line.contains("]") && line.indexOf("[") + 1 < line.indexOf("]");
    }

    protected boolean isTopLine(String line) {
        return line.contains("$ ");
    }

    protected boolean isElementLine(String line) {
        return line.contains("- ") && line.contains(",") && line.indexOf("- ") + 1 < line.indexOf(",");
    }

    protected String extractRelatedColumnNameFronTitleLine(String line) {
        if (!isTitleLine(line)) {
            String msg = "The line should be title line: line=" + line;
            throw new IllegalArgumentException(msg);
        }
        final String connectMark = "]:";
        final String wildCard = "*";
        final String prefixMark = DfNameHintUtil.PREFIX_MARK;
        final String suffixMark = DfNameHintUtil.SUFFIX_MARK;
        if (!line.contains(connectMark)) {
            return null;
        }
        line = line.trim();
        line = removeRearXmlEndIfNeeds(line);
        String relatedColumnName = line.substring(line.indexOf(connectMark) + connectMark.length()).trim();
        if (relatedColumnName == null) {
            return relatedColumnName;
        }
        if (relatedColumnName.startsWith(wildCard)) { // *_FLG
            relatedColumnName = suffixMark + relatedColumnName.substring(wildCard.length());
        } else if (relatedColumnName.endsWith(wildCard)) { // LD_*
            relatedColumnName = relatedColumnName.substring(0, relatedColumnName.lastIndexOf(wildCard));
            relatedColumnName = prefixMark + relatedColumnName;
        }
        return relatedColumnName;
    }

    protected DfClassificationTop extractClassificationTop(String line) {
        if (!isTopLine(line)) {
            String msg = "The line should be top line: line=" + line;
            throw new IllegalArgumentException(msg);
        }
        line = line.trim();
        line = removeRearXmlEndIfNeeds(line);
        line = line.substring(line.indexOf("$ ") + "$ ".length());

        final String classificationName;
        final String topComment;
        if (line.contains(",")) {
            classificationName = line.substring(0, line.indexOf(",")).trim();
            topComment = line.substring(line.indexOf(",") + ",".length()).trim();
        } else {
            classificationName = line.trim();
            topComment = null;
        }
        final DfClassificationTop classificationTop = new DfClassificationTop();
        classificationTop.setClassificationName(classificationName);
        if (topComment != null) {
            classificationTop.setTopComment(topComment);
        }
        return classificationTop;
    }

    protected DfClassificationElement extractClassificationElement(String line) {
        if (!isElementLine(line)) {
            String msg = "The line should be element line: line=" + line;
            throw new IllegalArgumentException(msg);
        }
        line = line.trim();
        line = removeRearXmlEndIfNeeds(line);
        line = line.substring(line.indexOf("- ") + "- ".length());

        final String code = line.substring(0, line.indexOf(",")).trim();
        line = line.substring(line.indexOf(",") + ",".length());
        final String name;
        String alias = null;
        String comment = null;
        if (line.contains(",")) {
            name = line.substring(0, line.indexOf(",")).trim();
            line = line.substring(line.indexOf(",") + ",".length());
            if (line.contains(",")) {
                alias = line.substring(0, line.indexOf(",")).trim();
                line = line.substring(line.indexOf(",") + ",".length());
                comment = line.substring(0).trim();
            } else {
                alias = line.substring(0).trim();
            }
        } else {
            name = line.substring(0).trim();
        }
        final DfClassificationElement classificationElement = new DfClassificationElement();
        classificationElement.setCode(code);
        classificationElement.setName(name);
        classificationElement.setAlias(alias);
        classificationElement.setComment(comment);
        return classificationElement;
    }

    protected String removeRearXmlEndIfNeeds(String line) {
        final String endMark = "\"/>";
        if (line.endsWith(endMark)) {
            line = line.substring(0, line.lastIndexOf(endMark));
        }
        return line;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected boolean containsLineSeparatorMark(String line) {
        return line.contains("&#xA;");
    }

    protected List<String> tokenizedLineSeparatorMark(String line) {
        return tokenize(line, "&#xA;");
    }

    protected java.util.List<String> tokenize(String value, String delimiter) {
        final DfLineToken lineToken = new DfLineTokenImpl();
        final DfLineTokenizingOption lineTokenizingOption = new DfLineTokenizingOption();
        lineTokenizingOption.setDelimiter(delimiter);
        return lineToken.tokenize(value, lineTokenizingOption);
    }
}
