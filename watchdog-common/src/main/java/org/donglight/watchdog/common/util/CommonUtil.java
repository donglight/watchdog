package org.donglight.watchdog.common.util;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 〈一句话功能简述〉<br>
 * 一些简单通用的方法
 *
 * @author donglight
 * @date 2019/4/24
 * @since 1.0.0
 */
public class CommonUtil {


    public static boolean isClassPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * 保留2位小数
     *
     * @param d
     * @return
     */
    public static double formatDouble(double d) {
        return (double) Math.round(d * 100) / 100;
    }

    /**
     * 获取本地web服务器的启动端口
     *
     * @return
     * @throws Exception
     */
    public static int getLocalWebServerPort() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectName> objs = null;
        try {
            objs = mBeanServer.queryNames(new ObjectName("*:type=Connector,*"),
                    Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
            // String hostname = InetAddress.getLocalHost().getHostName();
            // InetAddress[] addresses = InetAddress.getAllByName(hostname);
            ArrayList<String> endPoints = new ArrayList<>();
            ObjectName obj = objs.iterator().next();
        /*for (Iterator<ObjectName> i = objs.iterator(); i.hasNext(); ) {
            ObjectName obj = i.next();
            String scheme = mBeanServer.getAttribute(obj, "scheme").toString();
            String port = obj.getKeyProperty("port");
            for (InetAddress addr : addresses) {
                String host = addr.getHostAddress();
                String ep = scheme + "://" + host + ":" + port;
                endPoints.add(ep);
            }
        }*/
            return Integer.parseInt(obj.getKeyProperty("port"));
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 将通配符表达式转化为正则表达式
     *
     * @param path
     * @return
     */
    private static String getRegPath(String path) {
        char[] chars = path.toCharArray();
        int len = chars.length;
        StringBuilder sb = new StringBuilder();
        boolean preX = false;
        for (int i = 0; i < len; i++) {
            if (chars[i] == '*') {//遇到*字符
                if (preX) {//如果是第二次遇到*，则将**替换成.*
                    sb.append(".*");
                    preX = false;
                } else if (i + 1 == len) {//如果是遇到单星，且单星是最后一个字符，则直接将*转成[^/]*
                    sb.append("[^/]*");
                } else {//否则单星后面还有字符，则不做任何动作，下一把再做动作
                    preX = true;
                    continue;
                }
            } else {//遇到非*字符
                if (preX) {//如果上一把是*，则先把上一把的*对应的[^/]*添进来
                    sb.append("[^/]*");
                    preX = false;
                }
                if (chars[i] == '?') {//接着判断当前字符是不是?，是的话替换成.
                    sb.append('.');
                } else {//不是?的话，则就是普通字符，直接添进来
                    sb.append(chars[i]);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 通配符模式
     *
     * @param whitePath - 白名单地址
     * @param reqPath   - 请求地址
     * @return boolean
     */
    public static boolean match(String whitePath, String reqPath) {
        String regPath = getRegPath(whitePath);
        return Pattern.compile(regPath).matcher(reqPath).matches();
    }

    public static InetAddress getLocalIpv4Address() {
        Enumeration<NetworkInterface> ifaces = null;
        InetAddress siteLocalAddress = null;
        try {
            ifaces = NetworkInterface.getNetworkInterfaces();

            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = ifaces.nextElement();
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    String hostAddress = addr.getHostAddress();
                    if (addr instanceof Inet4Address
                            && !addr.isLoopbackAddress()
                            && !hostAddress.startsWith("192.168")
                            && !hostAddress.startsWith("172.")
                            && !hostAddress.startsWith("169.")) {
                        if (addr.isSiteLocalAddress()) {
                            siteLocalAddress = addr;
                        } else {
                            return addr;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return siteLocalAddress;
    }
}
