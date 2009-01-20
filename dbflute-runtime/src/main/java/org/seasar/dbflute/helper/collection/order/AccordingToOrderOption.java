package org.seasar.dbflute.helper.collection.order;

import java.util.List;

/**
 * @author jflute
 * @param <ELEMENT_TYPE> The type of element.
 * @param <ID_TYPE> The type of ID.
 */
public class AccordingToOrderOption<ELEMENT_TYPE, ID_TYPE> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<ID_TYPE> _orderedUniqueIdList;

    protected AccordingToOrderIdExtractor<ELEMENT_TYPE, ID_TYPE> _idExtractor;

    // ===================================================================================
    //                                                                         Easy-to-Use
    //                                                                         ===========
    public void setupOrderedResource(List<ID_TYPE> orderedUniqueIdList, AccordingToOrderIdExtractor<ELEMENT_TYPE, ID_TYPE> idExtractor) {
        setOrderedUniqueIdList(orderedUniqueIdList);
        setIdExtractor(idExtractor);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<ID_TYPE> getOrderedUniqueIdList() {
        return _orderedUniqueIdList;
    }

    public void setOrderedUniqueIdList(List<ID_TYPE> orderedUniqueIdList) {
        this._orderedUniqueIdList = orderedUniqueIdList;
    }

    public AccordingToOrderIdExtractor<ELEMENT_TYPE, ID_TYPE> getIdExtractor() {
        return _idExtractor;
    }

    public void setIdExtractor(AccordingToOrderIdExtractor<ELEMENT_TYPE, ID_TYPE> idExtractor) {
        _idExtractor = idExtractor;
    }
}
