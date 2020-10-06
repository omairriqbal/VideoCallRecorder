package com.videocaht.recorder.facetime.videocall.screenrecorder;

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

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by KAMAL OLI on 12/08/2017.
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
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class GridViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {
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


                Context wrapper = new ContextThemeWrapper(context, R.style.MyPopupMenu);
                popupMenu =new PopupMenu(wrapper,v);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.inflate(R.menu.list_item_menu);
                popupMenu.show();
                Log.e("ViewId",v.getId()+"");
                VideoModel model=dataList.get(getAdapterPosition());
                Log.e("ArrayList index",model.getName());

            });
        }
        public void setViews(VideoModel model) {
            // videoThumbnail.setImageBitmap(model.getImageBitmap());
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

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch ((item.getItemId())) {
                case R.id.rename:
                    String oldName = dataList.get(getAdapterPosition()).getName();
                    renameDailog(oldName);

                    /*new RenameDialog(context,oldName) {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }

                        @Override
                        public void onOK(String newName) {
                            SharedPreferences settings1= context.getSharedPreferences("shared preferences",MODE_PRIVATE);

                            String fullPath = settings1.getString("storage path","storage/emulated/0/");
                            String finalPath = fullPath + "/Video Call Recorder";

                            if (finalPath != null) {
                                File renamedFile = new File(finalPath, newName );
                                File file = new File(finalPath, oldName);
                                try {
                                    if (file.renameTo(renamedFile)) {
                                        notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(context, "Failed: Invalid Filename", Toast
                                                .LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    };*/
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
        window.setLayout(400, ViewGroup.LayoutParams.WRAP_CONTENT);

    }
}
