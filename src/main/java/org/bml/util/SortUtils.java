package org.bml.util;

/*
 * #%L
 * org.bml
 * %%
 * Copyright (C) 2006 - 2014 Brian M. Lima
 * %%
 * This file is part of ORG.BML.
 *     ORG.BML is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *     ORG.BML is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with ORG.BML.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Encapsulation for generic sorting. It is recommended that this only
 * be used when you can not control the original map implementation. When
 * sorting is an issue and you can control the origin map implementation I
 * recommended using commons Map implementations such as the BIDIMap.
 *
 * @author Brian M. Lima
 */
public final class SortUtils {

    /**
     * Enumeration for sort orders.
     */
    public static enum ORDER {

        /**
         * Sort ASCENDING.
         */
        ASCENDING,
        /**
         * Sort DECENDING.
         */
        DECENDING;
    }

    /**
     * Disables the default constructor.
     *
     * @throws InstantiationException Always.
     */
    private SortUtils() throws InstantiationException {
        throw new InstantiationException("Instances of this type are forbidden.");
    }

    /**
     * Sorts the input map by its values ascending. Does not alter the original map.
     *
     * @param theMap A map to sort by value.
     * @return A sorted version of the input map sorted by value ascending.
     */
    public static Map sortByValueLowToHigh(final Map theMap) {
        return sortMap(theMap, new Comparator() {
            public int compare(final Object o1, final Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });
    }

    /**
     * Sorts the input map by its values descending. Does not alter the original map.
     *
     * @param <K> Map Key class
     * @param <V> Map Value class
     * @param theMap A map to sort by value.
     * @return A sorted version of the input map sorted by value descending.
     */
    public static <K, V extends Comparable<V>> Map<K, V> sortByValueHighToLow(final Map<K, V> theMap) {
        return sortMap(theMap, new Comparator<Map.Entry<K, V>>() {
            public int compare(final Map.Entry<K, V> o1, final Map.Entry<K, V> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
    }

    /**
     * Sorts the input map using the a Comparator. Does not alter the original map.
     *
     * @param theMap A map to sort.
     * @param theComparator A Comparator to sort with.
     * @return A sorted version of the input map.
     */
    private static <K, V extends Comparable<V>> Map<K, V> sortMap(final Map<K, V> theMap, final Comparator<Map.Entry<K, V>> theComparator) {
        final List<Map.Entry<K, V>> list = new LinkedList<>(theMap.entrySet());
        Collections.sort(list, theComparator);
        final Map<K, V> result = new LinkedHashMap<>();
        list.forEach((entry) -> {
            result.put(entry.getKey(), entry.getValue());
        });
        return result;
    }
}
