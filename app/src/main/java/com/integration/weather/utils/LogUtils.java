package com.integration.weather.utils;

import android.util.Log;

import com.integration.weather.BuildConfig;


/**
 * Created by Wongerfeng on 2018/11/13.
 */

public class LogUtils {

    private static final boolean FLAG = BuildConfig.DEBUG;
    public static void i(String TAG, String msg){
        if (FLAG){
            Log.i(TAG, msg);
        }
    }
    public static void d(String TAG, String msg){
        if (FLAG){
            Log.i(TAG, msg);
        }
    }
    public static void e(String TAG, String msg){
        if (FLAG){
            Log.i(TAG, msg);
        }
    }
    public static void w(String TAG, String msg){
        if (FLAG){
            Log.i(TAG, msg);
        }
    }
}
