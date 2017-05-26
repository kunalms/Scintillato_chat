package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by VIVEK on 13-03-2017.
 */
public class Notification_Request_Adapter extends RecyclerView.Adapter<Notification_Request_Adapter.Request_Holder> {
//ljjf

    private List<Notification_Request_List> notification_request_list=new ArrayList<>();

    public class Request_Holder extends RecyclerView.ViewHolder {
        public final View view;
        
        TextView group_id,group_name,user_number,user_name;
        ImageView image;
        Button accept,reject;
        
        public Request_Holder(View row) {
            super(row);
            this.view = row;

            image=(ImageView)row.findViewById(R.id.iv_notification_request_row_image);
            group_id=(TextView) row.findViewById(R.id.tv_notification_request_row_group_id);
            //like=(ImageButton)row.findViewById(R.id.bt_answer_row_like);
            group_name=(TextView)row.findViewById(R.id.tv_notification_request_row_group_name);
            user_number=(TextView) row.findViewById(R.id.tv_notification_request_row_user_number);
            accept=(Button)row.findViewById(R.id.btn_notification_request_row_accept);
            reject=(Button)row.findViewById(R.id.btn_notification_request_row_reject);
        }
    }

    private Context ctx;
    private String cur_number,cur_user_id;
    public Notification_Request_Adapter(Context ctx, List<Notification_Request_List> notification_request_list) {
        this.notification_request_list = notification_request_list;
        this.ctx=ctx;
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(ctx,cur_number);
        Cursor c=obj.get_my_details(obj);
        c.moveToFirst();
        if(c.getCount()>0)
            cur_user_id=c.getString(0);
    }
    @Override
    public Notification_Request_Adapter.Request_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_request_row, parent, false);

        return new Request_Holder(itemView);
    }

    @Override
    public void onBindViewHolder(final Notification_Request_Adapter.Request_Holder request_holder, int position) {
        final Notification_Request_List notification_request_list =this.notification_request_list.get(position);
        request_holder.setIsRecyclable(false);

        //request_holder.category.setText(notification_request_list.getCategory());
        request_holder.user_number.setText(notification_request_list.getUser_number());
        request_holder.group_id.setText(notification_request_list.getGroup_id());
        request_holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        request_holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }//

    @Override
    public int getItemCount() {
        return notification_request_list.size();
    }
}