package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.SharedPreferences;
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

/**
 * Created by VIVEK on 18-03-2017.
 */
class BackGroundTaskSentReceipt extends AsyncTask<String, Void, String> {
    int flag;
    int flag1=1;
    String message_id,cur_number;
    Context ctx;
    String status="0";

    BackGroundTaskSentReceipt(String message_id, Context ctx)
    {
        this.message_id=message_id;
        flag=0;
        this.ctx=ctx;
        SharedPreferences sharedpreferences=ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number=sharedpreferences.getString("number", "");

    }
    @Override
    protected String doInBackground(String... params) {

        String receive_seen=params[0];//if 0:receive, 1:seen
        String number=params[1];
        String sender=params[2];
        String message_id=params[3];
        this.status=receive_seen;

        String register_url="http://www.scintillato.esy.es/message_send_single_received_seen.php";


        try{
            URL url=new URL(register_url);
            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream OS=httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
            String data= URLEncoder.encode("receive_seen","UTF-8")+"="+URLEncoder.encode(receive_seen,"UTF-8")+"&"+
                    URLEncoder.encode("r_mobile_no","UTF-8")+"="+URLEncoder.encode(number,"UTF-8")+"&"+
                    URLEncoder.encode("s_mobile_no","UTF-8")+"="+URLEncoder.encode(sender,"UTF-8")+"&"+
                    URLEncoder.encode("message_id","UTF-8")+"="+URLEncoder.encode(message_id,"UTF-8");
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
        // loading.dismiss();
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
                update_message_status(message_id,status);

                Chat_Database_Execute obj=new Chat_Database_Execute(ctx,cur_number);
                obj.delete_message_unsend_single_selected(message_id);

            }
        }
    }
    @Override
    protected void onPreExecute() {

    }
    void update_message_status(String message_id,String status){
        Chat_Database_Execute obj=new Chat_Database_Execute(ctx,cur_number);
        obj.update_status_message_single(obj,message_id,status);
    }
}

