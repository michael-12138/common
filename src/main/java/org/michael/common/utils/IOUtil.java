package org.michael.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.*;
import java.util.*;

/**
 * Created on 2019-09-16 11:30
 * Author : Michael.
 */
public class IOUtil {

    public static void closeQuietely(AutoCloseable o) {
        if (o != null) {
            try {
                o.close();
            } catch (Exception e) {
            }
        }
    }

    public static void closeQuietely(Closeable o) {
        if (o != null) {
            try {
                o.close();
            } catch (Exception e) {
            }
        }
    }

    public static List<String> readLines(String file) {
        return readLines(new File(file));
    }

    public static List<String> readLines(File file) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            return readLines(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtil.closeQuietely(in);
        }
    }

    public static List<String> readLines(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        List<String> lines = new LinkedList<>();
        String line = null;
        while ((line = br.readLine()) != null) {
            if (StringUtils.isBlank(line)) {
                continue;
            }
            line = line.trim();
            if (line.startsWith("#")) {
                continue;
            }
            lines.add(line);
        }
        return lines;
    }

    public static Map<String, String> readDictionary(InputStream in, String dictionarySplit) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        Map<String, String> map = new HashMap<>();

        try {
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }
                ImmutablePair<String, String> kv = smartSplit(line, dictionarySplit);
                if (kv.left != null && kv.right != null) {
                    map.put(kv.left, kv.right);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    private static ImmutablePair<String, String> smartSplit(String line, String split) {

        int index = line.indexOf(split);
        if (index == -1) {
            return null;
        }
        if (index == line.length() - 1) {
            return null;
        }
        if (index == 0) {
            return null;
        }
        String key = line.substring(0, index);
        String value = line.substring(index + 1);
        return ImmutablePair.of(key, value);
    }

    public static void writeContentToFile(String file, byte[] buf) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(buf);
            fos.flush();
        } finally {
            IOUtil.closeQuietely(fos);
        }
    }

    public static void writeContentToFile(String file, String content) throws IOException {
        writeContentToFile(new File(file), content);
    }

    public static void writeContentToFile(File file, String content) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(content, 0, content.length());
            bw.flush();
        } finally {
            IOUtil.closeQuietely(fos);
        }
    }

    public static byte[] readAllBytes(File file) throws IOException {
        long len = file.length();
        byte[] buf = new byte[(int) len];

        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            DataInputStream dis = new DataInputStream(in);
            dis.readFully(buf);
        } finally {
            IOUtil.closeQuietely(in);
        }
        return buf;
    }

    public static Map<String, String> readProperties(File filePath) throws IOException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(filePath);
            return readProperties(in);
        } finally {
            closeQuietely(in);
        }
    }

    public static Map<String, String> readProperties(String file) throws IOException {
        return readProperties(new File(file));
    }

    public static Map<String, String> readProperties(InputStream in) throws IOException {
        Properties prop = new Properties();
        InputStreamReader reader = new InputStreamReader(in, CommonUtil.UTF8);
        prop.load(reader);
        Map<String, String> r = new HashMap<String, String>();
        Set<Map.Entry<Object, Object>> entries = prop.entrySet();
        for (Map.Entry<Object, Object> en : entries) {
            if (en.getKey() != null && en.getValue() != null) {
                r.put(en.getKey().toString(), en.getValue().toString());
            }
        }
        return r;
    }

    public static byte[] readAllBytesFromInputStream(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(256);

        byte[] buf = new byte[4096];
        int n = 0;
        while ((n = in.read(buf)) != -1) {
            out.write(buf, 0, n);
        }
        out.flush();
        return out.toByteArray();
    }

    public static Map<String, String> readUtfMap(DataInputStream in) throws IOException {
        Map<String, String> result = new HashMap<>();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            String key = in.readUTF();
            String value = in.readUTF();
            result.put(key, value);
        }
        return result;
    }

    public static void writeUtfMap(Map<String, String> map, DataOutputStream out) throws IOException {
        int size = map.size();
        out.writeInt(size);
        for (Map.Entry<String, String> e : map.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();
            out.writeUTF(key);
            out.writeUTF(value);
        }
    }

}
