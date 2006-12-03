package org.seasar.dbflute;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;

public class DfConfigProvider {

    public static S2Container _container;

    public static Object getComponent(Class type) {
        try {
            initializeIfNeed(type);
            return _container.getComponent(type);
        } catch (Exception e) {
            return null;
        }
    }

    public static void initializeIfNeed(Class type) {
        if (_container == null) {
            final ClassLoader classLoader = type.getClassLoader();
            final String configPath = "config.dicon";
            _container = S2ContainerFactory.create(configPath, classLoader);
        }
    }
}