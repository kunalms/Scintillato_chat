package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class Comment_Feed_Community_Adapter extends ArrayAdapter {

    Context ctx;
    private List<Comment_Feed_Community_List> list =new ArrayList<Comment_Feed_Community_List>();
    String cur_user_id;
    Comment_Feed_Community_Adapter(Context context,int resources) {
        super(context, resources);
        ctx = context;
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        String cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(ctx,cur_number);
        Cursor c=obj.get_my_details(obj);
        c.moveToFirst();
        if (c.getCount()>0)
            cur_user_id=c.getString(0);
    }
    public void add(Comment_Feed_Community_List object)
    {
        super.add(object);
        list.add(object);
    }

    public void insert(Comment_Feed_Community_List obj,int pos)
    {
        super.insert(obj,pos);
        list.add(pos,obj);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        row=convertView;
        final Comment_Holder comment_holder;

        if(row==null)
        {
            LayoutInflater layoutinflator=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutinflator.inflate(R.layout.comment_feed_community_row,parent,false );
            comment_holder= new Comment_Holder();
            comment_holder.comment=(TextView) row.findViewById(R.id.tv_comment_feed_community_row_comment);
            comment_holder.username=(TextView) row.findViewById(R.id.tv_comment_feed_community_row_name);
            comment_holder.date=(TextView)row.findViewById(R.id.tv_comment_feed_community_row_time);
            comment_holder.profile=(CircleImageView) row.findViewById(R.id.iv_comment_feed_community_row_pic);

            row.setTag(comment_holder);
        }

        else
        {
            comment_holder= (Comment_Holder) row.getTag();
        }

        final Comment_Feed_Community_List Comment_Feed_Community_List =(Comment_Feed_Community_List)this.getItem(position);
        //answer_holder.category.setText(Comment_Feed_Community_List.getCategory());
        comment_holder.username.setText(Comment_Feed_Community_List.getUser_name());
        comment_holder.date.setText(Comment_Feed_Community_List.getComment_date_time());
        comment_holder.comment.setText(Comment_Feed_Community_List.getComment());

        comment_holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Comment_Feed_Community_List.getUser_id().equals(cur_user_id))
                {
                    Intent i = new Intent(ctx, Self_Profile.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(i);
                }
                else {
                    Intent i = new Intent(ctx, Profile_Other.class);
                    i.putExtra("user_id", Comment_Feed_Community_List.getUser_id());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(i);
                }

            }
        });

        return row;
    }


    static class Comment_Holder
    {
        TextView username,date,comment;
        RelativeLayout box;
        CircleImageView profile;
    }
}

