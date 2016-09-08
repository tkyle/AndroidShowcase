package com.tbd.androidshowcase;

import android.app.Application;
import android.util.Log;

import com.tbd.androidshowcase.utility.AWSMobileClient;

/**
 * Application class responsible for initializing singletons and other common components.
 */
public class ShowcaseApplication extends Application {

    private final static String LOG_TAG = Application.class.getSimpleName();

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "ShowcaseApplication.onCreate - Initializing application...");
        super.onCreate();
        initializeApplication();
        Log.d(LOG_TAG, "ShowcaseApplication.onCreate - Application initialized OK");
    }

    private void initializeApplication() {
        AWSMobileClient.initializeMobileClientIfNecessary(getApplicationContext());

        // ...Put any application-specific initialization logic here...
    }
}