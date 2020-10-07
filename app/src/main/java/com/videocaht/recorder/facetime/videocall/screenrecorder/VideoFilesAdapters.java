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
import android.provider.MediaStore;

import androidx.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by  Umair
 */

public class VideoFilesAdapters extends RecyclerView.Adapter<VideoFilesAdapters.GridViewHolder> {
    private Context context;
    private ArrayList<VideoModel> dataList = new ArrayList<>();
    VideoModel model;
    LayoutInflater inflater;
    private AlertDialog alertDialog;

    public VideoFilesAdapters(Context context, ArrayList<VideoModel> dataList) {
        this.context = context;

        int x = (dataList.size()) - 1;
        for (int i = x; i >= 0; i--) {
            this.dataList.add(dataList.get(i));
        }
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.single_view_of_video_new, parent, false);
        GridViewHolder viewHolder = new GridViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {
        model = dataList.get(position);
        holder.setViews(model);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class GridViewHolder extends RecyclerView.ViewHolder /*implements PopupMenu.OnMenuItemClickListener*/ {
        PopupMenu popupMenu;
        ImageView videoThumbnail;
        TextView name, size, date;
        ImageView menu;


        @SuppressLint("ResourceType")
        public GridViewHolder(View itemView) {
            super(itemView);

            videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
            name = itemView.findViewById(R.id.video_name);
            size = itemView.findViewById(R.id.video_size);
            menu = itemView.findViewById(R.id.menu);
            date = itemView.findViewById(R.id.date);

            menu.setOnClickListener((View v) -> {
                //Creating the instance of PopupMenu
                int pos = getAdapterPosition();
                buildPopupMenu(v, pos);

            });
            videoThumbnail.setOnClickListener((View v) -> {

                try {
                    final Intent i = new Intent(Intent.ACTION_VIEW);
                    //   Uri videoUri = FileProvider.getUriForFile(context,context.getPackageName(), new File(dataList.get(getAdapterPosition()).getUrl()));
                    i.setDataAndType(Uri.parse(dataList.get(getAdapterPosition()).getUrl()), "video/*");

                    //  i.setDataAndType(Uri.fromFile(new File(dataList.get(getAdapterPosition()).getUrl())), "video/*");
                    context.startActivity(i);
                } catch (Exception e) {
                    String msg = e.getMessage();
                    Log.e("ViewId", msg);
                    Toast.makeText(context, "rrr " + msg, Toast.LENGTH_SHORT).show();
                }

            });
        }

        public void setViews(VideoModel model) {
            size.setText(model.getSize());
            name.setText(model.getName());
            date.setText(model.getDate());
            // videoThumbnail.setImageBitmap(model.getImageBitmap());
            try {
                scaleBitmap(model.getImageBitmap());
            } catch (Exception e) {

            }
            if ((model.getCheck()))
                name.setText(model.getName());

        }

        public void scaleBitmap(Bitmap srcBmp) {
            Bitmap dstBmp;
            if (srcBmp.getWidth() >= srcBmp.getHeight()) {

                dstBmp = Bitmap.createBitmap(
                        srcBmp,
                        srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2,
                        0,
                        srcBmp.getHeight(),
                        200
                );

            } else {

                dstBmp = Bitmap.createBitmap(
                        srcBmp,
                        0,
                        srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2,
                        srcBmp.getWidth(),
                        200
                );
            }
            videoThumbnail.setImageBitmap(dstBmp);
        }

        private void renameDailog(String oldName) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

            View dialogView = inflater.inflate(R.layout.rename_dailog, null);
            dialogBuilder.setView(dialogView);

            EditText editText = (EditText) dialogView.findViewById(R.id.renameEdt);
            TextView okay = dialogView.findViewById(R.id.okRename);
            TextView cancel = dialogView.findViewById(R.id.cancelRename);
            editText.setText(oldName);
            cancel.setOnClickListener((View v) -> {
                alertDialog.dismiss();
            });
            okay.setOnClickListener((View v) -> {
                SharedPreferences settings1 = context.getSharedPreferences("shared preferences", MODE_PRIVATE);

                String fullPath = settings1.getString("storage path", "storage/emulated/0/");
                String finalPath = fullPath + "/Video Call Recorder";

                if (finalPath != null) {
                    File renamedFile = new File(finalPath, editText.getText().toString());
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
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.show();
            Window window = alertDialog.getWindow();
            window.setLayout(600, ViewGroup.LayoutParams.WRAP_CONTENT);
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
                            String oldName = dataList.get(position).getName();
                            renameDailog(oldName);
                            break;
                        case R.id.shareLink:
                            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                            sharingIntent.setType("video/*");
                            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Title");
                            sharingIntent.putExtra(Intent.EXTRA_STREAM,
                                    Uri.parse(dataList.get(position).getUrl()));
                            context.startActivity(Intent.createChooser(sharingIntent, "share:"));
                            break;
                        case R.id.deleteListItem:
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

    }

    private void getDeleteDialog(int position) {
        final AlertDialog.Builder textBuilder = new AlertDialog.Builder(context);

        View view = inflater.inflate(R.layout.delete_dailog, null);

        TextView yes = view.findViewById(R.id.feedbackSubmit);
        TextView cancel = view.findViewById(R.id.feedbackCancel);

        yes.setOnClickListener((View v) ->{

            if (dataList.size() > 0) {
                if (dataList.get(position).getId() == 0) {
                    String path = dataList.get(position).getUrl();
                    boolean anfffs = new File(path).delete();
                }
            } else {
                Uri uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, dataList.
                        get(position).getId());
                ContentResolver resolver = context.getContentResolver();
                resolver.delete(uri, null, null);
            }

            try {

            } catch (Exception e) {

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
        window.setLayout(500, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.setCancelable(false);
    }
}
