package com.scintillato.scintillatochat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by VIVEK on 21-12-2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    public static int i=0;
    private int flag;

    @Override
    public void onTokenRefresh()
    {
        String token= FirebaseInstanceId.getInstance().getToken();
        registerToken(token);
        i=1;
    }

    private void registerToken(String token)
    {


        SharedPreferences sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedpreferences.edit();
        editor.putString("token",token);
        editor.putInt("flag_token", 1);
        editor.commit();
        Intent i=new Intent(getApplicationContext(),Login_Page.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

}


