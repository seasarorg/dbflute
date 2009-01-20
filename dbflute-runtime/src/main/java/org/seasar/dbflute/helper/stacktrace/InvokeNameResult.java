package org.seasar.dbflute.helper.stacktrace;

/**
 * @author jflute
 */
public class InvokeNameResult {

    // ==========================================================================================
    //                                                                                  Attribute
    //                                                                                  =========
    protected String _simpleClassName;
    protected String _methodName;
    protected String _invokeName;
    protected int _foundIndex;
    protected int _foundFirstIndex;

    // ==========================================================================================
    //                                                                               Manipulation
    //                                                                               ============
    public int getNextStartIndex() {
        return _foundIndex + 1;
    }

    public void beEmptyResult() {
        _simpleClassName = null;
        _invokeName = ""; // As Default
    }

    // ==========================================================================================
    //                                                                              Determination
    //                                                                              =============
    public boolean isEmptyResult() {
        return _simpleClassName == null;
    }
    
    // ==========================================================================================
    //                                                                                   Accessor
    //                                                                                   ========
    public String getSimpleClassName() {
        return _simpleClassName;
    }
    public void setSimpleClassName(String simpleClassName) {
        _simpleClassName = simpleClassName;
    }
    public String getMethodName() {
        return _methodName;
    }
    public void setMethodName(String methodName) {
        _methodName = methodName;
    }
    public String getInvokeName() {
        return _invokeName;
    }
    public void setInvokeName(String invokeName) {
        _invokeName = invokeName;
    }
    public int getFoundIndex() {
        return _foundIndex;
    }
    public void setFoundIndex(int foundIndex) {
        _foundIndex = foundIndex;
    }
    public int getFoundFirstIndex() {
        return _foundFirstIndex;
    }
    public void setFoundFirstIndex(int foundFirstIndex) {
        _foundFirstIndex = foundFirstIndex;
    }
}