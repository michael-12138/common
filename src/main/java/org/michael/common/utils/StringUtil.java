package org.michael.common.utils;

import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 2019-09-16 11:33
 * Author : Michael.
 */
public class StringUtil {

    private static final ThreadLocal<BASE64Encoder> base64encoder = new ThreadLocal<BASE64Encoder>() {
        @Override
        protected BASE64Encoder initialValue() {
            return new BASE64Encoder();
        }
    };

    private static final ThreadLocal<BASE64Decoder> base64decoder = new ThreadLocal<BASE64Decoder>() {
        @Override
        protected BASE64Decoder initialValue() {
            return new BASE64Decoder();
        }
    };

    public static String urlencode(String s) {
        return URLEncoder.encode(s);
    }

    public static String urldecode(String s) {
        return URLDecoder.decode(s);
    }

    public static String encodeBase64(byte[] buf) {
        String str = base64encoder.get().encode(buf);
        str = str.replace("\n", "");
        return str;
    }

    public static byte[] decodeBase64(String b64) {
        try {
            return base64decoder.get().decodeBuffer(b64);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String md5(String str) {
        byte[] bytes = str.getBytes(CommonUtil.UTF8);
        return md5(bytes);
    }

    public static String md5(byte[] source) {
        java.security.MessageDigest md;
        try {
            md = java.security.MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e1) {
            throw new RuntimeException(e1);
        }

        md.update(source);
        byte tmp[] = md.digest();
        char str[] = new char[16 * 2];
        int k = 0;
        for (int i = 0; i < 16; i++) {
            byte byte0 = tmp[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }

    public static String mkStrings(String split, String... element) {
        if (element.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String s : element) {
            builder.append(s);
            builder.append(split);
        }
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }

    public static List<String> deepCheckSplit(String str, String splitChar) {
        String[] tmp = StringUtils.split(str, splitChar);
        List<String> r = new ArrayList<String>(tmp.length);
        for (String s : tmp) {
            if (StringUtils.isNotBlank(s)) {
                r.add(s.trim());
            }
        }
        return r;
    }

    public static boolean aLEb(String a, String b) {
        int r = a.compareTo(b);
        return r <= 0;
    }

    public static boolean aLb(String a, String b) {
        int r = a.compareTo(b);
        return r < 0;
    }

    public static boolean aGb(String a, String b) {
        int r = a.compareTo(b);
        return r > 0;
    }

    public static boolean aGEb(String a, String b) {
        int r = a.compareTo(b);
        return r >= 0;
    }

    public static Boolean toBoolean(String value) {
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer toInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static Float toFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static Long toLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static Double toDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isIp(String ipAddress) {
        if (isIpV4(ipAddress)) {
            return true;
        }

        if (isIpV6(ipAddress)) {
            return true;
        }
        return false;
    }

    public static boolean isIpV4(String ipAddress) {
        if (ipAddress == null) {
            return false;
        }
        String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }

    static String V6_PATTERN = "^([\\da-fA-F]{1,4}:){6}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|::([\\da−fA−F]1,4:)0,4((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|::([\\da−fA−F]1,4:)0,4((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:):([\\da-fA-F]{1,4}:){0,3}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)2:([\\da−fA−F]1,4:)0,2((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)2:([\\da−fA−F]1,4:)0,2((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:){3}:([\\da-fA-F]{1,4}:){0,1}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)4:((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)4:((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}|:((:[\\da−fA−F]1,4)1,6|:)|:((:[\\da−fA−F]1,4)1,6|:)|^[\\da-fA-F]{1,4}:((:[\\da-fA-F]{1,4}){1,5}|:)|([\\da−fA−F]1,4:)2((:[\\da−fA−F]1,4)1,4|:)|([\\da−fA−F]1,4:)2((:[\\da−fA−F]1,4)1,4|:)|^([\\da-fA-F]{1,4}:){3}((:[\\da-fA-F]{1,4}){1,3}|:)|([\\da−fA−F]1,4:)4((:[\\da−fA−F]1,4)1,2|:)|([\\da−fA−F]1,4:)4((:[\\da−fA−F]1,4)1,2|:)|^([\\da-fA-F]{1,4}:){5}:([\\da-fA-F]{1,4})?|([\\da−fA−F]1,4:)6:|([\\da−fA−F]1,4:)6:";

    public static boolean isIpV6(String ip) {
        Pattern IPV6_STD_PATTERN = Pattern.compile(V6_PATTERN);
        return IPV6_STD_PATTERN.matcher(ip).matches();
    }

}
