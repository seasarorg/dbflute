/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.sql2entity.pmbean;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfOutsideSqlProperties;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.Srl;
import org.seasar.dbflute.util.Srl.ScopeInfo;

/**
 * @author jflute
 * @since 0.8.6 (2008/11/21 Friday)
 */
public class DfPropertyTypePackageResolver {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String VAR_CDEF = "$$CDef$$";
    public static final String VAR_DOMAIN = "$$Domain$$";
    public static final String VAR_CUSTOMIZE = "$$Customize$$";
    public static final String VAR_PMB = "$$Pmb$$";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfPropertyTypePackageResolver() {
    }

    // ===================================================================================
    //                                                                            Resolver
    //                                                                            ========
    public String resolvePackageName(String typeName) {
        return doResolvePackageName(typeName, false);
    }

    public String resolvePackageNameExceptUtil(String typeName) {
        return doResolvePackageName(typeName, true);
    }

    protected String doResolvePackageName(String typeName, boolean exceptUtil) {
        if (typeName == null) {
            return typeName;
        }
        if (isTargetLanguageJava()) {
            final String processed = processJavaType(typeName, exceptUtil);
            if (processed != null) {
                return processed;
            }
        } else if (isTargetLanguageCSharp()) {
            final String processed = processCSharpType(typeName, exceptUtil);
            if (processed != null) {
                return processed;
            }
        }
        if (typeName.contains(VAR_CDEF)) {
            final DfBasicProperties prop = getBasicProperties();
            final String pkg = prop.getBaseCommonPackage();
            final String prefix = prop.getProjectPrefix();
            typeName = DfStringUtil.replace(typeName, VAR_CDEF, pkg + "." + prefix + "CDef");
        }
        if (typeName.contains(VAR_DOMAIN + ".")) { // as domain entity
            final String pkg = getBasicProperties().getExtendedEntityPackage();
            typeName = Srl.replace(typeName, VAR_DOMAIN + ".", pkg + ".");
        }
        if (typeName.contains(VAR_CUSTOMIZE + ".")) { // as customize entity
            final String pkg = getOutsideSqlProperties().getExtendedEntityPackage();
            typeName = Srl.replace(typeName, VAR_CUSTOMIZE + ".", pkg + ".");
        }
        if (typeName.contains(VAR_PMB + ".")) { // as parameter-bean
            final String pkg = getOutsideSqlProperties().getExtendedParameterBeanPackage();
            typeName = Srl.replace(typeName, VAR_PMB + ".", pkg + ".");
        }
        return typeName;
    }

    protected String processJavaType(String typeName, boolean exceptUtil) {
        if (!exceptUtil) {
            final String listType = processListType(typeName, exceptUtil, "java.util", "List");
            if (listType != null) {
                return listType;
            }
            final String mapType = processMapType(typeName, exceptUtil, "java.util", "Map");
            if (mapType != null) {
                return mapType;
            }
        }
        if (typeName.equals("BigDecimal")) {
            return "java.math." + typeName;
        }
        if (typeName.equals("Time")) {
            return "java.sql." + typeName;
        }
        if (typeName.equals("Timestamp")) {
            return "java.sql." + typeName;
        }
        if (!exceptUtil) {
            if (typeName.equals("Date")) {
                return "java.util." + typeName;
            }
        }
        return null;
    }

    protected String processCSharpType(String typeName, boolean exceptUtil) {
        final String listType = processListType(typeName, exceptUtil, "System.Collections.Generic", "IList");
        if (listType != null) {
            return listType;
        }
        final String mapType = processMapType(typeName, exceptUtil, "System.Collections.Generic", "IDictionary");
        if (mapType != null) {
            return mapType;
        }
        return null;
    }

    protected String processListType(String typeName, boolean exceptUtil, String listPkg, String listName) {
        final String listBegin = listName + "<";
        final String listEnd = ">";
        if (typeName.startsWith(listBegin) && typeName.endsWith(listEnd)) {
            final ScopeInfo scope = Srl.extractScopeWide(typeName, listBegin, listEnd);
            final String content = scope.getContent();
            final String resolvedContent = doResolvePackageName(content, exceptUtil);
            return listPkg + "." + listBegin + resolvedContent + listEnd;
        } else {
            return null;
        }
    }

    protected String processMapType(String typeName, boolean exceptUtil, String mapPkg, String mapName) {
        final String mapBegin = mapName + "<";
        final String mapEnd = ">";
        if (typeName.startsWith(mapBegin) && typeName.endsWith(mapEnd)) {
            final ScopeInfo scope = Srl.extractScopeWide(typeName, mapBegin, mapEnd);
            final String content = scope.getContent();
            final String keyType = Srl.substringFirstFront(content, ",").trim();
            final String valueType = Srl.substringFirstRear(content, ",").trim();
            final String resolvedValueType = doResolvePackageName(valueType, exceptUtil);
            return mapPkg + "." + mapBegin + keyType + ", " + resolvedValueType + mapEnd;
        } else {
            return null;
        }
    }

    protected boolean isTargetLanguageJava() {
        return getBasicProperties().isTargetLanguageJava();
    }

    protected boolean isTargetLanguageCSharp() {
        return getBasicProperties().isTargetLanguageCSharp();
    }

    protected DfBasicProperties getBasicProperties() {
        return DfBuildProperties.getInstance().getBasicProperties();
    }

    protected DfOutsideSqlProperties getOutsideSqlProperties() {
        return DfBuildProperties.getInstance().getOutsideSqlProperties();
    }
}
