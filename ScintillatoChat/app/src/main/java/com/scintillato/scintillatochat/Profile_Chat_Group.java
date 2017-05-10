package com.scintillato.scintillatochat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class Profile_Chat_Group extends AppCompatActivity {

    private Button btn_exit;
    private String group_id,cur_number,rank;
    private Context ctx;
    private RecyclerView rv_participants;
    private ProgressDialog loading;
    private Profile_Chat_Group_Recycler_Adapter adapter;
    private ArrayList<Choose_Contacts_Single_List> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_group);
        group_id=getIntent().getExtras().getString("group_id");
        btn_exit=(Button)findViewById(R.id.btn_profile_group_exit);
        rv_participants=(RecyclerView) findViewById(R.id.rv_profile_group_participant);
        ctx=getApplicationContext();
        SharedPreferences sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number=sharedpreferences.getString("number", "");


        list=new ArrayList<>();
        adapter=new Profile_Chat_Group_Recycler_Adapter(getApplicationContext(),list);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_participants.setLayoutManager(mLayoutManager);
        rv_participants.setItemAnimator(new DefaultItemAnimator());
        rv_participants.setAdapter(adapter);

        Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
        rank=obj.get_max_rank_group(obj);
        if(obj.check_isadmin_group_members(obj,group_id,cur_number))
        {

        }
        else
        {
        }
        Cursor c=obj.fetch_group_members(obj,group_id);
        if(c.getCount()>0)
        {
            c.moveToFirst();
            do {
                String number=c.getString(0);
                Contacts_Unregistered_Execute ob=new Contacts_Unregistered_Execute(getApplicationContext(),cur_number);
                String name=ob.get_name_message_table(ob,number);
                Choose_Contacts_Single_List list1=new Choose_Contacts_Single_List(name,number);
                list.add(list1);
                adapter.notifyDataSetChanged();
            }while (c.moveToNext());

        }
        rv_participants.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), rv_participants ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever

                    }

                    @Override public void onLongItemClick(View view, final int position) {
                        // do whatever
                        Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
                        CharSequence[] items;
                       // if(obj.check_isadmin_group_members(obj,group_id,cur_number)==true) {
                           items = new CharSequence[]{"Remove", "View", "Message"};
                        //}
                        //else
                       // {
                         //   items = new CharSequence[]{"View", "Message"};
                        //}
                        AlertDialog.Builder builder = new AlertDialog.Builder(Profile_Chat_Group.this);

                        builder.setTitle("Select The Action");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
                              //  if(obj.check_isadmin_group_members(obj,group_id,cur_number)==true) {
                                    if (item == 0)//remove
                                    {
                                        Choose_Contacts_Single_List list1 = list.get(position);
                                        remove(list1.get_number());
                                    }
                                //}
                            }
                        });
                        builder.show();
                    }
                })
        );
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leave_group();
            }
        });
    }
    BackGroundTaskLeave backGroundTaskLeave;
    void leave_group()
    {
        backGroundTaskLeave=new BackGroundTaskLeave(group_id);
        backGroundTaskLeave.execute(group_id,cur_number);
    }


    class BackGroundTaskLeave extends AsyncTask<String, Void, String> {
        int flag;
        int flag1=1;
        String group_id,number;
        BackGroundTaskLeave(String group_id)
        {
            this.group_id=group_id;
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String group_id=params[0];
            String number=params[1];
            this.number=number;

            String register_url="http://www.scintillato.esy.es/leave_group.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("group_id","UTF-8")+"="+URLEncoder.encode(group_id,"UTF-8")+"&"+
                        URLEncoder.encode("number","UTF-8")+"="+URLEncoder.encode(number,"UTF-8");
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
                    flag=0;
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
           //  loading.dismiss();
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
                    Chat_Database_Execute obj=new Chat_Database_Execute(ctx,cur_number);
                    obj.delete_group_selected(group_id);
                    obj.delete_group_members(group_id);
                    //delete from recent chats
                    Intent i=new Intent(ctx,Start_Page.class);
                    startActivity(i);
                }
            }
        }
        @Override
        protected void onPreExecute() {

          //  loading = ProgressDialog.show(ctx, "Status", "Leaving Group...",true,false);
        }
    }

    BackGroundTaskRemove backGroundTaskRemove;
    void remove(String number)
    {
        Toast.makeText(getApplicationContext(),number,Toast.LENGTH_SHORT).show();
        backGroundTaskRemove=new BackGroundTaskRemove(group_id);
        backGroundTaskRemove.execute(group_id,number);
    }
    class BackGroundTaskRemove extends AsyncTask<String, Void, String> {
        int flag;
        int flag1=1;
        String group_id,number;
        BackGroundTaskRemove(String group_id)
        {
            this.group_id=group_id;
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String group_id=params[0];
            String number=params[1];
            this.number=number;

            String register_url="http://www.scintillato.esy.es/remove_member_group.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("group_id","UTF-8")+"="+URLEncoder.encode(group_id,"UTF-8")+"&"+
                        URLEncoder.encode("number","UTF-8")+"="+URLEncoder.encode(number,"UTF-8")+"&"+
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
                    flag=0;
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
            //  loading.dismiss();
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
                    Chat_Database_Execute obj=new Chat_Database_Execute(ctx,cur_number);
                    //obj.delete_group_selected(group_id);
                    obj.delete_group_member_selected(this.number,group_id);
                    //delete from recent chats
                    Log.d("number",number);
                    obj.insert_message_group_boolean(obj,false,false,false,true,false,number,"",group_id,cur_number);
                    Intent i=new Intent(ctx,Start_Page.class);
                    startActivity(i);
                }
            }
        }
        @Override
        protected void onPreExecute() {

            //  loading = ProgressDialog.show(ctx, "Status", "Leaving Group...",true,false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_chat_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==R.id.profile_chat_group_add_members)
        {
            Intent i=new Intent(getApplicationContext(),Add_Member.class);
            i.putExtra("group_id",group_id);
            i.putExtra("rank",rank);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

}
