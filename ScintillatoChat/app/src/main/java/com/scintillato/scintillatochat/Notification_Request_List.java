package com.scintillato.scintillatochat;

/**
 * Created by VIVEK on 25-05-2017.
 */

public class Notification_Request_List {

    String group_id,user_number;
    Notification_Request_List(String group_id,String user_number)
    {
        this.group_id=group_id;
        this.user_number=user_number;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getUser_number() {
        return user_number;
    }

    public void setUser_number(String user_number) {
        this.user_number = user_number;
    }
}
