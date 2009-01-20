package org.seasar.dbflute.s2dao.rshandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.seasar.dbflute.resource.ResourceContext;
import org.seasar.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.dbflute.s2dao.metadata.TnDtoMetaData;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.dbflute.s2dao.rowcreator.TnRowCreator;

/**
 * @author jflute
 */
@SuppressWarnings("unchecked")
public abstract class TnAbstractDtoMetaDataResultSetHandler implements TnResultSetHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private TnDtoMetaData dtoMetaData;
    protected TnRowCreator rowCreator; // [DAO-118] (2007/08/25)

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param dtoMetaData Dto meta data. (NotNull)
     * @param rowCreator Row creator. (NotNull)
     */
    public TnAbstractDtoMetaDataResultSetHandler(TnDtoMetaData dtoMetaData, TnRowCreator rowCreator) {
        this.dtoMetaData = dtoMetaData;
        this.rowCreator = rowCreator;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    /**
     * @param columnNames The set of column name. (NotNull)
     * @return The map of row property cache. Map{String(columnName), PropertyType} (NotNull)
     * @throws SQLException
     */
    protected Map<String, TnPropertyType> createPropertyCache(Set<String> columnNames) throws SQLException {
        return rowCreator.createPropertyCache(columnNames, dtoMetaData);
    }

    /**
     * @param rs Result set. (NotNull)
     * @param propertyCache The map of property cache. Map{String(columnName), PropertyType} (NotNull)
     * @return Created row. (NotNull)
     * @throws SQLException
     */
    protected Object createRow(ResultSet rs, Map<String, TnPropertyType> propertyCache) throws SQLException {
        final Class beanClass = dtoMetaData.getBeanClass();
        return rowCreator.createRow(rs, propertyCache, beanClass);
    }

    protected Set<String> createColumnNames(ResultSet rs) throws SQLException {
        return ResourceContext.createSelectColumnNames(rs);
    }

    public TnDtoMetaData getDtoMetaData() {
        return dtoMetaData;
    }
}
