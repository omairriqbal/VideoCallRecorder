package com.videocaht.recorder.facetime.videocall.screenrecorder;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by umair on 02/10/2020.
 */

public class GetDataForAdapter {
    private Context context;
    private ContentResolver contentResolver;
    private Cursor cursor;
    ArrayList<VideoModel> models;
    public GetDataForAdapter(Context context){
        this.context=context;
        contentResolver=context.getContentResolver();


    }
     public ArrayList<VideoModel> getVideoData()
     {
        /* SharedPreferences settings= context.getSharedPreferences("MY_PREF",0);
         int ch1=settings.getInt("storage path",1);*/
         models = new ArrayList<>();


             try {
                 getData();
//                 getVideoData_external();
             }catch (Exception e)
             {
                 Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
             }

         return models;
     }
    public ArrayList<VideoModel> getVideoData_external()
    {

        contentResolver = context.getContentResolver();
        String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME};
        // String[] selectionArgs=new String[]{"FbVideoDownload"};
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArgs = new String[]{"%Screen Recorder%"};
        cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, selection, selectionArgs, MediaStore.Video.Media.DATE_TAKEN);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getColumnIndex(MediaStore.Video.Media._ID);
                int name = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
                int data = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                do {
                    VideoModel model = new VideoModel();
                    model.setName(cursor.getString(name));
                    model.setUrl(cursor.getString(data));
                    model.setId(cursor.getInt(id));
                    model.setCheck(false);
                    Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, model.getId(), MediaStore.Images.Thumbnails.MINI_KIND,
                            null);
                    if (bitmap == null) {
                        bitmap = ThumbnailUtils.createVideoThumbnail(model.getUrl(), MediaStore.Images.Thumbnails.MINI_KIND);
                    }
                    model.setImageBitmap(bitmap);
                    models.add(model);
                    Log.e("video file name",
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
                } while (cursor.moveToNext());
            } else
                {

            }

        }
        else
            {

        }
            return models;
        }

public void getData()
{
    SharedPreferences settings1= context.getSharedPreferences("shared preferences",MODE_PRIVATE);

    String path =  settings1.getString("storage path","storage/emulated/0/");
    String fullPath = path + "/Video Call Recorder";

    File dir = new File(fullPath);

    File listFile[] = dir.listFiles();
    if (listFile != null && listFile.length > 0) {
        for (File file : listFile) {
      /*      if (file.getName().endsWith(".mp4")
                    || file.getName().endsWith(".3gp")) {*/
                String temp = file.getPath().substring(0, file.getPath().lastIndexOf('/'));
                //   Bitmap resized = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.getAbsolutePath()), 180, 300);
                Bitmap  bitmap= ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                VideoModel model = new VideoModel();
                model.setName(file.getName());
               String length = Formatter.formatFileSize(context, file.length());
                model.setSize(length);
                model.setUrl(file.getAbsolutePath());
            Date lastModDate = new Date(file.lastModified());

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMM yyyy");


            String day = (String) DateFormat.format("dd", lastModDate); // 20
            String monthString = (String) DateFormat.format("MMM", lastModDate); // Jun
            String month = (String) DateFormat.format("MM", lastModDate); // 06
            String year = (String) DateFormat.format("yyyy", lastModDate); // 2013


            String parseDate = day + "/" + month + "/" + year;

            System.out.println("date : "+ parseDate.toString());

            model.setDate(parseDate);
                model.setImageBitmap(bitmap);
                model.setCheck(true);
                model.setId(0);
                models.add(model);
//            }

        }
    }
}
}
