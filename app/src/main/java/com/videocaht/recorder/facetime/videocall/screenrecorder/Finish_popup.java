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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

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
    ArrayList<VideoModel> result=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.finish_window);
        SharedPreferences settings1= getSharedPreferences("MY_PREF",0);
        int ch=settings1.getInt("storage path",1);
        vidName=getIntent().getStringExtra("name");
        url=getIntent().getStringExtra("url");
        if(vidName!=null)
        {
            vidName=vidName.replace("/","");
            ((EditText)(findViewById(R.id.title))).setText(vidName);

            if(ch==1)
                finall=getVideoData();
            else if(ch==2)
                finall= getFromSDCard();

            if(finall==null)
                finish();

            else if(finall.getUrl()==null)
                finall.setUrl(url+"/"+vidName);
        }

    }
    public void storage_path()
    {
        dialog = new Dialog(Finish_popup.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog .setCancelable(false);
        dialog .setContentView(R.layout.select_path);
        dialog.show();

        boolean isSDPresent=false;
        File f1 = new File("/storage/");
        String listOfStorages[]= f1.list();
        if(listOfStorages[1].contains("emulated")||listOfStorages[0].contains("-"))
        {
            isSDPresent=true;

        }
        if( isSDPresent)
        {
            // yes SD-card is present
        }
        else
        {
            // Sorry
            RadioButton r1=(RadioButton) dialog.findViewById(R.id.sd);
            r1.setEnabled(false);
            r1.setText("No SD Card");
        }

        (dialog.findViewById(R.id.close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        SharedPreferences settings1= getSharedPreferences("MY_PREF",0);
        int ch=settings1.getInt("storage path",1);
        selected=ch;
        if(ch==1)
        {
            RadioButton r1=(RadioButton) dialog.findViewById(R.id.internal);
            r1.setChecked(true);
        }
        if(ch==2)
        {
            RadioButton r1=(RadioButton) dialog.findViewById(R.id.sd);
            r1.setChecked(true);
        }

        RadioGroup rr=(RadioGroup) dialog.findViewById(R.id.path);
        rr.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {

            public void onCheckedChanged(RadioGroup buttonView, int ans)
            {
                // update your model (or other business logic) based on isChecked
                switch(ans)
                {
                    case R.id.internal:
                        selected=1;
                        break;

                    case R.id.sd:
                        selected=2;
                        break;

                }
                dialog.cancel();
                EditText title=(EditText)findViewById(R.id.title);
                String text=title.getText().toString();

                String newPath;

                if(!(title.getText().toString().equals(vidName)) || (selected!=ch))
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

                    if(selected==1)
                    {
                            if(!path.contains("emulated"))
                            {
                                newPath= Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screen Recorder";//+ ;
                                if(!(new File(newPath).exists()))
                                {
                                    new File(newPath).mkdir();
                                }
                                String name[]=vidName.split("\\.");
                                String new_name[]=text.split("\\.");

                                    if((text.endsWith(".mp4")  ||(text.endsWith(".3gp")) ))
                                newPath+="/"+title.getText().toString();
                                else
                                    {
                                        SharedPreferences settings1= getSharedPreferences("MY_PREF",0);
                                        int ch=settings1.getInt("video formate",1);
                                        String ext;
                                        if(ch==1)
                                            ext=".mp4";
                                        else
                                            ext=".3gp";
                                        text.replace(".","");
                                        newPath+="/"+text+"."+ext;
                                    }

                            }
                            else
                            {
                                newPath = path.replace(vidName, title.getText().toString());
                            }



                    }
                    else
                    {
                        if(path.contains("emulated"))
                        {
                            newPath = getExternalFilesDir("Screen Recorder").getAbsolutePath() ;
                            File f1 = new File("/storage/");
                            String[] list = f1.list();
                            newPath = newPath.replace("emulated/0",list[0]) ;

                            String name[]=vidName.split("\\.");
                            String new_name[]=text.split("\\.");
/*
                            if(name[name.length-1].equals(new_name[new_name.length-1]))
                                newPath+="/"+title.getText().toString()+"."+new_name[new_name.length-1];*/
                            if((text.endsWith(".mp4")  ||(text.endsWith(".3gp")) ))
                                newPath+="/"+title.getText().toString();
                            else
                            {
                                SharedPreferences settings1= getSharedPreferences("MY_PREF",0);
                                int ch=settings1.getInt("video formate",1);
                                String ext;
                                if(ch==1)
                                    ext=".mp4";
                                else
                                    ext=".3gp";

                                text.replace(".","");
                                newPath+="/"+text+"."+ext;
                            }

                        }
                        else
                        {
                            newPath = path.replace(vidName, title.getText().toString());
                        }

                    }

///storage/emulated/0/Screen Recorder/SR_1540903419371.mp4
                    // newPath = path.replace(vidName, title.getText().toString());
                    boolean anss = new File(path).renameTo(new File(newPath));
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
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }


                    Toast.makeText(Finish_popup.this, "Saved", Toast.LENGTH_SHORT).show();

                }
                finish();



            }
        });
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
        String fullPath;
        if(vidName.endsWith(".mp4")  ||(vidName.endsWith(".3gp")))
         fullPath = getExternalFilesDir("Screen Recorder").getAbsolutePath();
        else
            fullPath = getExternalFilesDir("Screen Capture Recorder").getAbsolutePath();
        File f1 = new File("/storage/");
        String[] list = f1.list();
        fullPath = fullPath.replace("emulated/0", list[0]);

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
    public VideoModel getVideoData()
    {
        ArrayList<VideoModel> models=new ArrayList<>();
        contentResolver=getContentResolver();
        String[] projection={MediaStore.Video.Media._ID,MediaStore.Video.Media.SIZE,MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME};
        // String[] selectionArgs=new String[]{"FbVideoDownload"};
        String selection=MediaStore.Video.Media.DATA +" like?";
        String[] selectionArgs=new String[]{"%Screen Recorder%"};
        cursor= contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,selection,selectionArgs,MediaStore.Video.Media.DATE_TAKEN);
        if(cursor!=null){
            if(cursor.moveToFirst()) {
                int id = cursor.getColumnIndex(MediaStore.Video.Media._ID);
                int name = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
                int data = cursor.getColumnIndex(MediaStore.Video.Media.DATA);


                    do {
                        String nn=cursor.getString(name);
                        if(nn!=null)
                        if (nn.equals(vidName))
                        {
                        model = new VideoModel();
                        model.setName(cursor.getString(name));
                        model.setUrl(cursor.getString(data));
                        model.setId(cursor.getInt(id));
                        Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, model.getId(), MediaStore.Images.Thumbnails.MINI_KIND,
                                null);
                        if (bitmap == null)
                        {
                            bitmap = ThumbnailUtils.createVideoThumbnail(model.getUrl(), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                        }
                        model.setImageBitmap(bitmap);
                            if(bitmap!=null)
                               scaleBitmap(bitmap);
                            else
                                finish();
                        models.add(model);

                        Log.e("video file name",
                                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
                        break;
                    }
                    } while (cursor.moveToNext());

            }
            else{
                Log.e("No media file","Present in the selected directory");
            }
            Log.e("Cursor",cursor.toString());
        }
        else{
            Log.e("No data in Cursor",cursor.toString()+"");
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
        storage_path();

    }

}
