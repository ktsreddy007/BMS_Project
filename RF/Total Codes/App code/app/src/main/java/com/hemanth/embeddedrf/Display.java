package com.hemanth.embeddedrf;


import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;


public class Display extends AppCompatActivity {

        SectionsAdapter sectionsAdapter;
        ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        sectionsAdapter=new SectionsAdapter(getSupportFragmentManager());
        viewPager=findViewById(R.id.container);
        setupViewPager(viewPager);
        TabLayout tabLayout=findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }
    public void setupViewPager(ViewPager viewPager){
        SectionsAdapter adapter=new SectionsAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabFragment(),"Tab1");
        adapter.addFragment(new Tab1Fragment(),"Tab2");
        adapter.addFragment(new Tab2Fragment(),"Tab3");
        adapter.addFragment(new Tab3Fragment(),"Tab4");
        adapter.addFragment(new Tab4Fragment(),"Tab5");
        viewPager.setAdapter(adapter);
    }
}
