package com.emergenciasapp.sosapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import com.emergenciasapp.manuals.ItemFragment;
import com.emergenciasapp.manuals.PdfViewer;
import com.emergenciasapp.manuals.doc.DocContent;
import com.emergenciasapp.map.MapGoogle;
import com.emergenciasapp.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {

    private ViewPager viewPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setAdapter(new TabAdapter(getSupportFragmentManager(),
                MainActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent Settings = new Intent(this, SettingsActivity.class);
                startActivity(Settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onListFragmentInteraction(DocContent.Document item) {
        PdfViewer pdf = new PdfViewer(this,item.filename);
        pdf.showPdf();
    }

    public static class TabAdapter extends FragmentPagerAdapter
    {
        private final static int PAG_COUNT = 2;
        private String [] tabTitles = {"Ubicación", "Manuales"};
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        List<Fragment> fragments = this.getSupportFragmentManager().getFragments();
        if(fragments==null)return;
        for(Fragment fragment : fragments){
            if(fragment instanceof MapGoogle){
                fragment.onRequestPermissionsResult(requestCode,permissions,grantResults);
            }
        }

    }
}
