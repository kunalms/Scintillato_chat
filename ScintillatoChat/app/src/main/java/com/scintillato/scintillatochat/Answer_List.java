package com.scintillato.scintillatochat;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by adikundiv on 13-01-2017.
 */

public class Answer_List {

    private String username, user, answer, answer_id, like_count, time, user_id,comment,user_pic,question_id,like_stat,comment_count,image_status;

    Answer_List(String username, String answer_id, String answer, String user_id, String time, String like_count, String user,String question_id,String like_stat,String comment_count,String image_status) {
        this.like_count = like_count;
        this.answer_id = answer_id;
        this.time = time;
        this.username = username;
        this.answer = answer;
        this.user_id = user_id;
        this.comment_count=comment_count;
        Log.d("user_id1",user_id+" "+this.user_id+" "+this.like_count+" "+like_count);

        this.user = user;
        this.question_id=question_id;
        this.like_stat=like_stat;
        this.image_status=image_status;
        //this.user_pic=user_pic;

    }

    public void setImage_status(String image_status) {
        this.image_status = image_status;
    }

    public String getImage_status() {
        return image_status;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public void setLike_stat(String like_stat) {
        this.like_stat = like_stat;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setAnswer_id(String answer_id) {
        this.answer_id = answer_id;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }

    public void setUser_pic(String user_pic) {
        this.user_pic = user_pic;
    }

    public String getUsername() {
        return username;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getAnswer() {
        return answer;
    }

    public String getUser() {
        return user;
    }

    public String getTime() {
        return time;
    }

    public String getAnswer_id() {
        return answer_id;
    }

    public String getComment() {
        return comment;
    }

    public String getLike_count() {
        return like_count;
    }

    public String getUser_pic() {
        return user_pic;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public String getLike_stat() {
        return like_stat;
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

