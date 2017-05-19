package com.scintillato.scintillatochat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class Group_Answer extends AppCompatActivity {


    private TextView user,question;
    private EditText answer;
    private Context ctx;
    private ProgressDialog loading;
    private String answer_text,question_id,user_id,group_id,question_text,user_name,cur_user_id;
    private Button post;
    private BackGroundTaskRegister backGroundTaskRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_answer);
        ctx=this;

        Bundle b=getIntent().getExtras();
        group_id=b.getString("group_id");
        user_id=b.getString("user_id");
        question_id=b.getString("question_id");
        question_text=b.getString("question");
        user_name=b.getString("user_name");
        cur_user_id=b.getString("cur_user_id");

        post=(Button)findViewById(R.id.btn_group_answer_post);
        user=(TextView)findViewById(R.id.tv_group_answer_user);
        question=(TextView)findViewById(R.id.tv_group_answer_question);
        answer=(EditText)findViewById(R.id.et_group_answer_answer);

        question.setText(question_text);
        user.setText(user_name);


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer_text=answer.getText().toString();

                if(answer_text.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Answer cannot be blank!",Toast.LENGTH_LONG).show();
                }
                else
                {
                    answer_question(group_id,cur_user_id,question_id,answer_text);
                }
            }
        });
    }

    void answer_question(String group_id,String user_id,String question_id,String answer_text)
    {
        backGroundTaskRegister=new BackGroundTaskRegister();
        backGroundTaskRegister.execute(group_id,user_id,question_id,answer_text);
    }


    int flag;
    class BackGroundTaskRegister extends AsyncTask<String, Void, String> {
        int flag1 = 1;

        BackGroundTaskRegister() {
            flag = 0;
        }

        @Override
        protected String doInBackground(String... params) {

            String group_id=params[0];
            String answer = params[3];
            String question_id = params[2];
            String user_id=params[1];

            String register_url = "http://scintillato.esy.es/answer_group.php";


            try {
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode("answer", "UTF-8") + "=" + URLEncoder.encode(answer, "UTF-8") + "&" +
                        URLEncoder.encode("question_id" , "UTF-8") + "=" + URLEncoder.encode(question_id, "UTF-8")+ "&" +
                        URLEncoder.encode("user_id" , "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8")+ "&" +
                        URLEncoder.encode("group_id" , "UTF-8") + "=" + URLEncoder.encode(group_id, "UTF-8");

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
                    Intent i=new Intent(getApplicationContext(),Message_Chat_Public_Group_Main.class);
                    i.putExtra("group_id",group_id);
                    finish();
                    startActivity(i);
                    overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

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
