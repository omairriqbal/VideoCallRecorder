package com.recorder.screen.recordingapp.editor;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jolta on 1/1/2018.
 */
public class AppRater {
    private static String APP_TITLE ;
    private  static String APP_PNAME;
    private final static int DAYS_UNTIL_PROMPT = 3;
    private final static int LAUNCHES_UNTIL_PROMPT = 5;
 //   static Context context;

    public static void app_launched(Context mContext, String pn, String name)
    {
        APP_PNAME=pn;
        APP_TITLE=name;
       // context=mContext;
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        SharedPreferences.Editor editor = prefs.edit();


        if (prefs.getBoolean("dontshowagain", false))
        {
            return ;
        }


        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;

        editor.putLong("launch_count", launch_count);
        // Wait at least n turns before opening dialog
        if (launch_count >=LAUNCHES_UNTIL_PROMPT)
        {
            if(!(prefs.getBoolean("rated",false)))
            {
                main_menu.feedback=true;
               editor.putLong("launch_count", 0);
            }

        }

        editor.commit();
    }



}