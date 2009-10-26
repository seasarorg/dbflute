package org.seasar.dbflute.properties;

import java.io.File;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;

import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.util.DfStringUtil;

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

    public String resolveTextForSchemaHtml(String text) {
        if (text == null || text.trim().length() == 0) {
            return null;
        }
        text = removeCR(text);
        final String htmlLineSeparator = "<br />";
        if (text.contains(NORMAL_LINE_SEPARATOR)) {
            text = text.replaceAll(NORMAL_LINE_SEPARATOR, htmlLineSeparator);
        }
        if (text.contains(SPECIAL_LINE_SEPARATOR)) {
            text = text.replaceAll(SPECIAL_LINE_SEPARATOR, htmlLineSeparator);
        }
        text = DfStringUtil.replace(text, "<", "&lt;");
        text = DfStringUtil.replace(text, ">", "&gt;");
        return text;
    }

    public String resolvePreTextForSchemaHtml(String text) {
        if (text == null || text.trim().length() == 0) {
            return null;
        }
        text = removeCR(text);
        text = DfStringUtil.replace(text, "<", "&lt;");
        text = DfStringUtil.replace(text, ">", "&gt;");
        return text;
    }

    public String resolveTextForJavaDoc(String Text, String indent) {
        if (getBasicProperties().isTargetLanguageCSharp()) {
            return resolveLineSeparatorForCSharpDoc(Text, "    " + indent);
        }
        if (Text == null || Text.trim().length() == 0) {
            return null;
        }
        Text = removeCR(Text);
        final String javaDocLineSeparator = "<br />" + NORMAL_LINE_SEPARATOR + indent + " * ";
        if (Text.contains(NORMAL_LINE_SEPARATOR)) {
            Text = Text.replaceAll(NORMAL_LINE_SEPARATOR, javaDocLineSeparator);
        }
        if (Text.contains(SPECIAL_LINE_SEPARATOR)) {
            Text = Text.replaceAll(SPECIAL_LINE_SEPARATOR, javaDocLineSeparator);
        }
        Text = DfStringUtil.replace(Text, "<", "&lt;");
        Text = DfStringUtil.replace(Text, ">", "&gt;");
        return Text;
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
                    final boolean mainSchema2 = table1.isMainSchema();
                    if (mainSchema1 != mainSchema2) {
                        if (mainSchema1) {
                            return -1;
                        }
                        if (mainSchema2) {
                            return 1;
                        }
                        // Unreachable!
                    }
                    final String schema1 = table1.getSchema();
                    final String schema2 = table2.getSchema();
                    if (schema1 != null && schema2 != null && !schema1.equals(schema2)) {
                        return schema1.compareTo(schema2);
                    }
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