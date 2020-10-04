package com.recorder.screen.recordingapp.editor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
                Toast.makeText(this, "gallery", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

}
