package com.scintillato.scintillatochat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class Add_Member extends AppCompatActivity {

    private RecyclerView recyclerView_contacts;
    private Add_Member_Adapter adapter;
    private Context ctx;
    private ProgressDialog loading;
    private int rank;
    private String cur_number,group_id,string_rank,jsonString_members;
    private ArrayList<Choose_Contacts_Single_List> contacts_list;
    private BackGroundTaskAddMember backGroundTaskAddMember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_member);
        recyclerView_contacts=(RecyclerView)findViewById(R.id.rv_add_member_contacts);
        contacts_list=new ArrayList<>();
        adapter=new Add_Member_Adapter(getApplicationContext(),contacts_list);

        recyclerView_contacts.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView_contacts.setLayoutManager(mLayoutManager);
        recyclerView_contacts.setItemAnimator(new DefaultItemAnimator());

        Bundle b=getIntent().getExtras();
        group_id=b.getString("group_id");
        string_rank=b.getString("rank");
        rank=Integer.parseInt(string_rank);
        Log.d("rank",string_rank);
        ctx=getApplicationContext();
        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        Selected_Memebers_Execute obj1=new Selected_Memebers_Execute(getApplicationContext(),cur_number);
        obj1.delete_selected_members_temp();

        Contacts_Unregistered_Execute obj=new Contacts_Unregistered_Execute(getApplicationContext(),cur_number);
        Cursor cr=obj.getinfo_reg(obj);
        if(cr.getCount()>0)
        {
            cr.moveToFirst();
            do
            {
                Log.d("name",cr.getString(0));
                Choose_Contacts_Single_List list=new Choose_Contacts_Single_List(cr.getString(0), cr.getString(1));
                contacts_list.add(list);
            }while (cr.moveToNext());
            adapter.notifyDataSetChanged();
        }

    }

    void fetch_members()
    {
        JSONObject object;
        JSONArray jsonArray=new JSONArray();
        Selected_Memebers_Execute obj=new Selected_Memebers_Execute(getApplicationContext(),cur_number);
        Cursor cursor_members=obj.getinfo_selected_members_temp(obj);
        if(obj!=null)
        {
            if(cursor_members.getCount()>0)
            {
                cursor_members.moveToFirst();
                do{
                    object=new JSONObject();
                    try {
                        object.put("number", cursor_members.getString(0));
                        object.put("rank",++rank);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.put(object);

                }while(cursor_members.moveToNext());
                jsonString_members=jsonArray.toString();
                Log.d("membersjson",jsonString_members);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Add atleast one member",Toast.LENGTH_LONG).show();
            }
        }

    }

    class BackGroundTaskAddMember extends AsyncTask<String, Void, String> {
        int flag1=1;
        int flag;
        BackGroundTaskAddMember()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {


            String group_id=params[0];
            String group_member=params[1];
            String cur_number=params[2];

            String register_url="http://www.scintillato.esy.es/add_member_group.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("group_id","UTF-8")+"="+URLEncoder.encode(group_id,"UTF-8")+"&"+
                        URLEncoder.encode("members","UTF-8")+"="+URLEncoder.encode(group_member,"UTF-8")+"&"+
                        URLEncoder.encode("cur_number","UTF-8")+"="+URLEncoder.encode(cur_number,"UTF-8");

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

                if(line.equals("")==false)
                {
                    flag=1;
                }
                else
                {
                    //Log.d("outside","1");
                    flag=0;
                    //tv_status.setText("failure");
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
            // loading.dismiss();
            Log.d("1",flag+"");
            Toast.makeText(ctx,result,Toast.LENGTH_LONG);

            if(flag1==0)
            {
                Toast.makeText(ctx,result,Toast.LENGTH_LONG);
            }
            else
            {

                if(flag==1)
                {
                    //String group_public_id;
                    //group_public_id=showlist(result);
                    rank=Integer.parseInt(string_rank);
                    String date=get_datetime();
                    Chat_Database_Execute obj=new Chat_Database_Execute(ctx,cur_number);
                    Selected_Memebers_Execute obj1=new Selected_Memebers_Execute(getApplicationContext(),cur_number);
                    Cursor cursor_members=obj1.getinfo_selected_members_temp(obj1);
                    if(obj!=null) {
                        if (cursor_members.getCount() > 0) {
                            cursor_members.moveToFirst();
                            do {
                                obj.putinfo_group_members(obj, group_id, cursor_members.getString(0), "0", ++rank+"", date);
                                obj.insert_message_group_boolean(obj,false,false,false,false,true,cursor_members.getString(0),"",group_id,cur_number);
                            }while (cursor_members.moveToNext());
                        }
                    }
                    Intent i=new Intent(ctx,Message_Chat_Public_Group_Main.class);
                    i.putExtra("group_id",group_id);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

                    Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(ctx,"Somethig went wrong!!",Toast.LENGTH_LONG).show();

                }
            }
        }
        @Override
        protected void onPreExecute() {
//            loading = ProgressDialog.show(ctx, "Status", "Adding Members...",true,false);
  //          loading.setCancelable(false);
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

    @Override
    protected void onPause() {
        if(backGroundTaskAddMember!=null) {
            backGroundTaskAddMember.cancel(true);
        }
        super.onPause();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.choose__contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==R.id.btn_choose_contacts_create)
        {
            fetch_members();
            backGroundTaskAddMember=new BackGroundTaskAddMember();
            backGroundTaskAddMember.execute(group_id,jsonString_members,cur_number);
        }

        return super.onOptionsItemSelected(item);
    }


}
