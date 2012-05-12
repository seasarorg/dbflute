package org.seasar.dbflute.properties.assistant.freegen.prop;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenTable;
import org.seasar.dbflute.util.Srl;
import org.seasar.dbflute.util.Srl.ScopeInfo;

/**
 * @author jflute
 */
public class DfPropTableLoader {

    // ===================================================================================
    //                                                                          Load Table
    //                                                                          ==========
    // ; resourceMap = map:{
    //     ; resourceType = PROP
    //     ; resourceFile = ../../.../foo.properties
    // }
    // ; outputMap = map:{
    //     ; templateFile = MessageDef.vm
    //     ; outputDirectory = ../src/main/java
    //     ; package = org.seasar.dbflute...
    //     ; className = MessageDef
    // }
    public DfFreeGenTable loadTable(String requestName, String resourceFile, Map<String, Object> tableMap,
            Map<String, Map<String, String>> mappingMap) {
        BufferedReader br = null;
        try {
            final Properties prop = readProperties(resourceFile);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(resourceFile), "ISO-8859-1"));
            final List<Map<String, Object>> columnList = readColumnList(br, prop);
            final String tableName = Srl.substringLastFront((Srl.substringLastRear(resourceFile, "/")));
            return new DfFreeGenTable(tableName, columnList);
        } catch (IOException e) {
            String msg = "Failed to read the properties: requestName=" + requestName + " resourceFile=" + resourceFile;
            throw new IllegalStateException(msg, e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    protected Properties readProperties(String resourceFile) throws IOException {
        final FileInputStream fis = new FileInputStream(resourceFile);
        final Properties prop = new Properties();
        prop.load(fis);
        return prop;
    }

    protected List<Map<String, Object>> readColumnList(BufferedReader br, Properties prop) throws IOException {
        final List<Map<String, Object>> columnList = new ArrayList<Map<String, Object>>();
        String previousComment = null;
        while (true) {
            final String line = br.readLine();
            if (line == null) {
                break;
            }
            final String ltrimmedLine = Srl.ltrim(line);
            if (ltrimmedLine.startsWith("# ")) { // comment lines
                final String commentCandidate = Srl.substringFirstRear(ltrimmedLine, "#").trim();
                if (ltrimmedLine.startsWith("# ")) { // 100% comment
                    previousComment = commentCandidate;
                } else {
                    if (!ltrimmedLine.contains(".")) {
                        previousComment = commentCandidate;
                    } else { // e.g. #foo.bar.qux (comment out???)
                        previousComment = null;
                    }
                }
                continue;
            }
            if (!ltrimmedLine.contains("=")) {
                continue;
            }
            final Map<String, Object> columnMap = new LinkedHashMap<String, Object>();
            final String key = Srl.substringFirstFront(ltrimmedLine, "=").trim();
            columnMap.put("key", key);

            final String defName = Srl.replace(key, ".", "_").toUpperCase();
            columnMap.put("defName", defName);

            final String camelizedName = Srl.camelize(defName);
            columnMap.put("camelizedName", camelizedName);
            columnMap.put("capCamelName", Srl.initCap(camelizedName));
            columnMap.put("uncapCamelName", Srl.initUncap(camelizedName));

            final String value = prop.getProperty(key); // by Properties
            columnMap.put("value", value); // basically unused
            final List<ScopeInfo> scopeList = Srl.extractScopeList(value, "{", "}");
            final List<ScopeInfo> variableScopeList = new ArrayList<ScopeInfo>();
            for (ScopeInfo scopeInfo : scopeList) {
                final String content = scopeInfo.getContent();
                try {
                    Integer.valueOf(content);
                    variableScopeList.add(scopeInfo);
                } catch (NumberFormatException ignored) {
                }
            }
            final List<Integer> variableNumberList = new ArrayList<Integer>();
            final StringBuilder argSb = new StringBuilder();
            for (ScopeInfo scopeInfo : variableScopeList) {
                final Integer number = Integer.valueOf(scopeInfo.getContent());
                variableNumberList.add(number);
                if (argSb.length() > 0) {
                    argSb.append(", ");
                }
                argSb.append("String arg").append(number);
            }
            columnMap.put("variableCount", variableScopeList.size());
            columnMap.put("variableNumberList", variableNumberList);
            columnMap.put("variableScopeList", scopeList);

            columnMap.put("comment", previousComment != null ? previousComment : "");

            columnList.add(columnMap);
            previousComment = null;
        }
        return columnList;
    }
}
