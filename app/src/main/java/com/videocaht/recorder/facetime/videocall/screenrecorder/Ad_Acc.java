package com.videocaht.recorder.facetime.videocall.screenrecorder;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.videocaht.recorder.facetime.videocall.screenrecorder.main_menu.show_Ads_counter;


public class Ad_Acc extends AppCompatActivity
{

    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_acc);


        mInterstitialAd = DataProvider.getInstance().get_interstitial();
        if ((mInterstitialAd.isLoaded() && DataProvider.show_ad)) {

            new Handler().postDelayed(() ->
                    show_ad(), 800);
        } else {

            {

                if(Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1)
                {
                    final String ACTION_PAUSE_PLAY =  "myAction.Activity.playpause.skip";
                    Intent i = new Intent(Ad_Acc.this, FloatingViewService_notification.class)
                            .setAction(ACTION_PAUSE_PLAY);
                    startService(i);
                }
                else {
                    final String ACTION_PAUSE_PLAY = "myAction.Activity.playpause.skip";
                    Intent i = new Intent(Ad_Acc.this, FloatingViewService.class)
                            .setAction(ACTION_PAUSE_PLAY);
                    startService(i);
                }
                finish();
            }
        }
        DataProvider.toggle_ad_check();




    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        save_CTA();
    }

    public void resettCount()
    {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("recorder", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("count",0);
        editor.commit();
    }


    public void show_ad()
    {

            mInterstitialAd.show();
        mInterstitialAd.setAdListener(new AdListener()
            {
                @Override
                public void onAdClosed()
                {
                    super.onAdClosed();
                   start_next();
                    DataProvider.getInstance().reload_admob();
                }
            });
        }

    public void start_next()
    {
        resettCount();
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1)
        {
            final String ACTION_PAUSE_PLAY =  "myAction.Activity.playpause";
            Intent i = new Intent(Ad_Acc.this, FloatingViewService_notification.class)
                    .setAction(ACTION_PAUSE_PLAY);
            startService(i);
        }
        else {
            final String ACTION_PAUSE_PLAY = "myAction.Activity.playpause";
            Intent i = new Intent(Ad_Acc.this, FloatingViewService.class)
                    .setAction(ACTION_PAUSE_PLAY);
            startService(i);
        }
        finish();
    }

    public void save_CTA()
    {
        SharedPreferences prefs = getSharedPreferences("CTA", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("num_of_ads_shown", show_Ads_counter);
        editor.putInt("num_of_ads_clicked", main_menu.click_Ad_counter);
        editor.apply();
    }

}
