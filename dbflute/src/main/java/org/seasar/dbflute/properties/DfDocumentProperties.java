package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

/**
 * @author jflute
 * @since 0.8.2 (2008/10/20 Monday)
 */
public final class DfDocumentProperties extends DfAbstractHelperProperties {

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
        if (comment == null || comment.trim().length() == 0) {
            return null;
        }
        if (!isAliasDelimiterInDbCommentValid()) {
            return null;
        }
        final String delimiter = getAliasDelimiterInDbComment();
        if (!comment.contains(delimiter)) {
            return null;
        }
        return comment.substring(0, comment.indexOf(delimiter));
    }

    // ===================================================================================
    //                                                            Entity JavaDoc DbComment
    //                                                            ========================
    public boolean isEntityJavaDocDbCommentValid() {
        String value = (String) getDocumentDefinitionMap().get("entityJavaDocDbCommentValid");
        return value != null && value.trim().length() > 0 && value.equalsIgnoreCase("true");
    }
}