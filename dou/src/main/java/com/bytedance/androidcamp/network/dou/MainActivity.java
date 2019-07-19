package com.bytedance.androidcamp.network.dou;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.bytedance.androidcamp.network.dou.fragments.MyInfoPage;
import com.bytedance.androidcamp.network.dou.fragments.Postpage;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

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
        fragList.add(new Postpage());
        fragList.add(new MyInfoPage());

        titleList = new ArrayList<>();
        titleList.add("首页");
        titleList.add("上传");
        titleList.add("我的");

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
