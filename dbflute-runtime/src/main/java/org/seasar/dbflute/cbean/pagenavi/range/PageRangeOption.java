package org.seasar.dbflute.cbean.pagenavi.range;

/**
 * The option of page range.
 * @author DBFlute(AutoGenerator)
 */
public class PageRangeOption implements java.io.Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected int _pageRangeSize;
    protected boolean _fillLimit;

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * @return The view string of all attribute values. (NotNull)
     */
	@Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append(" pageRangeSize=").append(_pageRangeSize);
        sb.append(" fillLimit=").append(_fillLimit);

        return sb.toString();
    }
	
    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public int getPageRangeSize() {
        return _pageRangeSize;
    }

    public void setPageRangeSize(int pageRangeSize) {
        this._pageRangeSize = pageRangeSize;
    }

    public boolean isFillLimit() {
        return _fillLimit;
    }

    public void setFillLimit(boolean fillLimit) {
        this._fillLimit = fillLimit;
    }
}
