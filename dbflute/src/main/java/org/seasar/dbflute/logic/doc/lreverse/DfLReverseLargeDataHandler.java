package org.seasar.dbflute.logic.doc.lreverse;

import org.seasar.dbflute.logic.doc.lreverse.DfLReverseDataExtractor.DfLReverseLargeDataResultSetWrapper;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/25 Monday)
 */
public interface DfLReverseLargeDataHandler {

    void handle(DfLReverseLargeDataResultSetWrapper wrapper);
}
