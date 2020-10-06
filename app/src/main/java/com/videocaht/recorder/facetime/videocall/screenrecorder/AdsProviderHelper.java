package com.videocaht.recorder.facetime.videocall.screenrecorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AdsProviderHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "admob";

    public static final String Ads= "ads";
    public static final String ID= "id";
    public static final String NAME = "name";
    public static final String AD_ID= "ad_id";


//    static final String DATABASE_CREATE1 = "CREATE TABLE IF NOT EXISTS " + "Ads" +
//            "( " + "ID" + " integer primary key autoincrement," + "name text,ad_id text); ";

    private static final String DATABASE_CREATE1 = "CREATE TABLE IF NOT EXISTS "
            + Ads + "(" + ID + " INTEGER PRIMARY KEY," + NAME
            + " TEXT," + AD_ID
            + " TEXT" + ")";


    public AdsProviderHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Ads);
        onCreate(sqLiteDatabase);
    }



    public boolean insert_Ads (Map<String, Object> list)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        Iterator<String> itr = list.keySet().iterator();
        boolean sucess=false;

        while (itr.hasNext())
        {
            String key = itr.next();
            String value = list.get(key).toString();

            newValues = new ContentValues();
            newValues.put("name", key);
            newValues.put("ad_id", value);
            // Insert the row into your table
            long val= db.insert("Ads", null, newValues);
            if(val>0)
                sucess=true;
        }
        return sucess;

    }


    public Map<String, Object> get_Ads ()
    {

        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, Object> list=new HashMap<>();

        Cursor cursor = db.rawQuery("select * from Ads",null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast())
            {

                list.put(cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("ad_id")));

                cursor.moveToNext();

            }
        }
        cursor.close();
        return list;
    }



}
