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
package org.seasar.dbflute.cbean;

/**
 * The bean of fetch narrowing.
 * @author jflute
 */
public interface FetchNarrowingBean {

    /**
     * Get fetch start index.
     * @return Fetch start index.
     */
    public int getFetchNarrowingSkipStartIndex();

    /**
     * Get fetch size.
     * @return Fetch size.
     */
    public int getFetchNarrowingLoopCount();

    /**
     * Is fetch start index supported?
     * @return Determination.
     */
    public boolean isFetchNarrowingSkipStartIndexEffective();

    /**
     * Is fetch size supported?
     * @return Determination.
     */
    public boolean isFetchNarrowingLoopCountEffective();

    /**
     * Is fetch-narrowing effective?
     * @return Determination.
     */
    public boolean isFetchNarrowingEffective();

    /**
     * Ignore fetch narrowing. Only checking safety result size is valid. {INTERNAL METHOD}
     */
    public void ignoreFetchNarrowing();

    /**
     * Restore ignored fetch narrowing. {INTERNAL METHOD}
     */
    public void restoreIgnoredFetchNarrowing();

    /**
     * Get the max size of safety result.
     * @return The max size of safety result.
     */
    public int getSafetyMaxResultSize();
}
