package org.donglight.watchdog.common.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 各种id生成策略
 */
public class IDUtils {

    /**
     * url id生成
     */
    public static long genUrlId() {
        //取当前时间的长整形值包含毫秒
        long millis = System.nanoTime();
        //加上两位随机数
        Random random = new Random();
        int end2 = random.nextInt(99);
        //如果不足两位前面补0
        String str = millis + String.format("%02d", end2);
        return new Long(str);
    }

    /**
     * urlState id生成
     */
    public static long genUrlStateId() {
        //取当前时间的长整形值包含毫秒
        long millis = System.nanoTime();
        //加上三位随机数
        Random random = new Random();
        int end3 = random.nextInt(1000);
        //如果不足两位前面补0
        String str = millis + String.format("%03d", end3);
        return new Long(str);
    }



    /**
     * 图片名生成
     */
    public static String genImageName() {
        //取当前时间的长整形值包含毫秒
        long millis = System.currentTimeMillis();
        //long millis = System.nanoTime();
        //加上三位随机数
        Random random = new Random();
        int end3 = random.nextInt(999);
        //如果不足三位前面补0
        String str = millis + String.format("%03d", end3);

        return str;
    }

    public static void main(String[] args) {
        Set<Long> longSet = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            longSet.add(genUrlId());
        }
        System.out.println(longSet.size());
    }
}
