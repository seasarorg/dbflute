package org.seasar.dbflute.logic.doc.schemahtml;

import org.apache.torque.engine.database.model.ForeignKey;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.2 (2009/02/12 Thursday)
 */
public class DfSchemaHtmlBuilder {

    protected DfDocumentProperties _documentProperties;

    public DfSchemaHtmlBuilder(DfDocumentProperties documentProperties) {
        _documentProperties = documentProperties;
    }

    public String buildRelatedTableLink(ForeignKey fk, String name, String delimiter) {
        final String lowerName = name.toLowerCase();
        final StringBuilder sb = new StringBuilder();
        sb.append(delimiter);
        final String baseTitle = fk.getName();
        final String comment = fk.getComment();
        final String contentName;
        if (fk.isAdditionalForeignKey()) {
            final String addtionalBaseTitle = baseTitle;
            final String fixedCondition = fk.getFixedCondition();
            final StringBuilder titleSb = new StringBuilder();
            titleSb.append(addtionalBaseTitle);
            boolean comma = false;
            if (fk.hasFixedCondition()) {
                titleSb.append(comma ? ", " : ": ");
                titleSb.append("fixedCondition=\"").append(fixedCondition).append("\"");
                comma = true;
            }
            if (Srl.is_NotNull_and_NotTrimmedEmpty(comment)) {
                titleSb.append(comma ? ", " : ": ");
                titleSb.append("comment=").append(comment);
                comma = true;
            }
            final String title = resolveTitle(titleSb.toString());
            sb.append("<a href=\"#" + lowerName + "\" class=\"additionalfk\" title=\"" + title + "\">");
            contentName = name + (fk.hasFixedSuffix() ? "(" + fk.getFixedSuffix() + ")" : "");
        } else {
            final StringBuilder titleSb = new StringBuilder();
            titleSb.append(baseTitle);
            if (Srl.is_NotNull_and_NotTrimmedEmpty(comment)) {
                titleSb.append(": comment=").append(comment);
            }
            final String title = resolveTitle(titleSb.toString());
            sb.append("<a href=\"#" + lowerName + "\" title=\"" + title + "\">");
            contentName = name;
        }
        sb.append(contentName).append("</a>");
        return sb.toString();
    }

    protected String resolveTitle(String title) {
        return _documentProperties.resolveAttributeForSchemaHtml(title);
    }
}
