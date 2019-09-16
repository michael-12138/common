package org.michael.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created on 2019-09-16 11:29
 * Author : Michael.
 */
public class DateUtil {

    public static ThreadLocal<SimpleDateFormat> yearFormatter = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy");
        }
    };

    public static ThreadLocal<SimpleDateFormat> monthFormatter = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMM");
        }
    };

    public static ThreadLocal<SimpleDateFormat> dayFormatter = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd");
        }
    };

    public static ThreadLocal<SimpleDateFormat> hourFormatter = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH");
        }
    };

    public static ThreadLocal<SimpleDateFormat> minuteFormatter = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("mm");
        }
    };

    public static ThreadLocal<SimpleDateFormat> secondFormatter = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("ss");
        }
    };

    public static int second(long time) {
        Date d = new Date(time);
        String second = secondFormatter.get().format(d);
        return Integer.parseInt(second);
    }

    public static String[] timeFields(long time) {
        Date d = new Date(time);
        return new String[]{dayFormatter.get().format(d), hourFormatter.get().format(d), minuteFormatter.get().format(d)};
    }

    public static String nDaysAgo(int n, long now) {
        long time = now - (n * 24 * 60 * 60 * 1000L);
        Date d = new Date(time);
        String day = dayFormatter.get().format(d);
        return day;
    }

    public static long parseDateString(String datetime, String format) {
        SimpleDateFormat f = new SimpleDateFormat(format);
        try {
            Date d = f.parse(datetime);
            return d.getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String format(long time) {
        Date d = new Date(time);
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        return f.format(d);
    }

    public static String format(long time, String format) {
        Date d = new Date(time);
        SimpleDateFormat f = new SimpleDateFormat(format);
        return f.format(d);
    }

    public static boolean isMonth(String str) {
        if (str == null || str.length() != 6) {
            return false;
        }
        try {
            int v = Integer.parseInt(str);
            return v >= 200101 && v <= 209001;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isYear(String str) {
        if (str == null || str.length() != 4) {
            return false;
        }
        try {
            int v = Integer.parseInt(str);
            return v >= 2001 && v <= 2090;
        } catch (Exception e) {
            return false;
        }
    }

    //19970701
    public static boolean isDay(String str) {
        if (str == null || str.length() != 8) {
            return false;
        }
        try {
            int v = Integer.parseInt(str);
            return v >= 20010101 && v <= 20900101;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNotDay(String str) {
        return !isDay(str);
    }

    public static boolean isNotMinute(String str) {
        return !isMinute(str);
    }

    public static boolean isMinute(String str) {
        if (str == null || str.length() != 2) {
            return false;
        }
        try {
            int v = Integer.parseInt(str);
            return v >= 0 && v <= 59;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNotHour(String str) {
        return !isHour(str);
    }

    public static boolean isHour(String str) {
        if (str == null || str.length() != 2) {
            return false;
        }
        try {
            int v = Integer.parseInt(str);
            return v >= 0 && v <= 23;
        } catch (Exception e) {
            return false;
        }
    }

    public static long timeOfdays(int days) {
        return days * 24 * 60 * 60 * 1000L;
    }

    public static long timeOfHours(int hours) {
        return hours * 60 * 60 * 1000L;
    }

    public static long timeOfMinutes(int minutes) {
        return minutes * 60 * 1000L;
    }

    public static long timeOfSeconds(int seconds) {
        return seconds * 1000L;
    }

}
