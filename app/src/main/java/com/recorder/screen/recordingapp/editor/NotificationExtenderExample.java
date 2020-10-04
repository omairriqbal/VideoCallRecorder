package com.recorder.screen.recordingapp.editor;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class NotificationExtenderExample extends NotificationExtenderService
{

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult)
    {

try
{
    createNotification(receivedResult.payload.bigPicture,receivedResult.payload.largeIcon,receivedResult.payload.launchURL,
            receivedResult.payload.title,receivedResult.payload.body);

}catch (Exception e)
{

}

        return true;
    }

    RemoteViews notificationView;
    RemoteViews notificationView_samll;
 /*   private  NotificationCompat.Builder createNotification(String image,String icon,String link,String title,String text)
    {

        Context cc=getApplicationContext();
        Intent view =new Intent(Intent.ACTION_VIEW, Uri.parse(link));

        PendingIntent pendingIntent = PendingIntent.getActivity(cc, 0, view, FLAG_UPDATE_CURRENT);
        notificationView = new RemoteViews(cc.getPackageName(), R.layout.server_notification_layout);
        notificationView_samll = new RemoteViews(cc.getPackageName(), R.layout.server_notification_layout_small);

        try
        {
            Bitmap bitmap=getBitmapfromUrl(image);
            notificationView.setImageViewBitmap(R.id.thumbnail,bitmap);

        }catch (Exception e)
        {
            String msg=e.getMessage();

        }
        try
        {
            Bitmap bitmap=getBitmapfromUrl(icon);
            notificationView.setImageViewBitmap(R.id.icon,bitmap);
            notificationView_samll.setImageViewBitmap(R.id.icon,bitmap);
        }
        catch (Exception e)
        {
            String msg=e.getMessage();
        }

        notificationView.setTextViewText(R.id.title_vid,text);
        notificationView_samll.setTextViewText(R.id.title_vid,text);

        notificationView.setTextViewText(R.id.title,title);
        notificationView_samll.setTextViewText(R.id.title,title);

        NotificationManager notificationManager = (NotificationManager) cc.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            notificationManager = (NotificationManager) cc.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = null;
            channel = new NotificationChannel("default",
                    "default",
                    NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription("Channel description");
            notificationManager.createNotificationChannel(channel);

        }
        NotificationCompat.Builder myNotification;
        myNotification =   new NotificationCompat.Builder(getApplicationContext())
                .setCustomContentView(notificationView)
                .setCustomHeadsUpContentView(notificationView_samll)
                .setCustomBigContentView(notificationView)
                .setAutoCancel(true);
        //Notification myNotification;
*//*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                myNotification = new Notification.Builder(cc, "default")
                        .setSmallIcon(R.drawable.scan)
                        .setTicker("New Notification")
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)

                        .setAutoCancel(true).build();

            }
            else {
                myNotification = new Notification.Builder(cc)
                        .setSmallIcon(R.drawable.scan)
                        .setTicker("New Notification")
                        .setContentIntent(pendingIntent)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setAutoCancel(true).build();
            }

            myNotification.bigContentView = notificationView;
            myNotification.contentView = notificationView_samll;*//*

          *//*  notificationView.setOnClickPendingIntent(R.id.main_layout, pendingIntent);
            notificationView_samll.setOnClickPendingIntent(R.id.main_layout, pendingIntent);*//*
          *//*  Random random = new Random();
            notificationManager.notify(random.nextInt(), myNotification);*//*
        return  myNotification;

        }*/


    private void createNotification(String image, String icon, String link, String title, String text)
    {

        Context cc=getApplicationContext();
        Intent view =new Intent(Intent.ACTION_VIEW, Uri.parse(link));

        PendingIntent pendingIntent = PendingIntent.getActivity(cc, 0, view, FLAG_UPDATE_CURRENT);
        notificationView = new RemoteViews(cc.getPackageName(), R.layout.server_notification_layout);
        notificationView_samll = new RemoteViews(cc.getPackageName(), R.layout.server_notification_layout_small);

        try
        {
            if(image!=null)
            {
                Bitmap bitmap=getBitmapfromUrl(image);
                notificationView.setImageViewBitmap(R.id.thumbnail,bitmap);
            }


        }catch (Exception e)
        {
            String msg=e.getMessage();
        }
        try
        {
            if(icon!=null)
            {
                Bitmap bitmap=getBitmapfromUrl(icon);
                notificationView.setImageViewBitmap(R.id.icon,bitmap);
                notificationView_samll.setImageViewBitmap(R.id.icon,bitmap);
            }
            else
            {
                notificationView_samll.setImageViewResource(R.id.icon,R.mipmap.ic_launcher);
            }

        }
        catch (Exception e)
        {
            String msg=e.getMessage();
        }

        if(text!=null) {
            notificationView.setTextViewText(R.id.title_vid, text);
            notificationView_samll.setTextViewText(R.id.title_vid,text);
        }
        else
        {
            notificationView.setTextViewText(R.id.title_vid,getString(R.string.Description_text));
            notificationView_samll.setTextViewText(R.id.title_vid,getString(R.string.Description_text));
        }



if(title!=null) {
    notificationView.setTextViewText(R.id.title, title);
    notificationView_samll.setTextViewText(R.id.title, title);
}
else
{
    notificationView.setTextViewText(R.id.title,getString(R.string.app_name));
    notificationView_samll.setTextViewText(R.id.title,getString(R.string.app_name));
}
        NotificationManager notificationManager = (NotificationManager) cc.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            notificationManager = (NotificationManager) cc.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = null;
            channel = new NotificationChannel("default",
                    "default",
                    NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription("Channel description");
            notificationManager.createNotificationChannel(channel);

        }

        Notification myNotification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                myNotification = new Notification.Builder(cc, "default")
                        .setSmallIcon(R.drawable.ic_notifications)
                        .setTicker(title)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true).build();

            }
            else {
                myNotification = new Notification.Builder(cc)
                        .setSmallIcon(R.drawable.ic_notifications)
                        .setTicker(title)
                        .setContentIntent(pendingIntent)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setAutoCancel(true).build();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                myNotification.headsUpContentView=notificationView_samll;
            }
            if(image!=null)
            myNotification.bigContentView = notificationView;

            myNotification.contentView = notificationView_samll;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                myNotification.headsUpContentView=notificationView_samll;
            }

          /*  notificationView.setOnClickPendingIntent(R.id.main_layout, pendingIntent);
            notificationView_samll.setOnClickPendingIntent(R.id.main_layout, pendingIntent);*/
            Random random = new Random();
            notificationManager.notify(random.nextInt(), myNotification);

        }
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }}
