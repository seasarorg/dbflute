package org.seasar.dbflute.logic.doc.dataxls;

import org.seasar.dbflute.logic.doc.dataxls.DfTemplateDataExtractor.DfTemplateDataResultSetWrapper;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/25 Monday)
 */
public interface DfTemplateDataLargeHandler {

    void handle(DfTemplateDataResultSetWrapper wrapper);
}
