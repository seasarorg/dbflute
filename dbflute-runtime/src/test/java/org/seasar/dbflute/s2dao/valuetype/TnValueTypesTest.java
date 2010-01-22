package org.seasar.dbflute.s2dao.valuetype;

import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.seasar.dbflute.jdbc.Classification;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.mock.MockValueType;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.6.4 (2010/01/22 Friday)
 */
public class TnValueTypesTest extends PlainTestCase {

    public void test_getBasicValueType_enum_priority_plain() throws Exception {
        // ## Arrange ##
        Class<?> keyType = TestPlainStatus.class;
        MockValueType mockValueType = new MockValueType();

        try {
            // ## Act ##
            TnValueTypes.registerBasicValueType(keyType, mockValueType);
            ValueType valueType = TnValueTypes.getValueType(keyType);

            // ## Assert ##
            assertNotSame(TnValueTypes.CLASSIFICATION, valueType);
            assertEquals(mockValueType, valueType);
        } finally {
            TnValueTypes.removeBasicValueType(keyType);
        }
    }

    public void test_getBasicValueType_enum_priority_classification() throws Exception {
        // ## Arrange ##
        Class<?> keyType = TestClassificationStatus.class; // embedded
        MockValueType mockValueType = new MockValueType();

        try {
            // ## Act ##
            TnValueTypes.registerBasicValueType(TestPlainStatus.class, mockValueType);
            TnValueTypes.registerBasicValueType(Enum.class, mockValueType);
            ValueType valueType = TnValueTypes.getValueType(keyType);

            // ## Assert ##
            assertNotSame(mockValueType, valueType);
            assertEquals(TnValueTypes.CLASSIFICATION, valueType);
        } finally {
            TnValueTypes.removeBasicValueType(keyType);
        }
    }

    public void test_getBasicValueType_enum_priority_enumKey() throws Exception {
        // ## Arrange ##
        Class<?> keyType = Enum.class;
        MockValueType mockValueType = new MockValueType();

        try {
            // ## Act ##
            TnValueTypes.registerBasicValueType(keyType, mockValueType);
            ValueType valueType = TnValueTypes.getValueType(keyType);

            // ## Assert ##
            assertNotSame(TnValueTypes.CLASSIFICATION, valueType);
            assertEquals(mockValueType, valueType);
        } finally {
            TnValueTypes.removeBasicValueType(Enum.class);
        }
    }

    private static enum TestPlainStatus {
        FML, PRV, WDL
    }

    private static enum TestClassificationStatus implements Classification {
        FML, PRV, WDL;

        public String code() {
            return null;
        }

        public String alias() {
            return null;
        }

        public DataType dataType() {
            return null;
        }
    }

    public void test_registerBasicValueType_interface_basic() throws Exception {
        // ## Arrange ##
        MockValueType mockValueType = new MockValueType();

        // ## Act ##
        TnValueTypes.registerBasicValueType(FilenameFilter.class, mockValueType);
        ValueType interfaceValueType = TnValueTypes.getBasicInterfaceValueType(FilenameFilter.class);

        // ## Assert ##
        assertEquals(mockValueType, interfaceValueType);
        assertNull(TnValueTypes.getBasicInterfaceValueType(FileFilter.class));
    }

    public void test_registerBasicValueType_interface_threadSafe() throws Exception {
        // ## Arrange ##
        ExecutionCreator<ValueType> creator = new ExecutionCreator<ValueType>() {
            public Execution<ValueType> create() {
                return new Execution<ValueType>() {
                    public ValueType execute() {
                        MockValueType mockValueType = new MockValueType();
                        if (Thread.currentThread().getId() % 2 == 0) {
                            TnValueTypes.registerBasicValueType(FilenameFilter.class, mockValueType);
                            ValueType interfaceValueType = TnValueTypes
                                    .getBasicInterfaceValueType(FilenameFilter.class);
                            return interfaceValueType;
                        } else {
                            TnValueTypes.registerBasicValueType(FileFilter.class, mockValueType);
                            ValueType interfaceValueType = TnValueTypes.getBasicInterfaceValueType(FileFilter.class);
                            return interfaceValueType;
                        }
                    }
                };
            }
        };

        // ## Act & Assert ##
        fireSameExecution(creator);
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    private <RESULT> List<RESULT> fireSameExecution(ExecutionCreator<RESULT> creator) {
        // ## Arrange ##
        ExecutorService service = Executors.newCachedThreadPool();
        int threadCount = 10;
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch goal = new CountDownLatch(threadCount);
        Execution<RESULT> execution = creator.create();
        List<Future<RESULT>> futureList = new ArrayList<Future<RESULT>>();
        for (int i = 0; i < threadCount; i++) {
            Future<RESULT> future = service.submit(createCallable(execution, ready, start, goal));
            futureList.add(future);
        }

        // ## Act ##
        // Start!
        start.countDown();
        try {
            // Wait until all threads are finished!
            goal.await();
        } catch (InterruptedException e) {
            String msg = "goal.await() was interrupted!";
            throw new IllegalStateException(msg, e);
        }
        log("All threads are finished!");

        // ## Assert ##
        List<RESULT> resultList = new ArrayList<RESULT>();
        for (Future<RESULT> future : futureList) {
            try {
                RESULT result = future.get();
                assertNotNull(result);
                resultList.add(result);
            } catch (InterruptedException e) {
                String msg = "future.get() was interrupted!";
                throw new IllegalStateException(msg, e);
            } catch (ExecutionException e) {
                String msg = "Failed to execute!";
                throw new IllegalStateException(msg, e.getCause());
            }
        }
        return resultList;
    }

    private static interface ExecutionCreator<RESULT> {
        Execution<RESULT> create();
    }

    private static interface Execution<RESULT> {
        RESULT execute();
    }

    private <RESULT> Callable<RESULT> createCallable(final Execution<RESULT> execution, final CountDownLatch ready,
            final CountDownLatch start, final CountDownLatch goal) {
        return new Callable<RESULT>() {
            public RESULT call() {
                try {
                    ready.countDown();
                    try {
                        start.await();
                    } catch (InterruptedException e) {
                        String msg = "start.await() was interrupted!";
                        throw new IllegalStateException(msg, e);
                    }
                    RESULT result = execution.execute();
                    return result;
                } finally {
                    goal.countDown();
                }
            }
        };
    }
}
