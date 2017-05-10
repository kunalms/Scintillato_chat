package com.scintillato.scintillatochat;

import android.util.Log;
import android.widget.Toast;

/**
 * Created by adikundiv on 11-01-2017.
 */

public class Self_Answer_List {

    private String question_id,answer_id,answer,answer_time,answer_like_count,question,answer_count,comment_count,like_stat,user_name,user_id,answer_image_status;

    Self_Answer_List(String question_id,String answer_id,String answer,String answer_time,String answer_like_count,String question,String answer_count,String comment_count,String like_stat,String user_name,String user_id,String answer_image_status){
        this.question_id=question_id;
        this.answer_id=answer_id;
        this.answer=answer;
        this.answer_time=answer_time;
        this.answer_like_count=answer_like_count;
        this.question=question;
        this.answer_count=answer_count;
        this.comment_count=comment_count;
        this.like_stat=like_stat;
        this.user_name=user_name;
        this.user_id=user_id;
        this.answer_image_status=answer_image_status;
    }

    public String getAnswer_image_status() {
        return answer_image_status;
    }

    public void setAnswer_image_status(String answer_image_status) {
        this.answer_image_status = answer_image_status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getLike_stat() {
        return like_stat;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public String getAnswer_id() {
        return answer_id;
    }

    public String getAnswer() {
        return answer;
    }

    public String getAnswer_time() {
        return answer_time;
    }

    public String getAnswer_like_count() {
        return answer_like_count;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer_count() {
        return answer_count;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public void setAnswer_id(String answer_id) {
        this.answer_id = answer_id;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setAnswer_time(String answer_time) {
        this.answer_time = answer_time;
    }

    public void setAnswer_like_count(String answer_like_count) {
        this.answer_like_count = answer_like_count;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer_count(String answer_count) {
        this.answer_count = answer_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public void setLike_stat(String like_stat) {
        this.like_stat = like_stat;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}

