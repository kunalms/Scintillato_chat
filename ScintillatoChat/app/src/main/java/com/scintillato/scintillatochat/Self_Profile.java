package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class Self_Profile extends AppCompatActivity {

    private TextView username,user_name,user_bio;
    private CircleImageView user_image;
    private String cur_number;
    private Button answer,question,groups,edit;
    private String user_id;
    private Bitmap bitmap_profile_pic;
    private Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_profile);
        user_image=(CircleImageView)findViewById(R.id.circleimageview_self_profile_pic);
        username=(TextView)findViewById(R.id.tv_username_profile_self);
        user_name=(TextView)findViewById(R.id.tv_user_profile_name);
        answer=(Button)findViewById(R.id.btn_self_profile_answer);
        question=(Button)findViewById(R.id.btn_self_profile_question) ;
        groups=(Button)findViewById(R.id.btn_self_profile_group);
        answer=(Button)findViewById(R.id.btn_self_profile_answer);
        user_bio=(TextView)findViewById(R.id.tv_self_profile_bio);
        edit=(Button)findViewById(R.id.btn_user_self_edit);
        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        ctx=this;

        disable_all_buttons();
        My_Details_Execute obj=new My_Details_Execute(getApplicationContext(),cur_number);
        Cursor c=obj.get_my_details(obj);
        if(c.getCount()>0) {
            c.moveToFirst();
            user_id=c.getString(0);
            user_name.setText(c.getString(3));
            username.setText("@"+c.getString(1));
            user_bio.setText(c.getString(5));
        }
        enable_all_buttons();
        c=obj.get_my_profile_pic(obj);
        if(c.getCount()>0) {
            c.moveToFirst();
            bitmap_profile_pic=getProfileImage(c.getString(0));
            //user_image.setImageBitmap(BitmapFactory.decodeFile(c.getString(0)));
            user_image.setImageBitmap(bitmap_profile_pic);
            Log.d("profile_pic2",c.getString(0));
            //.setImageBitmap(bitmap_profile_pic);
        }
      //  Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.userprofile100);
        //user_image.setImageBitmap(icon);

        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("abc","abc");
                Intent i=new Intent(getApplicationContext(),Self_Questions.class);
                i.putExtra("user_id",user_id);
                i.putExtra("user_name",user_name.getText().toString());
                i.putExtra("username",username.getText().toString());
                startActivity(i);
            }
        });
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Self_Answer.class);
                i.putExtra("user_id",user_id);
                i.putExtra("user_name",user_name.getText().toString());
                i.putExtra("username",username.getText().toString());
                startActivity(i);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Edit_Profile.class);
                i.putExtra("bio",user_bio.getText().toString());
                i.putExtra("user_name",user_name.getText().toString());
                i.putExtra("username",username.getText().toString());
                i.putExtra("user_id",user_id);
                startActivity(i);
            }
        });
        groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Self_Profile_Group.class);
                i.putExtra("user_id",user_id);
                startActivity(i);

            }
        });
    }
    void disable_all_buttons()
    {
        edit.setEnabled(false);
        groups.setEnabled(false);
        answer.setEnabled(false);
        question.setEnabled(false);
    }
    void enable_all_buttons()
    {
        edit.setEnabled(true);
        groups.setEnabled(true);
        answer.setEnabled(true);
        question.setEnabled(true);
    }
    public Bitmap getProfileImage(String u_profile_pic)
    {
        byte[] decodedString = Base64.decode(u_profile_pic, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }



}