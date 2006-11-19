package org.seasar.dbflute.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DfClassUtil {

    public static Class forName(String className) {
        if (className == null) {
            throw new IllegalArgumentException("Argument[className] must not be null.");
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("className=" + className, e);
        }
    }

    public static Object newInstance(String className) {
        return newInstance(forName(className));
    }

    public static Object newInstance(Class clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Class=" + clazz, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Class=" + clazz, e);
        }
    }

    public static Object newInstance(Constructor constructor, Object[] args) {
        try {
            return constructor.newInstance(args);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Constructor=" + constructor, e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Constructor=" + constructor, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Constructor=" + constructor, e);
        }
    }

    public static String getShortClassName(Class clazz) {
        String s = clazz.getName();
        int i = s.lastIndexOf('.');
        if (i > 0) {
            return s.substring(i + 1);
        } else {
            return s;
        }
    }

    public static String getPackageName(Object object, String valueIfNull) {
        if (object == null)
            return valueIfNull;
        else
            return getPackageName(object.getClass().getName());
    }

    public static String getPackageName(Class cls) {
        if (cls == null)
            throw new IllegalArgumentException("The class must not be null");
        else
            return getPackageName(cls.getName());
    }

    public static String getPackageName(String className) {
        if (isEmpty(className))
            throw new IllegalArgumentException("The class name must not be empty");
        int i = className.lastIndexOf('.');
        if (i == -1)
            return "";
        else
            return className.substring(0, i);
    }

    protected static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static String getClassNameFromFile(File classFile, String classesDirectoryName, String fileSeparator) {
        String tmp = classFile.getPath();
        if (!tmp.endsWith(".class")) {
            String msg = "The class file is not class file: " + classFile.getPath();
            throw new IllegalArgumentException(msg);
        }

        tmp = tmp.substring(tmp.indexOf(classesDirectoryName) + classesDirectoryName.length() + 1);
        tmp = tmp.substring(0, tmp.indexOf(".class"));

        if (fileSeparator.equals("\\")) {
            tmp = tmp.replaceAll("\\" + fileSeparator, ".");
        } else {
            tmp = tmp.replaceAll(fileSeparator, ".");
        }

        return tmp;
    }

    public static boolean isImplementsInterface(Class clazz, Class interfaze) {
        if (clazz == null) {
            String msg = "Argument[clazz] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        if (interfaze == null) {
            String msg = "Argument[interfaze] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        final Class[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            final Class currentInterface = interfaces[i];

            if (currentInterface.equals(interfaze)) {
                return true;
            }

        }
        final Class superClass = clazz.getSuperclass();
        if (superClass != null && !superClass.equals(clazz)) {
            return isImplementsInterface(superClass, interfaze);
        }
        return false;
    }
}