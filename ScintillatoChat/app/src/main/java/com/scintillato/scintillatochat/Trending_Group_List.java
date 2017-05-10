package com.scintillato.scintillatochat;

import android.util.Log;

/**
 * Created by VIVEK on 30-01-2017.
 */

public class Trending_Group_List {

    String group_public_id,group_name,group_topic,group_description,group_create_date,group_count,member_phone,category_id;
    public Trending_Group_List(String group_public_id,String group_name,String group_topic,String group_description,String group_create_date,String group_count,String member_phone,String category_id)
    {
        this.group_public_id=group_public_id;
        this.group_name=group_name;
        this.group_topic=group_topic;
        Log.d("getGroudptopic",group_topic);
        this.group_description=group_description;
        this.group_create_date=group_create_date;
        this.group_count=group_count;
        this.member_phone=member_phone;
        this.category_id=category_id;
    }

    public String getGroup_public_id() {
        return group_public_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public String getGroup_topic() {
        return group_topic;
    }

    public String getGroup_description() {
        return group_description;
    }

    public String getGroup_create_date() {
        return group_create_date;
    }

    public String getGroup_count() {
        return group_count;
    }

    public String getMember_phone() {
        return member_phone;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setGroup_public_id(String group_public_id) {
        this.group_public_id = group_public_id;
    }

    public void setGroup_topic(String group_topic) {
        this.group_topic = group_topic;
    }

    public void setGroup_description(String group_description) {
        this.group_description = group_description;
    }

    public void setGroup_count(String group_count) {
        this.group_count = group_count;
    }

    public void setGroup_create_date(String group_create_date) {
        this.group_create_date = group_create_date;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public void setMember_phone(String member_phone) {
        this.member_phone = member_phone;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }
}
