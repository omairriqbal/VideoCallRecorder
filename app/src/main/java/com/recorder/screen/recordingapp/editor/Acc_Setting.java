package com.recorder.screen.recordingapp.editor;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codekidlabs.storagechooser.Content;
import com.codekidlabs.storagechooser.StorageChooser;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.facebook.ads.AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CALLBACK_MODE;



/**
 * Created by umair on 02/10/2020.
 */

public class Acc_Setting extends AppCompatActivity
{
    ListView lv,lv1,lv2;
    RadioGroup r1,r2,r3,r4;
    TextView title1;
    Button ok,ok2,ok3;
    String min[];
    dataListAdapter adapter;
    String choice,delay,Tfrom,Tto;
    TextView t1,t2;
    int pos,dd;
    String formate="";
    boolean refresh=false,list=false,close_app,showWidget,watermak,thmem=false;
    int time[];
    TextView from,to;

    private AdView mAdView;
    private static final int REQUEST_INVITE =2 ;
    private View parentLayout;
    private PopupWindow popupWindow;
    int choice_vid,choice_img;
    private Dialog dialog;
    private int timmer;
    boolean buy=main_menu.buy;
    ImageView ivback;

    private boolean add1;

    static boolean  add2=false;

    ArrayList<String> strings=new ArrayList<>();

    private StorageChooser chooser;
    private StorageChooser.Builder builder = new StorageChooser.Builder();
    private static final int SDCARD_PERMISSION = 1;
    private TextView sec_text;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        checkStoragePermission();
        openDirectory();
        parentLayout = (RelativeLayout) findViewById(R.id.parent);

        ivback = findViewById(R.id.backarrow);
        lv=(ListView)findViewById(R.id.list);

        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //setTitle("Settings");
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        strings.add("Change Video Format");
        strings.add("Change Picture Format");
        strings.add("Hide widget when Recording");

        strings.add("Not close app when you exit the floating window");
        strings.add("Show countdown in recorded videos");
        strings.add("Path");

        Locale current = getResources().getConfiguration().locale;
        languagepair="en-"+current.getLanguage();
        if(!current.getLanguage().equals("en"))
            get_Transalations();


        SharedPreferences myPrefs = getSharedPreferences("Voice", 0);
        final SharedPreferences.Editor myPrefsEdit = myPrefs.edit();
        dd=myPrefs.getInt("theme",0);
        adapter=new dataListAdapter(strings);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {

                if(i==0)
                { settings_video(); }
                else if(i==1)
                { settings_Picture(); }
                else if(i==4)
                { settings_timmer();}
                else if(i==5)
                {
                    //select storage
                    Toast.makeText(Acc_Setting.this, "library", Toast.LENGTH_SHORT).show();
                    directoryChooser();
//                    storage_path();
                }
                else if(i==6)
                { showWarning(Acc_Setting.this,getPackageName()); }

            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    int selected = 0;
    public void settings_Picture()
    {

        dialog = new Dialog(Acc_Setting.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog .setCancelable(false);
        dialog .setContentView(R.layout.filetypes_picture);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

    /*    if(translated_string.containsKey("select_format"))
            ((TextView)dialog.findViewById(R.id.select_formate)).setText(translated_string.get("select_format").toString());*/

        (dialog.findViewById(R.id.close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        SharedPreferences settings1= getSharedPreferences("MY_PREF",0);
        int ch_img=settings1.getInt("img formate",1);
        selected=ch_img;
        if(ch_img==1)
        {
            RadioButton r1=(RadioButton) dialog.findViewById(R.id.format_png);
            r1.setChecked(true);
        }
        if(ch_img==2)
        {
            RadioButton r1=(RadioButton) dialog.findViewById(R.id.format_jpg);
            r1.setChecked(true);
        }
        RadioGroup img=(RadioGroup) dialog.findViewById(R.id.image);

        img.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {

            public void onCheckedChanged(RadioGroup buttonView, int ans) {
                // update your model (or other business logic) based on isChecked
                switch(ans)
                {
                    case R.id.format_png:
                        selected=1;
                        //   Toast.makeText(getApplicationContext(),""+choice1,Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.format_jpg:
                        selected=2;
                        //  Toast.makeText(getApplicationContext(),""+choice2,Toast.LENGTH_SHORT).show();

                        break;

                }
                SharedPreferences settings1= getSharedPreferences("MY_PREF",0);
                SharedPreferences.Editor editor1= settings1.edit();
                editor1.putInt("img formate",selected);
                editor1.apply();
                dialog.dismiss();
                adapter.notifyDataSetChanged();


            }
        });


    }
    public boolean warning()
    {
        final boolean[] ans = {false};
        dialog = new Dialog(Acc_Setting.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog .setCancelable(false);
        dialog .setContentView(R.layout.warning);

        if(translated_string.containsKey("warn_1"))
            ((TextView)dialog.findViewById(R.id.warning_text)).setText(translated_string.get("warn_1").toString());

        if(translated_string.containsKey("warn_2"))
            ((TextView)dialog.findViewById(R.id.warning_2)).setText(translated_string.get("warn_2").toString());

        if(translated_string.containsKey("cancel"))
            ((Button)dialog.findViewById(R.id.cancel)).setText(translated_string.get("cancel").toString());

        if(translated_string.containsKey("select"))
            ((Button)dialog.findViewById(R.id.select)).setText(translated_string.get("select").toString());




        dialog.show();

        (dialog.findViewById(R.id.close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        (dialog.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        return ans[0];

    }
    public void settings_video()
    {
        dialog = new Dialog(Acc_Setting.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog .setCancelable(false);
        dialog .setContentView(R.layout.filetypes_vid);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

       /* if(translated_string.containsKey("select_format"))
            ((TextView)dialog.findViewById(R.id.select_formate)).setText(translated_string.get("select_format").toString());

*/

        dialog.show();
        (dialog.findViewById(R.id.close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        SharedPreferences settings1= getSharedPreferences("MY_PREF",0);
        int ch=settings1.getInt("video formate",1);
        selected=ch;
        if(ch==1)
        {
            RadioButton r1=(RadioButton) dialog.findViewById(R.id.format_mp4);
            r1.setChecked(true);
        }
        if(ch==2)
        {
            RadioButton r1=(RadioButton) dialog.findViewById(R.id.format_3gp);
            r1.setChecked(true);
        }

        RadioGroup rr=(RadioGroup) dialog.findViewById(R.id.video);
        rr.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {

            public void onCheckedChanged(RadioGroup buttonView, int ans) {
                // update your model (or other business logic) based on isChecked
                switch(ans)
                {
                    case R.id.format_mp4:
                        selected=1;
                        break;

                    case R.id.format_3gp:
                        selected=2;
                        break;

                }

                SharedPreferences settings= getSharedPreferences("MY_PREF",0);
                SharedPreferences.Editor editor= settings.edit();
                editor.putInt("video formate",selected);
                editor.commit();
                dialog.dismiss();
                adapter.notifyDataSetChanged();

            }
        });
    }
    public void settings_timmer()
    {
        dialog = new Dialog(Acc_Setting.this);
        dialog .setCancelable(false);
        dialog .setContentView(R.layout.timmer);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        if(translated_string.containsKey("countdown"))
            ((TextView)dialog.findViewById(R.id.countdown)).setText(translated_string.get("countdown").toString());

        dialog.show();
        (dialog.findViewById(R.id.close)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        SharedPreferences settings1= getSharedPreferences("MY_PREF",0);
        int ch=settings1.getInt("timmer",3);
        selected=ch;
        if(ch==3)
        {
            RadioButton r1=(RadioButton) dialog.findViewById(R.id.s_3);
            r1.setChecked(true);
        }
        if(ch==5)
        {
            RadioButton r1=(RadioButton) dialog.findViewById(R.id.s_5);
            r1.setChecked(true);
        }
        if(ch==10)
        {
            RadioButton r1=(RadioButton) dialog.findViewById(R.id.s_10);
            r1.setChecked(true);
        }
        if(ch==0)
        {
            RadioButton r1=(RadioButton) dialog.findViewById(R.id.s_0);
            r1.setChecked(true);
        }


        RadioGroup rr=(RadioGroup) dialog.findViewById(R.id.timmer);
        rr.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {

            public void onCheckedChanged(RadioGroup buttonView, int ans) {
                // update your model (or other business logic) based on isChecked
                switch(ans)
                {
                    case R.id.s_0:
                        selected=0;
                        break;

                    case R.id.s_3:
                        selected=3;
                        break;

                    case R.id.s_5:
                        selected=5;
                        break;
                    case R.id.s_10:
                        selected=10;
                        break;

                }

                SharedPreferences settings= getSharedPreferences("MY_PREF",0);
                SharedPreferences.Editor editor= settings.edit();
                editor.putInt("timmer",selected);
                editor.commit();
                dialog.dismiss();
                adapter.notifyDataSetChanged();

            }
        });
    }
   /* public void storage_path() {

        dialog = new Dialog(Acc_Setting.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog .setCancelable(false);
        dialog .setContentView(R.layout.select_path);

        if(translated_string.containsKey("storage_location"))
            ((TextView)dialog.findViewById(R.id.storage_location)).setText(translated_string.get("storage_location").toString());

        dialog.show();



        boolean isSDPresent=false;
        File f1 = new File("/storage/");
        String listOfStorages[]= f1.list();
        if(listOfStorages[1].contains("emulated")||listOfStorages[0].contains("-"))
        {
            isSDPresent=true;

        }
        // if(isSDSupportedDevice && isSDPresent)
        if( isSDPresent)
        {
            // yes SD-card is present
        }
        else
        {
            // Sorry
            RadioButton r1=(RadioButton) dialog.findViewById(R.id.sd);
            r1.setEnabled(false);
            r1.setText("No SD Card");
        }
        (dialog.findViewById(R.id.close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        SharedPreferences settings1= getSharedPreferences("MY_PREF",0);
        int ch=settings1.getInt("storage path",1);
        selected=ch;
        if(ch==1)
        {
            RadioButton r1=(RadioButton) dialog.findViewById(R.id.internal);

            r1.setChecked(true);
        }
        if(ch==2)
        {
            RadioButton r1=(RadioButton) dialog.findViewById(R.id.sd);
            r1.setChecked(true);
        }

        RadioButton r1=(RadioButton) dialog.findViewById(R.id.internal);
        RadioButton r2=(RadioButton) dialog.findViewById(R.id.sd);

        if(translated_string.containsKey("internal"))
            r1.setText(translated_string.get("internal").toString());

        if(translated_string.containsKey("sd"))
            r2.setText(translated_string.get("sd").toString());




        RadioGroup rr=(RadioGroup) ((Dialog) dialog).findViewById(R.id.path);
        rr.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {

            public void onCheckedChanged(RadioGroup buttonView, int ans) {
                // update your model (or other business logic) based on isChecked
                switch(ans)
                {
                    case R.id.internal:
                        selected=1;
                        SharedPreferences settings= getSharedPreferences("MY_PREF",0);
                        SharedPreferences.Editor editor= settings.edit();
                        editor.putInt("storage path",selected);
                        editor.commit();
                        dialog.dismiss();
                        adapter.notifyDataSetChanged();
                        break;

                    case R.id.sd:
                        selected=2;
                        dialog.dismiss();
                        warning();
                        break;

                }



            }
        });
    }*/

    void checkStoragePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //Write permission is required so that folder picker can create new folder.
            //If you just want to pick files, Read permission is enough.

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        SDCARD_PERMISSION);
            }
        }

    }

    private void openDirectory() {
        Content c = new Content();
        c.setCreateLabel("Create");
        c.setInternalStorageText("My Storage");
        c.setCancelLabel("Cancel");
        c.setSelectLabel("Select");
        c.setOverviewHeading("Choose Drive");
        builder.withActivity(this)
                .withFragmentManager(getFragmentManager())
                .setMemoryBarHeight(0.5f)
//                .disableMultiSelect()
                .withContent(c);

        builder.allowAddFolder(true);
        builder.allowCustomPath(true);
        builder.setType(StorageChooser.DIRECTORY_CHOOSER);
    }

    public void directoryChooser() {
        chooser = builder.build();
        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String downloadPath) {
                sec_text.setText(Html.fromHtml(downloadPath));

                SharedPreferences settings= getSharedPreferences("shared preferences",MODE_PRIVATE);
                SharedPreferences.Editor editor= settings.edit();
                editor.putString("storage path",downloadPath);
                editor.commit();

                adapter.notifyDataSetChanged();
//                saveData();
//                sessionManager.createPathSession(String.valueOf(downloadPathTxt.getText()));
                System.out.println("path" + sec_text);
            }
        });

        chooser.setOnCancelListener(new StorageChooser.OnCancelListener() {
            @Override
            public void onCancel() {
//                Toast.makeText(getApplicationContext(), "Storage Chooser Cancelled.", Toast.LENGTH_SHORT).show();
            }
        });

        chooser.setOnMultipleSelectListener(new StorageChooser.OnMultipleSelectListener() {
            @Override
            public void onDone(ArrayList<String> selectedFilePaths) {
                for (String s : selectedFilePaths) {
                    Log.e("TAG", s);
                }
            }
        });
        chooser.show();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {


        if (requestCode == REQUEST_INVITE)
        {

        }
    }
    private void showWarning()
    {
        String yes,no,exit;
        if(translated_string.containsKey("yes"))
        {
            yes=translated_string.get("yes").toString() ;
            no=translated_string.get("no").toString() ;
            exit=translated_string.get("exit").toString() ;


        }
        else
        {
            yes="Yes";
            no="No";
            exit="Do you want to exit?";
        }

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        Locale.getDefault().getDisplayLanguage();
        builder.setMessage(exit)
                .setNegativeButton(yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finishAffinity();


                            }
                        })
                .setPositiveButton(no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });

        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }
    public  void showWarning(final Context mContext, final String APP_PNAME)
    {
        String yes,no,rate_us,satisfied,app_name;
        if(translated_string.containsKey("yes"))
        {
            yes=translated_string.get("yes").toString() ;
            no=translated_string.get("no").toString() ;
            rate_us=translated_string.get("rate_us").toString() ;
            app_name=translated_string.get("app_name").toString() ;

            satisfied=translated_string.get("satisfied").toString();
            satisfied+="\""+app_name+"\"?";


        }
        else
        {
            yes="Yes";
            no="No";
            rate_us="Rate us";
            app_name=getString(R.string.app_name);
            satisfied="Are you satisfied using \"" + app_name+ "\"\n";
        }
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        Locale.getDefault().getDisplayLanguage();
        builder.setTitle(rate_us);

        builder.setMessage(satisfied)
                .setPositiveButton(yes,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("rated", true);
                                editor.commit();
                                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                                dialog.dismiss();

                            }
                        })
                .setNegativeButton(no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                // showPopup(mContext);
                                showDD();
                            }
                        });

        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }
    public void showDD()
    {
        Intent in=new Intent(Acc_Setting.this,dialogg.class);
        startActivity(in);
    }

    public void onBackPressed()
    {

        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, android.R.anim.slide_out_right);
    }

    class dataListAdapter extends BaseAdapter {
        ArrayList<String> Title;

        Bitmap borderPatern;
        dataListAdapter() {
            Title = null;

        }

        public dataListAdapter(ArrayList<String> text) {
            Title = text;

        }

        public int getCount() {
            // TODO Auto-generated method stub
            return Title.size();
        }

        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.row, parent, false);
            final TextView title, detail,heading,heading2,heading3;
            ImageView symbol=(ImageView)row.findViewById(R.id.symbol);
            ImageView bottomline=(ImageView)row.findViewById(R.id.bottomline);

            title = (TextView) row.findViewById(R.id.main_text);
            heading = (TextView) row.findViewById(R.id.headingtv);
            heading2 = (TextView) row.findViewById(R.id.headingtv2);
            heading3 = (TextView) row.findViewById(R.id.headingtv3);
            title.setText(Title.get(position));
             sec_text=(TextView)row.findViewById(R.id.sec_text);
            SwitchCompat tt = (SwitchCompat) row.findViewById(R.id.tog);

            if((position==0) || (position==1) ||(position==2) ||(position==3) ||(position==4) ||(position==5))
            {
                if (position==0){
                    heading.setVisibility(View.VISIBLE);
                    heading.setText("Video Settings");
                }else if (position==2){
                    bottomline.setVisibility(View.VISIBLE);
                }else if (position==3){
                    heading2.setVisibility(View.VISIBLE);
                    heading2.setText("Control Settings");
                }/*else if (position==5){
                bottomline.setVisibility(View.VISIBLE);
            }
*/
                if (position == 0)
                {
                    SharedPreferences settings1 = getSharedPreferences("MY_PREF", 0);
                    choice_vid = settings1.getInt("video formate", 1);
                    if (choice_vid == 1) {
                        sec_text.setText(".mp4");
                    } else {
                        sec_text.setText(".3gp");
                    }
                    symbol.setImageResource(R.drawable.sym_video);
                    tt.setVisibility(View.GONE);

                }
                if (position == 1)
                {
                    SharedPreferences settings1 = getSharedPreferences("MY_PREF", 0);
                    choice_img = settings1.getInt("img formate", 1);
                    if (choice_img == 1) {
                        sec_text.setText(".png");
                    } else {
                        sec_text.setText(".jpg");
                    }

                    symbol.setImageResource(R.drawable.sym_img);
                    tt.setVisibility(View.GONE);
                }
                if (position == 2)
                {
                    symbol.setImageResource(R.drawable.sym_widget);

                    sec_text.setVisibility(View.GONE);
                    SharedPreferences myPrefs = getSharedPreferences("MY_PREF", 0);
                    final SharedPreferences.Editor myPrefsEdit = myPrefs.edit();
                    showWidget = myPrefs.getBoolean("show widget", false);
                    tt.setChecked(showWidget);
                    tt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                    {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                        {

                            buttonView.setChecked(isChecked);
                            myPrefsEdit.putBoolean("show widget", isChecked);
                            myPrefsEdit.apply();

                        }
                    });
                }

                if(position==3)
                {
                    symbol.setImageResource(R.drawable.sym_cross);

                    sec_text.setVisibility(View.GONE);
                    SharedPreferences myPrefs = getSharedPreferences("MY_PREF", 0);
                    final SharedPreferences.Editor myPrefsEdit = myPrefs.edit();
                    close_app = myPrefs.getBoolean("close app", false);
                    tt.setChecked(close_app);
                    tt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                    {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                        {

                            buttonView.setChecked(isChecked);
                            myPrefsEdit.putBoolean("close app", isChecked);
                            myPrefsEdit.apply();

                        }
                    });
                }


                if(position==4)
                {
                    SharedPreferences settings1 = getSharedPreferences("MY_PREF", 0);
                    timmer = settings1.getInt("timmer", 3);

                    symbol.setImageResource(R.drawable.sym_timer);
                    if(timmer!=0)
                        sec_text.setText(timmer+"s");
                    else
                        sec_text.setText("No countdown");

                    tt.setVisibility(View.GONE);

                }
                if(position==5)
                {
                    symbol.setImageResource(R.drawable.sys_storage);

                    SharedPreferences settings1 = getSharedPreferences("shared preferences", MODE_PRIVATE);

                    String fullPath =  settings1.getString("storage path","storage/emulated/0/");

                    sec_text.setText(fullPath+ "/Video Call Recorder");

                    tt.setVisibility(View.GONE);
                }

            }
            else
            {
                tt.setVisibility(View.GONE);
                sec_text.setVisibility(View.GONE);
            }
            return (row);

        }

    }


    String languagepair;
    public void get_Transalations()
    {

        array_list.addAll(strings);
        array_list.add("Settings");
        array_list.add("Select Format");
        array_list.add("Countdown");
      /*  array_list.add("Select Storage Location");
        array_list.add("Internal");
        array_list.add("SD Card");*/
        array_list.add(getString(R.string.warn_1));
        array_list.add(getString(R.string.warn_2));
        array_list.add("Cancel");
        array_list.add("Select");

        array_list.add("Are you satisfied using");
        array_list.add("Yes");
        array_list.add("No");

        array_list.add("Do you want to exit?");
        array_list.add(getString(R.string.app_name));

        SharedPreferences pref = getApplicationContext().getSharedPreferences("translation", 0);
        if(pref.getBoolean(languagepair,false))
        {

            if(pref.getBoolean("set_acc",false)) {
                outputString = new ArrayList<>();
                for (int i = -0; i < array_list.size(); i++) {
                    outputString.add(pref.getString(array_list.get(i), array_list.get(i)));
                }

                setStrings();
            }
            else
            {
                if(isNetworkOnline())
                    Translate(getString(R.string.app_name),languagepair);
            }
        }
        else
        {
            if(isNetworkOnline())
                Translate(getString(R.string.app_name),languagepair);
        }


    }
    public void setStrings()
    {

        try
        {
            strings=new ArrayList<>();
            for(int i=0;i<=10;i++)
                strings.add(outputString.get(i));

            translated_string.put("settings",outputString.get(11));
            translated_string.put("select_format",outputString.get(12));
            translated_string.put("countdown",outputString.get(13));
            translated_string.put("storage_location",outputString.get(14));
            translated_string.put("internal",outputString.get(15));
            translated_string.put("sd",outputString.get(16));
            translated_string.put("warn_1",outputString.get(17));
            translated_string.put("warn_2",outputString.get(18));
            translated_string.put("cancel",outputString.get(19));
            translated_string.put("select",outputString.get(20));
            translated_string.put("satisfied",outputString.get(21));
            translated_string.put("yes",outputString.get(22));
            translated_string.put("no",outputString.get(23));
            translated_string.put("exit",outputString.get(24));
            translated_string.put("app_name",outputString.get(25));

            translated_string.put("rate_us",outputString.get(6));

            adapter=new dataListAdapter(strings);
            lv.setAdapter(adapter);
            TextView title = findViewById(R.id.title);
            title.setText(translated_string.get("settings").toString());
            setTitle(translated_string.get("settings").toString());

        }
        catch (Exception e)
        {

        }
    }
    ArrayList<String> array_list=new ArrayList<>();
    ArrayList<String> outputString=new ArrayList<>();
    public  Map<String, Object> translated_string = new HashMap<>();

    public class TranslatorBackgroundTask extends AsyncTask<String, Void, String>
    {
        //Declare Context
        Context ctx;
        String resultString;
        //Set Context
        TranslatorBackgroundTask(Context ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params)
        {
            //String variables
            for(int i=0;i<array_list.size();i++)
            {
                String textToBeTranslated =array_list.get(i);
                String languagePair = params[1];
                if(textToBeTranslated.contains(" "))
                {
                    textToBeTranslated=textToBeTranslated.replaceAll(" ","%20");
                }


                String jsonString;

                try {
                    //Set up the translation call URL
                    String yandexKey = "trnsl.1.1.20190519T110025Z.51bea2e4ba0da9c7.d80d14134c67c4247640cda23ec7ba0e9a2bb7c3";
                    String yandexUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + yandexKey
                            + "&text=" + textToBeTranslated + "&lang=" + languagePair;
                    URL yandexTranslateURL = new URL(yandexUrl);

                    //Set Http Conncection, Input Stream, and Buffered Reader
                    HttpURLConnection httpJsonConnection = (HttpURLConnection) yandexTranslateURL.openConnection();
                    InputStream inputStream = httpJsonConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    //Set string builder and insert retrieved JSON result into it
                    StringBuilder jsonStringBuilder = new StringBuilder();
                    while ((jsonString = bufferedReader.readLine()) != null) {
                        jsonStringBuilder.append(jsonString + "\n");
                    }

                    //Close and disconnect
                    bufferedReader.close();
                    inputStream.close();
                    httpJsonConnection.disconnect();

                    //Making result human readable
                    resultString = jsonStringBuilder.toString().trim();
                    //Getting the characters between [ and ]
                    resultString = resultString.substring(resultString.indexOf('[') + 1);
                    resultString = resultString.substring(0, resultString.indexOf("]"));
                    //Getting the characters between " and "
                    resultString = resultString.substring(resultString.indexOf("\"") + 1);
                    resultString = resultString.substring(0, resultString.indexOf("\""));

                    //  Log.d("Translation Result:", resultString);
                    outputString.add(resultString);
                    //   return jsonStringBuilder.toString().trim();

                } catch (MalformedURLException e)
                {
                    outputString.add(textToBeTranslated);
                    e.printStackTrace();
                } catch (IOException e)
                {
                    outputString.add(textToBeTranslated);
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String result)
        {
            save_translations_to_prefrences();
            setStrings();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
    public void save_translations_to_prefrences()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("translation", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(languagepair,true);
        editor.putBoolean("set_acc",true);
        for(int i=0;i<outputString.size();i++)
        {
            editor.putString(array_list.get(i),outputString.get(i));
        }

        editor.apply();
    }
    public void Translate(String textToBeTranslated, String languagePair)
    {
        TranslatorBackgroundTask translatorBackgroundTask= new TranslatorBackgroundTask(getApplicationContext());
        translatorBackgroundTask.execute(textToBeTranslated,languagePair).toString();

    }
    public boolean isNetworkOnline()
    {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()== NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()== NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;

    }

}
