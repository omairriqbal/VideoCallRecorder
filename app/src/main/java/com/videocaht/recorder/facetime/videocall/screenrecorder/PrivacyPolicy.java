package com.videocaht.recorder.facetime.videocall.screenrecorder;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.List;

public class PrivacyPolicy extends AppCompatActivity {
    private Toolbar toolbar;
    private Handler handler_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        TextView textView = findViewById(R.id.textview);

       /* if (HelperClass.isNetworkConnected(this)) {
            loadAds();
        } else {

            start_handler_network_connection();

        }*/

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Privacy Policy");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));

        textView.setText(Html.fromHtml(" <b> Privacy Policy </b><br><br>" +
        "\n" +
                "Global Apps built the Video Call Screen Recorder app as an Ad Supported app. This SERVICE is provided by Global Apps at no cost and is intended for use as is.\n" +
                "\n" +
                "This page is used to inform visitors regarding our policies with the collection, use, and disclosure of Personal Information if anyone decided to use our Service.\n" +
                "\n" +
                "If you choose to use our Service, then you agree to the collection and use of information in relation to this policy. The Personal Information that we collect is used for providing and improving the Service. We will not use or share your information with anyone except as described in this Privacy Policy.\n" +
                "\n" +
                "The terms used in this Privacy Policy have the same meanings as in our Terms and Conditions, which is accessible at Video Call Screen Recorder unless otherwise defined in this Privacy Policy.\n" +
                " <br> <b>Information Collection and Use</b><br>" +
                "For a better experience, while using our Service, we may require you to provide us with certain personally identifiable information. The information that we request will be retained by us and used as described in this privacy policy.\n" +
                "The app does use third party services that may collect information used to identify you.\n" +
                "Link to privacy policy of third party service providers used by the app\n" +
                "Google Play Services\n" +
                "AdMob\n" +
                "Google Analytics for Firebase\n" +
                "Firebase Crashlytics\n" +
                "Fabric\n" +
                "One Signal\n" +
                "<br> <b>Log Data</b>\n<br>" +
                "We want to inform you that whenever you use our Service, in a case of an error in the app we collect data and information (through third party products) on your phone called Log Data. This Log Data may include information such as your device Internet Protocol (“IP”) address, device name, operating system version, the configuration of the app when utilizing our Service, the time and date of your use of the Service, and other statistics.\n" +
                "<br> <b>Cookies</b>\n<br>" +
                "Cookies are files with a small amount of data that are commonly used as anonymous unique identifiers. These are sent to your browser from the websites that you visit and are stored on your device's internal memory.\n" +
                "This Service does not use these “cookies” explicitly. However, the app may use third party code and libraries that use “cookies” to collect information and improve their services. You have the option to either accept or refuse these cookies and know when a cookie is being sent to your device. If you choose to refuse our cookies, you may not be able to use some portions of this Service.\n" +
                "  <br> <b>Service Providers</b><br>\n" +
                "We may employ third-party companies and individuals due to the following reasons:\n" +
                "⦁\tTo facilitate our Service;\n" +
                "⦁\tTo provide the Service on our behalf;\n" +
                "⦁\tTo perform Service-related services; or\n" +
                "⦁\tTo assist us in analyzing how our Service is used.\n" +
                "We want to inform users of this Service that these third parties have access to your Personal Information. The reason is to perform the tasks assigned to them on our behalf. However, they are obligated not to disclose or use the information for any other purpose.\n" +
                "   <br> <b> Security</b><br>" +
                "We value your trust in providing us your Personal Information, thus we are striving to use commercially acceptable means of protecting it. But remember that no method of transmission over the internet, or method of electronic storage is 100% secure and reliable, and we cannot guarantee its absolute security.\n" +
                "  <br>  <b>Links to Other Sites</b><br>" +
                "This Service may contain links to other sites. If you click on a third-party link, you will be directed to that site. Note that these external sites are not operated by us. Therefore, we strongly advise you to review the Privacy Policy of these websites. We have no control over and assume no responsibility for the content, privacy policies, or practices of any third-party sites or services.\n" +
                "<br><b>Children’s Privacy</b><br>" +
                "These Services do not address anyone under the age of 13. We do not knowingly collect personally identifiable information from children under 13. In the case we discover that a child under 13 has provided us with personal information, we immediately delete this from our servers. If you are a parent or guardian and you are aware that your child has provided us with personal information, please contact us so that we will be able to do necessary actions.\n" +
                "  <br>  <b>Changes to This Privacy Policy</b><br>\n" +
                "We may update our Privacy Policy from time to time. Thus, you are advised to review this page periodically for any changes. We will notify you of any changes by posting the new Privacy Policy on this page.\n" +
                "This policy is effective as of 2020-09-17\n" +
                "   <br> <b>Contact Us</b>\n<br>" +
                "If you have any questions or suggestions about our Privacy Policy, do not hesitate to contact us at kitoappsfeedback@gmail.com."));
    }

  /*  public void start_handler_network_connection() {

        handler_1 = new Handler();
        handler_1.postDelayed(new Runnable() {
            @Override
            public void run() {


                if (HelperClass.isNetworkConnected(PrivacyPolicy.this)) {

                    if (handler_1 != null)
                        handler_1.removeCallbacksAndMessages(null);

                    if (DataProvider.getInstance().read_data_remainig && !DataProvider.getInstance().load_request_send) {
                        DataProvider.getInstance().on_complete();
                        handler_1.postDelayed(this, 5000);
                    }
                    if (DataProvider.getInstance().get_Ads() != null && DataProvider.getInstance().get_Ads().size() > 0) {
                        loadAds();

                    }
                } else if (!DataProvider.getInstance().load_request_send)
                    handler_1.postDelayed(this, 5000);


                //Do something after 100ms
            }
        }, 4000);

    }


    private void loadAds() {
        if (!DataProvider.getInstance().buy) {
            UnifiedNativeAd native_admob = DataProvider.getInstance().get_native_admob();
            if (native_admob != null) {
                LinearLayout frameLayout =
                        (LinearLayout) findViewById(R.id.adLayout);
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                        .inflate(R.layout.ads_layout, null);
                populateUnifiedNativeAdView(native_admob, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);

                DataProvider.getInstance().load_native_admob();
            }
        }
    }*/

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {

        VideoController vc = nativeAd.getVideoController();

        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {

                super.onVideoEnd();
            }
        });

        com.google.android.gms.ads.formats.MediaView mediaView = (com.google.android.gms.ads.formats.MediaView) adView.findViewById(R.id.ad_media);
        ImageView mainImageView = (ImageView) adView.findViewById(R.id.ad_image);

        if (vc.hasVideoContent()) {
            mediaView.setVisibility(View.VISIBLE);
            adView.setMediaView(mediaView);
            mainImageView.setVisibility(View.GONE);

        } else {
            mainImageView.setVisibility(View.VISIBLE);
            adView.setImageView(mainImageView);
            mediaView.setVisibility(View.GONE);

            try {
                List<NativeAd.Image> images = nativeAd.getImages();
                if (images.size() > 0)
                    mainImageView.setImageDrawable(images.get(0).getDrawable());

            } catch (Exception e) {

            }
        }

        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
   /*     adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));*/

        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        /*if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }*/

       /* if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }*/

        adView.setNativeAd(nativeAd);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // one inherited from android.support.v4.app.FragmentActivity

        return false;
    }
}