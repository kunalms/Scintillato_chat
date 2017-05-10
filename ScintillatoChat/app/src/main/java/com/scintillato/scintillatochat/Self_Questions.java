package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
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

public class Self_Questions extends AppCompatActivity {

    private String user_id,last_id="0",myJSON,user_name,username,cur_user_id,cur_number;
    private RecyclerView recyclerView_questions;
    private Feed_Adapter_Recycler adapter;
    private Context ctx;
    private ProgressBar progressBar;
    private boolean last;
    private ArrayList<Feed_List> feed_list;
    private BackGroundTaskRegister backGroundTaskRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_questions);
        ctx=this;
        Bundle b=getIntent().getExtras();
        user_id=b.getString("user_id");
        user_name=b.getString("user_name");
        username=b.getString("username");
        feed_list=new ArrayList<>();
       SharedPreferences sharedpreferences = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(getApplicationContext(),cur_number);
        Cursor c=obj.get_my_details(obj);
        if(c.getCount()>0)
        {
            c.moveToFirst();
            do
            {
                cur_user_id=c.getString(0);
            }while(c.moveToNext());
        }

        recyclerView_questions=(RecyclerView) findViewById(R.id.lv_self_questions_questions);
        progressBar=(ProgressBar)findViewById(R.id.progressBar_self_questions);
        adapter=new Feed_Adapter_Recycler(getApplicationContext(),feed_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView_questions.setLayoutManager(mLayoutManager);
        recyclerView_questions.setItemAnimator(new DefaultItemAnimator());
        recyclerView_questions.setAdapter(adapter);
        recyclerView_questions.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy < 0) {
                    // Recycle view scrolling up...

                } else if (dy > 0) {
                    if(last==false) {
                        progressBar.setVisibility(View.VISIBLE);
                        fetch_questions();
                    }
                    // Recycle view scrolling down...
                }
            }
        });
        /*recyclerView_questions.setOnScrollListener(new AbsListView.OnScrollListener(){
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
            }
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                if (recyclerView_questions.getLastVisiblePosition() == listView_questions.getAdapter().getCount() -1 &&
                        listView_questions.getChildAt(listView_questions.getChildCount() - 1).getBottom() <= listView_questions.getHeight())
                {
                    if(last==false) {
                        progressBar.setVisibility(View.VISIBLE);
                        fetch_questions();
                    }
                }
            }
        });*/
        fetch_questions();
    }
    void fetch_questions()
    {
        last=true;
        backGroundTaskRegister=new BackGroundTaskRegister();
        backGroundTaskRegister.execute(user_id,last_id,cur_user_id);
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
            String last_id=params[1];
            String cur_user_id=params[2];

            String register_url="http://scintillato.esy.es/fetch_profile_other_question.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                        URLEncoder.encode("last_id","UTF-8")+"="+URLEncoder.encode(last_id,"UTF-8")+"&"+
                        URLEncoder.encode("cur_user_id","UTF-8")+"="+URLEncoder.encode(cur_user_id,"UTF-8");

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
            String question,question_id,answer_count,question_date,like_count,like_stat,que_image_status;

            while(count<jsonArray.length())
            {
                Log.d("list_inside","inside"+myJSON);

                JSONObject JO=jsonArray.getJSONObject(count);
                question=JO.getString("question");
                question_id=JO.getString("question_id");
                answer_count=JO.getString("answer_count");
                question_date=JO.getString("question_date");
                like_count=JO.getString("like_count");
                like_stat=JO.getString("like_stat");
                que_image_status=JO.getString("que_image_status");
                String[] a={"abc","bcd"};
                Feed_List list=new Feed_List(username,question_id,question,user_id,question_date,like_count,a,user_name,answer_count,like_stat,"0",que_image_status);
                Log.d("list1",list.getQuestion());
                feed_list.add(list);
                adapter.notifyDataSetChanged();
                last=false;
                if (count==jsonArray.length()-1) {
                    if (last_id != question_id) {
                        last_id = question_id;
                    }
                    else
                        last=true;
                }


                count++;
            }
            adapter.notifyDataSetChanged();

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
