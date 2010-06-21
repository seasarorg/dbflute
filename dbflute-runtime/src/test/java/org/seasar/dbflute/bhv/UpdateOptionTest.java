package org.seasar.dbflute.bhv;

import java.util.Stack;

import org.seasar.dbflute.cbean.SpecifyQuery;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.dbflute.mock.MockConditionBean;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.7.2 (2010/06/18 Friday)
 */
public class UpdateOptionTest extends PlainTestCase {

    public void test_buildStatement_plus() throws Exception {
        // ## Arrange ##
        final String columnDbName = "PURCHASE_COUNT";
        final Stack<String> columnStack = new Stack<String>();
        UpdateOption<MockConditionBean> option = createTarget(columnStack);
        option.self(new SpecifyQuery<MockConditionBean>() {
            public void specify(MockConditionBean cb) {
                columnStack.add(columnDbName);
            }
        }).plus(1);

        // ## Act ##
        option.resolveSpecification(new MockConditionBean());
        String actual = option.buildStatement(columnDbName, new ColumnSqlName(columnDbName));

        // ## Arrange ##
        log(actual);
        assertEquals(columnDbName + " + 1", actual);
        assertNull(option.buildStatement("NO_NAME", new ColumnSqlName("NO_NAME")));
    }

    public void test_buildStatement_minus() throws Exception {
        // ## Arrange ##
        final String columnDbName = "PURCHASE_COUNT";
        final Stack<String> columnStack = new Stack<String>();
        UpdateOption<MockConditionBean> option = createTarget(columnStack);
        option.self(new SpecifyQuery<MockConditionBean>() {
            public void specify(MockConditionBean cb) {
                columnStack.add(columnDbName);
            }
        }).minus(3);

        // ## Act ##
        option.resolveSpecification(new MockConditionBean());
        String actual = option.buildStatement(columnDbName, new ColumnSqlName("\"" + columnDbName + "\""));

        // ## Arrange ##
        log(actual);
        assertEquals("\"" + columnDbName + "\"" + " - 3", actual);
        assertNull(option.buildStatement("NO_NAME", new ColumnSqlName("NO_NAME")));
    }

    public void test_buildStatement_multiply() throws Exception {
        // ## Arrange ##
        final String columnDbName = "PURCHASE_COUNT";
        final Stack<String> columnStack = new Stack<String>();
        UpdateOption<MockConditionBean> option = createTarget(columnStack);
        option.self(new SpecifyQuery<MockConditionBean>() {
            public void specify(MockConditionBean cb) {
                columnStack.add(columnDbName);
            }
        }).multiply(100);

        // ## Act ##
        option.resolveSpecification(new MockConditionBean());
        String actual = option.buildStatement(columnDbName, new ColumnSqlName(columnDbName));

        // ## Arrange ##
        log(actual);
        assertEquals(columnDbName + " * 100", actual);
        assertNull(option.buildStatement("NO_NAME", new ColumnSqlName("NO_NAME")));
    }

    public void test_buildStatement_multiply_plus() throws Exception {
        // ## Arrange ##
        final String columnDbName = "PURCHASE_COUNT";
        final Stack<String> columnStack = new Stack<String>();
        UpdateOption<MockConditionBean> option = createTarget(columnStack);
        option.self(new SpecifyQuery<MockConditionBean>() {
            public void specify(MockConditionBean cb) {
                columnStack.add(columnDbName);
            }
        }).multiply(2).plus(1);

        // ## Act ##
        option.resolveSpecification(new MockConditionBean());
        String actual = option.buildStatement(columnDbName, new ColumnSqlName(columnDbName));

        // ## Arrange ##
        log(actual);
        assertEquals("(" + columnDbName + " * 2) + 1", actual);
        assertNull(option.buildStatement("NO_NAME", new ColumnSqlName("NO_NAME")));
    }

    public void test_buildStatement_multiply_plus_minus() throws Exception {
        // ## Arrange ##
        final String columnDbName = "PURCHASE_COUNT";
        final Stack<String> columnStack = new Stack<String>();
        UpdateOption<MockConditionBean> option = createTarget(columnStack);
        option.self(new SpecifyQuery<MockConditionBean>() {
            public void specify(MockConditionBean cb) {
                columnStack.add(columnDbName);
            }
        }).multiply(2).plus(1).minus(3);

        // ## Act ##
        option.resolveSpecification(new MockConditionBean());
        String actual = option.buildStatement(columnDbName, new ColumnSqlName(columnDbName));

        // ## Arrange ##
        log(actual);
        assertEquals("((" + columnDbName + " * 2) + 1) - 3", actual);
        assertNull(option.buildStatement("NO_NAME", new ColumnSqlName("NO_NAME")));
    }

    protected UpdateOption<MockConditionBean> createTarget(final Stack<String> columnStack) {
        return new UpdateOption<MockConditionBean>() {
            @Override
            protected void assertSpecifiedColumn(MockConditionBean cb, String columnDbName) {
                // no check
            }

            @Override
            protected String getSpecifiedColumnDbNameAsOne(MockConditionBean cb) {
                return columnStack.pop();
            }
        };
    }
}
