package com.hemanth.embeddedrf;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SectionsAdapter extends FragmentPagerAdapter {
    List<Fragment> fragmentList=new ArrayList<>();
    List<String> titles=new ArrayList<>();

    public SectionsAdapter(FragmentManager fm) {
        super(fm);
    }
    public void addFragment(Fragment fragment,String title){
        fragmentList.add(fragment);
        titles.add(title);
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
