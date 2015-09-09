package com.henry.exception;

import android.app.Application;

/**
 * Created by Henry on 2015/9/9.
 */
public class CrashApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }
}
