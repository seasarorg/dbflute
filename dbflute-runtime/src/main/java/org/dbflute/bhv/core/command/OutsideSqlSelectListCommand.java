package org.dbflute.bhv.core.command;

import java.util.List;

import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.dbflute.s2dao.valuetype.TnValueType;
import org.dbflute.s2dao.valuetype.TnValueTypes;
import org.seasar.extension.jdbc.ResultSetHandler;

/**
 * The behavior command for OutsideSql.selectList().
 * @author DBFlute(AutoGenerator)
 * @param <ENTITY> The type of entity.
 */
public class OutsideSqlSelectListCommand<ENTITY> extends AbstractOutsideSqlSelectCommand<List<ENTITY>> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The type of entity. (Required) */
    protected Class<ENTITY> _entityType;

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "selectList";
    }

    public Class<?> getCommandReturnType() {
        return List.class;
    }

    // ===================================================================================
    //                                                                             Factory
    //                                                                             =======
    // -----------------------------------------------------
    //                                          BeanMetaData
    //                                          ------------
    protected TnBeanMetaData createBeanMetaData() {
        assertEntityType("createBeanMetaData");
        return _beanMetaDataFactory.createBeanMetaData(_entityType);
    }

    // -----------------------------------------------------
    //                                      ResultSetHandler
    //                                      ----------------
    protected ResultSetHandler createOutsideSqlBeanListResultSetHandler(TnBeanMetaData bmd) {
        final TnValueType valueType = TnValueTypes.getValueType(_entityType);
        if (valueType == null || !valueType.equals(TnValueTypes.OBJECT)) {
            return createObjectListResultSetHandler(valueType);
        }
        return createBeanListMetaDataResultSetHandler(bmd);
    }

    // ===================================================================================
    //                                                                     Extension Point
    //                                                                     ===============
    @Override
    protected ResultSetHandler createOutsideSqlSelectResultSetHandler() {
        final TnBeanMetaData bmd = createBeanMetaData();
        final ResultSetHandler handler = createOutsideSqlBeanListResultSetHandler(bmd);
        return handler;
    }

    @Override
    protected Object getResultTypeSpecification() {
        return _entityType;
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertEntityType(String methodName) {
        if (_entityType == null) {
            throw new IllegalStateException(buildAssertMessage("_entityType", methodName));
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setEntityType(Class<ENTITY> entityType) {
        _entityType = entityType;
    }
}
