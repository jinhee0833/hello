package com.helloants.mm.helloants1.data;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by park on 2016-01-21.
 */
public class DeviceSize {
    public static Context context;
    public static int mWidth;
    public static int mHeight;

    public static void init(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
    }
}
