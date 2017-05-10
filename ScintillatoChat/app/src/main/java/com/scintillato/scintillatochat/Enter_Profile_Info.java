package com.scintillato.scintillatochat;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class Enter_Profile_Info extends ActionBarActivity {


	private Uri mImageCaptureUri;
	private String number,token="0";
	private BackGroundTaskRegister backgroundregister;
	private ProgressDialog loading;
	private Button next;
    private Bitmap bitmap_image_main;
	private int flag;
	private Context ctx;
	private CircleImageView image;
	private ImageView imageView;
	private String username,password,string_profile_pic;
	private EditText name;
	private RadioGroup gender;
	private String text_gender,cur_number;
	private int flag_gender=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_profile_info);
		next=(Button)findViewById(R.id.btn_enter_profile_info_next);
		image=(CircleImageView)findViewById(R.id.iv_enter_profile_info_pic);
		imageView=(ImageView)findViewById(R.id.imageView2);
		name=(EditText)findViewById(R.id.et_enter_profile_info_name);
		gender=(RadioGroup)findViewById(R.id.radioGroup_enter_profile_gender);
		Intent i=getIntent();
		Bundle b=i.getExtras();
        bitmap_image_main = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.userprofile100);
		number=b.getString("number");
		username=b.getString("username");
		password=b.getString("password");
		number=phone_manage(number);
		ctx=this;
		gender.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// checkedId is the RadioButton selected

				flag_gender=1;
				// find the radiobutton by returned id
				RadioButton button_word = (RadioButton) findViewById(checkedId);
				//  Toast.makeText(getApplicationContext(), button_word.getText().toString(), Toast.LENGTH_SHORT).show();
				text_gender=button_word.getText().toString();
			}
		});

		image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onSelectImageClick(v);
			}
		});



		next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(name.getText().toString().equals(""))
				{
					final Dialog dialog = new Dialog(Enter_Profile_Info.this);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

					dialog.setContentView(R.layout.verify_phone_error_dialog);
					// Set dialog title
					// set values for custom dialog components - text, image and button
					TextView dialogue_number = (TextView) dialog.findViewById(R.id.tv_verify_phone_error_dialog_1);
					dialogue_number.setText("Enter Name!");

					dialog.show();
					TextView ok;
					ok=(TextView)dialog.findViewById(R.id.tv_verify_phone_error_dialog_ok);

					ok.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					});
				}
				else if(flag_gender==0){
					final Dialog dialog = new Dialog(Enter_Profile_Info.this);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

					dialog.setContentView(R.layout.verify_phone_error_dialog);
					// Set dialog title
					// set values for custom dialog components - text, image and button
					TextView dialogue_number = (TextView) dialog.findViewById(R.id.tv_verify_phone_error_dialog_1);
					dialogue_number.setText("Let us know your gender..");

					dialog.show();
					TextView ok;
					ok=(TextView)dialog.findViewById(R.id.tv_verify_phone_error_dialog_ok);

					ok.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					});
				}
				else
				{
					register();
				}
			}
		});
	}


	private String phone_manage(String phone)
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
	private String getStringImage(Bitmap bmp){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] imageBytes = baos.toByteArray();
		String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
		return encodedImage;
	}


	void register()
	{
		SharedPreferences sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
		token=sharedpreferences.getString("token","");
		Toast.makeText(getApplicationContext(),token,Toast.LENGTH_LONG).show();
		image.buildDrawingCache();
	//	Bitmap bmap = image.getDrawingCache();
		String profile_pic=getStringImage(bitmap_image_main);
		string_profile_pic=profile_pic;
		backgroundregister =new BackGroundTaskRegister();
		Log.d("profile_pic1",profile_pic);
		backgroundregister.execute(name.getText().toString(),number,token,text_gender,profile_pic,username,password);
	}
	class BackGroundTaskRegister extends AsyncTask<String, Void, String> {
		int flag1=1;
		BackGroundTaskRegister()
		{
			flag=0;
		}
		@Override
		protected String doInBackground(String... params) {

			String name=params[0];
			String number=params[1];
			String token=params[2];
			String gender=params[3];
			String profile_pic=params[4];
			String username=params[5];
			String password=params[6];

			String register_url="http://www.scintillato.esy.es/insert.php";


			try{
				URL url=new URL(register_url);
				HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				OutputStream OS=httpURLConnection.getOutputStream();
				BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
				String data=URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"+
						URLEncoder.encode("andro_id","UTF-8")+"="+URLEncoder.encode(token,"UTF-8")+"&"+
						URLEncoder.encode("profile_pic","UTF-8")+"="+URLEncoder.encode(profile_pic,"UTF-8")+"&"+
						URLEncoder.encode("nation","UTF-8")+"="+URLEncoder.encode("india","UTF-8")+"&"+
						URLEncoder.encode("mobile_no","UTF-8")+"="+URLEncoder.encode(number,"UTF-8")+"&"+
						URLEncoder.encode("gender","UTF-8")+"="+URLEncoder.encode(gender,"UTF-8")+"&"+
						URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(username,"UTF-8")+"&"+
						URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");


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
			loading.dismiss();
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

					loading.dismiss();

					SharedPreferences sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor=sharedpreferences.edit();
					editor.putString("number",number);
					editor.putInt("flag", 1);
					editor.putString("name", name.getText().toString());
					editor.putBoolean("IsFirstTimeLaunch",true);
					editor.commit();
					Calendar c = Calendar.getInstance();
					System.out.println("Current time => "+c.getTime());

					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String formattedDate = df.format(c.getTime());
					sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
					cur_number = sharedpreferences.getString("number", "");
					My_Details_Execute obj=new My_Details_Execute(getApplicationContext(),cur_number);
					obj.putinto_my_details(obj,username,name.getText().toString(),number,result,formattedDate,string_profile_pic);
					Intent i = new Intent(Enter_Profile_Info.this, Category_check.class);
					// set the new task and clear flags
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(i);
					//Toast.makeText(Create_Page_3.this,result,Toast.LENGTH_LONG).show();
				}
			}
		}
		@Override
		protected void onPreExecute() {
			loading = ProgressDialog.show(ctx, "Status", "Registering...",true,true);
			loading.setOnCancelListener(new OnCancelListener() {

				public void onCancel(DialogInterface arg0) {
					if(backgroundregister!=null)
					{
						backgroundregister.cancel(true);
						//Toast.makeText(getApplicationContext(), "stopped", Toast.LENGTH_LONG).show();
						loading.cancel();
					}


				}
			});

		}
	}

	@Override
	protected void onPause() {
		if(backgroundregister!=null)
		{
			backgroundregister.cancel(true);
			//Toast.makeText(getApplicationContext(), "stopped", Toast.LENGTH_LONG).show();
			loading.cancel();
		}



		super.onPause();
	}
	@Override
	protected void onStop() {
		if(backgroundregister!=null)
		{
			backgroundregister.cancel(true);
			//Toast.makeText(getApplicationContext(), "stopped", Toast.LENGTH_LONG).show();
			loading.cancel();
		}


		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.enter__profile__info, menu);
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
		return super.onOptionsItemSelected(item);
	}

	public void onSelectImageClick(View view) {
		CropImage.startPickImageActivity(this);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (mImageCaptureUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			// required permissions granted, start crop image activity
			startCropImageActivity(mImageCaptureUri);
		} else {
			Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// handle result of pick image chooser
		if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			Uri imageUri = CropImage.getPickImageResultUri(this, data);

			// For API >= 23 we need to check specifically that we have permissions to read external storage.
			if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
				// request permissions and handle the result in onRequestPermissionsResult()
				mImageCaptureUri = imageUri;
				requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
			} else {
				// no permissions required or already grunted, can start crop image activity
				startCropImageActivity(imageUri);
			}
		}

		// handle result of CropImageActivity
		if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
			CropImage.ActivityResult result = CropImage.getActivityResult(data);
			if (resultCode == RESULT_OK) {
				((CircleImageView) findViewById(R.id.iv_enter_profile_info_pic)).setImageURI(result.getUri());
				imageView.setVisibility(View.INVISIBLE);
				Uri imageUri = result.getUri();
				try {
					bitmap_image_main = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
			} else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
				Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
			}
		}
	}


	private void startCropImageActivity(Uri imageUri) {
		CropImage.activity(imageUri)
				.setGuidelines(CropImageView.Guidelines.ON)
				.setMultiTouchEnabled(true).setAspectRatio(1,1)
				.start(this);
	}
}
