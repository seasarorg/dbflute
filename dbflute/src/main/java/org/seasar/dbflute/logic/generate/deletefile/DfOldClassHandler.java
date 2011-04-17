package org.seasar.dbflute.logic.generate.deletefile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.friends.velocity.DfGenerator;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefault;
import org.seasar.dbflute.logic.generate.packagepath.DfPackagePathHandler;
import org.seasar.dbflute.logic.sql2entity.pmbean.DfPmbMetaData;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;

/**
 * @author jflute
 * @since 0.7.8 (2008/08/23 Saturday)
 */
public class DfOldClassHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(Database.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DfGenerator _generator;
    protected DfBasicProperties _basicProperties;
    protected DfLittleAdjustmentProperties _littleAdjustmentProperties;
    protected DfGeneratedClassPackageDefault _generatedClassPackageDefault;
    protected List<Table> _tableList;
    protected Map<String, Map<String, Table>> _cmentityLocationMap;
    protected Map<String, Map<String, DfPmbMetaData>> _pmbLocationMap;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfOldClassHandler(DfGenerator generator, DfBasicProperties basicProperties,
            DfLittleAdjustmentProperties littleAdjustmentProperties, List<Table> tableList) {
        _generator = generator;
        _basicProperties = basicProperties;
        _littleAdjustmentProperties = littleAdjustmentProperties;
        _generatedClassPackageDefault = basicProperties.getLanguageDependencyInfo().getGeneratedClassPackageInfo();
        _tableList = tableList;
    }

    // ===================================================================================
    //                                                                     Old Table Class
    //                                                                     ===============
    public void deleteOldTableClass() {
        info("public void deleteOldTableClass() {");
        deleteOldTableClass_for_BaseBehavior();
        deleteOldTableClass_for_BaseDao();
        deleteOldTableClass_for_BaseEntity();
        deleteOldTableClass_for_DBMeta();
        deleteOldTableClass_for_BaseConditionBean();
        deleteOldTableClass_for_AbstractBaseConditionQuery();
        deleteOldTableClass_for_BaseConditionQuery();
        deleteOldTableClass_for_NestSelectSetupper();
        deleteOldTableClass_for_ExtendedConditionBean();
        deleteOldTableClass_for_ExtendedConditionQuery();
        deleteOldTableClass_for_ExtendedConditionInlineQuery();
        deleteOldTableClass_for_ExtendedBehavior();
        deleteOldTableClass_for_ExtendedDao();
        deleteOldTableClass_for_ExtendedEntity();
        info("}");
    }

    protected List<String> _deletedOldTableBaseBehaviorList;

    public void deleteOldTableClass_for_BaseBehavior() {
        final NotDeleteTCNSetupper setupper = new NotDeleteTCNSetupper() {
            public String setup(Table table) {
                if (_basicProperties.isApplicationBehaviorProject()) {
                    return table.getBaseBehaviorApClassName();
                } else {
                    return table.getBaseBehaviorClassName();
                }
            }
        };
        final String packagePath = getBaseBehaviorPackage();
        final String classPrefix = getProjectPrefix() + getBasePrefix();
        final String classSuffix;
        if (_basicProperties.isApplicationBehaviorProject()) {
            final String additionalSuffix = _basicProperties.getApplicationBehaviorAdditionalSuffix();
            classSuffix = "Bhv" + additionalSuffix;
        } else {
            classSuffix = "Bhv";
        }
        final DfOldTableClassDeletor deletor = createTCD(packagePath, classPrefix, classSuffix, setupper);
        _deletedOldTableBaseBehaviorList = deletor.deleteOldTableClass();
        showDeleteOldTableFile(_deletedOldTableBaseBehaviorList);
    }

    protected String getBaseBehaviorPackage() {
        return _basicProperties.getBaseBehaviorPackage();
    }

    protected List<String> _deletedOldTableBaseDaoList;

    public void deleteOldTableClass_for_BaseDao() {
        final NotDeleteTCNSetupper setupper = new NotDeleteTCNSetupper() {
            public String setup(Table table) {
                return table.getBaseDaoClassName();
            }
        };
        final String packagePath = getBaseDaoPackage();
        final String classPrefix = getProjectPrefix() + getBasePrefix();
        final DfOldTableClassDeletor deletor = createTCD(packagePath, classPrefix, "Dao", setupper);
        _deletedOldTableBaseDaoList = deletor.deleteOldTableClass();
        showDeleteOldTableFile(_deletedOldTableBaseDaoList);
    }

    protected String getBaseDaoPackage() {
        return _basicProperties.getBaseDaoPackage();
    }

    protected List<String> _deletedOldTableBaseEntityList;

    public void deleteOldTableClass_for_BaseEntity() {
        final NotDeleteTCNSetupper setupper = new NotDeleteTCNSetupper() {
            public String setup(Table table) {
                return table.getBaseEntityClassName();
            }
        };
        final String packagePath = getBaseEntityPackage();
        final String classPrefix = getProjectPrefix() + getBasePrefix();
        final DfOldTableClassDeletor deletor = createTCD(packagePath, classPrefix, null, setupper);
        _deletedOldTableBaseEntityList = deletor.deleteOldTableClass();
        showDeleteOldTableFile(_deletedOldTableBaseEntityList);
    }

    protected String getBaseEntityPackage() {
        return _basicProperties.getBaseEntityPackage();
    }

    public void deleteOldTableClass_for_DBMeta() {
        final NotDeleteTCNSetupper setupper = new NotDeleteTCNSetupper() {
            public String setup(Table table) {
                return table.getDBMetaClassName();
            }
        };
        final String packagePath = getDBMetaPackage();
        final String classPrefix = getProjectPrefix();
        final DfOldTableClassDeletor deletor = createTCD(packagePath, classPrefix, "Dbm", setupper);
        showDeleteOldTableFile(deletor.deleteOldTableClass());
    }

    protected String getDBMetaPackage() {
        return _basicProperties.getDBMetaPackage();
    }

    public void deleteOldTableClass_for_BaseConditionBean() {
        final NotDeleteTCNSetupper setupper = new NotDeleteTCNSetupper() {
            public String setup(Table table) {
                return table.getBaseConditionBeanClassName();
            }
        };
        final String packagePath = getConditionBeanPackage() + ".bs";
        final String classPrefix = getProjectPrefix() + getBasePrefix();
        final DfOldTableClassDeletor deletor = createTCD(packagePath, classPrefix, "CB", setupper);
        showDeleteOldTableFile(deletor.deleteOldTableClass());
    }

    protected String getConditionBeanPackage() {
        return _basicProperties.getConditionBeanPackage();
    }

    public void deleteOldTableClass_for_AbstractBaseConditionQuery() {
        final NotDeleteTCNSetupper setupper = new NotDeleteTCNSetupper() {
            public String setup(Table table) {
                return table.getAbstractBaseConditionQueryClassName();
            }
        };
        final String packagePath = getConditionBeanPackage() + ".cq.bs";
        final String classPrefix = getProjectPrefix() + "Abstract" + getBasePrefix();
        final DfOldTableClassDeletor deletor = createTCD(packagePath, classPrefix, "CQ", setupper);
        showDeleteOldTableFile(deletor.deleteOldTableClass());
    }

    public void deleteOldTableClass_for_BaseConditionQuery() {
        final NotDeleteTCNSetupper setupper = new NotDeleteTCNSetupper() {
            public String setup(Table table) {
                return table.getBaseConditionQueryClassName();
            }
        };
        final String packagePath = getConditionBeanPackage() + ".cq.bs";
        final String classPrefix = getProjectPrefix() + getBasePrefix();
        final DfOldTableClassDeletor deletor = createTCD(packagePath, classPrefix, "CQ", setupper);
        showDeleteOldTableFile(deletor.deleteOldTableClass());
    }

    public void deleteOldTableClass_for_NestSelectSetupper() {
        final NotDeleteTCNSetupper setupper = new NotDeleteTCNSetupper() {
            public String setup(Table table) {
                return table.getNestSelectSetupperClassName();
            }
        };
        final String packagePath = getConditionBeanPackage() + ".nss";
        final String classPrefix = getProjectPrefix();
        final DfOldTableClassDeletor deletor = createTCD(packagePath, classPrefix, "Nss", setupper);
        showDeleteOldTableFile(deletor.deleteOldTableClass());
    }

    public void deleteOldTableClass_for_ExtendedConditionBean() {
        final NotDeleteTCNSetupper setupper = new NotDeleteTCNSetupper() {
            public String setup(Table table) {
                return table.getExtendedConditionBeanClassName();
            }
        };
        final String packagePath = getConditionBeanPackage();
        final String classPrefix = getProjectPrefix();
        final DfOldTableClassDeletor deletor = createTCD(packagePath, classPrefix, "CB", setupper);
        showDeleteOldTableFile(deletor.deleteOldTableClass());
    }

    public void deleteOldTableClass_for_ExtendedConditionQuery() {
        final NotDeleteTCNSetupper setupper = new NotDeleteTCNSetupper() {
            public String setup(Table table) {
                return table.getExtendedConditionQueryClassName();
            }
        };
        final String packagePath = getConditionBeanPackage() + ".cq";
        final String classPrefix = getProjectPrefix();
        final DfOldTableClassDeletor deletor = createTCD(packagePath, classPrefix, "CQ", setupper);
        showDeleteOldTableFile(deletor.deleteOldTableClass());
    }

    public void deleteOldTableClass_for_ExtendedConditionInlineQuery() {
        final NotDeleteTCNSetupper setupper = new NotDeleteTCNSetupper() {
            public String setup(Table table) {
                return table.getExtendedConditionInlineQueryClassName();
            }
        };
        final String packagePath = getConditionBeanPackage() + ".cq.ciq";
        final String classPrefix = getProjectPrefix();
        final DfOldTableClassDeletor deletor = createTCD(packagePath, classPrefix, "CIQ", setupper);
        showDeleteOldTableFile(deletor.deleteOldTableClass());
    }

    public void deleteOldTableClass_for_ExtendedBehavior() {
        if (_deletedOldTableBaseBehaviorList == null || _deletedOldTableBaseBehaviorList.isEmpty()) {
            return;
        }
        final String outputPath = _generator.getOutputPath();
        final String packagePath = getExtendedBehaviorPackage();
        final DfPackagePathHandler packagePathHandler = createPackagePathHandler();
        final String dirPath = outputPath + "/" + packagePathHandler.getPackageAsPath(packagePath);
        for (String baseClassName : _deletedOldTableBaseBehaviorList) {
            final int prefixLength = getProjectPrefix().length() + getBasePrefix().length();
            final String extendedClassName = getProjectPrefix() + baseClassName.substring(prefixLength);
            final File file = new File(dirPath + "/" + extendedClassName + "." + getClassFileExtension());
            if (file.exists()) {
                file.delete();
                _log.info("    delete('" + extendedClassName + "');");
            }
        }
    }

    protected String getExtendedBehaviorPackage() {
        return _basicProperties.getExtendedBehaviorPackage();
    }

    public void deleteOldTableClass_for_ExtendedDao() {
        if (_deletedOldTableBaseDaoList == null || _deletedOldTableBaseDaoList.isEmpty()) {
            return;
        }
        final String outputPath = _generator.getOutputPath();
        final String packagePath = getExtendedDaoPackage();
        final DfPackagePathHandler packagePathHandler = createPackagePathHandler();
        final String dirPath = outputPath + "/" + packagePathHandler.getPackageAsPath(packagePath);
        for (String baseClassName : _deletedOldTableBaseDaoList) {
            final int prefixLength = getProjectPrefix().length() + getBasePrefix().length();
            final String extendedClassName = getProjectPrefix() + baseClassName.substring(prefixLength);
            final File file = new File(dirPath + "/" + extendedClassName + "." + getClassFileExtension());
            if (file.exists()) {
                file.delete();
                info("    delete('" + extendedClassName + "');");
            }
        }
    }

    protected String getExtendedDaoPackage() {
        return _basicProperties.getExtendedDaoPackage();
    }

    public void deleteOldTableClass_for_ExtendedEntity() {
        if (_deletedOldTableBaseEntityList == null || _deletedOldTableBaseEntityList.isEmpty()) {
            return;
        }
        final String outputPath = _generator.getOutputPath();
        final String packagePath = getExtendedEntityPackage();
        final DfPackagePathHandler packagePathHandler = createPackagePathHandler();
        final String dirPath = outputPath + "/" + packagePathHandler.getPackageAsPath(packagePath);
        for (String baseClassName : _deletedOldTableBaseEntityList) {
            final int prefixLength = getProjectPrefix().length() + getBasePrefix().length();
            final String extendedClassName = getProjectPrefix() + baseClassName.substring(prefixLength);
            final File file = new File(dirPath + "/" + extendedClassName + "." + getClassFileExtension());
            if (file.exists()) {
                file.delete();
                info("    delete('" + extendedClassName + "');");
            }
        }
    }

    protected String getExtendedEntityPackage() {
        return _basicProperties.getExtendedEntityPackage();
    }

    protected void showDeleteOldTableFile(List<String> deletedClassNameList) {
        for (String className : deletedClassNameList) {
            info("    delete('" + className + "');");
        }
    }

    protected DfOldTableClassDeletor createTCD(String packagePath, String classPrefix, String classSuffix,
            NotDeleteTCNSetupper setupper) { // createOldTableClassDeletor()
        final DfOldTableClassDeletor deletor = new DfOldTableClassDeletor(_generator.getOutputPath(),
                createPackagePathHandler());
        deletor.addPackagePath(packagePath);
        deletor.setClassPrefix(classPrefix);
        deletor.setClassSuffix(classSuffix);
        deletor.setClassExtension(getClassFileExtension());
        deletor.setNotDeleteClassNameSet(createNotDeleteTCNSet(setupper));
        return deletor;
    }

    protected static interface NotDeleteTCNSetupper { // NotDeleteTableClassNameSetupper
        public String setup(Table table);
    }

    protected Set<String> createNotDeleteTCNSet(NotDeleteTCNSetupper setupper) {
        final Set<String> notDeleteClassNameSet = new HashSet<String>();
        final List<Table> tableList = getTableList();
        for (Table table : tableList) {
            final String tableName = setupper.setup(table);
            if (tableName != null) {
                notDeleteClassNameSet.add(tableName);
            }
        }
        return notDeleteClassNameSet;
    }

    // ===================================================================================
    //                                                                 Old Customize Class
    //                                                                 ===================
    public void deleteOldCustomizeClass() {
        info("public void deleteOldCustomizeClass() {");
        deleteOldCustomizeClass_for_BaseCustomizeEntity();
        deleteOldCustomizeClass_for_DBMeta();
        deleteOldCustomizeClass_for_BaseCursor();
        deleteOldCustomizeClass_for_BaseCursorHandler();
        deleteOldCustomizeClass_for_BaseParameterBean();
        deleteOldCustomizeClass_for_ExtendedCustomizeEntity();
        deleteOldCustomizeClass_for_ExtendedCursor();
        deleteOldCustomizeClass_for_ExtendedCursorHandler();
        deleteOldCustomizeClass_for_ExtendedParameterBean();
        info("}");
    }

    protected List<String> _deletedOldCustomizeBaseEntityList;

    public void deleteOldCustomizeClass_for_BaseCustomizeEntity() {
        if (_cmentityLocationMap == null) {
            return;
        }
        final String customizePackageName = _generatedClassPackageDefault.getCustomizeEntitySimplePackageName();
        final String packagePath = getBaseEntityPackage() + "." + customizePackageName;
        final String classSuffix = null;

        _deletedOldCustomizeBaseEntityList = new ArrayList<String>();
        doDeleteOldCustomizeClass_for_BaseEntity(packagePath, classSuffix, _deletedOldCustomizeBaseEntityList,
                new NotDeleteTCNSetupper() {
                    public String setup(Table table) {
                        return table.getBaseEntityClassName();
                    }
                }, null);
    }

    public void deleteOldCustomizeClass_for_DBMeta() {
        if (_cmentityLocationMap == null) {
            return;
        }
        final String customizePackageName = _generatedClassPackageDefault.getCustomizeEntitySimplePackageName();
        final String dbmetaSimplePackageName = _generatedClassPackageDefault.getDBMetaSimplePackageName();
        final String packagePath = getBaseEntityPackage() + "." + customizePackageName + "." + dbmetaSimplePackageName;
        final String classSuffix = "Dbm";

        doDeleteOldCustomizeClass_for_BaseEntity(packagePath, classSuffix, new ArrayList<String>(),
                new NotDeleteTCNSetupper() {
                    public String setup(Table table) {
                        return table.getDBMetaClassName();
                    }
                }, null);
    }

    protected List<String> _deletedOldCustomizeBaseCursorList;

    public void deleteOldCustomizeClass_for_BaseCursor() {
        if (_cmentityLocationMap == null) {
            return;
        }
        final String cursorPackageName = _generatedClassPackageDefault.getCursorSimplePackageName();
        final String packagePath = getBaseBehaviorPackage() + "." + cursorPackageName;
        final String oldStylePackagePath = getBaseDaoPackage() + "." + cursorPackageName;
        final String classSuffix = "Cursor";

        _deletedOldCustomizeBaseCursorList = new ArrayList<String>();
        doDeleteOldCustomizeClass_for_BaseEntity(packagePath, classSuffix, _deletedOldCustomizeBaseCursorList,
                new NotDeleteTCNSetupper() {
                    public String setup(Table table) {
                        return table.getBaseEntityClassName() + classSuffix;
                    }
                }, oldStylePackagePath);
    }

    protected List<String> _deletedOldCustomizeBaseCursorHandlerList;

    public void deleteOldCustomizeClass_for_BaseCursorHandler() {
        if (_cmentityLocationMap == null) {
            return;
        }
        final String cursorPackageName = _generatedClassPackageDefault.getCursorSimplePackageName();
        final String packagePath = getBaseBehaviorPackage() + "." + cursorPackageName;
        final String oldStylePackagePath = getBaseDaoPackage() + "." + cursorPackageName;
        final String classSuffix = "CursorHandler";

        _deletedOldCustomizeBaseCursorHandlerList = new ArrayList<String>();
        doDeleteOldCustomizeClass_for_BaseEntity(packagePath, classSuffix, _deletedOldCustomizeBaseCursorHandlerList,
                new NotDeleteTCNSetupper() {
                    public String setup(Table table) {
                        return table.getBaseEntityClassName() + classSuffix;
                    }
                }, oldStylePackagePath);
    }

    protected void doDeleteOldCustomizeClass_for_BaseEntity(String packagePath, String classSuffix,
            List<String> deletedList, final NotDeleteTCNSetupper setupper, String oldStylePackagePath) {
        if (_cmentityLocationMap == null) {
            return;
        }
        final String classPrefix = getProjectPrefix() + getBasePrefix();
        final Set<Entry<String, Map<String, Table>>> entrySet = _cmentityLocationMap.entrySet();
        for (Entry<String, Map<String, Table>> entry : entrySet) {
            final String outputDirectory = entry.getKey();
            // *no need to use because tableList already exists
            //final Map<String, Table> elementMap = entry.getValue();

            final DfOldTableClassDeletor deletor = createCCD(outputDirectory, packagePath, classPrefix, classSuffix,
                    new NotDeleteTCNSetupper() {
                        public String setup(Table table) {
                            if (!table.getSql2EntityOutputDirectory().equals(outputDirectory)) {
                                return null;
                            }
                            return setupper.setup(table);
                        }
                    });
            if (oldStylePackagePath != null) { // e.g. cursor
                deletor.addPackagePath(oldStylePackagePath);
            }
            deletedList.addAll(deletor.deleteOldTableClass());
        }
        showDeleteOldTableFile(deletedList);
    }

    protected List<String> _deletedOldCustomizeBaseParameterBeanList;

    public void deleteOldCustomizeClass_for_BaseParameterBean() {
        if (_pmbLocationMap == null) {
            return;
        }
        final String parameterBeanPackageName = _generatedClassPackageDefault.getParameterBeanSimplePackageName();
        final String packagePath = getBaseBehaviorPackage() + "." + parameterBeanPackageName;
        final String oldStylePackagePath = getBaseDaoPackage() + "." + parameterBeanPackageName;
        final String classPrefix = getProjectPrefix() + getBasePrefix();
        _deletedOldCustomizeBaseParameterBeanList = new ArrayList<String>();
        final Set<Entry<String, Map<String, DfPmbMetaData>>> entrySet = _pmbLocationMap.entrySet();
        for (Entry<String, Map<String, DfPmbMetaData>> entry : entrySet) {
            final String outputDirectory = entry.getKey();
            final Map<String, DfPmbMetaData> elementMap = entry.getValue();
            final Set<String> notDeleteClassNameSet = new HashSet<String>();
            for (String pmbName : elementMap.keySet()) {
                notDeleteClassNameSet.add(getProjectPrefix() + getBasePrefix() + pmbName);
            }
            final DfOldTableClassDeletor deletor = createCCD(outputDirectory, packagePath, classPrefix, null,
                    notDeleteClassNameSet);
            deletor.addPackagePath(oldStylePackagePath); // for Old Style Package
            final List<String> deletedList = deletor.deleteOldTableClass();
            _deletedOldCustomizeBaseParameterBeanList.addAll(deletedList);
        }
        showDeleteOldTableFile(_deletedOldCustomizeBaseParameterBeanList);
    }

    public void deleteOldCustomizeClass_for_ExtendedCustomizeEntity() {
        if (_deletedOldCustomizeBaseEntityList == null || _deletedOldCustomizeBaseEntityList.isEmpty()) {
            return;
        }
        final String customizePackageName = _generatedClassPackageDefault.getCustomizeEntitySimplePackageName();
        final String packagePath = getExtendedEntityPackage() + "." + customizePackageName;
        deleteCustomizeExtendedClass(_deletedOldCustomizeBaseEntityList, packagePath);
    }

    public void deleteOldCustomizeClass_for_ExtendedCursor() {
        if (_deletedOldCustomizeBaseCursorList == null || _deletedOldCustomizeBaseCursorList.isEmpty()) {
            return;
        }
        final String cursorPackageName = _generatedClassPackageDefault.getCursorSimplePackageName();
        final String packagePath = getExtendedBehaviorPackage() + "." + cursorPackageName;
        final String oldStylePackagePath = getExtendedDaoPackage() + "." + cursorPackageName;
        deleteCustomizeExtendedClass(_deletedOldCustomizeBaseCursorList, packagePath, oldStylePackagePath);
    }

    public void deleteOldCustomizeClass_for_ExtendedCursorHandler() {
        if (_deletedOldCustomizeBaseCursorHandlerList == null || _deletedOldCustomizeBaseCursorHandlerList.isEmpty()) {
            return;
        }
        final String cursorPackageName = _generatedClassPackageDefault.getCursorSimplePackageName();
        final String packagePath = getExtendedBehaviorPackage() + "." + cursorPackageName;
        final String oldStylePackagePath = getExtendedDaoPackage() + "." + cursorPackageName;
        deleteCustomizeExtendedClass(_deletedOldCustomizeBaseCursorHandlerList, packagePath, oldStylePackagePath);
    }

    public void deleteOldCustomizeClass_for_ExtendedParameterBean() {
        if (_deletedOldCustomizeBaseParameterBeanList == null || _deletedOldCustomizeBaseParameterBeanList.isEmpty()) {
            return;
        }
        final String parameterBeanPackageName = _generatedClassPackageDefault.getParameterBeanSimplePackageName();
        final String packagePath = getExtendedBehaviorPackage() + "." + parameterBeanPackageName;
        final String oldStylePackagePath = getExtendedDaoPackage() + "." + parameterBeanPackageName;
        deleteCustomizeExtendedClass(_deletedOldCustomizeBaseParameterBeanList, packagePath, oldStylePackagePath);
    }

    protected DfOldTableClassDeletor createCCD(String outputDirectory, String packagePath, String classPrefix,
            String classSuffix, NotDeleteTCNSetupper setupper) { // createOldCustomizeClassDeletor()
        return createCCD(outputDirectory, packagePath, classPrefix, classSuffix, createNotDeleteTCNSet(setupper));
    }

    protected DfOldTableClassDeletor createCCD(String outputDirectory, String packagePath, String classPrefix,
            String classSuffix, Set<String> notDeleteClassNameSet) { // createOldCustomizeClassDeletor()
        final DfPackagePathHandler packagePathHandler = new DfPackagePathHandler(_basicProperties);
        final DfOldTableClassDeletor deletor = new DfOldTableClassDeletor(outputDirectory, packagePathHandler);
        deletor.addPackagePath(packagePath);
        deletor.setClassPrefix(classPrefix);
        deletor.setClassSuffix(classSuffix);
        deletor.setClassExtension(getClassFileExtension());
        deletor.setNotDeleteClassNameSet(notDeleteClassNameSet);
        return deletor;
    }

    protected void deleteCustomizeExtendedClass(List<String> baseClassList, String... packagePathList) {
        final String outputPath = _generator.getOutputPath();
        final DfPackagePathHandler packagePathHandler = createPackagePathHandler();
        for (String packagePath : packagePathList) {
            final String dirPath = outputPath + "/" + packagePathHandler.getPackageAsPath(packagePath);
            for (String baseClassName : baseClassList) {
                final int prefixLength = getProjectPrefix().length() + getBasePrefix().length();
                final String extendedClassName = getProjectPrefix() + baseClassName.substring(prefixLength);
                final File file = new File(dirPath + "/" + extendedClassName + "." + getClassFileExtension());
                if (file.exists()) {
                    file.delete();
                    info("    delete('" + extendedClassName + "');");
                }
            }
        }
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected DfPackagePathHandler createPackagePathHandler() {
        return new DfPackagePathHandler(_basicProperties);
    }

    protected String getProjectPrefix() {
        return _basicProperties.getProjectPrefix();
    }

    protected String getBasePrefix() {
        return _basicProperties.getBasePrefix();
    }

    protected String getClassFileExtension() {
        return _basicProperties.getClassFileExtension();
    }

    protected List<Table> getTableList() {
        return _tableList;
    }

    // -----------------------------------------------------
    //                                               Logging
    //                                               -------
    public void info(String msg) {
        _log.info(msg);
    }

    public void debug(String msg) {
        _log.debug(msg);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setCustomizeTableList(List<Table> customizeTableList) {
        final Map<String, Map<String, Table>> cmentityLocationMap = new LinkedHashMap<String, Map<String, Table>>();
        for (Table table : customizeTableList) {
            final String outputDirectory = table.getSql2EntityOutputDirectory();
            Map<String, Table> elementMap = cmentityLocationMap.get(outputDirectory);
            if (elementMap == null) {
                elementMap = new LinkedHashMap<String, Table>();
                cmentityLocationMap.put(outputDirectory, elementMap);
            }
            elementMap.put(table.getName(), table);
        }
        _cmentityLocationMap = cmentityLocationMap;
    }

    public void setPmbMetaDataMap(Map<String, DfPmbMetaData> pmbMetaDataMap) {
        final Map<String, Map<String, DfPmbMetaData>> pmbLocationMap = new LinkedHashMap<String, Map<String, DfPmbMetaData>>();
        final Set<Entry<String, DfPmbMetaData>> entrySet = pmbMetaDataMap.entrySet();
        for (Entry<String, DfPmbMetaData> entry : entrySet) {
            final String pmbName = entry.getKey();
            final DfPmbMetaData pmbMetaData = entry.getValue();
            final String outputDirectory = pmbMetaData.getSql2EntityOutputDirectory();
            Map<String, DfPmbMetaData> elementMap = pmbLocationMap.get(outputDirectory);
            if (elementMap == null) {
                elementMap = new LinkedHashMap<String, DfPmbMetaData>();
                pmbLocationMap.put(outputDirectory, elementMap);
            }
            elementMap.put(pmbName, pmbMetaData);
        }
        _pmbLocationMap = pmbLocationMap;
    }
}
