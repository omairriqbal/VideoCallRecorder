package com.recorder.screen.recordingapp.editor;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import static com.recorder.screen.recordingapp.editor.main_menu.show_Ads_counter;


public class Ad_Acc extends AppCompatActivity
{
    public  Map<String, Object> Ads = new HashMap<>();
    final private UnityAdsListener unityAdsListener = new UnityAdsListener();
    private RewardedVideoAd mRewardedVideoAd;
    private Dialog dialog;
    private InterstitialAd mInterstitialAd_main;
    private NativeAd native_main;
    private Handler handler_fb;
    Runnable admob,fb;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_acc);

        show_Dialog();
        init_Ads();
        init_firestore();




    }
    public void init_Ads()
    {
        mInterstitialAd_main = new InterstitialAd(Ad_Acc.this);
        native_main = new NativeAd(Ad_Acc.this, "");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        save_CTA();
    }

    public void show_Dialog()
    {
        dialog = new Dialog(Ad_Acc.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog .setCancelable(false);
        dialog .setContentView(R.layout.ad_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        (dialog.findViewById(R.id.close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
                finish();

            }
        });
        (dialog.findViewById(R.id.watch)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();

                new Handler().postDelayed(() -> show_ad(), 2000);

            }
        });



    }
    public void resettCount()
    {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("recorder", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("count",0);
        editor.commit();
    }
    @Override
    public void onBackPressed()
    {
            if (hide_native())
                    return;
    }

    public void show_ad()
    {
        if(UnityAds.isReady())
        {
            UnityAds.show(Ad_Acc.this,"rewardedVideo" );

        }
        else if(native_main.isAdLoaded())
        {
           show_native(native_main);
        }
        else if(mInterstitialAd_main.isLoaded())
        {
            mInterstitialAd_main.show();
            mInterstitialAd_main.setAdListener(new AdListener()
            {
                @Override
                public void onAdClosed()
                {
                    super.onAdClosed();
                   start_next();
                }
            });
        }
        else
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
    private class UnityAdsListener implements IUnityAdsListener
    {
        @Override
        public void onUnityAdsReady(String s)
        {
            //Called when Unity Ads has a video available to show
            //  Toast.makeText(MainActivity.this, "unity ad ready", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onUnityAdsStart(String s)
        {
            //Called when a video begins playing
        }

        @Override
        public void onUnityAdsFinish(String s, UnityAds.FinishState finishState)
        {
            // Toast.makeText(MainActivity.this, "unity ad finish", Toast.LENGTH_SHORT).show();

            //Called when a video vinishes playing
            start_next();
        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s)
        {
            //Called when the Unity Ads detects an error
            // Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();

        }
    }

    public void init_firestore()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        read_firestore(db);
    }


    public void read_firestore(FirebaseFirestore db)
    {
        DocumentReference docRef = db.collection("Ads").document("ad_acc");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                                           {
                                               @Override
                                               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                   if (task.isSuccessful())
                                                   {
                                                       DocumentSnapshot document = task.getResult();
                                                       if (document.exists())
                                                       {
                                                           Ads=document.getData();


                                                           UnityAds.initialize(Ad_Acc.this, Ads.get("unity_id").toString(), unityAdsListener);

                                                           mInterstitialAd_main = new InterstitialAd(Ad_Acc.this);
                                                           mInterstitialAd_main.setAdUnitId(Ads.get("admob_interstitial_ad_acc").toString() );
                                                           mInterstitialAd_main.loadAd(new AdRequest.Builder().build());

                                                           native_main = new NativeAd(Ad_Acc.this, Ads.get("fb_native_ad_acc").toString());
                                                           native_main.loadAd();




                                                       }


                                                   }
                                               }

                                           }

        );

    }

    public void show_native(NativeAd nativeAd)
    {


        if(nativeAd!=null)
            if(nativeAd.isAdLoaded())
            {
                native_main.setAdListener(new NativeAdListener() {
                    @Override
                    public void onMediaDownloaded(Ad ad) {

                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {

                    }

                    @Override
                    public void onAdLoaded(Ad ad) {

                    }

                    @Override
                    public void onAdClicked(Ad ad) {
                        main_menu.click_Ad_counter++;
                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {

                    }
                });
                inflateAd(nativeAd);
                show_Ads_counter++;
            }

    }

    public boolean hide_native()
    {
        if (((RelativeLayout) findViewById(R.id.native_container)).getVisibility() == View.VISIBLE)
        {
            ((RelativeLayout) findViewById(R.id.native_container)).setVisibility(View.GONE);

            native_main = new NativeAd(Ad_Acc.this,Ads.get("fb_native_ad_acc").toString());

            try
            {

                handler_fb = new Handler();
                fb = new Runnable() {
                    @Override
                    public void run() {
                        if (!native_main.isAdLoaded()){
                            native_main.loadAd();
                        }
                    }
                };

                handler_fb.postDelayed(fb, 50000);

            }catch (Exception e)
            {

            }

            native_main.destroy();
            return true;
        }
        else
            return false;


    }
    private void inflateAd(NativeAd nativeAd)
    {

        nativeAd.unregisterView();

        // Add the Ad view into the ad container.
        NativeAdLayout nativeAdLayout = findViewById(R.id.native_ad_container);
        LayoutInflater inflater = LayoutInflater.from(Ad_Acc.this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        RelativeLayout adView = (RelativeLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(Ad_Acc.this, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);


        // Create native UI using the ad metadata.
        AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(adView,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);





        int percent = (int)((main_menu.click_Ad_counter * 100.0f) / show_Ads_counter);

        if(show_Ads_counter>=100)
        {

            if(percent<=9)
            {
                //  nativeAdCallToAction.setVisibility(View.VISIBLE);
                nativeAdCallToAction.setEnabled(true);
                nativeAdTitle.setEnabled(true);

            }
            else
            {
                nativeAdCallToAction.setEnabled(false);
                nativeAdTitle.setEnabled(false);
            }
        }
        else
        {
            nativeAdCallToAction.setEnabled(false);
            nativeAdTitle.setEnabled(false);
        }



        ((LinearLayout)adView.findViewById(R.id.do_nothing)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        ((ImageView)adView.findViewById(R.id.close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide_native();
            }
        });

        ((RelativeLayout) findViewById(R.id.native_container)).setVisibility(View.VISIBLE);
    }
    public void get_CTA_from_db()
    {

        SharedPreferences prefs = getSharedPreferences("CTA", 0);

        show_Ads_counter=prefs.getInt("num_of_ads_shown",0);
        main_menu.click_Ad_counter=prefs.getInt("num_of_ads_clicked",0);


    }

    public void save_CTA()
    {
        SharedPreferences prefs = getSharedPreferences("CTA", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("num_of_ads_shown", show_Ads_counter);
        editor.putInt("num_of_ads_clicked",main_menu.click_Ad_counter);
        editor.apply();
    }

}
