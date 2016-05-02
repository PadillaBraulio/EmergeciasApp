package com.example.root.sosapp;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private ViewPager pager = null;
    private TabAdapter pageradapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TabAdapter(getSupportFragmentManager(),
                MainActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    public static class TabAdapter extends FragmentPagerAdapter
    {
        private final static int PAG_COUNT = 3;
        private String [] tabTitles = {"Ubicación","Manuales","Donativos"};
        private Context context;

        public TabAdapter(FragmentManager fm, Context context)  {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position)
            {

                default: return DefaultFragment.newInstance(1 + position);

            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return PAG_COUNT;
        }
    }
}
