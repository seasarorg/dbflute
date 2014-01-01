/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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

import java.util.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.thread.exception.ThreadFireFailureException;

/**
 * @author jflute
 * @since 1.0.5A (2013/10/17 Thursday)
 */
public class CountDownRaceLatch {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(CountDownRaceLatch.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final int _runnerCount;
    protected CountDownLatch _yourLatch;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public CountDownRaceLatch(int runnerCount) {
        _runnerCount = runnerCount;
    }

    // ===================================================================================
    //                                                                     CountDown Latch
    //                                                                     ===============
    public void await() {
        final CountDownLatch latch;
        final boolean last;
        synchronized (this) {
            latch = prepareLatch();
            last = (doGetCount(latch) == 1);
            if (last) {
                if (_log.isDebugEnabled()) {
                    _log.debug("...Restarting count down race");
                }
                clearLatch();
            }
            doCountDown(latch); // ready go if last
        }
        if (!last) {
            if (_log.isDebugEnabled()) {
                _log.debug("...Awaiting all runners coming here");
            }
            doAwait(latch);
        }
    }

    protected CountDownLatch prepareLatch() {
        if (_yourLatch == null) {
            _yourLatch = new CountDownLatch(_runnerCount);
        }
        return _yourLatch;
    }

    protected void clearLatch() {
        _yourLatch = null;
    }

    protected long doGetCount(CountDownLatch latch) {
        return latch.getCount();
    }

    protected void doCountDown(CountDownLatch latch) {
        latch.countDown();
    }

    protected void doAwait(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            String msg = "Failed to await by your latch: latch=" + latch;
            throw new ThreadFireFailureException(msg, e);
        }
    }

    public synchronized void reset() {
        if (_yourLatch == null) {
            return;
        }
        final long count = _yourLatch.getCount();
        if (count > 0) {
            if (_log.isDebugEnabled()) {
                _log.debug("...Resetting your latch: count=" + count);
            }
            for (int i = 0; i < count; i++) {
                _yourLatch.countDown();
            }
        }
        _yourLatch = null;
    }
}
