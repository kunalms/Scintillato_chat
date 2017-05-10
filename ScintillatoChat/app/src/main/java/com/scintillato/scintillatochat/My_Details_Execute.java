package com.scintillato.scintillatochat;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract.Columns;
import android.util.Log;
import com.scintillato.scintillatochat.My_Details_Database.*;

public class My_Details_Execute extends SQLiteOpenHelper {

    public static final int database_version=2;
    public String CREATE_QUERY_GROUP="CREATE TABLE IF NOT EXISTS "+MyDetailsDatabase.TABLE_NAME+"("+MyDetailsDatabase.USER_ID+" INTEGER PRIMARY KEY,"+MyDetailsDatabase.USERNAME+" TEXT,"+MyDetailsDatabase.NAME+" TEXT,"+MyDetailsDatabase.PHONE_NUMBER+" TEXT,"+MyDetailsDatabase.DATE_CREATED+" DATE,"+MyDetailsDatabase.PROFILE_PIC+" LONG BLOB,"+MyDetailsDatabase.USER_BIO+" TEXT)";//+ContactsRegisteredInfo.PROFILE_PIC+" TEXT);";
    public My_Details_Execute(Context context,String number) {
        super(context, MyDetailsDatabase.DATABASE_NAME+number, null, database_version);
        Log.d("status","Database Created"+MyDetailsDatabase.DATABASE_NAME+number);

        // TODO Auto-generated constructor stub
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MyDetailsDatabase.TABLE_NAME);
        onCreate(db);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        db.execSQL(CREATE_QUERY_GROUP);

        Log.d("status","Table Created"+MyDetailsDatabase.TABLE_NAME);
    }

    public void delete_muy_details()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ MyDetailsDatabase.TABLE_NAME);
        Log.d("database", "deleted "+MyDetailsDatabase.TABLE_NAME);
    }

    Cursor get_my_details(My_Details_Execute obj)
    {
        SQLiteDatabase SQ=obj.getReadableDatabase();
        String query="SELECT "+MyDetailsDatabase.USER_ID+", "+MyDetailsDatabase.USERNAME+","+MyDetailsDatabase.PHONE_NUMBER+","+MyDetailsDatabase.NAME+","+MyDetailsDatabase.DATE_CREATED+","+MyDetailsDatabase.USER_BIO+" FROM "+MyDetailsDatabase.TABLE_NAME + " LIMIT 1;";
        Cursor c=SQ.rawQuery(query,null);
        return c;
    }
    Cursor get_my_profile_pic(My_Details_Execute obj)
    {
        SQLiteDatabase SQ=obj.getReadableDatabase();
        String query="SELECT "+MyDetailsDatabase.PROFILE_PIC+" FROM "+MyDetailsDatabase.TABLE_NAME + " LIMIT 1;";
        Cursor c=SQ.rawQuery(query,null);
        return c;
    }

    void update_profile_details(My_Details_Execute obj,String user_id,String bio,String username,String user_name)
    {
        SQLiteDatabase SQ =obj.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyDetailsDatabase.USER_BIO,bio);
        cv.put(MyDetailsDatabase.USERNAME,username);
        cv.put(MyDetailsDatabase.NAME,user_name);

        SQ.update(MyDetailsDatabase.TABLE_NAME, cv, MyDetailsDatabase.USER_ID+"=?", new String[]{user_id});
    }
    public void putinto_my_details(My_Details_Execute obj,String user_name,String name,String  phone_number,String user_id,String create_date,String profile_pic)
    {     SQLiteDatabase SQ =obj.getWritableDatabase();
            ContentValues cv= new ContentValues();
            cv.put(MyDetailsDatabase.USER_ID, user_id);
            cv.put(MyDetailsDatabase.USERNAME, user_name);
            cv.put(MyDetailsDatabase.NAME, name);
            cv.put(MyDetailsDatabase.DATE_CREATED, create_date);
            cv.put(MyDetailsDatabase.PHONE_NUMBER, phone_number);
            cv.put(MyDetailsDatabase.PROFILE_PIC,profile_pic);
            long k=SQ.insert(MyDetailsDatabase.TABLE_NAME,null,cv);
            Log.d("statis","one row inserted"+MyDetailsDatabase.TABLE_NAME);
    }

}
