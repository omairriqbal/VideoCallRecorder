package com.videocaht.recorder.facetime.videocall.screenrecorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

public class RateUs extends Activity {
    ScaleRatingBar ratingBar;
    float rate = 0;
    TextView btnSend ,btnClose;
    ImageView imageView ,smileyImg;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_us);
        getView();
        getOnClicks();


    }

    private void getOnClicks() {

        ratingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, float rating, boolean fromUser) {

                rate = rating;

                if (rating == 1) {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.smiley1));
                } else if (rating == 2) {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.smiley2));
                } else if (rating == 3) {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.smiley3));
                } else if (rating == 4) {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.smiley4));
                } else if (rating == 5) {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.smiley5));
                }
            }
        });

        btnSend.setOnClickListener((View v) -> {

            if (rate < 4) {
//                finish();
                getFeedBackDialog();

            } else {
                getRateUsDialog();

            }

        });
        btnClose.setOnClickListener((View v)->{
            finish();
        });
    }

    private void getRateUsDialog() {

        final AlertDialog.Builder textBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.playstore_rating, null);

        TextView yes = view.findViewById(R.id.feedbackSubmit);
        TextView cancel = view.findViewById(R.id.feedbackCancel);

        yes.setOnClickListener((View v) ->{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));

            finish(); });

        cancel.setOnClickListener((View v) ->{

            dialog.cancel();  });

        textBuilder.setView(view);
        dialog = textBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(500, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
    }

    private void getFeedBackDialog() {

        final AlertDialog.Builder textBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.feedback_dailog, null);

        TextView yes = view.findViewById(R.id.feedbackSubmit);
        TextView cancel = view.findViewById(R.id.feedbackCancel);

        yes.setOnClickListener((View v) ->{
            String msg = "Thanks for your support";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            finish(); });

        cancel.setOnClickListener((View v) ->{
            InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            dialog.cancel();  });

        textBuilder.setView(view);
        dialog = textBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(500, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
    }

    private void getView() {
        imageView = findViewById(R.id.smileyImage);

        ratingBar = findViewById(R.id.simpleRatingBar);

        btnSend = findViewById(R.id.rateSubmit);
        btnClose = findViewById(R.id.rateCancel);
    }


}
