package com.scintillato.scintillatochat;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by VIVEK on 01-03-2017.
 */

class BackGroundTaskInsertUnique extends AsyncTask<String, Void, String> {
    int flag1=1,flag;
    private Context ctx;
    private Calendar c;
    private SimpleDateFormat df;
    private String formattedDate;
    BackGroundTaskInsertUnique(Context ctx)
    {
        this.ctx=ctx;
        flag=0;
    }
    @Override
    protected String doInBackground(String... params) {

        String user_number=params[0];
        String status=params[1];
        String date=get_datetime();
        String register_url="http://www.scintillato.esy.es/update_status_online.php";


        try{
            URL url=new URL(register_url);
            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setConnectTimeout(10000);
            OutputStream OS=httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
            String data= URLEncoder.encode("user_number","UTF-8")+"="+URLEncoder.encode(user_number,"UTF-8")+"&"+
                    URLEncoder.encode("status","UTF-8")+"="+URLEncoder.encode(status,"UTF-8")+"&"+
                    URLEncoder.encode("datetime","UTF-8")+"="+URLEncoder.encode(date,"UTF-8");

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

            Log.d("yaha","sad2");

            if(line.equals("")==false)
            {
                flag=1;
                //Log.d("inside","1");
                //tv_status.setText(line);

            }
            else
            {
                flag=0;
                //.setText("failure");
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
        // loading.dismiss();
        Log.d("yaha","1sad");

        Toast.makeText(ctx,result,Toast.LENGTH_LONG);

        if(flag1==0)
        {
            Toast.makeText(ctx,result,Toast.LENGTH_LONG);
        }
        else
        {

            if(flag==1)
            {
                Log.d("yaha","sad");
                Toast.makeText(ctx,"updated",Toast.LENGTH_LONG);

            }
        }
    }
    @Override
    protected void onPreExecute() {

    }
    String get_datetime()
    {
        c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//yyyy-MM-dd
        formattedDate = df.format(c.getTime());
        return formattedDate;
    }
}
