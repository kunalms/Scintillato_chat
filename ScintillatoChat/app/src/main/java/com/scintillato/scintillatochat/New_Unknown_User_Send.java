package com.scintillato.scintillatochat;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class New_Unknown_User_Send extends AppCompatActivity {

    private EditText number;
    private Button go;
    private String num;
    private TextView status;
    private ProgressDialog loading;
    private Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_unknown_user_send);
        ctx=this;
        number=(EditText)findViewById(R.id.et_new_unknown_user_send_number);
        go=(Button)findViewById(R.id.btn_new_unknown_user_send_go);
        status=(TextView)findViewById(R.id.tv_new_unknown_user_send_status);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num=number.getText().toString();
                if (num.equals("")) {
                    final Dialog dialog = new Dialog(New_Unknown_User_Send.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    dialog.setContentView(R.layout.verify_phone_error_dialog);
                    // Set dialog title
                    // set values for custom dialog components - text, image and button
                    TextView dialogue_number = (TextView) dialog.findViewById(R.id.tv_verify_phone_error_dialog_1);
                    dialogue_number.setText("Please enter your phone number.");

                    dialog.show();
                    TextView ok;
                    ok = (TextView) dialog.findViewById(R.id.tv_verify_phone_error_dialog_ok);

                    ok.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            dialog.cancel();
                        }
                    });

                } else if (num.length() > 10) {
                    final Dialog dialog = new Dialog(New_Unknown_User_Send.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    dialog.setContentView(R.layout.verify_phone_error_dialog);
                    // Set dialog title
                    // set values for custom dialog components - text, image and button
                    TextView dialogue_number = (TextView) dialog.findViewById(R.id.tv_verify_phone_error_dialog_1);
                    dialogue_number.setText("The phone number you entered is too long for India.");

                    dialog.show();
                    TextView ok;
                    ok = (TextView) dialog.findViewById(R.id.tv_verify_phone_error_dialog_ok);

                    ok.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            dialog.cancel();
                        }
                    });

                } else if (num.length() < 10) {
                    final Dialog dialog = new Dialog(New_Unknown_User_Send.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    dialog.setContentView(R.layout.verify_phone_error_dialog);
                    // Set dialog title
                    // set values for custom dialog components - text, image and button
                    TextView dialogue_number = (TextView) dialog.findViewById(R.id.tv_verify_phone_error_dialog_1);
                    dialogue_number.setText("The phone number you entered is too short for India.");

                    dialog.show();
                    TextView ok;
                    ok = (TextView) dialog.findViewById(R.id.tv_verify_phone_error_dialog_ok);

                    ok.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            dialog.cancel();
                        }
                    });

                } else {
                    num="+91"+num;
                    check_number(num);

                    //	check_number("+91"+number.getText().toString()); baadme daal dunga
                }
            }
        });

    }

    private BackGroundTaskRegister backgroudtask;
    void check_number(String number)
    {
        backgroudtask=new BackGroundTaskRegister();
        backgroudtask.execute(number);
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

            String number=params[0];


            String register_url="http://www.scintillato.esy.es/check_mobile_no.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("mobile_no","UTF-8")+"="+URLEncoder.encode(number,"UTF-8");

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

                if(line.equals("0")==true)
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

            }
            else
            {

                if(flag==1)
                {
                    status.setText("Invalid User!");
                }
                else
                {
                    Intent i=new Intent(ctx,Message_Chat_Single.class);
                    i.putExtra("user_number",num);
                    startActivity(i);
                    overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

                }
               }
        }
        @Override
        protected void onPreExecute() {
            loading = ProgressDialog.show(ctx, "Status", "Checking...",true,false);
            loading.setCancelable(false);
        }
    }

    @Override
    public void onPause()
    {
        if(backgroudtask!=null)
        {
            backgroudtask.cancel(true);
            loading.cancel();
        }
        super.onPause();
    }


    @Override
    public void onStop()
    {
        if(backgroudtask!=null)
        {
            backgroudtask.cancel(true);
            loading.cancel();
        }
        super.onPause();
    }
}
