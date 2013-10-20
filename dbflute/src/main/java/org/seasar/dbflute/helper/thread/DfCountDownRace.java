/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.helper.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.thread.exception.DfThreadFireFailureException;

/**
 * @author jflute
 * @since 1.0.5A (2013/10/17 Thursday)
 */
public class DfCountDownRace {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfCountDownRace.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final int _runnerCount;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfCountDownRace(int runnerCount) {
        if (runnerCount < 1) {
            String msg = "The argument 'runnerCount' should not be minus or zero: " + runnerCount;
            throw new IllegalArgumentException(msg);
        }
        _runnerCount = runnerCount;
    }

    // ===================================================================================
    //                                                                         Thread Fire
    //                                                                         ===========
    public void readyGo(DfCountDownRaceExecution execution) {
        if (execution == null) {
            String msg = "The argument 'execution' should be not null.";
            throw new IllegalArgumentException(msg);
        }
        doReadyGo(execution);
    }

    protected void doReadyGo(DfCountDownRaceExecution execution) {
        final ExecutorService service = Executors.newCachedThreadPool();
        final CountDownLatch ready = new CountDownLatch(_runnerCount);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch goal = new CountDownLatch(_runnerCount);
        final DfCountDownRaceLatch yourLatch = new DfCountDownRaceLatch(_runnerCount);
        final List<Future<Void>> futureList = new ArrayList<Future<Void>>();
        final Object lockObj = new Object();
        for (int i = 0; i < _runnerCount; i++) { // basically synchronized with parameter size
            final Callable<Void> callable = createCallable(execution, ready, start, goal, yourLatch, lockObj);
            final Future<Void> future = service.submit(callable);
            futureList.add(future);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("...Ready Go! Count Down Race just begun! (runner=" + _runnerCount + ")");
        }
        start.countDown(); // fire!
        try {
            goal.await(); // wait until all threads are finished
            if (_log.isDebugEnabled()) {
                _log.debug("All runners finished line! (runner=" + _runnerCount + ")");
            }
        } catch (InterruptedException e) {
            String msg = "goal.await() was interrupted!";
            throw new IllegalStateException(msg, e);
        }

        handleFuture(futureList);
    }

    protected void handleFuture(final List<Future<Void>> futureList) {
        for (Future<Void> future : futureList) {
            try {
                future.get();
            } catch (InterruptedException e) {
                String msg = "future.get() was interrupted!";
                throw new IllegalStateException(msg, e);
            } catch (ExecutionException e) {
                String msg = "Failed to fire the thread: " + future;
                throw new DfThreadFireFailureException(msg, e.getCause());
            }
        }
    }

    // ===================================================================================
    //                                                                            Callable
    //                                                                            ========
    protected Callable<Void> createCallable(final DfCountDownRaceExecution execution, final CountDownLatch ready,
            final CountDownLatch start, final CountDownLatch goal, final DfCountDownRaceLatch yourLatch,
            final Object lockObj) {
        return new Callable<Void>() {
            public Void call() { // each thread here
                final long threadId = Thread.currentThread().getId();
                try {
                    ready.countDown();
                    try {
                        start.await();
                    } catch (InterruptedException e) {
                        String msg = "start.await() was interrupted: start=" + start;
                        throw new IllegalStateException(msg, e);
                    }
                    RuntimeException cause = null;
                    try {
                        execution.execute(createRunner(threadId, yourLatch, lockObj));
                    } catch (RuntimeException e) {
                        cause = e;
                    }
                    if (cause != null) {
                        throw cause;
                    }
                } finally {
                    goal.countDown();
                    yourLatch.reset(); // to release waiting threads
                }
                return null;
            }
        };
    }

    protected DfCountDownRaceRunner createRunner(long threadId, DfCountDownRaceLatch yourLatch, Object lockObj) {
        return new DfCountDownRaceRunner(threadId, yourLatch, lockObj);
    }
}
