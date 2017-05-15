package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by VIVEK on 13-03-2017.
 */


public class Chat_Message_Adapter extends RecyclerView.Adapter<Chat_Message_Adapter.Chat_Holder> implements StickyRecyclerHeadersAdapter<Chat_Message_Adapter.Chat_Holder_Header> {

    ArrayList<Chat_Message_Single_List> list=new ArrayList<>();
   // private Context ctx;
    private String cur_number,cur_user_id;
    private SparseBooleanArray selectedItems;
    private Chat_Holder.ClickListener listener;


    public Chat_Message_Adapter(Chat_Holder.ClickListener clickListener,ArrayList<Chat_Message_Single_List> list) {
        this.list = list;
     //   this.ctx=ctx;
        /*SharedPreferences sharedpreferences = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(ctx,cur_number);
        Cursor c=obj.get_my_details(obj);
        c.moveToFirst();
        if(c.getCount()>0)
            cur_user_id=c.getString(0);*/
        selectedItems = new SparseBooleanArray();
        this.listener=clickListener;

    }

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            list.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }
    public void removeItems(List<Integer> positions) {
        // Reverse-sort the list
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        // Split the list in ranges
        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    ++count;
                }

                if (count == 1) {
                    removeItem(positions.get(0));
                } else {
                    removeRange(positions.get(count - 1), count);
                }

                for (int i = 0; i < count; ++i) {
                    positions.remove(0);
                }
            }
        }
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }
    @Override
    public Chat_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_single_row, parent, false);

        return new Chat_Holder(itemView,listener);
    }


    @Override
    public void onBindViewHolder(final Chat_Holder message_chat_holder, int position) {

        RelativeLayout.LayoutParams params  = new RelativeLayout.LayoutParams(message_chat_holder.box.getLayoutParams());
        message_chat_holder.setIsRecyclable(false);
        Chat_Message_Single_List message_chat=list.get(position);
        if(message_chat.isMine())
        {
            message_chat_holder.box.setBackgroundResource(R.drawable.cyanbubble);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            message_chat_holder.box.setLayoutParams(params);
            message_chat_holder.message.setTextColor(Color.parseColor("#FFFFFF"));
            message_chat_holder.time.setTextColor(Color.parseColor("#FFFFFF"));
            if(message_chat.getStatus().equals("0"))
                message_chat_holder.status.setImageResource(R.drawable.done_black_18x18);
            else if(message_chat.getStatus().equals("1"))
            {
                message_chat_holder.status.setImageResource(R.drawable.ic_done_white18);
            }
            else if(message_chat.getStatus().equals("2"))
            {
                message_chat_holder.status.setImageResource(R.drawable.ic_done_all_white18);
            }
            else {

            }


        }
        //If not mine then it is from sender to show orange background and align to left
        else
        {
            message_chat_holder.box.setBackgroundResource(R.drawable.whitebubble);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
            message_chat_holder.message.setTextColor(Color.parseColor("#000000"));
            message_chat_holder.time.setTextColor(Color.parseColor("#000000"));
            message_chat_holder.box.setLayoutParams(params);
            message_chat_holder.status.setVisibility(View.GONE);
        }
        if(message_chat.isUnseen()==true)
        {

        }
        else {
            Log.e("time_id", message_chat.getMessage());
            message_chat_holder.time.setText(time(message_chat.getTime()));
            message_chat_holder.message.setText(message_chat.getMessage());
            if (isSelected(position)) {
                message_chat_holder.view.setBackgroundColor(Color.CYAN);
            } else {
                message_chat_holder.view.setBackgroundColor(Color.TRANSPARENT);
            }
        }


    }


    @Override
    public long getHeaderId(int position) {
        return list.get(position).getmillisec();
        //return position/2;
    }

    @Override
    public Chat_Holder_Header onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.header, parent, false);
        return new Chat_Holder_Header(view);
    }

    @Override
    public void onBindHeaderViewHolder(Chat_Holder_Header holder, int position) {
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Chat_Holder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        public final View view;
        TextView message,time;
        RelativeLayout box;
        ImageView status;
        private ClickListener listener;
      //  RelativeLayout background;

        public Chat_Holder(View row,ClickListener listener) {
            super(row);
            this.view = row;
            message=(TextView)row.findViewById(R.id.tv_message_chat_single_row_message);
            time=(TextView) row.findViewById(R.id.tv_message_chat_single_row_time);
            box=(RelativeLayout) row.findViewById(R.id.message_single_chat_box);
            status=(ImageView)row.findViewById(R.id.img_message_single_chat_status);
         //   background=(RelativeLayout)row.findViewById(R.id.chat_message_single_row_background);
            this.listener=listener;
            row.setOnClickListener(this);
            row.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (listener != null) {
                listener.onItemClicked(getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (listener != null) {
                return listener.onItemLongClicked(getLayoutPosition());
            }
            return false;
        }

        public interface ClickListener {
            void onItemClicked(int position);
            boolean onItemLongClicked(int position);
        }

    }
    public  class Chat_Holder_Header extends RecyclerView.ViewHolder{
        public  View view;
        TextView date;
        public  Chat_Holder_Header(View row){
            super(row);
            this.view=row;
            date=(TextView)row.findViewById(R.id.tv_header_row);
        }
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


    public void toggleSelection(int position) {
        selectView(position, !selectedItems.get(position));
    }
    public void selectView(int position, boolean value) {
        if (value) {
            selectedItems.put(position, value);

        }
            else {
            selectedItems.delete(position);

        }
        notifyDataSetChanged();
    }
    public void removeSelection() {
        selectedItems = new SparseBooleanArray();
        notifyDataSetChanged();
    }
    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedIds() {
        List<Integer> items = new ArrayList<> (selectedItems.size());
        for (int i = 0; i < selectedItems.size(); ++i) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public boolean isSelected(int pos){
        return getSelectedIds().contains(pos);
    }
    public Object getItem(int position)
    {
        return list.get(position);
    }

    public void remove(Object object) {
        list.remove(object);
    }





}