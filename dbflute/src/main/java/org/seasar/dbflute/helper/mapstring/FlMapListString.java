package org.seasar.dbflute.helper.mapstring;

import java.util.List;
import java.util.Map;

/**
 * MapList-String.
 * <p>
 * <pre>
 * # 以下のような文字列(マップリストストリング)からをマップやリストの生成を提供するインターフェース。
 * # 
 * #   ex) map:{key1=value1,key2=list:{value21,value22,value23},key3=map:{key31=value31}}
 * #   ex) list:{key1=value1,key2=list:{value21,value22,value23},key3=map:{key31=value31}}
 * #
 * </pre>
 * @author jflute
 */
public interface FlMapListString {

    public static final String DEFAULT_MAP_MARK = "map:";

    public static final String DEFAULT_LIST_MARK = "list:";

    public static final String DEFAULT_DELIMITER = ",";

    public static final String DEFAULT_START_BRACE = "{";

    public static final String DEFAULT_END_BRACE = "}";

    public static final String DEFAULT_EQUAL = "=";

    // ==========================================================================================
    //                                                                                     Setter
    //                                                                                     ======
    /**
     * Set delimiter.
     * 
     * @param delimiter Delimiter.
     */
    public void setDelimiter(String delimiter);

    /**
     * Set start brace.
     * 
     * @param startBrace Start brace.
     */
    public void setStartBrace(String startBrace);

    /**
     * Set end brace.
     * 
     * @param endBrace End brace.
     */
    public void setEndBrace(String endBrace);
    
    // ==========================================================================================
    //                                                                                   Generate
    //                                                                                   ========
    /**
     * Generate map from map-string.
     * 
     * @param mapString Map-string (NotNull)
     * @return Generated map. (NotNull)
     */
    public Map<String, Object> generateMap(String mapString);

    /**
     * Generate map from list-string. {Implement}
     * 
     * @param listString List-string (NotNull)
     * @return Generated list. (NotNull)
     */
    public List<Object> generateList(String listString);
}