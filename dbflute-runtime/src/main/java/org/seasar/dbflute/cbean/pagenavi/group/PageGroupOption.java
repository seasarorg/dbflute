package org.seasar.dbflute.cbean.pagenavi.group;

/**
 * The option of page group.
 * @author DBFlute(AutoGenerator)
 */
public class PageGroupOption implements java.io.Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected int _pageGroupSize;

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * @return The view string of all attribute values. (NotNull)
     */
	@Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append(" pageGroupSize=").append(_pageGroupSize);

        return sb.toString();
    }
	
    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public int getPageGroupSize() {
        return _pageGroupSize;
    }

    public void setPageGroupSize(int pageGroupSize) {
        this._pageGroupSize = pageGroupSize;
    }
}
