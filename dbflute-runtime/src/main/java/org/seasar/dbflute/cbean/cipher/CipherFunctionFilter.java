package org.seasar.dbflute.cbean.cipher;

/**
 * @author jflute
 * @since 0.9.8.4 (2011/05/21 Saturday)
 */
public interface CipherFunctionFilter {

    /**
     * Filter the value expression by functions to encrypt.
     * @param valueExp The value expression, column name or bind expression. (NotNull)
     * @return The filtered expression. (NotNull)
     */
    String encrypt(String valueExp);

    /**
     * Filter the value expression by functions to decrypt.
     * @param valueExp The value expression, column name or bind expression. (NotNull)
     * @return The filtered expression. (NotNull)
     */
    String decrypt(String valueExp);
}
