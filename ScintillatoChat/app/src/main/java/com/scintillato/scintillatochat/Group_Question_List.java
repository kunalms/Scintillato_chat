package com.scintillato.scintillatochat;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by adikundiv on 11-01-2017.
 */

public class Group_Question_List {

    private String anonymous,username, question, question_id, like_count, time,user_name, user_id,no_answer,like_status,mobile_no;
    private String group_id;
    Group_Question_List(String question_id,String question,String like_count,String anonymous,String answer_count,String question_date,String user_id,String username,String mobile_no,String user_name,String group_id,String like_status) {
        this.question_id=question_id;
        this.question=question;
        this.like_count=like_count;
        this.anonymous=anonymous;
        this.no_answer=answer_count;
        this.time=question_date;
        this.user_id=user_id;
        this.username=username;
        this.mobile_no=mobile_no;
        this.user_name=user_name;
        this.like_status=like_status;
        this.group_id=group_id;
    }


    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUser(String user) {
        this.user_name = user;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public void setAnonymous(String anonymous) {
        this.anonymous = anonymous;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public String getLike_count() {
        return like_count;
    }

    public String getQuestion() {
        return question;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public String getTime() {
        return time;
    }

    public String getUser() {
        return user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }



    public String getNo_answer() {
        return no_answer;
    }

    public String getAnonymous() {
        return anonymous;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setNo_answer(String no_answer) {
        this.no_answer = no_answer;
    }

    public String getLike_status() {
        return like_status;
    }

    public void setLike_status(String like_status) {
        this.like_status = like_status;
    }

    long getmillisec()
    {
        long millisecond=0;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
