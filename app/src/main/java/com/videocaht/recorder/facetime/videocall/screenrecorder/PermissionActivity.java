package com.videocaht.recorder.facetime.videocall.screenrecorder;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import com.onesignal.OneSignal;

public class PermissionActivity extends Activity {
    public static final String ACTION_PERMISSION=  "myAction.Activity.Permissions";
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Trace myTrace;
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(null);
//        Fabric.with(this, new Crashlytics());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        myTrace = FirebasePerformance.getInstance().newTrace("test_trace");
        myTrace.start();
        myTrace.incrementMetric("item_cache_hit", 1);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.None)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        OneSignal.setLocationShared(false);

    }
    private boolean haspermission() {

        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionState1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionState2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if(permissionState== PackageManager.PERMISSION_GRANTED && permissionState1==PackageManager.PERMISSION_GRANTED)
            return permissionState == PackageManager.PERMISSION_GRANTED;
        else
            return false;
    }
public void onResume()
{

    super.onResume();

    if(!haspermission())
    {
        Intent in = new Intent(getApplicationContext(), main_menu.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(in);
        this.finish();
        return;
    }


    SharedPreferences prefs = getSharedPreferences("launches", 0);
    int count=prefs.getInt("count",112);
    if(count==11)
    {
        if (isServiceRunning(getApplicationContext()))
        {
            if(getIntent().getAction()!= "com.open.by.main")
            {
                Intent in = new Intent(getApplicationContext(), main_menu.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                 startActivity(in);
                 finish();
            }
            else
            {
                mediaProjectionManager = (MediaProjectionManager) getApplicationContext().getSystemService(MEDIA_PROJECTION_SERVICE);
                startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 1);

            }
        }
        else
        {
            mediaProjectionManager = (MediaProjectionManager) getApplicationContext().getSystemService(MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 1);
        }
    }
    else
    {
        if(getIntent().getAction()!= "com.open.by.main")
        {

            Intent in = new Intent(getApplicationContext(), main_menu.class);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
            this.finish();
        }


    }
}
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {

                    if (!Settings.canDrawOverlays(getApplicationContext()))
                    {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 11);

                    }
                    else
                        {
                        Intent i =
                                new Intent(this, FloatingViewService.class)
                                        .setAction(ACTION_PERMISSION)
                                        .putExtra(ScreenshotService.EXTRA_RESULT_CODE, resultCode)
                                        .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, data);
                        startService(i);
                       this.finish();
                    }
                }
                else  if(Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1)
                {
                    Intent i =
                            new Intent(this, FloatingViewService_notification.class)
                                    .setAction(ACTION_PERMISSION)
                                    .putExtra(ScreenshotService.EXTRA_RESULT_CODE, resultCode)
                                    .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, data);
                    startService(i);
                    Intent in = new Intent(getApplicationContext(), main_menu.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in);
                    this.finish();
                }
                else
                {
                    Intent i =
                            new Intent(this, FloatingViewService.class)
                                    .setAction(ACTION_PERMISSION)
                                    .putExtra(ScreenshotService.EXTRA_RESULT_CODE, resultCode)
                                    .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, data);
                    startService(i);

                   this.finish();
                }
            }
            else
            {
                finish();
            }
        }


        if (requestCode==11)
        {
            if (resultCode==RESULT_OK)
            {
                Intent i =
                        new Intent(this, FloatingViewService.class)
                                .setAction(ACTION_PERMISSION)
                                .putExtra(ScreenshotService.EXTRA_RESULT_CODE, resultCode)
                                .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, data);
                startService(i);
               this.finish();
            }
        }

    }
    public static boolean isServiceRunning(Context queryingContext)
    {
        try {
            ActivityManager manager = (ActivityManager) queryingContext.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            {
                if ((queryingContext.getPackageName()+".FloatingViewService").equals(service.service.getClassName()))
                    return true;
            }
        } catch (Exception ex)
        {
        }
        return false;
    }

}