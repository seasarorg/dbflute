package org.dbflute.s2dao.procedure;

import java.lang.reflect.Field;

import org.dbflute.jdbc.TnValueType;

/**
 * @author DBFlute(AutoGenerator)
 */
public class TnProcedureParameterType {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String parameterName;
    private Integer parameterIndex;
    private Field field;
    private TnValueType valueType;
    private boolean inType;
    private boolean outType;
    private boolean returnType;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnProcedureParameterType(Field field) {
        this.field = field;
        this.parameterName = field.getName();
    }

    // ===================================================================================
    //                                                                         Field Value
    //                                                                         ===========
    public Object getValue(Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            String msg = "The getting of the field threw the exception:";
            msg = msg + " class=" + field.getDeclaringClass().getSimpleName();
            msg = msg + " field=" + field.getName();
            throw new IllegalStateException(msg, e);
        }
    }

    public void setValue(Object target, Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            String msg = "The setting of the field threw the exception:";
            msg = msg + " class=" + field.getDeclaringClass().getSimpleName();
            msg = msg + " field=" + field.getName();
            throw new IllegalStateException(msg, e);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getParameterName() {
        return parameterName;
    }
    
    public Integer getParameterIndex() {
        return parameterIndex;
    }
    
    public void setParameterIndex(Integer parameterIndex) {
        this.parameterIndex = parameterIndex;
    }

    public TnValueType getValueType() {
        return valueType;
    }

    public void setValueType(final TnValueType valueType) {
        this.valueType = valueType;
    }

    public boolean isInType() {
        return inType;
    }

    public void setInType(final boolean inType) {
        this.inType = inType;
    }

    public boolean isOutType() {
        return outType;
    }

    public void setOutType(final boolean outType) {
        this.outType = outType;
    }

    public boolean isReturnType() {
        return returnType;
    }

    public void setReturnType(final boolean returnType) {
        this.returnType = returnType;
    }
}
