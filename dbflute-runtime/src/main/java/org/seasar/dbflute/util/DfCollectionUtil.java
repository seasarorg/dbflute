/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jflute
 * @since 0.9.4 (2009/03/20 Friday)
 */
public class DfCollectionUtil {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final List<?> EMPTY_LIST = Collections.unmodifiableList(new ArrayList<Object>());
    private static final Map<?, ?> EMPTY_MAP = Collections.unmodifiableMap(new HashMap<Object, Object>());
    private static final Set<?> EMPTY_SET = Collections.unmodifiableSet(new HashSet<Object>());

    // ===================================================================================
    //                                                                          Collection
    //                                                                          ==========
    public static Class<?> findFirstElementType(Collection<?> collection) {
        for (Object object : collection) {
            if (object != null) {
                return object.getClass();
            }
        }
        return null;
    }

    public static boolean hasValidElement(Collection<?> collection) {
        for (Object object : collection) {
            if (object != null) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                                List
    //                                                                                ====
    public static <ELEMENT> ArrayList<ELEMENT> newArrayList() {
        return new ArrayList<ELEMENT>();
    }

    public static <ELEMENT> ArrayList<ELEMENT> newArrayList(Collection<ELEMENT> elements) {
        final ArrayList<ELEMENT> list = newArrayList();
        list.addAll(elements);
        return list;
    }

    public static <ELEMENT> ArrayList<ELEMENT> newArrayList(ELEMENT... elements) {
        final ArrayList<ELEMENT> list = newArrayList();
        for (ELEMENT element : elements) {
            list.add(element);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static <ELEMENT> List<ELEMENT> emptyList() {
        return (List<ELEMENT>) EMPTY_LIST;
    }

    public static <ELEMENT extends Object> List<List<ELEMENT>> splitByLimit(List<ELEMENT> elementList, int limit) {
        final List<List<ELEMENT>> valueList = newArrayList();
        final int valueSize = elementList.size();
        int index = 0;
        int remainderSize = valueSize;
        do {
            final int beginIndex = limit * index;
            final int endPoint = beginIndex + limit;
            final int endIndex = limit <= remainderSize ? endPoint : valueSize;
            final List<ELEMENT> splitList = newArrayList();
            splitList.addAll(elementList.subList(beginIndex, endIndex));
            valueList.add(splitList);
            remainderSize = valueSize - endIndex;
            ++index;
        } while (remainderSize > 0);
        return valueList;
    }

    // ===================================================================================
    //                                                                                 Map
    //                                                                                 ===
    public static <KEY, VALUE> HashMap<KEY, VALUE> newHashMap() {
        return new HashMap<KEY, VALUE>();
    }

    public static <KEY, VALUE> LinkedHashMap<KEY, VALUE> newLinkedHashMap() {
        return new LinkedHashMap<KEY, VALUE>();
    }

    public static <KEY, VALUE> ConcurrentHashMap<KEY, VALUE> newConcurrentHashMap() {
        return new ConcurrentHashMap<KEY, VALUE>();
    }

    @SuppressWarnings("unchecked")
    public static <KEY, VALUE> Map<KEY, VALUE> emptyMap() {
        return (Map<KEY, VALUE>) EMPTY_MAP;
    }

    // ===================================================================================
    //                                                                                 Set
    //                                                                                 ===
    public static <ELEMENT> HashSet<ELEMENT> newHashSet() {
        return new HashSet<ELEMENT>();
    }

    public static <ELEMENT> HashSet<ELEMENT> newHashSet(Collection<ELEMENT> elements) {
        final HashSet<ELEMENT> set = newHashSet();
        set.addAll(elements);
        return set;
    }

    public static <ELEMENT> HashSet<ELEMENT> newHashSet(ELEMENT... elements) {
        final HashSet<ELEMENT> set = newHashSet();
        for (ELEMENT element : elements) {
            set.add(element);
        }
        return set;
    }

    public static <ELEMENT> LinkedHashSet<ELEMENT> newLinkedHashSet() {
        return new LinkedHashSet<ELEMENT>();
    }

    public static <ELEMENT> LinkedHashSet<ELEMENT> newLinkedHashSet(Collection<ELEMENT> elements) {
        final LinkedHashSet<ELEMENT> set = newLinkedHashSet();
        set.addAll(elements);
        return set;
    }

    public static <ELEMENT> LinkedHashSet<ELEMENT> newLinkedHashSet(ELEMENT... elements) {
        final LinkedHashSet<ELEMENT> set = newLinkedHashSet();
        for (ELEMENT element : elements) {
            set.add(element);
        }
        return set;
    }

    @SuppressWarnings("unchecked")
    public static <ELEMENT> Set<ELEMENT> emptySet() {
        return (Set<ELEMENT>) EMPTY_SET;
    }

    // ===================================================================================
    //                                                                               Order
    //                                                                               =====
    /**
     * Order the unordered list according to specified resources.
     * @param unorderedList The unordered list. (NotNull)
     * @param resource The resource of according-to-order. (NotNull)
     * @param <ELEMENT_TYPE> The type of element.
     * @param <ID_TYPE> The type of ID.
     */
    public static <ELEMENT_TYPE, ID_TYPE> void orderAccordingTo(List<ELEMENT_TYPE> unorderedList,
            AccordingToOrderResource<ELEMENT_TYPE, ID_TYPE> resource) {
        assertObjectNotNull("unorderedList", unorderedList);
        if (unorderedList.isEmpty()) {
            return;
        }
        assertObjectNotNull("resource", resource);
        final List<ID_TYPE> orderedUniqueIdList = resource.getOrderedUniqueIdList();
        assertObjectNotNull("resource.getOrderedUniqueIdList()", orderedUniqueIdList);
        if (orderedUniqueIdList.isEmpty()) {
            return;
        }
        final AccordingToOrderIdExtractor<ELEMENT_TYPE, ID_TYPE> idExtractor = resource.getIdExtractor();
        assertObjectNotNull("resource.getIdExtractor()", idExtractor);

        final Map<ID_TYPE, Integer> idIndexMap = new LinkedHashMap<ID_TYPE, Integer>();
        int index = 0;
        for (ID_TYPE id : orderedUniqueIdList) {
            if (idIndexMap.containsKey(id)) {
                String msg = "The id was duplicated: id=" + id + " orderedUniqueIdList=" + orderedUniqueIdList;
                throw new IllegalStateException(msg);
            }
            idIndexMap.put(id, index);
            ++index;
        }
        final Comparator<ELEMENT_TYPE> comp = new Comparator<ELEMENT_TYPE>() {
            public int compare(ELEMENT_TYPE o1, ELEMENT_TYPE o2) {
                final ID_TYPE id1 = idExtractor.extractId(o1);
                final ID_TYPE id2 = idExtractor.extractId(o2);
                assertObjectNotNull("id1 of " + o1, id1);
                assertObjectNotNull("id2 of " + o2, id2);
                final Integer index1 = idIndexMap.get(id1);
                final Integer index2 = idIndexMap.get(id2);
                if (index1 != null && index2 != null) {
                    return index1.compareTo(index2);
                }
                if (index1 == null && index2 == null) {
                    return 0;
                }
                return index1 == null ? 1 : -1;
            }
        };
        Collections.sort(unorderedList, comp);
    }

    public static interface AccordingToOrderIdExtractor<ELEMENT_TYPE, ID_TYPE> {

        /**
         * Extract ID from the element instance.
         * @param element Element instance. (NotNull)
         * @return Extracted ID. (NotNull)
         */
        ID_TYPE extractId(ELEMENT_TYPE element);
    }

    public static class AccordingToOrderResource<ELEMENT_TYPE, ID_TYPE> {
        protected List<ID_TYPE> _orderedUniqueIdList;
        protected AccordingToOrderIdExtractor<ELEMENT_TYPE, ID_TYPE> _idExtractor;

        public AccordingToOrderResource<ELEMENT_TYPE, ID_TYPE> setupResource(List<ID_TYPE> orderedUniqueIdList,
                AccordingToOrderIdExtractor<ELEMENT_TYPE, ID_TYPE> idExtractor) {
            setOrderedUniqueIdList(orderedUniqueIdList);
            setIdExtractor(idExtractor);
            return this;
        }

        public List<ID_TYPE> getOrderedUniqueIdList() {
            return _orderedUniqueIdList;
        }

        public void setOrderedUniqueIdList(List<ID_TYPE> orderedUniqueIdList) {
            _orderedUniqueIdList = orderedUniqueIdList;
        }

        public AccordingToOrderIdExtractor<ELEMENT_TYPE, ID_TYPE> getIdExtractor() {
            return _idExtractor;
        }

        public void setIdExtractor(AccordingToOrderIdExtractor<ELEMENT_TYPE, ID_TYPE> idExtractor) {
            _idExtractor = idExtractor;
        }
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected static void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }
}
