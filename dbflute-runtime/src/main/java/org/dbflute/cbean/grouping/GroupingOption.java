package org.dbflute.cbean.grouping;

/**
 * The class of option for grouping.
 * @param  <ENTITY> The type of entity.
 * @author DBFlute(AutoGenerator)
 */
public class GroupingOption<ENTITY> {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected int _elementCount;

    protected GroupingRowEndDeterminer<ENTITY> _groupingRowEndDeterminer;

    // =====================================================================================
    //                                                                           Constructor
    //                                                                           ===========
    /**
     * Constructor. You should set the determiner of grouping row end after you create the instance.
     */
    public GroupingOption() {
    }

    /**
     * Constructor.
     * @param elementCount The count of row element in a group.
     */
    public GroupingOption(int elementCount) {
        _elementCount = elementCount;
    }

    // =====================================================================================
    //                                                                              Accessor
    //                                                                              ========
    public int getElementCount() {
        return this._elementCount;
    }

    public GroupingRowEndDeterminer<ENTITY> getGroupingRowEndDeterminer() {
        return this._groupingRowEndDeterminer;
    }

    public void setGroupingRowEndDeterminer(GroupingRowEndDeterminer<ENTITY> groupingRowEndDeterminer) {
        this._groupingRowEndDeterminer = groupingRowEndDeterminer;
    }
}
