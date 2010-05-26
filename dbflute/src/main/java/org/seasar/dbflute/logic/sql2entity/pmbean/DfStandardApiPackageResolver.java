package org.seasar.dbflute.logic.sql2entity.pmbean;

import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.util.DfStringUtil;

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
        final DfBasicProperties prop = _basicProperties;
        if (prop.isTargetLanguageJava()) {
            if (!exceptUtil) {
                if (typeName.startsWith("List<") && typeName.endsWith(">")) {
                    return "java.util." + typeName;
                }
                if (typeName.startsWith("Map<") && typeName.endsWith(">")) {
                    return "java.util." + typeName;
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
        } else if (prop.isTargetLanguageCSharp()) {
            if (typeName.startsWith("IList<") && typeName.endsWith(">")) {
                return "System.Collections.Generic." + typeName;
            }
        }
        if (typeName.startsWith("$$CDef$$")) {
            String pkg = prop.getBaseCommonPackage();
            String prefix = prop.getProjectPrefix();
            typeName = DfStringUtil.replace(typeName, "$$CDef$$", pkg + "." + prefix + "CDef");
            return typeName;
        }
        return typeName;
    }
}
