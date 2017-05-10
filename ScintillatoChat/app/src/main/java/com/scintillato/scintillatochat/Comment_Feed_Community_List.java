package com.scintillato.scintillatochat;

import android.util.Log;

/**
 * Created by adikundiv on 13-01-2017.
 */

public class Comment_Feed_Community_List {

    private String user_name,user_id,answer_id,comment_id,comment,comment_date_time;

    Comment_Feed_Community_List(String comment_id,String comment_date_time,String comment,String user_id,String user_name,String answer_id){
        this.user_name = user_name;
        this.answer_id = answer_id;
        this.user_id= user_id;
        this.comment_id = comment_id;
        this.comment = comment;
        this.comment_date_time = comment_date_time;

    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setAnswer_id(String answer_id) {
        this.answer_id = answer_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setComment_date_time(String comment_date_time) {
        this.comment_date_time = comment_date_time;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getComment() {
        return comment;
    }

    public String getComment_date_time() {
        return comment_date_time;
    }

    public String getComment_id() {
        return comment_id;
    }

    public String getAnswer_id() {
        return answer_id;
    }
}


