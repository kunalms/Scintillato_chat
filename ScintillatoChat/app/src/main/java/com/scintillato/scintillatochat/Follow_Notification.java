package com.scintillato.scintillatochat;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

public class Follow_Notification extends ActionBarActivity implements TabListener{

    private ViewPager tabsviewPager;
    private Follow_Notification_Adapter mTabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_notification);
        final View view_col = new View(this);
        view_col.setBackgroundColor(Color.BLUE);
        tabsviewPager = (ViewPager) findViewById(R.id.tabspager_follow_notification_1);

        mTabsAdapter = new Follow_Notification_Adapter(getSupportFragmentManager());
        tabsviewPager.setAdapter(mTabsAdapter);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        Tab following = getSupportActionBar().newTab().setText("FOLLOWING").setTabListener(this);
        Tab notifications= getSupportActionBar().newTab().setText("NOTIFICATIONS").setTabListener(this);
        getSupportActionBar().addTab(following);
        getSupportActionBar().addTab(notifications);
        //This helps in providing swiping effect for v7 compat library
        tabsviewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                getSupportActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
        Log.d("hey","hey");
    }

    @Override
    public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTabSelected(Tab selectedtab, FragmentTransaction arg1) {
        // TODO Auto-generated method stub
        tabsviewPager.setCurrentItem(selectedtab.getPosition()); //update tab position on tap
    }

    @Override
    public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
        // TODO Auto-generated method stub

    }
}