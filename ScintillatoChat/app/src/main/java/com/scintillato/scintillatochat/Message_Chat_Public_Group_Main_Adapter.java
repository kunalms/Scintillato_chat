package com.scintillato.scintillatochat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class Message_Chat_Public_Group_Main_Adapter  extends FragmentStatePagerAdapter{

    private int TOTAL_TABS = 2;
    private Bundle bundle;
    public Message_Chat_Public_Group_Main_Adapter(FragmentManager fm,Bundle bundle) {
        super(fm);
        this.bundle=bundle;
        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int index) {
        // TODO Auto-generated method stub
        switch (index) {
            case 1:
                Fragment f=new Group_Question();
                f.setArguments(this.bundle);
                return f;

            case 0:
                f=new Message_Group_Chat_Public();
                f.setArguments(this.bundle);
                return f;
        }

        return null;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return TOTAL_TABS;
    }

}
