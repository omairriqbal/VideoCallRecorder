
package com.videocaht.recorder.facetime.videocall.screenrecorder;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.onesignal.OneSignal;


public class VideoCallRecorderApplication extends Application {
    private static VideoCallRecorderApplication instance;
    static Context context;
    private Intent downloadService;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context=getApplicationContext();

       DataProvider.getInstance();
        AppRater.app_launched(getContext());
        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

    }



    public static VideoCallRecorderApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return context;
    }

}
