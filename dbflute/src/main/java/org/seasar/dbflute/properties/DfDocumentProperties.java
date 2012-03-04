package org.seasar.dbflute.properties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;

import org.apache.torque.engine.database.model.Table;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.exception.DfIllegalPropertyTypeException;
import org.seasar.dbflute.util.DfCollectionUtil;
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

    protected static final String STYLE_SHEET_EMBEDDED_MARK = "$";
    protected static final String JAVA_SCRIPT_EMBEDDED_MARK = "$";

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

    public String getAliasDelimiterInDbComment() {
        String delimiter = (String) getDocumentDefinitionMap().get("aliasDelimiterInDbComment");
        if (delimiter == null || delimiter.trim().length() == 0) {
            delimiter = null;
        }
        return delimiter;
    }

    public String extractAliasFromDbComment(String comment) { // alias is trimmed
        if (isAliasHandling(comment)) {
            if (hasAliasDelimiter(comment)) {
                final String delimiter = getAliasDelimiterInDbComment();
                return comment.substring(0, comment.indexOf(delimiter)).trim();
            } else {
                if (isDbCommentOnAliasBasis()) {
                    // because the comment is for alias
                    return comment != null ? comment.trim() : null;
                }
            }
        }
        // alias does not exist everywhere
        // if alias handling is not valid
        return null;
    }

    public String extractCommentFromDbComment(String comment) { // comment is trimmed
        if (isAliasHandling(comment)) {
            if (hasAliasDelimiter(comment)) {
                final String delimiter = getAliasDelimiterInDbComment();
                return comment.substring(comment.indexOf(delimiter) + delimiter.length()).trim();
            } else {
                if (isDbCommentOnAliasBasis()) {
                    // because the comment is for alias
                    return null;
                }
            }
        }
        return comment != null ? comment.trim() : null;
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

    public boolean isDbCommentOnAliasBasis() {
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
        text = Srl.replace(text, "<", "&lt;");
        text = Srl.replace(text, ">", "&gt;");
        text = Srl.replace(text, " ", "&nbsp;");

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
        text = Srl.replace(text, "<", "&lt;");
        text = Srl.replace(text, ">", "&gt;");
        text = Srl.replace(text, "\"", "&quot;");

        // line separator
        text = removeCR(text);
        return text;
    }

    public String resolvePreTextForSchemaHtml(String text) {
        if (text == null || text.trim().length() == 0) {
            return null;
        }
        // escape
        text = Srl.replace(text, "<", "&lt;");
        text = Srl.replace(text, ">", "&gt;");

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
        text = Srl.replace(text, "<", "&lt;");
        text = Srl.replace(text, ">", "&gt;");
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
        text = Srl.replace(text, "\"", "\\\""); // escape double quotation

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

    // -----------------------------------------------------
    //                                           Style Sheet
    //                                           -----------
    public boolean isSchemaHtmlStyleSheetEmbedded() {
        final String styleSheet = getSchemaHtmlStyleSheet();
        return styleSheet != null && hasSchemaHtmlStyleSheetEmbeddedMark(styleSheet);
    }

    public boolean isSchemaHtmlStyleSheetLink() {
        final String styleSheet = getSchemaHtmlStyleSheet();
        return styleSheet != null && !hasSchemaHtmlStyleSheetEmbeddedMark(styleSheet);
    }

    protected boolean hasSchemaHtmlStyleSheetEmbeddedMark(String styleSheet) {
        return styleSheet.startsWith(STYLE_SHEET_EMBEDDED_MARK);
    }

    public String getSchemaHtmlStyleSheetEmbedded() {
        return readSchemaHtmlStyleSheetEmbedded(getSchemaHtmlStyleSheet());
    }

    protected String readSchemaHtmlStyleSheetEmbedded(String styleSheet) {
        final String purePath = Srl.substringFirstRear(styleSheet, STYLE_SHEET_EMBEDDED_MARK);
        final File cssFile = new File(purePath);
        BufferedReader br = null;
        try {
            final String encoding = getBasicProperties().getTemplateFileEncoding();
            final String separator = getBasicProperties().getSourceCodeLineSeparator();
            br = new BufferedReader(new InputStreamReader(new FileInputStream(cssFile), encoding));
            final StringBuilder sb = new StringBuilder();
            while (true) {
                final String line = br.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line).append(separator);
            }
            return sb.toString();
        } catch (IOException e) {
            String msg = "Failed to read the CSS file: " + cssFile;
            throw new IllegalStateException(msg, e);
        }
    }

    public String getSchemaHtmlStyleSheetLink() {
        return buildSchemaHtmlStyleSheetLink(getSchemaHtmlStyleSheet());
    }

    protected String buildSchemaHtmlStyleSheetLink(String styleSheet) {
        return "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + styleSheet + "\" />";
    }

    protected String getSchemaHtmlStyleSheet() { // closet
        return getProperty("schemaHtmlStyleSheet", null, getDocumentDefinitionMap());
    }

    // -----------------------------------------------------
    //                                            JavaScript
    //                                            ----------
    public boolean isSchemaHtmlJavaScriptEmbedded() {
        final String javaScript = getSchemaHtmlJavaScript();
        return javaScript != null && hasSchemaHtmlJavaScriptEmbeddedMark(javaScript);
    }

    public boolean isSchemaHtmlJavaScriptLink() {
        final String javaScript = getSchemaHtmlJavaScript();
        return javaScript != null && !hasSchemaHtmlJavaScriptEmbeddedMark(javaScript);
    }

    protected boolean hasSchemaHtmlJavaScriptEmbeddedMark(String javaScript) {
        return javaScript.startsWith(JAVA_SCRIPT_EMBEDDED_MARK);
    }

    public String getSchemaHtmlJavaScriptEmbedded() {
        return readSchemaHtmlJavaScriptEmbedded(getSchemaHtmlJavaScript());
    }

    protected String readSchemaHtmlJavaScriptEmbedded(String javaScript) {
        final String purePath = Srl.substringFirstRear(javaScript, JAVA_SCRIPT_EMBEDDED_MARK);
        final File cssFile = new File(purePath);
        BufferedReader br = null;
        try {
            final String encoding = getBasicProperties().getTemplateFileEncoding();
            final String separator = getBasicProperties().getSourceCodeLineSeparator();
            br = new BufferedReader(new InputStreamReader(new FileInputStream(cssFile), encoding));
            final StringBuilder sb = new StringBuilder();
            while (true) {
                final String line = br.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line).append(separator);
            }
            return sb.toString();
        } catch (IOException e) {
            String msg = "Failed to read the CSS file: " + cssFile;
            throw new IllegalStateException(msg, e);
        }
    }

    public String getSchemaHtmlJavaScriptLink() {
        return buildSchemaHtmlJavaScriptLink(getSchemaHtmlJavaScript());
    }

    protected String buildSchemaHtmlJavaScriptLink(String javaScript) {
        return "<script type=\"text/javascript\" src=\"" + javaScript + "\"></script>";
    }

    protected String getSchemaHtmlJavaScript() { // closet
        return getProperty("schemaHtmlJavaScript", null, getDocumentDefinitionMap());
    }

    // ===================================================================================
    //                                                                         HistoryHTML
    //                                                                         ===========
    public String getHistoryHtmlFileName(String projectName) {
        final String defaultName = "history-" + projectName + ".html";
        return getProperty("historyHtmlFileName", defaultName, getDocumentDefinitionMap());
    }

    // options below are related to HistoryHTML, SchemaSyncCheck, AlterCheck, ...

    public boolean isCheckColumnDefOrderDiff() {
        return isProperty("isCheckColumnDefOrderDiff", false, getDocumentDefinitionMap());
    }

    public boolean isCheckDbCommentDiff() {
        return isProperty("isCheckDbCommentDiff", false, getDocumentDefinitionMap());
    }

    // -----------------------------------------------------
    //                                           Style Sheet
    //                                           -----------
    public boolean isHistoryHtmlStyleSheetEmbedded() {
        final String styleSheet = getHistoryHtmlStyleSheet();
        return styleSheet != null && hasSchemaHtmlStyleSheetEmbeddedMark(styleSheet);
    }

    public boolean isHistoryHtmlStyleSheetLink() {
        final String styleSheet = getHistoryHtmlStyleSheet();
        return styleSheet != null && !hasSchemaHtmlStyleSheetEmbeddedMark(styleSheet);
    }

    public String getHistoryHtmlStyleSheetEmbedded() {
        return readSchemaHtmlStyleSheetEmbedded(getHistoryHtmlStyleSheet());
    }

    public String getHistoryHtmlStyleSheetLink() {
        return buildSchemaHtmlStyleSheetLink(getHistoryHtmlStyleSheet());
    }

    protected String getHistoryHtmlStyleSheet() { // closet
        return getProperty("historyHtmlStyleSheet", null, getDocumentDefinitionMap());
    }

    // -----------------------------------------------------
    //                                            JavaScript
    //                                            ----------
    public boolean isHistoryHtmlJavaScriptEmbedded() {
        final String javaScript = getHistoryHtmlJavaScript();
        return javaScript != null && hasSchemaHtmlJavaScriptEmbeddedMark(javaScript);
    }

    public boolean isHistoryHtmlJavaScriptLink() {
        final String javaScript = getHistoryHtmlJavaScript();
        return javaScript != null && !hasSchemaHtmlJavaScriptEmbeddedMark(javaScript);
    }

    public String getHistoryHtmlJavaScriptEmbedded() {
        return readSchemaHtmlJavaScriptEmbedded(getHistoryHtmlJavaScript());
    }

    public String getHistoryHtmlJavaScriptLink() {
        return buildSchemaHtmlJavaScriptLink(getHistoryHtmlJavaScript());
    }

    protected String getHistoryHtmlJavaScript() { // closet
        return getProperty("historyHtmlJavaScript", null, getDocumentDefinitionMap());
    }

    // ===================================================================================
    //                                                                     LoadDataReverse
    //                                                                     ===============
    protected Map<String, String> _loadDataReverseMap;

    protected Map<String, String> getLoadDataReverseMap() {
        if (_loadDataReverseMap != null) {
            return _loadDataReverseMap;
        }
        final String key = "loadDataReverseMap";
        @SuppressWarnings("unchecked")
        final Map<String, String> map = (Map<String, String>) getDocumentDefinitionMap().get(key);
        if (map != null) {
            _loadDataReverseMap = map;
        } else {
            _loadDataReverseMap = DfCollectionUtil.emptyMap();
        }
        return _loadDataReverseMap;
    }

    public boolean isLoadDataReverseValid() {
        return getLoadDataReverseRecordLimit() != null;
    }

    public Integer getLoadDataReverseRecordLimit() {
        final Map<String, String> loadDataReverseMap = getLoadDataReverseMap();
        String limitExp = null;
        if (!loadDataReverseMap.isEmpty()) {
            limitExp = loadDataReverseMap.get("recordLimit");
        }
        if (limitExp == null) {
            return null;
        }
        try {
            return Integer.valueOf(limitExp);
        } catch (NumberFormatException e) {
            String msg = "The property 'recordLimit' of loadDataReverse in " + KEY_documentDefinitionMap;
            msg = msg + " should be number but: value=" + limitExp;
            throw new DfIllegalPropertyTypeException(msg, e);
        }
    }

    public boolean isLoadDataReverseContainsCommonColumn() {
        return isProperty("isContainsCommonColumn", false, getLoadDataReverseMap());
    }

    public boolean isLoadDataReverseManagedTableOnly() {
        return isLoadDataReverseOutputToPlaySql();
    }

    public Integer getLoadDataReverseXlsLimit() {
        final Map<String, String> loadDataReverseMap = getLoadDataReverseMap();
        String limitExp = null;
        if (!loadDataReverseMap.isEmpty()) {
            limitExp = loadDataReverseMap.get("xlsLimit");
        }
        if (limitExp == null) {
            return null; // if null, default limit
        }
        try {
            return Integer.valueOf(limitExp);
        } catch (NumberFormatException e) {
            String msg = "The property 'xlsLimit' of loadDataReverse in " + KEY_documentDefinitionMap;
            msg = msg + " should be number but: value=" + limitExp;
            throw new DfIllegalPropertyTypeException(msg, e);
        }
    }

    public String getLoadDataReverseXlsDataDir() {
        if (isLoadDataReverseOutputToPlaySql()) {
            return getReplaceSchemaProperties().getMainCurrentLoadTypeFirstXlsDataDir();
        } else {
            final String outputDirectory = getDocumentOutputDirectory();
            return outputDirectory + "/data";
        }
    }

    public String getLoadDataReverseDelimiterDataDir() { // for large data
        if (isLoadDataReverseOutputToPlaySql()) {
            return getReplaceSchemaProperties().getMainCurrentLoadTypeTsvUTF8DataDir();
        } else {
            final String templateDir = getLoadDataReverseXlsDataDir();
            return templateDir + "/large-data";
        }
    }

    public String getLoadDataReverseFileTitle() {
        return "load-data";
    }

    public boolean isLoadDataReverseOutputToPlaySql() {
        return isProperty("isOutputToPlaySql", false, getLoadDataReverseMap());
    }

    // ===================================================================================
    //                                                                     SchemaSyncCheck
    //                                                                     ===============
    protected Map<String, String> _schemaSyncCheckMap;

    protected Map<String, String> getSchemaSyncCheckMap() {
        if (_schemaSyncCheckMap != null) {
            return _schemaSyncCheckMap;
        }
        final String key = "schemaSyncCheckMap";
        @SuppressWarnings("unchecked")
        final Map<String, String> map = (Map<String, String>) getDocumentDefinitionMap().get(key);
        if (map != null) {
            _schemaSyncCheckMap = map;
        } else {
            _schemaSyncCheckMap = DfCollectionUtil.emptyMap();
        }
        return _schemaSyncCheckMap;
    }

    public boolean isSchemaSyncCheckValid() {
        return getSchemaSyncCheckDatabaseUser() != null;
    }

    public String getSchemaSyncCheckDatabaseUrl() {
        final Map<String, String> schemaSyncCheckMap = getSchemaSyncCheckMap();
        final String url = schemaSyncCheckMap.get("url");
        return Srl.is_NotNull_and_NotTrimmedEmpty(url) ? url : getDatabaseProperties().getDatabaseUrl();
    }

    public String getSchemaSyncCheckDatabaseCatalog() {
        final Map<String, String> schemaSyncCheckMap = getSchemaSyncCheckMap();
        final String catalog = schemaSyncCheckMap.get("catalog");
        final String url = getSchemaSyncCheckDatabaseUrl();
        return getDatabaseProperties().prepareMainCatalog(catalog, url);
    }

    public UnifiedSchema getSchemaSyncCheckDatabaseSchema() {
        final Map<String, String> schemaSyncCheckMap = getSchemaSyncCheckMap();
        final String schema = schemaSyncCheckMap.get("schema");
        final String catalog = getSchemaSyncCheckDatabaseCatalog();
        return getDatabaseProperties().prepareMainUnifiedSchema(catalog, schema);
    }

    public String getSchemaSyncCheckDatabaseUser() {
        final Map<String, String> schemaSyncCheckMap = getSchemaSyncCheckMap();
        return schemaSyncCheckMap.get("user");
    }

    public String getSchemaSyncCheckDatabasePassword() {
        final Map<String, String> schemaSyncCheckMap = getSchemaSyncCheckMap();
        final String password = schemaSyncCheckMap.get("password");
        return password != null ? password : "";
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