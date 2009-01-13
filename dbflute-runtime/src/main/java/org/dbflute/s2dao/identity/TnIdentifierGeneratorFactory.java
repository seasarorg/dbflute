package org.dbflute.s2dao.identity;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.dbflute.util.SimpleStringUtil;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.ConstructorUtil;

/**
 * @author DBFlute(AutoGenerator)
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

    public static TnIdentifierGenerator createIdentifierGenerator(PropertyType propertyType) {
        return createIdentifierGenerator(propertyType, null);
    }

    public static TnIdentifierGenerator createIdentifierGenerator(PropertyType propertyType, String annotation) {
        if (propertyType == null) {
            String msg = "The argument[propertyType] should not be null: annotation=" + annotation;
            throw new IllegalArgumentException(msg);
        }
        if (annotation == null) {
            return new TnIdentifierAssignedGenerator(propertyType);
        }
        String[] array = SimpleStringUtil.split(annotation, "=, ");
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
        return ClassUtil.forName(name);
    }

    protected static TnIdentifierGenerator createIdentifierGenerator(Class<?> clazz, PropertyType propertyType) {
        Constructor<?> constructor = ClassUtil.getConstructor(clazz, new Class<?>[] { PropertyType.class });
        return (TnIdentifierGenerator) ConstructorUtil.newInstance(constructor, new Object[] { propertyType });
    }

    protected static void setProperty(TnIdentifierGenerator generator, String propertyName, String value) {
        BeanDesc beanDesc = BeanDescFactory.getBeanDesc(generator.getClass());
        PropertyDesc pd = beanDesc.getPropertyDesc(propertyName);
        pd.setValue(generator, value);
    }
}
