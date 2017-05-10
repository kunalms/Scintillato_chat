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
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Group_create_contacts_adapter extends ArrayAdapter{

	private List<Group_create_contacts_list> list=new ArrayList<Group_create_contacts_list>();
	Context ctx;
	public Group_create_contacts_adapter(Context context, int resource) {
		super(context, resource);
		ctx=context;
		// TODO Auto-generated constructor stub
	}

	public void add(Group_create_contacts_list object)
	{
		super.add(object);
		list.add(object);
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override 
	public Object getItem(int position)
	{
		return list.get(position);
	}

	@Override 
	public View getView(int position, View convertView,ViewGroup parent)
	{
		View row;
		row=convertView;
		 final Group_create_contacts_holder group_create_holder;
		if(row==null)
		{
			LayoutInflater layoutinflator=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row=layoutinflator.inflate(R.layout.activity_group_create_contacts_row,parent,false );
			group_create_holder= new Group_create_contacts_holder();
			group_create_holder.name=(TextView)row.findViewById(R.id.tv_group_create_contacts_row_name);
			group_create_holder.number=(TextView)row.findViewById(R.id.tv_group_create_contacts_row_number);
			group_create_holder.invite=(TextView)row.findViewById(R.id.tv_group_create_contacts_row_invite);
			row.setTag(group_create_holder);
		}
		else
		{
			group_create_holder=(Group_create_contacts_holder)row.getTag();
		}
	
		Group_create_contacts_list issue_1_list=(Group_create_contacts_list)this.getItem(position);
		group_create_holder.name.setText(issue_1_list.get_name());	
		group_create_holder.number.setText(issue_1_list.get_number());	
		
		return row;
	}
  int flag;
	
	static class Group_create_contacts_holder
	{
		TextView name,number,invite;

	}
}
