package org.seasar.dbflute.mock;

import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import org.seasar.dbflute.outsidesql.OutsideSqlContext;

public class MockOutsideSqlContext extends OutsideSqlContext {

    public MockOutsideSqlContext() {
        super(new DBMetaProvider() {
            public DBMeta provideDBMeta(String tableFlexibleName) {
                return null;
            }

            public DBMeta provideDBMetaChecked(String tableFlexibleName) {
                return null;
            }
        }, null);
    }
}
