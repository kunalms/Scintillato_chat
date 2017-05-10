package com.scintillato.scintillatochat;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class Comment_Feed_Community extends AppCompatActivity {

    private ListView comment_list;
    private Comment_Feed_Community_Adapter adapter;
    private String answer_id,last_id="0",myJSON;
    private ProgressBar progressBar;
    private boolean last;
    private Context ctx;
    private BackGroundTaskRegister backGroundTaskRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_feed_community);
        ctx=this;
        progressBar=(ProgressBar)findViewById(R.id.progressBar_comment_feed_community);
        comment_list=(ListView)findViewById(R.id.lv_comment_feed_community_comment);
        adapter=new Comment_Feed_Community_Adapter(getApplicationContext(),R.layout.comment_feed_community_row);
        comment_list.setAdapter(adapter);
        Bundle b=getIntent().getExtras();

        answer_id=b.getString("answer_id");
        Log.d("answer_id",answer_id);
        fetch_comment();
        comment_list.setOnScrollListener(new AbsListView.OnScrollListener(){
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
            }
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                if (comment_list.getLastVisiblePosition() == comment_list.getAdapter().getCount() -1 &&
                        comment_list.getChildAt(comment_list.getChildCount() - 1).getBottom() <= comment_list.getHeight())
                {
                    if(last==false) {
                        progressBar.setVisibility(View.VISIBLE);
                        fetch_comment();
                    }
                    else
                        progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
    void fetch_comment()
    {
        last=true;
        backGroundTaskRegister=new BackGroundTaskRegister();
        backGroundTaskRegister.execute(answer_id,last_id);
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

            String answer_id=params[0];
            String last_id=params[1];
            String register_url="http://scintillato.esy.es/fetch_comment_community.php";
            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("answer_id","UTF-8")+"="+URLEncoder.encode(answer_id,"UTF-8")+"&"+
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
            String comment_id,comment_date_time,comment,user_id,user_name;
            //	Log.d("length", jsonArray.length()+"");
            Log.d("answer_id",myJSON);
            //       Toast.makeText(getApplicationContext(),jsonArray.length()+"",Toast.LENGTH_LONG).show();
            if(jsonArray.length()>0) {
                while (count < jsonArray.length()) {
                    Log.d("list_inside", "inside" + myJSON);

                    JSONObject JO = jsonArray.getJSONObject(count);
                    comment_id = JO.getString("comment_id");
                    comment = JO.getString("comment");
                    comment_date_time = JO.getString("comment_date_time");
                    user_id = JO.getString("user_id");
                    user_name = JO.getString("user_name");
                    last=false;
                    Comment_Feed_Community_List list = new Comment_Feed_Community_List(comment_id,comment_date_time,comment,user_id,user_name,answer_id);
                    adapter.add(list);
                    adapter.notifyDataSetChanged();
                    if (count == jsonArray.length() - 1) {
                        if (last_id != comment_id) {
                            last_id = comment_id;

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
