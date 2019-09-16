package org.michael.common;

import org.michael.common.utils.StringUtil;

import java.util.List;
import java.util.Map;

/**
 * Created on 2019-09-16 11:25
 * Author : Michael.
 */
public class TypeWrapMap<K, V> {

    final Map<K, V> map;

    public TypeWrapMap(Map<K, V> map) {
        this.map = map;
    }

    public int getInt(K key, int defaultValue) {
        V value = getValue(key);
        return value == null ? defaultValue : Integer.parseInt(value.toString());
    }

    public long getLong(K key, long defaultValue) {
        V value = getValue(key);
        return value == null ? defaultValue : Long.parseLong(value.toString());
    }

    public String getString(K key, String defaultValue) {
        V value = getValue(key);
        return value == null ? defaultValue : value.toString().trim();
    }

    public boolean getBoolean(K key, boolean defaultValue) {
        V value = getValue(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value.toString());
    }

    public List<String> getList(K key, String split) {
        String value = getString(key, "");
        List<String> list = StringUtil.deepCheckSplit(value, split);
        return list;
    }

    public V getValue(K key) {
        return this.map.get(key);
    }

}
