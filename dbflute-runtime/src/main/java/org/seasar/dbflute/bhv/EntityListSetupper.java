package org.seasar.dbflute.bhv;

import java.util.List;

import org.seasar.dbflute.Entity;


/**
 * The interface of entity list set-upper.
 * @param <ENTITY> The type of entity.
 * @author jflute
 */
public interface EntityListSetupper<ENTITY extends Entity> {

    /**
     * Set up the list of entity.
     * @param entityList The list of entity. (NotNull)
     */
    public void setup(List<ENTITY> entityList);
}
