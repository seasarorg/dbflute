package org.seasar.dbflute.bhv.batch;

import org.seasar.dbflute.Entity;

/**
 * @author jflute
 */
public class TokenFileOutputResult {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected java.util.List<Entity> _selectedList;

    // =====================================================================================
    //                                                                              Accessor
    //                                                                              ========
    public java.util.List<Entity> getSelectedList() {
        return _selectedList;
    }

    public void setSelectedList(java.util.List<Entity> selectedList) {
        _selectedList = selectedList;
    }
}
