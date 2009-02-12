package org.seasar.dbflute.logic.schemahtml;

import org.apache.torque.engine.database.model.ForeignKey;

/**
 * @author jflute
 * @since 0.9.2 (2009/02/12 Thursday)
 */
public class DfSchemaHtmlBuilder {

    public String buildRelatedTableLink(ForeignKey fk, String name, String delimiter) {
        StringBuilder sb = new StringBuilder();
        sb.append(delimiter);
        if (fk.isAdditionalForeignKey()) {
            sb.append("<a href=\"#" + name + "\" class=\"additionalfk\">");
        } else {
            sb.append("<a href=\"#" + name + "\">");
        }
        sb.append(name).append("</a>");
        return sb.toString();
    }
}
