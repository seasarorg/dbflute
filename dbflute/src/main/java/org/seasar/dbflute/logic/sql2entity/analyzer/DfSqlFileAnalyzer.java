package org.seasar.dbflute.logic.sql2entity.analyzer;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.exception.DfCustomizeEntityDuplicateException;
import org.seasar.dbflute.exception.DfParameterBeanDuplicateException;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerBase;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.sql2entity.cmentity.DfCustomizeEntityInfo;
import org.seasar.dbflute.logic.sql2entity.cmentity.DfCustomizeEntityMetaExtractor;
import org.seasar.dbflute.logic.sql2entity.cmentity.DfCustomizeEntityMetaExtractor.DfForcedJavaNativeProvider;
import org.seasar.dbflute.logic.sql2entity.outsidesql.DfSqlFileNameResolver;
import org.seasar.dbflute.logic.sql2entity.pmbean.DfPmbMetaData;
import org.seasar.dbflute.logic.sql2entity.pmbean.DfPropertyTypePackageResolver;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfSqlFileAnalyzer extends DfSqlFileRunnerBase {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSqlFileAnalyzer.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DfSql2EntityMeta _sql2entityMeta;
    protected final DBDef _currentDBDef;

    protected final DfSql2EntityMarkAnalyzer _outsideSqlMarkAnalyzer = new DfSql2EntityMarkAnalyzer();
    protected final DfSqlFileNameResolver _sqlFileNameResolver = new DfSqlFileNameResolver();
    protected final DfPropertyTypePackageResolver _propertyTypePackageResolver = new DfPropertyTypePackageResolver();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSqlFileAnalyzer(DfRunnerInformation runInfo, DataSource dataSource, DfSql2EntityMeta sqlFileMeta) {
        super(runInfo, dataSource);
        _sql2entityMeta = sqlFileMeta;
        _currentDBDef = currentDBDef();
    }

    /**
     * Filter the string of SQL. Resolve JDBC dependency.
     * @param sql The string of SQL. (NotNull)
     * @return The filtered string of SQL. (NotNull)
     */
    @Override
    protected String filterSql(String sql) {
        // the comments are special mark for Sql2Entity
        // so this timing to do is bad because the special mark is removed.
        //if (!currentDBDef.dbway().isBlockCommentSupported()) {
        //    sql = removeBlockComment(sql);
        //}
        //if (!currentDBDef.dbway().isLineCommentSupported()) {
        //    sql = removeLineComment(sql);
        //}
        return super.filterSql(sql);
    }

    @Override
    protected void execSQL(String sql) {
        ResultSet rs = null;
        try {
            boolean alreadyIncrementGoodSqlCount = false;
            if (isTargetEntityMakingSql(sql)) {
                final String executedActuallySql;
                {
                    // the timing to remove comments is here
                    String filtered = sql;
                    if (!_currentDBDef.dbway().isBlockCommentSupported()) {
                        filtered = removeBlockComment(filtered);
                    }
                    if (!_currentDBDef.dbway().isLineCommentSupported()) {
                        filtered = removeLineComment(filtered);
                    }
                    executedActuallySql = filtered;
                }
                checkStatement(executedActuallySql);
                rs = _currentStatement.executeQuery(executedActuallySql);

                _goodSqlCount++;
                alreadyIncrementGoodSqlCount = true;

                final Map<String, String> columnForcedJavaNativeMap = createColumnForcedJavaNativeMap(sql);
                final DfCustomizeEntityMetaExtractor customizeEntityMetaExtractor = new DfCustomizeEntityMetaExtractor();
                final Map<String, DfColumnMetaInfo> columnMetaInfoMap = customizeEntityMetaExtractor
                        .extractColumnMetaInfoMap(rs, sql, new DfForcedJavaNativeProvider() {
                            public String provide(String columnName) {
                                return columnForcedJavaNativeMap.get(columnName);
                            }
                        });

                // for Customize Entity
                String entityName = getCustomizeEntityName(sql);
                if (entityName != null) {
                    entityName = resolveEntityNameIfNeeds(entityName, _sqlFile);
                    assertDuplicateEntity(entityName, _sqlFile);
                    _sql2entityMeta.addEntityInfo(entityName, new DfCustomizeEntityInfo(entityName, columnMetaInfoMap));
                    if (isCursor(sql)) {
                        _sql2entityMeta.addCursorInfo(entityName, DfSql2EntityMeta.CURSOR_INFO_DUMMY);
                    }
                    _sql2entityMeta.addEntitySqlFile(entityName, _sqlFile);
                    _sql2entityMeta.addPrimaryKey(entityName, getPrimaryKeyColumnNameList(sql));
                }
            }
            if (isTargetParameterBeanMakingSql(sql)) {
                if (!alreadyIncrementGoodSqlCount) {
                    _goodSqlCount++;
                }

                // for Parameter Bean
                final DfPmbMetaData parameterBeanMetaData = extractParameterBeanMetaData(sql);
                if (parameterBeanMetaData != null) {
                    final String pmbName = parameterBeanMetaData.getClassName();
                    assertDuplicateParameterBean(pmbName, _sqlFile);
                    _sql2entityMeta.addPmbMetaData(pmbName, parameterBeanMetaData);
                }
            }
        } catch (SQLException e) {
            if (_runInfo.isErrorContinue()) {
                _log.warn("Failed to execute: " + sql, e);
                _sql2entityMeta.addExceptionInfo(_sqlFile.getName(), e.getMessage() + ln() + sql);
                return;
            }
            throwSQLFailureException(sql, e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                    _log.warn("Ignored exception: " + ignored.getMessage());
                }
            }
        }
    }

    protected Map<String, String> createColumnForcedJavaNativeMap(String sql) {
        final List<String> entityPropertyTypeList = getEntityPropertyTypeList(sql);
        final Map<String, String> columnJavaNativeMap = StringKeyMap.createAsFlexible();
        for (String element : entityPropertyTypeList) {
            final String nameDelimiter = " ";
            final int nameDelimiterLength = nameDelimiter.length();
            element = element.trim();
            final int nameIndex = element.lastIndexOf(nameDelimiter);
            if (nameIndex <= 0) {
                String msg = "The customize entity element should be [typeName columnName].";
                msg = msg + " But: element=" + element;
                msg = msg + " srcFile=" + _sqlFile;
                throw new IllegalStateException(msg);
            }
            final String typeName = resolvePackageName(element.substring(0, nameIndex).trim());
            final String columnName = element.substring(nameIndex + nameDelimiterLength).trim();
            columnJavaNativeMap.put(columnName, typeName);
        }
        return columnJavaNativeMap;
    }

    protected boolean isTargetEntityMakingSql(String sql) {
        final String entityName = getCustomizeEntityName(sql);
        if (entityName == null) {
            return false;
        }
        if ("df:x".equalsIgnoreCase(entityName)) { // non target making SQL!
            return false;
        }
        return true;
    }

    protected boolean isTargetParameterBeanMakingSql(String sql) {
        final String parameterBeanName = getParameterBeanName(sql);
        return parameterBeanName != null;
    }

    /**
     * Extract the meta data of parameter bean.
     * @param sql Target SQL. (NotNull and NotEmpty)
     * @return the meta data of parameter bean. (NullAllowed: If it returns null, it means 'not found'.)
     */
    protected DfPmbMetaData extractParameterBeanMetaData(String sql) {
        final String parameterBeanName = getParameterBeanName(sql);
        if (parameterBeanName == null) {
            return null;
        }
        final DfPmbMetaData pmbMetaData = new DfPmbMetaData();
        {
            final String delimiter = "extends";
            final int idx = parameterBeanName.indexOf(delimiter);
            {
                String className = (idx >= 0) ? parameterBeanName.substring(0, idx) : parameterBeanName;
                className = className.trim();
                className = resolvePmbNameIfNeeds(className, _sqlFile);
                pmbMetaData.setClassName(className);
            }
            if (idx >= 0) {
                final String superClassName = parameterBeanName.substring(idx + delimiter.length()).trim();
                pmbMetaData.setSuperClassName(superClassName);
                resolveSuperClassSimplePagingBean(pmbMetaData);
            }
        }

        final Map<String, String> propertyNameTypeMap = new LinkedHashMap<String, String>();
        final Map<String, String> propertyNameOptionMap = new LinkedHashMap<String, String>();
        pmbMetaData.setPropertyNameTypeMap(propertyNameTypeMap);
        pmbMetaData.setPropertyNameOptionMap(propertyNameOptionMap);
        final List<String> parameterBeanElement = getParameterBeanPropertyTypeList(sql);
        for (String element : parameterBeanElement) {
            final String nameDelimiter = " ";
            final String optionDelimiter = ":";
            element = element.trim();
            final int optionIndex = element.indexOf(optionDelimiter);
            final String propertyDef;
            final String optionDef;
            if (optionIndex > 0) {
                propertyDef = element.substring(0, optionIndex).trim();
                optionDef = element.substring(optionIndex + optionDelimiter.length()).trim();
            } else {
                propertyDef = element;
                optionDef = null;
            }
            final int nameIndex = propertyDef.lastIndexOf(nameDelimiter);
            if (nameIndex <= 0) {
                String msg = "The parameter bean element should be [typeName propertyName].";
                msg = msg + " But: element=" + element + " srcFile=" + _sqlFile;
                throw new IllegalStateException(msg);
            }
            final String typeName = resolvePackageNameExceptUtil(propertyDef.substring(0, nameIndex).trim());
            final String propertyName = propertyDef.substring(nameIndex + nameDelimiter.length()).trim();
            propertyNameTypeMap.put(propertyName, typeName);
            if (optionDef != null) {
                propertyNameOptionMap.put(propertyName, optionDef);
            }
        }
        pmbMetaData.setSqlFile(_sqlFile);
        return pmbMetaData;
    }

    protected void resolveSuperClassSimplePagingBean(final DfPmbMetaData pmbMetaData) {
        final String superClassName = pmbMetaData.getSuperClassName();
        if (superClassName.equalsIgnoreCase("Paging") // main
                || superClassName.equalsIgnoreCase("SPB")) { // an old style for compatibility before 0.9.7.5
            final String baseCommonPackage = getBasicProperties().getBaseCommonPackage();
            final String projectPrefix = getBasicProperties().getProjectPrefix();
            final DfBasicProperties basicProperties = getProperties().getBasicProperties();
            final DfLanguageDependencyInfo languageDependencyInfo = basicProperties.getLanguageDependencyInfo();
            final String cbeanPackageName = languageDependencyInfo.getConditionBeanPackageName();
            final String spbName = "SimplePagingBean";
            pmbMetaData.setSuperClassName(baseCommonPackage + "." + cbeanPackageName + "." + projectPrefix + spbName);
        }
    }

    protected String resolvePackageName(String typeName) { // [DBFLUTE-271]
        return _propertyTypePackageResolver.resolvePackageName(typeName);
    }

    protected String resolvePackageNameExceptUtil(String typeName) {
        return _propertyTypePackageResolver.resolvePackageNameExceptUtil(typeName);
    }

    @Override
    protected String replaceCommentQuestionMarkIfNeeds(String line) {
        if (line.indexOf("--!!") >= 0 || line.indexOf("-- !!") >= 0) {
            // If the line comment is for a property of parameter-bean, 
            // it does not replace question mark.
            return line;
        }
        return super.replaceCommentQuestionMarkIfNeeds(line);
    }

    @Override
    protected boolean isTargetSql(String sql) {
        final String entityName = getCustomizeEntityName(sql);
        final String parameterBeanClassDefinition = getParameterBeanName(sql);

        // No Pmb and Non Target Entity --> Non Target
        if (parameterBeanClassDefinition == null && entityName != null && "df:x".equalsIgnoreCase(entityName)) {
            return false;
        }

        return entityName != null || parameterBeanClassDefinition != null;
    }

    @Override
    protected void traceSql(String sql) {
        _log.info("{SQL}" + ln() + sql);
    }

    @Override
    protected void traceResult(int goodSqlCount, int totalSqlCount) {
        if (totalSqlCount > 0) {
            _log.info("  --> success=" + goodSqlCount + " failure=" + (totalSqlCount - goodSqlCount));
        } else {
            _log.info("  --> SQL for sql2entity was not found in the SQL file!");
        }
    }

    @Override
    protected boolean isSqlTrimAndRemoveLineSeparator() {
        return false;
    }

    // ===================================================================================
    //                                                                   Assert Definition
    //                                                                   =================
    protected void assertDuplicateEntity(String entityName, File currentSqlFile) {
        final File sqlFile = _sql2entityMeta.getEntitySqlFileMap().get(entityName);
        if (sqlFile == null) {
            return;
        }
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The customize entity was duplicated!" + ln();
        msg = msg + ln();
        msg = msg + "[Customize Entity]" + ln() + entityName + ln();
        msg = msg + ln();
        msg = msg + "[SQL Files]" + ln() + sqlFile + ln() + currentSqlFile + ln();
        msg = msg + "* * * * * * * * * */";
        throw new DfCustomizeEntityDuplicateException(msg);
    }

    protected void assertDuplicateParameterBean(String pmbName, File currentSqlFile) {
        final DfPmbMetaData metaData = _sql2entityMeta.getPmbMetaDataMap().get(pmbName);
        if (metaData == null) {
            return;
        }
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The parameter-bean was duplicated!" + ln();
        msg = msg + ln();
        msg = msg + "[ParameterBean]" + ln() + pmbName + ln();
        msg = msg + ln();
        msg = msg + "[SQL Files]" + ln() + metaData.getSqlFile() + ln() + currentSqlFile + ln();
        msg = msg + "* * * * * * * * * */";
        throw new DfParameterBeanDuplicateException(msg);
    }

    // ===================================================================================
    //                                                                           Analyzing
    //                                                                           =========
    protected String getCustomizeEntityName(final String sql) {
        return _outsideSqlMarkAnalyzer.getCustomizeEntityName(sql);
    }

    protected boolean isCursor(final String sql) {
        return _outsideSqlMarkAnalyzer.isCursor(sql);
    }

    protected List<String> getEntityPropertyTypeList(final String sql) {
        return _outsideSqlMarkAnalyzer.getCustomizeEntityPropertyTypeList(sql);
    }

    protected String getParameterBeanName(final String sql) {
        return _outsideSqlMarkAnalyzer.getParameterBeanName(sql);
    }

    protected List<String> getParameterBeanPropertyTypeList(final String sql) {
        return _outsideSqlMarkAnalyzer.getParameterBeanPropertyTypeList(sql);
    }

    protected List<String> getPrimaryKeyColumnNameList(final String sql) {
        return _outsideSqlMarkAnalyzer.getPrimaryKeyColumnNameList(sql);
    }

    protected String resolveEntityNameIfNeeds(String className, File file) {
        return _sqlFileNameResolver.resolveEntityNameIfNeeds(className, file.getName());
    }

    protected String resolvePmbNameIfNeeds(String className, File file) {
        return _sqlFileNameResolver.resolvePmbNameIfNeeds(className, file.getName());
    }

    protected String removeBlockComment(final String sql) {
        return Srl.removeBlockComment(sql);
    }

    protected String removeLineComment(final String sql) {
        return Srl.removeLineComment(sql); // with removing CR
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DBDef currentDBDef() {
        return getBasicProperties().getCurrentDBDef();
    }

    protected DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }

    protected DfDatabaseProperties getDatabaseProperties() {
        return getProperties().getDatabaseProperties();
    }
}
