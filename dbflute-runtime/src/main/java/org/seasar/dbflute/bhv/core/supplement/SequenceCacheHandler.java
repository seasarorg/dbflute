/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.bhv.core.supplement;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

/**
 * The handler of sequence cache.
 * @author jflute
 * @since 0.9.6.4 (2010/01/15 Friday)
 */
public class SequenceCacheHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Map<String, SequenceCache> _sequenceCacheMap = new ConcurrentHashMap<String, SequenceCache>();

    // ===================================================================================
    //                                                                            Handling
    //                                                                            ========
    public SequenceCache findSequenceCache(String sequenceName, DataSource dataSource, Integer incrementSize,
            Class<?> resultType) {
        if (incrementSize == null || incrementSize <= 1) {
            return null;
        }
        final String key = generateKey(sequenceName, dataSource);
        SequenceCache sequenceCache = getSequenceCache(key);
        if (sequenceCache != null) {
            return sequenceCache;
        }
        synchronized (_sequenceCacheMap) {
            sequenceCache = getSequenceCache(key);
            if (sequenceCache != null) {
                return sequenceCache;
            }
            sequenceCache = createSequenceCache(sequenceName, dataSource, incrementSize, resultType);
            _sequenceCacheMap.put(key, sequenceCache);
        }
        if (sequenceCache == null) {
            String msg = "createSequenceCache() should not return null:";
            msg = msg + " sequenceName=" + sequenceName + " dataSource=" + dataSource;
            throw new IllegalStateException(msg);
        }
        return sequenceCache;
    }

    protected SequenceCache getSequenceCache(String key) {
        return _sequenceCacheMap.get(key);
    }

    protected SequenceCache createSequenceCache(String sequenceName, DataSource dataSource, Integer incrementSize,
            Class<?> resultType) {
        return new SequenceCache(new BigDecimal(incrementSize), resultType);
    }

    protected String generateKey(String sequenceName, DataSource dataSource) {
        // TODO
        return sequenceName + "." + dataSource.getClass().getName() + "@" + dataSource.hashCode();
    }
}
