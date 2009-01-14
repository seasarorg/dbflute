package org.seasar.dbflute.helper.token.file.impl;

import org.seasar.dbflute.helper.token.file.FileMakingCallback;
import org.seasar.dbflute.helper.token.file.FileMakingOption;
import org.seasar.dbflute.helper.token.file.FileMakingRowResource;
import org.seasar.dbflute.helper.token.file.FileMakingSimpleFacade;
import org.seasar.dbflute.helper.token.file.FileToken;


/**
 * @author DBFlute(AutoGenerator)
 */
public class FileMakingSimpleFacadeImpl implements FileMakingSimpleFacade {

    protected FileToken _fileToken = new FileTokenImpl();

    public void setFileToken(FileToken fileToken) {
        this._fileToken = fileToken;
    }

    /**
     * Make token-file from row-list.
     * 
     * @param filename Output target file name. (NotNull)
     * @param rowList Row-list composed of value-list. (NotNull)
     * @param fileMakingOption File-making option. (NotNull and Required{encoding and delimiter})
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void makeFromRowList(final String filename, final java.util.List<java.util.List<String>> rowList, final FileMakingOption fileMakingOption) throws java.io.FileNotFoundException, java.io.IOException {
        final FileMakingCallback fileMakingCallback = new FileMakingCallback() {
            protected int rowCount = 0;
            public FileMakingRowResource getRowResource() {
                ++rowCount;
                if (rowList.size() < rowCount) {
                    return null;// The End!
                }
                final java.util.List<String> valueList = (java.util.List<String>)rowList.get(rowCount - 1);
                final FileMakingRowResource fileMakingRowResource = new FileMakingRowResource();
                fileMakingRowResource.setValueList(valueList);
                return fileMakingRowResource;
            }
        };
        _fileToken.make(filename, fileMakingCallback, fileMakingOption);
    }

    /**
     * Make bytes from row-list.
     * 
     * @param rowList Row-list composed of value-list. (NotNull)
     * @param fileMakingOption File-making option. (NotNull and Required{encoding and delimiter})
     * @return Result byte array. (NotNull)
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public byte[] makeFromRowList(final java.util.List<java.util.List<String>> rowList, final FileMakingOption fileMakingOption) throws java.io.FileNotFoundException, java.io.IOException {
        final FileMakingCallback fileMakingCallback = new FileMakingCallback() {
            protected int rowCount = 0;
            public FileMakingRowResource getRowResource() {
                ++rowCount;
                if (rowList.size() < rowCount) {
                    return null;// The End!
                }
                final java.util.List<String> valueList = (java.util.List<String>)rowList.get(rowCount - 1);
                final FileMakingRowResource fileMakingRowResource = new FileMakingRowResource();
                fileMakingRowResource.setValueList(valueList);
                return fileMakingRowResource;
            }
        };
        final java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        _fileToken.make(baos, fileMakingCallback, fileMakingOption);
        return baos.toByteArray();
    }
}
