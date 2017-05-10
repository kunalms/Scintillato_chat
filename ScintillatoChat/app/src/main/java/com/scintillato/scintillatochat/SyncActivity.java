package com.scintillato.scintillatochat;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION.SDK;

public class SyncActivity extends RuntimePermissionsActivity {

    private ProgressBar spinner;
    private String one_to_one_flag;
    private Context ctx;
    private ProgressDialog loading;
    private int flag,contacts_update;
    private String contact_json,cur_number;
    private Contacts_Unregistered_Execute obj1;
    private Button skip;
    private List<String> number_list,name_list;
    private List<contacts_list> contacts_distinct,c_reg,c_not_reg;
    private BackGroundTaskFetch backgroundfetch;
    private static final int REQUEST_PERMISSIONS = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        ctx=this;
        if(Build.VERSION.SDK_INT>=21) {
            SyncActivity.super.requestAppPermissions(new
                    String[]{android.Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.string
                    .permission_rationale, REQUEST_PERMISSIONS);
        }
        else
        {
            Intent i = new Intent(getApplicationContext(), WelcomeActivity.class);
            finish();
            startActivity(i);
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode) {


        SharedPreferences sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);

        contacts_update=sharedpreferences.getInt("contacts_update",-1);

      //  Toast.makeText(ctx, contacts_update+"", Toast.LENGTH_LONG).show();
        if(contacts_update==1)
        {
            go_to_start_page();
        }
        else
        {
            try {
                fetchContacts();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }

    void go_to_start_page()
    {

        Intent i = new Intent(getApplicationContext(), WelcomeActivity.class);
        finish();
        startActivity(i);
    }

    BackGroundSync backGroundSync;
    public void fetchContacts() throws JSONException {
        backGroundSync=new BackGroundSync();
        backGroundSync.execute();
    }


    String phone_manage(String phone)
    {
        phone=phone.replaceAll("\\s","");
        if(phone.startsWith("0"))
        {
            String tem=phone.substring(1);
            tem="+91"+tem;
            phone=tem;
        }
        else if(phone.startsWith("+"))
        {

        }
        else
        {
            String tem=phone;
            tem="+91"+tem;
            phone=tem;
        }
        return phone;
    }
    void manage_number(List<contacts_list> list)
    {
        for(int i=0;i<list.size();i++)
        {
            list.get(i).number=list.get(i).number.replaceAll("\\s","");
            if(list.get(i).number.startsWith("0"))
            {
                String tem=list.get(i).number.substring(1);
                tem="+91"+tem;
                list.get(i).number=tem;
            }
            else if(list.get(i).number.startsWith("+"))
            {

            }
            else
            {
                String tem=list.get(i).number;
                tem="+91"+tem;
                list.get(i).number=tem;
            }
            //	Log.e("numbers",list.get(i).number+" "+list.get(i).name);

        }
    }
    void contact_fetch_server()
    {
        backgroundfetch=new BackGroundTaskFetch();
        //	Toast.makeText(ctx, contact_json, Toast.LENGTH_LONG).show();
        backgroundfetch.execute(contact_json);
    }

    class BackGroundSync extends AsyncTask<String,Void,String>
    {
        int flag1=1,flag=0;
        BackGroundSync()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params)
        {

            try
            {

            number_list=new ArrayList<String>();
            name_list=new ArrayList<String>();
            contacts_distinct=new ArrayList<contacts_list>();

            String phoneNumber = null;
            String email = null;

            Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
            String _ID = ContactsContract.Contacts._ID;
            String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
            String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

            Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
            String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

            Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
            String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
            String DATA = ContactsContract.CommonDataKinds.Email.DATA;



            ContentResolver contentResolver = getContentResolver();

            Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {

                    String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                    String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                    if (hasPhoneNumber > 0) {

                        //output.append("\n First Name:" + name);

                        // Query and loop for every phone number of the contact
                        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                            //output.append("\n Phone number:" + phoneNumber);
                            number_list.add(phoneNumber);
                            name_list.add(name);
                            phoneNumber = phone_manage(phoneNumber);
                            contacts_list temp = new contacts_list(name, phoneNumber);
                            int flag = temp.check(contacts_distinct, temp);
                            if (flag == 0) {
                                //				Log.e("dist",temp.name+temp.number);
                                contacts_distinct.add(temp);

                            }
                            //		Log.e("name,number",name_list.get(name_list.size()-1)+" "+number_list.get(number_list.size()-1));
                        }

                        phoneCursor.close();

                        // Query and loop for every email of the contact
                        Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);

                        while (emailCursor.moveToNext()) {

                            email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                        }

                        emailCursor.close();
                    }


                }

                JSONArray jsonArray = new JSONArray();
                manage_number(contacts_distinct);

                for (int i = 0; i < contacts_distinct.size(); i++) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("number", contacts_distinct.get(i).number);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.put(object);
                }
                //		JSONObject number_object=new JSONObject();
                //		number_object.put("result",jsonArray);
                contact_json = jsonArray.toString();
                //		Log.d("json",contact_json+"");
                //    contact_fetch_server();
            }
                flag=1;
                return "1";

            }
            catch (Exception e)
            {
                flag1=0;
                return "Something went wrong!";
            }

        }
        @Override
        protected void onPostExecute(String result) {
            //	Log.e("1",result+"");
            //	Toast.makeText(ctx,flag+"",Toast.LENGTH_LONG).show();

            if(flag1==0)
            {
                loading.dismiss();
                Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
            }
            else
            {
                if(flag==1)
                {
                    contact_fetch_server();
                   // showlist(result);
                }
                else
                {
                    loading.dismiss();

                    Toast.makeText(ctx, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        @Override
        protected void onPreExecute() {
            loading = ProgressDialog.show(ctx, "Status", "Syncing Contacts...",true,false);
        }
    }
    class BackGroundTaskFetch extends AsyncTask<String, Void, String>
    {
        int flag1=1;
        BackGroundTaskFetch()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String json=params[0];


            String register_url="http://scintillato.esy.es/contacts.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("json","UTF-8")+"="+URLEncoder.encode(json,"UTF-8");/*+"&"+
					URLEncoder.encode("andro_id","UTF-8")+"="+URLEncoder.encode(token,"UTF-8");*/


                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS=httpURLConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(IS,"iso-8859-1"));

                String line="";
                line=bufferedReader.readLine();


                bufferedReader.close();
                IS.close();
                httpURLConnection.disconnect();
                //	Log.d("line", line);

                if(line.equals(""))
                {
                    flag=0;
                }
                else
                {
                    flag=1;
                }
                return line;

            }
            catch(Exception e)
            {
                flag1=0;
                return "Check Internet Connection!";

            }


        }
        @Override
        protected void onPostExecute(String result) {
            loading.dismiss();
            //	Log.e("1",result+"");
            //	Toast.makeText(ctx,flag+"",Toast.LENGTH_LONG).show();

            if(flag1==0)
            {
                loading.dismiss();

                Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
            }
            else
            {

                if(flag==1)
                {

                    showlist(result);
                }
                else
                {
                    loading.dismiss();

                    Toast.makeText(ctx, "Failure Occured!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        @Override
        protected void onPreExecute() {
            /*loading = ProgressDialog.show(ctx, "Status", "Syncing Contacts...",true,true);
            loading.setOnCancelListener(new DialogInterface.OnCancelListener() {

                public void onCancel(DialogInterface arg0) {
                    if(backgroundfetch!=null)
                    {
                        backgroundfetch.cancel(true);
                        //Toast.makeText(getApplicationContext(), "stopped", Toast.LENGTH_LONG).show();
                        loading.cancel();
                    }


                }
            });
*/
        }
    }
    private int count;
    public void showlist(String myJSON)
    {

        JSONObject jsonObject;
        JSONArray jsonArray;
       int count=0;
        obj1=new Contacts_Unregistered_Execute(ctx,cur_number);
        obj1.delete_reg();
        obj1.delete_unreg();
        c_not_reg=new ArrayList<contacts_list>();
        c_reg=new ArrayList<contacts_list>();
        try{
            SharedPreferences sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedpreferences.edit();
            editor.putInt("contacts_update", 1);
            editor.commit();

            jsonObject=new JSONObject(myJSON);
            jsonArray=jsonObject.getJSONArray("result");
            SharedPreferences sharedpreferences1=getSharedPreferences("User", Context.MODE_PRIVATE);
            String cur_number=sharedpreferences1.getString("number","");
            count=0;
            String status;
            //	Log.d("length", jsonArray.length()+"");
            while(count<jsonArray.length())
            {
                JSONObject JO=jsonArray.getJSONObject(count);
                status=JO.getString("status");
                if(status.equals("1"))
                {


                    //		Log.e("reg",contacts_distinct.get(count).name+" "+contacts_distinct.get(count).number);
                    if(contacts_distinct.get(count).number.equals(cur_number)==false)
                    {
                        obj1.putinfo_reg(obj1, contacts_distinct.get(count).name, contacts_distinct.get(count).number);
                        c_reg.add(contacts_distinct.get(count));
                    }
                }
                else
                {

                    Group_create_contacts_list contact_list=new Group_create_contacts_list(contacts_distinct.get(count).name, contacts_distinct.get(count).number);
                    obj1.putinfo_unreg(obj1, contacts_distinct.get(count).name, contacts_distinct.get(count).number);
                    c_not_reg.add(contacts_distinct.get(count));
                }
                count++;
            }

			/*if(obj1!=null)
			{
				Cursor cr=obj1.getinfo_unreg(obj1);
				cr.moveToFirst();
				do
				{
					Log.d("unregname",cr.getString(0));
					Log.d("unregnumber",cr.getString(1));

				}while(cr.moveToNext());
			}
			if(obj1!=null)
			{
				Cursor cr=obj1.getinfo_reg(obj1);
				cr.moveToFirst();
				do
				{
					Log.d("regname",cr.getString(0));
					Log.d("regnumber",cr.getString(1));

				}while(cr.moveToNext());
			}
			*/
//            go_to_start_page();
            loading.dismiss();

           fetch_group();

        }
        catch(Exception e)
        {

        }
    }

    BackGroundTaskFetchGroups backGroundTaskFetchGroups;
    void fetch_group()
    {
     backGroundTaskFetchGroups=new BackGroundTaskFetchGroups();
        backGroundTaskFetchGroups.execute(cur_number);
    }
    class BackGroundTaskFetchGroups extends AsyncTask<String, Void, String>
    {
        int flag1=1;
        BackGroundTaskFetchGroups()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String mobile_no=params[0];


            String register_url="http://scintillato.esy.es/fetch_group_mobile_no.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("mobile_no","UTF-8")+"="+URLEncoder.encode(mobile_no,"UTF-8");/*+"&"+
					URLEncoder.encode("andro_id","UTF-8")+"="+URLEncoder.encode(token,"UTF-8");*/


                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS=httpURLConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(IS,"iso-8859-1"));

                String line="";
                line=bufferedReader.readLine();


                bufferedReader.close();
                IS.close();
                httpURLConnection.disconnect();
                Log.d("get_group", line);

                if(line.equals("")==true)
                {
                    flag=0;
                }
                else
                {
                    flag=1;
                }
                return line;

            }
            catch(Exception e)
            {
                flag1=0;
                return "Check Internet Connection!";

            }


        }
        @Override
        protected void onPostExecute(String result) {
            //	Log.e("1",result+"");
            //	Toast.makeText(ctx,flag+"",Toast.LENGTH_LONG).show();

            if(flag1==0)
            {
                loading.dismiss();

                Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
            }
            else
            {

                if(flag==1)
                {
                    Log.d("get_group","here");
                   add_groups_list(result);

                }
                else
                {
                    loading.dismiss();
                    Toast.makeText(ctx, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        @Override
        protected void onPreExecute() {
            loading = ProgressDialog.show(ctx, "Status", "Preparing things...",true,false);
        }
    }

    String get_datetime()
    {
        java.util.Calendar c;
        java.text.SimpleDateFormat df;
        c = java.util.Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//yyyy-MM-dd
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
    public void add_groups_list(String myJSON) {
       int  count = 0;
        Log.d("get_group1",myJSON);
        JSONObject jsonObject;
        JSONArray jsonArray;

        try {
            jsonObject = new JSONObject(myJSON);
            jsonArray = jsonObject.getJSONArray("result");
            String group_public_id,group_name,group_topic,group_description,group_create_date,group_count,status,member,admin;
            	Log.d("length1", jsonArray.length()+"");


                while (count < jsonArray.length()) {
                    Log.d("get_group2", "here");
                    Log.d("count", count + " " + jsonArray.length());
                    JSONObject JO = jsonArray.getJSONObject(count);
                    status = JO.getString("status");
                    group_public_id = JO.getString("group_public_id");
                    group_name = JO.getString("group_name");
                    group_topic = JO.getString("group_topic");
                    group_description = JO.getString("group_description");
                    group_create_date = JO.getString("group_create_date");
                    group_count = JO.getString("group_count");
                    status = JO.getString("status");
                    member = JO.getString("member");
                    admin = JO.getString("admin");
                    Log.d("get_group", group_name);


                    Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
                    obj.insert_groups(obj,group_name,group_topic,group_description,group_create_date,group_count,status,group_public_id);
                    add_group_members(member,group_public_id,admin,group_create_date);
                    obj.insert_recent_chats(obj,"0",group_public_id,"-1",admin,get_datetime());

                    /*Group_Execute obj = new Group_Execute(getApplicationContext(), cur_number);
                    obj.putinfo_groups(obj, group_name, group_topic, group_count, group_description, group_create_date, status, group_public_id);
                    add_group_members(member, group_public_id, admin);

                    obj.putinfo_recentchats(obj, group_public_id, "0", group_create_date, "1", "", "0");
                    Log.d("recent", group_public_id);
                    count++;
                    Log.d("count", count + " " + jsonArray.length());*/
                    count++;

            }
            loading.dismiss();

            go_to_start_page();
        }
        catch (Exception e)
        {
            Log.d("error","here");
        }
    }

    void add_group_members(String myJSON,String group_public_id,String admin,String date)
    {
        int count = 0;
        Log.d("get_group1",myJSON);
        JSONObject jsonObject;
        JSONArray jsonArray;
        Chat_Database_Execute obj=null;
        try {
            jsonObject = new JSONObject(myJSON);
            jsonArray = jsonObject.getJSONArray("result");
            count = 0;
            String member_phone_number,rank;
            Log.d("length1", jsonArray.length()+"");
            while (count < jsonArray.length()) {
                Log.d("get_group2","here");

                JSONObject JO = jsonArray.getJSONObject(count);
                member_phone_number = JO.getString("member_phone_number");
                rank=JO.getString("rank");
                obj=new Chat_Database_Execute(getApplicationContext(),cur_number);

                if(admin.equals(member_phone_number)==true) {
                    if(obj!=null)
                        obj.putinfo_group_members(obj,group_public_id,member_phone_number,"1",rank,date);
                }
                else
                {
                    if(obj!=null)
                        obj.putinfo_group_members(obj,group_public_id,member_phone_number,"0",rank,date);
                }
                //  admin = JO.getString("admin");

                /*Group_Execute obj=new Group_Execute(getApplicationContext(),cur_number);
                String name;
                Contacts_Unregistered_Execute obj1=new Contacts_Unregistered_Execute(getApplicationContext(),cur_number);
                name=obj1.get_name_message_table(obj1,member_phone_number);
                obj.putinfo_group_members(obj,name,member_phone_number,admin,group_public_id);
                count++;*/
                count++;
            }
        }
        catch (Exception e)
        {
            Log.d("error","here");

        }
    }




    @Override
    protected void onPause() {
        if(backgroundfetch!=null)
        {
            backgroundfetch.cancel(true);
            //Toast.makeText(getApplicationContext(), "stopped", Toast.LENGTH_LONG).show();
            loading.cancel();
        }



        super.onPause();
    }
    @Override
    protected void onStop() {
        if(backgroundfetch!=null)
        {
            backgroundfetch.cancel(true);
            //Toast.makeText(getApplicationContext(), "stopped", Toast.LENGTH_LONG).show();
            loading.cancel();
        }
        if(backGroundSync!=null)
        {
            backgroundfetch.cancel(true);
            loading.cancel();
        }
        if (backGroundTaskFetchGroups!=null)
        {
            backGroundTaskFetchGroups.cancel(true);
            loading.cancel();
        }
        super.onStop();
    }
}
