package com.raouf.android.muff.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.raouf.android.muff.Adapters.MainPagerAdapter;
import com.raouf.android.muff.R;

/**
 *  Main Screen of app.
 *  - Holds and shows {@link com.raouf.android.muff.Fragments.SearchFragment} and {@link com.raouf.android.muff.Fragments.FavoritesFragment} inside a {@link ViewPager}.
 *  - ask for storage permissions if needed to be able to store images in the external storage.
 *  @author Raouf
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // setup viewpager
        ViewPager mViewPager = (ViewPager) findViewById(R.id.activity_main_viewPager);
        mViewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
        // setup tabs for viewpager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_main_tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
        TabLayout.Tab searchTab = tabLayout.getTabAt(0);
        if (searchTab != null) searchTab.setIcon(R.drawable.ic_serach);
        TabLayout.Tab favTab = tabLayout.getTabAt(1);
        if (favTab != null) favTab.setIcon(R.drawable.ic_favorite);
        // ask for storage permissions if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=  PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

    }

}
