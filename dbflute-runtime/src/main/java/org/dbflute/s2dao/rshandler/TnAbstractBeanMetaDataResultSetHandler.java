package org.dbflute.s2dao.rshandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.dbflute.Entity;
import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.dbflute.s2dao.metadata.TnRelationPropertyType;
import org.dbflute.s2dao.rowcreator.TnRelationRowCreator;
import org.dbflute.s2dao.rowcreator.TnRowCreator;
import org.dbflute.s2dao.metadata.TnPropertyType;

/**
 * @author DBFlute(AutoGenerator)
 */
public abstract class TnAbstractBeanMetaDataResultSetHandler extends TnAbstractDtoMetaDataResultSetHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private TnBeanMetaData beanMetaData;
    protected TnRelationRowCreator relationRowCreator;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param beanMetaData Bean meta data. (NotNull)
     * @param rowCreator Row creator. (NotNull)
     * @param relationRowCreator Relation row creator. (NotNul)
     */
    public TnAbstractBeanMetaDataResultSetHandler(TnBeanMetaData beanMetaData, TnRowCreator rowCreator,
            TnRelationRowCreator relationRowCreator) {
        super(beanMetaData, rowCreator);
        this.beanMetaData = beanMetaData;
        this.relationRowCreator = relationRowCreator;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    /**
     * @param columnNames The set of column name. (NotNull)
     * @return The map of row property cache. Map{String(columnName), PropertyType} (NotNull)
     * @throws SQLException
     */
    @Override
    protected Map<String, TnPropertyType> createPropertyCache(Set<String> columnNames) throws SQLException {
        // - - - - - - - - -
        // Override for Bean
        // - - - - - - - - -
        return rowCreator.createPropertyCache(columnNames, beanMetaData);
    }

    /**
     * @param rs Result set. (NotNull)
     * @param propertyCache The map of property cache. Map{String(columnName), PropertyType} (NotNull)
     * @return Created row. (NotNull)
     * @throws SQLException
     */
    @Override
    protected Object createRow(ResultSet rs, Map<String, TnPropertyType> propertyCache) throws SQLException {
        // - - - - - - - - -
        // Override for Bean
        // - - - - - - - - -
        final Class<?> beanClass = beanMetaData.getBeanClass();
        return rowCreator.createRow(rs, propertyCache, beanClass);
    }

    /**
     * @param columnNames The set of column name. (NotNull)
     * @return The map of relation property cache. Map{String(relationNoSuffix), Map{String(columnName), PropertyType}} (NotNull)
     * @throws SQLException
     */
    protected Map<String, Map<String, TnPropertyType>> createRelationPropertyCache(Set<String> columnNames) throws SQLException {
        return relationRowCreator.createPropertyCache(columnNames, beanMetaData);
    }

    /**
     * @param rs Result set. (NotNull)
     * @param rpt The type of relation property. (NotNull)
     * @param columnNames The set of column name. (NotNull)
     * @param relKeyValues The map of relation key values. (Nullable)
     * @param relationPropertyCache The map of relation property cache. Map{String(relationNoSuffix), Map{String(columnName), PropertyType}} (NotNull)
     * @return Created relation row. (Nullable)
     * @throws SQLException
     */
    protected Object createRelationRow(ResultSet rs, TnRelationPropertyType rpt, Set<String> columnNames,
            Map<String, Object> relKeyValues, Map<String, Map<String, TnPropertyType>> relationPropertyCache)
            throws SQLException {
        return relationRowCreator.createRelationRow(rs, rpt, columnNames, relKeyValues, relationPropertyCache);
    }

    /**
     * @param row The row of result list. (NotNull)
     */
    protected void postCreateRow(final Object row) {
        if (row instanceof Entity) { // DBFlute Target
            ((Entity) row).clearModifiedPropertyNames();
        } else { // Basically Unreachable
            final TnBeanMetaData bmd = getBeanMetaData();
            final Set<String> names = bmd.getModifiedPropertyNames(row);
            names.clear();
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public TnBeanMetaData getBeanMetaData() {
        return beanMetaData;
    }
}
