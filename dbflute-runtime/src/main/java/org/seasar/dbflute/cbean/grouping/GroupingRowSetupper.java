package org.seasar.dbflute.cbean.grouping;

/**
 * The setupper of grouping row.
 * @param <ROW> The type of row.
 * @param <ENTITY> The type of entity.
 * @author jflute
 */
public interface GroupingRowSetupper<ROW, ENTITY> {

    /**
     * Set up the instance of grouping row.
     * @param groupingRowResource Grouping row resource. (NotNull)
     * @return The instance of grouping row. (NotNull)
     */
    public ROW setup(GroupingRowResource<ENTITY> groupingRowResource);
}
