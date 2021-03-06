package com.videocaht.recorder.facetime.videocall.screenrecorder;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import static com.videocaht.recorder.facetime.videocall.screenrecorder.PermissionActivity.ACTION_PERMISSION;

public class FirstActivity extends AppCompatActivity {

    CheckBox privacyCheck;
    RelativeLayout firstTimeLayout;
    ImageView enableButton,disableButton;
    SharedPreferences preferences;
    private boolean previouslyStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        if (DataProvider.getInstance().read_data_remainig && !DataProvider.getInstance().load_request_send) {
            DataProvider.getInstance().on_complete();
        }

        enableButton = findViewById(R.id.enable_startup_button);
        disableButton = findViewById(R.id.disable_startup_button);

        firstTimeLayout = findViewById(R.id.firstLayout);

        privacyCheck = findViewById(R.id.privacyCheck);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        previouslyStarted = preferences.getBoolean("previously_started", false);

        if(!previouslyStarted) {
            firstTimeLayout.setVisibility(View.VISIBLE);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(FirstActivity.this,main_menu.class));
                    finish();
                }

            }, 2000);

        }


        SpannableString ss = new SpannableString("I agree to Privacy Policy of this app.");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
               startActivity(new Intent(FirstActivity.this, PrivacyPolicy.class));

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 11, 25, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        privacyCheck.setText(ss);
        privacyCheck.setMovementMethod(LinkMovementMethod.getInstance());

        privacyCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (privacyCheck.isChecked()) {
                    enableButton.setVisibility(View.VISIBLE);
                    disableButton.setVisibility(View.GONE);
                } else {
                    disableButton.setVisibility(View.VISIBLE);
                    enableButton.setVisibility(View.GONE);
                }
            }
        });

        disableButton.setOnClickListener((View v) ->{
            Snackbar snackbar = Snackbar
                    .make(v, "Please accept the privacy policy to proceed.", Snackbar.LENGTH_LONG);
            snackbar.show();
        });

        enableButton.setOnClickListener((View v) ->{
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean("previously_started", true);

            edit.commit();

            startActivity(new Intent(this,main_menu.class));
            finish();

        });

    }

}