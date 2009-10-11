package org.seasar.dbflute.helper.language.properties;

/**
 * @author jflute
 */
public interface DfDefaultDBFluteDicon {

    /**
     * @return The namespace of dbflute dicon. (NotNull)
     */
    public String getDBFluteDiconNamespace();

    /**
     * @return The file name of dbflute dicon. (NotNull)
     */
    public String getDBFluteDiconFileName();

    /**
     * @return The resource name of J2EE dicon. (NotNull)
     */
    public String getJ2eeDiconResourceName();

    /**
     * @return The component name of 'requiredTx'. (NotNull)
     */
    public String getRequiredTxComponentName();

    /**
     * @return The component name of 'requiresNewTx'. (NotNull)
     */
    public String getRequiresNewTxComponentName();
}