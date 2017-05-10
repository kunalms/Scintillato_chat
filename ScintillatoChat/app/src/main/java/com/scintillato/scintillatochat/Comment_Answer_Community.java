package com.scintillato.scintillatochat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

public class Comment_Answer_Community extends AppCompatActivity {

    private Button post_comment;
    private EditText et_comment;
    private String answer_id,cur_user_id,cur_number,user_id,user_name,like_stat;
    private Context ctx;
    private ProgressDialog loading;
    private BackGroundTaskRegister backGroundTaskRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_answer_community);
        Bundle b=getIntent().getExtras();
        getWindow().setBackgroundDrawableResource(R.drawable.chatback);
        answer_id=b.getString("answer_id");
        user_id=b.getString("user_id");
        user_name=b.getString("user_name");

        Log.d("answer_id",answer_id+" "+user_id+" "+user_name);
      //  like_stat=b.getString("like_stat");
        ctx=this;
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(ctx,cur_number);
        Cursor c=obj.get_my_details(obj);
        c.moveToFirst();
        if(c.getCount()>0)
            cur_user_id=c.getString(0);
        post_comment=(Button)findViewById(R.id.btn_comment_answer_community_post);
        et_comment=(EditText)findViewById(R.id.et_comment_answer_community_comment);

        post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_comment.getText().toString().equals(""))
                {

                }
                else
                {
                    send_comment(et_comment.getText().toString(),cur_user_id,answer_id);
                }
            }
        });
    }
    void send_comment(String comment,String user_id,String answer_id)
    {
        backGroundTaskRegister=new BackGroundTaskRegister();
        backGroundTaskRegister.execute(comment,cur_user_id,answer_id);
    }

    int flag;
    class BackGroundTaskRegister extends AsyncTask<String, Void, String> {
        int flag1 = 1;

        BackGroundTaskRegister() {
            flag = 0;
        }

        @Override
        protected String doInBackground(String... params) {

            String comment = params[0];
            String user_id=params[1];
            String answer_id = params[2];
            String register_url = "http://scintillato.esy.es/comment_answer_community.php";


            try {
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode("comment", "UTF-8") + "=" + URLEncoder.encode(comment, "UTF-8") + "&" +
                        URLEncoder.encode("answer_id" , "UTF-8") + "=" + URLEncoder.encode(answer_id, "UTF-8")+ "&" +
                        URLEncoder.encode("user_id" , "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));

                String line = "";
                line = bufferedReader.readLine();


                bufferedReader.close();
                IS.close();
                httpURLConnection.disconnect();

                if (line.equals("") == false) {
                    flag = 1;
                    //Log.d("inside","1");
                    //tv_status.setText(line);

                } else {
                    //Log.d("outside","1");
                    flag = 0;
                    //tv_status.setText("failure");
                }

                return line;

            } catch (Exception e) {
                flag1 = 0;
                return "Check Internet Connection!";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            //  Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG);
            loading.dismiss();
            if(flag1==0)
            {
                Toast.makeText(ctx,result,Toast.LENGTH_LONG);
            }
            else
            {
                if(flag==1) {
                    Intent i=new Intent(ctx,Comment_Feed_Community.class);
                    //i.putExtra("user_id",user_id);
                    //i.putExtra("user_name",user_name);
                    i.putExtra("answer_id",answer_id);
                    finish();
                    startActivity(i);

                }
            }
        }
        @Override
        protected void onPreExecute() {

            loading = ProgressDialog.show(ctx, "Status", "Registering...",true,false);

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
