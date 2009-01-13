package org.dbflute.util;

/**
 * @author DBFlute(AutoGenerator)
 */
public class SimpleSystemUtil {

    // ===================================================================================
    //                                                                      Line Separator
    //                                                                      ==============
    public static String getLineSeparator() {
        // /- - - - - - - - - - - - - - - - - - - - - -
        // Because 'CR + LF' caused many trouble!
		// And Now 'LF' have little trouble. 
		// 
        // return System.getProperty("line.separator");
        // - - - - - - - - - -/
		return "\n";
    }
}
