package com.scintillato.scintillatochat;
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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Trending_Group_Adapter extends ArrayAdapter{

    private List<Trending_Group_List> list=new ArrayList<Trending_Group_List>();
    Context ctx;
    public Trending_Group_Adapter(Context context, int resource) {
        super(context, resource);
        ctx=context;
        // TODO Auto-generated constructor stub
    }

    public void add(Trending_Group_List object)
    {
        super.add(object);
        list.add(object);
    }
    public void remove(Trending_Group_List object)
    {
        super.remove(object);
        list.remove(object);
    }
    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView,ViewGroup parent)
    {
        View row;
        row=convertView;
        final Home_Page_holder home_page_holder;
        if(row==null)
        {
            LayoutInflater layoutinflator=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutinflator.inflate(R.layout.trending_group_row,parent,false );
            home_page_holder= new Home_Page_holder();
            home_page_holder.name=(TextView)row.findViewById(R.id.tv_trending_group_row_name);
            home_page_holder.datetime=(TextView)row.findViewById(R.id.tv_trending_group_row_datetime);
            home_page_holder.count=(TextView)row.findViewById(R.id.tv_trending_group_row_count);
            home_page_holder.dp=(CircleImageView)row.findViewById(R.id.iv_trending_group_row_dp);
           row.setTag(home_page_holder);
        }
        else
        {
            home_page_holder=(Home_Page_holder)row.getTag();
        }

        Trending_Group_List issue_1_list=(Trending_Group_List)this.getItem(position);
        home_page_holder.name.setText(issue_1_list.getGroup_name());
        home_page_holder.datetime.setText(issue_1_list.getGroup_create_date());
        home_page_holder.count.setText("Members:"+issue_1_list.getGroup_count());
        Picasso.with(ctx).load("http://scintillato.esy.es/fetch_group_profile_png_id.php?group_id=" + issue_1_list.getGroup_public_id()).placeholder(home_page_holder.dp.getDrawable()).into(home_page_holder.dp);
            return row;
    }
    int flag;

    static class Home_Page_holder
    {
        TextView name,count,datetime;
        CircleImageView dp;
    }
}
