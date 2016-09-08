package com.tbd.androidshowcase.utility;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Trevor on 9/7/2016.
 */
public class ThreadUtils {

    private ThreadUtils() {
    }

    public static void runOnUiThread(final Runnable runnable) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            new Handler(Looper.getMainLooper()).post(runnable);
        } else {
            runnable.run();
        }
    }
}