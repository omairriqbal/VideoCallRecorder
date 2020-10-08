package com.videocaht.recorder.facetime.videocall.screenrecorder;

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
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class FloatingViewService extends Service implements View.OnClickListener
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
    private static final int REQUEST_CODE = 1000;
    private int mScreenDensity;
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
    String name;
    View right,left;
    private WindowManager mWindowManager,windowManagerClose;
    private View mFloatingView;

    View layout;
    LinearLayout options;
    private ImageView collapsed_iv,fab1;
    ComponentName serviceName;
    private Handler handler=new Handler();
    TextView timmer;
    View crossLayout;
    private GestureDetector gestureDetector;
    private int screen_height;
    private int screen_width;
    private WindowManager.LayoutParams params,params_1,params_2,params_3,params_4,params_5,bg_pram,close_prams;
    private MediaProjectionManager mgr;
    static final String EXTRA_RESULT_CODE="resultCode";
    static final String EXTRA_RESULT_INTENT="resultIntent";
    private int resultCode;
    private Intent resultData;
    Animation show,hide,bottom_up,bottom_down;
    static boolean open=true,r=true;
    MoveAnimator animator=new MoveAnimator();
    ButtonAnimator btn_animator_1=new ButtonAnimator();
    ButtonAnimator btn_animator_2=new ButtonAnimator();
    ButtonAnimator btn_animator_3=new ButtonAnimator();
    ButtonAnimator btn_animator_4=new ButtonAnimator();
    ButtonAnimator btn_animator_5=new ButtonAnimator();
    static boolean singleTap=false;
    private static long mDeBounce = 0;

    private WindowManager windowManagerPanel;
    private ImageView circle_1,circle_2,circle_3,circle_4,circle_5;
    private LinearLayout layout_1,layout_2,layout_3,layout_4,layout_5,bg;
    static boolean recording=false;
    int x1,y1,x2,y2,x3,y3,x4,y4,x5,y5;
    private boolean showWidget;
    private View countdown;
    private TextView counter;
    private boolean buy;


    private void getScreenSize()
    {
        if(mWindowManager==null)
            return;
        Display display = mWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        Point displaySize = new Point();
        mWindowManager.getDefaultDisplay().getRealSize(displaySize);
        screen_width = size.x;//1080
        screen_height = size.y;//1920
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mWindowManager.getDefaultDisplay().getRealMetrics(metrics);
        mScreenDensity = (int)(metrics.density * 160f);

    }
    private void openPermissionActivity()
    {
        Intent mainIntent = new Intent(getApplicationContext(), PermissionActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
    }
    public FloatingViewService() {
    }
    public Intent restoreIntent()
    {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("ads", 0);
        Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION");
        String uri = prefs.getString("permission", "");
        try {
            return Intent.getIntent(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
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
                    try
                    {
                        if(mWindowManager!=null)
                        {
                            params = new WindowManager.LayoutParams(
                                    WindowManager.LayoutParams.WRAP_CONTENT,
                                    WindowManager.LayoutParams.WRAP_CONTENT,
                                    LAYOUT_FLAG,
                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                    PixelFormat.TRANSLUCENT);
                            params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                            params.x = -screen_width/2 ; // horizontal center for the image
                            params.y = 0;
                            layout.animate().translationX(0f).translationY(600f).setInterpolator(new AccelerateDecelerateInterpolator()).start();

                            try {
                                mWindowManager.addView(mFloatingView, params);
                                windowManagerClose.addView(layout, close_prams);

                                show_Circle();
                            }catch (Exception e)
                            {

                            }
                        }
                    }
                    catch (Exception e)
                    {

                    }

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
                            customHandler.removeCallbacks(updateTimerThread);
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
                            startTime=current_Time;
                            customHandler.postDelayed(updateTimerThread, 0);
                            update_Noti("play");
                        }
                        catch (Exception e)
                        {

                        }

                    }

                    break;

                case ACTION_OPEN_MAIN:
                    clearAll();

                    Intent in=new Intent(getApplicationContext(), FirstActivity.class);
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
        SharedPreferences myPrefs = getSharedPreferences("MY_PREF", 0);
        final SharedPreferences.Editor myPrefsEdit = myPrefs.edit();
        showWidget = myPrefs.getBoolean("show widget", true);
        // LAYOUT_FLAG =WindowManager.LayoutParams.TYPE_PHONE;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
        {

            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_TOAST;
        }
        else
        {
            LAYOUT_FLAG =  WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }


        show = AnimationUtils.loadAnimation(getApplication(), R.anim.show);
        hide = AnimationUtils.loadAnimation(getApplication(), R.anim.left_in);
        bottom_down = AnimationUtils.loadAnimation(getApplication(), R.anim.bottom_down);
        bottom_up = AnimationUtils.loadAnimation(getApplication(), R.anim.bottom_up);
        DisplayMetrics metrics = new DisplayMetrics();
        mProjectionManager = (MediaProjectionManager) getSystemService
                (Context.MEDIA_PROJECTION_SERVICE);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        getScreenSize();
        mMediaProjectionCallback = new MediaProjectionCallback();
        //getting the widget layout from xml using layout inflater
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floatingwidget, null);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
        params.x = -screen_width/2 ; // horizontal center for the image
        params.y = 0;

        close_prams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        close_prams.gravity =Gravity.CENTER;
        close_prams.x = 0;
        close_prams.y = screen_height/2-150;


        layout = LayoutInflater.from(this).inflate(R.layout.bottom_layout, null);
        countdown=LayoutInflater.from(this).inflate(R.layout.countdown, null);
        counter=(TextView)countdown.findViewById(R.id.countdown);
        windowManagerClose = (WindowManager) getSystemService(WINDOW_SERVICE);
        layout.animate().translationX(0f).translationY(600f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        layout.setVisibility(View.INVISIBLE);
        collapsed_iv = (ImageView)mFloatingView.findViewById(R.id.collapsed_iv);

        timmer=mFloatingView.findViewById(R.id.timmer);
        Typeface font1 = Typeface.createFromAsset(getAssets(), "DS-DIGIB.TTF");
        timmer.setTypeface(font1);

        gestureDetector = new GestureDetector(this, new SingleTapConfirm());
        initializeView();
        windowManagerPanel.addView(bg, bg_pram);
        show_Circle();

        mWindowManager.addView(mFloatingView, params);
        windowManagerClose.addView(layout, close_prams);

        createRecordNotification();
        mFloatingView.setOnTouchListener(new View.OnTouchListener()
        {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            DisplayMetrics displayMetrics = new DisplayMetrics();
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;



            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(gestureDetector.onTouchEvent(event))
                {

                }
                else {
                    switch (event.getAction())
                    {

                        case MotionEvent.ACTION_DOWN:
                        {
                            collapsed_iv.setAlpha(255);
                            collapsed_iv.animate().translationX(0f).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                            collapsed_iv.animate().scaleX(1f).scaleY(1f).setDuration(100);

                            timmer.animate().translationX(0f).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                            timmer.animate().scaleX(1f).scaleY(1f).setDuration(100);

                            initialX = params.x;
                            initialY = params.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            animator.stop();
                            btn_animator_1.stop();
                            btn_animator_2.stop();
                            btn_animator_3.stop();
                            btn_animator_4.stop();
                            btn_animator_5.stop();
                            if(handler!=null)
                                handler.removeCallbacksAndMessages(null);

                            if (open)
                                hide_circle();
                            if(!recording) {
                                layout.setVisibility(View.VISIBLE);
                                layout.animate().translationX(0f).translationY(50f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                            }
                            // close.startAnimation(bottom_up);

                            break;
                        }


                        case MotionEvent.ACTION_MOVE:
                        {



                            if (MathUtil.betweenExclusive(params.x, -10, 70) && MathUtil.betweenExclusive(params.y, -150+screen_height / 3, screen_height / 2)) {
                                if(!recording)
                                {
                                    Visibility();
                                    Vibrator vv = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        vv.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                                    } else {
                                        //deprecated in API 26
                                        vv.vibrate(50);
                                    }

                                }
                            }
                            else {
                                //this code is helping the widget to move around the screen with fingers

                                params.x = initialX + (int) (event.getRawX() - initialTouchX);
                                params.y = initialY + (int) (event.getRawY() - initialTouchY);
                                mWindowManager.updateViewLayout(mFloatingView, params);
                            }


                            break;
                        }

                        case MotionEvent.ACTION_UP: {

                            if (MathUtil.betweenExclusive(params.x, -100, 100) && !MathUtil.betweenExclusive(params.y, screen_height / 3, screen_height / 2)) {
                                //moving to center range of screen
                                animator.start(screen_width / 2, params.y);


                            } else if (MathUtil.betweenExclusive((int) event.getRawX(), 0, screen_width / 5)) {
                                //move to left of screen

                                if (MathUtil.betweenExclusive((int) event.getRawY(), 0, screen_height / 10)) {


                                    animator.start(-screen_width / 2, -((screen_height / 2) - 150));

                                } else if (MathUtil.betweenExclusive((int) event.getRawY(), 9 * (screen_height / 10), screen_height)) {
                                    animator.start(-screen_width / 2, screen_height / 2 - 150);

                                } else {
                                    animator.start(-screen_width / 2, params.y);

                                }

                            } else if (MathUtil.betweenExclusive((int) event.getRawX(), screen_width - (screen_width / 5), screen_width)) {
                                //move to right of screen

                                if (MathUtil.betweenExclusive((int) event.getRawY(), 0, screen_height / 10)) {

                                    // params.y = 0 ;
                                    animator.start(screen_width / 2, -((screen_height / 2) - 150));
                                    layout.setVisibility(View.INVISIBLE);
                                } else if (MathUtil.betweenExclusive((int) event.getRawY(), 9 * (screen_height / 10), screen_height)) {
                                    animator.start(screen_width / 2, screen_height / 2 - 150);
                                    mWindowManager.updateViewLayout(v, params);


                                } else {
                                    animator.start(screen_width / 2, params.y);

                                }

                            } else if (MathUtil.betweenExclusive((int) event.getRawX(), screen_width / 5, 2 * (screen_width / 5))) {
                                //move to left of screen
                                if (MathUtil.betweenExclusive((int) event.getRawY(), 0, screen_height / 10)) {


                                    animator.start(-screen_width / 2, -((screen_height / 2) - 150));


                                } else if (MathUtil.betweenExclusive((int) event.getRawY(), 9 * (screen_height / 10), screen_height)) {
                                    animator.start(-screen_width / 2, screen_height / 2 - 150);


                                } else {
                                    animator.start(-screen_width / 2, params.y);


                                }
                            } else if (MathUtil.betweenExclusive((int) event.getRawX(), 3 * (screen_width / 5), screen_width)) {
                                //move to right of screen
                                if (MathUtil.betweenExclusive((int) event.getRawY(), 0, screen_height / 10)) {

                                    // params.y = 0 ;
                                    animator.start(screen_width / 2, -((screen_height / 2) - 150));

                                    layout.setVisibility(View.INVISIBLE);
                                } else if (MathUtil.betweenExclusive((int) event.getRawY(), 9 * (screen_height / 10), screen_height)) {
                                    animator.start(screen_width / 2, screen_height / 2 - 150);


                                } else {
                                    animator.start(screen_width / 2, params.y);


                                }
                            }
                            else if(!recording)
                            {
                                if (MathUtil.betweenExclusive(params.x, -10, 70) && MathUtil.betweenExclusive(params.y, -150 + screen_height / 3, screen_height / 2)) {

                                    Visibility();

                                    Vibrator vv = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        vv.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                                    } else {
                                        //deprecated in API 26
                                        vv.vibrate(50);
                                    }
                                }
                                else
                                {

                                    //not in either of the above cases
                                    animator.start(screen_width / 2, params.y);
                                    mWindowManager.updateViewLayout(v, params);
                                }

                            }
                            else
                            {

                                //not in either of the above cases
                                animator.start(screen_width / 2, params.y);
                                mWindowManager.updateViewLayout(v, params);
                            }
                            //  close.startAnimation(bottom_down);
                            if(!recording) {
                                layout.animate().translationX(0f).translationY(600f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                                layout.setVisibility(View.INVISIBLE);
                            }
                            handler.postDelayed(() ->
                            {
                                {
                                    if(open)
                                        hide_circle();
                                    collapsed_iv.setAlpha(130);
                                    collapsed_iv.animate().scaleX(0.7f).scaleY(0.7f).setDuration(100);
                                    timmer.animate().scaleX(0.7f).scaleY(0.7f).setDuration(100);
                                    int x = collapsed_iv.getLayoutParams().width / 2;
                                    if (params.x < 0) {
                                        collapsed_iv.animate().translationX(-x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                                        timmer.animate().translationX(-x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                                    }
                                    else {
                                        collapsed_iv.animate().translationX(x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                                        timmer.animate().translationX(x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                                    }


                                }

                            }, 5000);

                        }

                    }


                }
                return true;
            }
        });
        serviceName = new ComponentName(this, FloatingViewService.class);
    }

    public void clearAll()
    {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
        try {
            if (mFloatingView != null)
                mWindowManager.removeView(mFloatingView);

        }catch (Exception e)
        {

        }
        try {
            if (layout != null) windowManagerClose.removeView(layout);


        }catch (Exception e)
        {

        }
        if(recording)
            stop();

        if(open)
            hide_circle();


    }
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        getScreenSize();
        if(params.x<0)
            animator.start(-screen_width/2,params.y);
        else
            animator.start(screen_width/2,params.y);
    }


    public void update_Noti(String action)
    {

        Intent closeButton = null;
        Intent stop = new Intent(this, FloatingViewService.class);
        stop.setAction(ACTION_STOP);

        PendingIntent pendingStopIntent = PendingIntent.getService(getApplicationContext(), 1, stop, 0);
        RemoteViews notificationView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widget_new_notification);

        if(action.equals("play"))
        {
            closeButton = new Intent(this, FloatingViewService.class);
            closeButton.setAction(ACTION_PAUSE);
            notificationView.setImageViewResource(R.id.btn_play, R.mipmap.ic_pause);
            circle_1.setImageResource(R.mipmap.ic_action_pause);
            circle_1.setId(R.id.pause);
        }
        else if(action.equals("pause"))
        {
            closeButton = new Intent(this, FloatingViewService.class);
            closeButton.setAction(ACTION_RESUME);
            notificationView.setImageViewResource(R.id.btn_play, R.mipmap.ic_action_resume);
            circle_1.setImageResource(R.mipmap.ic_resume);
            circle_1.setId(R.id.resume);
        }
        else  if(action.equals("stop"))
        {
            closeButton = new Intent(this, FloatingViewService.class);
            closeButton.setAction(ACTION_PAUSE_PLAY);
            notificationView.setImageViewResource(R.id.btn_play, R.drawable.ic_notification_play);
            circle_1.setImageResource(R.mipmap.widget_rec);
            circle_1.setId(R.id.rec);

        }
        PendingIntent pendingSwitchIntent = PendingIntent.getService(getApplicationContext(), 0, closeButton, 0);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationView.setOnClickPendingIntent(R.id.btn_stop, pendingStopIntent);
        notificationView.setOnClickPendingIntent(R.id.btn_play, pendingSwitchIntent);

        notificationView.setTextViewText(R.id.text,"Recorder is recording. Tap stop to finish");
        myNotification.bigContentView = notificationView;
//        windowManagerPanel.updateViewLayout(layout_1,params_1);
        notificationManager.notify(1, myNotification);


    }
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    private long current_Time=0l;
    private Runnable updateTimerThread = new Runnable()
    {

        public void run()
        {
            long timeInMilliseconds = 0L;
            long timeSwapBuff = 0L;
            long updatedTime = 0L;
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;
            current_Time=updatedTime;
            int secs = (int) (updatedTime / 1000);
            int  mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            //  collapsed_iv.setBackground(null);
            collapsed_iv.setImageDrawable(getApplicationContext().getResources().getDrawable(R.mipmap.ic_empty));
            timmer.setText(String.format("%02d", mins) + ":"
                    + String.format("%02d", secs));


            customHandler.postDelayed(this, 0);
        }

    };

    private void Visibility()
    {
        if (mWindowManager != null)
        {
            try {
                mWindowManager.removeViewImmediate(mFloatingView);
                windowManagerClose.removeViewImmediate(layout);
            }
            catch (Exception e)
            {

            }
            if(open)
                hide_circle();

         /*   LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                    .getInstance(FloatingViewService.this);
            localBroadcastManager.*/

            SharedPreferences myPrefs = getSharedPreferences("MY_PREF", 0);
            final SharedPreferences.Editor myPrefsEdit = myPrefs.edit();
            boolean close_app = myPrefs.getBoolean("close app", false);
            if(!close_app)
            {
                sendBroadcast(new Intent("com.action.close"));
                stopSelf();
                stopForeground(true);
                notificationManager.cancelAll();
            }

        }
    }
    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener
    {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event)
        {
            if(open)
                hide_circle();
            else
                show_Circle();


            // Toast.makeText(getApplicationContext(),"Single tap",Toast.LENGTH_SHORT).show();
            singleTap=true;
            return true;
        }
    }

    private static class MathUtil
    {
        public static boolean betweenExclusive(int x, int min, int max) {
            return x > min && x < max;
        }
    }
    RemoteViews notificationView;
    Notification myNotification;
    NotificationManager notificationManager;
    private void createRecordNotification()
    {


        Intent play = new Intent(this, FloatingViewService.class);
        play.setAction(ACTION_PAUSE_PLAY);
        PendingIntent pendingSwitchIntent = PendingIntent.getService(getApplicationContext(), 0, play, 0);

        Intent stop = new Intent(this, FloatingViewService.class);
        stop.setAction(ACTION_STOP);
        PendingIntent pendingStopIntent = PendingIntent.getService(getApplicationContext(), 1, stop, 0);


       /* Intent clear = new Intent(this, FloatingViewService.class);
        clear.setAction(ACTION_CLEAR);*/
        // PendingIntent pendingClearIntent = PendingIntent.getService(getApplicationContext(), 2, clear, 0);

        Intent home = new Intent(this, FloatingViewService.class);
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
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(false).build();
        }
        else {
            myNotification  = new Notification.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_noti)
                    .setTicker("Recorder is ready")
                    .setOnlyAlertOnce(true)
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
        try {
            if (mFloatingView != null)
                mWindowManager.removeView(mFloatingView);
            if(open)
                hide_circle();


        }catch (Exception e)
        {

        }
        stopForeground(true);
        stopSelf();
        stopService(new Intent(this, FloatingViewService.class));
        notificationManager.cancelAll();
    }

    @Override
    public void onClick(View v)
    {
        int x=collapsed_iv.getLayoutParams().width/2;
        switch (v.getId())
        {
            case R.id.pause:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                {
                    try {
                        mMediaRecorder.pause();
                        customHandler.removeCallbacks(updateTimerThread);
                        update_Noti("pause");
                    }
                    catch (Exception e)
                    {

                    }

                }
                break;
            case R.id.resume:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                {
                    try {
                        mMediaRecorder.resume();
                        startTime=current_Time;
                        customHandler.postDelayed(updateTimerThread, 0);
                        update_Noti("play");
                    }
                    catch (Exception e)
                    {

                    }

                }
                break;
            case R.id.rec:
                createRecordNotification();
                record();
                break;

            case R.id.stop:
                stop();
                break;

            case R.id.cc:
                hide_circle();
                collapsed_iv.setAlpha(130);

                if(params.x<0)
                    collapsed_iv.animate().translationX(-x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                else
                    collapsed_iv.animate().translationX(x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                layout.setVisibility(View.INVISIBLE);
                break;

            case R.id.openApp:
                // clearAll();

                hide_circle();
                collapsed_iv.setAlpha(130);
                collapsed_iv.animate().scaleX(0.7f).scaleY(0.7f).setDuration(100);
                timmer.animate().scaleX(0.7f).scaleY(0.7f).setDuration(100);

                if(params.x<0) {
                    collapsed_iv.animate().translationX(-x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    timmer.animate().translationX(-x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                }
                else {
                    collapsed_iv.animate().translationX(x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    timmer.animate().translationX(x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();

                }
                Intent in=new Intent(getApplicationContext(), FirstActivity.class);
                in.setFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
                break;
            case R.id.screen_shot:
                if(!haspermission())
                {
                    Toast.makeText(this, "Can't Save! Permissions Missing", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent ii=
                        new Intent(this, ScreenshotService.class)
                                .putExtra(ScreenshotService.EXTRA_RESULT_CODE, resultCode)
                                .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, resultData);




                bg.setVisibility(View.INVISIBLE);
                mFloatingView.setVisibility(View.INVISIBLE);
                hide_circle();
                handler.postDelayed(() ->
                {
                    {

                        bg.setVisibility(View.VISIBLE);
                        mFloatingView.setVisibility(View.VISIBLE);
                        show_Circle();
                    }

                }, 2000);
                startService(ii);
                break;
            case R.id.bg:
                hide_circle();
                collapsed_iv.setAlpha(130);
                collapsed_iv.animate().scaleX(0.7f).scaleY(0.7f).setDuration(100);
                timmer.animate().scaleX(0.7f).scaleY(0.7f).setDuration(100);

                if(params.x<0) {
                    collapsed_iv.animate().translationX(-x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    timmer.animate().translationX(-x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                }
                else {
                    collapsed_iv.animate().translationX(x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    timmer.animate().translationX(x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();

                }
                break;
        }
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

                counter.setText(""+seconds);
            }

            @Override
            public void onFinish()
            {
                try {
                    mWindowManager.removeViewImmediate(countdown);
                }catch (Exception e)
                {

                }
                startTime = SystemClock.uptimeMillis();
                if(!showWidget)
                    customHandler.postDelayed(updateTimerThread, 0);
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

        SharedPreferences myPrefs = getSharedPreferences("MY_PREF", 0);
        final SharedPreferences.Editor myPrefsEdit = myPrefs.edit();
        showWidget = myPrefs.getBoolean("show widget", false);
        if ((showWidget))
        {
            try {
                mWindowManager.removeViewImmediate(mFloatingView);
            }catch (Exception ee)
            {

            }
            try
            {
                windowManagerClose.removeViewImmediate(layout);
            }
            catch (Exception e)
            {

            }
            try {
                if (open)
                    hide_circle();
            }
            catch (Exception e)
            {

            }

        }
        else
        {
            int x=collapsed_iv.getLayoutParams().width/2;
            hide_circle();
            collapsed_iv.setAlpha(130);
            collapsed_iv.animate().scaleX(0.7f).scaleY(0.7f).setDuration(100);
            timmer.animate().scaleX(0.7f).scaleY(0.7f).setDuration(100);

            if(params.x<0) {
                collapsed_iv.animate().translationX(-x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                timmer.animate().translationX(-x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            }
            else {
                collapsed_iv.animate().translationX(x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                timmer.animate().translationX(x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();

            }
        }
        if(!recording)
        {
            // Toast.makeText(getApplicationContext(), "started", Toast.LENGTH_SHORT).show();
            WindowManager.LayoutParams countdown_pram = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            countdown_pram.gravity = Gravity.CENTER;
            countdown_pram.x = 0;
            countdown_pram.y = 0;
            try {
                mWindowManager.addView(countdown, countdown_pram);
            }
            catch (Exception e)
            {

            }
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
            if (open)
                hide_circle();
            return;
        }

        SharedPreferences myPrefs = getSharedPreferences("MY_PREF", 0);
        final SharedPreferences.Editor myPrefsEdit = myPrefs.edit();
        showWidget = myPrefs.getBoolean("show widget", false);
        if ((showWidget))
        {
            try
            {
                try
                {
                    mWindowManager.removeViewImmediate(mFloatingView);
                    windowManagerClose.removeViewImmediate(layout);
                }
                catch (Exception e)
                {

                }

                if (open)
                    hide_circle();
            }catch (Exception e)
            {

            }

        }
        else
        {
            int x=collapsed_iv.getLayoutParams().width/2;
            hide_circle();
            collapsed_iv.setAlpha(130);
            collapsed_iv.animate().scaleX(0.7f).scaleY(0.7f).setDuration(100);
            timmer.animate().scaleX(0.7f).scaleY(0.7f).setDuration(100);

            if(params.x<0) {
                collapsed_iv.animate().translationX(-x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                timmer.animate().translationX(-x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            }
            else {
                collapsed_iv.animate().translationX(x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                timmer.animate().translationX(x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();

            }
        }
        if(!recording)
        {
            if(!check_Ad()) {


                // Toast.makeText(getApplicationContext(), "started", Toast.LENGTH_SHORT).show();
                WindowManager.LayoutParams countdown_pram = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                countdown_pram.gravity = Gravity.CENTER;
                countdown_pram.x = 0;
                countdown_pram.y = 0;
                try {
                    mWindowManager.addView(countdown, countdown_pram);
                }
                catch (Exception e)
                {

                }
                SharedPreferences settings1 = getSharedPreferences("MY_PREF", 0);
                int sec = settings1.getInt("timmer", 3);
                recording = !recording;
                if(sec!=0)
                    start_Timer(1000 * (sec+1));

                else
                    start_Timer(1000 * sec);
            }
        }


    }
    public boolean check_Ad()
    {
        if(!buy)
        {
            incremntCount();
            check_InApp();
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("recorder", 0);
            int count = prefs.getInt("count", 0);
            //   Toast.makeText(MainActivity.this, ""+count, Toast.LENGTH_SHORT).show();
            if (count >= 5) {

                Intent mainIntent = new Intent(getApplicationContext(), Ad_Acc.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                return true;

            }
        }
        return false;
       /* Intent mainIntent = new Intent(getApplicationContext(), Ad_Acc.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        return true;*/
    }
    public void stop()
    {
        if(recording) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mMediaRecorder.stop();
                        mMediaRecorder.reset();
                        mMediaRecorder.release();
                        mMediaRecorder = null;
                        recording=!recording;
                    }
                    catch (Exception e)
                    {
                        // Toast.makeText(getApplicationContext(), "crash", Toast.LENGTH_SHORT).show();
                        return;
                        //  mMediaRecorder.release();
                    }
                }
            }).start();

            SharedPreferences settings = getSharedPreferences("shared preferences", MODE_PRIVATE);

            String path =  settings.getString("storage path","storage/emulated/0/");
            String fullPath = path + "/Video Call Recorder";

            String output = fullPath + name;
            stopScreenSharing();
            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{output}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
             Toast.makeText(getApplicationContext(), "SavedService", Toast.LENGTH_SHORT).show();
            customHandler.removeCallbacks(updateTimerThread);
            collapsed_iv.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.button_recorder));
            timmer.setText("");
            notificationView.setTextViewText(R.id.text, "Recorder is ready.");
            myNotification.bigContentView = notificationView;
            notificationManager.notify(1, myNotification);


            Intent in=new Intent(FloatingViewService.this,Finish_popup.class);
            in.setFlags(FLAG_ACTIVITY_NEW_TASK);
            in.putExtra("name",name);
            in.putExtra("url",fullPath);

             new Handler().postDelayed(() -> startActivity(in), 1000);
            hide_circle();
            collapsed_iv.setAlpha(130);
            collapsed_iv.animate().scaleX(0.7f).scaleY(0.7f).setDuration(100);
            int x=collapsed_iv.getLayoutParams().width/2;
            if(params.x<0)
                collapsed_iv.animate().translationX(-x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            else
                collapsed_iv.animate().translationX(x).translationY(0f).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            if ((showWidget))
            {
                try {
                    mWindowManager.addView(mFloatingView, params);
                    windowManagerClose.addView(layout, close_prams);
                }
                catch (Exception e)
                {

                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
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
        SharedPreferences settings = getSharedPreferences("shared preferences", MODE_PRIVATE);

        String path =  settings.getString("storage path","storage/emulated/0/");
        String fullPath = path + "/Video Call Recorder";

        File ff=new File(fullPath);
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
            mMediaRecorder.setOutputFile(fullPath + name);
            mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setVideoEncodingBitRate(3000000);
            mMediaRecorder.setVideoFrameRate(16);
            Toast.makeText(this, "Savedservie2", Toast.LENGTH_SHORT).show();
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
    private class MediaProjectionCallback extends MediaProjection.Callback
    {
        @Override
        public void onStop()
        {
            try {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
            }
            catch(Exception e)
            {

            }
            try
            {
                stopScreenSharing();
            }catch(Exception e)
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




    //this class is written to move the image to either corners if touch_up
    private class MoveAnimator implements Runnable {

        private Handler handler = new Handler(Looper.getMainLooper());
        private float destinationX;
        private float destinationY;
        private long startingTime;

        private void start(float x, float y) {
            this.destinationX = x;
            this.destinationY = y;
            startingTime = System.currentTimeMillis();
            handler.post(this);
        }

        @Override
        public void run()
        {
            if (mFloatingView != null && mFloatingView.getParent() != null)
            {
                float progress = Math.min(1, (System.currentTimeMillis() - startingTime) / 100f);

                float deltaX = (destinationX - params.x) * progress;
                float deltaY = (destinationY - params.y) * progress;
                move(deltaX, deltaY);
                if (progress < 1) {
                    handler.post(this);
                }
            }
        }

        private void stop() {
            handler.removeCallbacks(this);
        }
    }
    protected void move(float deltaX, float deltaY) {
        params.x += deltaX;
        params.y += deltaY;
        mWindowManager.updateViewLayout(mFloatingView, params);
    }
    private class ButtonAnimator implements Runnable
    {

        private Handler handler = new Handler(Looper.getMainLooper());
        private float destinationX;
        private float destinationY;
        private long startingTime;
        int count;

        private void start(float x, float y, int c) {
            this.destinationX = x;
            this.destinationY = y;
            this.count=c;
            startingTime = System.currentTimeMillis();
            handler.post(this);
        }

        @Override
        public void run() {
            if (layout != null )
            {
                float progress = Math.min(1, (System.currentTimeMillis() - startingTime) / 100f);
                // float progress = 10f;
                float deltaX = 0,deltaY=0;
                if(count==1)
                {
                    deltaX = (destinationX - params_1.x) * progress;
                    deltaY = (destinationY - params_1.y) * progress;
                }
                if(count==2)
                {
                    deltaX = (destinationX - params_2.x) * progress;
                    deltaY = (destinationY - params_2.y) * progress;
                }
                if(count==3)
                {
                    deltaX = (destinationX - params_3.x) * progress;
                    deltaY = (destinationY - params_3.y) * progress;
                }
                if(count==4)
                {
                    deltaX = (destinationX - params_4.x) * progress;
                    deltaY = (destinationY - params_4.y) * progress;
                }
                if(count==5)
                {
                    deltaX = (destinationX - params_5.x) * progress;
                    deltaY = (destinationY - params_5.y) * progress;
                }

                move(deltaX, deltaY);
                if (progress < 1)
                {
                    handler.post(this);
                }

            }
        }

        private void stop()
        {
            handler.removeCallbacks(this);
        }

        protected void move(float deltaX, float deltaY)
        {
            if(count==1) {
                params_1.x += deltaX;
                params_1.y += deltaY;
                windowManagerPanel.updateViewLayout(layout_1, params_1);
            }
            if(count==2)
            {
                params_2.x += deltaX;
                params_2.y += deltaY;
                windowManagerPanel.updateViewLayout(layout_2, params_2);
            }
            if(count==3)
            {
                params_3.x += deltaX;
                params_3.y += deltaY;
                windowManagerPanel.updateViewLayout(layout_3, params_3);
            }
            if(count==4)
            {
                params_4.x += deltaX;
                params_4.y += deltaY;
                windowManagerPanel.updateViewLayout(layout_4, params_4);
            }
            if(count==5)
            {
                params_5.x += deltaX;
                params_5.y += deltaY;
                windowManagerPanel.updateViewLayout(layout_5, params_5);
            }

        }
    }
    private void initializeView()
    {
        windowManagerPanel = (WindowManager) getSystemService(WINDOW_SERVICE);
        circle_1 = new ImageView(this);
        circle_1.setImageResource(R.drawable.ic_notification_play);
        circle_1.setMaxWidth(10);
        circle_1.setMaxHeight(10);
        circle_1.setId(R.id.rec);
        circle_1.setLayoutParams(new android.view.ViewGroup.LayoutParams(converTodp(40),converTodp(40)));
        layout_1 = new LinearLayout(this);
        layout_1.addView(circle_1);
        circle_1.setOnClickListener(this);


        circle_2 = new ImageView(this);
        circle_2.setImageResource(R.drawable.ic_notification_stop);
        circle_2.setMaxWidth(10);
        circle_2.setMaxHeight(10);
        circle_2.setId(R.id.stop);
        circle_2.setLayoutParams(new android.view.ViewGroup.LayoutParams(converTodp(40),converTodp(40)));
        layout_2 = new LinearLayout(this);
        layout_2.addView(circle_2);
        circle_2.setOnClickListener(this);

        circle_3 = new ImageView(this);
        circle_3.setImageResource(R.drawable.ic_notification_screen_shot);
        circle_3.setMaxWidth(10);
        circle_3.setMaxHeight(10);
        circle_3.setId(R.id.screen_shot);
        circle_3.setLayoutParams(new android.view.ViewGroup.LayoutParams(converTodp(40),converTodp(40)));
        layout_3 = new LinearLayout(this);
        layout_3.addView(circle_3);
        circle_3.setOnClickListener(this);

        circle_4 = new ImageView(this);
        circle_4.setImageResource(R.drawable.ic_notification_view);
        circle_4.setMaxWidth(10);
        circle_4.setMaxHeight(10);
        circle_4.setId(R.id.openApp);
        circle_4.setLayoutParams(new android.view.ViewGroup.LayoutParams(converTodp(40),converTodp(40)));
        layout_4 = new LinearLayout(this);
        layout_4.addView(circle_4);
        circle_4.setOnClickListener(this);

        circle_5 = new ImageView(this);
        circle_5.setImageResource(R.drawable.ic_notification_exit);
        circle_5.setMaxWidth(10);
        circle_5.setMaxHeight(10);
        circle_5.setId(R.id.cc);
        circle_5.setLayoutParams(new android.view.ViewGroup.LayoutParams(converTodp(40),converTodp(40)));
        layout_5 = new LinearLayout(this);
        layout_5.addView(circle_5);
        circle_5.setOnClickListener(this);

        bg = new LinearLayout(this);
        bg.setBackgroundColor(getResources().getColor(R.color.bg));
        bg.setId(R.id.bg);
        bg.setOnClickListener(this);
        bg_pram = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE ,
                PixelFormat.TRANSLUCENT);


    }

    public void show_Circle()
    {
        collapsed_iv.animate().scaleX(1f).scaleY(1f).setDuration(100);
        int x=params.x;
        int y=params.y;
        int topedge=-(screen_height/2)+250;
        int bottomedge=screen_height/2-150-100;
        open=true;
        if((x<0))
        {
            if( ((y<0) && (y > topedge))
                    ||((y>0) && (y<bottomedge))
                    ||(y==0))
            {

                //on left edge
                params_1 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_1.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_1.x = x ; // horizontal center for the image
                params_1.y = y ;
                x1=x + converTodp(20);
                y1=y - converTodp(60);

                params_2 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_2.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_2.x = x ; // horizontal center for the image
                params_2.y = y ;
                x2=x + converTodp(60);
                y2=y - converTodp(40);
                //on left edge
                params_3 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_3.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_3.x = x ; // horizontal center for the image
                params_3.y = y;
                x3=x+converTodp(80);
                y3=y;

                params_4 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_4.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_4.x = x; // horizontal center for the image
                params_4.y = y ;
                x4=x + converTodp(60);
                y4=y + converTodp(40);

                params_5 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_5.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_5.x = x; // horizontal center for the image
                params_5.y = y;
                x5=x + converTodp(20);
                y5=y + converTodp(60);

            }
            else
            {
                if(((y<0) && (y < topedge)))
                {
                    //topedge
                    params.y=y+converTodp(50);
                    animator.start(x,y+converTodp(50));


                }
                else
                {
                    //bottomedge
                    params.y=y-converTodp(50);
                    animator.start(x,y-converTodp(50));
                }
                x=params.x;
                y=params.y;
                params_1 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_1.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_1.x = x ; // horizontal center for the image
                params_1.y = y ;
                x1=x + converTodp(20);
                y1=y - converTodp(60);

                params_2 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_2.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_2.x = x ; // horizontal center for the image
                params_2.y = y ;
                x2=x + converTodp(60);
                y2=y - converTodp(40);
                //on left edge
                params_3 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_3.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_3.x = x ; // horizontal center for the image
                params_3.y = y;
                x3=x+converTodp(80);
                y3=y;

                params_4 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_4.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_4.x = x; // horizontal center for the image
                params_4.y = y ;
                x4=x + converTodp(60);
                y4=y + converTodp(40);

                params_5 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_5.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_5.x = x; // horizontal center for the image
                params_5.y = y;
                x5=x + converTodp(20);
                y5=y + converTodp(60);
            }

        }
        else
        {
            //right edge
            if( ((y<0) && (y > topedge))
                    ||((y>0) && (y<bottomedge))
                    ||(y==0))
            {
                params_1 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_1.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_1.x = x ; // horizontal center for the image
                params_1.y = y ;
                x1=x + converTodp(20);
                y1=y - converTodp(60);

                params_2 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_2.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_2.x = x ; // horizontal center for the image
                params_2.y = y ;
                x2=x - converTodp(60);
                y2=y - converTodp(40);
                //on left edge
                params_3 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_3.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_3.x = x ; // horizontal center for the image
                params_3.y = y;
                x3=x-converTodp(80);
                y3=y;

                params_4 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_4.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_4.x = x; // horizontal center for the image
                params_4.y = y ;
                x4=x - converTodp(60);
                y4=y + converTodp(40);

                params_5 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_5.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_5.x = x; // horizontal center for the image
                params_5.y = y;
                x5=x + converTodp(20);
                y5=y + converTodp(60);
            }
            else
            {
                if(((y<0) && (y < topedge)))
                {
                    //topedge
                    params.y=y+converTodp(50);
                    animator.start(x,y+converTodp(50));


                }
                else
                {
                    //bottomedge
                    params.y=y-200;
                    animator.start(x,y-200);
                }
                x=params.x;
                y=params.y;
                params_1 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_1.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_1.x = x ; // horizontal center for the image
                params_1.y = y ;
                x1=x + converTodp(20);
                y1=y - converTodp(60);

                params_2 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_2.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_2.x = x ; // horizontal center for the image
                params_2.y = y ;
                x2=x - converTodp(60);
                y2=y - converTodp(40);
                //on left edge
                params_3 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,

                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_3.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_3.x = x ; // horizontal center for the image
                params_3.y = y;
                x3=x-converTodp(80);
                y3=y;

                params_4 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,

                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_4.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_4.x = x; // horizontal center for the image
                params_4.y = y ;
                x4=x - converTodp(60);
                y4=y + converTodp(40);

                params_5 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                params_5.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
                params_5.x = x; // horizontal center for the image
                params_5.y = y;
                x5=x + converTodp(20);
                y5=y + converTodp(60);
            }

        }



        windowManagerPanel.addView(layout_1, params_1);
        windowManagerPanel.addView(layout_2, params_2);
        windowManagerPanel.addView(layout_3, params_3);
        windowManagerPanel.addView(layout_4, params_4);
        windowManagerPanel.addView(layout_5, params_5);

        bg.setVisibility(View.VISIBLE);
        btn_animator_1.start(x1,y1,1);
        btn_animator_2.start(x2,y2,2);
        btn_animator_3.start(x3,y3,3);
        btn_animator_4.start(x4,y4,4);
        btn_animator_5.start(x5,y5,5);
    }
    public void hide_circle()
    {
        try {
            btn_animator_1.stop();
            btn_animator_2.stop();
            btn_animator_3.stop();
            btn_animator_4.stop();
            btn_animator_5.stop();
            windowManagerPanel.removeViewImmediate(layout_1);
            windowManagerPanel.removeViewImmediate(layout_2);
            windowManagerPanel.removeViewImmediate(layout_3);
            windowManagerPanel.removeViewImmediate(layout_4);
            windowManagerPanel.removeViewImmediate(layout_5);
            bg.setVisibility(View.INVISIBLE);

        }
        catch (Exception e)
        {

        }
        open=false;

    }
    public int converTodp(int somePxValue)
    {
        float density = getResources().getDisplayMetrics().density;

        int val= (int) (somePxValue / density);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,somePxValue
                , getResources().getDisplayMetrics());

        return height;
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
/*button_recorder*/