package com.videocaht.recorder.facetime.videocall.screenrecorder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;


import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class ScreenshotService extends Service
{
    private static final int NOTIFY_ID=9906;
    static final String EXTRA_RESULT_CODE="resultCode";
    static final String EXTRA_RESULT_INTENT="resultIntent";
    static final String ACTION_RECORD=
            BuildConfig.APPLICATION_ID+".RECORD";
    static final String ACTION_SHUTDOWN=
            BuildConfig.APPLICATION_ID+".SHUTDOWN";
    static final int VIRT_DISPLAY_FLAGS=
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY |
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private MediaProjection projection;
    private VirtualDisplay vdisplay;
    final private HandlerThread handlerThread=
            new HandlerThread(getClass().getSimpleName(),
                    android.os.Process.THREAD_PRIORITY_BACKGROUND);
    private Handler handler;
    private MediaProjectionManager mgr;
    private WindowManager wmgr;
    private ImageTransmogrifier it;
    private int resultCode;
    private Intent resultData;
    private static WindowManager windowManager;
    private static WindowManager.LayoutParams myParams;
    private static int screen_width;
    private ImageView smallCircle; //this is the image onscreen
    private ImageView close;
    private LinearLayout layout;
    private WindowManager windowManagerClose;
    private static int screen_height;
    private static  Context context;
    MoveAnimator animator=new MoveAnimator();
    private GestureDetector gestureDetector;
    private Animation bottom_down,bottom_up;
static LinearLayout overlay;

    @Override
    public void onCreate()
    {
        super.onCreate();
        context=getApplicationContext();
        bottom_down = AnimationUtils.loadAnimation(getApplication(), R.anim.bottom_down);
        bottom_up = AnimationUtils.loadAnimation(getApplication(),R.anim.bottom_up);
        mgr=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
        wmgr=(WindowManager)getSystemService(WINDOW_SERVICE);

        handlerThread.start();
        handler=new Handler(handlerThread.getLooper());
        initializeView();
        getScreenSize();
    //    showFloat();

    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId)
    {
        if (i.getAction()==null)
        {
            resultCode=i.getIntExtra(EXTRA_RESULT_CODE, 1337);
            resultData=i.getParcelableExtra(EXTRA_RESULT_INTENT);
            try {
                startCapture();
            }catch (Exception e)
            {

            }
            //foregroundify();
        }
        else if (ACTION_RECORD.equals(i.getAction()))
        {
            if (resultData!=null)
            {
                startCapture();
            }
            else {
                Intent ui=
                        new Intent(this, main_menu.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(ui);
            }
        }

        return(START_NOT_STICKY);
    }
    public static final String ACTION_PERMISSION=  "myAction.Activity.Permissions";
    @Override
    public void onDestroy()
    {

        super.onDestroy();

        if (overlay != null)
        {
//            overlay.setVisibility(View.GONE);
            try
            {
                windowManager.removeView(overlay);
            }catch (Exception e)
            {

            }

        }

        stopSelf();
        Intent i;

        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
             i = new Intent(this, FloatingViewService_notification.class)
                    .setAction(ACTION_PERMISSION)
                    .putExtra(ScreenshotService.EXTRA_RESULT_CODE, -1)
                    .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, resultData);
        }
        else {
             i = new Intent(this, FloatingViewService.class)
                    .setAction(ACTION_PERMISSION)
                    .putExtra(ScreenshotService.EXTRA_RESULT_CODE, -1)
                    .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, resultData);
        }



        try {
            startService(i);
        }
        catch (Exception e)

        {

        }


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new IllegalStateException("Binding not supported. Go away.");
    }

    WindowManager getWindowManager() {
        return(wmgr);
    }

    Handler getHandler() {
        return(handler);
    }
String path;
    void processImage(final byte[] png, Bitmap img) {
        new Thread() {
            @Override
            public void run()
            {

                SharedPreferences settings1 = getSharedPreferences("shared preferences", MODE_PRIVATE);

                String path =  settings1.getString("storage path","storage/emulated/0/");
                String fullPath = path + "/Video Call Recorder ScreenShots";

                File ff=new File(fullPath);
                if(!(ff.exists()))
                {
                    ff.mkdir();
                }
                Calendar cc=Calendar.getInstance();

                SharedPreferences settings= getSharedPreferences("MY_PREF",0);
                int ch=settings.getInt("img formate",1);
                String ext;
                if(ch==1)
                    ext=".png";
                else
                    ext=".jpg";
                String name="SR_"+cc.getTimeInMillis()+ext;
                File output=new File(fullPath,
                        name);

                try {
                    FileOutputStream fos=new FileOutputStream(output);
                    fos.write(png);
                    fos.flush();
                    fos.getFD().sync();
                    fos.close();
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{output.toString()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> uri=" + uri);
                                }
                            });
                    path=output.getAbsolutePath();
                    /*MediaScannerConnection.scanFile(ScreenshotService.this,
                            new String[] {output.getAbsolutePath()},
                            new String[] {"image/png"},
                            null);*/

                }
                catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Exception writing out screenshot", e);
                }
            }
        }.start();


        AudioPlayer player=new AudioPlayer();
        player.play(getApplicationContext(),R.raw.audio);

        show_Overlay(img);
        it.close();

    }

    private void stopCapture()
    {

        if (projection!=null) {
            projection.stop();
            vdisplay.release();
            projection=null;
            it.close();
        }
        //show_Overlay();
    }

    private void startCapture()
    {
      smallCircle.setVisibility(View.GONE);
       layout.setVisibility(View.GONE);
if(mgr!=null)
       projection=mgr.getMediaProjection(resultCode, resultData);
else
{
    mgr=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
    projection=mgr.getMediaProjection(resultCode, resultData);
}
        it=new ImageTransmogrifier(this);

        MediaProjection.Callback cb=new MediaProjection.Callback()
        {
            @Override
            public void onStop() {
                vdisplay.release();
                stopCapture();
            }
        };

        vdisplay=projection.createVirtualDisplay("andshooter",
                it.getWidth(), it.getHeight(),
                getResources().getDisplayMetrics().densityDpi,
                VIRT_DISPLAY_FLAGS, it.getSurface(), null, handler);
        projection.registerCallback(cb, handler);

    }

    static ImageView colored;
    public  void show_Overlay(Bitmap bbb)
    {
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_TOAST;
        }
        else {
            LAYOUT_FLAG =  WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        myParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        myParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
        overlay=new LinearLayout(context);
         colored=new ImageView(context);
         colored.setImageBitmap(bbb);
          //colored.setColorFilter(context.getResources().getColor(R.color.bg));
         colored.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
       // colored.setLayoutParams(new LinearLayout.LayoutParams(500,500));
        LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        pp.height=screen_height;
        pp.width=screen_width;
        overlay.addView(colored,pp);
        windowManager.addView(overlay,myParams);
        colored.animate()
                .scaleX(0f)
                .scaleY(0f)
                .translationX(-screen_width/2)
                .translationY(150+screen_height/2)
                .alpha(0.3f)
                .setDuration(600)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        super.onAnimationEnd(animation);
                        stopCapture();
                        stopSelf();

                    }
                });

    }

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener
    {

        @Override
        public boolean onSingleTapUp(MotionEvent event)
        {
            // Toast.makeText(getApplicationContext(), "single tap", Toast.LENGTH_SHORT).show();
            //  expandedView.setVisibility(View.VISIBLE);
            //createRecordNotification();
            if (resultData!=null) {
                startCapture();
            }
            else {
                Intent ui=
                        new Intent(getApplicationContext(), main_menu.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(ui);
            }
            return true;
        }
    }
    private void Visibility() {
        if (windowManager != null) {
            windowManager.removeViewImmediate(smallCircle);
            windowManagerClose.removeViewImmediate(layout);
        }
    }

    private void initializeView()
    {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManagerClose = (WindowManager) getSystemService(WINDOW_SERVICE);
        smallCircle = new ImageView(this);
        smallCircle.setImageResource(R.mipmap.ic_add_img);
        smallCircle.setMaxWidth(25);
        smallCircle.setMaxHeight(25);
        close = new ImageView(this);
        close.setImageResource(R.mipmap.ic_close);
        context = ScreenshotService.this;
        layout = new LinearLayout(this);
        layout.addView(close);

    }

    private void getScreenSize() {
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screen_width = size.x;
        screen_height = size.y;

    }


    private static class MathUtil {
        public static boolean betweenExclusive(int x, int min, int max) {
            return x > min && x < max;
        }
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
            if (smallCircle != null && smallCircle.getParent() != null) {
                float progress = Math.min(1, (System.currentTimeMillis() - startingTime) / 400f);

                float deltaX = (destinationX - myParams.x) * progress;
                float deltaY = (destinationY - myParams.y) * progress;
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

    protected void move(float deltaX, float deltaY)

    {
        myParams.x += deltaX;
        myParams.y += deltaY;
        windowManager.updateViewLayout(overlay, myParams);
    }


    public class AudioPlayer {

        private MediaPlayer mMediaPlayer;

        public void stop() {
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }

        public void play(Context c, int rid)
        {
            stop();

            mMediaPlayer = MediaPlayer.create(c, rid);
            mMediaPlayer.setVolume(200,500);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stop();
                }
            });

            mMediaPlayer.start();
        }

    }



}