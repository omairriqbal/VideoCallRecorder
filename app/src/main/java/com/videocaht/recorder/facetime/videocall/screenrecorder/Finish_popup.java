package com.videocaht.recorder.facetime.videocall.screenrecorder;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.database.Cursor;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jolta on 2/14/2018.
 */

public class Finish_popup extends AppCompatActivity {
    String vidName,url;
    String pass;
    private ContentResolver contentResolver;
    private Cursor cursor;
    VideoModel model,finall;
    private Dialog dialog;
    private int selected;
    EditText editText;
    ArrayList<VideoModel> result=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.finish_window);

        vidName=getIntent().getStringExtra("name");
        url=getIntent().getStringExtra("url");
       /* if(vidName!=null)
        {
            vidName=vidName.replace("/","");
            editText =  (EditText)findViewById(R.id.title);
            editText.setText(vidName);


        }*/
        finall= getFromSDCard();
        loadAds();

    }


    private void loadAds() {
        if (!DataProvider.getInstance().buy) {
            UnifiedNativeAd native_admob = DataProvider.getInstance().get_native_admob();
            if (native_admob != null) {
                LinearLayout frameLayout =
                        (LinearLayout) findViewById(R.id.adLayout);
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                        .inflate(R.layout.big_native, null);
                populateUnifiedNativeAdView(native_admob, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);

                DataProvider.getInstance().load_native_admob();
            }

        }
    }

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

    public static void copy(File src, File dst) throws IOException
    {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }
    public void close(View view)
    {
        finish();
    }

    public VideoModel getFromSDCard()
    {
        ArrayList<VideoModel> models=new ArrayList<>();

        SharedPreferences settings1= this.getSharedPreferences("shared preferences",MODE_PRIVATE);

        String path =  settings1.getString("storage path","storage/emulated/0/");
        String fullPath = path + "/Video Call Recorder";

        File dir = new File(fullPath);

        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0)
        {
            for (File file : listFile)
            {

                if(file.getAbsoluteFile().toString().endsWith(vidName))
                {
                    String temp = file.getPath().substring(0, file.getPath().lastIndexOf('/'));
                    //  Bitmap resized = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.getAbsolutePath()), 180, 300);
                    Bitmap  bitmap= ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                    model = new VideoModel();
                    model.setName(file.getName());
                    model.setUrl(file.getAbsolutePath());
                    model.setImageBitmap(bitmap);
                    try
                    {
                        if(bitmap!=null)
                            scaleBitmap(bitmap);
                        else
                            finish();
                    }
                    catch (Exception e)
                    {

                    }
                    models.add(model);
                }


            }
        }
        return model;
    }

    public void setImage(Bitmap bb)
    {
        ImageView im=(ImageView)findViewById(R.id.thubnail);
        im.setImageBitmap(bb);
    }
    public void scaleBitmap(Bitmap srcBmp)
    {
        if(srcBmp==null)
            return;
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        }else{

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }
        setImage(dstBmp);
    }

    public void playMedia(View view)
    {
        String path;
        try
        {
            path = finall.getUrl();
        }
        catch (Exception e)
        {
            path=url+"/"+vidName;
        }
        try {
            final Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.parse(path), "video/*");
            startActivity(i);
            finish();
        }
        catch (Exception e)
        {

        }
    }
    public void share(View v)
    {
        String path;
        try
        {
            path = finall.getUrl();
        }
        catch (Exception e)
        {
            path=url+"/"+vidName;
        }
        try{
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("video/*");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Title");
            sharingIntent.putExtra(Intent.EXTRA_STREAM,
                    Uri.parse(path));
            startActivity(Intent.createChooser(sharingIntent, "share:"));
        }
        catch (Exception e)
        {

        }
    }
    public void delete(View v)
    {
        if(finall==null)
            return;

        if(finall.getId()==0)
        {
            String path = finall.getUrl();
            boolean  anfffs= new File(path).delete();
        }
        else
        {
            Uri uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, finall.getId());
            ContentResolver resolver = getContentResolver();
            resolver.delete(uri, null, null);
        }
        finish();
    }

    public void rename(View v)
    {
        String path = url+"/"+vidName;
        String newPath = url+"/"+editText.getText();
        try {
            copy(new File(path),new File(newPath));

        boolean  anfffs= new File(path).delete();

        Uri uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, finall.getId());
        ContentResolver resolver = getContentResolver();
        resolver.delete(uri, null, null);

        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{newPath}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });


        Toast.makeText(Finish_popup.this, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        finish();
//        storage_path();

    }

}
