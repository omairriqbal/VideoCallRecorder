package com.recorder.screen.recordingapp.editor;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.ActivityCompat;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class FloatingViewService_notification extends Service
{

    public static final String ACTION_PAUSE_PLAY =  "myAction.Activity.playpause";
    public static final String SKIP =  "myAction.Activity.playpause.skip";
    public static final String ACTION_STOP=  "myAction.Activity.stop";
    public static final String ACTION_PAUSE=  "myAction.Activity.pause";
    public static final String ACTION_RESUME=  "myAction.Activity.resume";
    public static final String ACTION_CLEAR=  "myAction.Activity.clear";
    public static final String ACTION_OPEN_MAIN=  "myAction.Activity.main";
    public static final String ACTION_PERMISSION=  "myAction.Activity.Permissions";

    private MediaRecorder mMediaRecorder;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private MediaProjectionManager mProjectionManager;
    private static final int DISPLAY_WIDTH = 720;
    private static final int DISPLAY_HEIGHT = 1280;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionCallback mMediaProjectionCallback;
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    String fullpath,name;
    View right,left;
    private WindowManager mWindowManager,windowManagerClose;
    private View mFloatingView;

    View layout;
    LinearLayout options;
    private ImageView collapsed_iv,fab1;
     ComponentName serviceName;
    private Handler handler=new Handler();
    TextView timmer;
    private MediaProjectionManager mgr;
    static final String EXTRA_RESULT_CODE="resultCode";
    static final String EXTRA_RESULT_INTENT="resultIntent";
    private int resultCode;
    private Intent resultData;
    static boolean recording=false;
    private boolean buy;
    private int mScreenDensity;


    private void openPermissionActivity()
    {
        Intent mainIntent = new Intent(getApplicationContext(), PermissionActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
    }
    public FloatingViewService_notification() {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (intent.getAction()==null)
        {
            if(resultData==null)
            {
                if(intent.hasExtra(EXTRA_RESULT_CODE))
                {
                      resultCode=intent.getIntExtra(EXTRA_RESULT_CODE, 1337);
                    resultData=intent.getParcelableExtra(EXTRA_RESULT_INTENT);
                }
                else
               openPermissionActivity();
            }
            /*else
            {
                Intent in=new Intent(getApplicationContext(),main_menu.class);
                in.setFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
            }*/

          //  resultCode=intent.getIntExtra(EXTRA_RESULT_CODE, 1337);
           // resultData=intent.getParcelableExtra(EXTRA_RESULT_INTENT);
            //foregroundify();
        }
       else if (intent != null && intent.getAction() != null)
       {

            switch (intent.getAction())
            {
                case   "com.open.by.main":

                    Intent mainIntent = new Intent(getApplicationContext(), PermissionActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mainIntent.setAction("com.open.by.main");
                    startActivity(mainIntent);

                    break;
                case ACTION_PERMISSION:
                     resultCode=intent.getIntExtra(EXTRA_RESULT_CODE, 1337);
                    resultData=intent.getParcelableExtra(EXTRA_RESULT_INTENT);
                    createRecordNotification();
                    break;

                case ACTION_STOP:
                    // go to previous song
                    stop();

                    break;


                    case ACTION_PAUSE_PLAY:
                    // pause or play song

                       record();
                        break;


                case ACTION_PAUSE:
                    // pause or play song
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    {
                        try {
                            mMediaRecorder.pause();
                            update_Noti("pause");
                        }
                        catch(Exception e)
                            {

                            }


                    }


                    break;
                case ACTION_RESUME:
                    // pause or play song
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    {
                        try {
                            mMediaRecorder.resume();

                            update_Noti("play");
                        }
                        catch (Exception e)
                        {

                        }

                    }

                    break;
               /* case ACTION_CLEAR:
                    Intent i=
                            new Intent(this, ScreenshotService.class)
                                    .putExtra(ScreenshotService.EXTRA_RESULT_CODE, resultCode)
                                    .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, resultData);

                    startService(i);
                    clearAll();
                    stopSelf();
                    break;*/
                case ACTION_OPEN_MAIN:
                    clearAll();
                    Intent in=new Intent(getApplicationContext(),main_menu.class);
                    in.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in);
                    break;
                case SKIP:
                    record_skip();
                    break;
            }
        }
      //  return START_NOT_STICKY;
        return START_NOT_STICKY;
    }

    int LAYOUT_FLAG;
    @Override
    public void onCreate()
    {
        super.onCreate();

        getScreenSize();
        DisplayMetrics metrics = new DisplayMetrics();
        mProjectionManager = (MediaProjectionManager) getSystemService
                (Context.MEDIA_PROJECTION_SERVICE);

        mMediaProjectionCallback = new MediaProjectionCallback();

        createRecordNotification();
        serviceName = new ComponentName(this, FloatingViewService_notification.class);
    }

    public void clearAll() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
        if (recording)
            stop();


    }

    public void update_Noti(String action)
    {

        Intent closeButton = null;
        Intent stop = new Intent(this, FloatingViewService_notification.class);
        stop.setAction(ACTION_STOP);

        PendingIntent pendingStopIntent = PendingIntent.getService(getApplicationContext(), 1, stop, 0);
        RemoteViews notificationView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widget_new_notification);

        if(action.equals("play"))
        {
            closeButton = new Intent(this, FloatingViewService_notification.class);
            closeButton.setAction(ACTION_PAUSE);
            notificationView.setImageViewResource(R.id.btn_play,R.mipmap.ic_pause);
            notificationView.setTextViewText(R.id.text,"Recorder is recording. Tap stop to finish");


        }
        else if(action.equals("pause"))
        {
            closeButton = new Intent(this, FloatingViewService_notification.class);
            closeButton.setAction(ACTION_RESUME);
            notificationView.setImageViewResource(R.id.btn_play,R.mipmap.ic_action_resume);

        }
        else  if(action.equals("stop"))
        {
            closeButton = new Intent(this, FloatingViewService_notification.class);
            closeButton.setAction(ACTION_PAUSE_PLAY);
            notificationView.setImageViewResource(R.id.btn_play,R.mipmap.ic_rec);


        }
        PendingIntent pendingSwitchIntent = PendingIntent.getService(getApplicationContext(), 0, closeButton, 0);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        myNotification.bigContentView = notificationView;
        notificationView.setOnClickPendingIntent(R.id.btn_stop, pendingStopIntent);
        notificationView.setOnClickPendingIntent(R.id.btn_play, pendingSwitchIntent);


//        windowManagerPanel.updateViewLayout(layout_1,params_1);
        notificationManager.notify(1, myNotification);


    }



    RemoteViews notificationView;
    Notification myNotification;
    NotificationManager notificationManager;
    private void createRecordNotification()
    {


        Intent play = new Intent(this, FloatingViewService_notification.class);
        play.setAction(ACTION_PAUSE_PLAY);
        PendingIntent pendingSwitchIntent = PendingIntent.getService(getApplicationContext(), 0, play, 0);

        Intent stop = new Intent(this, FloatingViewService_notification.class);
        stop.setAction(ACTION_STOP);
        PendingIntent pendingStopIntent = PendingIntent.getService(getApplicationContext(), 1, stop, 0);


       /* Intent clear = new Intent(this, FloatingViewService.class);
        clear.setAction(ACTION_CLEAR);*/
       // PendingIntent pendingClearIntent = PendingIntent.getService(getApplicationContext(), 2, clear, 0);

        Intent home = new Intent(this, FloatingViewService_notification.class);
        home.setAction(ACTION_OPEN_MAIN);
        PendingIntent pendingHomeIntent = PendingIntent.getService(getApplicationContext(), 3, home, 0);


         notificationView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widget_new_notification);

        RemoteViews notificationView_samll = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widget_small_notification);
         notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
           notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = null;
            channel = new NotificationChannel("default",
                    "Channel name",
                    NotificationManager.IMPORTANCE_DEFAULT);

            channel.setDescription("Channel description");
            notificationManager.createNotificationChannel(channel);
           /* Notification.Builder builder = new Notification.Builder(getApplicationContext(),"default").setSmallIcon(R.mipmap.ic_launcher).
                    setTicker("Recorder is ready")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setSubText("Limited Stocks, Don't Wait!")
                        .setContentTitle("Custom Notification Title")
                    .setAutoCancel(false);
            notificationManager.notify(1, builder.build());
            return;*/
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            myNotification  = new Notification.Builder(getApplicationContext(),"default")
                   .setSmallIcon(R.drawable.ic_noti)
                   .setTicker("Recorder is ready")
                   .setAutoCancel(false).build();
        }
        else {
            myNotification  = new Notification.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_noti)
                    .setTicker("Recorder is ready")
                    .setAutoCancel(false).build();
        }

        // myNotification.contentView = customViewSmall;
        myNotification.bigContentView = notificationView;
        myNotification.contentView = notificationView_samll;

      //  notificationView.setProgressBar(R.id.pb_progress, 100, 12, false);

        Notification.Builder builder = new Notification.Builder(getApplicationContext()).setSmallIcon(R.mipmap.ic_launcher).setTicker("Recorder is ready").setContent(notificationView).setAutoCancel(false);
        notificationView.setOnClickPendingIntent(R.id.btn_stop, pendingStopIntent);
        notificationView.setOnClickPendingIntent(R.id.btn_play, pendingSwitchIntent);
       // notificationView.setOnClickPendingIntent(R.id.btnClear, pendingClearIntent);
        notificationView.setOnClickPendingIntent(R.id.openApp, pendingHomeIntent);

        notificationView_samll.setOnClickPendingIntent(R.id.btn_stop, pendingStopIntent);
        notificationView_samll.setOnClickPendingIntent(R.id.btn_play, pendingSwitchIntent);
      //  notificationView_samll.setOnClickPendingIntent(R.id.btnClear, pendingClearIntent);
        notificationView_samll.setOnClickPendingIntent(R.id.openApp, pendingHomeIntent);
        notificationManager.notify(1, myNotification);

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopForeground(true);
     stopSelf();
     stopService(new Intent(this,FloatingViewService_notification.class));
     notificationManager.cancelAll();
    }


    CountDownTimer cdt;
    public void start_Timer(long time)
    {

         cdt = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished)
            {
               // currentTime = millisUntilFinished;
                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);


            }

            @Override
            public void onFinish()
            {


try {
    initRecorder();
    shareScreen();

}catch (Exception e)
{

}

                    notificationView.setTextViewText(R.id.text, "Recorder is recording. Tap stop to finish");
                    myNotification.bigContentView = notificationView;
                    notificationManager.notify(1, myNotification);

            }
        };
        cdt.start();
    }
    public void record_skip()
    {
        if(!recording)
        {
                SharedPreferences settings1 = getSharedPreferences("MY_PREF", 0);
                int sec = settings1.getInt("timmer", 3);
                recording = !recording;
                if(sec!=0)
                    start_Timer(1000 * (sec+1));

                else
                    start_Timer(1000 * sec);

        }


    }
    private boolean haspermission() {

        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionState1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionState== PackageManager.PERMISSION_GRANTED && permissionState1==PackageManager.PERMISSION_GRANTED)
            return permissionState == PackageManager.PERMISSION_GRANTED;
        else
            return false;
    }

    public void record()
    {
            if(!haspermission())
            {
                Toast.makeText(this, "Can't Record! Permissions Missing", Toast.LENGTH_SHORT).show();

                return;

            }

    }

    public void stop()
    {
        if(recording) {
            try {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
                recording=!recording;
            }
            catch (Exception e)
            {
                return;
                //  mMediaRecorder.release();
            }
            SharedPreferences settings1= getSharedPreferences("MY_PREF",0);
            int ch=settings1.getInt("storage path",1);
            String fullPath;
            if(ch==1)
             fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screen Recorder";//+ ;
            else
                {
                fullPath = getExternalFilesDir("Screen Recorder").getAbsolutePath() ;
                File f1 = new File("/storage/");
                String[] list = f1.list();
                fullPath = fullPath.replace("emulated/0",list[0]) ;
            }

            String output = fullPath + name;
            stopScreenSharing();
            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{output}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
           // Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();



            notificationView.setTextViewText(R.id.text, "Recorder is ready.");
            myNotification.bigContentView = notificationView;
            notificationManager.notify(1, myNotification);
            Intent in=new Intent(FloatingViewService_notification.this,Finish_popup.class);
            in.setFlags(FLAG_ACTIVITY_NEW_TASK);
            in.putExtra("name",name);
            in.putExtra("url",fullPath);
            new Handler().postDelayed(() -> startActivity(in), 1000);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                update_Noti("stop");
            }

        }
    }
    public void incremntCount()
    {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("recorder", 0);
        SharedPreferences.Editor editor = prefs.edit();
        int count=prefs.getInt("count",0);
        count+=1;
        editor.putInt("count",count);
        editor.commit();
    }
    public void resettCount()
    {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("recorder", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("count",0);
        editor.commit();
    }
    private void initRecorder()
    {
        Calendar cc=Calendar.getInstance();
        SharedPreferences settings1= getSharedPreferences("MY_PREF",0);
        int ch=settings1.getInt("video formate",1);
        String ext;
        if(ch==1)
            ext=".mp4";
        else
            ext=".3gp";
         name="/SR_"+cc.getTimeInMillis()+ext;
        //fullpath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/Screen Recorder";//+ ;
        int ch1=settings1.getInt("storage path",1);
        if(ch1==1)
            fullpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screen Recorder";//+ ;
        else
        {
            fullpath = getExternalFilesDir("Screen Recorder").getAbsolutePath() ;
            File f1 = new File("/storage/");
            String[] list = f1.list();
            fullpath = fullpath.replace("emulated/0",list[0]) ;
        }
        File ff=new File(fullpath);
        if(!(ff.exists()))
        {
            ff.mkdir();
        }
       /* CamcorderProfile cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        mMediaRecorder.setProfile(cpHigh);*/
        try {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
           /* mMediaRecorder.setOutputFile(Environment
                    .getExternalStoragePublicDirectory(Environment
                            .DIRECTORY_DOWNLOADS) + name);*/
            mMediaRecorder.setOutputFile(fullpath + name);
            mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setVideoEncodingBitRate(3000000);
            mMediaRecorder.setVideoFrameRate(16);
            //   int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(0 + 90);
            mMediaRecorder.setOrientationHint(orientation);
            mMediaRecorder.prepare();
            mgr=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
            mMediaProjection=mgr.getMediaProjection(resultCode, resultData);
            if(mMediaProjectionCallback!=null)
            mMediaProjection.registerCallback(mMediaProjectionCallback, null);
            else
            {
                mMediaProjectionCallback=new MediaProjectionCallback();
                mMediaProjection.registerCallback(mMediaProjectionCallback, null);
                mMediaProjection.registerCallback(mMediaProjectionCallback, null);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                update_Noti("play");
        }
        catch (Exception e)
        {
            e.printStackTrace();
           // Toast.makeText(getApplicationContext(), "ex 1", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareScreen()
    {
        if (mMediaProjection == null)
        {
            Intent in=mProjectionManager.createScreenCaptureIntent();
            in.setFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
            return;
        }
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();
    }
    private VirtualDisplay createVirtualDisplay()
    {
        return mMediaProjection.createVirtualDisplay("MainActivity",
                DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder.getSurface(), null /*Callbacks*/, null
                /*Handler*/);
    }
    private void getScreenSize()
    {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = mWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        Point displaySize = new Point();
        mWindowManager.getDefaultDisplay().getRealSize(displaySize);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mWindowManager.getDefaultDisplay().getRealMetrics(metrics);
        mScreenDensity = (int)(metrics.density * 160f);

    }
    private class MediaProjectionCallback extends MediaProjection.Callback
    {
        @Override
        public void onStop()
        {
            try
            {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                stopScreenSharing();
            }
            catch (Exception e)
            {

            }

        }
    }
    private void stopScreenSharing()
    {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        //mMediaRecorder.release(); //If used: mMediaRecorder object cannot
        // be reused again
        destroyMediaProjection();
    }
    private void destroyMediaProjection()
    {
        if (mMediaProjection != null)
        {
            mMediaProjection.unregisterCallback(mMediaProjectionCallback);
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        //  Log.i(TAG, "MediaProjection Stopped");
    }



    public void check_InApp()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("ads", 0);
        buy = pref.getBoolean("buy", false);
        if(buy) {
            boolean foever = pref.getBoolean("forever", false);
            if (!foever) {
                Calendar cal = Calendar.getInstance();
                long time = cal.getTimeInMillis();
                long prev = pref.getLong("Ad_time", 0);
                Date current = new Date(time);
                Date previous = new Date(prev);
                int duration = pref.getInt("duration", 0);
                if (duration == 0)
                    return;
                long diff = current.getTime() - previous.getTime();
                long days = TimeUnit.MILLISECONDS.toDays(diff);
                if (duration != 6) {
                    if (days >= duration)
                    {
                        SharedPreferences prefs = getApplicationContext().getSharedPreferences("ads", 0);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("buy", false);
                        editor.putLong("Ad_time", 0);
                        editor.putString("duration", "0");
                        editor.commit();
                        buy = false;
                    }

                }
                else if (duration == 6) {
                    duration = 6 * 30;
                    if (days >= duration) {
                        SharedPreferences prefs = getApplicationContext().getSharedPreferences("ads", 0);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("buy", false);
                        editor.putLong("Ad_time", 0);
                        editor.putString("duration", "0");
                        editor.commit();
                        buy = false;
                    }
                }


            }
        }


    }
}