package com.videocaht.recorder.facetime.videocall.screenrecorder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Umair .
 */

public class PicAdapters extends RecyclerView.Adapter<PicAdapters.GridViewHolder> {
    private Context context;
    private ArrayList<VideoModel> dataList=new ArrayList<>();
    LayoutInflater inflater;
    private AlertDialog alertDialog;

    public PicAdapters(Context context, ArrayList<VideoModel> dataList){
        this.context=context;
        int x=(dataList.size())-1;
        for(int i=x;i>=0;i--)
        {
            this.dataList.add(dataList.get(i));
        }
        inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view=inflater.inflate(R.layout.single_view_of_pic,parent,false);
        GridViewHolder viewHolder=new GridViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {
        VideoModel model=dataList.get(position);
        holder.setViews(model);
        if(position ==2 || (position + 1) % 4 == 0){
            holder.load_Ads();
        } else
            holder.frameLayout.setVisibility(View.GONE);

    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class GridViewHolder extends RecyclerView.ViewHolder{
        private final LinearLayout frameLayout;
        PopupMenu popupMenu;
        ImageView videoThumbnail;
        TextView name,size,date;
        ImageView menu;

        public GridViewHolder(View itemView) {
            super(itemView);
            videoThumbnail=itemView.findViewById(R.id.video_thumbnail);
            name=itemView.findViewById(R.id.video_name);
            size = itemView.findViewById(R.id.video_size);
            menu = itemView.findViewById(R.id.menu);
            date = itemView.findViewById(R.id.date);
            frameLayout = (LinearLayout) itemView.findViewById(R.id.adLayout);

            videoThumbnail.setOnClickListener((View v)->{
                VideoModel model=dataList.get(getAdapterPosition());
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try{
                    Uri photoURI;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        photoURI = GenericFileProvider.getUriForFile(context, context.getApplicationContext().getPackageName(),new File(model.getUrl()) );
                    else
                        photoURI=Uri.fromFile(new File(model.getUrl()));
                    intent.setDataAndType(photoURI, "image/png");

                    context.startActivity(intent);
                }catch (Exception e)
                {

                }
            });
            menu.setOnClickListener((View v)->{

                int pos = getAdapterPosition();
                buildPopupMenu(v, pos);

            });
        }

        private void load_Ads() {

            if (!DataProvider.getInstance().buy) {
                UnifiedNativeAd native_admob = DataProvider.getInstance().get_native_admob();
                if (native_admob != null) {

                    UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(itemView.getContext())
                            .inflate(R.layout.native_admob, null);
                    populateUnifiedNativeAdView(native_admob, adView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(adView);
                    frameLayout.setVisibility(View.VISIBLE);
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

//            com.google.android.gms.ads.formats.MediaView mediaView = (com.google.android.gms.ads.formats.MediaView) adView.findViewById(R.id.ad_media);
            ImageView mainImageView = (ImageView) adView.findViewById(R.id.ad_image);

            adView.setImageView(mainImageView);

            try {
                List<NativeAd.Image> images = nativeAd.getImages();
                if (images.size() > 0)
                    mainImageView.setImageDrawable(images.get(0).getDrawable());

            } catch (Exception e) {

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
           /* if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(
                        nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }
*/
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

        public void setViews(VideoModel model) {

            try {
                scaleBitmap(model.getImageBitmap());
                name.setText(model.getName());
                size.setText(model.getSize());
                date.setText(model.getDate());
            }catch (Exception e)
            {

            }

        }

        public void scaleBitmap(Bitmap srcBmp)
        {
            Bitmap dstBmp;
            if (srcBmp.getWidth() >= srcBmp.getHeight()){

                dstBmp = Bitmap.createBitmap(
                        srcBmp,
                        srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                        0,
                        srcBmp.getHeight(),
                        140
                );

            }
            else
            {

                dstBmp = Bitmap.createBitmap(
                        srcBmp,
                        0,
                        srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                        srcBmp.getWidth(),
                        140
                );
            }
            videoThumbnail.setImageBitmap(dstBmp);
        }

    }



        @SuppressLint("RestrictedApi")
        private void buildPopupMenu(View v, int position) {
            @SuppressLint("RestrictedApi") MenuBuilder menuBuilder = new MenuBuilder(context);
            MenuInflater inflater = new MenuInflater(context);
            inflater.inflate(R.menu.list_item_menu, menuBuilder);
            @SuppressLint("RestrictedApi") MenuPopupHelper optionsMenu = new MenuPopupHelper(context, menuBuilder, v);
            optionsMenu.setForceShowIcon(true);

            menuBuilder.setCallback(new MenuBuilder.Callback() {
                @Override
                public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                    switch ((item.getItemId())) {
                        case R.id.rename:
                            DataProvider.getInstance().log_event("clicked_rename_dailog", "video_adapter_menu");
                            String oldName = dataList.get(position).getName();
                            renameDailog(oldName);
                            break;
                        case R.id.shareLink:
                            DataProvider.getInstance().log_event("clicked_share_dailog", "video_adapter_menu");
                            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                            sharingIntent.setType("video/*");
                            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Title");
                            sharingIntent.putExtra(Intent.EXTRA_STREAM,
                                    Uri.parse(dataList.get(position).getUrl()));
                            context.startActivity(Intent.createChooser(sharingIntent, "share:"));
                            break;
                        case R.id.deleteListItem:
                            DataProvider.getInstance().log_event("clicked_delete_dailog", "video_adapter_menu");
                            getDeleteDialog(position);
                            break;
                    }
                    return false;
                }

                @Override
                public void onMenuModeChange(@NonNull MenuBuilder menu) {

                }
            });
            optionsMenu.show();
        }


    private void getDeleteDialog(int position) {
        final AlertDialog.Builder textBuilder = new AlertDialog.Builder(context);

        View view = inflater.inflate(R.layout.delete_dailog, null);

        TextView yes = view.findViewById(R.id.feedbackSubmit);
        TextView cancel = view.findViewById(R.id.feedbackCancel);

        yes.setOnClickListener((View v) ->{

            if(dataList.get(position).getId()==0)
            {
                String path = dataList.get(position).getUrl();
                boolean  anfffs= new File(path).delete();
            }
            else
            {
                try
                {
                    Uri uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, dataList.
                            get(position).getId());
                    ContentResolver resolver = context.getContentResolver();
                    resolver.delete(uri, null, null);
                }
                catch (SecurityException r)
                {


                }
                catch (Exception e)
                {

                }

            }
            Snackbar.make(v,"Item deleted Successfully", Snackbar.LENGTH_LONG).show();

            dataList.remove(position);
            notifyItemRemoved(position);

            alertDialog.cancel();
        });

        cancel.setOnClickListener((View v) ->{

            alertDialog.cancel();  });

        textBuilder.setView(view);
        alertDialog = textBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setLayout(700, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.setCancelable(false);
    }

    private void renameDailog(String oldName) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        View dialogView = inflater.inflate(R.layout.rename_dailog, null);
        dialogBuilder.setView(dialogView);

        EditText editText = (EditText) dialogView.findViewById(R.id.renameEdt);
        TextView okay = dialogView.findViewById(R.id.okRename);
        TextView cancel = dialogView.findViewById(R.id.cancelRename);
        editText.setText(oldName);

        cancel.setOnClickListener((View v)->{ alertDialog.dismiss(); });

        okay.setOnClickListener((View v)->{
            SharedPreferences settings1= context.getSharedPreferences("shared preferences",MODE_PRIVATE);

            String fullPath = settings1.getString("storage path","storage/emulated/0/");
            String finalPath = fullPath + "/Video Call Recorder ScreenShots";

            if (finalPath != null) {
                File renamedFile = new File(finalPath, editText.getText().toString() );
                File file = new File(finalPath, oldName);
                try {
                    if (file.renameTo(renamedFile)) {
                        notifyDataSetChanged();
//                        new Handler().postDelayed(() ->   notifyDataSetChanged(), 1000);
                    } else {
                        Toast.makeText(context, "Failed: Invalid Filename", Toast
                                .LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            alertDialog.dismiss();
        });
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setLayout(750, ViewGroup.LayoutParams.WRAP_CONTENT);

    }
}
