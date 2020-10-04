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

    class GridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, PopupMenu.OnMenuItemClickListener {
        PopupMenu popupMenu;
        ImageView sdcard;
        ShapeableImageView videoThumbnail;
        TextView name;
        private ImageView menu;
        public GridViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            videoThumbnail=itemView.findViewById(R.id.video_thumbnail);
            name=itemView.findViewById(R.id.video_name);
            sdcard=itemView.findViewById(R.id.sdcard);
            menu  = itemView.findViewById(R.id.menu);

            menu.setOnClickListener((View v) ->{
//buildPopupMenu(v);
            });

           videoThumbnail.setShapeAppearanceModel(videoThumbnail.getShapeAppearanceModel()
                    .toBuilder()
                    .setTopRightCorner(CornerFamily.ROUNDED, 10)
                    .setBottomRightCorner(CornerFamily.ROUNDED, 10)
                    .setTopLeftCorner(CornerFamily.ROUNDED, 10)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, 10)
                    .build());
            itemView.setOnCreateContextMenuListener(this);
            ((itemView.findViewById(R.id.play))).setOnClickListener(this);
            ((itemView.findViewById(R.id.share))).setOnClickListener(this);
            ((itemView.findViewById(R.id.delete))).setOnClickListener(this);
            videoThumbnail.setOnClickListener(this);
        }
        public void setViews(VideoModel model)
        {
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

        private void showContextMenu(){
        }
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.play:
                  /*   Intent intent=new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("videoUrl",dataList.get(getAdapterPosition()).getUrl());
                    context.startActivity(intent);*/
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

            }
        }
        @Override
        public void onClick(View  item) {
            switch (item.getId())
            {
                case R.id.video_thumbnail:
                case R.id.play:
                  /*   Intent intent=new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("videoUrl",dataList.get(getAdapterPosition()).getUrl());
                    context.startActivity(intent);*/
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
                  break;

                case R.id.share:
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("video/*");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Title");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM,
                            Uri.parse(dataList.get(getAdapterPosition()).getUrl()));
                    context.startActivity(Intent.createChooser(sharingIntent,"share:"));
                    break;
                case R.id.delete:
                    int position=getAdapterPosition();
                    showWarning(position);
                    break;

            }
        }
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        }
    }

/*    @SuppressLint("RestrictedApi")
    private void buildPopUpMenu(View view) {
        @SuppressLint("RestrictedApi") MenuBuilder menuBuilder = new MenuBuilder(context);
        MenuInflater inflater = new MenuInflater(context);
        inflater.inflate(R.menu.list_item_menu, menuBuilder);
        @SuppressLint("RestrictedApi") MenuPopupHelper optionsMenu = new MenuPopupHelper(context, menuBuilder, view);
        optionsMenu.setForceShowIcon(true);
        // Set Item Click Listener
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.rename:
                        new RenameDialog(context, baseName) {
                            @Override
                            public void onDismiss(DialogInterface dialog) {

                            }

                            @Override
                            public void onOK(String newName) {
                                String downloadFolder = DownloadManager.getDownloadFolder();
                                if (downloadFolder != null) {
                                    File renamedFile = new File(downloadFolder, newName );
                                    File file = new File(downloadFolder, baseName);
                                    try {
                                        if (file.renameTo(renamedFile)) {
                                            videos.set(getAdapterPosition(), newName );
                                            completedVideos.save(getApplicationContext());
                                            checkSameFile();
                                            downloadsList.getAdapter().notifyItemChanged(getAdapterPosition());
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Failed: Invalid Filename", Toast
                                                    .LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(CompletedDownloadsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        };
                        break;

                    case R.id.downloadLocation:
                        new AlertDialog.Builder(CompletedDownloadsActivity.this)
                                .setTitle("Download Location")
                                .setMessage(DownloadManager.getDownloadFolder())

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setIcon(R.mipmap.ic_launcher_round)
                                .show();
                        break;

                    case R.id.deleteListItem:
                        final AlertDialog.Builder checkbuilder = new AlertDialog.Builder(CompletedDownloadsActivity.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.delete_dailog, null);
                        ImageView imageView = view.findViewById(R.id.closeDialog);
                        Button yes = view.findViewById(R.id.dailog_delete_button);
                        Button no = view.findViewById(R.id.dailog_canel_button);

                        imageView.setOnClickListener((View v) -> {
                            dialog.dismiss();
                        });
                        no.setOnClickListener((View v) -> {
                            dialog.dismiss();
                        });

                        yes.setOnClickListener((View v) -> {
                            dialog.dismiss();
                            String downloadFolder = "";
                            File directory = null;
                            try {
                                directory = DownloadManager.prepareTargetDirectory();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                                 files = directory.listFiles();
                            downloadFolder = directory.getAbsolutePath() + "/";

                            if (downloadFolder != null) {
                                try {
                                    int adapterposition = getAdapterPosition();
                                    Uri fileUri = Uri.fromFile(files[adapterposition]);
                                    File f = new File(fileUri.getPath());
                                    if (f.exists()) {
                                        if (f.delete()) {
                                            Toast.makeText(CompletedDownloadsActivity.this, "file Deleted :" + fileUri.getPath(), Toast.LENGTH_SHORT).show();
                                            System.out.println("file Deleted :" + fileUri.getPath());
                                            int position = getAdapterPosition();
                                            videos.remove(position);
                                            completedVideos.save(getApplicationContext());
                                            downloadsList.getAdapter().notifyItemRemoved(position);
                                        } else {
                                            Toast.makeText(CompletedDownloadsActivity.this, "file not Deleted :" + fileUri.getPath(), Toast.LENGTH_SHORT).show();

                                            System.out.println("file not Deleted :" + fileUri.getPath());
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        checkbuilder.setView(view);
                        dialog = checkbuilder.create();
                        dialog.show();

                        break;
                    case R.id.shareLink:
                        File directory = null;
                        try {
                            directory = DownloadManager.prepareTargetDirectory();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                             files = directory.listFiles();
                        String downloadFolder =
                                directory.getAbsolutePath() + "/";
                        int adapterposition = getAdapterPosition();

                        Uri fileUri = Uri.fromFile(files[adapterposition]);
                        File f = new File(fileUri.getPath());

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("video/*");
                        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
                        startActivity(Intent.createChooser(intent, "Share via"));

                        break;

                }
                return false;
            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {
            }
        });

        optionsMenu.show();
    }*/

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
