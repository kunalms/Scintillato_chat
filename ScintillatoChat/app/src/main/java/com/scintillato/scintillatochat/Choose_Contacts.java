package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class Choose_Contacts extends ActionBarActivity {

	private ListView list_contacts;
	private Choose_Contacts_Adapter adapter;
	private String one_to_one_flag,cur_number;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.choose_contacts);
		Bundle b=getIntent().getExtras();
		one_to_one_flag=b.getString("one_to_one_flag");

		SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
		cur_number = sharedpreferences.getString("number", "");
		Selected_Memebers_Execute obj=new Selected_Memebers_Execute(getApplicationContext(),cur_number);
			obj.delete_selected_members_temp();
		list_contacts=(ListView)findViewById(R.id.lv_choose_contacts_contacts);
		adapter=new Choose_Contacts_Adapter(getApplicationContext(), R.layout.choose_contacts);
		list_contacts.setAdapter(adapter);
		
		fetch_contacts();
	}
	void fetch_contacts()
	{
		Contacts_Unregistered_Execute obj=new Contacts_Unregistered_Execute(getApplicationContext(),cur_number);
		if(obj!=null)
		{
			Cursor cr=obj.getinfo_reg(obj);
			if(cr.getCount()>0) {
				cr.moveToFirst();
				do {
					Log.e("name", cr.getString(0));
					Log.e("number", cr.getString(1));
					Group_create_contacts_list group_create_list = new Group_create_contacts_list(cr.getString(0), cr.getString(1));
					adapter.add(group_create_list);

				} while (cr.moveToNext());
			}else {
				Toast.makeText(getApplicationContext(),"no contacts found",Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose__contacts, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		else if(id==R.id.btn_choose_contacts_create) {
			if (one_to_one_flag.equals("0")) {
				Intent i = new Intent(getApplicationContext(), Group_Details.class);
				startActivity(i);
			} else if (one_to_one_flag.equals("2")) {
				Intent i = new Intent(getApplicationContext(), Group_Details_Public.class);
				startActivity(i);
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
