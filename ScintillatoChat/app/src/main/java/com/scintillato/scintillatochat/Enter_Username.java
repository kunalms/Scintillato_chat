package com.scintillato.scintillatochat;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PathPermission;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class Enter_Username extends AppCompatActivity {

    private String number;
    private CheckBox show_password;
    private EditText username,pwd,repwd;
    private ProgressDialog loading;
    private Context ctx;
    private Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx=this;
        setContentView(R.layout.enter_username);
        username=(EditText)findViewById(R.id.et_enter_username_username);
        next=(Button)findViewById(R.id.btn_enter_username_next);
        show_password=(CheckBox)findViewById(R.id.checkbox_enter_username_show_pwd);
        pwd=(EditText)findViewById(R.id.et_enter_username_pwd);
        repwd=(EditText)findViewById(R.id.et_enter_username_repwd);
        Bundle b=getIntent().getExtras();
        number=b.getString("number");
        show_pwd();
        next.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    /*if(username.getText().toString().equals("")==false && pwd.getText().equals("")==false && repwd.getText().equals("")==false) {
                        Intent i = new Intent(getApplicationContext(), Enter_Profile_Info.class);
                        i.putExtra("username", username.getText().toString());
                        i.putExtra("number",number);
                        startActivity(i);
                    }
                    else
                    {
                        final Dialog dialog = new Dialog(Enter_Username.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                        dialog.setContentView(R.layout.verify_phone_error_dialog);
                        // Set dialog title
                        // set values for custom dialog components - text, image and button
                        TextView dialogue_number = (TextView) dialog.findViewById(R.id.tv_verify_phone_error_dialog_1);
                        dialogue_number.setText("Please enter username!");

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
                    }*/
                    if(username.getText().toString().equals(""))
                    {
                        final Dialog dialog = new Dialog(Enter_Username.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                        dialog.setContentView(R.layout.verify_phone_error_dialog);
                        // Set dialog title
                        // set values for custom dialog components - text, image and button
                        TextView dialogue_number = (TextView) dialog.findViewById(R.id.tv_verify_phone_error_dialog_1);
                        dialogue_number.setText("Please enter username!");

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
                    }
                    else if(pwd.getText().toString().equals(""))
                    {
                        final Dialog dialog = new Dialog(Enter_Username.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                        dialog.setContentView(R.layout.verify_phone_error_dialog);
                        // Set dialog title
                        // set values for custom dialog components - text, image and button
                        TextView dialogue_number = (TextView) dialog.findViewById(R.id.tv_verify_phone_error_dialog_1);
                        dialogue_number.setText("Please enter password!");

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

                    }
                    else if(repwd.getText().toString().equals(""))
                    {
                        final Dialog dialog = new Dialog(Enter_Username.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                        dialog.setContentView(R.layout.verify_phone_error_dialog);
                        // Set dialog title
                        // set values for custom dialog components - text, image and button
                        TextView dialogue_number = (TextView) dialog.findViewById(R.id.tv_verify_phone_error_dialog_1);
                        dialogue_number.setText("Please re-enter password!");

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
                    }
                    else if(check_pwd()==false)
                    {

                        final Dialog dialog = new Dialog(Enter_Username.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                        dialog.setContentView(R.layout.verify_phone_error_dialog);
                        // Set dialog title
                        // set values for custom dialog components - text, image and button
                        TextView dialogue_number = (TextView) dialog.findViewById(R.id.tv_verify_phone_error_dialog_1);
                        dialogue_number.setText("Password Mis-match!");

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
                    }
                    else
                    {
                        check_username(username.getText().toString());

                    }
                }
        });
    }
    private boolean check_pwd()
    {
        if(pwd.getText().toString().equals(repwd.getText().toString()))
        {
            return true;
        }
        return false;
    }
    void show_pwd(){
        show_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // show password
                    repwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    repwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    // hide password
                    pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
    }

    private BackGroundTaskRegister backgroudtask;
    void check_username(String username)
    {
        backgroudtask=new BackGroundTaskRegister();
        backgroudtask.execute(username);
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

            String username=params[0];


            String register_url="http://www.scintillato.esy.es/check_username.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(username,"UTF-8");

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
                    Intent i = new Intent(getApplicationContext(), Enter_Profile_Info.class);
                    i.putExtra("username", username.getText().toString());
                    i.putExtra("number",number);
                    i.putExtra("password",pwd.getText().toString());
                    startActivity(i);
                    overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

                    //Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(ctx,"Username already exists!",Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected void onPreExecute() {
            loading = ProgressDialog.show(ctx, "Status", "Checking...",true,false);
            loading.setCancelable(false);
        }
    }
}
