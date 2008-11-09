/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.helper.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jflute
 * @since 0.8.3 (2008/11/08 Saturday)
 */
public class DfStringSet implements Set<String> {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final Object DUMMY_VALUE = new Object();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Map<String, Object> _internalMap;

    protected boolean _removeUnderscore;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfStringSet(boolean removeUnderscore, boolean concurrent) {
        _removeUnderscore = removeUnderscore;
        if (concurrent) {
            _internalMap = newConcurrentHashMap();
        } else {
            _internalMap = newHashMap();
        }
    }

    public static DfStringSet createAsFlexible() {
        return new DfStringSet(true, false);
    }

    public static DfStringSet createAsFlexibleConcurrent() {
        return new DfStringSet(true, true);
    }

    public static DfStringSet createAsCaseInsensitive() {
        return new DfStringSet(false, false);
    }

    public static DfStringSet createAsCaseInsensitiveConcurrent() {
        return new DfStringSet(false, true);
    }

    // ===================================================================================
    //                                                                        Map Emulator
    //                                                                        ============
    // -----------------------------------------------------
    //                                           Key Related
    //                                           -----------
    public boolean add(String value) {
        final String stringValue = convertStringKey(value);
        if (stringValue != null) {
            return _internalMap.put(stringValue, DUMMY_VALUE) != null;
        }
        return false;
    }

    public boolean remove(Object value) {
        final String stringValue = convertStringKey(value);
        if (stringValue != null) {
            return _internalMap.remove(stringValue) != null;
        }
        return false;
    }

    public boolean contains(Object value) {
        final String stringValue = convertStringKey(value);
        if (stringValue != null) {
            return _internalMap.containsKey(stringValue);
        }
        return false;
    }

    // -----------------------------------------------------
    //                                              Delegate
    //                                              --------
    public void clear() {
        _internalMap.clear();
    }

    public int size() {
        return _internalMap.size();
    }

    public boolean isEmpty() {
        return _internalMap.isEmpty();
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
        return _internalMap.keySet().iterator();
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
        return _internalMap.keySet().toArray();
    }

    public <T> T[] toArray(T[] a) {
        return _internalMap.keySet().toArray(a);
    }

    // ===================================================================================
    //                                                                       Key Converter
    //                                                                       =============
    protected String convertStringKey(Object value) {
        if (!(value instanceof String)) {
            return null;
        }
        return toLowerCase(removeUnderscore((String) value));
    }

    protected String removeUnderscore(String value) {
        if (_removeUnderscore) {
            return replace(value, "_", "");
        }
        return value;
    }

    protected String toLowerCase(String value) {
        return value.toLowerCase();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected static String replace(String text, String fromText, String toText) {
        if (text == null || fromText == null || toText == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(100);
        int pos = 0;
        int pos2 = 0;
        while (true) {
            pos = text.indexOf(fromText, pos2);
            if (pos == 0) {
                sb.append(toText);
                pos2 = fromText.length();
            } else if (pos > 0) {
                sb.append(text.substring(pos2, pos));
                sb.append(toText);
                pos2 = pos + fromText.length();
            } else {
                sb.append(text.substring(pos2));
                break;
            }
        }
        return sb.toString();
    }

    protected static <KEY, VALUE> ConcurrentHashMap<KEY, VALUE> newConcurrentHashMap() {
        return new ConcurrentHashMap<KEY, VALUE>();
    }

    protected static <KEY, VALUE> HashMap<KEY, VALUE> newHashMap() {
        return new HashMap<KEY, VALUE>();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public boolean equals(Object obj) {
        return _internalMap.keySet().equals(obj);
    }

    @Override
    public int hashCode() {
        return _internalMap.keySet().hashCode();
    }

    @Override
    public String toString() {
        return _internalMap.keySet().toString();
    }
}