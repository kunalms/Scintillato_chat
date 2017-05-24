package com.scintillato.scintillatochat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class Answer_Group_Feed extends AppCompatActivity {

    private BackGroundTaskRegister backGroundTaskRegister;
    private ListView list_answer;
    private Answer_Group_Feed_Adapter adapter;
    private String myJSON,question_id,last_answer_id="-1",user_id,cur_user_id,group_id,user_name,time,question,like_count,like_stat,answer_count;
    private Context ctx;
    private String register_url;
    private TextView tv_user_name,tv_time,tv_question,tv_like_count,tv_answer_count;
    private ImageButton ib_like,ib_answer,ib_tags;
    private boolean last;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_group_feed);
        ctx=this;
        tv_user_name=(TextView)findViewById(R.id.tv_answer_group_feed_user);
        tv_like_count=(TextView)findViewById(R.id.tv_answer_group_feed_like);
        tv_answer_count=(TextView)findViewById(R.id.tv_answer_group_feed_answer);
        tv_question=(TextView)findViewById(R.id.tv_answer_group_feed_question);
        tv_time=(TextView)findViewById(R.id.tv_answer_group_feed_time);
        ib_answer=(ImageButton)findViewById(R.id.btn_answer_group_feed_answer);
        ib_like=(ImageButton)findViewById(R.id.btn_answer_group_feed_like);
        ib_tags=(ImageButton)findViewById(R.id.btn_answer_group_feed_tag);

        list_answer=(ListView)findViewById(R.id.lv_answer_group_feed_answers);
        adapter=new Answer_Group_Feed_Adapter(getApplicationContext(),R.layout.answer_group_feed_row);
        list_answer.setAdapter(adapter);
        progressBar=(ProgressBar)findViewById(R.id.progressBar_answer_group_feed);
        progressBar.setVisibility(View.GONE);

        Bundle b=getIntent().getExtras();
        question_id=b.getString("question_id");
        group_id=b.getString("group_id");
        user_name=b.getString("user_name");
        user_id=b.getString("user_id");
        time=b.getString("time");
        question=b.getString("question");
        like_count=b.getString("like_count");
        like_stat=b.getString("like_stat");
        answer_count=b.getString("answer_count");

        if(like_stat.equals("1"))
            ib_like.setImageResource(R.drawable.likefilled30);
        else
            ib_like.setImageResource(R.drawable.like30);

        tv_answer_count.setText("Comments:"+answer_count);
        tv_like_count.setText("Likes:"+like_count);
        tv_user_name.setText(user_name);
        tv_question.setText(question);
        tv_time.setText(time);


        ib_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ctx,Group_Answer.class);
                i.putExtra("question_id",question_id);
                i.putExtra("group_id",group_id);
                i.putExtra("user_id",user_id);
                i.putExtra("question",question);
                i.putExtra("user_name",user_name);
                i.putExtra("cur_user_id",cur_user_id);
                ctx.startActivity(i);
               // ((Activity)ctx).overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);


            }
        });

        ib_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like_unlike_question(question_id,cur_user_id,like_stat,group_id);
                if(like_stat.equals("0"))
                {
                    like_stat="1";
                    ib_like.setImageResource(R.drawable.likefilled30);
                    like_count=Integer.parseInt(like_count)+1+"";
                    tv_like_count.setText("Likes:"+like_count);
                }
                else
                {
                    like_stat="0";
                    ib_like.setImageResource(R.drawable.like30);
                    like_count=Integer.parseInt(like_count)-1+"";
                    tv_like_count.setText("Likes:"+like_count);
                }
            }
        });
        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        String cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(ctx,cur_number);
        Cursor c=obj.get_my_details(obj);
        c.moveToFirst();
        if (c.getCount()>0)
            cur_user_id=c.getString(0);
        fetch_answers();
    }

    void fetch_answers()
    {

        if (backGroundTaskRegister!=null)
            backGroundTaskRegister.cancel(true);
        backGroundTaskRegister=new BackGroundTaskRegister();
        backGroundTaskRegister.execute(question_id,last_answer_id,cur_user_id,group_id);
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

            String last_answer_id=params[1];
            String question_id=params[0];
            String user_id=params[2];
            String group_id=params[3];
            Log.d("abc",last_answer_id+" "+question_id+" "+user_id+" "+group_id);
            String register_url="http://scintillato.esy.es/fetch_answer_group.php";
            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("question_id","UTF-8")+"="+URLEncoder.encode(question_id,"UTF-8")+"&"+
                        URLEncoder.encode("last_answer_id","UTF-8")+"="+URLEncoder.encode(last_answer_id,"UTF-8")+"&"+
                        URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                        URLEncoder.encode("group_id","UTF-8")+"="+URLEncoder.encode(group_id,"UTF-8");

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
                    //Toast.makeText(Create_Page_3.this,result,Toast.LENGTH_LONG).show();
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

        Log.d("list_inside",myJSON);

        try {
            jsonObject=new JSONObject(myJSON);
            jsonArray=jsonObject.getJSONArray("result");
            String answer_id,answer,like_count,answer_time,user_id,username,user_name,stat;
            if(jsonArray.length()>0) {
                while (count < jsonArray.length()) {
                    Log.d("list_inside", "inside" + myJSON);

                    JSONObject JO = jsonArray.getJSONObject(count);
                    answer = JO.getString("answer");
                    answer_id = JO.getString("answer_id");
                    user_id = JO.getString("user_id");
                    like_count = JO.getString("like_count");
                    answer_time = JO.getString("answer_time");
                    user_name = JO.getString("user_name");
                    username = JO.getString("username");
                    stat=JO.getString("stat");
                    String[] a = {"abc", "bcd"};
                    Log.d("user_id", user_id+" "+like_count);
                    Answer_Group_Feed_List list = new Answer_Group_Feed_List(username, answer_id, answer, user_id, answer_time, like_count, user_name,question_id,stat,group_id);
                    adapter.add(list);
                    adapter.notifyDataSetChanged();
                    if (count == jsonArray.length() - 1) {
                        if (last_answer_id != answer_id) {
                            last_answer_id = answer_id;

                        } else
                            last = true;
                    }


                    count++;
                }
            }
        }
        catch (JSONException e)
        {

        }
    }


    void like_unlike_question(String question_id,String user_id,String status,String group_id)
    {
        if(status.equals("0"))
        {
            like_question(question_id,user_id,group_id);
        }
        else {
            unlike_question(question_id,user_id,group_id);
        }
    }
    void like_question(String question_id,String user_id,String group_id)
    {

        Log.d("here","like");
        if(backGroundTaskLike!=null)
            backGroundTaskLike.cancel(true);
        register_url="http://scintillato.esy.es/like_question_group.php";
        backGroundTaskLike=new BackGroundTaskLike();
        backGroundTaskLike.execute(user_id,question_id,group_id);

    }
    void unlike_question(String question_id,String user_id,String group_id)
    {

        Log.d("here","unlike");
        if(backGroundTaskLike!=null)
            backGroundTaskLike.cancel(true);
        register_url="http://scintillato.esy.es/unlike_question_group.php";
        backGroundTaskLike=new BackGroundTaskLike();
        backGroundTaskLike.execute(user_id,question_id,group_id);
    }

    BackGroundTaskLike backGroundTaskLike;
    class BackGroundTaskLike extends AsyncTask<String, Void, String> {
        int flag1=1;
        BackGroundTaskLike()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String user_id=params[0];
            String question_id=params[1];
            String group_id=params[2];



            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                        URLEncoder.encode("question_id","UTF-8")+"="+URLEncoder.encode(question_id,"UTF-8")+"&"+
                        URLEncoder.encode("group_id","UTF-8")+"="+URLEncoder.encode(group_id,"UTF-8");

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

            if(flag1==0)
            {
                Toast.makeText(ctx,result,Toast.LENGTH_LONG);
            }
            else
            {

                if(flag==1)
                {
                    Toast.makeText(ctx,"done",Toast.LENGTH_LONG);

                    //Toast.makeText(Create_Page_3.this,result,Toast.LENGTH_LONG).show();
                }
            }
        }
        @Override
        protected void onPreExecute() {

        }
    }

    @Override
    public void onPause()
    {
        if(backGroundTaskRegister!=null)
        {
            backGroundTaskRegister.cancel(true);
        }

        if (backGroundTaskLike!=null)
        {
            backGroundTaskLike.cancel(true);
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
        if (backGroundTaskLike!=null)
        {
            backGroundTaskLike.cancel(true);
        }
        super.onStop();
    }
}
