package org.seasar.dbflute.bhv;

import java.util.Stack;

import org.seasar.dbflute.cbean.SpecifyQuery;
import org.seasar.dbflute.mock.MockConditionBean;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.7.2 (2010/06/18 Friday)
 */
public class UpdateOptionTest extends PlainTestCase {

    public void test_buildStatement_plus() throws Exception {
        // ## Arrange ##
        final String columnName = "PURCHASE_COUNT";
        final Stack<String> columnStack = new Stack<String>();
        UpdateOption<MockConditionBean> option = createTarget(columnStack);
        option.self(new SpecifyQuery<MockConditionBean>() {
            public void specify(MockConditionBean cb) {
                columnStack.add(columnName);
            }
        }).plus(1);

        // ## Act ##
        option.resolveSpeicification(new MockConditionBean());
        String actual = option.buildStatement(columnName);

        // ## Arrange ##
        log(actual);
        assertEquals(columnName + " + 1", actual);
        assertNull(option.buildStatement("NO_NAME"));
    }

    public void test_buildStatement_minus() throws Exception {
        // ## Arrange ##
        final String columnName = "PURCHASE_COUNT";
        final Stack<String> columnStack = new Stack<String>();
        UpdateOption<MockConditionBean> option = createTarget(columnStack);
        option.self(new SpecifyQuery<MockConditionBean>() {
            public void specify(MockConditionBean cb) {
                columnStack.add(columnName);
            }
        }).minus(3);

        // ## Act ##
        option.resolveSpeicification(new MockConditionBean());
        String actual = option.buildStatement(columnName);

        // ## Arrange ##
        log(actual);
        assertEquals(columnName + " - 3", actual);
        assertNull(option.buildStatement("NO_NAME"));
    }

    public void test_buildStatement_multiply() throws Exception {
        // ## Arrange ##
        final String columnName = "PURCHASE_COUNT";
        final Stack<String> columnStack = new Stack<String>();
        UpdateOption<MockConditionBean> option = createTarget(columnStack);
        option.self(new SpecifyQuery<MockConditionBean>() {
            public void specify(MockConditionBean cb) {
                columnStack.add(columnName);
            }
        }).multiply(100);

        // ## Act ##
        option.resolveSpeicification(new MockConditionBean());
        String actual = option.buildStatement(columnName);

        // ## Arrange ##
        log(actual);
        assertEquals(columnName + " * 100", actual);
        assertNull(option.buildStatement("NO_NAME"));
    }

    public void test_buildStatement_multiply_plus() throws Exception {
        // ## Arrange ##
        final String columnName = "PURCHASE_COUNT";
        final Stack<String> columnStack = new Stack<String>();
        UpdateOption<MockConditionBean> option = createTarget(columnStack);
        option.self(new SpecifyQuery<MockConditionBean>() {
            public void specify(MockConditionBean cb) {
                columnStack.add(columnName);
            }
        }).multiply(2).plus(1);

        // ## Act ##
        option.resolveSpeicification(new MockConditionBean());
        String actual = option.buildStatement(columnName);

        // ## Arrange ##
        log(actual);
        assertEquals("(" + columnName + " * 2) + 1", actual);
        assertNull(option.buildStatement("NO_NAME"));
    }

    public void test_buildStatement_multiply_plus_minus() throws Exception {
        // ## Arrange ##
        final String columnName = "PURCHASE_COUNT";
        final Stack<String> columnStack = new Stack<String>();
        UpdateOption<MockConditionBean> option = createTarget(columnStack);
        option.self(new SpecifyQuery<MockConditionBean>() {
            public void specify(MockConditionBean cb) {
                columnStack.add(columnName);
            }
        }).multiply(2).plus(1).minus(3);

        // ## Act ##
        option.resolveSpeicification(new MockConditionBean());
        String actual = option.buildStatement(columnName);

        // ## Arrange ##
        log(actual);
        assertEquals("((" + columnName + " * 2) + 1) - 3", actual);
        assertNull(option.buildStatement("NO_NAME"));
    }

    protected UpdateOption<MockConditionBean> createTarget(final Stack<String> columnStack) {
        return new UpdateOption<MockConditionBean>() {
            @Override
            protected String getSpecifiedColumnNameAsOne(MockConditionBean cb) {
                return columnStack.pop();
            }
        };
    }
}
