package ru.alemakave.android.utils;

import android.content.Context;
import android.util.Log;
import android.view.ContextThemeWrapper;
import androidx.appcompat.app.AlertDialog;
import ru.alemakave.mfstock.BuildConfig;
import ru.alemakave.mfstock.R;

public final class Logger {
    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = 7;

    private Logger() {}

    /**
     * Send an {@link #INFO} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg == null ? "null" : msg);
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg == null ? "null" : msg);
        }
    }

    public static void e(Context context, String tag, String msg) {
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Theme_MFStock_Widget_AlertDialog)).setPositiveButton("Ok", null).setTitle(tag).setMessage(msg).create().show();
        e(tag, msg);
    }
}
