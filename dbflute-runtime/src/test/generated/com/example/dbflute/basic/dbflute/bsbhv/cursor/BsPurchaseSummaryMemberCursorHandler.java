package com.example.dbflute.basic.dbflute.bsbhv.cursor;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.dbflute.jdbc.CursorHandler;

import com.example.dbflute.basic.dbflute.exbhv.cursor.PurchaseSummaryMemberCursor;

/**
 * The cursor handler of PurchaseSummaryMember.
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsPurchaseSummaryMemberCursorHandler implements CursorHandler {

    /**
     * Handle.
     * @param rs Result set. (NotNull)
     * @return Result. (Nullable)
     * @throws java.sql.SQLException
     */
    public Object handle(java.sql.ResultSet rs) throws SQLException {
        return fetchCursor(createTypeSafeCursor(rs));
    }

    /**
     * Create type safe cursor.
     * @param rs Result set. (NotNull)
     * @return Type safe cursor. (Nullable)
     * @throws java.sql.SQLException
     */
    protected PurchaseSummaryMemberCursor createTypeSafeCursor(ResultSet rs) throws SQLException {
        final PurchaseSummaryMemberCursor cursor = new PurchaseSummaryMemberCursor();
        cursor.accept(rs);
        return cursor;
    }

    /**
     * Fetch cursor.
     * @param cursor Type safe cursor. (NotNull)
     * @return Result. (Nullable)
     * @throws java.sql.SQLException
     */
    abstract protected Object fetchCursor(PurchaseSummaryMemberCursor cursor) throws SQLException;
}
