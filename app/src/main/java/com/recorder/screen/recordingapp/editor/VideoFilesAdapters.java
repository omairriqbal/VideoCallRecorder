package com.recorder.screen.recordingapp.editor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import static com.codekidlabs.storagechooser.StorageChooser.dialog;

/**
 * Created by  umair on 02/10/2020.
 */

public class VideoFilesAdapters extends RecyclerView.Adapter<VideoFilesAdapters.GridViewHolder> {
    private Context context;
    private ArrayList<VideoModel> dataList=new ArrayList<>();
    LayoutInflater inflater;
    public VideoFilesAdapters(Context context, ArrayList<VideoModel> dataList){
        this.context=context;

        int x=(dataList.size())-1;
        for(int i=x;i>=0;i--)
        {
            this.dataList.add(dataList.get(i));
        }
        inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.single_view_of_video_new,parent,false);
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

    class GridViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {
        PopupMenu popupMenu;
        ImageView videoThumbnail;
        TextView name,size;
        ImageView menu;

        public GridViewHolder(View itemView) {
            super(itemView);

            videoThumbnail=itemView.findViewById(R.id.video_thumbnail);
            name=itemView.findViewById(R.id.video_name);
            size=itemView.findViewById(R.id.video_size);
            menu = itemView.findViewById(R.id.menu);

           /*  ((itemView.findViewById(R.id.play))).setOnClickListener(this);
            ((itemView.findViewById(R.id.share))).setOnClickListener(this);
            ((itemView.findViewById(R.id.delete))).setOnClickListener(this);*/


            menu.setOnClickListener((View v)->{
                Toast.makeText(context, "menu", Toast.LENGTH_SHORT).show();
                int position = getAdapterPosition();
                Context wrapper = new ContextThemeWrapper(context, R.style.MyPopupMenu);
                popupMenu =new PopupMenu(wrapper,v);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.inflate(R.menu.list_item_menu);
                popupMenu.show();
                Log.e("ViewId",v.getId()+"");
                VideoModel model=dataList.get(getAdapterPosition());
                Log.e("ArrayList index",model.getName());
                //context.startActivity(intent);
//                buildPopupMenu(v,position);
            });
            videoThumbnail.setOnClickListener((View v) ->{
                Toast.makeText(context, "thumbnail", Toast.LENGTH_SHORT).show();
                try {
                    final Intent i = new Intent(Intent.ACTION_VIEW);
                    //   Uri videoUri = FileProvider.getUriForFile(context,context.getPackageName(), new File(dataList.get(getAdapterPosition()).getUrl()));
                    i.setDataAndType(Uri.parse(dataList.get(getAdapterPosition()).getUrl()), "video/*");

                    //  i.setDataAndType(Uri.fromFile(new File(dataList.get(getAdapterPosition()).getUrl())), "video/*");
                    context.startActivity(i);
                }
                catch (Exception e)
                {
                    String msg=e.getMessage();
                    Log.e("ViewId",msg);
                    Toast.makeText(context, "rrr "+msg, Toast.LENGTH_SHORT).show();
                }

            });
        }
        public void setViews(VideoModel model)
        {
            size.setText(model.getSize());
            name.setText(model.getName());
            // videoThumbnail.setImageBitmap(model.getImageBitmap());
            try {
                scaleBitmap(model.getImageBitmap());
            }catch (Exception e)
            {

            }
            if((model.getCheck()))
            name.setText(model.getName());
                /*sdcard.setVisibility(View.VISIBLE);
                sdcard.setImageResource(R.drawable.sdcard);*/


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
                        200
                );

            }else{

                dstBmp = Bitmap.createBitmap(
                        srcBmp,
                        0,
                        srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                        srcBmp.getWidth(),
                        200
                );
            }
            videoThumbnail.setImageBitmap(dstBmp);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch ((item.getItemId())) {
                case R.id.rename:
                    Toast.makeText(context, "rename", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.shareLink:
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("video/*");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Title");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM,
                            Uri.parse(dataList.get(getAdapterPosition()).getUrl()));
                    context.startActivity(Intent.createChooser(sharingIntent,"share:"));
                    break;
                case R.id.deleteListItem:
                    int position = getAdapterPosition();
                    showWarning(position);
                    break;
            }
            return false;
        }
        /*    @Override
            public void onClick(View view)
            {
                Context wrapper = new ContextThemeWrapper(context, R.style.MyPopupMenu);
                popupMenu =new PopupMenu(wrapper,view);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.inflate(R.menu.video_context_menu);
                popupMenu.show();
                Log.e("ViewId",view.getId()+"");
                VideoModel model=dataList.get(getAdapterPosition());
                Log.e("ArrayList index",model.getName());
                //context.startActivity(intent);
            }
    */


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
                        Toast.makeText(context, "rename", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.shareLink:
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("video/*");
                        sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Title");
                        sharingIntent.putExtra(Intent.EXTRA_STREAM,
                                Uri.parse(dataList.get(position).getUrl()));
                        context.startActivity(Intent.createChooser(sharingIntent,"share:"));
                        break;
                    case R.id.deleteListItem:
                        showWarning(position);
                        break;
                }
                return false;
            }

            @Override
            public void onMenuModeChange(@NonNull MenuBuilder menu) {

            }
        });
    }

    private void showWarning(int position) {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        Locale.getDefault().getDisplayLanguage();
        builder.setMessage("Video will be deleted permanently")
                .setNegativeButton("Proceed",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();


                                if(dataList.size()>0) {
                                    if (dataList.get(position).getId() == 0) {
                                        String path = dataList.get(position).getUrl();
                                        boolean anfffs = new File(path).delete();
                                    }
                                }
                                else {
                                    Uri uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, dataList.
                                            get(position).getId());
                                    ContentResolver resolver = context.getContentResolver();
                                    resolver.delete(uri, null, null);
                                }

                                try
                                {

                                }catch (Exception e)
                                {
                                    dataList.remove(position);
                                    notifyItemRemoved(position);
                                }

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
        try {
            alert.show();
        }
        catch (Exception e)
        {
            /*try
            {
                if(dataList.get(position).getId()==0)
                {
                    String path = dataList.get(position).getUrl();
                    boolean  anfffs= new File(path).delete();
                }
                else {
                    Uri uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, dataList.
                            get(position).getId());
                    ContentResolver resolver = context.getContentResolver();
                    resolver.delete(uri, null, null);
                }
                dataList.remove(position);
                notifyItemRemoved(position);
            }
            catch (Exception ee)
            {

            }*/
        }

    }
}
