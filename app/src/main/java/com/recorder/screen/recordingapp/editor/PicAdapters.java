package com.recorder.screen.recordingapp.editor;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by KAMAL OLI on 12/08/2017.
 */

public class PicAdapters extends RecyclerView.Adapter<PicAdapters.GridViewHolder> {
    private Context context;
    private ArrayList<VideoModel> dataList=new ArrayList<>();
    LayoutInflater inflater;
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
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class GridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {
        PopupMenu popupMenu;
        ImageView videoThumbnail,sdcard;
        TextView name;
        public GridViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            videoThumbnail=itemView.findViewById(R.id.video_thumbnail);
            name=itemView.findViewById(R.id.video_name);
            sdcard=itemView.findViewById(R.id.sdcard);
            ((itemView.findViewById(R.id.play))).setOnClickListener(this);
            ((itemView.findViewById(R.id.share))).setOnClickListener(this);
            ((itemView.findViewById(R.id.delete))).setOnClickListener(this);
            videoThumbnail.setOnClickListener(this);
          //  itemView.setOnCreateContextMenuListener(this);
        }
        public void setViews(VideoModel model) {
           // videoThumbnail.setImageBitmap(model.getImageBitmap());
            try {
                scaleBitmap(model.getImageBitmap());
            }catch (Exception e)
            {

            }
            if((model.getCheck()))
            {
                sdcard.setVisibility(View.VISIBLE);
                sdcard.setImageResource(R.drawable.sdcard);

            }
            else
                sdcard.setVisibility(View.INVISIBLE);
            name.setText(model.getName());
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
     /*   @Override
        public void onClick(View view)
        {
            VideoModel model=dataList.get(getAdapterPosition());
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(model.getUrl())), "image/png");
            context.startActivity(intent);
          *//*  Context wrapper = new ContextThemeWrapper(context, R.style.MyPopupMenu);
            popupMenu =new PopupMenu(wrapper,view);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.inflate(R.menu.video_context_menu);
            popupMenu.show();

            Log.e("ViewId",view.getId()+"");
            VideoModel model=dataList.get(getAdapterPosition());
            Log.e("ArrayList index",model.getName());*//*
            //context.startActivity(intent);
        }*/
        public void onClick(View  item) {
            switch (item.getId())
            {
                case R.id.video_thumbnail:
                case R.id.play:
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


                  /*  try {
                        final Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setDataAndType(Uri.parse(dataList.get(getAdapterPosition()).getUrl()), "video/*");
                        context.startActivity(i);
                    }
                    catch (Exception e)
                    {
                        String msg=e.getMessage();
                        Log.e("ViewId",msg);
                        Toast.makeText(context, "rrr "+msg, Toast.LENGTH_SHORT).show();
                    }*/
                    break;

                case R.id.share:
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("image/png");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Title");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM,
                            Uri.parse(dataList.get(getAdapterPosition()).getUrl()));
                    context.startActivity(Intent.createChooser(sharingIntent,"share:"));
                    break;
                case R.id.delete:
                    int position=getAdapterPosition();
                    showWarning(position);
                    break;
                   /* Intent in=new Intent(context, videoeditor.bhuvnesh.com.ffmpegvideoeditor.activity.MainActivity.class);
                    Uri uu=Uri.fromFile(new File(dataList.get(getAdapterPosition()).getUrl()));
                    //  PreferenceUtils.setPreferenceValue(videos.this,"pp",""+uu);
                    in.putExtra("uu",uu.toString());
                    context.startActivity(in);*/
                 /*   Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(dataList.get(getAdapterPosition()).getUrl()));
                    i.setDataAndType(Uri.parse(dataList.get(getAdapterPosition()).getUrl()), "video");
                    context.startActivity(i);*/
            }
        }

      /*  @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.play:
                  *//*   Intent intent=new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("videoUrl",dataList.get(getAdapterPosition()).getUrl());
                    context.startActivity(intent);*//*
                    try {
                        final Intent i = new Intent(Intent.ACTION_VIEW);
                        Uri videoUri = FileProvider.getUriForFile(context,context.getPackageName(), new File(dataList.get(getAdapterPosition()).getUrl()));
                        i.setDataAndType(Uri.parse(dataList.get(getAdapterPosition()).getUrl()), "video/*");
                        context.startActivity(i);
                    }
                    catch (Exception e)
                    {
                        String msg=e.getMessage();
                        Log.e("ViewId",msg);
                    }
                 return true;
                default:
                    return false;
                case R.id.cancel:
                    popupMenu.dismiss();
                    return true;
                case R.id.share:
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("video/*");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Title");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM,
                            Uri.parse(dataList.get(getAdapterPosition()).getUrl()));
                    context.startActivity(Intent.createChooser(sharingIntent,"share:"));
                    return true;
                case R.id.delete:
                    int position=getAdapterPosition();
                    Uri uri= ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,dataList.
                            get(position).getId());
                    ContentResolver resolver=context.getContentResolver();
                    resolver.delete(uri,null,null);
                    dataList.remove(position);
                    notifyItemRemoved(position);
                    return true;
                   *//* Intent in=new Intent(context, videoeditor.bhuvnesh.com.ffmpegvideoeditor.activity.MainActivity.class);
                    Uri uu=Uri.fromFile(new File(dataList.get(getAdapterPosition()).getUrl()));
                    //  PreferenceUtils.setPreferenceValue(videos.this,"pp",""+uu);
                    in.putExtra("uu",uu.toString());
                    context.startActivity(in);*//*
                 *//*   Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(dataList.get(getAdapterPosition()).getUrl()));
                    i.setDataAndType(Uri.parse(dataList.get(getAdapterPosition()).getUrl()), "video");
                    context.startActivity(i);*//*
            }
        }*/
        private void showWarning(int position) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
            Locale.getDefault().getDisplayLanguage();
            builder.setMessage("Image will be deleted permanently")
                    .setNegativeButton("Proceed",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();


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
                                    dataList.remove(position);
                                    notifyItemRemoved(position);
                                }
                            })
                    .setPositiveButton("Cancel",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                }
                            });

            androidx.appcompat.app.AlertDialog alert = builder.create();
            alert.getWindow().setWindowAnimations(R.style.Animation);
            alert.show();
        }
    }
}
