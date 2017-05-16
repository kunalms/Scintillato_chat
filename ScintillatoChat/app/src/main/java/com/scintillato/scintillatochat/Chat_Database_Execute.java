package com.scintillato.scintillatochat;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcelable;
import android.util.Log;

import com.scintillato.scintillatochat.Group_Database.GroupDatabase;
import com.scintillato.scintillatochat.Message_Database.*;
import com.scintillato.scintillatochat.Message_Database_Unsend.*;
import com.scintillato.scintillatochat.Recent_Chats.RecentChatsInfo;
import com.scintillato.scintillatochat.Group_Member_Database.GroupMemberDatabase;

import java.util.ArrayList;

/**
 * Created by VIVEK on 13-03-2017.
 */

public class Chat_Database_Execute extends SQLiteOpenHelper {

    private Context ctx;
    private String cur_number;
    public static final int database_version=2;
    public String CREATE_QUERY_GROUP="CREATE TABLE IF NOT EXISTS "+GroupDatabase.TABLE_NAME+"("+GroupDatabase.GROUP_NAME+" TEXT,"+GroupDatabase.TOPIC+" TEXT,"+GroupDatabase.DESCRIPTION+" TEXT,"+GroupDatabase.CREATE_DATE+" DATETIME,"+GroupDatabase.GROUP_IMAGE+" LONGBLOB,"+GroupDatabase.MEMBER_COUNT+" INTEGER,"+GroupDatabase.STATUS+" TEXT, "+GroupDatabase.GROUP_ID+" TEXT PRIMARY KEY)";
    public String CREATE_QUERY_GROUP_MEMBERS="CREATE TABLE IF NOT EXISTS "+ Group_Member_Database.GroupMemberDatabase.TABLE_NAME+"("+ Group_Member_Database.GroupMemberDatabase.GROUP_ID+" TEXT,"+ Group_Member_Database.GroupMemberDatabase.NUMBER+" TEXT,"+ Group_Member_Database.GroupMemberDatabase.ADMIN+" INTEGER,"+Group_Member_Database.GroupMemberDatabase.RANK+" INTEGER,"+Group_Member_Database.GroupMemberDatabase.ENTER_DATE+" DATETIME,"+ Group_Member_Database.GroupMemberDatabase.PROFILE_PIC+" LONGBLOB);";
    public String CREATE_QUERY_MESSAGE_SINGLE="CREATE TABLE IF NOT EXISTS "+MessageSingleInfo.TABLE_NAME+"("+MessageSingleInfo.MESSAGE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+MessageSingleInfo.DATE_TIME+" DATETIME,"+MessageSingleInfo.MESSAGE+" TEXT,"+MessageSingleInfo.SEND_RECIEVE+" INTEGER,"+MessageSingleInfo.SENDER+" TEXT,"+MessageSingleInfo.RECIEVER+" TEXT,"+MessageSingleInfo.OPPOSITE_PERSON_NUMBER+" TEXT,"+MessageSingleInfo.IMAGE_LOC+" TEXT,"+MessageSingleInfo.VIDEO_LOC+" TEXT,"+MessageSingleInfo.STATUS+" INTEGER,"+MessageSingleInfo.OPPOSITE_PERSON_MESSAGE_ID+" INTEGER);";;
    public String CREATE_QUERY_MESSAGE_GROUP="CREATE TABLE IF NOT EXISTS "+MessageGroupInfo.TABLE_NAME+"("+MessageGroupInfo.MESSAGE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+MessageGroupInfo.DATE_TIME+" DATETIME,"+MessageGroupInfo.MESSAGE+" TEXT,"+MessageGroupInfo.SEND_RECIEVE+" INTEGER,"+MessageGroupInfo.SENDER+" TEXT,"+MessageGroupInfo.GROUP_ID+" TEXT,"+MessageGroupInfo.IMAGE_LOC+" TEXT,"+MessageGroupInfo.VIDEO_LOC+" TEXT,"+MessageGroupInfo.STATUS+" INTEGER,"+MessageGroupInfo._MEMBER+" TEXT,"+MessageGroupInfo.ADD+" BOOLEAN,"+MessageGroupInfo.REMOVE+" BOOLEAN,"+MessageGroupInfo.LEFT+" BOOLEAN,"+MessageGroupInfo.ICON_CHANGE+" BOOLEAN,"+MessageGroupInfo.NAME_CHANGE+" BOOLEAN,"+MessageGroupInfo.NEW_NAME+" TEXT);";
    public String CREATE_QUERY_RECENT_CHATS="CREATE TABLE IF NOT EXISTS " +RecentChatsInfo.TABLE_NAME+"("+RecentChatsInfo.FLAG+" INTEGER,"+RecentChatsInfo.GROUP_ID+" TEXT,"+RecentChatsInfo.OPPOSITE_PERSON_NUMBER+" TEXT,"+RecentChatsInfo.SENDER+" TEXT,"+RecentChatsInfo.LAST_UPDATED+" DATETIME);";
    public String CREATE_QUERY_UNSEND_MESSAGE_SINGLE="CREATE TABLE IF NOT EXISTS "+MessageUnsendSingleInfo.TABLE_NAME+"("+MessageUnsendSingleInfo.MESSAGE_ID+" INTEGER PRIMARY KEY,"+MessageUnsendSingleInfo.DATE_TIME+" DATETIME,"+MessageUnsendSingleInfo.MESSAGE+" TEXT,"+MessageUnsendSingleInfo.SEND_RECIEVE+" INTEGER,"+MessageUnsendSingleInfo.SENDER+" TEXT,"+MessageUnsendSingleInfo.RECIEVER+" TEXT,"+MessageUnsendSingleInfo.OPPOSITE_PERSON_NUMBER+" TEXT,"+MessageUnsendSingleInfo.IMAGE_LOC+" TEXT,"+MessageUnsendSingleInfo.VIDEO_LOC+" TEXT,"+MessageUnsendSingleInfo.STATUS+" INTEGER);";
    public String CREATE_QUERY_UNSEND_MESSAGE_GROUP="CREATE TABLE IF NOT EXISTS "+MessageUnsendGroupInfo.TABLE_NAME+"("+MessageUnsendGroupInfo.MESSAGE_ID+" INTEGER PRIMARY KEY,"+MessageUnsendGroupInfo.DATE_TIME+" DATETIME,"+MessageUnsendGroupInfo.MESSAGE+" TEXT,"+MessageUnsendGroupInfo.SEND_RECIEVE+" INTEGER,"+MessageUnsendGroupInfo.SENDER+" TEXT,"+MessageUnsendGroupInfo.GROUP_ID+" TEXT,"+MessageUnsendGroupInfo.IMAGE_LOC+" TEXT,"+MessageUnsendGroupInfo.VIDEO_LOC+" TEXT,"+MessageUnsendGroupInfo.STATUS+" INTEGER);";

    public Chat_Database_Execute(Context context, String number) {
        super(context, GroupDatabase.DATABASE_NAME+number, null, database_version);
        ctx=context;
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");

        Log.d("status","Database Created"+GroupDatabase.DATABASE_NAME+number);

        // TODO Auto-generated constructor stub
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY_GROUP);
        db.execSQL(CREATE_QUERY_GROUP_MEMBERS);
        db.execSQL(CREATE_QUERY_MESSAGE_SINGLE);
        db.execSQL(CREATE_QUERY_MESSAGE_GROUP);
        db.execSQL(CREATE_QUERY_RECENT_CHATS);
        db.execSQL(CREATE_QUERY_UNSEND_MESSAGE_SINGLE);
        db.execSQL(CREATE_QUERY_UNSEND_MESSAGE_GROUP);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GroupDatabase.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GroupMemberDatabase.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MessageSingleInfo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MessageGroupInfo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RecentChatsInfo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MessageUnsendSingleInfo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MessageUnsendGroupInfo.TABLE_NAME);
        onCreate(db);
    }
    Cursor fetch_message_single_all(Chat_Database_Execute obj,String opposite_person_number)
    {
        SQLiteDatabase SQ=obj.getReadableDatabase();
        String[] coloumns={MessageSingleInfo.MESSAGE_ID,MessageSingleInfo.MESSAGE,MessageSingleInfo.DATE_TIME,MessageSingleInfo.SEND_RECIEVE,MessageSingleInfo.SENDER,MessageSingleInfo.RECIEVER,MessageSingleInfo.OPPOSITE_PERSON_NUMBER,MessageSingleInfo.STATUS,MessageSingleInfo.IMAGE_LOC,MessageSingleInfo.VIDEO_LOC};
        Cursor cr=SQ.query(MessageSingleInfo.TABLE_NAME, coloumns, MessageSingleInfo.OPPOSITE_PERSON_NUMBER+"=?", new String[]{opposite_person_number}, null, null,null);
        return cr;
    }

    //*******************************************************MESSAGE_SINGLE********************************************************
    Cursor fetch_message_single_first(Chat_Database_Execute obj,String opposite_person_number)
    {
        SQLiteDatabase SQ=obj.getReadableDatabase();
        String[] coloumns={MessageSingleInfo.MESSAGE_ID,MessageSingleInfo.MESSAGE,MessageSingleInfo.DATE_TIME,MessageSingleInfo.SEND_RECIEVE,MessageSingleInfo.SENDER,MessageSingleInfo.RECIEVER,MessageSingleInfo.OPPOSITE_PERSON_NUMBER,MessageSingleInfo.STATUS,MessageSingleInfo.IMAGE_LOC,MessageSingleInfo.VIDEO_LOC,MessageSingleInfo.OPPOSITE_PERSON_MESSAGE_ID};

        Cursor cr=SQ.query(MessageSingleInfo.TABLE_NAME, coloumns, MessageSingleInfo.OPPOSITE_PERSON_NUMBER+"=?",new String[]{opposite_person_number}, null, null, MessageSingleInfo.MESSAGE_ID+" DESC ","15");
        return cr;
    }

    public Cursor fetch_message_chat_next_single(Chat_Database_Execute obj, String opposite_person_number,String last_id)
    {
        SQLiteDatabase SQ=obj.getReadableDatabase();
        String[] coloumns={MessageSingleInfo.MESSAGE_ID,MessageSingleInfo.MESSAGE,MessageSingleInfo.DATE_TIME,MessageSingleInfo.SEND_RECIEVE,MessageSingleInfo.SENDER,MessageSingleInfo.RECIEVER,MessageSingleInfo.OPPOSITE_PERSON_NUMBER,MessageSingleInfo.STATUS,MessageSingleInfo.IMAGE_LOC,MessageSingleInfo.VIDEO_LOC,MessageSingleInfo.OPPOSITE_PERSON_MESSAGE_ID};
        Cursor cr=SQ.query(MessageSingleInfo.TABLE_NAME, coloumns, MessageSingleInfo.OPPOSITE_PERSON_NUMBER+"=? AND "+MessageSingleInfo.MESSAGE_ID+"<?",new String[]{opposite_person_number,last_id}, null, null, MessageSingleInfo.MESSAGE_ID+" DESC ","5");
        return cr;
    }
    public Cursor fetch_last_message_chat_single(Chat_Database_Execute obj, String opposite_person_number)
    {
        SQLiteDatabase SQ=obj.getReadableDatabase();
        String[] coloumns={MessageSingleInfo.MESSAGE_ID,MessageSingleInfo.MESSAGE,MessageSingleInfo.STATUS,MessageSingleInfo.SEND_RECIEVE};//,MessageSingleInfo.DATE_TIME,MessageSingleInfo.SEND_RECIEVE,MessageSingleInfo.SENDER,MessageSingleInfo.RECIEVER,MessageSingleInfo.OPPOSITE_PERSON_NUMBER,MessageSingleInfo.STATUS,MessageSingleInfo.IMAGE_LOC,MessageSingleInfo.VIDEO_LOC};
        Cursor cr=SQ.query(MessageSingleInfo.TABLE_NAME, coloumns, MessageSingleInfo.OPPOSITE_PERSON_NUMBER+"=?",new String[]{opposite_person_number}, null, null, MessageSingleInfo.MESSAGE_ID+" DESC ","1");
        return cr;
    }

    public String[] get_count_unread_message_single(Chat_Database_Execute obj,String opposite_person_number) {
        long i = 0;
        int count=0;
        String message_id="";
        SQLiteDatabase SQ = obj.getReadableDatabase();
        String[] coloumns = {MessageSingleInfo.MESSAGE_ID, MessageSingleInfo.MESSAGE, MessageSingleInfo.STATUS, MessageSingleInfo.SEND_RECIEVE};//,MessageSingleInfo.DATE_TIME,MessageSingleInfo.SEND_RECIEVE,MessageSingleInfo.SENDER,MessageSingleInfo.RECIEVER,MessageSingleInfo.OPPOSITE_PERSON_NUMBER,MessageSingleInfo.STATUS,MessageSingleInfo.IMAGE_LOC,MessageSingleInfo.VIDEO_LOC};
        for (i = 0; ; i++) {
            if(i==0)
            {
                Cursor cr = SQ.query(MessageSingleInfo.TABLE_NAME, coloumns, MessageSingleInfo.OPPOSITE_PERSON_NUMBER + "=? and "+MessageSingleInfo.SEND_RECIEVE+"=? and "+MessageSingleInfo.STATUS+"=?", new String[]{opposite_person_number,"1","0"}, null, null, MessageSingleInfo.MESSAGE_ID + " DESC ", "1");
                if(cr.getCount()>0)
                {
                    cr.moveToFirst();
                    message_id=cr.getString(0);
                    count++;
                }
                else
                    break;
            }
            else
            {
                Cursor cr = SQ.query(MessageSingleInfo.TABLE_NAME, coloumns, MessageSingleInfo.OPPOSITE_PERSON_NUMBER + "=? and "+MessageSingleInfo.SEND_RECIEVE+"=? and "+MessageSingleInfo.STATUS+"=? and "+MessageSingleInfo.MESSAGE_ID+"<?", new String[]{opposite_person_number,"1","0",message_id}, null, null, MessageSingleInfo.MESSAGE_ID + " DESC ", "1");
                if(cr.getCount()>0)
                {
                    cr.moveToFirst();
                    message_id=cr.getString(0);
                    count++;
                }
                else
                    break;
            }
        }
        return new String[]{count+"",message_id};
    }

    public ArrayList<String> get_array_other_message_id_unread_message_single(Chat_Database_Execute obj,String opposite_person_number) {
        long i = 0;
        int count=0;
        String message_id="";
        ArrayList<String> message_id_list=new ArrayList<>();
        SQLiteDatabase SQ = obj.getReadableDatabase();
        String[] coloumns = {MessageSingleInfo.MESSAGE_ID, MessageSingleInfo.MESSAGE, MessageSingleInfo.STATUS, MessageSingleInfo.SEND_RECIEVE,MessageSingleInfo.OPPOSITE_PERSON_MESSAGE_ID};//,MessageSingleInfo.DATE_TIME,MessageSingleInfo.SEND_RECIEVE,MessageSingleInfo.SENDER,MessageSingleInfo.RECIEVER,MessageSingleInfo.OPPOSITE_PERSON_NUMBER,MessageSingleInfo.STATUS,MessageSingleInfo.IMAGE_LOC,MessageSingleInfo.VIDEO_LOC};
        for (i = 0; ; i++) {
            if(i==0)
            {
                Cursor cr = SQ.query(MessageSingleInfo.TABLE_NAME, coloumns, MessageSingleInfo.OPPOSITE_PERSON_NUMBER + "=? and "+MessageSingleInfo.SEND_RECIEVE+"=? and "+MessageSingleInfo.STATUS+"=?", new String[]{opposite_person_number,"1","0"}, null, null, MessageSingleInfo.MESSAGE_ID + " DESC ", "1");
                if(cr.getCount()>0)
                {
                    cr.moveToFirst();
                    message_id=cr.getString(0);
                    count++;
                    message_id_list.add(cr.getString(4));
                }
                else
                    break;
            }
            else
            {
                Cursor cr = SQ.query(MessageSingleInfo.TABLE_NAME, coloumns, MessageSingleInfo.OPPOSITE_PERSON_NUMBER + "=? and "+MessageSingleInfo.SEND_RECIEVE+"=? and "+MessageSingleInfo.STATUS+"=? and "+MessageSingleInfo.MESSAGE_ID+"<?", new String[]{opposite_person_number,"1","0",message_id}, null, null, MessageSingleInfo.MESSAGE_ID + " DESC ", "1");
                if(cr.getCount()>0)
                {
                    cr.moveToFirst();
                    message_id=cr.getString(0);
                    count++;
                    message_id_list.add(cr.getString(4));
                }
                else
                    break;
            }
        }
        return message_id_list;
    }

    public void delete_message_single_all()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ MessageSingleInfo.TABLE_NAME);
        Log.d("database", "deleted "+MessageSingleInfo.TABLE_NAME);
    }
    public void delete_message_single_selected(String message_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ MessageSingleInfo.TABLE_NAME+" where "+MessageSingleInfo.MESSAGE_ID+"="+message_id);
        Log.d("database", "deleted "+MessageSingleInfo.TABLE_NAME+" id:"+message_id);
    }

    public void update_status_message_single(Chat_Database_Execute obj,String message_id,String status)
    {
        SQLiteDatabase SQ =obj.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MessageSingleInfo.STATUS,status);
        SQ.update(MessageSingleInfo.TABLE_NAME, cv, MessageSingleInfo.MESSAGE_ID+"=?", new String[]{message_id});
    }

    public void update_status_message_single_seen(Chat_Database_Execute obj,String status)//change afterwards
    {
        SQLiteDatabase SQ =obj.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MessageSingleInfo.STATUS,status);
        SQ.update(MessageSingleInfo.TABLE_NAME, cv, MessageSingleInfo.SEND_RECIEVE+"=1", null);
    }

    public String insert_message_single(Chat_Database_Execute obj,String time_date,String message,String send_receive,String sender,String reciever,String opposite_person_number,String status,String image_loc,String video_loc,String opposite_person_message_id)
    {
        SQLiteDatabase SQ =obj.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(MessageSingleInfo.DATE_TIME, time_date);
        cv.put(MessageSingleInfo.MESSAGE, message);
        cv.put(MessageSingleInfo.SEND_RECIEVE, send_receive);
        cv.put(MessageSingleInfo.SENDER,sender);
        cv.put(MessageSingleInfo.RECIEVER, reciever);
        cv.put(MessageSingleInfo.OPPOSITE_PERSON_NUMBER,opposite_person_number);
        cv.put(MessageSingleInfo.STATUS,status);
        cv.put(MessageSingleInfo.IMAGE_LOC,image_loc);
        cv.put(MessageSingleInfo.VIDEO_LOC,video_loc);
        cv.put(MessageSingleInfo.OPPOSITE_PERSON_MESSAGE_ID,opposite_person_message_id);
        long k=SQ.insert(MessageSingleInfo.TABLE_NAME,null,cv);
        Log.d("statis","one row inserted"+MessageSingleInfo.TABLE_NAME+k+message);
        return k+"";
    }

    //*******************************************************RECENT_CHATS********************************************************

    public void insert_recent_chats(Chat_Database_Execute obj,String flag,String group_id,String opposite_person_number,String sender,String last_updated)
    {
        SQLiteDatabase SQ =obj.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(RecentChatsInfo.FLAG,flag);
        cv.put(RecentChatsInfo.GROUP_ID, group_id);
        cv.put(RecentChatsInfo.OPPOSITE_PERSON_NUMBER, opposite_person_number);
        cv.put(RecentChatsInfo.SENDER,sender);
        cv.put(RecentChatsInfo.LAST_UPDATED, last_updated);
        SQ.insert(RecentChatsInfo.TABLE_NAME,null,cv);
        Log.d("statis","one row inserted"+RecentChatsInfo.TABLE_NAME);
    }

    public Cursor get_recent_chats(Chat_Database_Execute obj)
    {
        SQLiteDatabase SQ=obj.getReadableDatabase();
        String[] coloumns={RecentChatsInfo.FLAG,RecentChatsInfo.GROUP_ID,RecentChatsInfo.OPPOSITE_PERSON_NUMBER,RecentChatsInfo.SENDER,RecentChatsInfo.LAST_UPDATED};
        Cursor cr=SQ.query(RecentChatsInfo.TABLE_NAME, coloumns, null, null, null, null,RecentChatsInfo.LAST_UPDATED+" DESC");
        return cr;
        //Cursor cr=SQ.query(MessageInfo.TABLE_NAME, coloumns, MessageInfo.GROUP_ID+"=?",new String[]{group_id}, null, null, null,20+"");
       // String query="SELECT "+RecentChatsInfo.GROUP_ID+","+RecentChatsInfo.OPPOSITE_PERSON_NUMBER+","+RecentChatsInfo.FLAG+","+RecentChatsInfo.LAST_UPDATED+","+RecentChatsInfo.LAST_MESSAGE+","+RecentChatsInfo.COUNT_UNREAD+" FROM "+RecentChatsInfo.TABLE_NAME+" ORDER BY "+RecentChatsInfo.LAST_UPDATED+" DESC;";
    //    return SQ.rawQuery(query, null);
    }
    public boolean recent_chats_single_exists(Chat_Database_Execute obj,String opposite_person_number) {
        SQLiteDatabase SQ=obj.getReadableDatabase();

        String[] coloumns={RecentChatsInfo.OPPOSITE_PERSON_NUMBER};
        Cursor cr=SQ.query(RecentChatsInfo.TABLE_NAME, coloumns, RecentChatsInfo.OPPOSITE_PERSON_NUMBER+"=?",new String[]{opposite_person_number}, null, null, null,null);
        if(cr.getCount() > 0){
            cr.close();
            return true;
        }
        cr.close();
        return false;
    }
    public boolean recent_chats_group_exists(Chat_Database_Execute obj,String group_id) {
        SQLiteDatabase SQ=obj.getReadableDatabase();

        String[] coloumns={RecentChatsInfo.GROUP_ID};
        Cursor cr=SQ.query(RecentChatsInfo.TABLE_NAME, coloumns, RecentChatsInfo.GROUP_ID+"=?",new String[]{group_id}, null, null, null,null);
        if(cr.getCount() > 0){
            cr.close();
            return true;
        }
        cr.close();
        return false;
    }
    public void update_recent_chats_single(Chat_Database_Execute obj,String opposite_person_number,String date)
    {
        SQLiteDatabase SQ=obj.getWritableDatabase();
       // ContentValues cv = new ContentValues();
       // cv.put(RecentChatsInfo.LAST_UPDATED, date);
       // SQ.update(RecentChatsInfo.TABLE_NAME, cv, RecentChatsInfo.OPPOSITE_PERSON_NUMBER+"="+opposite_person_number, null);
       // Log.d("updated",RecentChatsInfo.TABLE_NAME+" updated");
        String strSQL = "UPDATE "+ RecentChatsInfo.TABLE_NAME +" SET "+ RecentChatsInfo.LAST_UPDATED+"='"+ date+"' WHERE "+RecentChatsInfo.OPPOSITE_PERSON_NUMBER +"='"+opposite_person_number+"'";

        SQ.execSQL(strSQL);
    }

    public void update_recent_chats_groups(Chat_Database_Execute obj,String group_id,String date)
    {
        SQLiteDatabase SQ=obj.getWritableDatabase();
    //    ContentValues cv=new ContentValues();
      //  cv.put(RecentChatsInfo.LAST_UPDATED,date);
        //int x=SQ.update(RecentChatsInfo.TABLE_NAME, cv, RecentChatsInfo.GROUP_ID+"="+group_id, null);
        String strSQL = "UPDATE "+ RecentChatsInfo.TABLE_NAME +" SET "+ RecentChatsInfo.LAST_UPDATED+"='"+ date+"' WHERE "+RecentChatsInfo.GROUP_ID +"='"+group_id+"'";
        SQ.execSQL(strSQL);
        //return x;
    }
    //*******************************************************MESSAGE_UNSEND_SINGLE********************************************************

    public void insert_message_unsend_single(Chat_Database_Execute obj,String time_date,String message,String send_receive,String sender,String reciever,String opposite_person_number,String status,String image_loc,String video_loc,String message_id)
    {
        SQLiteDatabase SQ =obj.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(MessageUnsendSingleInfo.DATE_TIME, time_date);
        cv.put(MessageUnsendSingleInfo.MESSAGE, message);
        cv.put(MessageUnsendSingleInfo.SEND_RECIEVE, send_receive);
        cv.put(MessageUnsendSingleInfo.SENDER,sender);
        cv.put(MessageUnsendSingleInfo.RECIEVER, reciever);
        cv.put(MessageUnsendSingleInfo.OPPOSITE_PERSON_NUMBER,opposite_person_number);
        cv.put(MessageUnsendSingleInfo.STATUS,status);
        cv.put(MessageUnsendSingleInfo.IMAGE_LOC,image_loc);
        cv.put(MessageUnsendSingleInfo.VIDEO_LOC,video_loc);
        cv.put(MessageUnsendSingleInfo.MESSAGE_ID,message_id);
        long k=SQ.insert(MessageUnsendSingleInfo.TABLE_NAME,null,cv);
        Log.d("statis","one row inserted"+MessageUnsendSingleInfo.TABLE_NAME+k+message);
    }
    public void delete_message_unsend_single_selected(String message_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ MessageUnsendSingleInfo.TABLE_NAME+" where "+MessageUnsendSingleInfo.MESSAGE_ID+"="+message_id);
        Log.d("database", "deleted "+MessageUnsendSingleInfo.TABLE_NAME+" id:"+message_id);
    }

    public Cursor fetch_message_unsend_single(Chat_Database_Execute obj)
    {
        SQLiteDatabase SQ=obj.getReadableDatabase();
        String[] coloumns={MessageUnsendSingleInfo.MESSAGE_ID,MessageUnsendSingleInfo.MESSAGE};
        Cursor cr=SQ.query(MessageUnsendSingleInfo.TABLE_NAME, coloumns, null,null, null, null, null,null);
        return cr;
    }

    //*******************************************************MESSAGE_GROUP*************************************************************

    public void delete_message_group_all()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ MessageGroupInfo.TABLE_NAME);
        Log.d("database", "deleted "+MessageGroupInfo.TABLE_NAME);
    }
    public void delete_message_group_selected(String message_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ MessageGroupInfo.TABLE_NAME+" where "+MessageGroupInfo.MESSAGE_ID+"="+message_id);
        Log.d("database", "deleted "+MessageGroupInfo.TABLE_NAME+" id:"+message_id);
    }
    Cursor fetch_message_group_first(Chat_Database_Execute obj,String group_id)
    {
        SQLiteDatabase SQ=obj.getReadableDatabase();
        String[] coloumns={MessageGroupInfo.MESSAGE_ID,MessageGroupInfo.MESSAGE,MessageGroupInfo.DATE_TIME,MessageGroupInfo.SEND_RECIEVE,MessageGroupInfo.SENDER,MessageGroupInfo.GROUP_ID,MessageGroupInfo.STATUS,MessageGroupInfo.IMAGE_LOC,MessageGroupInfo.VIDEO_LOC,MessageGroupInfo._MEMBER,MessageGroupInfo.NEW_NAME,MessageGroupInfo.ADD,MessageGroupInfo.NAME_CHANGE,MessageGroupInfo.REMOVE,MessageGroupInfo.LEFT,MessageGroupInfo.ICON_CHANGE};
        Cursor cr=SQ.query(MessageGroupInfo.TABLE_NAME, coloumns, MessageGroupInfo.GROUP_ID+"=?",new String[]{group_id}, null, null, MessageGroupInfo.MESSAGE_ID+" DESC ","15");
        return cr;
    }
    public Cursor fetch_message_chat_next_group(Chat_Database_Execute obj, String group_id,String last_id)
    {
        SQLiteDatabase SQ=obj.getReadableDatabase();
        String[] coloumns={MessageGroupInfo.MESSAGE_ID,MessageGroupInfo.MESSAGE,MessageGroupInfo.DATE_TIME,MessageGroupInfo.SEND_RECIEVE,MessageGroupInfo.SENDER,MessageGroupInfo.GROUP_ID,MessageGroupInfo.STATUS,MessageGroupInfo.IMAGE_LOC,MessageGroupInfo.VIDEO_LOC,MessageGroupInfo._MEMBER,MessageGroupInfo.NEW_NAME,MessageGroupInfo.ADD,MessageGroupInfo.NAME_CHANGE,MessageGroupInfo.REMOVE,MessageGroupInfo.LEFT,MessageGroupInfo.ICON_CHANGE};
        Cursor cr=SQ.query(MessageGroupInfo.TABLE_NAME, coloumns, MessageGroupInfo.GROUP_ID+"=? AND "+MessageGroupInfo.MESSAGE_ID+"<?",new String[]{group_id,last_id}, null, null, MessageGroupInfo.MESSAGE_ID+" DESC ","5");
        return cr;
    }
    public Cursor fetch_last_message_chat_group(Chat_Database_Execute obj, String group_id)
    {
        SQLiteDatabase SQ=obj.getReadableDatabase();
        String[] coloumns={MessageGroupInfo.MESSAGE_ID,MessageGroupInfo.MESSAGE,MessageGroupInfo.STATUS,MessageGroupInfo.SEND_RECIEVE};//,MessageSingleInfo.DATE_TIME,MessageSingleInfo.SEND_RECIEVE,MessageSingleInfo.SENDER,MessageSingleInfo.RECIEVER,MessageSingleInfo.OPPOSITE_PERSON_NUMBER,MessageSingleInfo.STATUS,MessageSingleInfo.IMAGE_LOC,MessageSingleInfo.VIDEO_LOC};
        Cursor cr=SQ.query(MessageGroupInfo.TABLE_NAME, coloumns, MessageGroupInfo.GROUP_ID+"=?",new String[]{group_id}, null, null, MessageGroupInfo.MESSAGE_ID+" DESC ","1");
        return cr;
    }
    public String[] get_count_unread_message_group(Chat_Database_Execute obj,String group_id) {
        long i = 0;
        int count=0;
        String message_id="";
        SQLiteDatabase SQ = obj.getReadableDatabase();
        String[] coloumns = {MessageGroupInfo.MESSAGE_ID, MessageGroupInfo.MESSAGE, MessageGroupInfo.STATUS, MessageGroupInfo.SEND_RECIEVE};//,MessageSingleInfo.DATE_TIME,MessageSingleInfo.SEND_RECIEVE,MessageSingleInfo.SENDER,MessageSingleInfo.RECIEVER,MessageSingleInfo.OPPOSITE_PERSON_NUMBER,MessageSingleInfo.STATUS,MessageSingleInfo.IMAGE_LOC,MessageSingleInfo.VIDEO_LOC};
        for (i = 0; ; i++) {
            if(i==0)
            {
                Cursor cr = SQ.query(MessageGroupInfo.TABLE_NAME, coloumns, MessageGroupInfo.GROUP_ID + "=? and "+MessageGroupInfo.SEND_RECIEVE+"=? and "+MessageGroupInfo.STATUS+"=?", new String[]{group_id,"1","0"}, null, null, MessageGroupInfo.MESSAGE_ID + " DESC ", "1");
                if(cr.getCount()>0)
                {
                    message_id=cr.getString(0);
                    count++;
                }
                else
                    break;
            }
            else
            {
                Cursor cr = SQ.query(MessageGroupInfo.TABLE_NAME, coloumns, MessageGroupInfo.GROUP_ID + "=? and "+MessageGroupInfo.SEND_RECIEVE+"=? and "+MessageGroupInfo.STATUS+"=? and "+MessageGroupInfo.MESSAGE_ID+"<?", new String[]{group_id,"1","0",message_id}, null, null, MessageGroupInfo.MESSAGE_ID + " DESC ", "1");
                if(cr.getCount()>0)
                {
                    message_id=cr.getString(0);
                    count++;
                }
                else
                    break;
            }
        }
        return new String[]{count+"",message_id};
    }
    public void update_status_message_group(Chat_Database_Execute obj,String message_id,String status)
    {
        SQLiteDatabase SQ =obj.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MessageGroupInfo.STATUS,status);
        SQ.update(MessageGroupInfo.TABLE_NAME, cv, MessageGroupInfo.MESSAGE_ID+"=?", new String[]{message_id});
    }
    public String insert_message_group_boolean(Chat_Database_Execute obj,boolean icon_change,boolean name_change,boolean left,boolean remove,boolean add,String member,String new_name,String group_id,String sender)
    {
        SQLiteDatabase SQ =obj.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(MessageGroupInfo._MEMBER,member);
        cv.put(MessageGroupInfo.NEW_NAME,new_name);
        cv.put(MessageGroupInfo.ADD,add);
        cv.put(MessageGroupInfo.REMOVE,remove);
        cv.put(MessageGroupInfo.LEFT,left);
        cv.put(MessageGroupInfo.ICON_CHANGE,icon_change);
        cv.put(MessageGroupInfo.NAME_CHANGE,name_change);
        cv.put(MessageGroupInfo.GROUP_ID,group_id);
        cv.put(MessageGroupInfo.SENDER,sender);
        long k=SQ.insert(MessageGroupInfo.TABLE_NAME,null,cv);
        return k+"";
    }
    public String insert_message_group(Chat_Database_Execute obj,String time_date,String message,String send_receive,String sender,String group_id,String status,String image_loc,String video_loc)
    {
        SQLiteDatabase SQ =obj.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(MessageGroupInfo.DATE_TIME, time_date);
        cv.put(MessageGroupInfo.MESSAGE, message);
        cv.put(MessageGroupInfo.SEND_RECIEVE, send_receive);
        cv.put(MessageGroupInfo.SENDER,sender);
        cv.put(MessageGroupInfo.GROUP_ID,group_id);
        cv.put(MessageGroupInfo.STATUS,status);
        cv.put(MessageGroupInfo.IMAGE_LOC,image_loc);
        cv.put(MessageGroupInfo.VIDEO_LOC,video_loc);
        cv.put(MessageGroupInfo._MEMBER,"");
        cv.put(MessageGroupInfo.ADD,false);
        cv.put(MessageGroupInfo.REMOVE,false);
        cv.put(MessageGroupInfo.LEFT,false);
        cv.put(MessageGroupInfo.ICON_CHANGE,false);
        cv.put(MessageGroupInfo.NAME_CHANGE,false);
        long k=SQ.insert(MessageGroupInfo.TABLE_NAME,null,cv);

        Log.d("statis","one row inserted"+MessageGroupInfo.TABLE_NAME+k+message);
        return k+"";
    }
    //*******************************************************GROUP MESSAGE UNSEND*************************************************************


    public String insert_unsend_message_group(Chat_Database_Execute obj,String message_id,String time_date,String message,String send_receive,String sender,String group_id,String status,String image_loc,String video_loc)
    {
        SQLiteDatabase SQ =obj.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(MessageUnsendGroupInfo.DATE_TIME, time_date);
        cv.put(MessageUnsendGroupInfo.MESSAGE, message);
        cv.put(MessageUnsendGroupInfo.SEND_RECIEVE, send_receive);
        cv.put(MessageUnsendGroupInfo.SENDER,sender);
        cv.put(MessageUnsendGroupInfo.GROUP_ID,group_id);
        cv.put(MessageUnsendGroupInfo.STATUS,status);
        cv.put(MessageUnsendGroupInfo.IMAGE_LOC,image_loc);
        cv.put(MessageUnsendGroupInfo.VIDEO_LOC,video_loc);
        cv.put(MessageUnsendGroupInfo.MESSAGE_ID,message_id);
        long k=SQ.insert(MessageUnsendGroupInfo.TABLE_NAME,null,cv);
        Log.d("statis","one row inserted"+MessageUnsendGroupInfo.TABLE_NAME+k+message);
        return k+"";
    }
    public void delete_message_unsend_group_selected(String message_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ MessageUnsendGroupInfo.TABLE_NAME+" where "+MessageUnsendGroupInfo.MESSAGE_ID+"="+message_id);
        Log.d("database", "deleted "+MessageUnsendGroupInfo.TABLE_NAME+" id:"+message_id);
    }

    //*******************************************************GROUP*************************************************************

    public void insert_groups(Chat_Database_Execute obj,String group_name,String topic,String description,String date,String memebr_count,String status,String group_public_id /*,String profile_pic */)
    {
        SQLiteDatabase SQ =obj.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(GroupDatabase.GROUP_NAME, group_name);
        cv.put(GroupDatabase.TOPIC, topic);
        cv.put(GroupDatabase.DESCRIPTION, description);
        cv.put(GroupDatabase.CREATE_DATE, date);
        cv.put(GroupDatabase.MEMBER_COUNT, memebr_count);
        cv.put(GroupDatabase.GROUP_ID, group_public_id);
        cv.put(GroupDatabase.STATUS,status);//0 public 1 private

        //	cv.put(ContactsUnregisteredInfo.PROFILE_PIC, profile_pic);
        long k=SQ.insert(GroupDatabase.TABLE_NAME,null,cv);
        Log.d("statis","one row inserted"+GroupDatabase.TABLE_NAME);
    }

    public void update_group_count(Chat_Database_Execute obj,String group_id,String count)
    {
        SQLiteDatabase SQ =obj.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(GroupDatabase.MEMBER_COUNT,count);
        SQ.update(GroupDatabase.TABLE_NAME, cv, GroupDatabase.GROUP_ID+"=?", new String[]{group_id});
    }
    public void delete_groups()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ GroupDatabase.TABLE_NAME);
        Log.d("database", "deleted "+GroupDatabase.TABLE_NAME);
    }
    Cursor fetch_groups(Chat_Database_Execute obj)
    {
        SQLiteDatabase SQ=obj.getReadableDatabase();
        String[] coloumns={GroupDatabase.GROUP_ID,GroupDatabase.GROUP_NAME,GroupDatabase.TOPIC,GroupDatabase.DESCRIPTION,GroupDatabase.CREATE_DATE,GroupDatabase.MEMBER_COUNT,GroupDatabase.STATUS};
        Cursor cr=SQ.query(GroupDatabase.TABLE_NAME, coloumns, null, null, null, null,null);
        return cr;
    }

    Cursor fetch_group_selected(Chat_Database_Execute obj,String group_id)
    {
        SQLiteDatabase SQ=obj.getReadableDatabase();
        String[] coloumns={GroupDatabase.GROUP_ID,GroupDatabase.GROUP_NAME,GroupDatabase.TOPIC,GroupDatabase.DESCRIPTION,GroupDatabase.CREATE_DATE,GroupDatabase.MEMBER_COUNT,GroupDatabase.STATUS};
        Cursor cr=SQ.query(GroupDatabase.TABLE_NAME, coloumns, GroupMemberDatabase.GROUP_ID+"=?", new String[]{group_id}, null, null,null);
        return cr;
    }
    public void delete_group_selected(String group_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ GroupDatabase.TABLE_NAME+" where "+GroupDatabase.GROUP_ID+"="+group_id);
        Log.d("database", "deleted "+GroupMemberDatabase.TABLE_NAME);
    }



    //*******************************************************GROUP MEMBERS*************************************************************

    public void putinfo_group_members(Chat_Database_Execute obj,String group_id,String number,String admin,String rank_integer,String enter_date)
    {
        SQLiteDatabase SQ =obj.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(GroupMemberDatabase.GROUP_ID, group_id);
        cv.put(GroupMemberDatabase.NUMBER, number);
        cv.put(GroupMemberDatabase.ADMIN, admin);
        cv.put(GroupMemberDatabase.RANK, rank_integer);
        cv.put(GroupMemberDatabase.ENTER_DATE, enter_date);

        long k=SQ.insert(GroupMemberDatabase.TABLE_NAME,null,cv);
        Log.d("statis","one row inserted"+GroupMemberDatabase.TABLE_NAME);
    }
    public void delete_group_members(String group_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ GroupMemberDatabase.TABLE_NAME+" where "+GroupMemberDatabase.GROUP_ID+"="+group_id);
        Log.d("database", "deleted "+GroupMemberDatabase.TABLE_NAME);
    }
    public void delete_group_member_selected(String number,String group_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        int x=db.delete(GroupMemberDatabase.TABLE_NAME,
                GroupMemberDatabase.GROUP_ID + "=? AND " + GroupMemberDatabase.NUMBER + "=?",
                new String[] {group_id,number});
        //int x=db.delete(GroupMemberDatabase.TABLE_NAME, GroupMemberDatabase.GROUP_ID + "=" + group_id+" and "+GroupMemberDatabase.NUMBER+"="+number, null);
        //db.execSQL("delete from "+ GroupMemberDatabase.TABLE_NAME+" where "+GroupMemberDatabase.NUMBER+"="+number+" and "+GroupMemberDatabase.GROUP_ID+"="+group_id);
        Log.d("databasemember", "deleted "+x+" "+GroupMemberDatabase.TABLE_NAME);
        Chat_Database_Execute obj=new Chat_Database_Execute(ctx,cur_number);
        Cursor c=fetch_group_members(obj,group_id);
        if(c.getCount()>0)
        {
            c.moveToFirst();
            do {
                Log.d("member",c.getString(0));

            }while (c.moveToNext());
        }
    }
    String get_max_rank_group(Chat_Database_Execute obj,String group_id)
    {
        SQLiteDatabase SQ=obj.getReadableDatabase();
       // String[] coloumns={GroupMemberDatabase.RANK};
       // Cursor cr=SQ.query(GroupMemberDatabase.TABLE_NAME, coloumns, GroupMemberDatabase.GROUP_ID+"=?",new String[]{group_id}, null, null, null);
       // cr.moveToFirst();
        Cursor cr=SQ.rawQuery("SELECT max("+GroupMemberDatabase.RANK+") FROM " +GroupMemberDatabase.TABLE_NAME+" WHERE "+GroupMemberDatabase.GROUP_ID+ "=?", new String[] {group_id});
        cr.moveToFirst();
        //if(cr.getCount()>0) {
           // do {
               // if(cr!=null) {
              //      Log.d("rank2", cr.getString(0));
            //    }
          //  } while (cr.moveToNext());
        //}
        cr.moveToFirst();
        if(cr.getCount()>0) {
              Log.d("rank1",cr.getString(0));
              return cr.getString(0);
        }
        else
            return "0";

    //    Cursor c=SQ.query(GroupMemberDatabase.TABLE_NAME, null, GroupMemberDatabase.RANK+"=(SELECT MAX("+GroupMemberDatabase.RANK+") FROM "+GroupMemberDatabase.TABLE_NAME+" WHERE "+GroupMemberDatabase.GROUP_ID+"="+group_id +")", null, null, null, null);
      //  c.moveToFirst();
       // if(c.getCount()>0) {
         //   Log.d("rank1",c.getString(0));
           // return c.getString(0);
        //}
        //else
          //  return "0";
    }
    public Cursor fetch_group_members(Chat_Database_Execute obj, String group_id)
    {
        SQLiteDatabase SQ=obj.getReadableDatabase();
        String[] coloumns={GroupMemberDatabase.NUMBER,GroupMemberDatabase.ADMIN,GroupMemberDatabase.RANK,GroupMemberDatabase.ENTER_DATE};
        Cursor cr=SQ.query(GroupMemberDatabase.TABLE_NAME, coloumns, GroupMemberDatabase.GROUP_ID+"=?",new String[]{group_id}, null, null, null);
        return cr;
    }
    public boolean check_isadmin_group_members(Chat_Database_Execute obj,String group_id,String number)
    {
        SQLiteDatabase SQ=obj.getReadableDatabase();
        String[] coloumns={GroupMemberDatabase.ADMIN};
        Cursor cr=SQ.query(GroupMemberDatabase.TABLE_NAME, coloumns, GroupMemberDatabase.GROUP_ID+"=? and "+GroupMemberDatabase.NUMBER+"=?",new String[]{group_id,number}, null, null, null);
        cr.moveToFirst();
        if(cr.getCount()>0)
        {
            if(cr.getString(0).equals("1"))
                return true;
            else
                return false;
        }
        else
            return false;
    }

    public Cursor get_groups_member(Chat_Database_Execute obj,String number)
    {
        SQLiteDatabase SQ=obj.getReadableDatabase();
        String[] coloumns={GroupMemberDatabase.GROUP_ID};
        Cursor cr=SQ.query(GroupMemberDatabase.TABLE_NAME, coloumns, GroupMemberDatabase.NUMBER+"=?",new String[]{number}, null, null, null);
        return cr;
    }
}
