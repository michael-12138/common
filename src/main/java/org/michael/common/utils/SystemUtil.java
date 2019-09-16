package org.michael.common.utils;

import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created on 2019-09-16 11:34
 * Author : Michael.
 */
public class SystemUtil {

    public static void sleepQuietly(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }

    public static long getPid() {
        String system = ManagementFactory.getRuntimeMXBean().getName();
        String pidStr = "0";
        int indexOf = system.indexOf('@');
        if (indexOf > 0) {
            pidStr = system.substring(0, indexOf);
        }
        try {
            return Long.parseLong(pidStr);
        } catch (Exception e) {
            return 0;
        }
    }

    public static List<InetAddress> getNetworkInterfaceIP(String interfaceName) throws SocketException {
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        List<InetAddress> ans = new ArrayList<InetAddress>();

        while (en.hasMoreElements()) {
            NetworkInterface i = en.nextElement();
            if (!interfaceName.equals(i.getDisplayName())) {
                continue;
            }
            for (Enumeration<InetAddress> en2 = i.getInetAddresses(); en2.hasMoreElements(); ) {
                InetAddress addr = en2.nextElement();
                if (addr instanceof Inet4Address) {
                    ans.add(addr);
                }
            }
        }
        return ans;
    }

    public static List<InetAddress> getNetworkInterfaceIP(String interfaceName, String pattern) throws SocketException {
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        List<InetAddress> ans = new ArrayList<InetAddress>();

        while (en.hasMoreElements()) {
            NetworkInterface i = en.nextElement();
            if (!interfaceName.equals(i.getDisplayName())) {
                continue;
            }
            for (Enumeration<InetAddress> en2 = i.getInetAddresses(); en2.hasMoreElements(); ) {
                InetAddress addr = en2.nextElement();
                if (addr instanceof Inet4Address) {
                    if (addr.getHostAddress().startsWith(pattern)) {
                        ans.add(addr);
                    }
                }
            }
        }
        return ans;
    }

    public static String getPatternAddress(String pattern) throws SocketException {
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface i = en.nextElement();
            for (Enumeration<InetAddress> en2 = i.getInetAddresses(); en2.hasMoreElements(); ) {
                InetAddress addr = en2.nextElement();
                if (addr instanceof Inet4Address) {
                    if (addr.getHostAddress().startsWith(pattern)) {
                        return addr.getHostAddress();
                    }
                }
            }
        }
        return null;
    }

}
