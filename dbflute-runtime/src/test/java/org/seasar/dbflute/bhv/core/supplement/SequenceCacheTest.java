package org.seasar.dbflute.bhv.core.supplement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.seasar.dbflute.bhv.core.supplement.SequenceCache.SequenceRealExecutor;
import org.seasar.dbflute.unit.PlainTestCase;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 * @since 0.9.6.4 (2010/01/15 Friday)
 */
public class SequenceCacheTest extends PlainTestCase {

    public void test_nextval_BigDecimal() {
        // ## Arrange ##
        int incrementSize = 10;
        SequenceCache cache = createSequenceCache(incrementSize, BigDecimal.class);
        BigDecimalResultExecutor executor = new BigDecimalResultExecutor(incrementSize);

        // ## Act & Assert ##
        assertEquals(1, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(2, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(3, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(4, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(5, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(6, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(7, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(8, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(9, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(10, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(11, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(12, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(13, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(14, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(15, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(16, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(17, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(18, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(19, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(20, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(21, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(22, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(23, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(24, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(25, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(26, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(27, convertBigDecimalToInteger(cache.nextval(executor)));
        assertEquals(3, executor.getCount());
    }

    public void test_nextval_Long() {
        // ## Arrange ##
        int incrementSize = 10;
        SequenceCache cache = createSequenceCache(incrementSize, Long.class);
        LongResultExecutor executor = new LongResultExecutor(incrementSize);

        // ## Act & Assert ##
        assertEquals(1, convertLongToInteger(cache.nextval(executor)));
        assertEquals(2, convertLongToInteger(cache.nextval(executor)));
        assertEquals(3, convertLongToInteger(cache.nextval(executor)));
        assertEquals(4, convertLongToInteger(cache.nextval(executor)));
        assertEquals(5, convertLongToInteger(cache.nextval(executor)));
        assertEquals(6, convertLongToInteger(cache.nextval(executor)));
        assertEquals(7, convertLongToInteger(cache.nextval(executor)));
        assertEquals(8, convertLongToInteger(cache.nextval(executor)));
        assertEquals(9, convertLongToInteger(cache.nextval(executor)));
        assertEquals(10, convertLongToInteger(cache.nextval(executor)));
        assertEquals(11, convertLongToInteger(cache.nextval(executor)));
        assertEquals(12, convertLongToInteger(cache.nextval(executor)));
        assertEquals(13, convertLongToInteger(cache.nextval(executor)));
        assertEquals(14, convertLongToInteger(cache.nextval(executor)));
        assertEquals(15, convertLongToInteger(cache.nextval(executor)));
        assertEquals(2, executor.getCount());
    }

    public void test_nextval_Integer() {
        // ## Arrange ##
        int incrementSize = 10;
        SequenceCache cache = createSequenceCache(incrementSize, Integer.class);
        IntegerResultExecutor executor = new IntegerResultExecutor(incrementSize);

        // ## Act & Assert ##
        assertEquals(1, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(2, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(3, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(4, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(5, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(6, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(7, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(8, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(9, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(10, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(11, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(12, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(13, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(14, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(15, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(16, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(17, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(18, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(19, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(20, convertIntegerToInteger(cache.nextval(executor)));
        assertEquals(2, executor.getCount());
    }

    public void test_nextval_threadSafe() {
        // ## Arrange ##
        final int incrementSize = 20;
        final SequenceCache cache = createSequenceCache(incrementSize, Integer.class);
        final BigDecimalResultExecutor executor = new BigDecimalResultExecutor(incrementSize);
        ExecutionCreator<Set<Integer>> creator = new ExecutionCreator<Set<Integer>>() {
            public Execution<Set<Integer>> create() {
                return new Execution<Set<Integer>>() {
                    public Set<Integer> execute() {
                        final Set<Integer> valSet = new HashSet<Integer>();
                        for (int i = 0; i < 5; i++) {
                            valSet.add((Integer) cache.nextval(executor));
                        }
                        return valSet;
                    }
                };
            }
        };

        // ## Act & Assert ##
        log("...Executing all threads");
        HashSet<Integer> allAllSet = new HashSet<Integer>();
        for (int i = 0; i < 30; i++) {
            List<Set<Integer>> resultList = fireSameExecution(creator);
            HashSet<Integer> allSet = new HashSet<Integer>();
            for (Set<Integer> set : resultList) {
                allSet.addAll(set);
            }
            assertEquals(50, allSet.size());
            allAllSet.addAll(allSet);
            log(allSet);
        }
        assertEquals(1500, allAllSet.size());
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected int convertBigDecimalToInteger(Object obj) {
        assertEquals(BigDecimal.class, obj.getClass());
        return DfTypeUtil.toInteger(obj).intValue();
    }

    protected int convertLongToInteger(Object obj) {
        assertEquals(Long.class, obj.getClass());
        return DfTypeUtil.toInteger(obj).intValue();
    }

    protected int convertIntegerToInteger(Object obj) {
        assertEquals(Integer.class, obj.getClass());
        return DfTypeUtil.toInteger(obj).intValue();
    }

    protected SequenceCache createSequenceCache(int incrementSize, Class<?> resultType) {
        return new SequenceCache(new BigDecimal(incrementSize), resultType);
    }

    protected class BigDecimalResultExecutor implements SequenceRealExecutor {
        protected int _count;
        protected int _incrementSize;

        public BigDecimalResultExecutor(int incrementSize) {
            _incrementSize = incrementSize;
        }

        public Object execute() {
            ++_count;
            return new BigDecimal((_incrementSize * (_count - 1)) + 1);
        }

        public int getCount() {
            return _count;
        }
    }

    protected class LongResultExecutor implements SequenceRealExecutor {
        protected int _count;
        protected int _incrementSize;

        public LongResultExecutor(int incrementSize) {
            _incrementSize = incrementSize;
        }

        public Object execute() {
            ++_count;
            long result = (_incrementSize * (_count - 1)) + 1;
            return result;
        }

        public int getCount() {
            return _count;
        }
    }

    protected class IntegerResultExecutor implements SequenceRealExecutor {
        protected int _count;
        protected int _incrementSize;

        public IntegerResultExecutor(int incrementSize) {
            _incrementSize = incrementSize;
        }

        public Object execute() {
            ++_count;
            int result = (_incrementSize * (_count - 1)) + 1;
            return result;
        }

        public int getCount() {
            return _count;
        }
    }

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
