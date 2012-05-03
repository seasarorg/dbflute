package org.seasar.dbflute.properties.assistant.freegenerate;

import org.seasar.dbflute.properties.assistant.freegenerate.DfFreeGenRequest.DfFreeGenerateResourceType;

/**
 * @author jflute
 */
public class DfFreeGenResource {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DfFreeGenerateResourceType _resourceType;
    protected final String _resourceFile;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfFreeGenResource(DfFreeGenerateResourceType resourceType, String resourceFile) {
        _resourceType = resourceType;
        _resourceFile = resourceFile;
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isResourceTypeXls() {
        return DfFreeGenerateResourceType.XLS.equals(_resourceType);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{resourceType=" + _resourceType + ", resourceFile=" + _resourceFile + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DfFreeGenerateResourceType getResourceType() {
        return _resourceType;
    }

    public String getResourceFile() {
        return _resourceFile;
    }
}
