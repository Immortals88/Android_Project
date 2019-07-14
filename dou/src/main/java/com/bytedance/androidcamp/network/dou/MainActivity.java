package com.bytedance.androidcamp.network.dou;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.bytedance.androidcamp.network.dou.fragments.HomePage;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ViewPager pager;
    private TabLayout tab;
    private List<Fragment> fragList;
    private List<String> titleList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragList = new ArrayList<>();
        fragList.add(new HomePage());
        fragList.add(new Fragment());

        titleList = new ArrayList<>();
        titleList.add("HomePage");
        titleList.add("Hello");

        pager = findViewById(R.id.viewPager);
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragList.get(i);
            }

            @Override
            public int getCount() {
                return fragList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titleList.get(position);
            }
        });

        tab = findViewById(R.id.tab);
        tab.setupWithViewPager(pager);
    }
}
