package com.recorder.screen.recordingapp.editor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



import java.util.ArrayList;
import java.util.Calendar;

import com.recorder.screen.recordingapp.editor.R;

public class buy_panel extends Activity
{
    com.ebookfrenzy.inappbilling.util.IabHelper mHelper;
    private static final String TAG = "com.example.inappbilling";
    static final String day = "removeads_oneday";
    static final String day_3 = "removeads_3day";
    static final String day_15 = "removeads_10days";
    static final String day_30 = "removeads_30days";
    static final String months = "removeads_6months";
    static final String forever = "removeads_lifetime";
      String current="";
    com.ebookfrenzy.inappbilling.util.IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;
    com.ebookfrenzy.inappbilling.util.IabHelper.OnConsumeFinishedListener mConsumeFinishedListener;
    private boolean buy;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_panel);
        In_app();


    }
    public void close(View view)
    {
        finish();
    }
    public void ok(View view)
    {
      // consumeItem();
       finish();
    }
    public void buyClick_day(View view)
    {

        try {
            mHelper.launchPurchaseFlow(this, day, 10001,
                    mPurchaseFinishedListener, "remove_ad");
        } catch (com.ebookfrenzy.inappbilling.util.IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }
    public void buyClick_day_3(View view)
    {

        try {
            mHelper.launchPurchaseFlow(this, day_3, 10001,
                    mPurchaseFinishedListener, "remove_ad");
        } catch (com.ebookfrenzy.inappbilling.util.IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }
    public void buyClick_day15(View view)
    {

        try {
            mHelper.launchPurchaseFlow(this, day_15, 10001,
                    mPurchaseFinishedListener, "remove_ad");
        } catch (com.ebookfrenzy.inappbilling.util.IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }
    public void buyClick_month(View view)
    {

        try {
            mHelper.launchPurchaseFlow(this, day_30, 10001,
                    mPurchaseFinishedListener, "remove_ad");
        } catch (com.ebookfrenzy.inappbilling.util.IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }
    public void buyClick_months(View view)
    {

        try {
            mHelper.launchPurchaseFlow(this, months, 10001,
                    mPurchaseFinishedListener, "remove_ad");
        } catch (com.ebookfrenzy.inappbilling.util.IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }
    public void buyClick_forever(View view)
    {

        try {
            mHelper.launchPurchaseFlow(this, forever, 10001,
                    mPurchaseFinishedListener, "remove_ad");
        } catch (com.ebookfrenzy.inappbilling.util.IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }
    public void In_app()
    {
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiq5nMw07E5BwNzPLFISymHqEwQmWs71vd7l17h0RB9XpbJ2V6eJ490EOO6oEUwklPotc7i+e2N1d5q1Dr45vXpgQjU+yKxtYa+TqciOumD+KJ5vcsPjRwPZdB8Zp5z0rw627EG3Xb6gRN+JYrd/BXC1XaS0jadfk6Y2g/v1RytJb9q9kSaRaEqqfmyEH05/wJ9bvRsmhDYxSwNfocOOXbAVb+MDdtaFyLtB4wBvNYz7a54IRe/KoyuNaZcgc0PBdoI+Wkzeg0Hs+KBgS7vlYCg/u+S4KEG+t/1GbChZgUlv/uix1+z6i/8/Pdo34Hxria0zl46TDrydTJMlYPt26xQIDAQAB";
        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new com.ebookfrenzy.inappbilling.util.IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new com.ebookfrenzy.inappbilling.util.IabHelper.OnIabSetupFinishedListener()
        {
            public void onIabSetupFinished(com.ebookfrenzy.inappbilling.util.IabResult result)
            {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    Log.d("TAG", "Problem setting up In-app Billing: " + result);
                    Toast.makeText(buy_panel.this,"Not Avaliable Now. Try again later", Toast.LENGTH_SHORT).show();
                    finish();
                }
                // Hooray, IAB is fully set up!
                ArrayList skuu=new ArrayList();
                skuu.add(day);
                skuu.add(day_3);
                skuu.add(day_15);
                skuu.add(day_30);
                skuu.add(months);
                skuu.add(forever);
                try
                {
                    mHelper.queryInventoryAsync(true, skuu, null, mReceivedInventoryListener);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                ProgressBar pb=findViewById(R.id.pb);
                pb.setVisibility(View.GONE);
            }
        });
        mPurchaseFinishedListener = new com.ebookfrenzy.inappbilling.util.IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(com.ebookfrenzy.inappbilling.util.IabResult result,
                                              com.ebookfrenzy.inappbilling.util.Purchase purchase) {
                if (result.isFailure())
                {
                    ////  Toast.makeText(menu.this, result.getMessage(), Toast.LENGTH_SHORT).show();

                    // Handle error
                    return;
                }
                else if (purchase.getSku().equals(day))
                {
                    current=day;
                    consumeItem();

                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("ads", 0);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("buy", true);
                    editor.putLong("Ad_time", Calendar.getInstance().getTimeInMillis());
                    editor.putInt("duration",1);
                    editor.commit();
                }
                else if (purchase.getSku().equals(day_3))
                {
                    current=day_3;
                    consumeItem();

                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("ads", 0);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("buy", true);
                    editor.putLong("Ad_time", Calendar.getInstance().getTimeInMillis());
                    editor.putInt("duration",3);
                    editor.commit();
                }
                else if (purchase.getSku().equals(day_15))
                {
                    current=day_15;
                    consumeItem();

                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("ads", 0);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("buy", true);
                    editor.putLong("Ad_time", Calendar.getInstance().getTimeInMillis());
                    editor.putInt("duration",10);
                    buy = true;
                    editor.commit();
                }
                else if (purchase.getSku().equals(day_30))
                {
                    current=day_30;
                    consumeItem();

                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("ads", 0);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("buy", true);
                    editor.putLong("Ad_time", Calendar.getInstance().getTimeInMillis());
                    editor.putInt("duration",30);
                    buy = true;
                    editor.commit();
                }
                else if (purchase.getSku().equals(months))
                {
                    current=months;
                    consumeItem();
                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("ads", 0);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("buy", true);
                    editor.putLong("Ad_time", Calendar.getInstance().getTimeInMillis());
                    editor.putInt("duration",6);
                    buy = true;
                    editor.commit();
                }
                else if (purchase.getSku().equals(forever))
                {
                    current=forever;
                    consumeItem();
                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("ads", 0);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("buy", true);
                    editor.putBoolean("forever",true);
                    buy = true;
                    editor.commit();
                }

            }
        };
        mConsumeFinishedListener =
                new com.ebookfrenzy.inappbilling.util.IabHelper.OnConsumeFinishedListener()
                {
                    public void onConsumeFinished(com.ebookfrenzy.inappbilling.util.Purchase purchase,
                                                  com.ebookfrenzy.inappbilling.util.IabResult result) {

                        if (result.isSuccess()) {

                        } else {
                            // handle error
                        }
                    }
                };
    }

    public void consumeItem()
    {
        try {
            mHelper.queryInventoryAsync(mReceivedInventoryListener);
        } catch (com.ebookfrenzy.inappbilling.util.IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }


    com.ebookfrenzy.inappbilling.util.IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new com.ebookfrenzy.inappbilling.util.IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(com.ebookfrenzy.inappbilling.util.IabResult result,
                                             com.ebookfrenzy.inappbilling.util.Inventory inventory)
        {


            if (result.isFailure()) {
                // Handle failure
            }
            else {
                if(current==day)
                {
                    try {
                        mHelper.consumeAsync(inventory.getPurchase(day),
                                mConsumeFinishedListener);
                    } catch (com.ebookfrenzy.inappbilling.util.IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                }
                if(current==day_3)
                {
                    try {
                        mHelper.consumeAsync(inventory.getPurchase(day_3),
                                mConsumeFinishedListener);
                    } catch (com.ebookfrenzy.inappbilling.util.IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                }
                else
                if(current==day_15)
                {
                    try {
                        mHelper.consumeAsync(inventory.getPurchase(day_15),
                                mConsumeFinishedListener);
                    } catch (com.ebookfrenzy.inappbilling.util.IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                }
                else
                if(current==day_30) {
                    try {
                        mHelper.consumeAsync(inventory.getPurchase(day_30),
                                mConsumeFinishedListener);
                    } catch (com.ebookfrenzy.inappbilling.util.IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                }
                else
                if(current==months) {
                    try {
                        mHelper.consumeAsync(inventory.getPurchase(months),
                                mConsumeFinishedListener);
                    } catch (com.ebookfrenzy.inappbilling.util.IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                }
                else
                if(current==forever) {
                    try {
                        mHelper.consumeAsync(inventory.getPurchase(forever),
                                mConsumeFinishedListener);
                    } catch (com.ebookfrenzy.inappbilling.util.IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                }

            }
            try
            {
                String skuPrice = inventory.getSkuDetails(day).getPrice();
                ((TextView)findViewById(R.id.text1)).setText(skuPrice);
                String skuPrice2 = inventory.getSkuDetails(day_3).getPrice();
                ((TextView)findViewById(R.id.text2)).setText(skuPrice2);
                String skuPrice3= inventory.getSkuDetails(day_15).getPrice();
                ((TextView)findViewById(R.id.text3)).setText(skuPrice3);
                String skuPrice4= inventory.getSkuDetails(day_30).getPrice();
                ((TextView)findViewById(R.id.text4)).setText(skuPrice4);
                String skuPrice5= inventory.getSkuDetails(months).getPrice();
                ((TextView)findViewById(R.id.text5)).setText(skuPrice5);
                String skuPrice6= inventory.getSkuDetails(forever).getPrice();
                ((TextView)findViewById(R.id.text6)).setText(skuPrice6);

                String[] tt;
                String sku1 = inventory.getSkuDetails(day).getTitle();
                tt=sku1.split("\\(");
                ((TextView)findViewById(R.id.title1)).setText(tt[0]);

                String sku2 = inventory.getSkuDetails(day_3).getTitle();
                tt=sku2.split("\\(");
                ((TextView)findViewById(R.id.title2)).setText(tt[0]);
                String sku3= inventory.getSkuDetails(day_15).getTitle();
                tt=sku3.split("\\(");
                ((TextView)findViewById(R.id.title3)).setText(tt[0]);
                String sku4= inventory.getSkuDetails(day_30).getTitle();
                tt=sku4.split("\\(");
                ((TextView)findViewById(R.id.title4)).setText(tt[0]);
                 String sku5= inventory.getSkuDetails(months).getTitle();
                tt=sku5.split("\\(");
                ((TextView)findViewById(R.id.title5)).setText(tt[0]);
                String sku6= inventory.getSkuDetails(forever).getTitle();
                tt=sku6.split("\\(");
                ((TextView)findViewById(R.id.title6)).setText(tt[0]);

            }
            catch (Exception e)
            {
                String msg=e.getMessage();
                Toast.makeText(buy_panel.this,"Not Avaliable Now. Try again later", Toast.LENGTH_SHORT).show();
                finish();

            }

        }
    };
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
