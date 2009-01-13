package org.dbflute.cbean.pagenavi;

/**
 * The setupper of page number link.
 * @param <LINK> The type of link.
 * @author DBFlute(AutoGenerator)
 */
public interface PageNumberLinkSetupper<LINK extends PageNumberLink> {

    /**
     * Set up page number link.
     * @param pageNumberElement Page number element.
     * @param current Is current page?
     * @return Page number link. (NotNull)
     */
    public LINK setup(int pageNumberElement, boolean current);
}
