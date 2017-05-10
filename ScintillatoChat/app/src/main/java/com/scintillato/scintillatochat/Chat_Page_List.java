package com.scintillato.scintillatochat;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Chat_Page_List {
    private String name,message,group_id,opposite_person_number,flag,message_count,time,status,send_receive;
    public Chat_Page_List(String name,String message,String _id,String flag,String message_count,String time,String status,String send_receive) {
        // TODO Auto-generated constructor stub
        this.name=name;
        this.message=message;
        this.time=time;
        if(flag.equals("0"))//flag 0 group 1 single
            this.group_id=_id;
        else
            this.opposite_person_number=_id;

        this.flag=flag;
        this.message_count=message_count;
        this.send_receive=send_receive;
        this.status=status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setSend_receive(String send_receive) {
        this.send_receive = send_receive;
    }

    public String getSend_receive() {
        return send_receive;
    }

    public String getMessage_count() {
        return message_count;
    }

    public void setMessage_count(String message_count) {
        this.message_count = message_count;
    }

    void set_group_id(String group_id)
    {
        this.group_id=group_id;
    }
    String get_group_id()
    {
        return group_id;
    }
    String get_opposite_person_number(){return opposite_person_number;}
    String get_name()
    {
        return name;
    }
    String get_messaage()
    {
        return message;
    }
    String get_flag(){return flag;}
    public String getTime() {
        return time;
    }
    public void setTime_home(String time) {
        this.time = time;
    }

    long getmillisec()
    {
        long millisecond=0;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
