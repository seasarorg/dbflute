package org.seasar.dbflute.s2dao.identity;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.seasar.dbflute.s2dao.beans.TnBeanDesc;
import org.seasar.dbflute.s2dao.beans.TnPropertyDesc;
import org.seasar.dbflute.s2dao.beans.factory.TnBeanDescFactory;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.DfStringUtil;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnIdentifierGeneratorFactory {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private static Map<String, Class<?>> generatorClasses = new HashMap<String, Class<?>>();

    static {
        addIdentifierGeneratorClass("assigned", TnIdentifierAssignedGenerator.class);
        addIdentifierGeneratorClass("identity", TnIdentifierIdentityGenerator.class);
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    private TnIdentifierGeneratorFactory() {
    }

    // ===================================================================================
    //                                                                Identifier Generator
    //                                                                ====================
    public static void addIdentifierGeneratorClass(String name, Class<?> clazz) {
        generatorClasses.put(name, clazz);
    }

    public static TnIdentifierGenerator createIdentifierGenerator(TnPropertyType propertyType) {
        return createIdentifierGenerator(propertyType, null);
    }

    public static TnIdentifierGenerator createIdentifierGenerator(TnPropertyType propertyType, String annotation) {
        if (propertyType == null) {
            String msg = "The argument[propertyType] should not be null: annotation=" + annotation;
            throw new IllegalArgumentException(msg);
        }
        if (annotation == null) {
            return new TnIdentifierAssignedGenerator(propertyType);
        }
        String[] array = DfStringUtil.split(annotation, "=, ");
        Class<?> clazz = getGeneratorClass(array[0]);
        TnIdentifierGenerator generator = createIdentifierGenerator(clazz, propertyType);
        for (int i = 1; i < array.length; i += 2) {
            setProperty(generator, array[i].trim(), array[i + 1].trim());
        }
        return generator;
    }

    protected static Class<?> getGeneratorClass(String name) {
        Class<?> clazz = generatorClasses.get(name);
        if (clazz != null) {
            return clazz;
        }
        return DfReflectionUtil.forName(name);
    }

    protected static TnIdentifierGenerator createIdentifierGenerator(Class<?> clazz, TnPropertyType propertyType) {
        Constructor<?> constructor = DfReflectionUtil.getConstructor(clazz, new Class<?>[] { TnPropertyType.class });
        return (TnIdentifierGenerator) DfReflectionUtil.newInstance(constructor, new Object[] { propertyType });
    }

    protected static void setProperty(TnIdentifierGenerator generator, String propertyName, String value) {
        TnBeanDesc beanDesc = TnBeanDescFactory.getBeanDesc(generator.getClass());
        TnPropertyDesc pd = beanDesc.getPropertyDesc(propertyName);
        pd.setValue(generator, value);
    }
}
