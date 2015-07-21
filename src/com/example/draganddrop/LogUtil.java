package com.example.draganddrop;

import android.util.Log;


public class LogUtil {
    private static boolean mIsLog = true;
    
    public static void enableLog() {
        mIsLog = true;
    }
    
    public static void disableLog() {
        mIsLog = false;
    }
    
    public static void e(String TAG, String msg) {
        if (mIsLog) {
            Log.e(TAG, msg);
        }
    }
    
    public static void w(String TAG, String msg) {
        if (mIsLog) {
            Log.w(TAG, msg);
        }
    }
    
    public static void d(String TAG, String msg) {
        if (mIsLog) {
            Log.d(TAG, msg);
        }
    }
}
