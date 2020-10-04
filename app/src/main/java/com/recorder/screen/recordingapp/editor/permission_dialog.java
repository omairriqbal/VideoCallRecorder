package com.recorder.screen.recordingapp.editor;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

/**
 * Created by jolta on 2/14/2018.
 */

public class permission_dialog extends AppCompatActivity {
    String accountName;
    String pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission_dialog);

    }
    public void checkPermission()
    {
        Context context=getApplicationContext();
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M)
        {
            if(      (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    //  ||(ContextCompat.checkSelfPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED)
                    ||(ContextCompat.checkSelfPermission(context,Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED)
                    ||(ContextCompat.checkSelfPermission(context,android.Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)
            )
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(permission_dialog.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    androidx.appcompat.app.AlertDialog.Builder alertBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Allow \"Screen Recorder \" to access storage");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which)
                        {
                            ActivityCompat.requestPermissions(permission_dialog.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.VIBRATE }, 1);
                        }
                    });
                    androidx.appcompat.app.AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {

                    ActivityCompat.requestPermissions(permission_dialog.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.VIBRATE }, 1);
                }
            }

        }


    }
    public void close(View view)
    {
        finish();
    }

    public void send(View view)
    {
checkPermission();
    /*    String name=getResources().getString(R.string.app_name);
        EditText t1=(EditText)findViewById(R.id.ff);
        try {
            GMailSender sender = new GMailSender("maria.kinley1@gmail.com", "maria4321");
            sender.sendMail(name,
                    t1.getText().toString(),
                    "maria.kinley1@gmail.com",
                    "maria.kinley1@gmail.com");
        } catch (Exception e) {
            Log.e("mylog", "Error: " + e.getMessage());
        }*/
        finish();
    }

}
