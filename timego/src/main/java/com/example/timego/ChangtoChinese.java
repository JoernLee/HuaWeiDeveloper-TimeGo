package com.example.timego;

/**
 * Created by Administrator on 2017/3/28.
 */

public class ChangtoChinese {
    public static String toChinese(String string) {
        String[] s1 = { "日", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
        String[] s2 = { "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千" };

        String result = "";

        int n = string.length();
        for (int i = 0; i < n; i++) {

            int num = string.charAt(i) - '0';

            if (i != n - 1 && num != 0) {
                result += s1[num] + s2[n - 2 - i];
            } else {
                result += s1[num];
            }

        }

        return result;

    }
}
