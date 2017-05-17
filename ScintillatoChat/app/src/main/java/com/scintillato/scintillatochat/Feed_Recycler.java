package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

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

public class Feed_Recycler extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView list_questions;
    private Feed_Adapter_Recycler adapter;
    private ArrayList<Feed_List> feedList;
    private String myJSON,last_question_id="-1",cur_number;
    private ProgressBar progressBar;
    private boolean last=false,currently_fetching=false;
    private BackGroundTaskRegister backGroundTaskRegister;

    private Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_super_recycler);
        ctx=this;
        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        feedList=new ArrayList<>();

        progressBar=(ProgressBar)findViewById(R.id.progress_bar_feed);
        list_questions=(RecyclerView) findViewById(R.id.lv_feed_feed);
        adapter=new Feed_Adapter_Recycler(getApplication(),feedList);
        list_questions.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        list_questions.setLayoutManager(mLayoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        list_questions.setItemAnimator(itemAnimator);

        fab=(FloatingActionButton)findViewById(R.id.fab_feed_super);
        fab.setIcon(R.drawable.create_white_24);
        fab.setColorNormalResId(R.color.cyan);

        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                Intent i=new Intent(getApplicationContext(),Ask_Question_Category.class);
                startActivity(i);
            }
        });


        list_questions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                Log.d("here","here");
                if (!recyclerView.canScrollVertically(-1)) {
                    //onscrolled to top
                }
                else if (!recyclerView.canScrollVertically(1)) {
                    Log.d("here","here2");
                    //onScrolledToBottom();
                    if(last==false && currently_fetching==false) {
                        currently_fetching=true;
                        progressBar.setVisibility(View.VISIBLE);
                        fetch_questions();
                    }

                } else if (dy < 0) {
                    Log.d("here","here3");
                    //  onScrolledUp();
                } else if (dy > 0) {
                    Log.d("here","here4");
                    //  onScrolledDown();
                }
            }
        });
/*
        list_questions.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy < 0) {
                    // Recycle view scrolling up...

                } else if (dy > 0) {
                    // Recycle view scrolling down...
                }
            }
        });
*/
      /*  list_questions.setOnScrollListener(new AbsListView.OnScrollListener(){
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
            }
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                if (list_questions.getLastVisiblePosition() == list_questions.getAdapter().getCount() -1 &&
                        list_questions.getChildAt(list_questions.getChildCount() - 1).getBottom() <= list_questions.getHeight())
                {
                    if(last==false) {
                        progressBar.setVisibility(View.VISIBLE);
                        fetch_questions();
                    }
                }
            }
        });
        list_questions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),"abc",Toast.LENGTH_SHORT).show();
            }
        });
*/
        fetch_questions();
    }
    void fetch_questions()
    {
        String user_id="";
        My_Details_Execute obj=new My_Details_Execute(getApplicationContext(),cur_number);
        Cursor cursor=obj.get_my_details(obj);
        if (cursor.getCount()>0)
        {
            cursor.moveToFirst();
            user_id=cursor.getString(0);
        }
        backGroundTaskRegister=new BackGroundTaskRegister();
        backGroundTaskRegister.execute(user_id,last_question_id);
    }

    int flag;

    class BackGroundTaskRegister extends AsyncTask<String, Void, String> {
        int flag1=1;
        BackGroundTaskRegister()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String user_id=params[0];
            String last_question_id=params[1];

            String register_url="http://scintillato.esy.es/fetch_questions_community.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                        URLEncoder.encode("last_question_id","UTF-8")+"="+URLEncoder.encode(last_question_id,"UTF-8");

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
            Log.d("1",flag+"");
            Log.d("abbbbbbbcccc",result);
            //  Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG);

            if(flag1==0)
            {
                Toast.makeText(ctx,result,Toast.LENGTH_LONG);
            }
            else
            {

                if(flag==1)
                {
                    progressBar.setVisibility(View.GONE);

                    myJSON=result;
                    Log.d("list_inside_here","inside"+myJSON);

                    encode_json(myJSON);
                }
            }
        }
        @Override
        protected void onPreExecute() {

        }
    }


    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private int count;
    public void encode_json(String myJSON)
    {
        count=0;

        Log.d("list_inside","inside");

        try {
            jsonObject=new JSONObject(myJSON);
            jsonArray=jsonObject.getJSONArray("result");
            String question,user_name,user_id,username,question_id,answer_count,question_date,like_count,stat,anonymous,que_image_status;

            while(count<jsonArray.length())
            {
                Log.d("list_inside","inside"+myJSON);

                JSONObject JO=jsonArray.getJSONObject(count);
                question=JO.getString("question");
                question_id=JO.getString("question_id");
                user_id=JO.getString("user_id");
                user_name=JO.getString("user_name");
                username=JO.getString("username");
                answer_count=JO.getString("answer_count");
                question_date=JO.getString("question_date");
                like_count=JO.getString("like_count");
                stat=JO.getString("stat");
                anonymous=JO.getString("anonymous");
                que_image_status=JO.getString("que_image_status");

                String[] a={"abc","bcd"};
                Feed_List list=new Feed_List(username,question_id,question,user_id,question_date,like_count,a,user_name,answer_count,stat,anonymous,que_image_status);
                Log.d("list1",list.getQuestion());
                feedList.add(list);
                if (count==jsonArray.length()-1) {
                    if (last_question_id != question_id) {
                        last_question_id = question_id;
                    }
                    else
                        last=true;
                }


                count++;
            }
            SharedPreferences sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedpreferences.edit();
            editor.putString("refresh_flag_feed","1");
            editor.commit();

            adapter.notifyDataSetChanged();

            currently_fetching=false;
            for(int i=0;i<feedList.size();i++)
            {
                Log.d("list",feedList.get(i).getQuestion());
            }
        }
        catch (JSONException e)
        {

        }
    }
    @Override
    public void onPause()
    {
        if(backGroundTaskRegister!=null)
        {
            backGroundTaskRegister.cancel(true);
        }
        super.onPause();
    }
    @Override
    public void onStop()
    {
        if(backGroundTaskRegister!=null)
        {
            backGroundTaskRegister.cancel(true);
        }
        super.onStop();
    }
}
