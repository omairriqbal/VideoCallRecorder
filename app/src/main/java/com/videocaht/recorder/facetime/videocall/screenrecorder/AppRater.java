package com.videocaht.recorder.facetime.videocall.screenrecorder;

import android.content.Context;
import android.content.SharedPreferences;


public class AppRater {
    private final static int DAYS_UNTIL_PROMPT = 3;
    private final static int LAUNCHES_UNTIL_PROMPT = 5;
    //   static Context context;
    boolean feedback = false;
    public static void app_launched(Context mContext)
    {

        // context=mContext;
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false))
        {
            return ;
        }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;

        editor.putLong("launch_count", launch_count);
        // Wait at least n turns before opening dialog
        if (launch_count >=LAUNCHES_UNTIL_PROMPT)
        {
            if(!(prefs.getBoolean("rated",false)))
            {
//               feedback=true;
                editor.putLong("launch_count", 0);
            }

        }

        editor.commit();
    }
    public static boolean getLaunchCount(Context c)
    {
        SharedPreferences prefs = c.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false))
        {

            return false;
        }
        SharedPreferences.Editor editor = prefs.edit();
        long launch_count = prefs.getLong("launch_count", 0);
        if (launch_count >=LAUNCHES_UNTIL_PROMPT)
        {
            if(!(prefs.getBoolean("rated",false)))
            {

                editor.putLong("launch_count", 0);
                editor.commit();
                return true;
            }

        }
        return false;
    }






}