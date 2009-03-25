package org.seasar.dbflute.properties;

import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 * @author jflute
 * @since 0.8.2 (2008/10/20 Monday)
 */
public final class DfDocumentProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String NORMAL_LINE_SEPARATOR = "\n";
    protected static final String SPECIAL_LINE_SEPARATOR = "&#xa;";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfDocumentProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                               documentDefinitionMap
    //                                                               =====================
    public static final String KEY_documentDefinitionMap = "documentDefinitionMap";
    protected Map<String, Object> _documentDefinitionMap;

    protected Map<String, Object> getDocumentDefinitionMap() {
        if (_documentDefinitionMap == null) {
            _documentDefinitionMap = mapProp("torque." + KEY_documentDefinitionMap, DEFAULT_EMPTY_MAP);
        }
        return _documentDefinitionMap;
    }

    // ===================================================================================
    //                                                        Alias Delimiter In DbComment
    //                                                        ============================
    public boolean isAliasDelimiterInDbCommentValid() {
        final String delimiter = getAliasDelimiterInDbComment();
        return delimiter != null && delimiter.trim().length() > 0 && !delimiter.trim().equalsIgnoreCase("null");
    }

    protected String getAliasDelimiterInDbComment() {
        String delimiter = (String) getDocumentDefinitionMap().get("aliasDelimiterInDbComment");
        if (delimiter == null || delimiter.trim().length() == 0) {
            delimiter = null;
        }
        return delimiter;
    }

    public String extractAliasFromDbComment(String comment) {
        if (!hasAlias(comment)) {
            return null;
        }
        final String delimiter = getAliasDelimiterInDbComment();
        return comment.substring(0, comment.indexOf(delimiter)).trim();
    }

    public String extractCommentFromDbComment(String comment) {
        if (!hasAlias(comment)) {
            return comment;
        }
        final String delimiter = getAliasDelimiterInDbComment();
        return comment.substring(comment.indexOf(delimiter) + delimiter.length()).trim();
    }

    protected boolean hasAlias(String comment) {
        if (comment == null || comment.trim().length() == 0) {
            return false;
        }
        if (!isAliasDelimiterInDbCommentValid()) {
            return false;
        }
        final String delimiter = getAliasDelimiterInDbComment();
        if (!comment.contains(delimiter)) {
            return false;
        }
        return true;
    }

    // ===================================================================================
    //                                                            Entity JavaDoc DbComment
    //                                                            ========================
    public boolean isEntityJavaDocDbCommentValid() {
        String value = (String) getDocumentDefinitionMap().get("entityJavaDocDbCommentValid");
        if (value == null) {
            value = (String) getDocumentDefinitionMap().get("isEntityJavaDocDbCommentValid");
        }
        return value != null && value.trim().equalsIgnoreCase("true");
    }

    public String resolveLineSeparatorForSchemaHtml(String comment) {
        if (comment == null || comment.trim().length() == 0) {
            return null;
        }
        comment = removeCR(comment);
        final String htmlLineSeparator = "<br />";
        if (comment.contains(NORMAL_LINE_SEPARATOR)) {
            comment = comment.replaceAll(NORMAL_LINE_SEPARATOR, htmlLineSeparator);
        }
        if (comment.contains(SPECIAL_LINE_SEPARATOR)) {
            comment = comment.replaceAll(SPECIAL_LINE_SEPARATOR, htmlLineSeparator);
        }
        return comment;
    }

    public String resolveLineSeparatorForJavaDoc(String comment, String indent) {
        if (getBasicProperties().isTargetLanguageCSharp()) {
            return resolveLineSeparatorForCSharpDoc(comment, "    " + indent);
        }
        if (comment == null || comment.trim().length() == 0) {
            return null;
        }
        comment = removeCR(comment);
        final String javaDocLineSeparator = "<br />" + NORMAL_LINE_SEPARATOR + indent + " * ";
        if (comment.contains(NORMAL_LINE_SEPARATOR)) {
            comment = comment.replaceAll(NORMAL_LINE_SEPARATOR, javaDocLineSeparator);
        }
        if (comment.contains(SPECIAL_LINE_SEPARATOR)) {
            comment = comment.replaceAll(SPECIAL_LINE_SEPARATOR, javaDocLineSeparator);
        }
        return comment;
    }

    protected String resolveLineSeparatorForCSharpDoc(String comment, String indent) {
        if (comment == null || comment.trim().length() == 0) {
            return null;
        }
        comment = removeCR(comment);
        final String javaDocLineSeparator = NORMAL_LINE_SEPARATOR + indent + "/// ";
        if (comment.contains(NORMAL_LINE_SEPARATOR)) {
            comment = comment.replaceAll(NORMAL_LINE_SEPARATOR, javaDocLineSeparator);
        }
        if (comment.contains(SPECIAL_LINE_SEPARATOR)) {
            comment = comment.replaceAll(SPECIAL_LINE_SEPARATOR, javaDocLineSeparator);
        }
        return comment;
    }

    // ===================================================================================
    //                                                                   Data Xls Tempalte
    //                                                                   =================
    public boolean isDataXlsTemplateRecordLimitValid() {
        final Integer limit = getDataXlsTemplateRecordLimit();
        return limit != null;
    }

    public Integer getDataXlsTemplateRecordLimit() {
        String limit = (String) getDocumentDefinitionMap().get("dataXlsTemplateRecordLimit");
        if (limit == null || limit.trim().length() == 0 || limit.trim().equalsIgnoreCase("null")) {
            return null;
        }
        try {
            return Integer.valueOf(limit);
        } catch (NumberFormatException e) {
            String msg = "The property 'dataXlsTemplateRecordLimit' of " + KEY_documentDefinitionMap;
            msg = msg + " should be number but: value=" + limit;
            throw new IllegalStateException(msg, e);
        }
    }

    public boolean isDataXlsTemplateContainsCommonColumn() {
        String value = (String) getDocumentDefinitionMap().get("dataXlsTemplateContainsCommonColumn");
        if (value == null) {
            value = (String) getDocumentDefinitionMap().get("isDataXlsTemplateContainsCommonColumn");
        }
        return value != null && value.trim().equalsIgnoreCase("true");
    }

    public File getDataXlsTemplateFile() {
        final File xlsFile = new File("./output/doc/data-xls-template.xls");
        return xlsFile;
    }
    
    public File getDataCsvTemplateDir() {
        final File xlsFile = new File("./output/doc/csvdata");
        return xlsFile;
    }
}