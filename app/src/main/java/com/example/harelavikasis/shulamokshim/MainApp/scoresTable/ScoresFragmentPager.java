package com.example.harelavikasis.shulamokshim.MainApp.scoresTable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by harelavikasis on 03/01/2017.
 */

public class ScoresFragmentPager extends FragmentPagerAdapter {

    //integer to count number of tabs
    int tabCount;
    private static final String TABLE_FRAGMENT ="Table";
    private static final String MAP_FRAGMENT ="Map";

    //Constructor to the class
    public ScoresFragmentPager(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return TABLE_FRAGMENT;
            case 1:
                return MAP_FRAGMENT;
            default:
                return null;
        }
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                Fragment f1 = new TableFragment();
                return f1;
            case 1:
                Fragment f2 = new MapFragment();
                return f2;
            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}
