package com.scintillato.scintillatochat;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PagerSnapHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
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

public class Verify_Phone extends ActionBarActivity {

	private EditText number;
	private Button next;
	private int flag_token;
    private Context ctx;
	private String token;
	private Button login;
    private ProgressDialog loading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verify_phone);
        ctx=this;
		getSupportActionBar().hide();
        number=(EditText)findViewById(R.id.et_verify_phone_number);
		next=(Button)findViewById(R.id.btn_verify_phone_next);
		login=(Button) findViewById(R.id.tv_verify_phone_login);
		SharedPreferences sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
		flag_token=sharedpreferences.getInt("flag_token", -1);
		token=sharedpreferences.getString("token","");
		Toast.makeText(getApplicationContext(),token+flag_token,Toast.LENGTH_LONG).show();
		next.setEnabled(false);
		next.setTextColor(getApplication().getResources().getColor(R.color.white));
		next.setBackgroundResource(R.drawable.textview_back);

		login.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Intent i=new Intent(getApplicationContext(),Login_Page.class);
				startActivity(i);
				finish();
			}
		});
		number.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length()==10){
					next.setEnabled(true);
					next.setTextColor(getApplication().getResources().getColor(R.color.black));
					next.setBackgroundResource(R.drawable.button_semi_transparent);
				}
				else{
					next.setEnabled(false);
					next.setTextColor(getApplication().getResources().getColor(R.color.white));
					next.setBackgroundResource(R.drawable.textview_back);
				}
			}
			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if (flag_token == 1) {
					String num = number.getText().toString();

					if (num.equals("")) {
						final Dialog dialog = new Dialog(Verify_Phone.this);
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

						dialog.setContentView(R.layout.verify_phone_error_dialog);
						// Set dialog title
						// set values for custom dialog components - text, image and button
						TextView dialogue_number = (TextView) dialog.findViewById(R.id.tv_verify_phone_error_dialog_1);
						dialogue_number.setText("Please enter your phone number.");

						dialog.show();
						TextView ok;
						ok = (TextView) dialog.findViewById(R.id.tv_verify_phone_error_dialog_ok);

						ok.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								dialog.cancel();
							}
						});

					} else if (num.length() > 10) {
						final Dialog dialog = new Dialog(Verify_Phone.this);
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

						dialog.setContentView(R.layout.verify_phone_error_dialog);
						// Set dialog title
						// set values for custom dialog components - text, image and button
						TextView dialogue_number = (TextView) dialog.findViewById(R.id.tv_verify_phone_error_dialog_1);
						dialogue_number.setText("The phone number you entered is too long for India.");

						dialog.show();
						TextView ok;
						ok = (TextView) dialog.findViewById(R.id.tv_verify_phone_error_dialog_ok);

						ok.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								dialog.cancel();
							}
						});

					} else if (num.length() < 10) {
						final Dialog dialog = new Dialog(Verify_Phone.this);
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

						dialog.setContentView(R.layout.verify_phone_error_dialog);
						// Set dialog title
						// set values for custom dialog components - text, image and button
						TextView dialogue_number = (TextView) dialog.findViewById(R.id.tv_verify_phone_error_dialog_1);
						dialogue_number.setText("The phone number you entered is too short for India.");

						dialog.show();
						TextView ok;
						ok = (TextView) dialog.findViewById(R.id.tv_verify_phone_error_dialog_ok);

						ok.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								dialog.cancel();
							}
						});

					} else {
						final Dialog dialog = new Dialog(Verify_Phone.this);
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

						dialog.setContentView(R.layout.verify_phone_dialogue);
						// Set dialog title
						// set values for custom dialog components - text, image and button
						TextView dialogue_number = (TextView) dialog.findViewById(R.id.tv_verify_phone_dialogue_number);
						dialogue_number.setText(number.getText().toString());

						dialog.show();
						TextView edit, ok;
						edit = (TextView) dialog.findViewById(R.id.tv_verify_phone_dialogue_edit);
						ok = (TextView) dialog.findViewById(R.id.tv_verify_phone_dialogue_ok);
						edit.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								// TODO Auto-generated method stub
								dialog.cancel();
							}
						});
						ok.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent i = new Intent(getApplicationContext(), Enter_Username.class);
								i.putExtra("number", number.getText().toString());

								startActivity(i);
							}
						});

					//	check_number("+91"+number.getText().toString()); baadme daal dunga
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(),"Somthing went wrong!",Toast.LENGTH_LONG).show();
				}
			}

				// TODO Auto-generated method stub
				
		});
		
	}

	private BackGroundTaskRegister backgroudtask;
	void check_number(String number)
	{
		backgroudtask=new BackGroundTaskRegister();
		backgroudtask.execute(number);
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

			String number=params[0];


			String register_url="http://www.scintillato.esy.es/check_mobile_no.php";


			try{
				URL url=new URL(register_url);
				HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				OutputStream OS=httpURLConnection.getOutputStream();
				BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
				String data= URLEncoder.encode("mobile_no","UTF-8")+"="+URLEncoder.encode(number,"UTF-8");

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
                    final Dialog dialog = new Dialog(Verify_Phone.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    dialog.setContentView(R.layout.verify_phone_dialogue);
                    // Set dialog title
                    // set values for custom dialog components - text, image and button
                    TextView dialogue_number = (TextView) dialog.findViewById(R.id.tv_verify_phone_dialogue_number);
                    dialogue_number.setText(number.getText().toString());

                    dialog.show();
                    TextView edit, ok;
                    edit = (TextView) dialog.findViewById(R.id.tv_verify_phone_dialogue_edit);
                    ok = (TextView) dialog.findViewById(R.id.tv_verify_phone_dialogue_ok);
                    edit.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            // TODO Auto-generated method stub
                            dialog.cancel();
                        }
                    });
                    ok.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            Intent i = new Intent(getApplicationContext(), Enter_Username.class);
                            i.putExtra("number", number.getText().toString());

                            startActivity(i);
                        }
                    });
                    //Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
				}
                else
                    Toast.makeText(ctx,"User already exists!",Toast.LENGTH_LONG).show();
			}
		}
		@Override
		protected void onPreExecute() {
            loading = ProgressDialog.show(ctx, "Status", "Checking...",true,false);
            loading.setCancelable(false);
		}
	}

	@Override
	public void onPause()
	{
		if(backgroudtask!=null)
		{
			backgroudtask.cancel(true);
			loading.cancel();
		}
		super.onPause();
	}


	@Override
	public void onStop()
	{
		if(backgroudtask!=null)
		{
			backgroudtask.cancel(true);
			loading.cancel();
		}
		super.onStop();
	}
}
