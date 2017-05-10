package com.scintillato.scintillatochat;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class Self_Profile_Group extends AppCompatActivity {

    private ListView groups_listview;
    private String user_id,cur_number,last_id="0";
    private BackGroundTaskFetchGroups backGroundTaskFetchGroups;
    private ProgressBar progressBar;
    private boolean last=false;
    private Context ctx;
    private Trending_Group_Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_profile_group);
        ctx=this;
        progressBar=(ProgressBar)findViewById(R.id.progress_bar_self_profile_group);
        groups_listview=(ListView)findViewById(R.id.lv_self_profile_group_groups);
        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        adapter=new Trending_Group_Adapter(getApplicationContext(),R.layout.trending_group_row);
        groups_listview.setAdapter(adapter);
        Bundle b=getIntent().getExtras();
        user_id=b.getString("user_id");
        groups_listview.setOnScrollListener(new AbsListView.OnScrollListener(){
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
            }
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                if (groups_listview.getLastVisiblePosition() == groups_listview.getAdapter().getCount() -1 &&
                        groups_listview.getChildAt(groups_listview.getChildCount() - 1).getBottom() <= groups_listview.getHeight())
                {
                    if(last==false) {
                        progressBar.setVisibility(View.VISIBLE);
                        Log.d("here",last_id);
                        fetch_groups();
                    }
                }
            }
        });
        groups_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Trending_Group_List trending_group_list=(Trending_Group_List) adapter.getItem(position);
                final Dialog dialog = new Dialog(Self_Profile_Group.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(R.layout.trending_groups_details_dialoge);
                // Set dialog title
                // set values for custom dialog components - text, image and button
                TextView name = (TextView) dialog.findViewById(R.id.tv_trending_groups_details_dialoge_name);

                TextView topic = (TextView) dialog.findViewById(R.id.tv_trending_groups_details_dialoge_topic);
                TextView description= (TextView) dialog.findViewById(R.id.tv_trending_groups_details_dialoge_description);
                TextView followers = (TextView) dialog.findViewById(R.id.tv_trending_groups_details_dialoge_followers);
                name.setText(trending_group_list.getGroup_name());
                description.setText(trending_group_list.getGroup_description());
                topic.setText(trending_group_list.getGroup_topic());
                followers.setText(trending_group_list.getGroup_count());
                dialog.show();
                TextView ok;
                ok = (TextView) dialog.findViewById(R.id.btn_trending_groups_details_dialoge_ok);
                ok.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                });

            }
        });
        fetch_groups();
    }
    void fetch_groups()
    {
        last=true;
        backGroundTaskFetchGroups=new BackGroundTaskFetchGroups();
        Log.d("user_id_group",user_id);
        backGroundTaskFetchGroups.execute(user_id,last_id);
    }
    class BackGroundTaskFetchGroups extends AsyncTask<String, Void, String>
    {
        int flag1=1,flag;
        BackGroundTaskFetchGroups()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String user_id=params[0];
            String last_id=params[1];


            String register_url="http://scintillato.esy.es/fetch_profile_group.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                        URLEncoder.encode("last_id","UTF-8")+"="+URLEncoder.encode(last_id,"UTF-8");

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

            progressBar.setVisibility(View.GONE);
            if(flag1==0)
            {

                Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
            }
            else
            {

                if(flag==1)
                {
                    Log.d("get_group",result);
                    add_groups_list(result);

                }
                else
                {
                    Toast.makeText(ctx, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        @Override
        protected void onPreExecute() {
        }
    }

    public void add_groups_list(String myJSON) {
        int  count = 0;
        Log.d("get_group1",myJSON);
        JSONObject jsonObject;
        JSONArray jsonArray;

        try {
            jsonObject = new JSONObject(myJSON);
            jsonArray = jsonObject.getJSONArray("result");
            String group_public_id,group_name,group_topic,group_description,group_create_date,group_count,member_phone,category_id;
            Log.d("length1", jsonArray.length()+"");


            while (count < jsonArray.length()) {
                JSONObject JO = jsonArray.getJSONObject(count);
                group_public_id = JO.getString("group_id");
                group_name = JO.getString("group_name");
                group_topic = JO.getString("group_topic");
                group_description = JO.getString("group_description");
                group_create_date = JO.getString("group_create_date");
                group_count = JO.getString("group_count");
                member_phone = JO.getString("member_phone_admin");
                category_id = JO.getString("category_id");
                Log.d("get_group", group_topic);

                count++;
                Log.d("count", count + " " + jsonArray.length());
                last=false;
                Trending_Group_List list=new Trending_Group_List(group_public_id,group_name,group_topic,group_description,group_create_date,group_count,member_phone,category_id);
                adapter.add(list);
                if (last_id != group_public_id) {
                    last_id=group_public_id;
                }
                else
                    last=true;

            }
        }
        catch (Exception e)
        {
            Log.d("error","here");
        }
    }
    @Override
    public void onPause()
    {
        if(backGroundTaskFetchGroups!=null)
        {
            backGroundTaskFetchGroups.cancel(true);
        }
        super.onPause();
    }
    @Override
    public void onStop()
    {

        if(backGroundTaskFetchGroups!=null)
        {
            backGroundTaskFetchGroups.cancel(true);
        }
        super.onStop();
    }
}

