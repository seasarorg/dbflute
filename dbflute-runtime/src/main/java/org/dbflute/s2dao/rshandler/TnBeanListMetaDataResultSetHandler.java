package org.dbflute.s2dao.rshandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.ConditionBeanContext;
import org.dbflute.outsidesql.OutsideSqlContext;
import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.dbflute.s2dao.metadata.TnRelationPropertyType;
import org.dbflute.s2dao.rowcreator.TnRelationRowCreator;
import org.dbflute.s2dao.rowcreator.TnRowCreator;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.framework.beans.PropertyDesc;

/**
 * @author DBFlute(AutoGenerator)
 */
public class TnBeanListMetaDataResultSetHandler extends TnAbstractBeanMetaDataResultSetHandler {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param beanMetaData Bean meta data. (NotNull)
     * @param rowCreator Row creator. (NotNull)
     * @param relationRowCreator Relation row creator. (NotNul)
     */
    public TnBeanListMetaDataResultSetHandler(TnBeanMetaData beanMetaData, TnRowCreator rowCreator,
            TnRelationRowCreator relationRowCreator) {
        super(beanMetaData, rowCreator, relationRowCreator);
    }

    // ===================================================================================
    //                                                                              Handle
    //                                                                              ======
    public Object handle(ResultSet rs) throws SQLException {
        // Lazy initialization because if the result is zero, the resources are unused.
        Set<String> columnNames = null; // Set<String(columnName)>
        Map<String, PropertyType> propertyCache = null; // Map<String(columnName), PropertyType>
        Map<String, Map<String, PropertyType>> relationPropertyCache = null; // Map<String(relationNoSuffix), Map<String(columnName), PropertyType>>
        TnRelationRowCache relRowCache = null;

        final List<Object> list = new ArrayList<Object>();
        final int relSize = getBeanMetaData().getRelationPropertyTypeSize();
        final boolean hasCB = hasConditionBean();
        final boolean skipRelationLoop;
        {
            final boolean emptyRelation = isSelectedForeignInfoEmpty();
            final boolean hasOSC = hasOutsideSqlContext();
            final boolean specifiedOutsideSql = isSpecifiedOutsideSql();

            // If it has condition-bean that has no relation to get
            // or it has outside-sql context that is specified-outside-sql,
            // they are unnecessary to do relation loop!
            skipRelationLoop = (hasCB && emptyRelation) || (hasOSC && specifiedOutsideSql);
        }

        while (rs.next()) {
            if (columnNames == null) {
                columnNames = createColumnNames(rs.getMetaData());
            }
            if (propertyCache == null) {
                propertyCache = createPropertyCache(columnNames);
            }

            // Create row instance of base table by row property cache.
            final Object row = createRow(rs, propertyCache);

            // If it has condition-bean that has no relation to get
            // or it has outside SQL context that is specified outside SQL,
            // they are unnecessary to do relation loop!
            if (skipRelationLoop) {
                postCreateRow(row);
                list.add(row);
                continue;
            }

            if (relationPropertyCache == null) {
                relationPropertyCache = createRelationPropertyCache(columnNames);
            }
            if (relRowCache == null) {
                relRowCache = new TnRelationRowCache(relSize);
            }
            for (int i = 0; i < relSize; ++i) {
                final TnRelationPropertyType rpt = getBeanMetaData().getRelationPropertyType(i);
                if (rpt == null) {
                    continue;
                }

                // Do only selected foreign property for performance if condition-bean exists.
                if (hasCB && !hasSelectedForeignInfo(buildRelationNoSuffix(rpt))) {
                    continue;
                }

                final Map<String, Object> relKeyValues = new HashMap<String, Object>();
                final TnRelationKey relKey = createRelationKey(rs, rpt, columnNames, relKeyValues);
                Object relationRow = null;
                if (relKey != null) {
                    relationRow = relRowCache.getRelationRow(i, relKey);
                    if (relationRow == null) { // when no cache
                        relationRow = createRelationRow(rs, rpt, columnNames, relKeyValues, relationPropertyCache);
                        if (relationRow != null) {
                            relRowCache.addRelationRow(i, relKey, relationRow);
                            postCreateRow(relationRow);
                        }
                    }
                }
                if (relationRow != null) {
                    final PropertyDesc pd = rpt.getPropertyDesc();
                    pd.setValue(row, relationRow);
                }
            }
            postCreateRow(row);
            list.add(row);
        }
        return list;
    }

    protected TnRelationKey createRelationKey(ResultSet rs, TnRelationPropertyType rpt, Set<String> columnNames,
            Map<String, Object> relKeyValues) throws SQLException {
        final List<Object> keyList = new ArrayList<Object>();
        final TnBeanMetaData bmd = rpt.getBeanMetaData();
        for (int i = 0; i < rpt.getKeySize(); ++i) {
            final ValueType valueType;
            String columnName = rpt.getMyKey(i);
            if (columnNames.contains(columnName)) {
                final PropertyType pt = getBeanMetaData().getPropertyTypeByColumnName(columnName);
                valueType = pt.getValueType();
            } else {
                final PropertyType pt = bmd.getPropertyTypeByColumnName(rpt.getYourKey(i));
                columnName = pt.getColumnName() + buildRelationNoSuffix(rpt);
                if (columnNames.contains(columnName)) {
                    valueType = pt.getValueType();
                } else {
                    return null;
                }
            }
            final Object value = valueType.getValue(rs, columnName);
            if (value == null) {
                return null;
            }
            relKeyValues.put(columnName, value);
            keyList.add(value);
        }
        if (keyList.size() > 0) {
            Object[] keys = keyList.toArray();
            return new TnRelationKey(keys);
        } else {
            return null;
        }
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected boolean hasConditionBean() {
        return ConditionBeanContext.isExistConditionBeanOnThread();
    }

    protected boolean isSelectedForeignInfoEmpty() {
        if (!hasConditionBean()) {
            return true;
        }
        ConditionBean cb = ConditionBeanContext.getConditionBeanOnThread();
        if (cb.getSqlClause().isSelectedForeignInfoEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Has it selected foreign information?
     * You should call hasConditionBean() before calling this!
     * @param relationNoSuffix The suffix of relation NO. (NotNull)
     * @return Determination.
     */
    protected boolean hasSelectedForeignInfo(String relationNoSuffix) {
        final ConditionBean cb = ConditionBeanContext.getConditionBeanOnThread();
        if (cb.getSqlClause().hasSelectedForeignInfo(relationNoSuffix)) {
            return true;
        }
        return false;
    }

    /**
     * Build the string of relation No suffix.
     * @param rpt The property type of relation. (NotNull)
     * @return The string of relation No suffix. (NotNull)
     */
    protected String buildRelationNoSuffix(TnRelationPropertyType rpt) {
        return "_" + rpt.getRelationNo();
    }

    protected boolean hasOutsideSqlContext() {
        return OutsideSqlContext.isExistOutsideSqlContextOnThread();
    }

    protected boolean isSpecifiedOutsideSql() {
        if (!hasOutsideSqlContext()) {
            return false;
        }
        final OutsideSqlContext context = OutsideSqlContext.getOutsideSqlContextOnThread();
        return context.isSpecifiedOutsideSql();
    }
}
