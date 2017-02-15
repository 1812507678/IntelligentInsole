package com.amsu.intelligentinsole.application;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by HP on 2017/2/15.
 */

public class MyApplication extends Application {
    public static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences("appinfo", MODE_PRIVATE);
    }
}
