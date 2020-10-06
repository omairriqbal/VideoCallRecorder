package com.videocaht.recorder.facetime.videocall.screenrecorder;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.media.projection.MediaProjectionManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;

import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.google.android.gms.ads.AdView;
import com.google.firebase.perf.metrics.AddTrace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class main_menu  extends BaseActivity
{
    private static final int REQUEST_INVITE =2 ;
    private static final int REQUEST_SCREENSHOT = 22;
    public static boolean feedback=false;

    ViewPager viewPager;
    PagerAdapterr pagerAdapter;

     boolean add2=true;

    public static boolean buy;

    public static MediaProjectionManager mProjectionManager,mgr;
    private PopupWindow popupWindow;
    int choice1_vid=1;
    int choice2_vid=2;
    Intent requestData;
    int choice1_img=1;
    int choice2_img=2;
    LinearLayout parentLayout;
    static boolean first=false;
    Animation animShake,sss;
    AdView mAdView;
    static boolean banner_setting;
    public static int show_Ads_counter;
    public static int click_Ad_counter;
    public  Map<String, Object> Ads = new HashMap<>();
    public int nativecheck=0;
    int MULTIPLE_PERMISSIONS =111;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.action.close"))
            {
               finishAffinity();
            }
        }
    };


    boolean destroy=false;
    protected void onDestroy()
    {
        super.onDestroy();
        this.unregisterReceiver(this.mBroadcastReceiver);
        if(handler_admob!=null)
            handler_admob.removeCallbacksAndMessages(null);
        if(handler_fb!=null)
            handler_fb.removeCallbacksAndMessages(null);

        group1=false;
        group2=false;
        Ads=null;
        destroy=true;
    }
    @Override
    @AddTrace(name = "onCreateTrace", enabled = true/*Optional*/)
    public void onCreate(Bundle icicle) {
        setContentView(R.layout.drawer_layout);
        super.onCreate(icicle);

        AppRater.app_launched(this);

//        create_back_dialog();
       //Ad_data();
        Locale current = getResources().getConfiguration().locale;
        languagepair="en-"+current.getLanguage();
        if(current.getLanguage()!="en")
        get_Transalations();

        parentLayout=findViewById(R.id.parent);
        androidx.appcompat.widget.Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DisplayMetrics metrics = new DisplayMetrics();
        SharedPreferences prefs = getSharedPreferences("launches", 0);
        int count=prefs.getInt("count",0);
        if(count!=11)
        {
            checkPermission();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("count",11);
            editor.commit();
        }
        this.registerReceiver(this.mBroadcastReceiver, new IntentFilter("com.action.close"));

         initPaging();
        first=false;

    }


public void ask_permissions(View v)
{
    checkPermission();
}
    public void checkPermission()
    {
        Context context=getApplicationContext();
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M)
        {
            if(      (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                  //  ||(ContextCompat.checkSelfPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED)
                    ||(ContextCompat.checkSelfPermission(context,Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED)
                    ||(ContextCompat.checkSelfPermission(context,android.Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)
                    )
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(main_menu.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    ActivityCompat.requestPermissions(main_menu.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.VIBRATE }, MULTIPLE_PERMISSIONS);

                } else {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.VIBRATE }, MULTIPLE_PERMISSIONS);
                }
            }

        }
        else
        {
            mgr = (MediaProjectionManager) getApplicationContext().getSystemService(MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mgr.createScreenCaptureIntent(), REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted=true;
        for(int i=0;i<grantResults.length;i++)
        {
            if(grantResults[i]!=PackageManager.PERMISSION_GRANTED)
            {
                granted=false;

            }

        }
        if(granted)
        {
            ((LinearLayout)findViewById(R.id.permissions_layout)).setVisibility(View.GONE);

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
                    mgr = (MediaProjectionManager) getApplicationContext().getSystemService(MEDIA_PROJECTION_SERVICE);
                    startActivityForResult(mgr.createScreenCaptureIntent(), REQUEST_CODE);
                }
            }
            else
            {
                mgr = (MediaProjectionManager) getApplicationContext().getSystemService(MEDIA_PROJECTION_SERVICE);
                startActivityForResult(mgr.createScreenCaptureIntent(), REQUEST_CODE);
            }
        }
        else
        {

            ((LinearLayout)findViewById(R.id.permissions_layout)).setVisibility(View.VISIBLE);
        /*    Snackbar.make(findViewById(R.id.pager),"Permissions Missing",Snackbar.LENGTH_INDEFINITE).setAction("Allow", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkPermission();

                }
            }).show();*/
        }


    }

    public void go(View v)
    {

        if(requestData!=null)
        {
            if(Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                Intent i =
                        new Intent(this, FloatingViewService_notification.class)
                                .setAction("com.open.by.main")
                                .putExtra(ScreenshotService.EXTRA_RESULT_CODE, -1)
                                .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, requestData);
                startService(i);
            }
            else {
                Intent i = new Intent(this, FloatingViewService.class)
                        .setAction("com.open.by.main")
                        .putExtra(ScreenshotService.EXTRA_RESULT_CODE, -1)
                        .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, requestData);
                startService(i);
            }
        }
        else
        {
            Intent i = new Intent(this, PermissionActivity.class);
            i.setAction("com.open.by.main");
            startActivity(i);
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

/*
public void openSettings(View v)
{
    nativecheck=2;
    Intent in=new Intent(main_menu.this,Acc_Setting.class);

        startActivity(in);

}
*/

    PicFragment frag1;
    vidFragment frag;

    private void initPaging()
    {
        String videos="Videos",ss="Screenshots";
        if(translated_string.containsKey("videos"))
        {
            videos=translated_string.get("videos").toString();
            ss=translated_string.get("ss").toString();
        }
        pagerAdapter = new PagerAdapterr(getSupportFragmentManager(),new String[]{videos, ss});

        frag =new vidFragment();

        if(translated_string.containsKey("no_vid"))
            frag.setMsg(translated_string.get("no_vid").toString());

        pagerAdapter.addFragment(frag);

         frag1 =new PicFragment();

        if(translated_string.containsKey("no_pic"))
            frag1.setMsg(translated_string.get("no_pic").toString());

        pagerAdapter.addFragment(frag1);


        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(0);
      //  viewPager.setPageTransformer(false, new FadePageTransformer());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state)
            {

            }
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

               /* if(mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }*/
            }

            public void onPageSelected(int position)
            {

                // Check if this is the page you want.


            }
        });
        viewPager.setCurrentItem(0);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void onResume()
    {
        super.onResume();

            if (feedback)
            {
                showWarning(main_menu.this, getPackageName());
                feedback = !feedback;
            }



    }
    public void buyClick(View view) {

        Intent in=new Intent(main_menu.this,buy_panel.class);
        startActivity(in);

    }

    public  void showWarning(final Context mContext, final String APP_PNAME)
    {
        String yes,no,rate_us,satisfied,app_name;
        if(translated_string.containsKey("yes"))
        {
            yes=translated_string.get("yes").toString() ;
            no=translated_string.get("no").toString() ;
            rate_us=translated_string.get("rate_us").toString() ;
            app_name=translated_string.get("app_name").toString() ;

            satisfied=translated_string.get("satisfied").toString();
            satisfied+="\""+app_name+"\"?";


        }
        else
        {
            yes="Yes";
            no="No";
            rate_us="Rate us";
            app_name=getString(R.string.app_name);
            satisfied="Are you satisfied using \"" + app_name+ "\"\n";
        }
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        Locale.getDefault().getDisplayLanguage();
        builder.setTitle(rate_us);

        builder.setMessage(satisfied)
                .setPositiveButton(yes,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("rated", true);
                                editor.commit();
                                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                                dialog.dismiss();

                            }
                        })
                .setNegativeButton(no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                // showPopup(mContext);

                            }
                        });

        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }



    public void share()
    {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        String appName=getResources().getString(R.string.app_name);
        share.putExtra(Intent.EXTRA_SUBJECT, appName);
        String sAux = "\nLet me recommend you this application\n"+appName+"\n";
        sAux = sAux + getPackageName()+"\n\n";
        share.putExtra(Intent.EXTRA_TEXT,sAux );

        try
        {
            startActivity(Intent.createChooser(share, "Share Link"));

        }catch (Exception e)
        {

        }

    }


    public void settings()
    {

        View view = initPopUp(R.layout.filetypes_vid, 0.9f, 0.8f, 0.8f, 0.7f);
        popupWindow.showAtLocation(parentLayout, Gravity.CENTER,0, 0);


        SharedPreferences settings1= getSharedPreferences("MY_PREF",0);
        int ch=settings1.getInt("video formate",1);
        if(ch==1)
        {
            RadioButton r1=(RadioButton) view.findViewById(R.id.format_mp4);
            r1.setChecked(true);
        }
        if(ch==2)
        {
            RadioButton r1=(RadioButton) view.findViewById(R.id.format_3gp);
            r1.setChecked(true);
        }
        int ch_img=settings1.getInt("img formate",1);

        if(ch_img==1)
        {
            RadioButton r1=(RadioButton) view.findViewById(R.id.format_png);
            r1.setChecked(true);
        }
        if(ch_img==2)
        {
            RadioButton r1=(RadioButton) view.findViewById(R.id.format_jpg);
            r1.setChecked(true);
        }
        RadioGroup rr=(RadioGroup) view.findViewById(R.id.video);
        rr.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup buttonView, int ans) {
                // update your model (or other business logic) based on isChecked
                switch(ans)
                {
                    case R.id.format_mp4:
                        //   Toast.makeText(getApplicationContext(),""+choice1,Toast.LENGTH_SHORT).show();
                        SharedPreferences settings= getSharedPreferences("MY_PREF",0);
                        SharedPreferences.Editor editor= settings.edit();
                        editor.putInt("video formate",choice1_vid);
                        editor.commit();
                        break;

                    case R.id.format_3gp:
                        //  Toast.makeText(getApplicationContext(),""+choice2,Toast.LENGTH_SHORT).show();
                        SharedPreferences settings1= getSharedPreferences("MY_PREF",0);
                        SharedPreferences.Editor editor1= settings1.edit();
                        editor1.putInt("video formate",choice2_vid);
                        editor1.commit();
                        break;

                }



            }
        });

        RadioGroup img=(RadioGroup) view.findViewById(R.id.image);
        img.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup buttonView, int ans) {
                // update your model (or other business logic) based on isChecked
                switch(ans)
                {
                    case R.id.format_png:
                        //   Toast.makeText(getApplicationContext(),""+choice1,Toast.LENGTH_SHORT).show();
                        SharedPreferences settings= getSharedPreferences("MY_PREF",0);
                        SharedPreferences.Editor editor= settings.edit();
                        editor.putInt("img formate",choice1_img);
                        editor.commit();
                        break;

                    case R.id.format_jpg:
                        //  Toast.makeText(getApplicationContext(),""+choice2,Toast.LENGTH_SHORT).show();
                        SharedPreferences settings1= getSharedPreferences("MY_PREF",0);
                        SharedPreferences.Editor editor1= settings1.edit();
                        editor1.putInt("img formate",choice2_img);
                        editor1.commit();
                        break;

                }



            }
        });


    }
    private View initPopUp(int resourse,
                           float landPercentW,
                           float landPercentH,
                           float portraitPercentW,
                           float portraitPercentH)
    {

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(resourse, null);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            popupWindow = new PopupWindow(
                    view,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
        } else {
            popupWindow = new PopupWindow(
                    view,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.Animation);

        return view;
    }
    private static final int REQUEST_CODE = 1000;
    private static final int WIDGET_CODE = 2000;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 11)) {
            if (requestCode == RESULT_OK) {
                mgr = (MediaProjectionManager) getApplicationContext().getSystemService(MEDIA_PROJECTION_SERVICE);
                startActivityForResult(mgr.createScreenCaptureIntent(), REQUEST_CODE);
            }
            return;
        }
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                Intent i =
                        new Intent(this, FloatingViewService_notification.class)
                                .setAction("com.open.by.main")
                                .putExtra(ScreenshotService.EXTRA_RESULT_CODE, -1)
                                .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, requestData);
                startService(i);
            } else {
                requestData = data;
                Intent i =
                        new Intent(this, FloatingViewService.class)
                                .putExtra(ScreenshotService.EXTRA_RESULT_CODE, resultCode)
                                .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, data);
                startService(i);
            }
            return;

        } else if (requestCode == REQUEST_SCREENSHOT) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                Intent i =
                        new Intent(this, FloatingViewService_notification.class)
                                .setAction("com.open.by.main")
                                .putExtra(ScreenshotService.EXTRA_RESULT_CODE, -1)
                                .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, requestData);
                startService(i);
            } else {
                if (resultCode == RESULT_OK) {
                    Intent i =
                            new Intent(this, FloatingViewService.class)
                                    .putExtra(ScreenshotService.EXTRA_RESULT_CODE, resultCode)
                                    .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, data);

                    startService(i);

                }
            }
        }


    }

    public void goto_SS(View v)
    {
        if(requestData!=null)
        {
            Intent i = new Intent(this, FloatingViewService.class)
                    .setAction("com.open.by.main")
                    .putExtra(ScreenshotService.EXTRA_RESULT_CODE, -1)
                    .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, requestData);
            startService(i);
        }
        else
        {
            Intent i = new Intent(this, PermissionActivity.class);
            i.setAction("com.open.by.main");
            startActivity(i);
        }
    /*    mgr = (MediaProjectionManager) getApplicationContext().getSystemService(MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mgr.createScreenCaptureIntent(), REQUEST_SCREENSHOT);*/
    }

    Handler handler_admob,handler_fb;


    Dialog dialog;


    public  Boolean group1=false,group2=false;

    String languagepair;
    public void get_Transalations()
    {


        array_list.add("Videos");
        array_list.add("Screenshots");
        array_list.add("Are you satisfied using");
        array_list.add("Yes");
        array_list.add("No");

        array_list.add("Do you want to exit?");
        array_list.add(getString(R.string.app_name));
        array_list.add("Rate us");

        array_list.add("No videos");
        array_list.add("No images");


        SharedPreferences pref = getApplicationContext().getSharedPreferences("translation", 0);
        if(pref.getBoolean(languagepair,false))
        {

            if(pref.getBoolean("main",false)) {
                outputString = new ArrayList<>();
                for (int i = -0; i < array_list.size(); i++) {
                    outputString.add(pref.getString(array_list.get(i), array_list.get(i)));
                }

                setStrings();
            }
            else
            {
                if(isNetworkOnline())
                    Translate(getString(R.string.app_name),languagepair);
            }
        }
        else
        {
            if(isNetworkOnline())
                Translate(getString(R.string.app_name),languagepair);
        }


    }
    public void setStrings()
    {

        try
        {
translated_string.put("videos",outputString.get(0));
            translated_string.put("ss",outputString.get(1));

            translated_string.put("satisfied",outputString.get(2));
            translated_string.put("yes",outputString.get(3));
            translated_string.put("no",outputString.get(4));
            translated_string.put("exit",outputString.get(5));
            translated_string.put("app_name",outputString.get(6));
            translated_string.put("rate_us",outputString.get(7));

            translated_string.put("no_vid",outputString.get(8));
            translated_string.put("no_pic",outputString.get(9));


           if(frag!=null)
            frag.setMsg(translated_string.get("no_vid").toString());

            if(frag1!=null)
                frag1.setMsg(translated_string.get("no_pic").toString());

        }
        catch (Exception e)
        {
String msg=e.getMessage();
        }
    }
    ArrayList<String> array_list=new ArrayList<>();
    ArrayList<String> outputString=new ArrayList<>();
    public  Map<String, Object> translated_string = new HashMap<>();

    public class TranslatorBackgroundTask extends AsyncTask<String, Void, String>
    {
        //Declare Context
        Context ctx;
        String resultString;
        //Set Context
        TranslatorBackgroundTask(Context ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params)
        {
            //String variables
            for(int i=0;i<array_list.size();i++)
            {
                String textToBeTranslated =array_list.get(i);
                String languagePair = params[1];
                if(textToBeTranslated.contains(" "))
                {
                    textToBeTranslated=textToBeTranslated.replaceAll(" ","%20");
                }


                String jsonString;

                try {
                    //Set up the translation call URL
                    String yandexKey = "trnsl.1.1.20190519T110025Z.51bea2e4ba0da9c7.d80d14134c67c4247640cda23ec7ba0e9a2bb7c3";
                    String yandexUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + yandexKey
                            + "&text=" + textToBeTranslated + "&lang=" + languagePair;
                    URL yandexTranslateURL = new URL(yandexUrl);

                    //Set Http Conncection, Input Stream, and Buffered Reader
                    HttpURLConnection httpJsonConnection = (HttpURLConnection) yandexTranslateURL.openConnection();
                    InputStream inputStream = httpJsonConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    //Set string builder and insert retrieved JSON result into it
                    StringBuilder jsonStringBuilder = new StringBuilder();
                    while ((jsonString = bufferedReader.readLine()) != null) {
                        jsonStringBuilder.append(jsonString + "\n");
                    }

                    //Close and disconnect
                    bufferedReader.close();
                    inputStream.close();
                    httpJsonConnection.disconnect();

                    //Making result human readable
                    resultString = jsonStringBuilder.toString().trim();
                    //Getting the characters between [ and ]
                    resultString = resultString.substring(resultString.indexOf('[') + 1);
                    resultString = resultString.substring(0, resultString.indexOf("]"));
                    //Getting the characters between " and "
                    resultString = resultString.substring(resultString.indexOf("\"") + 1);
                    resultString = resultString.substring(0, resultString.indexOf("\""));

                    //  Log.d("Translation Result:", resultString);
                    outputString.add(resultString);
                    //   return jsonStringBuilder.toString().trim();

                } catch (MalformedURLException e)
                {
                    outputString.add(textToBeTranslated);
                    e.printStackTrace();
                } catch (IOException e)
                {
                    outputString.add(textToBeTranslated);
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String result)
        {
            save_translations_to_prefrences();
            setStrings();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
    public void save_translations_to_prefrences()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("translation", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(languagepair,true);
        editor.putBoolean("main",true);
        for(int i=0;i<outputString.size();i++)
        {
            editor.putString(array_list.get(i),outputString.get(i));
        }

        editor.commit();
    }
    public void Translate(String textToBeTranslated, String languagePair)
    {
        TranslatorBackgroundTask translatorBackgroundTask= new TranslatorBackgroundTask(getApplicationContext());
        translatorBackgroundTask.execute(textToBeTranslated,languagePair).toString();

    }
    public boolean isNetworkOnline()
    {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()== NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()== NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;

    }


}
