package org.donglight.watchdog.common.util;

/**
 * 〈一句话功能简述〉<br>
 * 一些简单通用的方法
 *
 * @author donglight
 * @date 2019/4/24
 * @since 1.0.0
 */
public class CommomUtil {


    public static boolean isClassPresent(String className){
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
