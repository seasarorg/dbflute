/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.generate.column;

import java.util.Iterator;
import java.util.List;

import org.apache.torque.engine.database.model.Column;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfBasicProperties;

/**
 * @author jflute
 */
public class DfColumnListToStringUtil {

    public static String getColumnArgsString(List<Column> columnList) {
        validateColumnList(columnList);

        final StringBuilder sb = new StringBuilder();
        for (Iterator<Column> ite = columnList.iterator(); ite.hasNext();) {
            final Column pk = (Column) ite.next();
            final String javaNative;
            if (pk.isForceClassificationSetting()) {
                final DfBasicProperties prop = getBasicProperties();
                final String projectPrefix = prop.getProjectPrefix();
                final String classificationName = pk.getClassificationName();
                javaNative = projectPrefix + "CDef." + classificationName;
            } else {
                javaNative = pk.getJavaNative();
            }
            final String uncapitalisedJavaName = pk.getUncapitalisedJavaName();
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(javaNative).append(" ").append(uncapitalisedJavaName);
        }
        return sb.toString();
    }

    public static String getColumnArgsJavaDocString(List<Column> columnList, String ln) {
        validateColumnList(columnList);

        final StringBuilder sb = new StringBuilder();
        for (Iterator<Column> ite = columnList.iterator(); ite.hasNext();) {
            final Column pk = (Column) ite.next();
            final String uncapitalisedJavaName = pk.getUncapitalisedJavaName();
            if (sb.length() > 0) {
                sb.append(ln).append("     * ");
            }
            sb.append("@param ").append(uncapitalisedJavaName);
            sb.append(" The one of primary key. (NotNull)");
        }
        return sb.toString();
    }

    public static String getColumnArgsAssertString(List<Column> columnList) {
        return doGetColumnArgsAssertString(columnList, false);
    }

    public static String getColumnArgsAssertStringCSharp(List<Column> columnList) {
        return doGetColumnArgsAssertString(columnList, true);
    }

    private static String doGetColumnArgsAssertString(List<Column> columnList, boolean initCap) {
        validateColumnList(columnList);

        final StringBuilder sb = new StringBuilder();
        for (Iterator<Column> ite = columnList.iterator(); ite.hasNext();) {
            final Column pk = (Column) ite.next();
            final String uncapitalisedJavaName = pk.getUncapitalisedJavaName();
            sb.append(initCap ? "A" : "a").append("ssertObjectNotNull(\"");
            sb.append(uncapitalisedJavaName).append("\", ");
            sb.append(uncapitalisedJavaName).append(");");
        }
        return sb.toString();
    }

    public static String getColumnArgsSetupString(String beanName, List<Column> columnList) {
        validateColumnList(columnList);
        final String beanPrefix = (beanName != null ? beanName + "." : "");

        String result = "";
        for (Iterator<Column> ite = columnList.iterator(); ite.hasNext();) {
            Column pk = (Column) ite.next();
            final String javaName = pk.getJavaName();
            final String variable = pk.getUncapitalisedJavaName();
            final String setter = beanPrefix + "set" + javaName + "(" + variable + ");";
            if ("".equals(result)) {
                result = setter;
            } else {
                result = result + setter;
            }
        }
        return result;
    }

    public static String getColumnArgsSetupStringCSharp(String beanName, List<Column> columnList) {
        validateColumnList(columnList);
        final String beanPrefix = (beanName != null ? beanName + "." : "");

        String result = "";
        for (Iterator<Column> ite = columnList.iterator(); ite.hasNext();) {
            Column pk = (Column) ite.next();
            final String javaName = pk.getJavaName();
            final String variable = pk.getUncapitalisedJavaName();
            final String setter = beanPrefix + javaName + " = " + variable + ";";
            if ("".equals(result)) {
                result = setter;
            } else {
                result = result + setter;
            }
        }
        return result;
    }

    public static String getColumnArgsConditionSetupString(List<Column> columnList) {
        return doGetColumnArgsConditionSetupString(columnList, false);
    }

    public static String getColumnArgsConditionSetupStringCSharp(List<Column> columnList) {
        return doGetColumnArgsConditionSetupString(columnList, true);
    }

    private static String doGetColumnArgsConditionSetupString(List<Column> columnList, boolean csharp) {
        validateColumnList(columnList);

        final StringBuilder sb = new StringBuilder();
        for (Iterator<Column> ite = columnList.iterator(); ite.hasNext();) {
            Column pk = (Column) ite.next();
            final String javaName = pk.getJavaName();
            final String variable = pk.getUncapitalisedJavaName();
            final String setter;
            if (pk.isForceClassificationSetting()) {
                final String cls = pk.getClassificationName();
                if (csharp) {
                    setter = "cb.Query().Set" + javaName + "_Equal_As" + cls + "(" + variable + ");";
                } else {
                    setter = "cb.query().set" + javaName + "_Equal_As" + cls + "(" + variable + ");";
                }
            } else {
                if (csharp) {
                    setter = "cb.Query().Set" + javaName + "_Equal(" + variable + ");";
                } else {
                    setter = "cb.query().set" + javaName + "_Equal(" + variable + ");";
                }
            }
            sb.append(setter);
        }
        return sb.toString();
    }

    public static String getColumnNameCommaString(List<Column> columnList) {
        validateColumnList(columnList);

        String result = "";
        for (Iterator<Column> ite = columnList.iterator(); ite.hasNext();) {
            final Column col = (Column) ite.next();
            final String name = col.getName();
            if ("".equals(result)) {
                result = name;
            } else {
                result = result + ", " + name;
            }
        }
        return result;
    }

    public static String getColumnJavaNameCommaString(List<Column> columnList) {
        validateColumnList(columnList);

        String result = "";
        for (Iterator<Column> ite = columnList.iterator(); ite.hasNext();) {
            final Column col = (Column) ite.next();
            final String name = col.getJavaName();
            if ("".equals(result)) {
                result = name;
            } else {
                result = result + ", " + name;
            }
        }
        return result;
    }

    public static String getColumnUncapitalisedJavaNameCommaString(List<Column> columnList) {
        validateColumnList(columnList);

        String result = "";
        for (Iterator<Column> ite = columnList.iterator(); ite.hasNext();) {
            final Column col = (Column) ite.next();
            final String name = col.getUncapitalisedJavaName();
            if ("".equals(result)) {
                result = name;
            } else {
                result = result + ", " + name;
            }
        }
        return result;
    }

    public static String getColumnGetterCommaString(List<Column> columnList) {
        validateColumnList(columnList);

        String result = "";
        for (Iterator<Column> ite = columnList.iterator(); ite.hasNext();) {
            final Column col = (Column) ite.next();
            final String javaName = col.getJavaName();
            final String getterString = "get" + javaName + "()";
            if ("".equals(result)) {
                result = getterString;
            } else {
                result = result + ", " + getterString;
            }
        }
        return result;
    }

    public static String getColumnOrderByString(List<Column> columnList, String sortString) {
        validateColumnList(columnList);

        final StringBuilder sb = new StringBuilder();
        for (Iterator<Column> ite = columnList.iterator(); ite.hasNext();) {
            final Column pk = (Column) ite.next();
            final String name = pk.getName();
            if ("".equals(sb.toString())) {
                sb.append(name).append(" ").append(sortString);
            } else {
                sb.append(", ").append(name).append(" ").append(sortString);
            }
        }
        return sb.toString();
    }

    public static String getColumnDispValueString(List<Column> columnList, String getterPrefix) {
        validateColumnList(columnList);

        String result = "";
        for (Iterator<Column> ite = columnList.iterator(); ite.hasNext();) {
            Column pk = (Column) ite.next();
            final String javaName = pk.getJavaName();
            final String getterString = getterPrefix + javaName + "()";
            if ("".equals(result)) {
                result = getterString;
            } else {
                result = result + " + \"-\" + " + getterString;
            }
        }
        return result;
    }

    private static DfBasicProperties getBasicProperties() {
        return DfBuildProperties.getInstance().getBasicProperties();
    }

    private static void validateColumnList(List<Column> columnList) {
        if (columnList == null) {
            String msg = "The columnList is null.";
            throw new IllegalStateException(msg);
        }
    }
}