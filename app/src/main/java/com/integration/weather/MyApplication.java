package com.integration.weather;

import android.app.Activity;
import android.os.Bundle;

import org.litepal.LitePalApplication;

/**
 * Created by Wongerfeng on 2019/7/5.
 */
public class MyApplication extends LitePalApplication {


    @Override
    public void onCreate() {
        super.onCreate();

        ActivityLifecycleCallbacks lifecycleCallbacks = new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        };
    }


}
