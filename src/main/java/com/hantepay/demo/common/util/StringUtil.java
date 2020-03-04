package com.hantepay.demo.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class StringUtil {

    /**
     * 获取32位随机字符串
     *
     * @return
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    /**
     * 判断整形是否为null
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(Integer value) {
        return (value == null || value == 0);
    }

    /**
     * 判断字符串是否为空
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(String value) {
        return (value == null || "".equals(value.trim()) || "null".equals(value.trim()));
    }

    /**
     * 判断数组是否为空
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(Object[] value) {
        return value == null || value.length == 0;
    }

    /**
     * 判断List是否为空
     *
     * @param list
     * @return
     */
    public static boolean isEmpty(List<Object> list) {
        return list == null || list.size() == 0;
    }

    /**
     * 判断Map是否为空
     *
     * @param map
     * @return
     */
    public static boolean isEmpty(Map<Object, Object> map) {
        return map == null || map.size() == 0;
    }

    /**
     * 判断Set是否为空
     *
     * @param set
     * @return
     */
    public static boolean isEmpty(Set<Object> set) {
        return set == null || set.size() == 0;
    }

    /**
     * 判断Object是否为空
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(Object value) {
        return value == null || "null".equals(value) || "".equals(value);
    }

    /**
     * 生产随机字符串
     *
     * @param len 字符长度
     * @return
     */
    public static String getToken(int len) {
        String[] codeArr = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
                "A", "B", "C", "D", "E",
                "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O",
                "P", "Q", "R", "S", "T",
                "U", "V", "W", "X", "Y",
                "Z", "a", "b", "c", "d",
                "e", "f", "g", "h", "i",
                "j", "k", "l", "m", "n",
                "o", "p", "q", "r", "s",
                "t", "u", "v", "w", "x",
                "y", "z"};
        return commonRandom(codeArr, len);
    }

    private static String commonRandom(String[] limitCharArr, int len) {
        Random rand = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < len; i++) {
            code.append(limitCharArr[rand.nextInt(limitCharArr.length)]);
        }
        return code.toString();
    }



}
