package com.scintillato.scintillatochat;

import android.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import de.hdodenhof.circleimageview.CircleImageView;

public class Edit_Profile extends AppCompatActivity {

    private Uri mImageCaptureUri;
    private EditText et_username,et_user_name,et_bio;
    private String ori_username,ori_user_name,ori_bio,user_id,new_username,new_user_name,new_bio,cur_number;
    private Context ctx;
    private CircleImageView profile_pic;
    private ProgressDialog loading;
    private BackGroundTaskRegister backGroundTaskRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        profile_pic=(CircleImageView)findViewById(R.id.ibtn_edit_profile);
        ctx=this;
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");

        Bundle b=getIntent().getExtras();
        ori_bio=b.getString("bio");
        ori_user_name=b.getString("user_name");
        ori_username=b.getString("username");
        user_id=b.getString("user_id");

        et_user_name=(EditText)findViewById(R.id.et_edit_profile_user_name);

        et_bio=(EditText)findViewById(R.id.et_edit_profile_bio);
        et_username=(EditText)findViewById(R.id.et_edit_profile_username);
        et_bio.setText(ori_bio);
        et_user_name.setText(ori_user_name);
        et_username.setText(ori_username.substring(1));

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick(v);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==R.id.btn_edit_profile_change)
        {
            new_bio=et_bio.getText().toString();
            new_user_name=et_user_name.getText().toString();
            new_username=et_username.getText().toString();
            if(new_bio.equals(ori_bio) && new_username.equals(ori_username)&&new_user_name.equals(ori_user_name))
            {
                Intent i=new Intent(getApplicationContext(),Start_Page.class);
                finish();
                startActivity(i);
                overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

            }
            else
            {

                backGroundTaskRegister=new BackGroundTaskRegister();
                backGroundTaskRegister.execute(user_id,new_user_name,new_username,new_bio);
            }

        }


        return super.onOptionsItemSelected(item);
    }
    class BackGroundTaskRegister extends AsyncTask<String, Void, String> {
        int flag1 = 1;
        int flag;

        BackGroundTaskRegister() {
            flag = 0;
        }

        @Override
        protected String doInBackground(String... params) {

            String user_id = params[0];
            String user_name = params[1];
            String username=params[2];
            String bio=params[3];

            String register_url = "http://scintillato.esy.es/edit_profile_info.php";


            try {
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8") + "&" +
                        URLEncoder.encode("username" , "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")+ "&" +
                        URLEncoder.encode("name" , "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8")+ "&" +
                        URLEncoder.encode("user_bio" , "UTF-8") + "=" + URLEncoder.encode(bio, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));

                String line = "";
                line = bufferedReader.readLine();


                bufferedReader.close();
                IS.close();
                httpURLConnection.disconnect();

                if (line.equals("") == false) {
                    flag = 1;

                } else {
                    flag = 0;
                }

                return line;

            } catch (Exception e) {
                flag1 = 0;
                return "Check Internet Connection!";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            //  Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG);
            loading.dismiss();
            if(flag1==0)
            {
                Toast.makeText(ctx,result,Toast.LENGTH_LONG);
            }
            else
            {
                if(flag==1) {

                    My_Details_Execute obj=new My_Details_Execute(getApplicationContext(),cur_number);
                    obj.update_profile_details(obj,user_id,new_bio,new_username,new_user_name);
                    Intent i=new Intent(getApplicationContext(),Self_Profile.class);
                    finish();
                    startActivity(i);
                    overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

                }
            }
        }
        @Override
        protected void onPreExecute() {

            loading = ProgressDialog.show(ctx, "Status", "Updating...",true,false);

        }


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
                ((CircleImageView) findViewById(R.id.ibtn_edit_profile)).setImageURI(result.getUri());
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

    @Override
    public void onPause()
    {
        if(backGroundTaskRegister!=null)
        {
            backGroundTaskRegister.cancel(true);
        }
        super.onPause();
    }

    @Override
    public void onStop()
    {
        if(backGroundTaskRegister!=null)
        {
            backGroundTaskRegister.cancel(true);
        }
        super.onStop();
    }
}
