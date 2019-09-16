package org.michael.common.utils;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;

/**
 * Created on 2019-09-16 11:28
 * Author : Michael.
 */
public class CollectionUtil {

    public static <K, V> Map<K, V> mergeMap(Map<K, V> a, Map<K, V> b) {
        Map<K, V> r = new HashMap<>();
        r.putAll(a);
        r.putAll(b);
        return r;
    }

    public static <E> boolean isSameList(List<E> a, List<E> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            E ea = a.get(i);
            E eb = b.get(i);
            if (!Objects.equals(ea, eb)) {
                return false;
            }
        }
        return true;
    }

    public static <E> List<E> newList(E... e) {
        List<E> list = new ArrayList<>(16);
        for (E i : e) {
            list.add(i);
        }
        return list;
    }

    public static <E> boolean isSameSet(Set<E> a, Set<E> b) {
        if (a.size() != b.size()) {
            return false;
        }

        for (E e : a) {
            if (!b.contains(e)) {
                return false;
            }
        }
        for (E e : b) {
            if (!a.contains(e)) {
                return false;
            }
        }
        return true;
    }

    public static <K, V> Map<String, String> toStringMap(Map<K, V> m) {
        Map<String, String> tmp = new HashMap<>();
        for (Map.Entry<K, V> e : m.entrySet()) {
            if (e.getKey() != null && e.getValue() != null) {
                tmp.put(e.getKey().toString(), e.getValue().toString());
            }
        }
        return tmp;
    }

    public static <K, V> List<LinkedList<ImmutablePair<K, V>>> splitMap(Map<K, V> map, int batch) {
        List<LinkedList<ImmutablePair<K, V>>> result = new LinkedList<>();

        LinkedList<ImmutablePair<K, V>> tmp = new LinkedList<>();
        for (Map.Entry<K, V> e : map.entrySet()) {
            if (tmp.size() > batch) {
                result.add(tmp);
                tmp = new LinkedList<>();
            }
            tmp.add(ImmutablePair.of(e.getKey(), e.getValue()));
        }

        if (tmp.size() > 0) {
            result.add(tmp);
        }
        return result;
    }

    /**
     * Split list to max parts.
     * Return parts may less than maxParts
     *
     * @param list
     * @param maxParts
     * @param <E>
     * @return
     */
    public static <E> List<LinkedList<E>> splitListToNParts(List<E> list, int maxParts) {
        List<LinkedList<E>> result = new LinkedList<>();
        if (maxParts <= 1 || list.size() <= 1) {
            LinkedList<E> aList = new LinkedList<>();
            aList.addAll(list);
            result.add(aList);
            return result;
        }

        if (list.size() <= maxParts) {
            for (E e : list) {
                LinkedList<E> aList = new LinkedList<>();
                aList.add(e);
                result.add(aList);
            }
            return result;
        }

        int size = list.size();
        int sizePerParts = size / maxParts;
        int rest = size % maxParts;

        for (int i = 0; i < maxParts; i++) {
            LinkedList<E> aList = new LinkedList<>();
            for (int j = 0; j < sizePerParts; j++) {
                int index = i * sizePerParts + j;
                E e = list.get(index);
                aList.add(e);
            }
            result.add(aList);
        }

        LinkedList<E> lastList = result.get(result.size() - 1);
        if (rest > 0) {
            int startIndex = list.size() - rest;
            for (int i = startIndex; i < list.size(); i++) {
                lastList.add(list.get(i));
            }
        }
        return result;
    }

    public static <E> List<LinkedList<E>> splitList(List<E> list, int sizePerSplit) {
        List<LinkedList<E>> result = new LinkedList<>();
        LinkedList<E> tmp = new LinkedList<>();
        for (E e : list) {
            if (tmp.size() >= sizePerSplit) {
                result.add(tmp);
                tmp = new LinkedList<>();
            }
            tmp.add(e);
        }
        if (tmp.size() > 0) {
            result.add(tmp);
        }
        return result;
    }

}
