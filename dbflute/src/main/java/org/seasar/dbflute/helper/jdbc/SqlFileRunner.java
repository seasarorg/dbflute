package org.seasar.dbflute.helper.jdbc;

import java.io.File;

public interface SqlFileRunner {
    public void setSrc(File src);
    public void runTransaction();
    public int getGoodSqlCount();
    public int getTotalSqlCount();
}
