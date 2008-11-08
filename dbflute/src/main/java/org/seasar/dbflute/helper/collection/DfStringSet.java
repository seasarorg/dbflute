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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author jflute
 * @since 0.8.3 (2008/11/08 Saturday)
 */
public class DfStringSet implements Set<String> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected HashSet<String> _internalSet = new HashSet<String>();

    protected boolean _removeUnderscore;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfStringSet(boolean removeUnderscore) {
        _removeUnderscore = removeUnderscore;
    }

    public static DfStringSet createAsFlexible() {
        return new DfStringSet(true);
    }

    public static DfStringSet createAsCaseInsensitive() {
        return new DfStringSet(false);
    }

    // ===================================================================================
    //                                                                        Map Emulator
    //                                                                        ============
    // -----------------------------------------------------
    //                                           Key Related
    //                                           -----------
    public boolean add(String value) {
        final String stringValue = convertStringValue(value);
        if (stringValue != null) {
            return _internalSet.add(stringValue);
        }
        return false;
    }

    public boolean remove(Object value) {
        final String stringValue = convertStringValue(value);
        if (stringValue != null) {
            return _internalSet.remove(stringValue);
        }
        return false;
    }

    public boolean contains(Object value) {
        final String stringValue = convertStringValue(value);
        if (stringValue != null) {
            return _internalSet.contains(stringValue);
        }
        return false;
    }

    // -----------------------------------------------------
    //                                              Delegate
    //                                              --------
    public void clear() {
        _internalSet.clear();
    }

    public int size() {
        return _internalSet.size();
    }

    public boolean isEmpty() {
        return _internalSet.isEmpty();
    }

    public boolean addAll(Collection<? extends String> c) {
        return _internalSet.addAll(c);
    }

    public boolean containsAll(Collection<?> c) {
        return _internalSet.containsAll(c);
    }

    public Iterator<String> iterator() {
        return _internalSet.iterator();
    }

    public boolean removeAll(Collection<?> c) {
        return _internalSet.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return _internalSet.retainAll(c);
    }

    public Object[] toArray() {
        return _internalSet.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return _internalSet.toArray(a);
    }

    // ===================================================================================
    //                                                                       Key Converter
    //                                                                       =============
    protected String convertStringValue(Object value) {
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

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public boolean equals(Object obj) {
        return _internalSet.equals(obj);
    }

    @Override
    public int hashCode() {
        return _internalSet.hashCode();
    }

    @Override
    public String toString() {
        return _internalSet.toString();
    }
}