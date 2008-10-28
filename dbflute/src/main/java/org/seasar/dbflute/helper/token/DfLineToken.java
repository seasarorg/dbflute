package org.seasar.dbflute.helper.token;

/**
 * @author DBFlute(AutoGenerator)
 */
public interface DfLineToken {

    public java.util.List<String> tokenize(String lineString, DfLineTokenizingOption lineTokenizingOption);

    public String make(java.util.List<String> valueList, DfLineMakingOption lineMakingOption);
}