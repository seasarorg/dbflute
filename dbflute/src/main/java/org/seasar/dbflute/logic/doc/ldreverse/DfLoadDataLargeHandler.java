package org.seasar.dbflute.logic.doc.ldreverse;

import org.seasar.dbflute.logic.doc.ldreverse.DfLoadDataExtractor.DfLodaDataResultSetWrapper;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/25 Monday)
 */
public interface DfLoadDataLargeHandler {

    void handle(DfLodaDataResultSetWrapper wrapper);
}
