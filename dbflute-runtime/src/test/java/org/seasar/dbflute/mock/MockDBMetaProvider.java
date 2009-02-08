package org.seasar.dbflute.mock;

import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.DBMetaProvider;

public class MockDBMetaProvider implements DBMetaProvider {

    public DBMeta provideDBMeta(String tableFlexibleName) {
        return new MockDBMeta();
    }

    public DBMeta provideDBMetaChecked(String tableFlexibleName) {
        return new MockDBMeta();
    }
}
