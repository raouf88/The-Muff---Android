package com.raouf.android.muff.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.RecyclerView;

import com.raouf.android.muff.Fragments.FavoritesFragment;
import com.raouf.android.muff.Fragments.SearchFragment;


/**
 *  Adapter to show {@link SearchFragment } and {@link FavoritesFragment}.
 *  @author Raouf
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new SearchFragment();
            case 1:
                return new FavoritesFragment();
            default:
                return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "SEARCH";
            case 1:
                return "FAVORITES";
        }
        return null;
    }
}
