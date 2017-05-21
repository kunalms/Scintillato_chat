package com.scintillato.scintillatochat;


import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
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

public class Start_Page extends TabActivity {


	private String cur_number;
    private Cursor c;
   // private BackGroundTaskSingle backGroundTaskSingle;
    //private BackGroundTaskGroup backGroundTaskGroup;
    private Context ctx;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_page);
        ctx=this;
		SharedPreferences sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
		cur_number=sharedpreferences.getString("number", "");
		My_Details_Execute obj=new My_Details_Execute(getApplicationContext(),cur_number);
		Cursor c=obj.get_my_details(obj);
		if(c.getCount()>0)
		{
			c.moveToFirst();
			do {

				Toast.makeText(getApplicationContext(),c.getString(0),Toast.LENGTH_LONG).show();
			}while (c.moveToNext());
		}
		/** TabHost will have Tabs */
		TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);

		/** TabSpec used to create a new tab.
		 * By using TabSpec only we can able to setContent to the tab.
		 * By using TabSpec setIndicator() we can set name to tab. */

		/** tid1 is firstTabSpec Id. Its used to access outside. */
		TabSpec firstTabSpec = tabHost.newTabSpec("tid_home_1");
		TabSpec secondTabSpec = tabHost.newTabSpec("tid_group_2");
		TabSpec thirdTabSpec = tabHost.newTabSpec("tid_search_3");
		TabSpec fourthTabSpec = tabHost.newTabSpec("tid_profile_4");

		TabSpec fifthTabSpec = tabHost.newTabSpec("tid_profile_5");
/** TabSpec setIndicator() is used to set name for the tab. */
		/** TabSpec setContent() is used to set content for a particular tab. */
		firstTabSpec.setIndicator("",getResources().getDrawable(R.drawable.tab_1)).setContent(new Intent(this,Chat_Page.class));
		secondTabSpec.setIndicator("",getResources().getDrawable(R.drawable.tab_2)).setContent(new Intent(this,Feed_Recycler.class));
		thirdTabSpec.setIndicator("",getResources().getDrawable(R.drawable.tab_3)).setContent(new Intent(this,Follow_Notification.class));
		fourthTabSpec.setIndicator("",getResources().getDrawable(R.drawable.tab_4)).setContent(new Intent(this,Self_Profile.class));
		fifthTabSpec.setIndicator("",getResources().getDrawable(R.drawable.tab_1)).setContent(new Intent(this,Trending_Groups.class));
		/** Add tabSpec to the TabHost to display. */

		tabHost.addTab(firstTabSpec);
		tabHost.addTab(secondTabSpec);
		tabHost.addTab(thirdTabSpec);
		tabHost.addTab(fourthTabSpec);
		tabHost.addTab(fifthTabSpec);
		TabWidget widget = tabHost.getTabWidget();
		for(int i = 0; i < widget.getChildCount(); i++) {
			View v = widget.getChildAt(i);
			v.setBackgroundResource(R.drawable.tab_selector);
		}

		if(isNetworkAvailable()==true)
		{
		}
		else
		{
			Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
		}
        send_message_pending();

        put_status(cur_number,"1");
	}

    void send_message_pending()
    {
        Chat_Database_Execute obj=new Chat_Database_Execute(ctx,cur_number);
        c=obj.fetch_message_unsend_single(obj);
        c.moveToFirst();
        if(c.getCount()>0) {
            do {
                Cursor c1 = obj.fetch_message_by_id(obj, c.getString(0));
                c1.moveToFirst();
                if (c1.getCount() > 0) {
                    do {
                        send_single_message(c.getString(1),c1.getString(5),cur_number,c.getString(0));
                    }while(c1.moveToNext());
                }
                // send_single_message();
            }while (c.moveToNext());
        }

        obj=new Chat_Database_Execute(ctx,cur_number);
        c=obj.fetch_message_unsend_group(obj);
        c.moveToFirst();
        if(c.getCount()>0)
        {
            do {
                send_group_message(c.getString(1),cur_number,c.getString(2),c.getString(0));
            }while (c.moveToNext());
        }

    }
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

    BackGroundTaskSend backGroundTaskSend;
    void send_single_message(String message,String number,String sender,String message_id)
    {
        backGroundTaskSend=new BackGroundTaskSend(message_id);
        backGroundTaskSend.execute(message,number,sender,message_id);

    }
	class BackGroundTaskSend extends AsyncTask<String, Void, String> {
		int flag;
		int flag1=1;
		String message_id;
		BackGroundTaskSend(String message_id)
		{
			this.message_id=message_id;
			flag=0;
		}
		@Override
		protected String doInBackground(String... params) {

			String message=params[0];
			String number=params[1];
			String sender=params[2];
			String message_id=params[3];

			String register_url="http://www.scintillato.esy.es/message_send_single.php";


			try{
				URL url=new URL(register_url);
				HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				OutputStream OS=httpURLConnection.getOutputStream();
				BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
				String data= URLEncoder.encode("message","UTF-8")+"="+URLEncoder.encode(message,"UTF-8")+"&"+
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
					update_message_status(message_id,"1");
					Chat_Database_Execute obj=new Chat_Database_Execute(ctx,cur_number);
					obj.delete_message_unsend_single_selected(message_id);
                    Toast.makeText(ctx, "sent"+message_id, Toast.LENGTH_SHORT).show();
                }
			}
		}
		@Override
		protected void onPreExecute() {

		}
	}


	void update_message_status(String message_id,String status){
		Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
		obj.update_status_message_single(obj,message_id,status);
	}
    private BackGroundTaskInsertUnique BackGroundTaskInsertUnique;
    void put_status(String user_number,String status)
    {
        if(BackGroundTaskInsertUnique!=null)
            BackGroundTaskInsertUnique.cancel(true);
        BackGroundTaskInsertUnique=new BackGroundTaskInsertUnique(getApplicationContext());
        BackGroundTaskInsertUnique.execute(user_number,status);
    }

    /*
	class BackGroundTaskSingle extends AsyncTask<String, Void, String> {

        int flag1=1,flag;
        String flag_type;
        String message_id;
        String message,number,sender;
        BackGroundTaskSingle(String message_id,String flag_type)
        {
            this.flag_type=flag_type;
            this.message_id=message_id;
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String message=params[0];
            String number=params[1];
            String sender=params[2];
            this.message=message;
            this.number=number;
            this.sender=sender;
            String register_url="http://www.scintillato.esy.es/message_send_single.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setConnectTimeout(15000);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("message","UTF-8")+"="+URLEncoder.encode(message,"UTF-8")+"&"+
                        URLEncoder.encode("r_mobile_no","UTF-8")+"="+URLEncoder.encode(number,"UTF-8")+"&"+
                        URLEncoder.encode("s_mobile_no","UTF-8")+"="+URLEncoder.encode(sender,"UTF-8");

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
                send_single_message(message,number,sender,message_id);
                Toast.makeText(ctx,result,Toast.LENGTH_LONG);
            }
            else
            {

                if(flag==1)
                {
                    update_message_status(message_id,"1");
                    Group_Execute obj=new Group_Execute(ctx,cur_number);

                    boolean a=obj.deletemessage_unsend(message_id);
                    if(a==true)
                    {
                        Toast.makeText(getApplicationContext(),message_id+" deleted",Toast.LENGTH_SHORT).show();
                    }
                    if(c.moveToNext())
                    {
                        String flag=c.getString(4);
                        if(flag.equals("0"))
                        {

                            send_single_message(c.getString(3),c.getString(2),cur_number,c.getString(0));
                        }
                        else
                        {
                            send_group_message(c.getString(3),cur_number,c.getString(1),c.getString(0));
                        }
                    }
                   // Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
                }
                else
                {
                    send_single_message(message,number,sender,message_id);

                }
            }
        }
        @Override
        protected void onPreExecute() {

        }
    }

    void update_message_status(String message_id,String status){
/*        Group_Execute obj=new Group_Execute(getApplicationContext(),cur_number);
        obj.update_messasge_status(obj,message_id,status);
        Toast.makeText(getApplicationContext(),"updated"+message_id,Toast.LENGTH_LONG).show();*/
    /*
    }
    class BackGroundTaskGroup extends AsyncTask<String, Void, String> {
        int flag1=1,flag;

        String message_id,flag_type,message,sender,group_id;
        BackGroundTaskGroup(String message_id,String flag_type)
        {
            this.flag_type=flag_type;
            this.message_id=message_id;
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String message=params[0];
            String sender=params[1];
            String group_id=params[2];
            this.message=message;
            this.sender=sender;
            this.group_id=group_id;


            String register_url="http://www.scintillato.esy.es/message_send_group.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setConnectTimeout(15000);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("message","UTF-8")+"="+URLEncoder.encode(message,"UTF-8")/*+"&"+
                        URLEncoder.encode("r_mobile_no","UTF-8")+"="+URLEncoder.encode(number_json,"UTF-8")*/
    /*+"&"+
                        URLEncoder.encode("s_mobile_no","UTF-8")+"="+URLEncoder.encode(sender,"UTF-8")+"&"+
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
                send_group_message(message,sender,group_id,message_id);
            }
            else
            {


                if(flag==1)
                {

                    //  save_message();
                    //}while (cr.moveToNext());
                    //}
                    update_message_status(message_id,"1");
                    Group_Execute obj=new Group_Execute(ctx,cur_number);

                    boolean a=obj.deletemessage_unsend(message_id);
                    if(a==true)
                    {
                        Toast.makeText(ctx,message_id+" deleted",Toast.LENGTH_SHORT).show();
                    }
                    if(c.moveToNext())
                    {
                        String flag=c.getString(4);
                        if(flag.equals("0"))
                        {

                            send_single_message(c.getString(3),c.getString(2),cur_number,c.getString(0));
                        }
                        else
                        {
                            send_group_message(c.getString(3),cur_number,c.getString(1),c.getString(0));
                        }
                    }
                    Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
                }
                else
                {
                    send_group_message(message,sender,group_id,message_id);
                }
            }
        }
        @Override
        protected void onPreExecute() {

        }
    }
   */


    private BackGroundTaskSendGroup backgroudsendgroup;
    void send_group_message(String message,String sender,String group_id,String message_id)
    {

        backgroudsendgroup=new BackGroundTaskSendGroup(message_id);
        backgroudsendgroup.execute(message,sender,group_id);
    }

    int flag;
    class BackGroundTaskSendGroup extends AsyncTask<String, Void, String> {
        int flag1=1;
        String message_id;
        BackGroundTaskSendGroup(String message_id)
        {
            this.message_id=message_id;
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String message=params[0];
            //String number_json=params[1];
            String sender=params[1];
            String group_id=params[2];


            String register_url="http://www.scintillato.esy.es/message_send_group.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("message","UTF-8")+"="+URLEncoder.encode(message,"UTF-8")+"&"+
                        URLEncoder.encode("s_mobile_no","UTF-8")+"="+URLEncoder.encode(sender,"UTF-8")+"&"+
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

                if(line.equals("")==true)
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
                    update_message_status_group(message_id,"1");
                    Chat_Database_Execute obj=new Chat_Database_Execute(ctx,cur_number);
                    obj.delete_message_unsend_group_selected(message_id);
                }
            }
        }
        @Override
        protected void onPreExecute() {

        }
    }

    void update_message_status_group(String message_id,String status){
        Chat_Database_Execute obj=new Chat_Database_Execute(ctx,cur_number);
        obj.update_status_message_group(obj,message_id,status);
    }

    @Override
public void onBackPressed() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    put_status(cur_number,"0");
                    Start_Page.this.finish();
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
    AlertDialog alert = builder.create();
    alert.show();
    alert.getButton(alert.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
    alert.getButton(alert.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
}

}

