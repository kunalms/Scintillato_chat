package com.scintillato.scintillatochat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


/**
 * Created by VIVEK on 21-12-2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    Context context;
    String sender_name,code,final_message,sender,group_pubic_id,group_private_id,message_id,user_name,user_number,message_date,other_message_id;
    String formattedDate,cur_number;
    String other_message_id_receipt="";
    String group_name,group_topic,group_description,group_count,status,admin,group_members,other_members,group_details;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        formattedDate = get_time();
        showNotification(remoteMessage.getData().get("message"));
        context=this;
        updateMyActivity(getApplicationContext());//,final_message,group_id,sender,code,sender_name);
    }
    String get_time()
    {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(c.getTime());
    }
    void update_message_status(String message_id,String status){
        Chat_Database_Execute obj=new Chat_Database_Execute(this,cur_number);
        obj.update_status_message_single(obj,message_id,status);
    }
    private void showNotification(String message)
    {
        showlist(message);
        if(code.equals("1")) {
            formattedDate=get_time();
            Log.d("message",message);
            Intent i = new Intent(this, Message_Group_Chat_Public.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setAutoCancel(true)
                    .setContentTitle(message)
                    .setContentText(final_message+group_pubic_id+group_details)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pendingIntent);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());


            Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
            message_id=obj.insert_message_group(obj,formattedDate,final_message,"1",sender,group_pubic_id,"0","0","0");
            if(obj.recent_chats_group_exists(obj,group_pubic_id))
            {
                obj.update_recent_chats_groups(obj,group_pubic_id,formattedDate);
            }
            else
            {
                obj.insert_recent_chats(obj,"0",group_pubic_id,"-1",sender,formattedDate);
            }


            /*Cursor cursor=obj.getinfo_messages(obj);
            if (cursor.getCount()>0)
            {
                cursor.moveToFirst();
                do{
                    Log.d("message",cursor.getString(1));
                }while (cursor.moveToNext());
            }*/
        }
        else if(code.equals("2"))
        {
            formattedDate=get_time();
            Intent i = new Intent(this, Chat_Page.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setAutoCancel(true)
                    .setContentTitle(sender)
                    .setContentText(group_pubic_id+"  ")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pendingIntent);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());

            Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
            obj.insert_groups(obj,group_name,group_topic,group_description,formattedDate,group_count,status,group_pubic_id);
            add_members_group(group_members,group_pubic_id,admin);

            obj.insert_recent_chats(obj,"0",group_pubic_id,"-1",admin,formattedDate);
           }
        else if(code.equals("3"))
        {
            formattedDate=get_time();
            Intent i = new Intent(this, Chat_Message_Single.class);
            i.putExtra("user_number",sender);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            Contacts_Unregistered_Execute ob=new Contacts_Unregistered_Execute(getApplicationContext(),cur_number);
            String user_exists=ob.number_exists(ob,sender);

            if(user_exists.equals("1")==true) {
                user_name=ob.get_name_message_table(ob,sender);
                Log.d("userusername3",user_name);
            }
            else
            {
                Log.d("useruser0",sender);
                ob.putinfo_reg(ob,sender,sender);
                user_name=sender;
            }
            sent_receipt(sender,other_message_id);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setAutoCancel(true)
                    .setContentTitle(user_name+" "+other_message_id)
                    .setContentText(final_message)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pendingIntent);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());

            Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
            message_id=obj.insert_message_single(obj,formattedDate,final_message,"1",sender,cur_number,sender,"0","0","0",other_message_id);
            obj.insert_message_unsend_single(obj,formattedDate,final_message,"1",sender,cur_number,sender,"0","0","0",message_id);
            if(obj.recent_chats_single_exists(obj,sender)==true)
            {
                obj.update_recent_chats_single(obj,sender,formattedDate);
            }
            else
            {
                obj.insert_recent_chats(obj,"1","-1",sender,sender,formattedDate);
            }
        }
        else if(code.equals("4"))
        {
            if(status.equals("0"))
                update_message_status(message_id,"2");
            else
                update_message_status(message_id,"3");

        }
        else if(code.equals("5"))
        {
            if(status.equals("0")) {
                List<String> other_message_id_list = Arrays.asList(other_message_id_receipt.split(","));

                for(int i=0;i<other_message_id_list.size();i++)
                {
                    update_message_status(other_message_id_list.get(i), "2");
                }
            }
            else {
                List<String> other_message_id_list = Arrays.asList(other_message_id_receipt.split(","));

                for(int i=0;i<other_message_id_list.size();i++)
                {
                    update_message_status(other_message_id_list.get(i), "3");
                }
            }

        }
        else if(code.equals("6"))
        {
            Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
            obj.insert_message_group_boolean(obj,false,false,true,false,false,sender,"",group_pubic_id,sender);
            obj.delete_group_member_selected(sender,group_pubic_id);
        }
        else if(code.equals("7"))
        {
            Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
            obj.insert_message_group_boolean(obj,false,false,false,true,false,sender,"",group_pubic_id,sender);
            obj.delete_group_member_selected(sender,group_pubic_id);
        }
        else if(code.equals("8"))
        {
            fetch_group_details(group_details,group_pubic_id);
            fetch_new_members(group_members,group_pubic_id);
            fetch_members(other_members,group_pubic_id);


        }
        else if(code.equals("9"))
        {
            fetch_new_members(group_members,group_pubic_id);
            Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
            obj.update_group_count(obj,group_pubic_id,group_count);
        }
    }

    public void add_members_group(String myJSON,String group_id,String admin)
    {
        try{
            formattedDate=get_time();
            Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
            jsonArray=new JSONArray(myJSON);

            String member,member_name,rank;
            count=0;
            while(count<jsonArray.length())
            {
                JSONObject JO=jsonArray.getJSONObject(count);
                member=JO.getString("number");
                rank=JO.getString("rank");
                Log.d("member1",member);
                member_name=member;
                if(member.equals(admin)==false)
                    obj.putinfo_group_members(obj,group_id,member,"0",rank,formattedDate);
                else
                    obj.putinfo_group_members(obj,group_id,member,"1",rank,formattedDate);

                count++;
            }
        }
        catch(Exception e)
        {

        }
    }

    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private int count;

    public void showlist(String myJSON)
    {
        try{
            jsonObject=new JSONObject(myJSON);
            jsonArray=jsonObject.getJSONArray("result");

            count=0;
            //	Log.d("length", jsonArray.length()+"");
            while(count<jsonArray.length())
            {
                JSONObject JO=jsonArray.getJSONObject(count);
                code=JO.getString("code");
                if(code.equals("1")) {
                    final_message = JO.getString("message");
                    sender = JO.getString("sender_number");
                    group_pubic_id = JO.getString("group_id");
                    message_date=JO.getString("date");
                    Log.d("data", final_message);
                }
                else if(code.equals("2"))
                {
                    group_name=JO.getString("group_name");
                    group_topic=JO.getString("group_topic");
                    group_description=JO.getString("group_description");
                    group_count=JO.getString("group_count");
                    status=JO.getString("status");
                    admin=JO.getString("admin");
                    group_members=JO.getString("group_members");
                    group_pubic_id=JO.getString("group_id");
                }
                else if(code.equals("3"))
                {
                    final_message=JO.getString("message");
                    sender=JO.getString("sender_number");
                    message_date=JO.getString("date");
                    other_message_id=JO.getString("message_id");
                    Log.d("message_id",other_message_id);
                }
                else if(code.equals("4"))
                {
                    message_id=JO.getString("message_id");
                    sender=JO.getString("sender_number");
                    status=JO.getString("receive_seen");
                }
                else if(code.equals("5"))
                {
                    other_message_id_receipt=JO.getString("message_id");
                    sender=JO.getString("sender_number");
                    status=JO.getString("receive_seen");
                }
                else if(code.equals("6"))
                {
                    group_pubic_id=JO.getString("group_id");
                    sender=JO.getString("number");
                }
                else if(code.equals("7"))
                {
                    group_pubic_id=JO.getString("group_id");
                    sender=JO.getString("number");
                }
                else if(code.equals("8"))
                {
                    group_pubic_id=JO.getString("group_id");
                    group_details=JO.getString("group_details");
                    group_members=JO.getString("members");
                    other_members=JO.getString("other_members");
                    sender=JO.getString("sender");
                }
                else if(code.equals("9"))
                {
                    group_pubic_id=JO.getString("group_id");
                    group_members=JO.getString("members");
                    group_count=JO.getString("group_count");
                }
                count++;
            }
        }
        catch(Exception e)
        {

        }
        sender_name=sender;
    }
    void updateMyActivity(Context context)//, String message,String group_id,String sender,String code,String sender_name) {
    {
        if(code.equals("1")) {
            Intent intent = new Intent("message_group");
            //put whatever data you want to send, if any
            intent.putExtra("message", final_message);
            intent.putExtra("group_id", group_pubic_id);
            intent.putExtra("sender", sender);
            intent.putExtra("code", code);
            intent.putExtra("message_id",message_id);
            context.sendBroadcast(intent);
        }
        else if(code.equals("2"))
        {
            Intent intent = new Intent("new_group");

            //put whatever data you want to send, if any
            intent.putExtra("group_id", group_pubic_id);
            intent.putExtra("group_name", group_name);
            intent.putExtra("code", code);
            intent.putExtra("group_topic", group_topic);
            intent.putExtra("group_description", group_description);
            intent.putExtra("group_count", group_count);
            intent.putExtra("group_private_id",group_private_id);
            intent.putExtra("group_members",group_members);
            intent.putExtra("admin",admin);
            context.sendBroadcast(intent);
        }
        else if(code.equals("3"))
        {
            Intent intent = new Intent("message_single");
            //put whatever data you want to send, if any
            intent.putExtra("message", final_message);
            intent.putExtra("sender", sender);
            intent.putExtra("code", code);
            intent.putExtra("user_name",user_name);
            intent.putExtra("message_id",message_id);
            intent.putExtra("opposite_message_id",other_message_id);
            context.sendBroadcast(intent);
        }
        else if(code.equals("4"))
        {
            Intent intent = new Intent("message_single_receipt");
            //put whatever data you want to send, if any
            intent.putExtra("sender", sender);
            intent.putExtra("code", code);
            intent.putExtra("message_id",message_id);
            intent.putExtra("status",status);
            context.sendBroadcast(intent);
        }
        else if(code.equals("5"))
        {
            Intent intent = new Intent("message_single_multiple_receipt");
            //put whatever data you want to send, if any
            intent.putExtra("sender", sender);
            intent.putExtra("code", code);
            intent.putExtra("message_id",other_message_id_receipt);
            intent.putExtra("status",status);
            context.sendBroadcast(intent);
        }
        else if(code.equals("6"))
        {

        }
        else if(code.equals("7"))
        {

        }
        //send broadcast
    }


    void fetch_members(String json,String group_id)
    {
        String date=get_time();
        try{
            jsonObject=new JSONObject(json);
            jsonArray=jsonObject.getJSONArray("result");

            count=0;
            String rank,number;
            //	Log.d("length", jsonArray.length()+"");
            while(count<jsonArray.length())
            {
                JSONObject JO=jsonArray.getJSONObject(count);
                rank=JO.getString("rank");
                number=JO.getString("member");

                Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
                obj.putinfo_group_members(obj,group_id,number,"0",rank,date);
                count++;
            }
        }
        catch(Exception e)
        {

        }
    }
    public void fetch_new_members(String myJSON,String group_id)
    {
        try{
            formattedDate=get_time();
            Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
            jsonArray=new JSONArray(myJSON);

            String member,member_name,rank;
            count=0;
            while(count<jsonArray.length())
            {
                JSONObject JO=jsonArray.getJSONObject(count);
                member=JO.getString("number");
                rank=JO.getString("rank");
                Log.d("member1",member);
                obj.putinfo_group_members(obj,group_id,member,"0",rank,formattedDate);
                obj.insert_message_group_boolean(obj,false,false,false,false,true,member,"",group_pubic_id,"");
                count++;
            }
        }
        catch(Exception e)
        {

        }
    }

    void fetch_group_details(String json,String group_id)
    {
        String date=get_time();
        try{
            jsonObject=new JSONObject(json);
            jsonArray=jsonObject.getJSONArray("result");

            count=0;
            String group_name,group_topic,group_description,group_create_date,group_count,status;
            while(count<jsonArray.length())
            {

                JSONObject JO=jsonArray.getJSONObject(count);
                group_count=JO.getString("group_count");
                status=JO.getString("status");
                group_create_date=JO.getString("group_create_date");
                group_description=JO.getString("group_description");
                group_topic=JO.getString("group_topic");
                group_name=JO.getString("group_name");

                /*group_name'=>$row1[0],
                'group_description'=>$row1[1],
                    'group_topic'=>$row1[2],
                    'group_create_date'=>$row1[3],
                    'group_count'=>$row1[4],
                    'status'=>$row1[5]*/
                Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
                obj.insert_groups(obj,group_name,group_topic,group_description,group_create_date,group_count,status,group_id);
                obj.insert_recent_chats(obj,"0",group_id,"-1",sender,date);
                count++;
            }
        }
        catch(Exception e)
        {

        }
    }
    BackGroundTaskSentReceipt backGroundTaskSentReceipt;
    void sent_receipt(String user_number,String other_message_id)
    {
        backGroundTaskSentReceipt=new BackGroundTaskSentReceipt(message_id,this);
        backGroundTaskSentReceipt.execute("0",user_number,cur_number,other_message_id);
    }
}
