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

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.XLog;
import org.seasar.dbflute.exception.SequenceCacheIllegalStateException;
import org.seasar.dbflute.exception.SequenceCacheSizeNotDividedIncrementSizeException;
import org.seasar.dbflute.resource.ResourceContext;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.DfSystemUtil;

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
    protected SequenceCacheKeyGenerator _sequenceCacheKeyGenerator;

    // ===================================================================================
    //                                                                            Handling
    //                                                                            ========
    public SequenceCache findSequenceCache(String sequenceName, DataSource dataSource, Integer cacheSize,
            Class<?> resultType) {
        if (cacheSize == null || cacheSize <= 1) { // if it is not cache valid size
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
            if (isLogEnabled()) {
                log("...Initializing sequence cache: " + sequenceName + ":cache(" + cacheSize + ")");
            }
            sequenceCache = createSequenceCache(sequenceName, dataSource, cacheSize, resultType);
            _sequenceCacheMap.put(key, sequenceCache);
        }
        if (sequenceCache == null) {
            String msg = "createSequenceCache() should not return null:";
            msg = msg + " sequenceName=" + sequenceName + " dataSource=" + dataSource;
            throw new SequenceCacheIllegalStateException(msg);
        }
        return sequenceCache;
    }

    protected SequenceCache getSequenceCache(String key) {
        return _sequenceCacheMap.get(key);
    }

    protected SequenceCache createSequenceCache(String sequenceName, DataSource dataSource, Integer cacheSize,
            Class<?> resultType) {
        return new SequenceCache(new BigDecimal(cacheSize), resultType);
    }

    protected String generateKey(String sequenceName, DataSource dataSource) {
        if (_sequenceCacheKeyGenerator != null) {
            return _sequenceCacheKeyGenerator.generateKey(sequenceName, dataSource);
        }
        return sequenceName; // as default
    }

    // ===================================================================================
    //                                                                      Union Sequence
    //                                                                      ==============
    /**
     * Filter the SQL for next value. <br />
     * This method uses ResourceContext.
     * @param cacheSize The cache size of sequence. (NotNull, CacheValidSize)
     * @param incrementSize The increment size of sequence. (NotNull, NotMinus, NotZero)
     * @param nextValueSql The SQL for next value. (NotNull, NotTrimmedEmpty)
     * @return The filtered SQL. (NotNull, NotTrimmedEmpty)
     */
    public String filterNextValueSql(Integer cacheSize, Integer incrementSize, String nextValueSql) {
        assertFilterArgumentValid(cacheSize, incrementSize, nextValueSql);
        assertCacheSizeCanBeDividedByIncrementSize(cacheSize, incrementSize, nextValueSql);
        final Integer divided = cacheSize / incrementSize;
        final Integer unionCount = divided - 1;
        final StringBuilder sb = new StringBuilder();
        if (unionCount > 0) { // "union" way
            if (ResourceContext.isCurrentDBDef(DBDef.Oracle)) { // Oracle patch
                sb.append(DfStringUtil.replace(nextValueSql, "from dual", "from ("));
                sb.append(ln()).append("  select * from dual");
                for (int i = 0; i < unionCount; i++) {
                    sb.append(ln()).append("   union all ");
                    sb.append(ln()).append("  select * from dual");
                }
                sb.append(") dflocal");
            } else {
                // PostgreSQL and H2 are OK (but DB2 is NG)
                if (ResourceContext.isCurrentDBDef(DBDef.DB2)) {
                    String msg = "The cacheSize should be same as incrementSize on DB2:";
                    msg = msg + " cacheSize=" + cacheSize + " incrementSize=" + incrementSize;
                    msg = msg + " nextValueSql=" + nextValueSql;
                    throw new UnsupportedOperationException(msg);
                }
                sb.append(nextValueSql);
                for (int i = 0; i < unionCount; i++) {
                    sb.append(ln()).append(" union all ");
                    sb.append(ln()).append(nextValueSql);
                }
                sb.append(ln()).append(" order by 1 asc");

            }
        } else { // "increment" way
            sb.append(nextValueSql);
        }
        return sb.toString();
    }

    protected void assertFilterArgumentValid(Integer cacheSize, Integer incrementSize, String nextValueSql) {
        if (cacheSize == null || cacheSize <= 1) {
            String msg = "The argument 'cacheSize' should be cache valid size: " + cacheSize;
            throw new SequenceCacheIllegalStateException(msg);
        }
        if (incrementSize == null || incrementSize <= 0) {
            String msg = "The argument 'incrementSize' should be plus size: " + incrementSize;
            throw new SequenceCacheIllegalStateException(msg);
        }
        if (nextValueSql == null || nextValueSql.trim().length() == 0) {
            String msg = "The argument 'nextValueSql' should be valid: " + nextValueSql;
            throw new SequenceCacheIllegalStateException(msg);
        }
    }

    protected void assertCacheSizeCanBeDividedByIncrementSize(Integer cacheSize, Integer incrementSize,
            String nextValSql) {
        final Integer extraValue = cacheSize % incrementSize;
        if (extraValue != 0) {
            throwSequenceCacheSizeNotDividedIncrementSizeException(cacheSize, incrementSize, nextValSql);
        }
    }

    protected void throwSequenceCacheSizeNotDividedIncrementSizeException(Integer cacheSize, Integer incrementSize,
            String nextValueSql) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The cache size cannot be divided by increment size!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm sequence increment size and dfcache size setting." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x) - cacheSize = 50, incrementSize = 3" + ln();
        msg = msg + "    (x) - cacheSize = 50, incrementSize = 27" + ln();
        msg = msg + "    (o) - cacheSize = 50, incrementSize = 1" + ln();
        msg = msg + "    (o) - cacheSize = 50, incrementSize = 50" + ln();
        msg = msg + "    (o) - cacheSize = 50, incrementSize = 2" + ln();
        msg = msg + ln();
        msg = msg + "[Cache Size]" + ln() + cacheSize + ln();
        msg = msg + ln();
        msg = msg + "[Increment Size]" + ln() + incrementSize + ln();
        msg = msg + ln();
        msg = msg + "[SQL for Next Value]" + ln() + nextValueSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new SequenceCacheSizeNotDividedIncrementSizeException(msg);
    }

    // ===================================================================================
    //                                                                                 Log
    //                                                                                 ===
    protected void log(String msg) {
        XLog.log(msg);
    }

    protected boolean isLogEnabled() {
        return XLog.isLogEnabled();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setSequenceCacheKeyGenerator(SequenceCacheKeyGenerator sequenceCacheKeyGenerator) {
        _sequenceCacheKeyGenerator = sequenceCacheKeyGenerator;
    }
}
