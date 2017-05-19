package com.scintillato.scintillatochat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;


public class Group_Details_Public extends ActionBarActivity {

    private EditText group_name,group_title,group_description;
    private CircleImageView group_pic;
    private ListView members;
    private String jsonString_members,cur_number;
    private int member_count;
    private Context ctx;
    private Uri mImageCaptureUri;
    private Bitmap bitmap_main;
    private int flag_image;
    private Cursor cursor_members;
    private ProgressDialog loading;
    private Group_Details_Adapter adapter;
    private Selected_Memebers_Execute obj;
    private BackGroundTaskRegister backGroundTaskRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_details);
        ctx=this;

        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        members=(ListView)findViewById(R.id.lv_group_details_members);
        group_name=(EditText)findViewById(R.id.et_group_details_name);
        group_title=(EditText)findViewById(R.id.et_group_details_topic);
        group_description=(EditText)findViewById(R.id.et_group_details_description);
        group_pic=(CircleImageView)findViewById(R.id.iv_group_details_image);
        adapter=new Group_Details_Adapter(getApplicationContext(), R.layout.group_details_row);
        members.setAdapter(adapter);

        bitmap_main = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.groupprofile100);
        group_pic.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {
                onSelectImageClick(v);
            }
        });
        fetch_members();
    }

    void fetch_members()
    {
        JSONObject object;
        JSONArray jsonArray=new JSONArray();
        obj=new Selected_Memebers_Execute(getApplicationContext(),cur_number);
        cursor_members=obj.getinfo_selected_members_temp(obj);
        member_count=cursor_members.getCount();
        if(obj!=null)
        {
            int rank=2;
            if(cursor_members.getCount()>0)
            {
                SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
                String number = sharedpreferences.getString("number", "");
                cursor_members.moveToFirst();
                do{

                    object=new JSONObject();
                    try {
                        object.put("number", cursor_members.getString(0));
                        object.put("rank",rank++);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.put(object);
                    Group_create_contacts_list list=new Group_create_contacts_list(cursor_members.getString(1), cursor_members.getString(0));
                    adapter.add(list);
                }while(cursor_members.moveToNext());

                object=new JSONObject();
                try {
                    object.put("number", number);
                    object.put("rank",1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(object);
                jsonString_members=jsonArray.toString();
                Log.d("membersjson",jsonString_members);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Add atleast one member",Toast.LENGTH_LONG).show();
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.group__details, menu);
        return true;
    }


    private String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    String get_datetime()
    {
        java.util.Calendar c;
        java.text.SimpleDateFormat df;
        c = java.util.Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//yyyy-MM-dd
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    void private_group_create(String public_id)
    {

        //  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");;
        //String currentDateandTime = sdf.format(new Date());
        String formattedDate=get_datetime();
        Chat_Database_Execute obj_chat=new Chat_Database_Execute(getApplicationContext(),cur_number);
        obj_chat.insert_groups(obj_chat,group_name.getText().toString(),group_title.getText().toString(),group_description.getText().toString(),formattedDate,member_count+1+"","1",public_id);
        obj=new Selected_Memebers_Execute(getApplicationContext(),cur_number);
        cursor_members=obj.getinfo_selected_members_temp(obj);
        int rank=1;
        obj_chat.putinfo_group_members(obj_chat,public_id,cur_number,"1",(rank++)+"",formattedDate);

        if (cursor_members.getCount() > 0) {
            cursor_members.moveToFirst();
            do {
                obj_chat.putinfo_group_members(obj_chat,public_id,cursor_members.getString(0),"0",(rank++)+"",formattedDate);
            } while (cursor_members.moveToNext());
        }

        obj_chat.insert_recent_chats(obj_chat,"0",public_id,"0",cur_number,formattedDate);

        Cursor c=obj_chat.fetch_group_members(obj_chat,public_id);
        if(c.getCount()>0)
        {
            c.moveToFirst();
            do {

            }while (c.moveToNext());
        }
        obj.delete_selected_members_temp();


        loading.cancel();
        Intent i = new Intent(getApplicationContext(), Start_Page.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(i);
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
        else if(id==R.id.btn_group_details_create)
        {
            public_group_create(0);
        }


        return super.onOptionsItemSelected(item);
    }

    void public_group_create(int x)
    {
        String group_pic_string;
        group_pic_string=getStringImage(bitmap_main);
        //  if(x==0) {
        //    if (flag_image != 1) {
        // backGroundTaskRegister = new BackGroundTaskRegister();
        // backGroundTaskRegister.execute(group_name.getText().toString(), group_title.getText().toString(), group_description.getText().toString(), member_count + 1 + "", "1", x + "", number, jsonString_members);
        //} else {
        backGroundTaskRegister = new BackGroundTaskRegister();
        backGroundTaskRegister.execute(group_name.getText().toString(), group_title.getText().toString(), group_description.getText().toString(), member_count + 1 + "", group_pic_string, "0", cur_number, jsonString_members);

        // }
        //}
        //else
        //{
        //group_insert_public.php
        //}
    }
    class BackGroundTaskRegister extends AsyncTask<String, Void, String> {
        int flag1=1;
        int flag;
        BackGroundTaskRegister()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String group_name=params[0];
            String group_topic=params[1];
            String group_description=params[2];
            String group_count=params[3];
            String group_image=params[4];
            String status=params[5];
            String admin=params[6];
            String members=params[7];
            Log.d("mmemem",members);
            String register_url="http://www.scintillato.esy.es/group_insert.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("group_name","UTF-8")+"="+URLEncoder.encode(group_name,"UTF-8")+"&"+
                        URLEncoder.encode("group_topic","UTF-8")+"="+URLEncoder.encode(group_topic,"UTF-8")+"&"+
                        URLEncoder.encode("group_description","UTF-8")+"="+URLEncoder.encode(group_description,"UTF-8")+"&"+
                        URLEncoder.encode("group_count","UTF-8")+"="+URLEncoder.encode(group_count,"UTF-8")+"&"+
                        URLEncoder.encode("group_image","UTF-8")+"="+URLEncoder.encode(group_image,"UTF-8")+"&"+
                        URLEncoder.encode("status","UTF-8")+"="+URLEncoder.encode(status,"UTF-8")+"&"+
                        URLEncoder.encode("admin","UTF-8")+"="+URLEncoder.encode(admin,"UTF-8")+"&"+
                        URLEncoder.encode("members","UTF-8")+"="+URLEncoder.encode(members,"UTF-8");

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
                    //String group_public_id;
                    //group_public_id=showlist(result);
                    private_group_create(result);


                    Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(ctx,"Somethig went wrong!!",Toast.LENGTH_LONG).show();

                }
            }
        }
        @Override
        protected void onPreExecute() {
            loading = ProgressDialog.show(ctx, "Status", "Logging In...",true,false);
            loading.setCancelable(false);
        }
    }
    @Override
    protected void onPause() {
        if(backGroundTaskRegister!=null)
        {
            backGroundTaskRegister.cancel(true);
        }
        super.onPause();
    }


    private void storeImage(Bitmap image,String group_id) {
        File pictureFile = getOutputMediaFile(group_id);

        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("", "Error accessing file: " + e.getMessage());
        }
    }
    private  File getOutputMediaFile(String group_id){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),"Skim Whim");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        Log.d("herepath1",mediaStorageDir.getAbsolutePath());

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        String mImageName="group"+group_id +".png";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
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
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                group_pic.setImageURI(result.getUri());
                Uri imageUri = result.getUri();
                try {
                    flag_image=1;
                    bitmap_main = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
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

    @Override
    protected void onStop() {
        if(backGroundTaskRegister!=null)
        {
            backGroundTaskRegister.cancel(true);
        }
        super.onStop();
    }

    @Override
    protected  void onResume()
    {
        super.onResume();
    }
}
