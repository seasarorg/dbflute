package org.seasar.dbflute.bhv.batch;

import java.util.List;

import org.seasar.dbflute.Entity;

/**
 * @author jflute
 */
public class TokenFileOutputResult {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<Entity> _selectedList;

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<Entity> getSelectedList() {
        return _selectedList;
    }

    public void setSelectedList(List<Entity> selectedList) {
        _selectedList = selectedList;
    }
}
