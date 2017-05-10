package com.scintillato.scintillatochat;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Message_Chat_Public_Group_Main extends ActionBarActivity implements TabListener{

    private ViewPager tabsviewPager;
    private ActionBar mActionBar;
    private TextView title,subtitle;
    private RelativeLayout relativeLayout;
    private ImageView back;
    private Message_Chat_Public_Group_Main_Adapter mTabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_notification);
        final View view_col = new View(this);
        view_col.setBackgroundColor(Color.BLUE);
        tabsviewPager = (ViewPager) findViewById(R.id.tabspager_follow_notification_1);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.custom_actionbar_layout);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        View activityName =getSupportActionBar().getCustomView();

        title=(TextView)activityName.findViewById(R.id.tv_custom_actionbar_layout_title);
        subtitle=(TextView)activityName.findViewById(R.id.tv_custom_actionbar_layout_subtitle);
        relativeLayout=(RelativeLayout)activityName.findViewById(R.id.rl_custom_actionbar_layout);
        back=(ImageView)activityName.findViewById(R.id.iv_custom_actionbar_layout_back);
        Intent i = getIntent();
        final Bundle b = i.getExtras();
        title.setText(b.getString("group_name"));
        title.setTextColor(this.getResources().getColor(R.color.white));
        subtitle.setTextColor(this.getResources().getColor(R.color.white));

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Profile_Chat_Group.class);
                i.putExtra("group_id",b.getString("group_id"));
                startActivity(i);
                Toast.makeText(getApplicationContext(),"Actionbar clicked",Toast.LENGTH_SHORT).show();
            }
        });
       /* profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Image clicked",Toast.LENGTH_SHORT).show();
            }
        });*/
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"back button clicked",Toast.LENGTH_SHORT).show();
            }
        });

        // getSupportActionBar().setTitle(b.getString("group_name"));

        mTabsAdapter = new Message_Chat_Public_Group_Main_Adapter(getSupportFragmentManager(),b);
        tabsviewPager.setAdapter(mTabsAdapter);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        Tab following = getSupportActionBar().newTab().setText("QUESTIONS").setTabListener(this);
        Tab notifications= getSupportActionBar().newTab().setText("DISCUSSION").setTabListener(this);
        getSupportActionBar().addTab(notifications);
        getSupportActionBar().addTab(following);

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