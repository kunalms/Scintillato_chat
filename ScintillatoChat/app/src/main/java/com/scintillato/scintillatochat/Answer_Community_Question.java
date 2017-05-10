package com.scintillato.scintillatochat;

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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class Answer_Community_Question extends AppCompatActivity {


    private Uri mImageCaptureUri;
    private Bitmap bitmap_image_main=null;
    private String string_answer_image="";
    private Button post;
    private CircleImageView dp;
    private TextView tv_name,tv_question;
    private String user_id;
    private ImageView answer_image;
    private ProgressDialog loading;
    private Context ctx;
    private EditText et_answer;
    private String question_id,user_name,question,answer_count,like_count,like_stat,anonymous;
    private BackGroundTaskRegister backGroundTaskRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_community_question);
        post=(Button)findViewById(R.id.btn_answer_community_question_post);
        dp=(CircleImageView)findViewById(R.id.iv_answer_community_question_dp);
        tv_name=(TextView)findViewById(R.id.tv_answer_community_question_name);
        et_answer=(EditText)findViewById(R.id.et_answer_community_question_answer);
        tv_question=(TextView)findViewById(R.id.tv_answer_community_question_question);
        answer_image=(ImageView)findViewById(R.id.iv_answer_community_question_image);
        ctx=this;
        Bundle b=getIntent().getExtras();

        question_id=b.getString("question_id");
        answer_count=b.getString("answer_count");
        question=b.getString("question");
        like_count=b.getString("like_count");
        user_name=b.getString("user_name");
        like_stat=b.getString("like_stat");
        anonymous=b.getString("anonymous");

        tv_question.setText(question);
        Log.d("like_stat",like_stat);
        if(anonymous.equals("0"))
        tv_name.setText(user_name);
        else
        tv_name.setText("Anonymous User");
        Toast.makeText(getApplicationContext(),question_id,Toast.LENGTH_LONG).show();
        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        String cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(getApplicationContext(),cur_number);
        Cursor c=obj.get_my_details(obj);
        if(c.getCount()>0)
        {
            c.moveToFirst();
            do
            {
                user_id=c.getString(0);
            }while (c.moveToNext());
        }
        Log.d("question_id1",question_id+user_id);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("question_id2",question_id+user_id);

                if(et_answer.getText().toString().equals("")==false)
                post_answer();
                else
                {
                    Toast.makeText(getApplicationContext(),"Answer cannot be blank!",Toast.LENGTH_LONG).show();
                }
            }
        });
        answer_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick(v);

            }
        });
    }
    void post_answer()
    {
        if(bitmap_image_main!=null)
        {
            string_answer_image=getStringImage(bitmap_image_main);
        }
        else
        {
            string_answer_image="";

        }
        Log.d("question_id",question_id+user_id);
        backGroundTaskRegister=new BackGroundTaskRegister();
        backGroundTaskRegister.execute(question_id,et_answer.getText().toString(),user_id,string_answer_image);
    }

    int flag;
    class BackGroundTaskRegister extends AsyncTask<String, Void, String> {
        int flag1 = 1;

        BackGroundTaskRegister() {
            flag = 0;
        }

        @Override
        protected String doInBackground(String... params) {

            String answer = params[1];
            String question_id = params[0];
            String user_id=params[2];
            String answer_image=params[3];

            String register_url = "http://scintillato.esy.es/answer_question_community.php";


            try {
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode("answer", "UTF-8") + "=" + URLEncoder.encode(answer, "UTF-8") + "&" +
                        URLEncoder.encode("question_id" , "UTF-8") + "=" + URLEncoder.encode(question_id, "UTF-8")+ "&" +
                        URLEncoder.encode("user_id" , "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8")+ "&" +
                        URLEncoder.encode("answer_image" , "UTF-8") + "=" + URLEncoder.encode(answer_image, "UTF-8");

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
                    //Log.d("inside","1");
                    //tv_status.setText(line);

                } else {
                    //Log.d("outside","1");
                    flag = 0;
                    //tv_status.setText("failure");
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
                    Intent i=new Intent(ctx,Answer_Feed.class);
                    i.putExtra("question_id",question_id);
                    i.putExtra("question",question);
                    i.putExtra("user_name",user_name);
                    i.putExtra("like_count",like_count);
                    i.putExtra("answer_count",answer_count);
                    i.putExtra("like_stat",like_stat);
                    i.putExtra("anonymous",anonymous);

                    finish();
                    startActivity(i);

                }
            }
        }
        @Override
        protected void onPreExecute() {

               loading = ProgressDialog.show(ctx, "Status", "Registering...",true,false);

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
                answer_image.setImageURI(result.getUri());
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
