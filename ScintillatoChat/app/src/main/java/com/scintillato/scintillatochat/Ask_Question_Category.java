package com.scintillato.scintillatochat;

import android.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class Ask_Question_Category extends AppCompatActivity {

    private Uri mImageCaptureUri;
    private Bitmap bitmap_image_main=null;
    private EditText question,category;
    private Button post;
    private String question_text,json_category;
    private ArrayList<String> list_category_fetch;
    private Context ctx;
    private CircleImageView iv_que_image;
    private ListView list_category;
    private Ask_Question_Category_Adapter adapter;
    private ProgressDialog loading;
    private boolean isanonymous;
    private String string_question_image;
    private Switch anonymous;
    private BackGroundFetch backGroundFetch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_question_category);
        ctx=this;
        getWindow().setBackgroundDrawableResource(R.drawable.chatback);

        question=(EditText)findViewById(R.id.et_ask_question_category_question);
        post=(Button)findViewById(R.id.btn_ask_question_category_post);
        question=(EditText)findViewById(R.id.et_ask_question_category_question);
        post=(Button)findViewById(R.id.btn_ask_question_category_post);
        iv_que_image=(CircleImageView)findViewById(R.id.iv_ask_question_category_image);
        category=(EditText)findViewById(R.id.et_ask_question_category_category);
        list_category=(ListView)findViewById(R.id.lv_ask_question_category_category);
        adapter=new Ask_Question_Category_Adapter(getApplicationContext(),R.layout.ask_question_category_row);
        list_category.setAdapter(adapter);
        final String []cat=getResources().getStringArray(R.array.Categories);

        for (String i: cat)
        {
        }
        anonymous=(Switch)findViewById(R.id.switch_ask_question_category_anonumous);
        anonymous.setChecked(false);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question_text=question.getText().toString();
                if(question_text.equals("")==true)
                {
                    Toast.makeText(getApplicationContext(),"Ask question",Toast.LENGTH_SHORT).show();
                }
                else
                ask_question();
            }
        });

        anonymous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                isanonymous=isChecked;
                Toast.makeText(getApplicationContext(),""+isanonymous,Toast.LENGTH_LONG).show();
            }
        });


        category.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                adapter=null;
                list_category.setAdapter(null);
                adapter=new Ask_Question_Category_Adapter(getApplicationContext(),R.layout.ask_question_category_row);
                list_category.setAdapter(adapter);
                fetch(s+"");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        iv_que_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick(v);

            }
        });
    }

    void fetch(String s){
        backGroundFetch=new BackGroundFetch();
        backGroundFetch.execute(s);
    }

    BackGroundTaskRegister backGroundTaskRegister;
    void ask_question()
    {
        ArrayList<Ask_questions_category_list> categoryArrayList =(ArrayList<Ask_questions_category_list>) adapter.list;

        JSONArray jsonArray=new JSONArray();
        for(int i=0;i<categoryArrayList.size();i++)
        {

            if(categoryArrayList.get(i).get_selected()==true) {
                jsonArray.put(Integer.parseInt(categoryArrayList.get(i).getTag_id()));
            }
        }
        json_category=jsonArray.toString();
        Toast.makeText(getApplicationContext(),json_category,Toast.LENGTH_LONG).show();
        String user_id=null;

        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        String cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(getApplicationContext(),cur_number);
        Cursor c=obj.get_my_details(obj);
        if(c.getCount()>0)
        {
            c.moveToFirst();
            user_id=c.getString(0);
        }
        String anonymous_text;
        if(isanonymous==true)
            anonymous_text="1";
        else
        anonymous_text="0";
        if(bitmap_image_main!=null)
            string_question_image=getStringImage(bitmap_image_main);
        else
        {
            string_question_image="";
        }
       backGroundTaskRegister=new BackGroundTaskRegister();
       backGroundTaskRegister.execute(question_text,user_id,json_category,anonymous_text,string_question_image);
    }

    class BackGroundFetch extends AsyncTask<String, Void, String> {
        int flag1=1;
        BackGroundFetch()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String tags=params[0];
            String register_url="http://scintillato.esy.es/fetch_tags.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("tags","UTF-8")+"="+URLEncoder.encode(tags,"UTF-8");

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
                Toast.makeText(ctx,result,Toast.LENGTH_LONG);
            }
            else
            {

                if(flag==1)
                {
                    decode_json(result);
                }
            }
        }
        @Override
        protected void onPreExecute() {
            loading = ProgressDialog.show(ctx, "Status", "Registering...",true,true);
            loading.setOnCancelListener(new DialogInterface.OnCancelListener() {

                public void onCancel(DialogInterface arg0) {

                }
            });

        }
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

            String question=params[0];
            String user_id=params[1];
            String json_tags=params[2];
            String anonymous=params[3];
            String question_image=params[4];
            String register_url="http://scintillato.esy.es/insert_question_category_1.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("question","UTF-8")+"="+URLEncoder.encode(question,"UTF-8")+"&"+
                        URLEncoder.encode("json_tags","UTF-8")+"="+URLEncoder.encode(json_tags,"UTF-8")+"&"+
                        URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                        URLEncoder.encode("anonymous","UTF-8")+"="+URLEncoder.encode(anonymous,"UTF-8")+"&"+
                        URLEncoder.encode("question_image","UTF-8")+"="+URLEncoder.encode(question_image,"UTF-8");

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
                Toast.makeText(ctx,result,Toast.LENGTH_LONG);
            }
            else
            {

                if(flag==1)
                {
                    loading.dismiss();
                    Intent i=new Intent(getApplicationContext(),Start_Page.class);
                    finish();
                    startActivity(i);
                    //Toast.makeText(Create_Page_3.this,result,Toast.LENGTH_LONG).show();
                }
            }
        }
        @Override
        protected void onPreExecute() {
            loading = ProgressDialog.show(ctx, "Status", "Registering...",true,true);
            loading.setOnCancelListener(new DialogInterface.OnCancelListener() {

                public void onCancel(DialogInterface arg0) {


                }
            });

        }
    }



    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private int count;
    public void decode_json(String myJSON)
    {
        count=0;

        Log.d("list_inside","inside");

        try {
            jsonObject=new JSONObject(myJSON);
            jsonArray=jsonObject.getJSONArray("result");
            String tag_name,tag_id,community_id;

            while(count<jsonArray.length())
            {
                Log.d("list_inside","inside"+myJSON);

                JSONObject JO=jsonArray.getJSONObject(count);
                tag_id=JO.getString("tag_id");
                tag_name=JO.getString("tag_name");
                community_id=JO.getString("community_id");
                count++;
                Ask_questions_category_list list=new Ask_questions_category_list(tag_name,tag_id);
                adapter.add(list);
            }
        }
        catch (JSONException e)
        {

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
                ((CircleImageView) findViewById(R.id.iv_ask_question_category_image)).setImageURI(result.getUri());
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
    private String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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
