package org.seasar.dbflute.logic.sql2entity.pmbean;

import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.Srl;
import org.seasar.dbflute.util.Srl.ScopeInfo;

/**
 * @author jflute
 * @since 0.8.6 (2008/11/21 Friday)
 */
public class DfStandardApiPackageResolver {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DfBasicProperties _basicProperties;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfStandardApiPackageResolver(DfBasicProperties basicProperties) {
        _basicProperties = basicProperties;
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
        if (typeName.startsWith("$$CDef$$")) {
            final DfBasicProperties prop = _basicProperties;
            final String pkg = prop.getBaseCommonPackage();
            final String prefix = prop.getProjectPrefix();
            typeName = DfStringUtil.replace(typeName, "$$CDef$$", pkg + "." + prefix + "CDef");
            return typeName;
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
        return _basicProperties.isTargetLanguageJava();
    }

    protected boolean isTargetLanguageCSharp() {
        return _basicProperties.isTargetLanguageCSharp();
    }
}
