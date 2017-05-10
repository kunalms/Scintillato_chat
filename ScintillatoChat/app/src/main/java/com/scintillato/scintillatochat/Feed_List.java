package com.scintillato.scintillatochat;

import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by adikundiv on 11-01-2017.
 */

public class Feed_List {

    private String username, user, question, question_id, like_count, time, user_id,no_answer,like_status,anonymous,image_status;
    private String[] category;

    Feed_List(String username, String question_id, String question, String user_id, String time, String like_count, String[] category, String user,String no_answer,String like_status,String anonymous,String image_status) {
        this.like_count = like_count;
        this.question_id = question_id;
        this.time = time;
        this.username = username;
        this.question = question;
        this.category = category;
        this.user_id = user_id;
        this.user = user;
        this.no_answer=no_answer;
        this.like_status=like_status;
        this.anonymous=anonymous;
        this.image_status=image_status;
    }

    public String getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(String anonymous) {
        this.anonymous = anonymous;
    }

    public void setCategory(String[] category) {
        this.category = category;
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
        this.user = user;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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
        return user;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String[] getCategory() {
        return category;
    }

    public String getNo_answer() {
        return no_answer;
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

    public String getImage_status() {
        return image_status;
    }

    public void setImage_status(String image_status) {
        this.image_status = image_status;
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
