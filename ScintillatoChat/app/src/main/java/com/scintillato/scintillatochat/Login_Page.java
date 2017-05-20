package com.scintillato.scintillatochat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class Login_Page extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
  //  private static final int REQUEST_READ_CONTACTS = 0;

    private EditText user_number,password;
    private Button signin,signup;
    private int flag_token;
    private ProgressDialog loading;
    private String token;
    private Context ctx;
    private CheckBox show_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        // Set up the login form.
        getSupportActionBar().hide();

        show_password=(CheckBox)findViewById(R.id.cb_login_page);
        SharedPreferences sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
        flag_token=sharedpreferences.getInt("flag_token", -1);
        token=sharedpreferences.getString("token","");


        ctx=this;
        user_number = (EditText) findViewById(R.id.et_login_page_number);
        user_number.setNextFocusDownId(R.id.et_login_page_pwd);
        password = (EditText) findViewById(R.id.et_login_page_pwd);
        password.setNextFocusDownId(R.id.btn_login_page_signin);
        signin = (Button) findViewById(R.id.btn_login_page_signin);
        signup=(Button)findViewById(R.id.btn_login_page_signup);
        signin.setBackgroundResource(R.drawable.textview_back);
        signin.setTextColor(getApplication().getResources().getColor(R.color.white));
        signin.setEnabled(false);
        signup.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Verify_Phone.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

            }
        });
        show_pwd();
        button_changer();
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    void login()
    {
        BackGroundTaskLogin backGroundTaskLogin=new BackGroundTaskLogin(ctx);
        backGroundTaskLogin.execute("+91"+user_number.getText().toString(),password.getText().toString(),token);
    }

    class BackGroundTaskLogin extends AsyncTask<String, Void, String> {

        Context ctx;
        int flag=0,flag1=1;
        BackGroundTaskLogin(Context ctx)
        {
            this.ctx=ctx;
            flag=0;
            flag1=1;
        }
        @Override
        protected String doInBackground(String... params) {
            String number= params[0];
            String password = params[1];
            String token=params[2];

            String register_url="http://scintillato.esy.es/login.php";
            Log.d("inside token", token);

            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("number","UTF-8")+"="+URLEncoder.encode(number,"UTF-8")+"&"+
                        URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8")+"&"+
                        URLEncoder.encode("token","UTF-8")+"="+URLEncoder.encode(token,"UTF-8");


                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS=httpURLConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(IS,"iso-8859-1"));
                String response="";
                String line="";
                line=bufferedReader.readLine();
                Log.e("result1", line);

                if(line.equals("")==true)
                {
                    Log.e("result2", line);

                    //txtv_login1_status.setText(line);
                    flag=0;
                }
                else
                    flag=1;
                bufferedReader.close();
                IS.close();
                httpURLConnection.disconnect();
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

            Log.e("result", result);
            Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
            loading.dismiss();
            if(flag1==0)
            {
                Toast.makeText(ctx,"Check Internet Connection!",Toast.LENGTH_LONG).show();
            }else{
                if(flag==1)
                {
                    fetch_user_details(result);
                }
                else
                {
                    Toast.makeText(ctx,"Invalid Credentials!",Toast.LENGTH_LONG).show();
                }
            }
        }
        @Override
        protected void onPreExecute() {

            loading = ProgressDialog.show(ctx, "Status", "Logging In...",true,false);
            loading.setCancelable(false);


        }
    }


    private JSONObject jsonObject;
    private JSONArray jsonArray;
    public void fetch_user_details(String myJSON)
    {
        loading.dismiss();

        try{
            try {
                jsonObject=new JSONObject(myJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray=jsonObject.getJSONArray("result");

            int count=0;
            String user_name,username,user_id,date,profile_pic;
            Log.d("length", jsonArray.length()+"");
            while(count<jsonArray.length())
            {
                JSONObject JO=jsonArray.getJSONObject(count);
                user_id=JO.getString("user_id");
                username=JO.getString("username");
                user_name=JO.getString("user_name");
                date=JO.getString("date");
                profile_pic=JO.getString("profile_pic");
              //  Log.d("details_id",user_id+user_name+username+user_number);
                My_Details_Execute obj=new My_Details_Execute(getApplicationContext(),"+91"+user_number.getText().toString());
                obj.delete_muy_details();
                obj.putinto_my_details(obj,username,user_name,"+91"+user_number.getText().toString(),user_id,date,profile_pic);
                SharedPreferences sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedpreferences.edit();
                editor.putString("number","+91"+user_number.getText().toString());
                editor.putInt("flag", 1);
                editor.putString("name", user_name);
                editor.commit();
                count++;
            }

            Intent i = new Intent(Login_Page.this, SyncActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    /*private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }


    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
  /*  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }
*/

        /**
         * Attempts to sign in or register the account specified by the login form.
         * If there are form errors (invalid email, missing fields, etc.), the
         * errors are presented and no actual login attempt is made.
         */


        void show_pwd(){
            show_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // checkbox status is changed from uncheck to checked.
                    if (!isChecked) {
                        // show password
                        password.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    } else {
                        password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                }
            });
        }
        void button_changer(){
            user_number.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.length()==10 && password.getText().length()>0){
                        signin.setEnabled(true);
                        signin.setTextColor(getApplication().getResources().getColor(R.color.black));
                        signin.setBackgroundResource(R.drawable.button_semi_transparent);
                    }
                    else{
                        signin.setEnabled(false);
                        signin.setTextColor(getApplication().getResources().getColor(R.color.white));
                        signin.setBackgroundResource(R.drawable.textview_back);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            password.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length()>0 && user_number.getText().length()==10){
                            signin.setEnabled(true);
                            signin.setTextColor(getApplication().getResources().getColor(R.color.black));
                            signin.setBackgroundResource(R.drawable.button_semi_transparent);
                    }
                    else{
                        signin.setEnabled(false);
                        signin.setTextColor(getApplication().getResources().getColor(R.color.white));
                        signin.setBackgroundResource(R.drawable.textview_back);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
}

