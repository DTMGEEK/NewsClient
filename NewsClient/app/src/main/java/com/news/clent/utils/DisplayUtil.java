package com.news.clent.utils;

import android.content.Context;

/**
 * Created by jake（lian_weijian@126.com）
 * Date: 2015-02-21
 * Time: 23:34
 */

public class DisplayUtil {
    /**
     * 根据手机分辨率 把dip转换为px
     * @param context
     * @param dipValue  单位为dip的数值
     * @return
     */
    public static int dip2px(Context context,float dipValue){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * 根据手机分辨率 px转换为dip
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context,float pxValue){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue/scale+0.5f);
    }
}
