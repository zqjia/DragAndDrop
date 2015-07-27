package com.example.draganddrop;

import android.util.Log;


public class LogUtil {
    private static boolean mIsLog = false;
    
    private static boolean mIsLogError = true;
    private static boolean mIsLogDebug = true;
    private static boolean mIsLogWarning = true;
    private static boolean mIsLogVerbose = true;
    
    /**
     * can't be instance
     * */
    private LogUtil() {
        
    }
    
    public static void enableLog() {
        mIsLog = true;
    }
    
    public static void disableLog() {
        mIsLog = false;
    }
    
    public static void enableLogError() {
        mIsLogError = true;
    }
    
    public static void disableLogError() {
        mIsLogError = false;
    }
    
    public static void enableLogDebug() {
        mIsLogDebug = true;
    }
    
    public static void disableLogDebug() {
        mIsLogDebug = false;
    }
    
    public static void e(String TAG, String msg) {
        if (mIsLog) {
            if (mIsLogError) {
                Log.e(TAG, msg);
            }
        }
    }
    
    public static void w(String TAG, String msg) {
        if (mIsLog) {
            if (mIsLogWarning) {
                Log.w(TAG, msg);
            }
        }
    }
    
    public static void d(String TAG, String msg) {
        if (mIsLog) {
            if (mIsLogDebug) {
                Log.d(TAG, msg);
            }
        }
    }
    
    public static void v(String TAG, String msg) {
        if (mIsLog) {
            if (mIsLogVerbose) {
                Log.v(TAG, msg);
            }
        }
    }
}
