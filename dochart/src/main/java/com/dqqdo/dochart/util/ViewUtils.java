package com.dqqdo.dochart.util;

import android.content.Context;
import android.view.WindowManager;

/**
 * 作者：duqingquan
 * 时间：2016/12/23:13:47
 * 邮箱：
 * 说明：
 */
public class ViewUtils {

    public static int screenWidth;
    public static int screenHeight;

    public static void initScreenInfo(Context context) {

        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
    }


}
