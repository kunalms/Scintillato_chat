package com.scintillato.scintillatochat;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Chat_Message_Single_List {
    private String name,message,message_id,sender,time,opposite_person_number,status,other_person_message_id;
    private boolean ismine,unseen=false;
    private String unseen_count;
    public Chat_Message_Single_List(boolean unseen,String unseen_count)
    {
        this.unseen=unseen;
        this.unseen_count=unseen_count;
    }
    public Chat_Message_Single_List(String name,String message,String sender,String time,String message_id,String opposite_person_number,String status,String other_person_message_id) {
        this.name=name;
        this.message=message;
        this.message_id=message_id;
        this.sender=sender;
        this.time=time;
        this.status=status;
        this.opposite_person_number=opposite_person_number;
        this.other_person_message_id=other_person_message_id;
    }

    public String getUnseen_count() {
        return unseen_count;
    }

    public void setUnseen_count(String unseen_count) {
        this.unseen_count = unseen_count;
    }

    public boolean isUnseen() {
        return unseen;
    }

    public void setUnseen(boolean unseen) {
        this.unseen = unseen;
    }

    public String getOther_person_message_id() {
        return other_person_message_id;
    }

    public void setOther_person_message_id(String other_person_message_id) {
        this.other_person_message_id = other_person_message_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOpposite_person_number() {
        return opposite_person_number;
    }

    public void setOpposite_person_number(String opposite_person_number) {
        this.opposite_person_number = opposite_person_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

