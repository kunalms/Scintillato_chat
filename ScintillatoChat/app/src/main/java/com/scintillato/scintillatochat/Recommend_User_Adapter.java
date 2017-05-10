package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

/**
 * Created by adikundiv on 31-01-2017.
 */

public class Recommend_User_Adapter extends ArrayAdapter {
    Context ctx;
    private List<Recommend_User_List> list = new ArrayList<Recommend_User_List>();
    private String cur_user_id,cur_number;

    public Recommend_User_Adapter(Context ctx, int resource) {
        super(ctx, resource);
        this.ctx = ctx;
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(ctx,cur_number);
        Cursor c=obj.get_my_details(obj);
        c.moveToFirst();
        if(c.getCount()>0)
            cur_user_id=c.getString(0);
    }

    public ArrayList<Recommend_User_List> getlist(){return  (ArrayList<Recommend_User_List>) list;}

    public void add(ArrayList<Recommend_User_List> object )
    {
        super.add(object);
        list=object;
    }
    public void add(Recommend_User_List object)
    {
        super.add(object);
        list.add(object);
    }public void insert(Recommend_User_List obj,int pos)
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
        row= convertView;

        ViewHolder viewHolder;

        if (row==null)
        {
            LayoutInflater layoutInflater=(LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row= layoutInflater.inflate(R.layout.recommend_user_row,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.button=(Button)row.findViewById(R.id.btn_recommend_user_follow);
            viewHolder.name=(TextView)row.findViewById(R.id.tv_recommend_user_name);
            viewHolder.follower=(TextView)row.findViewById(R.id.tv_recommend_user_follwers);
            viewHolder.profile_pic=(CircleImageView)row.findViewById(R.id.iv_recommend_user_profile);
            row.setTag(viewHolder);
        }
        else {
            viewHolder=(ViewHolder)row.getTag();
        }

        final Recommend_User_List recommend_user_list=(Recommend_User_List)this.getItem(position);
        Picasso.with(ctx).load("http://scintillato.esy.es/fetch_profile_pic_png_number.php?user_id=" + recommend_user_list.getId()).placeholder(viewHolder.profile_pic.getDrawable()).into(viewHolder.profile_pic);
            viewHolder.name.setText(recommend_user_list.getName());
        viewHolder.follower.setText(recommend_user_list.getFollower()+" followers");
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //on click event
            }
        });

        return  row;
    }


        public class ViewHolder {
        TextView name,follower;
        Button button;
            CircleImageView profile_pic;
    }

}
