package org.seasar.dbflute.cbean.pagenavi;

/**
 * The class of page number link.
 * @author jflute
 */
public class PageNumberLink implements java.io.Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected int _pageNumberElement;
    protected boolean _current;
    protected String _pageNumberLinkHref;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public PageNumberLink() {
    }

    // ===================================================================================
    //                                                                         Initializer
    //                                                                         ===========
    public PageNumberLink initialize(int pageNumberElement, boolean current, String pageNumberLinkHref) {
        setPageNumberElement(pageNumberElement);
        setCurrent(current);
        setPageNumberLinkHref(pageNumberLinkHref);
        return this;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * @return The view string of all attribute values. (NotNull)
     */
	 @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();

        sb.append(" pageNumberElement=").append(_pageNumberElement);
        sb.append(" pageNumberLinkHref=").append(_pageNumberLinkHref);
        sb.append(" current=").append(_current);

        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public int getPageNumberElement() {
        return _pageNumberElement;
    }

    public void setPageNumberElement(int pageNumberElement) {
        this._pageNumberElement = pageNumberElement;
    }

    public boolean isCurrent() {
        return _current;
    }

    public void setCurrent(boolean current) {
        this._current = current;
    }

    public String getPageNumberLinkHref() {
        return _pageNumberLinkHref;
    }

    public void setPageNumberLinkHref(String pageNumberLinkHref) {
        this._pageNumberLinkHref = pageNumberLinkHref;
    }
}
