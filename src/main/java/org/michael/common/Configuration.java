package org.michael.common;

import org.apache.commons.lang3.StringUtils;
import org.michael.common.utils.IOUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2019-09-16 11:15
 * Author : Michael.
 */
public class Configuration {

    private final Map<String, String> objs = new ConcurrentHashMap<String, String>();
    private final List<ConfigurationChangeListener> listeners = new LinkedList<>();

    public Configuration() {

    }

    public Map<String, String> getAllObjects() {
        return Collections.unmodifiableMap(this.objs);
    }

    private void onConfigurationChange() {
        synchronized (listeners) {
            for (ConfigurationChangeListener listener : listeners) {
                listener.onConfigurationChange(this);
            }
        }
    }

    public void addListener(ConfigurationChangeListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public Configuration(Map<String, String> initConf) {
        this.objs.putAll(initConf);
    }

    public Configuration copy() {
        return new Configuration(this.objs);
    }

    public void putConfig(String key, String value) {
        this.objs.put(key, value);
        onConfigurationChange();
    }

    public void putAllConfigs(Map<String, String> configs) {
        this.objs.putAll(configs);
        onConfigurationChange();
    }

    public static Configuration fromMap(Map<Object, Object> map) {
        Map<String, String> tmp = new HashMap<>();
        for (Map.Entry<Object, Object> o : map.entrySet()) {
            if (o.getKey() == null || o.getValue() == null) {
                continue;
            }
            String key = o.getKey().toString();
            String value = o.getValue().toString();
            tmp.put(key, value);
        }
        return new Configuration(tmp);
    }

    public int getInt(String prefix, String key, int defaultValue) {
        String value = getValue(prefix, key);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    public long getLong(String prefix, String key, long defaultValue) {
        String value = getValue(prefix, key);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    public int getInt(String key, int defaultValue) {
        String value = getValue(key);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    public long getLong(String key, long defaultValue) {
        String value = getValue(key);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    public String getString(String prefix, String key, String defaultValue) {
        String value = getValue(prefix, key);
        return value == null ? defaultValue : value.trim();
    }

    public String getString(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new RuntimeException(key + " is null.");
        }
        return value;
    }

    public String getString(String key, String defaultValue) {
        String value = getValue(key);
        return value == null ? defaultValue : value.trim();
    }

    public boolean getBoolean(String prefix, String key, boolean defaultValue) {
        String value = getValue(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = getValue(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    public String getValue(String prefix, String key) {
        return this.objs.get(prefix + "." + key);
    }

    public String[] getList(String prefix, String key, String split) {
        String value = getValue(prefix, key);
        if (value == null) {
            return new String[0];
        }
        String[] tmp = StringUtils.split(value, split);
        List<String> result = new LinkedList<>();
        for (String s : tmp) {
            if (StringUtils.isNotBlank(s)) {
                result.add(s.trim());
            }
        }
        return result.toArray(new String[result.size()]);
    }

    public String[] getList(String key, String split) {
        String value = getValue(key);
        if (value == null) {
            return new String[0];
        }
        String[] tmp = StringUtils.split(value, split);
        List<String> result = new LinkedList<>();
        for (String s : tmp) {
            if (StringUtils.isNotBlank(s)) {
                result.add(s.trim());
            }
        }
        return result.toArray(new String[result.size()]);
    }

    public String getValue(String key) {
        return this.objs.get(key);
    }

    public Map<String, String> getDictionnary(String prefix, String key, String pairSplit, String fieldSplit) {
        String str = getValue(prefix, key);
        if (str == null) {
            throw new RuntimeException("config key[" + key + "] not found.");
        }
        String[] tmp = StringUtils.split(str, pairSplit);
        Map<String, String> r = new HashMap<>();
        for (String kv : tmp) {
            String[] pair = StringUtils.split(kv, fieldSplit);
            if (pair == null || pair.length != 2) {
                throw new InvalidParameterException("bad dictionary str[" + str + "].");
            }
            String kk = pair[0];
            String vv = pair[1];
            if (StringUtils.isBlank(kk) || StringUtils.isBlank(vv)) {
                throw new InvalidParameterException("bad dictionary str[" + str + "].");
            }
            kk = kk.trim();
            vv = vv.trim();
            r.put(kk, vv);
        }
        return r;
    }

    public Map<String, String> getDictionary(String key, String pairSplit, String fieldSplit) {
        String str = getValue(key);
        if (str == null) {
            throw new RuntimeException("config key[" + key + "] not found.");
        }
        String[] tmp = StringUtils.split(str, pairSplit);
        Map<String, String> r = new HashMap<>();
        for (String kv : tmp) {
            String[] pair = StringUtils.split(kv, fieldSplit);
            if (pair == null || pair.length != 2) {
                throw new InvalidParameterException("bad dictionary str[" + str + "].");
            }
            String kk = pair[0];
            String vv = pair[1];
            if (StringUtils.isBlank(kk) || StringUtils.isBlank(vv)) {
                throw new InvalidParameterException("bad dictionary str[" + str + "].");
            }
            kk = kk.trim();
            vv = vv.trim();
            r.put(kk, vv);
        }
        return r;
    }

    public static Configuration createConfiguration(Map<Object, Object> conf) {
        Map<String, String> m = new HashMap<String, String>();
        for (Map.Entry<Object, Object> e : conf.entrySet()) {
            if (e.getKey() != null && e.getValue() != null) {
                m.put(e.getKey().toString(), e.getValue().toString());
            }
        }
        return new Configuration(m);
    }

    public static Configuration fromFile(String file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            Map pros = IOUtil.readProperties(fis);
            return createConfiguration(pros);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtil.closeQuietely(fis);
        }
    }
}
