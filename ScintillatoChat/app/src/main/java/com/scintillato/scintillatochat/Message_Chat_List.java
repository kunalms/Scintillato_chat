package com.scintillato.scintillatochat;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message_Chat_List {
    private String name,message,group_id,message_id,number,time,status;
    private boolean ismine,image_icon_change,group_name_change,member_removed,member_left,member_added;
    private String _member,group_new_name;
    public Message_Chat_List(String name,String message,String group_id,String number,String time,String message_id) {
        // TODO Auto-generated constructor stub
        this.name=name;
        this.message=message;
        this.group_id=group_id;
        this.message_id=message_id;
        this.number=number;
        this.time=time;
        status="0";
    }
    Message_Chat_List(String group_id)
    {
        this.group_id=group_id;
    }

    public String getGroup_new_name() {
        return group_new_name;
    }

    public void setGroup_new_name(String group_new_name) {
        this.group_new_name = group_new_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String get_member() {
        return _member;
    }

    public void set_member(String _member) {
        this._member = _member;
    }

    public boolean isGroup_name_change() {
        return group_name_change;
    }

    public void setGroup_name_change(boolean group_name_change) {
        this.group_name_change = group_name_change;
    }

    public boolean isMember_added() {
        return member_added;
    }

    public void setMember_added(boolean member_added) {
        this.member_added = member_added;
    }

    public boolean isMember_left() {
        return member_left;
    }

    public void setMember_left(boolean member_left) {
        this.member_left = member_left;
    }

    public boolean isMember_removed() {
        return member_removed;
    }

    public void setMember_removed(boolean member_removed) {
        this.member_removed = member_removed;
    }

    public boolean isImage_icon_change() {
        return image_icon_change;
    }

    public void setImage_icon_change(boolean image_icon_change) {
        this.image_icon_change = image_icon_change;
    }


    void set_group_id(String group_id)
    {
        this.group_id=group_id;
    }
    String get_group_id()
    {
        return group_id;
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
        Log.d("time__1", time );
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

