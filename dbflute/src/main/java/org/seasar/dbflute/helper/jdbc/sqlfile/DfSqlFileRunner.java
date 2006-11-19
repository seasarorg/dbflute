package org.seasar.dbflute.helper.jdbc.sqlfile;

import java.io.File;

public interface DfSqlFileRunner {
    public void setSrc(File src);
    public void runTransaction();
    public int getGoodSqlCount();
    public int getTotalSqlCount();
}
