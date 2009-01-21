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
package org.seasar.dbflute.exception;

/**
 * The exception of when the result size is dangerous.
 * @author jflute
 */
public class DangerousResultSizeException extends RuntimeException {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** Safety max result size. */
    protected int _safetyMaxResultSize;

    /**
     * Constructor.
     * 
     * @param msg Exception message.
     * @param safetyMaxResultSize Safety max result size.
     */
    public DangerousResultSizeException(String msg, int safetyMaxResultSize) {
        super(msg);
        this._safetyMaxResultSize = safetyMaxResultSize;
    }

    /**
     * Get safety max result size.
     * @return Safety max result size.
     */
    public int getSafetyMaxResultSize() {
        return _safetyMaxResultSize;
    }
}
