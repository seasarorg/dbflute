package org.seasar.dbflute.helper.jdbc.sqlfile;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.seasar.dbflute.unit.DfDBFluteTestCase;
import org.seasar.dbflute.util.io.DfResourceUtil;

/**
 * @author jflute
 * @since 0.5.7 (2007/11/03 Saturday)
 */
public class DfSqlFileGetterTest extends DfDBFluteTestCase {

    @Test
    public void test_getSqlFileList() throws Exception {
        log("test_getSqlFileList()");
        final DfSqlFileGetter getter = new DfSqlFileGetter();

        final File buildDir = DfResourceUtil.getBuildDir(DfSqlFileGetterTest.class);
        final String canonicalPath = buildDir.getCanonicalPath();

        final List<File> sqlFileList = getter.getSqlFileList(canonicalPath);
        if (sqlFileList.size() < 2) {
            Assert.fail();
        }
        for (File file : sqlFileList) {
            log(file);
        }
    }
}
