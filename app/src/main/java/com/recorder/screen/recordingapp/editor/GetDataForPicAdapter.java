package com.recorder.screen.recordingapp.editor;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by KAMAL OLI on 14/08/2017.
 */

public class GetDataForPicAdapter
{
    private Context context;
    private ContentResolver contentResolver;
    private Cursor cursor;
    private WindowManager windowManagerPanel;
    ArrayList<VideoModel> models;

    public GetDataForPicAdapter(Context context){
        this.context=context;
        contentResolver=context.getContentResolver();


    }
    public ArrayList<VideoModel> getVideoData()
    {
        models=new ArrayList<>();

        try {
            getData();
        }catch (Exception e)
        {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return models;

    }
    public ArrayList<VideoModel> getVideoData_external()
    {

        contentResolver=context.getContentResolver();
        String[] projection={MediaStore.Images.Media._ID,MediaStore.Images.Media.SIZE,MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME};
       // String[] selectionArgs=new String[]{"FbVideoDownload"};
        String selection=MediaStore.Images.Media.DATA +" like?";
        String[] selectionArgs=new String[]{"%Screen Capture Recorder%"};
        cursor= contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,selection,selectionArgs,MediaStore.Images.Media.DATE_TAKEN);
        if(cursor!=null)
        {
            if(cursor.moveToFirst())
            {
                int id=cursor.getColumnIndex(MediaStore.Images.Media._ID);
                int name=cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                int data=cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                do{
                    VideoModel model=new VideoModel();
                    model.setName(cursor.getString(name));
                    model.setUrl(cursor.getString(data));
                    model.setId(cursor.getInt(id));
                    model.setCheck(false);
                    Bitmap bitmap=MediaStore.Images.Thumbnails.getThumbnail(contentResolver,model.getId(), MediaStore.Images.Thumbnails.MINI_KIND,
                            null);
                    if(bitmap==null){
                        bitmap= ThumbnailUtils.createVideoThumbnail(model.getUrl(), MediaStore.Images.Thumbnails.MINI_KIND);
                    }
                    model.setImageBitmap(bitmap);
                    models.add(model);
                    Log.e("video file name",
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                }while(cursor.moveToNext());
            }
            else{

            }

        }
        else{

        }


       /* cursor= contentResolver.query(sdcard,
                projection,selection,selectionArgs,MediaStore.Images.Media.DATE_TAKEN);
        if(cursor!=null)
        {
            if(cursor.moveToFirst())
            {
                int id=cursor.getColumnIndex(MediaStore.Images.Media._ID);
                int name=cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                int data=cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                do{
                    VideoModel model=new VideoModel();
                    model.setName(cursor.getString(name));
                    model.setUrl(cursor.getString(data));
                    model.setId(cursor.getInt(id));
                    Bitmap bitmap=MediaStore.Images.Thumbnails.getThumbnail(contentResolver,model.getId(), MediaStore.Images.Thumbnails.MINI_KIND,
                            null);
                    if(bitmap==null){
                        bitmap= ThumbnailUtils.createVideoThumbnail(model.getUrl(), MediaStore.Images.Thumbnails.MINI_KIND);
                    }
                    model.setImageBitmap(bitmap);
                    models.add(model);
                    Log.e("video file name",
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                }while(cursor.moveToNext());
            }
            else{
                Log.e("No media file","Present in the selected directory");
            }
            Log.e("Cursor",cursor.toString());
        }
        else{
            Log.e("No data in Cursor",cursor.toString()+"");
        }*/

        return models;
    }
    public void getData()
    {

        SharedPreferences settings1= context.getSharedPreferences("shared preferences",MODE_PRIVATE);

        String fullPath = settings1.getString("storage path","storage/emulated/0/");
        String finalPath = fullPath + "/Video Call Recorder ScreenShots";

        File dir=new File(finalPath);

        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (File file : listFile)
            {
                if (file.getName().endsWith(".png")
                        || file.getName().endsWith(".jpg")
                        || file.getName().endsWith(".jpeg")
                        || file.getName().endsWith(".gif")
                        || file.getName().endsWith(".bmp")
                        || file.getName().endsWith(".webp"))
                {
                    String temp = file.getPath().substring(0, file.getPath().lastIndexOf('/'));
                    Bitmap resized = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.getAbsolutePath()), 180, 300);
                    // Bitmap  bitmap= ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                    VideoModel model=new VideoModel();
                    model.setName(file.getName());
                    model.setUrl(file.getAbsolutePath());
                    model.setImageBitmap(resized);
                    model.setCheck(true);
                    model.setId(0);
                    models.add(model);
                }

            }


        }
    }



}
