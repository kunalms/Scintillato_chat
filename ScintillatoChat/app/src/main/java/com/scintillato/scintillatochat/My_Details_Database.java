package com.scintillato.scintillatochat;
import android.provider.BaseColumns;

public class My_Details_Database {

    public My_Details_Database() {
        // TODO Auto-generated constructor stub
    }

    public static abstract class MyDetailsDatabase implements BaseColumns
    {
        public static final String USERNAME="username";
        public static final String NAME="name";
        public static final String PHONE_NUMBER="phone_number";
        public static final String USER_ID="user_id";
        public static final String DATE_CREATED="date_created";
        public static final String USER_BIO="user_bio";
        public static final String DATABASE_NAME="profile";
        public static final String PROFILE_PIC="profile_pic";
        public static final String TABLE_NAME="my_details";
    }
}

