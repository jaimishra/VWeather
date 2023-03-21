package com.virtusaweather.utils;

import android.text.TextUtils;
import android.util.Log;
import androidx.viewbinding.BuildConfig;


public class VirtusaWeatherTestLog {


    public static String logTag(Class<?> cls) {
        return logTag(cls.getSimpleName());
    }

    public static String logTag(String tag) {
        if (!StringUtils.isEmpty(tag) && tag.length() > 23) {
            return tag.substring(0, 23);
        }
        return tag;
    }

    public static void d(String tag, String msg) {
        if ((Log.isLoggable(tag, Log.DEBUG) || BuildConfig.DEBUG) && !TextUtils.isEmpty(msg) && !TextUtils.isEmpty(tag)) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if ((Log.isLoggable(tag, Log.DEBUG) || BuildConfig.DEBUG) && tr != null  && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(msg)) {
            Log.d(tag, msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        // always log errors
        if (!TextUtils.isEmpty(msg)  && !TextUtils.isEmpty(tag)) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        // always log errors
        if (!TextUtils.isEmpty(msg) && tr != null  && !TextUtils.isEmpty(tag)) {
            Log.e(tag, msg, tr);
        }

    }

    public static void i(String tag, String msg) {
        if ((Log.isLoggable(tag, Log.INFO) || BuildConfig.DEBUG) && !TextUtils.isEmpty(msg)  && !TextUtils.isEmpty(tag)) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if ((Log.isLoggable(tag, Log.INFO) || BuildConfig.DEBUG) && TextUtils.isEmpty(msg) && tr != null  && !TextUtils.isEmpty(tag)) {
            Log.i(tag, msg, tr);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if ((Log.isLoggable(tag, Log.VERBOSE) || BuildConfig.DEBUG) && !TextUtils.isEmpty(msg) && tr != null  && !TextUtils.isEmpty(tag)) {
            Log.v(tag, msg, tr);
        }
    }

    public static void v(String tag, String msg) {
        if ((Log.isLoggable(tag, Log.VERBOSE) || BuildConfig.DEBUG) && !TextUtils.isEmpty(msg)  && !TextUtils.isEmpty(tag)) {
            Log.v(tag, msg);
        }
    }

    public static void w(String tag, Throwable tr) {
        if ((Log.isLoggable(tag, Log.WARN) || BuildConfig.DEBUG) && tr != null  && !TextUtils.isEmpty(tag)) {
            Log.w(tag, tr);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if ((Log.isLoggable(tag, Log.WARN) || BuildConfig.DEBUG) && !TextUtils.isEmpty(msg) && tr != null  && !TextUtils.isEmpty(tag)) {
            Log.w(tag, msg, tr);
        }
    }

    public static void w(String tag, String msg) {
        if ((Log.isLoggable(tag, Log.WARN) || BuildConfig.DEBUG) && !TextUtils.isEmpty(msg)  && !TextUtils.isEmpty(tag)) {
            Log.w(tag, msg);
        }
    }
}
