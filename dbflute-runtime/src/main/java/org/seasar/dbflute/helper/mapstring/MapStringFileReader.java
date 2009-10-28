package org.seasar.dbflute.helper.mapstring;

import java.util.List;
import java.util.Map;

/**
 * The reader for map string file.
 * @author jflute
 * @since 0.9.6 (2009/10/28 Wednesday)
 */
public interface MapStringFileReader {

    /**
     * Read the map string file. <br />
     * If the type of values is various type, this method is available. <br />
     * A trimmed line that starts with '#' is treated as line comment. <br />
     * This is the most basic method here.
     * <pre>
     * map:{
     *     ; key1 = string-value1
     *     ; key2 = list:{element1 ; element2 }
     *     ; key3 = map:{key1 = value1 ; key2 = value2 }
     *     ; ... = ...
     * }
     * </pre>
     * @param path The file path. (NotNull)
     * @param encoding The file encoding. (NotNull)
     * @return The read map. (NotNull)
     */
    Map<String, Object> readMap(String path, String encoding);

    /**
     * Read the map string file as string value. <br />
     * If the type of all values is string type, this method is available. <br />
     * A trimmed line that starts with '#' is treated as line comment.
     * <pre>
     * ex)
     * map:{
     *     ; key1 = string-value1
     *     ; key2 = string-value2
     *     ; ... = ...
     * }
     * </pre>
     * @param path The file path. (NotNull)
     * @param encoding The file encoding. (NotNull)
     * @return The read map. (NotNull)
     */
    Map<String, String> readMapAsStringValue(String path, String encoding);

    /**
     * Read the map string file as list string value. <br />
     * If the type of all values is list string type, this method is available. <br />
     * A trimmed line that starts with '#' is treated as line comment.
     * <pre>
     * ex)
     * map:{
     *     ; key1 = list:{string-element1 ; string-element2 ; ...}
     *     ; key2 = list:{string-element1 ; string-element2 ; ...}
     *     ; ... = list:{...}
     * }
     * </pre>
     * @param path The file path. (NotNull)
     * @param encoding The file encoding. (NotNull)
     * @return The read map. (NotNull)
     */
    Map<String, List<String>> readMapAsListStringValue(String path, String encoding);

    /**
     * Read the map string file as map string value. <br />
     * If the type of all values is map string type, this method is available. <br />
     * A trimmed line that starts with '#' is treated as line comment.
     * <pre>
     * ex)
     * map:{
     *     ; key1 = map:{string-key1 = string-value1 ; string-key2 = string-value2 }
     *     ; key2 = map:{string-key1 = string-value1 ; string-key2 = string-value2 }
     *     ; ... = map:{...}
     * }
     * </pre>
     * @param path The file path. (NotNull)
     * @param encoding The file encoding. (NotNull)
     * @return The read map. (NotNull)
     */
    Map<String, Map<String, String>> readMapAsMapStringValue(String path, String encoding);
}
