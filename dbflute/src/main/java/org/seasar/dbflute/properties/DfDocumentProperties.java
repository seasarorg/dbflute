package org.seasar.dbflute.properties;

import java.util.Comparator;
import java.util.Map;
import java.util.Properties;

import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.8.2 (2008/10/20 Monday)
 */
public final class DfDocumentProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    // here fixed line separator (simplified)
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
    //                                                                    Output Directory
    //                                                                    ================
    public String getDocumentOutputDirectory() {
        final String defaultValue = "./output/doc";
        return getProperty("documentOutputDirectory", defaultValue, getDocumentDefinitionMap());
    }

    // ===================================================================================
    //                                                                     Alias DbComment
    //                                                                     ===============
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
        if (!isAliasHandling(comment)) {
            // alias does not exist everywhere
            // if alias handling is not valid
            return null;
        }
        if (hasAliasDelimiter(comment)) {
            final String delimiter = getAliasDelimiterInDbComment();
            return comment.substring(0, comment.indexOf(delimiter)).trim();
        } else {
            if (isDbCommentOnAliasBasis()) {
                return comment; // because the comment is for alias
            } else {
                return null;
            }
        }
    }

    public String extractCommentFromDbComment(String comment) {
        if (!isAliasHandling(comment)) {
            return comment;
        }
        if (hasAliasDelimiter(comment)) {
            final String delimiter = getAliasDelimiterInDbComment();
            return comment.substring(comment.indexOf(delimiter) + delimiter.length()).trim();
        } else {
            if (isDbCommentOnAliasBasis()) {
                return null; // because the comment is for alias
            } else {
                return comment;
            }
        }
    }

    protected boolean isAliasHandling(String comment) {
        if (comment == null || comment.trim().length() == 0) {
            return false;
        }
        return isAliasDelimiterInDbCommentValid();
    }

    protected boolean hasAliasDelimiter(String comment) {
        final String delimiter = getAliasDelimiterInDbComment();
        return comment.contains(delimiter);
    }

    protected boolean isDbCommentOnAliasBasis() {
        return isProperty("isDbCommentOnAliasBasis", false, getDocumentDefinitionMap());
    }

    // ===================================================================================
    //                                                            Entity JavaDoc DbComment
    //                                                            ========================
    public boolean isEntityJavaDocDbCommentValid() {
        return isProperty("isEntityJavaDocDbCommentValid", false, getDocumentDefinitionMap());
    }

    public String resolveTextForSchemaHtml(String text) {
        if (text == null || text.trim().length() == 0) {
            return null;
        }
        // escape
        text = DfStringUtil.replace(text, "<", "&lt;");
        text = DfStringUtil.replace(text, ">", "&gt;");

        // line separator
        text = removeCR(text);
        final String htmlLineSeparator = "<br />";
        if (text.contains(NORMAL_LINE_SEPARATOR)) {
            text = text.replaceAll(NORMAL_LINE_SEPARATOR, htmlLineSeparator);
        }
        if (text.contains(SPECIAL_LINE_SEPARATOR)) {
            text = text.replaceAll(SPECIAL_LINE_SEPARATOR, htmlLineSeparator);
        }
        return text;
    }

    public String resolveAttributeForSchemaHtml(String text) {
        if (text == null || text.trim().length() == 0) {
            return null;
        }
        // escape
        text = DfStringUtil.replace(text, "<", "&lt;");
        text = DfStringUtil.replace(text, ">", "&gt;");
        text = DfStringUtil.replace(text, "\"", "&quot;");

        // line separator
        text = removeCR(text);
        return text;
    }

    public String resolvePreTextForSchemaHtml(String text) {
        if (text == null || text.trim().length() == 0) {
            return null;
        }
        // escape
        text = DfStringUtil.replace(text, "<", "&lt;");
        text = DfStringUtil.replace(text, ">", "&gt;");

        // line separator
        text = removeCR(text);
        return text;
    }

    public String resolveTextForJavaDoc(String text, String indent) {
        if (getBasicProperties().isTargetLanguageCSharp()) {
            return resolveLineSeparatorForCSharpDoc(text, "    " + indent);
        }
        if (text == null || text.trim().length() == 0) {
            return null;
        }
        text = DfStringUtil.replace(text, "<", "&lt;");
        text = DfStringUtil.replace(text, ">", "&gt;");
        text = removeCR(text);
        final String javaDocLineSeparator = "<br />" + NORMAL_LINE_SEPARATOR + indent + " * ";
        if (text.contains(NORMAL_LINE_SEPARATOR)) {
            text = text.replaceAll(NORMAL_LINE_SEPARATOR, javaDocLineSeparator);
        }
        if (text.contains(SPECIAL_LINE_SEPARATOR)) {
            text = text.replaceAll(SPECIAL_LINE_SEPARATOR, javaDocLineSeparator);
        }
        return text;
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
    //                                                             Entity DBMeta DbComment
    //                                                             =======================
    public boolean isEntityDBMetaDbCommentValid() {
        return isProperty("isEntityDBMetaDbCommentValid", false, getDocumentDefinitionMap());
    }

    public String resolveTextForDBMeta(String text) { // C# same as Java
        if (text == null || text.trim().length() == 0) {
            return null;
        }
        text = removeCR(text);
        text = DfStringUtil.replace(text, "\"", "\\\""); // escape double quotation

        final String literalLineSeparator = "\\\\n";
        if (text.contains(NORMAL_LINE_SEPARATOR)) {
            text = text.replaceAll(NORMAL_LINE_SEPARATOR, literalLineSeparator);
        }
        if (text.contains(SPECIAL_LINE_SEPARATOR)) {
            text = text.replaceAll(SPECIAL_LINE_SEPARATOR, literalLineSeparator);
        }
        return text;
    }

    // ===================================================================================
    //                                                                          SchemaHTML
    //                                                                          ==========
    public String getSchemaHtmlFileName(String projectName) {
        final String defaultName = "schema-" + projectName + ".html";
        return getProperty("schemaHtmlFileName", defaultName, getDocumentDefinitionMap());
    }

    public boolean isSuppressSchemaHtmlOutsideSql() {
        return isProperty("isSuppressSchemaHtmlOutsideSql", false, getDocumentDefinitionMap());
    }

    // ===================================================================================
    //                                                                         HistoryHTML
    //                                                                         ===========
    public String getHistoryHtmlFileName(String projectName) {
        final String defaultName = "history-" + projectName + ".html";
        return getProperty("historyHtmlFileName", defaultName, getDocumentDefinitionMap());
    }

    // ===================================================================================
    //                                                                   Data Xls Template
    //                                                                   =================
    protected Map<String, String> _dataXlsTemplateMap;

    protected Map<String, String> getDataXlsTemplateMap() {
        if (_dataXlsTemplateMap != null) {
            return _dataXlsTemplateMap;
        }
        final String key = "dataXlsTemplateMap";
        @SuppressWarnings("unchecked")
        final Map<String, String> map = (Map<String, String>) getDocumentDefinitionMap().get(key);
        if (map != null) {
            _dataXlsTemplateMap = map;
        } else {
            _dataXlsTemplateMap = DfCollectionUtil.emptyMap();
        }
        return _dataXlsTemplateMap;
    }

    public boolean isDataXlsTemplateRecordLimitValid() {
        final Integer limit = getDataXlsTemplateRecordLimit();
        return limit != null;
    }

    public Integer getDataXlsTemplateRecordLimit() {
        final Map<String, String> dataXlsTemplateMap = getDataXlsTemplateMap();
        String limitExp = null;
        if (!dataXlsTemplateMap.isEmpty()) {
            final String newKey = "recordLimit";
            limitExp = dataXlsTemplateMap.get(newKey);
        }
        if (Srl.is_Null_or_TrimmedEmpty(limitExp) || limitExp.trim().equalsIgnoreCase("null")) {
            final String oldKey = "dataXlsTemplateRecordLimit";
            limitExp = (String) getDocumentDefinitionMap().get(oldKey);
            if (Srl.is_Null_or_TrimmedEmpty(limitExp) || limitExp.trim().equalsIgnoreCase("null")) {
                return null;
            }
        }
        try {
            return Integer.valueOf(limitExp);
        } catch (NumberFormatException e) {
            String msg = "The property 'dataXlsTemplateRecordLimit' of " + KEY_documentDefinitionMap;
            msg = msg + " should be number but: value=" + limitExp;
            throw new IllegalStateException(msg, e);
        }
    }

    public boolean isDataXlsTemplateContainsCommonColumn() {
        final String newKey = "isContainsCommonColumn";
        final String oldKey = "isDataXlsTemplateContainsCommonColumn";
        final boolean defaultValue = isProperty(oldKey, false, getDocumentDefinitionMap());
        return isProperty(newKey, defaultValue, getDataXlsTemplateMap());
    }

    public String getDataXlsTemplateDir() {
        if (isDataXlsTemplateLoadDataReverse()) {
            return getReplaceSchemaProperties().getMainCurrentEnvFirstXlsDataDir();
        } else {
            final String outputDirectory = getDocumentOutputDirectory();
            return outputDirectory + "/data";
        }
    }

    public String getDataDelimiterTemplateDir() { // for large data
        if (isDataXlsTemplateLoadDataReverse()) {
            return getReplaceSchemaProperties().getMainCurrentEnvTsvUTF8DataDir();
        } else {
            final String templateDir = getDataXlsTemplateDir();
            return templateDir + "/large-data";
        }
    }

    public String getDataXlsTemplateFileTitle() {
        if (isDataXlsTemplateLoadDataReverse()) {
            return "reverse-data";
        } else {
            return "dataxls";
        }
    }

    public boolean isDataXlsTemplateLoadDataReverse() {
        final String key = "isLoadDataReverse";
        return isProperty(key, false, getDataXlsTemplateMap());
    }

    // ===================================================================================
    //                                                              Table Display Order By
    //                                                              ======================
    public Comparator<Table> getTableDisplayOrderBy() {
        return new Comparator<Table>() {
            public int compare(Table table1, Table table2) {
                // = = = =
                // Schema
                // = = = =
                // The main schema has priority
                {
                    final boolean mainSchema1 = table1.isMainSchema();
                    final boolean mainSchema2 = table2.isMainSchema();
                    if (mainSchema1 != mainSchema2) {
                        if (mainSchema1) {
                            return -1;
                        }
                        if (mainSchema2) {
                            return 1;
                        }
                        // unreachable
                    }
                    final String schema1 = table1.getDocumentSchema();
                    final String schema2 = table2.getDocumentSchema();
                    if (schema1 != null && schema2 != null && !schema1.equals(schema2)) {
                        return schema1.compareTo(schema2);
                    } else if (schema1 == null && schema2 != null) {
                        return 1; // nulls last
                    } else if (schema1 != null && schema2 == null) {
                        return -1; // nulls last
                    }
                    // passed: when both are NOT main and are same schema
                }

                // = = =
                // Type
                // = = =
                {
                    final String type1 = table1.getType();
                    final String type2 = table2.getType();
                    if (!type1.equals(type2)) {
                        // The table type has priority
                        if (table1.isTypeTable()) {
                            return -1;
                        }
                        if (table2.isTypeTable()) {
                            return 1;
                        }
                        return type1.compareTo(type2);
                    }
                }

                // = = =
                // Table
                // = = =
                final String name1 = table1.getName();
                final String name2 = table2.getName();
                return name1.compareTo(name2);
            }
        };
    }
}