package com.videocaht.recorder.facetime.videocall.screenrecorder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FeedbackDailog extends Activity {

    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_dailog);
        linearLayout = findViewById(R.id.main);
        //  setTitle("Please let us know about your problem");
        //  setTitle("");

    }

    public void close(View view) {

        InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
        finish();
    }

    public void send(View view) {
        String msg = "Please let us know about the problem you faced.\nThanks for your support";
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        finish();
    }


    public void mayBeLater(View view) {
        finish();
    }
    public void submit(View view) {
        String msg = "Please let us know about the problem you faced.\nThanks for your support";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        finish();
    }
}
