package org.seasar.dbflute.logic.schemahtml;

import org.apache.torque.engine.database.model.ForeignKey;
import org.seasar.dbflute.properties.DfDocumentProperties;

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
        final StringBuilder sb = new StringBuilder();
        sb.append(delimiter);
        final String baseTitle = fk.getName();
        final String contentName;
        if (fk.isAdditionalForeignKey()) {
            final String addtionalBaseTitle = baseTitle + "(additinal)";
            if (fk.hasFixedCondition()) {
                final String fixedCondition = fk.getFixedCondition();
                final String title = resolveTitle(addtionalBaseTitle + ": fixedCondition=\"" + fixedCondition + "\"");
                sb.append("<a href=\"#" + name + "\" class=\"additionalfk\" title=\"" + title + "\">");
            } else {
                final String title = resolveTitle(addtionalBaseTitle);
                sb.append("<a href=\"#" + name + "\" class=\"additionalfk\" title=\"" + title + "\">");
            }
            contentName = name + (fk.hasFixedSuffix() ? "(" + fk.getFixedSuffix() + ")" : "");
        } else {
            final String title = resolveTitle(baseTitle);
            sb.append("<a href=\"#" + name + "\" title=\"" + title + "\">");
            contentName = name;
        }
        sb.append(contentName).append("</a>");
        return sb.toString();
    }

    protected String resolveTitle(String title) {
        return _documentProperties.resolveAttributeForSchemaHtml(title);
    }
}
