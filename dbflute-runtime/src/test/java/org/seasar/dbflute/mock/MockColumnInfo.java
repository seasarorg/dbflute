package org.seasar.dbflute.mock;

import java.lang.reflect.Method;

import org.seasar.dbflute.dbmeta.DBMeta.OptimisticLockType;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;

public class MockColumnInfo extends ColumnInfo {

    public MockColumnInfo() {
        super(new MockDBMeta(), "mock", "mock", null, "mock", true, "mock", Integer.class, true, false, "INTEGER", 3,
                0, false, OptimisticLockType.NONE, "mock", null, null, null);
    }

    @Override
    protected PropertyGateway findPropertyGateway() {
        return null;
    }

    @Override
    protected Method findReadMethod() {
        return null;
    }

    @Override
    protected Method findWriteMethod() {
        return null;
    }
}
