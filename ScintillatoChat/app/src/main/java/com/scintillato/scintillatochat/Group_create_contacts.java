package com.scintillato.scintillatochat;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Group_create_contacts extends ActionBarActivity {

	private ProgressBar spinner;
	private ListView contacts_list;
	private String one_to_one_flag;
	private Context ctx;
	private ProgressDialog loading;
	private int flag,contacts_update;
	private String contact_json;
	private Contacts_Unregistered_Execute obj1;
	private Button skip;
	private List<String> number_list,name_list;
	private List<contacts_list> contacts_distinct,c_reg,c_not_reg,contacts_refreshed;
	private Group_create_contacts_adapter adapter;
	private BackGroundTaskFetch backgroundfetch;
	private String cur_number;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_create_contacts);
		SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
		cur_number = sharedpreferences.getString("number", "");
		Intent intent_extra=getIntent();
		Bundle b=intent_extra.getExtras();
		one_to_one_flag=b.getString("one_to_one_flag");
		spinner=(ProgressBar)findViewById(R.id.progress_bar_group_create_contacts_1);
		spinner.setVisibility(View.GONE);
		contacts_list=(ListView)findViewById(R.id.lv_group_create_contacts);
		adapter=new Group_create_contacts_adapter(getApplicationContext(), R.layout.activity_group_create_contacts_row);
		contacts_list.setAdapter(adapter);
		skip=(Button)findViewById(R.id.btn_activity_group_create_contacts_skip);
		ctx=this;
		
		
		spinner.setVisibility(View.VISIBLE);
		number_list=new ArrayList<String>();
		name_list=new ArrayList<String>();
		contacts_distinct=new ArrayList<contacts_list>();
        contacts_refreshed=new ArrayList<contacts_list>();

		sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
 		contacts_update=sharedpreferences.getInt("contacts_update", -1);
 		Toast.makeText(ctx, contacts_update+"", Toast.LENGTH_LONG).show();
 		if(contacts_update==1)
 		{
 			obj1=new Contacts_Unregistered_Execute(ctx,cur_number);
 			if(obj1!=null) {
				Cursor cr = obj1.getinfo_unreg(obj1);
				if (cr.getCount() > 0) {
					cr.moveToFirst();
					do {
						//		Log.e("name",cr.getString(0));
						//		Log.e("number",cr.getString(1));
						Group_create_contacts_list group_create_list = new Group_create_contacts_list(cr.getString(0), cr.getString(1));
						adapter.add(group_create_list);

					} while (cr.moveToNext());
				}
				else
				{
					Intent i=new Intent(getApplicationContext(),Choose_Contacts.class);
					startActivity(i);
					finish();
					overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

				}
			}
 		}
 		else
 		{
 			try {
 				fetchContacts();
 			} catch (JSONException e) {
			// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
 		}
		spinner.setVisibility(View.GONE);
		skip.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

					if(one_to_one_flag.equals("0")) {
						Intent i = new Intent(getApplicationContext(), Choose_Contacts.class);
						i.putExtra("one_to_one_flag", one_to_one_flag);
						startActivity(i);
						overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

					}
					else if(one_to_one_flag.equals("2"))
					{
						Intent i = new Intent(getApplicationContext(), Choose_Contacts.class);
						i.putExtra("one_to_one_flag", one_to_one_flag);
						startActivity(i);
						overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);


					}
					else
					{
						Intent i=new Intent(getApplicationContext(),Choose_Contacts_Single.class);
						startActivity(i);
						overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

					}


			}
		});
}


	public void refresh_contacts() {



		String phoneNumber = null;
		String email = null;
		name_list.clear();
		number_list.clear();
        contacts_refreshed.clear();
		contacts_distinct.clear();
		contact_json=null;
		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

		Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
		String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

		Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		String DATA = ContactsContract.CommonDataKinds.Email.DATA;


		ContentResolver contentResolver = getContentResolver();

		Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

		if (cursor.getCount() > 0) {

			while (cursor.moveToNext()) {

				String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
				String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

				int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

				if (hasPhoneNumber > 0) {

					//output.append("\n First Name:" + name);

					// Query and loop for every phone number of the contact
					Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

					while (phoneCursor.moveToNext()) {
						phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
						//output.append("\n Phone number:" + phoneNumber);

                        phoneNumber = phone_manage(phoneNumber);
                        number_list.add(phoneNumber);
                        name_list.add(name);
                        contacts_list temp = new contacts_list(name, phoneNumber);
						int flag = temp.check(contacts_distinct, temp);
						if (flag == 0) {
							//				Log.e("dist",temp.name+temp.number);
							contacts_distinct.add(temp);

						}
						//		Log.e("name,number",name_list.get(name_list.size()-1)+" "+number_list.get(number_list.size()-1));
					}

					phoneCursor.close();

					// Query and loop for every email of the contact
					Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);

					while (emailCursor.moveToNext()) {

						email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
					}

					emailCursor.close();
				}


			}

			JSONArray jsonArray = new JSONArray();
			manage_number(contacts_distinct);

			for (int i = 0; i < contacts_distinct.size(); i++) {

				if(check_already_registered_contacts(contacts_distinct.get(i).number)==0){
				JSONObject object = new JSONObject();
				try {
                    contacts_list temp = new contacts_list(contacts_distinct.get(i).name, contacts_distinct.get(i).number);
                    contacts_refreshed.add(temp);
                    Log.d("contacts",contacts_distinct.get(i).number);

                    object.put("number", contacts_distinct.get(i).number);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				jsonArray.put(object);
			}
			}
			contact_json = jsonArray.toString();
			Log.d("contacts_json",contact_json);

            contact_fetch_server();


		}
	}
	public int check_already_registered_contacts(String number)
	{
		Contacts_Unregistered_Execute obj = new Contacts_Unregistered_Execute(getApplicationContext(),cur_number);
		if (obj != null) {
			Cursor cr = obj.getinfo_reg(obj);

			if(cr.getCount()>0) {
				cr.moveToFirst();
				do {

					if (cr.getString(1).equals(number)) {
						return 1;
					}

				} while (cr.moveToNext());
			}
		}
		return 0;
	}
	public void fetchContacts() throws JSONException {

		String phoneNumber = null;
		String email = null;

		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

		Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
		String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

		Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		String DATA = ContactsContract.CommonDataKinds.Email.DATA;

        number_list.clear();
        name_list.clear();

		ContentResolver contentResolver = getContentResolver();

		Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

		if (cursor.getCount() > 0) {

			while (cursor.moveToNext()) {

				String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
				String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

				int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

				if (hasPhoneNumber > 0) {

					//output.append("\n First Name:" + name);

					// Query and loop for every phone number of the contact
					Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);

					while (phoneCursor.moveToNext()) {
						phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
						//output.append("\n Phone number:" + phoneNumber);
						phoneNumber=phone_manage(phoneNumber);
                        number_list.add(phoneNumber);
                        name_list.add(name);

                        contacts_list temp=new contacts_list(name, phoneNumber);
						int flag=temp.check(contacts_distinct,temp);
						if(flag==0)
						{
			//				Log.e("dist",temp.name+temp.number);
							contacts_distinct.add(temp);


						}
				//		Log.e("name,number",name_list.get(name_list.size()-1)+" "+number_list.get(number_list.size()-1));
					}

					phoneCursor.close();

					// Query and loop for every email of the contact
					Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,	null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);

					while (emailCursor.moveToNext()) {

						email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
					}

					emailCursor.close();
				}


			}

			JSONArray jsonArray=new JSONArray();
			manage_number(contacts_distinct);

			for(int i=0;i<contacts_distinct.size();i++)
			{
				JSONObject object=new JSONObject();
				object.put("number", contacts_distinct.get(i).number);
				jsonArray.put(object);
			}
	//		JSONObject number_object=new JSONObject();
	//		number_object.put("result",jsonArray);
			contact_json=jsonArray.toString();
		//		Log.d("json",contact_json+"");
			contact_fetch_server();

		}
	}

	String phone_manage(String phone)
	{
		phone=phone.replaceAll("\\s","");
		if(phone.startsWith("0"))
		{
			String tem=phone.substring(1);
			tem="+91"+tem;
			phone=tem;
		}
		else if(phone.startsWith("+"))
		{
			
		}
		else
		{
			String tem=phone;
			tem="+91"+tem;
			phone=tem;
		}
		return phone;
	}
	void manage_number(List<contacts_list> list)
	{
		for(int i=0;i<list.size();i++)
		{
			list.get(i).number=list.get(i).number.replaceAll("\\s","");
			if(list.get(i).number.startsWith("0"))
			{
				String tem=list.get(i).number.substring(1);
				tem="+91"+tem;
				list.get(i).number=tem;
			}
			else if(list.get(i).number.startsWith("+"))
			{
				
			}
			else
			{
				String tem=list.get(i).number;
				tem="+91"+tem;
				list.get(i).number=tem;
			}
		//	Log.e("numbers",list.get(i).number+" "+list.get(i).name);
			
		}
	}
	void contact_fetch_server()
	{
		backgroundfetch=new BackGroundTaskFetch();
	//	Toast.makeText(ctx, contact_json, Toast.LENGTH_LONG).show();
		backgroundfetch.execute(contact_json);
	}

    BackGroundRefresh backGroundRefresh;
    void refresh_contacts1()
    {
        backGroundRefresh=new BackGroundRefresh();
        backGroundRefresh.execute();
    }
    class BackGroundRefresh extends  AsyncTask<String,Void,String>
    {
        int flag1=1,flag;
        BackGroundRefresh()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params)
        {
            try {
                refresh_contacts();
                flag = 1;
                return "1";
            }
            catch (Exception e)
            {
                flag1=0;
                return "Something went wrong!";
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            if(flag1==0)
            {
                Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
            }
            else {
                if(flag==1)
                {
                    contact_fetch_server();
                }
            }

        }
        @Override
        protected void onPreExecute()
        {
            loading = ProgressDialog.show(ctx, "Status", "Refreshing Contacts...",true,false);
        }
    }
	class BackGroundTaskFetch extends AsyncTask<String, Void, String> 
	{
		int flag1=1;
		BackGroundTaskFetch()
		{
			flag=0;
		}
		@Override
		protected String doInBackground(String... params) {
        
			String json=params[0];

		
			String register_url="http://scintillato.esy.es/contacts.php";
        
        	
			try{
				URL url=new URL(register_url);
				HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				OutputStream OS=httpURLConnection.getOutputStream();
				BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
				String data=URLEncoder.encode("json","UTF-8")+"="+URLEncoder.encode(json,"UTF-8");

        	
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

    	if(line.equals(""))
    	{
    		flag=0;
    	}
    	else
    	{
    		flag=1;
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
    	Log.e("contacts_json111",result+"");
	//	Toast.makeText(ctx,flag+"",Toast.LENGTH_LONG).show();	
    	
    	if(flag1==0)
    	{
    		Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();	
    	}
    	else
    	{
        
        if(flag==1)
        {
        	
        	loading.dismiss();
        	showlist(result);
	    }
        else
        {
        	Toast.makeText(ctx, "Failure Occured!", Toast.LENGTH_SHORT).show();
        }
    	}
    }
    @Override
    protected void onPreExecute() {

        loading = ProgressDialog.show(ctx, "Status", "Loading Contacts...",true,false);

        loading.setOnCancelListener(new OnCancelListener() {

            public void onCancel(DialogInterface arg0) {
            	if(backgroundfetch!=null)
        		{
            		backgroundfetch.cancel(true);
        			//Toast.makeText(getApplicationContext(), "stopped", Toast.LENGTH_LONG).show();
        			loading.cancel();
        		}
        		

            }
        });
        
    }
	}
	private JSONObject jsonObject;
	private JSONArray jsonArray;
	private int count;
	public void showlist(String myJSON) 
	{
		obj1=new Contacts_Unregistered_Execute(ctx,cur_number);
		//obj1.delete_reg();
		//obj1.delete_unreg();
		c_not_reg=new ArrayList<contacts_list>();
		c_reg=new ArrayList<contacts_list>();
		try{
			SharedPreferences sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
    		SharedPreferences.Editor editor=sharedpreferences.edit();
    		editor.putInt("contacts_update", 1);
    		editor.commit();
    		
			jsonObject=new JSONObject(myJSON);
			jsonArray=jsonObject.getJSONArray("result");
			SharedPreferences sharedpreferences1=getSharedPreferences("User", Context.MODE_PRIVATE);
			String cur_number=sharedpreferences1.getString("number","");
			count=0;
			String status;
		//	Log.d("length", jsonArray.length()+"");
            adapter=new Group_create_contacts_adapter(getApplicationContext(), R.layout.activity_group_create_contacts_row);
            contacts_list.setAdapter(adapter);

            Cursor c=obj1.getinfo_reg(obj1);
            if(c.getCount()>0)
            {
                c.moveToFirst();
                do {
                    if(number_list.contains(c.getString(1)))
                    {
                        Log.d("match1",c.getString(1));
                        int index=number_list.indexOf(c.getString(1));
                        obj1.update_number_registered(obj1,c.getString(1),name_list.get(index));
                    }
                    else
                    {
                        Log.d("match1",c.getString(1)+" delete number");
                        obj1.delete_registered_number(c.getString(1));

                    }
                }while (c.moveToNext());
            }

            while(count<jsonArray.length())
			{
				JSONObject JO=jsonArray.getJSONObject(count);
				status=JO.getString("status");
				if(status.equals("1"))
				{

                    if(contacts_refreshed.get(count).number.equals(cur_number)==false)
					{
                        obj1.putinfo_reg(obj1, contacts_refreshed.get(count).name, contacts_refreshed.get(count).number);

					//	c_reg.add(contacts_distinct.get(count));
                        obj1.delete_unregistered(contacts_refreshed.get(count).number);
                    }
				}
				else
				{
					
						Group_create_contacts_list contact_list=new Group_create_contacts_list(contacts_refreshed.get(count).name, contacts_refreshed.get(count).number);
						adapter.add(contact_list);
					    obj1.putinfo_unreg(obj1, contacts_refreshed.get(count).name, contacts_refreshed.get(count).number);
					//c_not_reg.add(contacts_distinct.get(count));
				}


				count++;
			}
			


		}
		catch(Exception e)
		{
			
		}
	}

	@Override
	protected void onPause() {
		if(backgroundfetch!=null)
		{
			backgroundfetch.cancel(true);
			//Toast.makeText(getApplicationContext(), "stopped", Toast.LENGTH_LONG).show();
			loading.cancel();
		}
		super.onPause();
	}
	@Override
	protected void onStop() {
		if(backgroundfetch!=null)
		{
			backgroundfetch.cancel(true);
			//Toast.makeText(getApplicationContext(), "stopped", Toast.LENGTH_LONG).show();
			loading.cancel();
		}


		super.onStop();
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_create_contacts_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
               refresh_contacts1();
                break;

        }
        return true;
    }
}
