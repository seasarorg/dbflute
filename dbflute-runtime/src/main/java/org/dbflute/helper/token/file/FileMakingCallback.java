package org.dbflute.helper.token.file;

/**
 * @author DBFlute(AutoGenerator)
 */
public interface FileMakingCallback {

    /**
     * Get file-making header information.
     * <pre>
     * You should return your row resource for file-making.
     * It continues invoking until this method returns null.
     * </pre>
     * @return File-making header information. (Nullable)
     */
    public FileMakingRowResource getRowResource();
}
