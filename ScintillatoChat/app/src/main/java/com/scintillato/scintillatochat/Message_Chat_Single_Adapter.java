package com.scintillato.scintillatochat;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class Message_Chat_Single_Adapter extends ArrayAdapter implements StickyListHeadersAdapter{

    private List<Message_Chat_Single_List> list=new ArrayList<Message_Chat_Single_List>();
    Context ctx;
    private SparseBooleanArray mSelectedItemsIds;

    public Message_Chat_Single_Adapter(Context context, int resource) {
        super(context, resource);
        ctx=context;
        // TODO Auto-generated constructor stub
        mSelectedItemsIds = new SparseBooleanArray();
    }

    public void add(Message_Chat_Single_List object)
    {
        super.add(object);
        list.add(object);
    }
    public void add(int index,Message_Chat_Single_List object)
    {
        list.add(index,object);
        notifyDataSetChanged();
    }
    public void insert(Message_Chat_Single_List obj, int pos)
    {
        super.insert(obj,pos);
        list.add(pos,obj);
    }

    @Override
    public void remove(Object object) {
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

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    @Override
    public View getView(int position, View convertView,ViewGroup parent)
    {
        View row;
        row=convertView;
        final Message_Chat_Single_Holder message_chat_holder;
        Message_Chat_Single_List message_chat=(Message_Chat_Single_List) this.getItem(position);

        if(row==null)
        {
            LayoutInflater layoutinflator=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutinflator.inflate(R.layout.message_chat_single_row,parent,false );
            message_chat_holder= new Message_Chat_Single_Holder();
           // message_chat_holder.name=(TextView)row.findViewById(R.id.tv_message_chat_single_row_name);
            message_chat_holder.message=(TextView)row.findViewById(R.id.tv_message_chat_single_row_message);
            message_chat_holder.time=(TextView) row.findViewById(R.id.tv_message_chat_single_row_time);
            message_chat_holder.box=(RelativeLayout) row.findViewById(R.id.message_single_chat_box);
            message_chat_holder.status=(ImageView)row.findViewById(R.id.img_message_single_chat_status);

            row.setTag(message_chat_holder);
        }
        else
        {
            message_chat_holder=(Message_Chat_Single_Holder)row.getTag();
        }
        RelativeLayout.LayoutParams params  = new RelativeLayout.LayoutParams(message_chat_holder.box.getLayoutParams());


        if(message_chat.isMine())
        {
            message_chat_holder.box.setBackgroundResource(R.drawable.cyanbubble);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            message_chat_holder.box.setLayoutParams(params);
            message_chat_holder.message.setTextColor(Color.parseColor("#FFFFFF"));
            message_chat_holder.time.setTextColor(Color.parseColor("#FFFFFF"));
            message_chat_holder.status.setImageResource(R.drawable.ic_done_all_white18);
        }
        //If not mine then it is from sender to show orange background and align to left
        else
        {
            message_chat_holder.box.setBackgroundResource(R.drawable.whitebubble);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
            message_chat_holder.message.setTextColor(Color.parseColor("#000000"));
            message_chat_holder.time.setTextColor(Color.parseColor("#000000"));
            message_chat_holder.box.setLayoutParams(params);
            message_chat_holder.status.setImageResource(R.drawable.done_all_black_18x18);
        }



        Message_Chat_Single_List message_chat_list=(Message_Chat_Single_List) this.getItem(position);
     //   message_chat_holder.name.setText(message_chat_list.get_name());
        Log.e("time_id",message_chat_list.get_messaage());
        message_chat_holder.time.setText( time(message_chat_list.get_time()));
        message_chat_holder.message.setText(message_chat_list.get_messaage());
        return row;
    }

    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutinflator=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            holder = new HeaderViewHolder();
            convertView = layoutinflator.inflate(R.layout.header, parent, false);
            holder.date = (TextView) convertView.findViewById(R.id.tv_header_row);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        //set header text as first char in name
        String incoming_date=getdatemilli(list.get(position).getmillisec());
        String today_date=gettoday();
        String yester=getyesterday();
        Log.d("time", "today"+today_date+"incoming_date"+incoming_date);

        if(today_date.equals(incoming_date)){
            holder.date.setText("Today");
        }

        else if(incoming_date.equals(yester)){
            holder.date.setText("Yesterday");
        }
        else {
            holder.date.setText(incoming_date);
        }
        return convertView;
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

        Log.e("time",var);
        String [] dateParts = var.split(":");
        String hour = dateParts[0];
        String min = dateParts[1];
        String year = dateParts[2];
        hour=hour.substring(hour.length()-2,hour.length());
        return (hour+":"+min);
    }

    @Override
    public long getHeaderId(int position) {
        return list.get(position).getmillisec();
    }


    static class Message_Chat_Single_Holder
    {
        TextView message,time;
        RelativeLayout box;
        ImageView status;
    }
    static  class HeaderViewHolder{
        TextView date;
    }
}
