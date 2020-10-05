package com.recorder.screen.recordingapp.editor;

import android.graphics.Bitmap;

/**
 * Created by KAMAL OLI on 12/08/2017.
 */

public class VideoModel {
    private String url;
    private String name;
    private String size;
    private int id;
    private boolean sdcard;
    private Bitmap imageBitmap;
    public void setImageBitmap(Bitmap bitmap){
        imageBitmap=bitmap;
    }
    public Bitmap getImageBitmap(){
        return imageBitmap;
    }
    public void setCheck(boolean ans)
    {
        sdcard= ans;
    }
    public boolean getCheck()
    {
        return sdcard;
    }
    public void setUrl(String url){
        this.url=url;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setId(int id){
        this.id=id;
    }
    public String getUrl(){
        return url;
    }
    public String getName(){
        return name;
    }
    public int getId(){
        return id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
