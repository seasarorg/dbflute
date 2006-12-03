package org.seasar.dbflute;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class DfDBFluteProvider {

    public static S2Container _container;

    public static Object getComponent(Class type) {
        initializeIfNeed(type);
        return _container.getComponent(type);
    }

    public static void initializeIfNeed(Class type) {
        if (_container == null) {
            final ClassLoader classLoader = type.getClassLoader();
            final String configPath = SingletonS2ContainerFactory.getConfigPath();
            _container = S2ContainerFactory.create(configPath, classLoader);
        }
    }
}