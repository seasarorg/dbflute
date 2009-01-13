package org.dbflute.helper.token.line;

/**
 * @author DBFlute(AutoGenerator)
 */
public interface LineToken {

    public java.util.List<String> tokenize(String lineString, LineTokenizingOption lineTokenizingOption);

    public String make(java.util.List<String> valueList, LineMakingOption lineMakingOption);
}