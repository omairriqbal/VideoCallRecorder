package com.videocaht.recorder.facetime.videocall.screenrecorder;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jolta on 3/5/2018.
 */

public class PagerAdapterr extends FragmentPagerAdapter {

    private final List<Fragment> mFragments = new ArrayList<Fragment>();
    private String[] tabTitles;
    public PagerAdapterr(FragmentManager manager,String[] titles)
    {
        super(manager);
        tabTitles=titles;
    }

    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);
        notifyDataSetChanged();
    }

    public void set_title(String vid,String ss)
    {
        tabTitles = new String[]{vid, ss};
    }
    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

}