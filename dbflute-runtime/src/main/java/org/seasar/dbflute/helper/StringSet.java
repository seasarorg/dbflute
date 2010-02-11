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
package org.seasar.dbflute.helper;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author jflute
 */
public class StringSet implements Set<String> {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final Object DUMMY_VALUE = new Object();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Map<String, Object> _stringKeyMap;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected StringSet(boolean removeUnderscore, boolean order, boolean concurrent) {
        if (order && concurrent) {
            String msg = "The 'order' and 'concurrent' should not be both true at the same time!";
            throw new IllegalStateException(msg);
        }
        if (concurrent) {
            if (removeUnderscore) {
                _stringKeyMap = StringKeyMap.createAsFlexibleConcurrent();
            } else {
                _stringKeyMap = StringKeyMap.createAsCaseInsensitiveConcurrent();
            }
        } else {
            if (order) {
                if (removeUnderscore) {
                    _stringKeyMap = StringKeyMap.createAsFlexibleOrdered();
                } else {
                    _stringKeyMap = StringKeyMap.createAsCaseInsensitiveOrdered();
                }
            } else {
                if (removeUnderscore) {
                    _stringKeyMap = StringKeyMap.createAsFlexible();
                } else {
                    _stringKeyMap = StringKeyMap.createAsCaseInsensitive();
                }
            }
        }
    }

    public static StringSet createAsCaseInsensitive() {
        return new StringSet(false, false, false);
    }

    public static StringSet createAsCaseInsensitiveConcurrent() {
        return new StringSet(false, false, true);
    }

    public static StringSet createAsCaseInsensitiveOrdered() {
        return new StringSet(false, true, false);
    }

    public static StringSet createAsFlexible() {
        return new StringSet(true, false, false);
    }

    public static StringSet createAsFlexibleConcurrent() {
        return new StringSet(true, false, true);
    }

    public static StringSet createAsFlexibleOrdered() {
        return new StringSet(true, true, false);
    }

    // ===================================================================================
    //                                                                        Set Emulator
    //                                                                        ============
    // -----------------------------------------------------
    //                                           Key Related
    //                                           -----------
    public boolean add(String value) {
        return _stringKeyMap.put(value, DUMMY_VALUE) == null;
    }

    public boolean remove(Object value) {
        return _stringKeyMap.remove(value) != null;
    }

    public boolean contains(Object value) {
        return _stringKeyMap.containsKey(value);
    }

    // -----------------------------------------------------
    //                                              Delegate
    //                                              --------
    public void clear() {
        _stringKeyMap.clear();
    }

    public int size() {
        return _stringKeyMap.size();
    }

    public boolean isEmpty() {
        return _stringKeyMap.isEmpty();
    }

    public boolean addAll(Collection<? extends String> c) {
        boolean success = false;
        for (String s : c) {
            if (add(s)) {
                success = true;
            }
        }
        return success;
    }

    public boolean removeAll(Collection<?> c) {
        boolean success = false;
        for (Object s : c) {
            if (remove(s)) {
                success = true;
            }
        }
        return success;
    }

    public boolean containsAll(Collection<?> c) {
        for (Object s : c) {
            if (contains(s)) {
                return true;
            }
        }
        return false;
    }

    public Iterator<String> iterator() {
        return _stringKeyMap.keySet().iterator();
    }

    public boolean retainAll(Collection<?> c) {
        boolean success = false;
        for (Object s : c) {
            if (!contains(s)) {
                if (remove(s)) {
                    success = true;
                }
            }
        }
        return success;
    }

    public Object[] toArray() {
        return _stringKeyMap.keySet().toArray();
    }

    public <T> T[] toArray(T[] a) {
        return _stringKeyMap.keySet().toArray(a);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public boolean equals(Object obj) {
        return _stringKeyMap.keySet().equals(obj);
    }

    @Override
    public int hashCode() {
        return _stringKeyMap.keySet().hashCode();
    }

    @Override
    public String toString() {
        return _stringKeyMap.keySet().toString();
    }
}