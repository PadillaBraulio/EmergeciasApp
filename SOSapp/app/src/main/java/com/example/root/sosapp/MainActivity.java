package com.example.root.sosapp;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import manuals.ItemFragment;
import manuals.doc.DocContent;
import map.MapGoogle;

public class MainActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {

    private ViewPager pager = null;
    private TabAdapter pageradapter = null;
    private String CLASS = "Principal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setAdapter(new TabAdapter(getSupportFragmentManager(),
                MainActivity.this));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.i(CLASS,"Pagina seleccionada " + position);
                switch (position)
                {
                    case 0:

                        break;
                    default:

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public void onListFragmentInteraction(DocContent.DummyItem item) {

    }

    public static class TabAdapter extends FragmentPagerAdapter
    {
        private final static int PAG_COUNT = 2;
        private String [] tabTitles = {"Ubicación", "manuales"};
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
               case 0 : return new MapGoogle();

                case 1 : return new ItemFragment();
            }
            return  null;
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
