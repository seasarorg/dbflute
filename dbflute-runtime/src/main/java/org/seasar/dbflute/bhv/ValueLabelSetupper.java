package org.seasar.dbflute.bhv;

import org.seasar.dbflute.Entity;

/**
 * The interface of Value-Label Setupper.
 * @param <ENTITY> The type of entity.
 * @author jflute
 * @deprecated Sorry! This class will be deleted at the future.
 */
public interface ValueLabelSetupper<ENTITY extends Entity> {

    /**
     * Set up value-label.
     * @param box Value-label box. (NotNull)
     * @param entity Entity. (NotNull)
     */
    public void setup(ValueLabelBox box, ENTITY entity);
}
