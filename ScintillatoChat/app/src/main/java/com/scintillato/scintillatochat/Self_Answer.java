package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class Self_Answer extends AppCompatActivity {


    private BackGroundTaskRegister backGroundTaskRegister;
    private ProgressBar progressBar;
    private String myJSON,user_id,last_id="0",user_name,cur_number,cur_user_id;
    private boolean last;
    private ListView lv_self_answer;
    private Context ctx;
    private Self_Answer_Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_answers);

        ctx=this;
        Bundle b=getIntent().getExtras();
        user_id=b.getString("user_id");
        user_name=b.getString("user_name");
        Log.d("user_name",user_name);

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

        progressBar=(ProgressBar)findViewById(R.id.progress_bar_self_answer);
        lv_self_answer=(ListView)findViewById(R.id.lv_self_answer);
        adapter=new Self_Answer_Adapter(getApplicationContext(),R.layout.self_answer_row);
        lv_self_answer.setAdapter(adapter);
        lv_self_answer.setOnScrollListener(new AbsListView.OnScrollListener(){
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
            }
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                if (lv_self_answer.getLastVisiblePosition() == lv_self_answer.getAdapter().getCount() -1 &&
                        lv_self_answer.getChildAt(lv_self_answer.getChildCount() - 1).getBottom() <= lv_self_answer.getHeight())
                {
                    if(last==false) {
                        progressBar.setVisibility(View.VISIBLE);
                        fetch_answers();
                    }
                }
            }
        });
        fetch_answers();
    }
    void fetch_answers()
    {
        last=true;
        backGroundTaskRegister=new BackGroundTaskRegister();
        Log.d("user_id_last",user_id+" "+last_id);
        backGroundTaskRegister.execute(user_id,last_id,cur_user_id);
    }

    class BackGroundTaskRegister extends AsyncTask<String, Void, String> {
        int flag1=1,flag;
        BackGroundTaskRegister()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String user_id=params[0];
            String last_id=params[1];
            String cur_user_id=params[2];
            String register_url="http://scintillato.esy.es/fetch_profile_answer.php";


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
            String question,question_id,answer_count,like_stat,comment_count,answer_id,answer,answer_time,answer_like_count,answer_image_status;

            while(count<jsonArray.length())
            {
                Log.d("list_inside","inside"+myJSON);

                JSONObject JO=jsonArray.getJSONObject(count);
                question_id=JO.getString("question_id");
                answer_id=JO.getString("answer_id");
                answer=JO.getString("answer");
                answer_time=JO.getString("answer_time");
                answer_like_count=JO.getString("answer_like_count");
                question=JO.getString("question");
                answer_count=JO.getString("answer_count");
                comment_count=JO.getString("comment_count");
                like_stat=JO.getString("like_stat");
                answer_image_status=JO.getString("answer_image_status");
                String[] a={"abc","bcd"};
                last=false;
                Self_Answer_List list=new Self_Answer_List(question_id,answer_id,answer,answer_time,answer_like_count,question,answer_count,comment_count,like_stat,user_name,user_id,answer_image_status);
              //  Feed_List list=new Feed_List(question_id,question,user_id,question_date,like_count,a,user_name,answer_count,like_stat);
                //Log.d("list1",list.getQuestion());
                adapter.add(list);
                adapter.notifyDataSetChanged();
                if (count==jsonArray.length()-1) {
                    if (last_id != answer_id) {
                        last_id = answer_id;

                    }
                    else
                        last=true;
                }


                count++;
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
