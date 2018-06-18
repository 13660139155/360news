package com.example.a360news.unit;

import android.content.Context;

/**
 * dp,sp转换为px工具类
 * ldpi:mdpi:hdpi:xdpi:xxdpi = 3:4:6:8:12
 * Created by ASUS on 2018/6/15.
 */

public class DisplayUtil {

    /**
     * 将px转换为dip或dp，保证尺寸大小不变
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;//换算比例
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp转换为px，保证尺寸大小不变
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;//尺寸换算比例
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px转换为sp，保证文字大小不变
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue){
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;//文字尺寸换算比例
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp转换为px，保证文字大小不变
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue){
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
