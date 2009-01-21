/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.util;

import java.util.Map;

import ognl.ClassResolver;
import ognl.Ognl;
import ognl.OgnlException;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class DfOgnlUtil {

    protected DfOgnlUtil() {
    }

    public static Object getValue(Object exp, Object root) {
        return getValue(exp, root, null, 0);
    }

    public static Object getValue(Object exp, Object root, String path, int lineNumber) {
        return getValue(exp, null, root, path, lineNumber);
    }

    @SuppressWarnings("unchecked")
    public static Object getValue(Object exp, Map ctx, Object root) {
        return getValue(exp, ctx, root, null, 0);
    }

    @SuppressWarnings("unchecked")
    public static Object getValue(Object exp, Map ctx, Object root, String path, int lineNumber) {
        try {
            Map newCtx = addClassResolverIfNecessary(ctx, root);
            if (newCtx != null) {
                return Ognl.getValue(exp, newCtx, root);
            } else {
                return Ognl.getValue(exp, root);
            }
        } catch (OgnlException e) {
            throwOgnlGetValueException(exp, path, lineNumber, ctx, e);
            return null; // Unreachable!
        } catch (Exception e) {
            throwOgnlGetValueException(exp, path, lineNumber, ctx, e);
            return null; // Unreachable!
        }
    }

    @SuppressWarnings("unchecked")
    protected static void throwOgnlGetValueException(Object expression, String path, int lineNumber, Map ctx,
            OgnlException e) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The Ognl.getValue() threw the exception!!" + ln();
        msg = msg + ln();
        msg = msg + "[Expression]" + ln() + expression + ln();
        msg = msg + ln();
        msg = msg + "[Path]" + ln() + path + ln();
        msg = msg + ln();
        msg = msg + "[Line Number]" + ln() + lineNumber + ln();
        msg = msg + ln();
        msg = msg + "[Context]" + ln() + ctx + ln();
        msg = msg + ln();
        msg = msg + "[Reason]" + ln() + e.getReason() + ln();
        msg = msg + ln();
        msg = msg + "[Cause Exception]" + ln() + e.getMessage() + ln();
        msg = msg + "* * * * * * * * * */";
        throw new OgnlGetValueException(msg, e);
    }

    @SuppressWarnings("unchecked")
    protected static void throwOgnlGetValueException(Object expression, String path, int lineNumber, Map ctx,
            Exception e) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The Ognl.getValue() threw the exception!!" + ln();
        msg = msg + ln();
        msg = msg + "[Expression]" + ln() + expression + ln();
        msg = msg + ln();
        msg = msg + "[Path]" + ln() + path + ln();
        msg = msg + ln();
        msg = msg + "[Line Number]" + ln() + lineNumber + ln();
        msg = msg + ln();
        msg = msg + "[Context]" + ln() + ctx + ln();
        msg = msg + ln();
        msg = msg + "[Cause Exception]" + ln() + e.getMessage() + ln();
        msg = msg + "* * * * * * * * * */";
        throw new OgnlGetValueException(msg, e);
    }

    public static class OgnlGetValueException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public OgnlGetValueException(String msg, Throwable e) {
            super(msg, e);
        }
    }

    public static Object parseExpression(String expression) {
        return parseExpression(expression, null, 0);
    }

    public static Object parseExpression(String expression, String path, int lineNumber) {
        try {
            return Ognl.parseExpression(expression);
        } catch (Exception e) {
            throwOgnlParseExpressionException(expression, path, lineNumber, e);
            return null; // Unreachable!
        }
    }

    protected static void throwOgnlParseExpressionException(String expression, String path, int lineNumber, Exception e) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The Ognl.parseExpression() threw the exception!!" + ln();
        msg = msg + ln();
        msg = msg + "[Expression]" + ln() + expression + ln();
        msg = msg + ln();
        msg = msg + "[Path]" + ln() + path + ln();
        msg = msg + ln();
        msg = msg + "[Line Number]" + ln() + lineNumber + ln();
        msg = msg + ln();
        msg = msg + "[Cause Exception]" + ln() + e.getMessage() + ln();
        msg = msg + "* * * * * * * * * */";
        throw new OgnlParseExpressionException(msg, e);
    }

    public static class OgnlParseExpressionException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public OgnlParseExpressionException(String msg, Throwable e) {
            super(msg, e);
        }
    }

    @SuppressWarnings("unchecked")
    static Map addClassResolverIfNecessary(Map ctx, Object root) {
        // Is it necessary???
        //        if (root instanceof S2Container) {
        //            S2Container container = (S2Container) root;
        //            ClassLoader classLoader = container.getClassLoader();
        //            if (classLoader != null) {
        //                ClassResolverImpl classResolver = new ClassResolverImpl(classLoader);
        //                if (ctx == null) {
        //                    ctx = Ognl.createDefaultContext(root, classResolver);
        //                } else {
        //                    ctx = Ognl.addDefaultContext(root, classResolver, ctx);
        //                }
        //            }
        //        }
        return ctx;
    }

    public static class ClassResolverImpl implements ClassResolver {
        final private ClassLoader classLoader;

        public ClassResolverImpl(ClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        @SuppressWarnings("unchecked")
        public Class<?> classForName(String className, Map ctx) throws ClassNotFoundException {
            try {
                return classLoader.loadClass(className);
            } catch (ClassNotFoundException ex) {
                int dot = className.indexOf('.');
                if (dot < 0) {
                    return classLoader.loadClass("java.lang." + className);
                } else {
                    throw ex;
                }
            }
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected static String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
