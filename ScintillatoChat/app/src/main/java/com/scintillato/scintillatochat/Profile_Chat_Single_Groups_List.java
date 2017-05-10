package com.scintillato.scintillatochat;

/**
 * Created by VIVEK on 09-03-2017.
 */

public class Profile_Chat_Single_Groups_List {
    String group_name,group_id,group_members,group_image_url;
    Profile_Chat_Single_Groups_List(String group_id,String group_name)
    {
        this.group_id=group_id;
       // this.group_image_url=group_image_url;
        //this.group_members=group_members;
        this.group_name=group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_id() {
        return group_id;
    }

    public String getGroup_image_url() {
        return group_image_url;
    }

    public String getGroup_members() {
        return group_members;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public void setGroup_image_url(String group_image_url) {
        this.group_image_url = group_image_url;
    }

    public void setGroup_members(String group_members) {
        this.group_members = group_members;
    }
}
