package org.dbflute.s2dao.rshandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.dbflute.helper.StringSet;
import org.dbflute.s2dao.metadata.TnDtoMetaData;
import org.dbflute.s2dao.rowcreator.TnRowCreator;
import org.dbflute.s2dao.metadata.TnPropertyType;
import org.dbflute.s2dao.jdbc.ResultSetHandler;


/**
 * @author DBFlute(AutoGenerator)
 */
@SuppressWarnings("unchecked")
public abstract class TnAbstractDtoMetaDataResultSetHandler implements ResultSetHandler {

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

    protected Set<String> createColumnNames(final ResultSetMetaData rsmd) throws SQLException {
        final int count = rsmd.getColumnCount();
        final Set<String> columnNames = StringSet.createAsCaseInsensitive();
        for (int i = 0; i < count; ++i) {
            final String columnName = rsmd.getColumnLabel(i + 1);
            final int pos = columnName.lastIndexOf('.'); // [DAO-41]
            if (-1 < pos) {
                columnNames.add(columnName.substring(pos + 1));
            } else {
                columnNames.add(columnName);
            }
        }
        return columnNames;
    }

    public TnDtoMetaData getDtoMetaData() {
        return dtoMetaData;
    }
}
