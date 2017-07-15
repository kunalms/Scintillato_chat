package com.scintillato.scintillatochat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;

public class VerifyOTP extends AppCompatActivity {

    private Context ctx;
    ProgressDialog loading;
    EditText otp;
    String mobileno,country_code="91";
    Button validate;

    String authkey = "164854A3ujGlFnI596524f3";
    //Multiple mobiles numbers separated by comma
    String mobile;

    //otp
    String OTP;

    URLConnection myURLConnection=null;
    URL myURL=null;
    BufferedReader reader=null;
    String response;
    //Verify OTP
    String mainUrl="https://control.msg91.com/api/verifyRequestOTP.php?";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_otp);
        ctx=this;
        validate= (Button)findViewById(R.id.btn_validate);
        otp=(EditText)findViewById(R.id.et_verify_otp);

        Bundle b=getIntent().getExtras();
        mobileno=b.getString("number");
        mobile=country_code+mobileno;

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OTP=otp.getText().toString();
                StringBuilder sbPostData= new StringBuilder(mainUrl);
                sbPostData.append("authkey="+authkey);
                sbPostData.append("&mobile="+mobile);
                sbPostData.append("&otp="+OTP);
                mainUrl = sbPostData.toString();
                Log.d("APICALL",mainUrl);
                CheckOTP checkOTP=new CheckOTP();
                checkOTP.execute(mainUrl);
            }
        });


    }

    class CheckOTP extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            loading = ProgressDialog.show(ctx, "Status", "Veryfing OTP...",true,false);
            loading.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String apicall=params[0];
                myURL = new URL(apicall);
                myURLConnection = myURL.openConnection();
                myURLConnection.connect();
                reader= new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

                //reading response

                while ((response = reader.readLine()) != null)
                    //print response
                    Log.d("APIRESPONSE", ""+response);

                //finally close connection
                reader.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            loading.dismiss();
            Intent intent = new Intent(ctx,Enter_Username.class);
            intent.putExtra("number",mobileno);
            startActivity(intent);
        }
    }
}
