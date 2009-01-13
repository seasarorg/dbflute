package org.dbflute.helper.token.file;

/**
 * @author DBFlute(AutoGenerator)
 */
public interface FileMakingSimpleFacade {

    /**
     * Make token-file from row-list.
     * 
     * @param filename Output target file name. (NotNull)
     * @param rowList Row-list composed of value-list. (NotNull)
     * @param fileMakingOption File-making option. (NotNull and Required{encoding and delimiter})
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void makeFromRowList(final String filename, final java.util.List<java.util.List<String>> rowList, final FileMakingOption fileMakingOption) throws java.io.FileNotFoundException, java.io.IOException;

    /**
     * Make bytes from row-list.
     * 
     * @param rowList Row-list composed of value-list. (NotNull)
     * @param fileMakingOption File-making option. (NotNull and Required{encoding and delimiter})
     * @return Result byte array. (NotNull)
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public byte[] makeFromRowList(final java.util.List<java.util.List<String>> rowList, final FileMakingOption fileMakingOption) throws java.io.FileNotFoundException, java.io.IOException;
}
