package com.scintillato.scintillatochat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
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

public class Ask_Question_Group extends AppCompatActivity {

    private EditText question,category;
    private Button post;
    private String question_text,json_category,group_id;
    private ArrayList<String> list_category_fetch;
    private Context ctx;
    private ListView list_category;
    private Ask_Question_Category_Adapter adapter;
    private ProgressDialog loading;
    private BackGroundFetch backGroundFetch;
    private BackGroundTaskRegister backGroundRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_question_group);
        ctx=this;
        Intent i = getIntent();
        Bundle b = i.getExtras();
        group_id = b.getString("group_id");
        Log.d("group_id11", group_id);
        question=(EditText)findViewById(R.id.et_ask_question_group_question);
        post=(Button)findViewById(R.id.btn_ask_question_group_post);
        question=(EditText)findViewById(R.id.et_ask_question_group_question);
        post=(Button)findViewById(R.id.btn_ask_question_group_post);
        category=(EditText)findViewById(R.id.et_ask_question_group_category);
        list_category=(ListView)findViewById(R.id.lv_ask_question_group_category);
        adapter=new Ask_Question_Category_Adapter(getApplicationContext(),R.layout.ask_question_category_row);
        list_category.setAdapter(adapter);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question_text=question.getText().toString();
                if(question_text.equals("")==true)
                {
                    Toast.makeText(getApplicationContext(),"Ask question",Toast.LENGTH_SHORT).show();
                }
                else
                    ask_question();
            }
        });

        category.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(backGroundFetch!=null)
                    backGroundFetch.cancel(true);
                adapter=null;
                list_category.setAdapter(null);
                adapter=new Ask_Question_Category_Adapter(getApplicationContext(),R.layout.ask_question_category_row);
                list_category.setAdapter(adapter);
                fetch(s+"");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

    }
    void fetch(String s){
        backGroundFetch=new BackGroundFetch();
        backGroundFetch.execute(s);
    }
    void ask_question()
    {
        ArrayList<Ask_questions_category_list> categoryArrayList =(ArrayList<Ask_questions_category_list>) adapter.list;

        JSONArray jsonArray=new JSONArray();
        for(int i=0;i<categoryArrayList.size();i++)
        {

            if(categoryArrayList.get(i).get_selected()==true) {
                jsonArray.put(Integer.parseInt(categoryArrayList.get(i).getTag_id()));
            }
        }
        json_category=jsonArray.toString();
        Toast.makeText(getApplicationContext(),json_category,Toast.LENGTH_LONG).show();
        String user_id=null;

        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        String cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(getApplicationContext(),cur_number);
        Cursor c=obj.get_my_details(obj);
        if(c.getCount()>0)
        {
            c.moveToFirst();
            user_id=c.getString(0);
        }
        backGroundRegister=new BackGroundTaskRegister();
        backGroundRegister.execute(question.getText().toString(),user_id,json_category,group_id,"0");



    }

    class BackGroundFetch extends AsyncTask<String, Void, String> {
        int flag1=1;
        BackGroundFetch()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String tags=params[0];
            String register_url="http://scintillato.esy.es/fetch_tags.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("tags","UTF-8")+"="+URLEncoder.encode(tags,"UTF-8");

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
            loading.dismiss();
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
                    decode_json(result);
                }
            }
        }
        @Override
        protected void onPreExecute() {
            loading = ProgressDialog.show(ctx, "Status", "Registering...",true,true);
            loading.setOnCancelListener(new DialogInterface.OnCancelListener() {

                public void onCancel(DialogInterface arg0) {

                }
            });

        }
    }

    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private int count;
    public void decode_json(String myJSON)
    {
        count=0;

        Log.d("list_inside","inside");

        try {
            jsonObject=new JSONObject(myJSON);
            jsonArray=jsonObject.getJSONArray("result");
            String tag_name,tag_id,community_id;

            while(count<jsonArray.length())
            {
                Log.d("list_inside","inside"+myJSON);

                JSONObject JO=jsonArray.getJSONObject(count);
                tag_id=JO.getString("tag_id");
                tag_name=JO.getString("tag_name");
                community_id=JO.getString("community_id");
                count++;
                Ask_questions_category_list list=new Ask_questions_category_list(tag_name,tag_id);
                adapter.add(list);
            }
        }
        catch (JSONException e)
        {

        }
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

            String question=params[0];
            String user_id=params[1];
            String json_tags=params[2];
            String group_id=params[3];
            String anonymous=params[4];

            String register_url="http://scintillato.esy.es/insert_question_group.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("question","UTF-8")+"="+URLEncoder.encode(question,"UTF-8")+"&"+
                        URLEncoder.encode("json_tags","UTF-8")+"="+URLEncoder.encode(json_tags,"UTF-8")+"&"+
                        URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                        URLEncoder.encode("anonymous","UTF-8")+"="+URLEncoder.encode(anonymous,"UTF-8")+"&"+
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
            loading.dismiss();
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
                    loading.dismiss();
                    Intent i=new Intent(getApplicationContext(),Message_Chat_Public_Group_Main.class);
                    finish();
                    i.putExtra("group_id", group_id);
                    Log.d("group_id_list", group_id);
                    startActivity(i);
                    overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

                    //Toast.makeText(Create_Page_3.this,result,Toast.LENGTH_LONG).show();
                }
            }
        }
        @Override
        protected void onPreExecute() {
            loading = ProgressDialog.show(ctx, "Status", "Registering...",true,true);
            loading.setOnCancelListener(new DialogInterface.OnCancelListener() {

                public void onCancel(DialogInterface arg0) {


                }
            });

        }
    }

}
