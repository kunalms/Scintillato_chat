package com.scintillato.scintillatochat;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by VIVEK on 16-03-2017.
 */

public class Message_Group_Chat_Public_Adapter extends RecyclerView.Adapter<Message_Group_Chat_Public_Adapter.Chat_Page_Holder>  {
    private ArrayList<Message_Chat_List> list=new ArrayList<>();

    @Override
    public Chat_Page_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_chat_row, parent, false);
        return new Chat_Page_Holder(itemView);
    }

    @Override
    public void onBindViewHolder(Chat_Page_Holder message_chat_holder, int position) {
        RelativeLayout.LayoutParams params  = new RelativeLayout.LayoutParams(message_chat_holder.box.getLayoutParams());

        message_chat_holder.setIsRecyclable(false);
        Message_Chat_List message_chat=list.get(position);
        if(message_chat.isGroup_name_change())
        {
            message_chat_holder.box.setBackgroundResource(R.drawable.cyanbubble);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER, RelativeLayout.TRUE);
            message_chat_holder.box.setLayoutParams(params);
            message_chat_holder.name.setVisibility(View.GONE);
            message_chat_holder.time.setVisibility(View.GONE);
            message_chat_holder.name.setVisibility(View.GONE);
            message_chat_holder.status.setVisibility(View.GONE);
            message_chat_holder.message.setText("Icon Changed");
        }
        else if(message_chat.isImage_icon_change())
        {
            message_chat_holder.box.setBackgroundResource(R.drawable.cyanbubble);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER, RelativeLayout.TRUE);
            message_chat_holder.box.setLayoutParams(params);
            message_chat_holder.name.setVisibility(View.GONE);
            message_chat_holder.time.setVisibility(View.GONE);
            message_chat_holder.name.setVisibility(View.GONE);
            message_chat_holder.status.setVisibility(View.GONE);
            message_chat_holder.message.setText("Group name changed to "+message_chat.getGroup_new_name());
        }
        else if(message_chat.isMember_added())
        {
            message_chat_holder.box.setBackgroundResource(R.drawable.cyanbubble);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER, RelativeLayout.TRUE);
            message_chat_holder.box.setLayoutParams(params);
            message_chat_holder.name.setVisibility(View.GONE);
            message_chat_holder.time.setVisibility(View.GONE);
            message_chat_holder.name.setVisibility(View.GONE);
            message_chat_holder.status.setVisibility(View.GONE);
            message_chat_holder.message.setText(message_chat.get_member()+" added");
        }
        else if(message_chat.isMember_left())
        {
            message_chat_holder.box.setBackgroundResource(R.drawable.cyanbubble);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER, RelativeLayout.TRUE);
            message_chat_holder.box.setLayoutParams(params);
            message_chat_holder.name.setVisibility(View.GONE);
            message_chat_holder.time.setVisibility(View.GONE);
            message_chat_holder.name.setVisibility(View.GONE);
            message_chat_holder.status.setVisibility(View.GONE);
            message_chat_holder.message.setText(message_chat.get_member()+" left");
            Log.e("left","left");
        }
        else if(message_chat.isMember_removed())
        {
            message_chat_holder.box.setBackgroundResource(R.drawable.cyanbubble);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER, RelativeLayout.TRUE);
            message_chat_holder.box.setLayoutParams(params);
            message_chat_holder.name.setVisibility(View.GONE);
            message_chat_holder.time.setVisibility(View.GONE);
            message_chat_holder.name.setVisibility(View.GONE);
            message_chat_holder.status.setVisibility(View.GONE);
            message_chat_holder.message.setText(message_chat.get_member()+" removed");
        }
        else {
            if (message_chat.isMine()) {
                if (message_chat.getStatus().equals("0")) {
                    message_chat_holder.status.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_done_white18));
                } else if (message_chat.getStatus().equals("1")) {
                    message_chat_holder.status.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_done_black18));
                }
                message_chat_holder.box.setBackgroundResource(R.drawable.cyanbubble);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                message_chat_holder.box.setLayoutParams(params);
                message_chat_holder.name.setTextColor(Color.parseColor("#ffffff"));
                message_chat_holder.message.setTextColor(Color.parseColor("#FFFFFF"));
                message_chat_holder.time.setTextColor(Color.parseColor("#FFFFFF"));
                message_chat_holder.status.setImageResource(R.drawable.ic_done_all_white18);
            }
            //If not mine then it is from sender to show other background and align to left
            else {
                message_chat_holder.box.setBackgroundResource(R.drawable.whitebubble);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                message_chat_holder.name.setTextColor(Color.parseColor("#000000"));
                message_chat_holder.message.setTextColor(Color.parseColor("#000000"));
                message_chat_holder.time.setTextColor(Color.parseColor("#000000"));
                message_chat_holder.box.setLayoutParams(params);
                message_chat_holder.status.setImageResource(R.drawable.done_all_black_18x18);
            }

            message_chat_holder.name.setText(message_chat.get_name());
            Log.d("time12", message_chat.get_time());
            message_chat_holder.time.setText(time(message_chat.get_time()));
            message_chat_holder.message.setText(message_chat.get_messaage());
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Chat_Page_Holder extends RecyclerView.ViewHolder {
        public final View view;
        TextView name,message,time;
        RelativeLayout box;
        ImageView status;
        public Chat_Page_Holder(View row) {
            super(row);
            this.view = row;
            name=(TextView)row.findViewById(R.id.tv_message_chat_row_name);
            message=(TextView)row.findViewById(R.id.tv_message_chat_row_message);
            time=(TextView) row.findViewById(R.id.tv_message_chat_row_time);
            box=(RelativeLayout)row.findViewById(R.id.message_chat_box);
            status=(ImageView)row.findViewById(R.id.img_message_chat_status);
        }
    }
    private Context ctx;
    private String cur_number,cur_user_id;
    public Message_Group_Chat_Public_Adapter(Context ctx, ArrayList<Message_Chat_List> list) {
        this.list = list;
        this.ctx=ctx;
       /* SharedPreferences sharedpreferences = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(ctx,cur_number);
        Cursor c=obj.get_my_details(obj);
        c.moveToFirst();
        if(c.getCount()>0)
            cur_user_id=c.getString(0);*/
    }
    String getdatemilli(long milli)
    {
        Date date= new Date(milli);

        DateFormat df;
        df = new SimpleDateFormat("dd/MM/yyyy");
        String dat=df.format(date);
        return String.valueOf(dat);
    }
    String getyesterday()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();
        DateFormat df;
        df = new SimpleDateFormat("dd/MM/yyyy");
        String dat=df.format(yesterday);
        return  String.valueOf(dat);
    }
    String gettoday()
    {
        Date today = Calendar.getInstance().getTime();
        DateFormat df;
        df = new SimpleDateFormat("dd/MM/yyyy");
        String dat=df.format(today);
        return  String.valueOf(dat);
    }
    String time (String var)
    {
        String [] dateParts = var.split(":");
        String hour = dateParts[0];
        String min = dateParts[1];
        String year = dateParts[2];
        hour=hour.substring(hour.length()-2,hour.length());
        return (hour+":"+min);
    }

}
