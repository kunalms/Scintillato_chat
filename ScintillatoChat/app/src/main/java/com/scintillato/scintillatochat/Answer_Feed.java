package com.scintillato.scintillatochat;

import android.app.Activity;
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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.util.ArrayList;
import java.util.List;

public class Answer_Feed extends AppCompatActivity {

    private RecyclerView answer_list;
    private TextView tv_user_name,tv_time,tv_question,tv_category,tv_like_answer;
    private ImageButton btn_like,btn_answer,btn_tag;
    private BackGroundTaskRegister backGroundTaskRegister;
    private String last_answer_id="-1",anonymous;
    private ProgressBar progressBar;
    private String cur_user_id,like_stat;
    private Context ctx;
    private boolean last;
    private List<Answer_List> anslist;

    private Answer_Feed_Adapter_Recycler adapter;
    private String question_id,user_name,like_count,answer_count,question,myJSON;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_feed);

        ctx=this;
        anslist=new ArrayList<>();
        progressBar=(ProgressBar)findViewById(R.id.progressBar_answer_feed);
        tv_user_name=(TextView)findViewById(R.id.tv_answer_feed_user);
        tv_like_answer=(TextView)findViewById(R.id.tv_answer_feed_likeanswer);
        tv_category=(TextView)findViewById(R.id.tv_answer_feed_category);
        tv_question=(TextView)findViewById(R.id.tv_answer_feed_question);
        tv_time=(TextView)findViewById(R.id.tv_answer_feed_time);
        btn_answer=(ImageButton)findViewById(R.id.btn_answer_feed_answer);
        btn_tag=(ImageButton)findViewById(R.id.btn_answer_feed_tag);
        btn_like=(ImageButton)findViewById(R.id.btn_answer_feed_like);
        answer_list=(RecyclerView) findViewById(R.id.lv_answer_feed_answers);
        tv_question.setMovementMethod(new ScrollingMovementMethod());
        adapter=new Answer_Feed_Adapter_Recycler(getApplicationContext(),anslist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        answer_list.setLayoutManager(mLayoutManager);
        answer_list.setItemAnimator(new DefaultItemAnimator());
        answer_list.setAdapter(adapter);

        Bundle b=getIntent().getExtras();
        question_id=b.getString("question_id");
        question=b.getString("question");
        user_name=b.getString("user_name");
        like_count=b.getString("like_count");
        answer_count=b.getString("answer_count");
        like_stat=b.getString("like_stat");
        anonymous=b.getString("anonymous");
        Log.d("like_stat1",like_stat);

        if (like_stat.equals("1"))
            btn_like.setImageResource(R.drawable.likefilled30);
        else
            btn_like.setImageResource(R.drawable.like30);

        tv_like_answer.setText(like_count+" LIKE  "+answer_count+" ANSWER");
        tv_question.setText(question);
        tv_category.setText("");
        if(anonymous.equals("0"))
        tv_user_name.setText(user_name);
        else
            tv_user_name.setText("Anonymous User");
        tv_time.setText("time");
        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        String cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(getApplication(),cur_number);
        Cursor c=obj.get_my_details(obj);
        c.moveToFirst();
        if(c.getCount()>0)
        {
            cur_user_id=c.getString(0);
        }
        btn_answer.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                Intent i=new Intent(getApplicationContext(),Answer_Community_Question.class);
                i.putExtra("question_id",question_id);
                i.putExtra("user_name",user_name);
                i.putExtra("question",question);
                i.putExtra("like_count",like_count);
                i.putExtra("answer_count",answer_count);
                i.putExtra("like_stat",like_stat);
                i.putExtra("anonymous",anonymous);

                startActivity(i);
                overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

            }
        });

        answer_list.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy < 0) {
                    // Recycle view scrolling up...

                } else if (dy > 0) {
                    if(last==false ) {
                        progressBar.setVisibility(View.VISIBLE);
                        fetch_answes();
                    }
                    // Recycle view scrolling down...
                }
            }
        });
       /* answer_list.setOnScrollListener(new AbsListView.OnScrollListener(){
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
            }
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                if (answer_list.getLastVisiblePosition() == answer_list.getAdapter().getCount() -1 &&
                        answer_list.getChildAt(answer_list.getChildCount() - 1).getBottom() <= answer_list.getHeight())
                {
                    if(last==false) {
                        progressBar.setVisibility(View.VISIBLE);
                        fetch_answes();
                    }
                    else
                        progressBar.setVisibility(View.GONE);
                }
            }
        });*/

        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                like_unlike_question(question_id,cur_user_id,like_stat);
                if(like_stat.equals("0"))
                {
                    like_stat="1";
                    btn_like.setImageResource(R.drawable.likefilled30);
                    like_count=(Integer.parseInt(like_count)+1)+"";
                    tv_like_answer.setText(like_count+" LIKE  "+answer_count+" ANSWER");

                }
                else
                {
                    like_stat="0";
                    btn_like.setImageResource(R.drawable.like30);
                    like_count=(Integer.parseInt(like_count)-1)+"";
                    tv_like_answer.setText(like_count+" LIKE  "+answer_count+" ANSWER");
                }

            }
        });

        fetch_answes();
    }

    void fetch_answes()
    {
        if (backGroundTaskRegister!=null)
            backGroundTaskRegister.cancel(true);
        backGroundTaskRegister=new BackGroundTaskRegister();
        backGroundTaskRegister.execute(question_id,last_answer_id,cur_user_id);
    }

    class BackGroundTaskRegister extends AsyncTask<String, Void, String> {
        int flag1=1;
        int flag;
        BackGroundTaskRegister()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String last_answer_id=params[1];
            String question_id=params[0];
            String user_id=params[2];

            String register_url="http://scintillato.esy.es/fetch_answers_community.php";
            try{
                URL url=new URL(register_url);

                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("question_id","UTF-8")+"="+URLEncoder.encode(question_id,"UTF-8")+"&"+
                        URLEncoder.encode("last_answer_id","UTF-8")+"="+URLEncoder.encode(last_answer_id,"UTF-8")+"&"+
                        URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8");

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
                    //Log.d("inside","1");
                    //tv_status.setText(line);

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

        Log.d("list_inside",myJSON);

        try {
            jsonObject=new JSONObject(myJSON);
            jsonArray=jsonObject.getJSONArray("result");
            String answer_id,answer,like_count,answer_time,user_id,username,user_name,stat,comment_count,answer_image_status;
            //	Log.d("length", jsonArray.length()+"");
            //       Toast.makeText(getApplicationContext(),jsonArray.length()+"",Toast.LENGTH_LONG).show();
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
                    comment_count=JO.getString("comment_count");
                    answer_image_status=JO.getString("answer_image_status");
                    String[] a = {"abc", "bcd"};
                    Log.d("user_id", user_id+" "+like_count);
                    Answer_List answer_list = new Answer_List(username, answer_id, answer, user_id, answer_time, like_count, user_name,question_id,stat,comment_count,answer_image_status);
                    anslist.add(answer_list);
                    if (count == jsonArray.length() - 1) {
                        if (last_answer_id != answer_id) {
                            last_answer_id = answer_id;

                        } else
                            last = true;
                    }

                    for(int i=0;i<anslist.size();i++)
                        Log.d("answer"+(i+1),anslist.get(i).getAnswer());
                    count++;
                }
                adapter.notifyDataSetChanged();
            }
        }
        catch (JSONException e)
        {
            Toast.makeText(getApplicationContext(),"error_answer",Toast.LENGTH_SHORT).show();
        }
    }


    void like_unlike_question(String question_id,String user_id,String status)
    {
        if(status.equals("0"))
        {
            like_question(question_id,user_id);
        }
        else {
            unlike_question(question_id,user_id);
        }
    }
    private BackGroundTaskLike backGroundTaskLike;
    void like_question(String question_id,String user_id)
    {

        Log.d("here","like");
        if(backGroundTaskLike!=null)
            backGroundTaskLike.cancel(true);
        register_url="http://scintillato.esy.es/like_question_community.php";
        backGroundTaskLike=new BackGroundTaskLike();
        backGroundTaskLike.execute(user_id,question_id);

    }
    void unlike_question(String question_id,String user_id)
    {

        Log.d("here","unlike");
        if(backGroundTaskLike!=null)
            backGroundTaskLike.cancel(true);
        register_url="http://scintillato.esy.es/unlike_question_community.php";
        backGroundTaskLike=new BackGroundTaskLike();
        backGroundTaskLike.execute(user_id,question_id);
    }
    String register_url;

    int flag;

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



            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                        URLEncoder.encode("question_id","UTF-8")+"="+URLEncoder.encode(question_id,"UTF-8");

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
