/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.sql2entity.bqp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.exception.DfBehaviorNotFoundException;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.language.grammar.DfGrammarInfo;
import org.seasar.dbflute.logic.generate.packagepath.DfPackagePathHandler;
import org.seasar.dbflute.logic.sql2entity.analyzer.DfOutsideSqlFile;
import org.seasar.dbflute.logic.sql2entity.analyzer.DfOutsideSqlPack;
import org.seasar.dbflute.logic.sql2entity.analyzer.DfSql2EntityMarkAnalyzer;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;
import org.seasar.dbflute.properties.DfOutsideSqlProperties;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfBehaviorQueryPathSetupper {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfBehaviorQueryPathSetupper.class);

    public static final String KEY_PATH = "path";
    public static final String KEY_SUB_DIRECTORY_PATH = "subDirectoryPath";
    public static final String KEY_ENTITY_NAME = "entityName";
    public static final String KEY_BEHAVIOR_NAME = "behaviorName";
    public static final String KEY_BEHAVIOR_QUERY_PATH = "behaviorQueryPath";
    public static final String KEY_SQL_AP = "sqlAp";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_SQL = "sql";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfBehaviorQueryPathSetupper() {
    }

    // ===================================================================================
    //                                                                              Set up 
    //                                                                              ======
    /**
     * @param sqlFileList The list of SQL file. (NotNull)
     */
    public void setupBehaviorQueryPath(DfOutsideSqlPack sqlFileList) {
        if (getOutsideSqlProperties().isSuppressBehaviorQueryPath()) {
            _log.info("*Behavior Query Path is suppressed!");
            return;
        }
        if (sqlFileList.isEmpty()) {
            return;
        }
        final Map<String, Map<String, String>> behaviorQueryPathMap = doExtractBehaviorQueryPathMap(sqlFileList);
        reflectBehaviorQueryPath(behaviorQueryPathMap);
    }

    // ===================================================================================
    //                                                                             Extract
    //                                                                             =======
    /**
     * Extract the basic map of behavior query path
     * @param outsideSqlPack The pack object for outside-SQL file. (NotNull)
     * @return The basic map of behavior query path. The key is slash-path. (NotNull, EmptyAllowd: means not found)
     */
    public Map<String, Map<String, String>> extractBasicBqpMap(DfOutsideSqlPack outsideSqlPack) {
        return doExtractBehaviorQueryPathMap(outsideSqlPack);
    }

    /**
     * Extract the case insensitive map of table behavior query path.
     * <pre>
     * map:{
     *     [tablePropertyName] = map:{
     *         [behaviorQueryPath] = map:{
     *             ; path = [value]
     *             ; behaviorName = [value]
     *             ; entityName = [value]
     *             ; subDirectoryPath = [value]
     *             ; behaviorQueryPath = [value]
     *         }
     *     } 
     * }
     * </pre>
     * @param outsideSqlPack The pack object for outside-SQL file. (NotNull)
     * @return The case insensitive map of behavior query path per table. The key is table name. (NotNull, EmptyAllowd: means not found)
     */
    public Map<String, Map<String, Map<String, String>>> extractTableBqpMap(DfOutsideSqlPack outsideSqlPack) {
        final Map<String, Map<String, Map<String, String>>> resultMap = StringKeyMap.createAsFlexibleOrdered();
        if (outsideSqlPack.isEmpty()) {
            return resultMap;
        }
        final Map<String, Map<String, String>> bqpMap = doExtractBehaviorQueryPathMap(outsideSqlPack);
        final Map<File, Map<String, Map<String, String>>> resourceMap = createReflectResourceMap(bqpMap);
        final Set<Entry<File, Map<String, Map<String, String>>>> entrySet = resourceMap.entrySet();
        for (Entry<File, Map<String, Map<String, String>>> entry : entrySet) {
            final File bsbhvFile = entry.getKey();
            String tableKeyName = bsbhvFile.getName();
            final int extIndex = tableKeyName.lastIndexOf(".");
            if (extIndex >= 0) {
                tableKeyName = tableKeyName.substring(0, extIndex);
            }
            final DfBasicProperties basicProperties = getBasicProperties();
            final String bhvSuffix;
            final String projectPrefix;
            if (isApplicationBehaviorProject()) {
                bhvSuffix = "Bhv" + getApplicationBehaviorAdditionalSuffix();
                projectPrefix = getLibraryProjectPrefix();
            } else { // main is here
                bhvSuffix = "Bhv";
                projectPrefix = basicProperties.getProjectPrefix();
            }
            if (tableKeyName.endsWith(bhvSuffix)) {
                tableKeyName = tableKeyName.substring(0, tableKeyName.length() - bhvSuffix.length());
            }
            if (Srl.is_NotNull_and_NotTrimmedEmpty(projectPrefix) && tableKeyName.startsWith(projectPrefix)) {
                tableKeyName = tableKeyName.substring(projectPrefix.length());
            }
            final String basePrefix = basicProperties.getBasePrefix();
            if (Srl.is_NotNull_and_NotTrimmedEmpty(basePrefix) && tableKeyName.startsWith(basePrefix)) {
                tableKeyName = tableKeyName.substring(basePrefix.length(), tableKeyName.length());
            }
            resultMap.put(tableKeyName, entry.getValue());
        }
        return resultMap;
    }

    // ===================================================================================
    //                                                                        Main Process
    //                                                                        ============
    /**
     * @param outsideSqlPack The pack object for outside-SQL file. (NotNull)
     * @return The map of behavior query path. (NotNull)
     */
    protected Map<String, Map<String, String>> doExtractBehaviorQueryPathMap(DfOutsideSqlPack outsideSqlPack) {
        final String exbhvName;
        {
            String exbhvPackage = getBasicProperties().getExtendedBehaviorPackage();
            if (exbhvPackage.contains(".")) {
                exbhvPackage = exbhvPackage.substring(exbhvPackage.lastIndexOf(".") + ".".length());
            }
            exbhvName = exbhvPackage;
        }
        final Map<String, Map<String, String>> behaviorQueryPathMap = new LinkedHashMap<String, Map<String, String>>();
        gatherBehaviorQueryPathInfo(behaviorQueryPathMap, outsideSqlPack, exbhvName);
        return behaviorQueryPathMap;
    }

    /**
     * @param behaviorQueryPathMap The empty map of behavior query path. (NotNull)
     * @param outsideSqlPack The pack object for outside-SQL file. (NotNull)
     * @param exbhvName The name of extended behavior. (NotNull)
     */
    protected void gatherBehaviorQueryPathInfo(Map<String, Map<String, String>> behaviorQueryPathMap,
            DfOutsideSqlPack outsideSqlPack, String exbhvName) {
        final String exbhvMark = "/" + exbhvName + "/";
        final String exbhvSuffix = "Bhv";
        final Pattern behaviorQueryPathPattern = Pattern.compile(".+" + exbhvMark + ".+" + exbhvSuffix + "_.+.sql$");
        for (DfOutsideSqlFile outsideSqlFile : outsideSqlPack.getOutsideSqlFileList()) {
            final String path = getSlashPath(outsideSqlFile.getPhysicalFile());
            final Matcher matcher = behaviorQueryPathPattern.matcher(path);
            if (!matcher.matches()) {
                continue;
            }
            String subDirectoryPath = null;
            String simpleFileName = path.substring(path.lastIndexOf(exbhvMark) + exbhvMark.length());
            if (simpleFileName.contains("/")) {
                subDirectoryPath = simpleFileName.substring(0, simpleFileName.lastIndexOf("/"));
                simpleFileName = simpleFileName.substring(simpleFileName.lastIndexOf("/") + "/".length());
            }
            final int behaviorNameMarkIndex = simpleFileName.indexOf(exbhvSuffix + "_");
            final int behaviorNameEndIndex = behaviorNameMarkIndex + exbhvSuffix.length();
            final int behaviorQueryPathStartIndex = behaviorNameMarkIndex + (exbhvSuffix + "_").length();
            final int behaviorQueryPathEndIndex = simpleFileName.lastIndexOf(".sql");
            final String entityName = simpleFileName.substring(0, behaviorNameMarkIndex);
            final String behaviorName = simpleFileName.substring(0, behaviorNameEndIndex);
            final String behaviorQueryPath = simpleFileName.substring(behaviorQueryPathStartIndex,
                    behaviorQueryPathEndIndex);
            final Map<String, String> behaviorQueryElement = new LinkedHashMap<String, String>();
            behaviorQueryElement.put(KEY_PATH, path);
            behaviorQueryElement.put(KEY_SUB_DIRECTORY_PATH, subDirectoryPath);
            behaviorQueryElement.put(KEY_ENTITY_NAME, entityName);
            behaviorQueryElement.put(KEY_BEHAVIOR_NAME, behaviorName);
            behaviorQueryElement.put(KEY_BEHAVIOR_QUERY_PATH, behaviorQueryPath);
            if (outsideSqlFile.isSqlAp()) {
                behaviorQueryElement.put(KEY_SQL_AP, "true");
            }
            behaviorQueryPathMap.put(path, behaviorQueryElement);

            // setup informations in the SQL file
            setupInfoInSqlFile(outsideSqlFile, behaviorQueryElement);
        }
    }

    protected void setupInfoInSqlFile(DfOutsideSqlFile outsideSqlFile, Map<String, String> elementMap) {
        final DfSql2EntityMarkAnalyzer analyzer = new DfSql2EntityMarkAnalyzer();
        final BufferedReader reader = new BufferedReader(newInputStreamReader(outsideSqlFile));
        final StringBuilder sb = new StringBuilder();
        try {
            while (true) {
                final String line = reader.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line).append(ln());
            }
        } catch (IOException e) {
            String msg = "Failed to read the SQL: " + outsideSqlFile;
            throw new IllegalStateException(msg, e);
        }
        final String sql = sb.toString();
        final String customizeEntity = analyzer.getCustomizeEntityName(sql);
        final String parameterBean = analyzer.getParameterBeanName(sql);
        elementMap.put("customizeEntity", customizeEntity);
        elementMap.put("parameterBean", parameterBean);
        elementMap.put("cursor", analyzer.isCursor(sql) ? "cursor" : null);
        elementMap.put(KEY_TITLE, analyzer.getTitle(sql));
        elementMap.put(KEY_DESCRIPTION, analyzer.getDescription(sql));
        elementMap.put(KEY_SQL, sql);
    }

    protected InputStreamReader newInputStreamReader(DfOutsideSqlFile sqlFile) {
        final String encoding = getProperties().getOutsideSqlProperties().getSqlFileEncoding();
        try {
            return new InputStreamReader(new FileInputStream(sqlFile.getPhysicalFile()), encoding);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("The file does not exist: " + sqlFile, e);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("The encoding is unsupported: " + encoding, e);
        }
    }

    /**
     * @param behaviorQueryPathMap The map of behavior query path. (NotNull)
     */
    protected void reflectBehaviorQueryPath(Map<String, Map<String, String>> behaviorQueryPathMap) {
        final Map<File, Map<String, Map<String, String>>> reflectResourceMap = createReflectResourceMap(behaviorQueryPathMap);
        if (reflectResourceMap.isEmpty()) {
            return;
        }
        handleReflectResource(reflectResourceMap);
    }

    /**
     * @param behaviorQueryPathMap The map of behavior query path. (NotNull)
     * @return The map of reflect resource. (NotNull)
     * @throws DfBehaviorNotFoundException When the behavior is not found.
     */
    protected Map<File, Map<String, Map<String, String>>> createReflectResourceMap(
            Map<String, Map<String, String>> behaviorQueryPathMap) {
        if (behaviorQueryPathMap.isEmpty()) {
            return new HashMap<File, Map<String, Map<String, String>>>();
        }
        final String outputDir;
        {
            String tmp = getBasicProperties().getGenerateOutputDirectory();
            if (tmp.endsWith("/")) {
                tmp = tmp.substring(0, tmp.length() - "/".length());
            }
            outputDir = tmp;
        }
        final String bsbhvPackage = getBasicProperties().getBaseBehaviorPackage();
        final DfPackagePathHandler packagePathHandler = new DfPackagePathHandler(getBasicProperties());
        packagePathHandler.setFileSeparatorSlash(true);
        final String bsbhvPathBase = outputDir + "/" + packagePathHandler.getPackageAsPath(bsbhvPackage);

        final File bsbhvDir = new File(bsbhvPathBase);
        if (!bsbhvDir.exists()) {
            _log.warn("The base behavior directory was not found: bsbhvDir=" + bsbhvDir);
            return new HashMap<File, Map<String, Map<String, String>>>();
        }
        final Map<String, File> bsbhvFileMap = createBsBhvFileMap(bsbhvDir);

        final Map<File, Map<String, Map<String, String>>> reflectResourceMap = new HashMap<File, Map<String, Map<String, String>>>();
        final Set<Entry<String, Map<String, String>>> entrySet = behaviorQueryPathMap.entrySet();
        for (Entry<String, Map<String, String>> entry : entrySet) {
            final Map<String, String> behaviorQueryElementMap = entry.getValue();
            final String behaviorName = behaviorQueryElementMap.get(KEY_BEHAVIOR_NAME); // on SQL file
            final String behaviorQueryPath = behaviorQueryElementMap.get(KEY_BEHAVIOR_QUERY_PATH);
            final String sqlApExp = behaviorQueryElementMap.get(KEY_SQL_AP);
            if (sqlApExp != null && "true".equalsIgnoreCase(sqlApExp)) {
                continue; // out of target for ApplicationOutsideSql
            }

            // relation point between SQL file and BsBhv
            File bsbhvFile = bsbhvFileMap.get(behaviorName);
            if (bsbhvFile == null) {
                if (isApplicationBehaviorProject()) {
                    final String projectPrefixLib = getLibraryProjectPrefix();
                    String retryName = behaviorName;
                    if (retryName.startsWith(projectPrefixLib)) { // ex) LbFooBhv --> FooBhv
                        retryName.substring(projectPrefixLib.length());
                    }
                    final String projectPrefixAp = getBasicProperties().getProjectPrefix();
                    retryName = projectPrefixAp + retryName; // ex) FooBhv --> BpFooBhv
                    final String additionalSuffix = getApplicationBehaviorAdditionalSuffix();
                    retryName = retryName + additionalSuffix; // ex) BpFooBhv --> BpFooBhvAp
                    bsbhvFile = bsbhvFileMap.get(retryName);
                }
                if (bsbhvFile == null) {
                    throwBehaviorNotFoundException(bsbhvFileMap, behaviorQueryElementMap, bsbhvPathBase);
                }
            }

            Map<String, Map<String, String>> resourceElementMap = reflectResourceMap.get(bsbhvFile);
            if (resourceElementMap == null) {
                resourceElementMap = new LinkedHashMap<String, Map<String, String>>();
                reflectResourceMap.put(bsbhvFile, resourceElementMap);
            }
            if (!resourceElementMap.containsKey(behaviorQueryPath)) {
                resourceElementMap.put(behaviorQueryPath, behaviorQueryElementMap);
            }
        }
        return reflectResourceMap;
    }

    protected Map<String, File> createBsBhvFileMap(File bsbhvDir) {
        final String classFileExtension = getBasicProperties().getLanguageDependencyInfo().getGrammarInfo()
                .getClassFileExtension();
        final FileFilter filefilter = new FileFilter() {
            public boolean accept(File file) {
                final String path = file.getPath();
                if (isApplicationBehaviorProject()) {
                    final String additionalSuffix = getApplicationBehaviorAdditionalSuffix();
                    final String bhvSuffix = "Bhv" + additionalSuffix;
                    return path.endsWith(bhvSuffix + "." + classFileExtension);
                } else {
                    return path.endsWith("Bhv." + classFileExtension);
                }
            }
        };
        final List<File> bsbhvFileList = Arrays.asList(bsbhvDir.listFiles(filefilter));
        final Map<String, File> bsbhvFileMap = new HashMap<String, File>();
        for (File bsbhvFile : bsbhvFileList) {
            String path = getSlashPath(bsbhvFile);
            path = path.substring(0, path.lastIndexOf("." + classFileExtension));
            final String bsbhvSimpleName;
            if (path.contains("/")) {
                bsbhvSimpleName = path.substring(path.lastIndexOf("/") + "/".length());
            } else {
                bsbhvSimpleName = path;
            }
            final String behaviorName = removeBasePrefix(bsbhvSimpleName);
            bsbhvFileMap.put(behaviorName, bsbhvFile);
        }
        return bsbhvFileMap;
    }

    protected void throwBehaviorNotFoundException(Map<String, File> bsbhvFileMap,
            Map<String, String> behaviorQueryElementMap, String bsbhvPathBase) {
        final String path = behaviorQueryElementMap.get(KEY_PATH);
        final String behaviorName = behaviorQueryElementMap.get(KEY_BEHAVIOR_NAME);
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The behavior was Not Found!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm the existence of the behavior." + ln();
        msg = msg + "And confirm your SQL file name." + ln();
        msg = msg + ln();
        msg = msg + "[Your SQL File]" + ln() + path + ln();
        msg = msg + ln();
        msg = msg + "[Not Found Behavior]" + ln() + behaviorName + ln();
        msg = msg + ln();
        msg = msg + "[Behavior Directory]" + ln() + bsbhvPathBase + ln();
        msg = msg + ln();
        msg = msg + "[Behavior List]" + ln() + bsbhvFileMap.keySet() + ln();
        msg = msg + "* * * * * * * * * */" + ln();
        throw new DfBehaviorNotFoundException(msg);
    }

    /**
     * @param reflectResourceMap The map of reflect resource. (NotNull)
     */
    protected void handleReflectResource(Map<File, Map<String, Map<String, String>>> reflectResourceMap) {
        final Set<Entry<File, Map<String, Map<String, String>>>> entrySet = reflectResourceMap.entrySet();
        for (Entry<File, Map<String, Map<String, String>>> entry : entrySet) {
            final File bsbhvFile = entry.getKey();
            final Map<String, Map<String, String>> resourceElementMap = entry.getValue();
            writeBehaviorQueryPath(bsbhvFile, resourceElementMap);
        }
    }

    /**
     * @param bsbhvFile The file of base behavior. (NotNull)
     * @param resourceElementMap The map of resource element. (NotNull) 
     */
    protected void writeBehaviorQueryPath(File bsbhvFile, Map<String, Map<String, String>> resourceElementMap) {
        final String encoding = getBasicProperties().getSourceFileEncoding();
        final String lineSep = getBasicProperties().getSourceCodeLineSeparator();
        final DfGrammarInfo grammarInfo = getBasicProperties().getLanguageDependencyInfo().getGrammarInfo();
        final String behaviorQueryPathBeginMark = getBasicProperties().getBehaviorQueryPathBeginMark();
        final String behaviorQueryPathEndMark = getBasicProperties().getBehaviorQueryPathEndMark();
        final DfDocumentProperties docprop = getDocumentProperties();
        final BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(bsbhvFile), encoding));
        } catch (UnsupportedEncodingException e) {
            String msg = "The encoding is unsupported: encoding=" + encoding;
            throw new IllegalStateException(msg, e);
        } catch (FileNotFoundException e) {
            String msg = "The file of base behavior was not found: bsbhvFile=" + bsbhvFile;
            throw new IllegalStateException(msg, e);
        }
        String lineString = null;
        final StringBuilder sb = new StringBuilder();
        try {
            boolean targetArea = false;
            boolean done = false;
            while (true) {
                lineString = br.readLine();
                if (lineString == null) {
                    if (targetArea) {
                        String msg = "The end mark of behavior query path was not found:";
                        msg = msg + " bsbhvFile=" + bsbhvFile;
                        throw new IllegalStateException(msg);
                    }
                    break;
                }
                if (targetArea) {
                    if (lineString.contains(behaviorQueryPathEndMark)) {
                        targetArea = false;
                    } else {
                        continue;
                    }
                }
                sb.append(lineString).append(lineSep);
                if (!done && lineString.contains(behaviorQueryPathBeginMark)) {
                    targetArea = true;
                    final String indent = lineString.substring(0, lineString.indexOf(behaviorQueryPathBeginMark));
                    final Set<String> behaviorQueryPathSet = resourceElementMap.keySet();
                    for (String behaviorQueryPath : behaviorQueryPathSet) {
                        final Map<String, String> behaviorQueryElementMap = resourceElementMap.get(behaviorQueryPath);
                        final StringBuilder definitionLineSb = new StringBuilder();

                        final String title = behaviorQueryElementMap.get("title");
                        if (title != null && title.trim().length() > 0) {
                            final String resolvedTitle = docprop.resolveTextForJavaDoc(title, indent);
                            final String commentExp;
                            if (getBasicProperties().isTargetLanguageCSharp()) {
                                commentExp = indent + "/// <summary>" + resolvedTitle + " </summary>" + lineSep;
                            } else {
                                commentExp = indent + "/** " + resolvedTitle + " */" + lineSep; // basically here
                            }
                            definitionLineSb.append(commentExp);
                        }

                        definitionLineSb.append(indent);
                        definitionLineSb.append(grammarInfo.getPublicStaticDefinition());
                        final String subDirectoryPath = behaviorQueryElementMap.get(KEY_SUB_DIRECTORY_PATH);
                        if (Srl.is_NotNull_and_NotTrimmedEmpty(subDirectoryPath)) {
                            final String subDirectoryName = Srl.replace(subDirectoryPath, "/", "_");
                            final String subDirectoryValue = Srl.replace(subDirectoryPath, "/", ":");
                            definitionLineSb.append(" String PATH_");
                            definitionLineSb.append(subDirectoryName).append("_").append(behaviorQueryPath);
                            definitionLineSb.append(" = \"");
                            definitionLineSb.append(subDirectoryValue).append(":").append(behaviorQueryPath);
                            definitionLineSb.append("\";");
                        } else {
                            definitionLineSb.append(" String PATH_").append(behaviorQueryPath);
                            definitionLineSb.append(" = \"").append(behaviorQueryPath).append("\";");
                        }

                        definitionLineSb.append(lineSep);
                        sb.append(definitionLineSb);
                    }
                    done = true;
                }
            }
            if (!done) {
                _log.warn("*The mark of behavior query path was not found: " + bsbhvFile);
            }
        } catch (IOException e) {
            String msg = "BufferedReader.readLine() threw the exception: current line=" + lineString;
            throw new IllegalStateException(msg, e);
        } finally {
            try {
                br.close();
            } catch (IOException ignored) {
                _log.warn(ignored.getMessage());
            }
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(bsbhvFile), encoding));
            bw.write(sb.toString());
            bw.flush();
        } catch (UnsupportedEncodingException e) {
            String msg = "The encoding is unsupported: encoding=" + encoding;
            throw new IllegalStateException(msg, e);
        } catch (FileNotFoundException e) {
            String msg = "The file of base behavior was not found: bsbhvFile=" + bsbhvFile;
            throw new IllegalStateException(msg, e);
        } catch (IOException e) {
            String msg = "BufferedWriter.write() threw the exception: bsbhvFile=" + bsbhvFile;
            throw new IllegalStateException(msg, e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ignored) {
                    _log.warn(ignored.getMessage());
                }
            }
        }
    }

    protected String removeBasePrefix(String bsbhvSimpleName) {
        final String projectPrefix = getBasicProperties().getProjectPrefix();
        final String basePrefix = getBasicProperties().getBasePrefix();
        final String prefix = projectPrefix + basePrefix;
        if (!bsbhvSimpleName.startsWith(prefix)) {
            return bsbhvSimpleName;
        }
        final int prefixLength = prefix.length();
        if (!Character.isUpperCase(bsbhvSimpleName.substring(prefixLength).charAt(0))) {
            return bsbhvSimpleName;
        }
        if (bsbhvSimpleName.length() <= prefixLength) {
            return bsbhvSimpleName;
        }
        return projectPrefix + bsbhvSimpleName.substring(prefixLength);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    public String replaceString(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    public String getSlashPath(File file) {
        return replaceString(file.getPath(), getFileSeparator(), "/");
    }

    public String getFileSeparator() {
        return File.separator;
    }

    public String ln() {
        return "\n";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }

    protected DfOutsideSqlProperties getOutsideSqlProperties() {
        return getProperties().getOutsideSqlProperties();
    }

    protected DfDocumentProperties getDocumentProperties() {
        return getProperties().getDocumentProperties();
    }

    protected DfLittleAdjustmentProperties getLittleAdjustmentProperties() {
        return getProperties().getLittleAdjustmentProperties();
    }

    protected boolean isApplicationBehaviorProject() {
        return getBasicProperties().isApplicationBehaviorProject();
    }

    protected String getLibraryProjectPrefix() {
        return getBasicProperties().getLibraryProjectPrefix();
    }

    protected String getApplicationBehaviorAdditionalSuffix() {
        return getBasicProperties().getApplicationBehaviorAdditionalSuffix();
    }
}