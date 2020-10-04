package com.recorder.screen.recordingapp.editor;

/**
 * Created by jolta on 3/5/2018.
 */

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;


import java.util.ArrayList;

public class vidFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<VideoModel> dataList;
    GetDataForAdapter dataForAdapter;
    SwipeRefreshLayout mySwipeRefreshLayout;
    static Context context;
    static String pkg;
    boolean first,buy;
    private ViewGroup rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.vid, container, false);
        context=getContext();
        pkg=context.getPackageName();

        dataForAdapter=new GetDataForAdapter(getContext());
        recyclerView=(RecyclerView) rootView.findViewById(R.id.recycler_view_for_video);
        dataList=new ArrayList<>();
        /*browseFacebook=(Button)findViewById(R.id.browse_facebook);
        browseFacebook.setOnClickListener(this);*/

        first=true;
        mySwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        setRecyclerView();
                    }
                }
        );

        (rootView.findViewById(R.id.rec)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // context.sendBroadcast(new Intent("Download_Cancelled"));
                // createRecordNotification();
            }
        });

        ((TextView)rootView.findViewById(R.id.msg)).setText(msg);
        return rootView;
    }

    String msg="No vidoes";
    public void setMsg(String m)
    {
        msg=m;
    }



    @Override
    public void onResume() {
        super.onResume();
        if(first)
        {
            first=!first;
            setRecyclerView();
        }
    }
    private ArrayList<VideoModel> getDataList(){
        for(int i=0;i<20;i++){
            VideoModel model=new VideoModel();
            model.setName("Video Downloaded From Facebook");
            dataList.add(model);
        }
        return dataList;
    }
    private void setRecyclerView(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED&& ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
            else{
                recyclerViewPart();
            }
        }
        else{
            recyclerViewPart();
        }
        new Handler().postDelayed(() ->   mySwipeRefreshLayout.setRefreshing(false), 1000);
    }
    public void recyclerViewPart()
    {
      /*  SharedPreferences settings= context.getSharedPreferences("MY_PREF",0);
        int ch1=settings.getInt("storage path",1);*/
        VideoFilesAdapters adapters=new VideoFilesAdapters(context,dataForAdapter.getVideoData());
        recyclerView.setAdapter(adapters);
        if(adapters.getItemCount()>0)
        {
            TextView t1=(TextView)rootView.findViewById(R.id.msg);
            t1.setVisibility(View.GONE);
        }
        GridLayoutManager manager;
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE)
            manager=new GridLayoutManager(context,1);
        else
            manager=new GridLayoutManager(context,1);

        recyclerView.setLayoutManager(manager);
        registerForContextMenu(recyclerView);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    setRecyclerView();
                }
                else{
//
                    Log.e("Permission Denied","True");
                }
                break;
        }
    }


}
