package org.seasar.dbflute.cbean.mapping;

/**
 * The mapper of entity-to-dto.
 * @param <ENTITY> The type of entity.
 * @param <DTO> The type of dto.
 * @author DBFlute(AutoGenerator)
 */
public interface EntityDtoMapper<ENTITY, DTO> {

    /**
     * Map entity to data transfer object.
     * @param entity Entity. (NotNull)
     * @return Data transfer object. (NotNull)
     */
    public DTO map(ENTITY entity);
}
