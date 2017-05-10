package com.scintillato.scintillatochat;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message_Chat_Single_List {
    private String name,message,user_number,message_id,number,time;
    private boolean ismine;
    public Message_Chat_Single_List(String name,String message,String user_number,String number,String time,String message_id) {
        // TODO Auto-generated constructor stub
        this.name=name;
        this.message=message;
        this.user_number=user_number;
        this.message_id=message_id;
        this.number=number;
        this.time=time;
    }

    public String getUser_number() {
        return user_number;
    }

    public void setUser_number(String user_number) {
        this.user_number = user_number;
    }

    void set_message_id(String message_id)
    {
        this.message_id=message_id;
    }
    String get_message_id()
    {
        return message_id;
    }

    String get_name()
    {
        return name;
    }
    String get_messaage()
    {
        return message;
    }
    String get_time()
    {
        return time;
    }
    public boolean isMine() {
        if (this.ismine)
            return true;
        return false;
    }
    void set_ismine(Boolean val){this.ismine=val;}


    long getmillisec()
    {
        long millisecond=0;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date mdate = df.parse(time);
            millisecond=mdate.getTime();
        }
        catch (Exception e)
        {
            Log.d("error time","error time");
        }
        return millisecond;
    }

}

