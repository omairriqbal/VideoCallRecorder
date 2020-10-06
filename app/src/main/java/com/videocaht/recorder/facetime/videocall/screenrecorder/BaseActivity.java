package com.videocaht.recorder.facetime.videocall.screenrecorder;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;


import com.google.android.material.navigation.NavigationView;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    View contentView;
    DrawerLayout drawerLayout;
    private static final float END_SCALE = 0.7f;

    Toolbar toolbar;
    NavigationView navigationView;
    private ImageView hamburgIcon;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewDeclaration();
//        bottomNavigationWorking();
        customNavigation();

        View headerview = navigationView.getHeaderView(0);
        ImageView headerImg = headerview.findViewById(R.id.navigationHeaderImg);
        headerImg.setOnClickListener((View v) -> { drawerLayout.closeDrawer(Gravity.LEFT); });
    }

    public boolean openDrawer() {
        drawerLayout.openDrawer(Gravity.LEFT);
        return true;
    }
    public boolean closeDrawer() {
        drawerLayout.closeDrawer(Gravity.LEFT);
        return true;
    }



    public void toolbar(String text) {
        toolbar.setTitle(text);
    }

    private void customNavigation() {
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                                           @Override
                                           public void onDrawerSlide(View drawerView, float slideOffset) {

                                               // Scale the View based on current slide offset
                                               final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                                               final float offsetScale = 1 - diffScaledOffset;
                                               contentView.setScaleX(offsetScale);
                                               contentView.setScaleY(offsetScale);

                                               // Translate the View, accounting for the scaled width
                                               final float xOffset = drawerView.getWidth() * slideOffset;
                                               final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                                               final float xTranslation = xOffset - xOffsetDiff;
                                               contentView.setTranslationX(xTranslation);
                                           }

                                           @Override
                                           public void onDrawerClosed(View drawerView) {
                                           }
                                       }
        );
    }

    private void viewDeclaration() {
        toolbar = findViewById(R.id.toolbar);
        toolbar("");
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        contentView = findViewById(R.id.content);
//        bottomNavigation = findViewById(R.id.bottom_navigation);
        hamburgIcon = findViewById(R.id.hamburgIcon);

        hamburgIcon.setOnClickListener((View v)->{
            if (drawerLayout.isDrawerOpen(Gravity.LEFT))
                drawerLayout.closeDrawer(Gravity.LEFT);
            else
                openDrawer();
        });

        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navigationView.setBackground(null);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigationMenuSettings:
                startActivity(new Intent(this,Acc_Setting.class));
                break;

            case R.id.navigationMenuRate:
                startActivity(new Intent(this,RateUs.class));
                break;
            case R.id.navigationMenuFeedback:
                getFeedBackDialog();
                break;
            case R.id.navigationMenuShare:
                if (drawerLayout.isDrawerOpen(Gravity.LEFT))
                    drawerLayout.closeDrawer(Gravity.LEFT);

                shareUrl();
            case R.id.navigationMenuRemoveAd:
                if (drawerLayout.isDrawerOpen(Gravity.LEFT))
                    drawerLayout.closeDrawer(Gravity.LEFT);
                startActivity( new Intent(this, buy_panel.class));

                break;

            case R.id.navigationMenuMore:
                if (drawerLayout.isDrawerOpen(Gravity.LEFT))
                    drawerLayout.closeDrawer(Gravity.LEFT);

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Global+Apps+Technology"));
                startActivity(browserIntent);

                break;
        }
        return true;
    }

    private void shareUrl() {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String app_url = " https://play.google.com/store/apps/details?id=" + this.getPackageName();
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at \n\n" + app_url);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "WhatsApp Status Saver");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT))
            drawerLayout.closeDrawer(Gravity.LEFT);
        else
            getExitDailog();
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

    private void getExitDailog() {

        final AlertDialog.Builder textBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.exit_dailog, null);

        TextView yes = view.findViewById(R.id.feedbackSubmit);
        TextView cancel = view.findViewById(R.id.feedbackCancel);

        yes.setOnClickListener((View v) ->{
            dialog.cancel();  });


        cancel.setOnClickListener((View v) ->{

           finish(); });

        textBuilder.setView(view);
        dialog = textBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(500, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
    }
}