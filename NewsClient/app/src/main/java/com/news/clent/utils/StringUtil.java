package com.news.clent.utils;

/**
 * Created by jake（lian_weijian@126.com）
 * Date: 2015-02-23
 * Time: 15:23
 */

public class StringUtil {

    /**
     * 从字符串转换成整形
     * @param strVal 待转换字符串
     * @return
     */
    public static int String2int(String strVal){
        int intVal = 0;
        try {
            intVal = Integer.valueOf(strVal);
        }catch (Exception e){
            e.printStackTrace();
            throw new ClassCastException();
        }
        return intVal;
    }
}
