package com.recorder.screen.recordingapp.editor;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Created by jolta on 2/14/2018.
 */

public class dialogg extends AppCompatActivity {
    String accountName;
    String pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dd);
      accountName=getIntent().getStringExtra("sender");
    }

    public void close(View view)
    {
        finish();
    }

    public void send(View view)
    {
        /*
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto: qualityappsteam@gmail.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, t1.getText());
        startActivity(Intent.createChooser(intent, "Send Feedback"));
        finish();*/
        String name=getResources().getString(R.string.app_name);
        EditText t1=(EditText)findViewById(R.id.ff);
        try {
            GMailSender sender = new GMailSender("maria.kinley1@gmail.com", "maria4321");
            sender.sendMail(name,
                    t1.getText().toString(),
                    "maria.kinley1@gmail.com",
                    "maria.kinley1@gmail.com");
        } catch (Exception e) {
            Log.e("mylog", "Error: " + e.getMessage());
        }
        finish();
    }

}
