package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

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

public class Profile_Other extends AppCompatActivity {
    private TextView tv_username,tv_user_name,tv_user_bio;
    private CircleImageView user_image;
    private Button btn_question,btn_answer,btn_group;
    private String cur_number,user_id,cur_user_id,follow_status,user_number,user_name;
    private Button answer,question,groups,follow;
    private Bitmap bitmap_profile_pic;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_other);
        user_image=(CircleImageView)findViewById(R.id.circleimageview_self_profile_pic);
        tv_username=(TextView)findViewById(R.id.tv_username_profile);
        tv_user_name=(TextView)findViewById(R.id.tv_user_profile);
      //  answer=(Button)findViewById(R.id.btn_self_profile_answer);
    //   question=(Button)findViewById(R.id.btn_self_profile_question1) ;
        btn_question=(Button)findViewById(R.id.btn_self_profile_question1) ;
        btn_answer=(Button)findViewById(R.id.btn_self_profile_answer) ;
        btn_group=(Button)findViewById(R.id.btn_self_profile_group) ;

        //groups=(Button)findViewById(R.id.btn_self_profile_group);
        tv_user_bio=(TextView)findViewById(R.id.tv_profile_bio);
        follow=(Button)findViewById(R.id.btn_profile_follow);
        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        ctx=this;
        Bundle b=getIntent().getExtras();
        user_id=b.getString("user_id");

        Log.d("abc","abc2");
       /* question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("abc","abc1");
                Intent i=new Intent(getApplicationContext(),Self_Questions.class);
                i.putExtra("user_id",user_id);
                i.putExtra("user_name",tv_user_name.getText().toString());
                i.putExtra("username",tv_username.getText().toString());
                startActivity(i);
            }
        });
       /* tmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("abc","abc1");
                Intent i=new Intent(getApplicationContext(),Self_Questions.class);
                i.putExtra("user_id",user_id);
                i.putExtra("user_name",tv_user_name.getText().toString());
                i.putExtra("username",tv_username.getText().toString());
                startActivity(i);
            }
        });
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Self_Answer.class);
                i.putExtra("user_id",user_id);
                i.putExtra("user_name",tv_user_name.getText().toString());
                i.putExtra("username",tv_username.getText().toString());
                startActivity(i);
            }
        });*/
       /* question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("abc","abc1");
                Intent i=new Intent(getApplicationContext(),Self_Questions.class);
                i.putExtra("user_id",user_id);
                i.putExtra("user_name",tv_user_name.getText().toString());
                i.putExtra("username",tv_username.getText().toString());
                startActivity(i);
            }
        });*/
       btn_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("abc","abc1");
                Intent i=new Intent(getApplicationContext(),Self_Questions.class);
                i.putExtra("user_id",user_id);
                i.putExtra("user_name",tv_user_name.getText().toString());
                i.putExtra("username",tv_username.getText().toString());
                startActivity(i);
                overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

            }
        });
        btn_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Self_Answer.class);
                i.putExtra("user_id",user_id);
                i.putExtra("user_name",tv_user_name.getText().toString());
                i.putExtra("username",tv_username.getText().toString());
                startActivity(i);
                overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

            }
        });
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("abc","abc3");
            }
        });
        btn_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Self_Profile_Group.class);
                i.putExtra("user_id",user_id);
                startActivity(i);
                overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

            }
        });
        /*
        groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Self_Profile_Group.class);
                i.putExtra("user_id",user_id);
                startActivity(i);
            }
        });
*/
        My_Details_Execute obj=new My_Details_Execute(getApplicationContext(),cur_number);
        Cursor c=obj.get_my_details(obj);
        if(c.getCount()>0) {
            c.moveToFirst();
            cur_user_id=c.getString(0);
        }
        fetch_user_profile(user_id,cur_user_id);


    }
    BackGroundTaskFetchGroups backGroundTaskFetchGroups;
    void fetch_user_profile(String user_id,String cur_user_id)
    {
           backGroundTaskFetchGroups=new BackGroundTaskFetchGroups();
           backGroundTaskFetchGroups.execute(user_id,cur_user_id);
    }


    void disable_all_buttons()
    {
        follow.setEnabled(false);
     //   groups.setEnabled(false);
      //  answer.setEnabled(false);
       // question.setEnabled(false);
    }
    void enable_all_buttons()
    {
        follow.setEnabled(true);
     //   groups.setEnabled(true);
      //  answer.setEnabled(true);
       // question.setEnabled(true);
    }
    class BackGroundTaskFetchGroups extends AsyncTask<String, Void, String>
    {
        int flag1=1,flag;
        BackGroundTaskFetchGroups()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String user_id=params[0];
            String cur_user_id=params[1];


            String register_url="http://scintillato.esy.es/fetch_user_profile.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                        URLEncoder.encode("cur_user_id","UTF-8")+"="+URLEncoder.encode(cur_user_id,"UTF-8");

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
                Log.d("get_group", line);

                if(line.equals("")==true)
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
            //	Log.e("1",result+"");
            //	Toast.makeText(ctx,flag+"",Toast.LENGTH_LONG).show();

            if(flag1==0)
            {

                Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
            }
            else
            {
                if(flag==1)
                {
                    enable_all_buttons();
                    add_groups_list(result);
                    fetch_profile_pic(user_id);

                }
                else
                {
                    Toast.makeText(ctx, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        @Override
        protected void onPreExecute() {
            disable_all_buttons();
        }
    }

    BackGroundTaskFetchImage backGroundTaskFetchImage;
    void fetch_profile_pic(String user_id)
    {
        backGroundTaskFetchImage=new BackGroundTaskFetchImage();
        backGroundTaskFetchImage.execute(user_id);
    }
    class BackGroundTaskFetchImage extends AsyncTask<String, Void, String>
    {
        int flag1=1,flag;
        BackGroundTaskFetchImage()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String user_id=params[0];


            String register_url="http://scintillato.esy.es/fetch_profile_pic_user_id.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8");

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
                Log.d("get_group", line);

                if(line.equals("")==true)
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
            //	Log.e("1",result+"");
            //	Toast.makeText(ctx,flag+"",Toast.LENGTH_LONG).show();

            if(flag1==0)
            {

                Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
            }
            else
            {

                if(flag==1)
                {

                    String profile_pic_string=fetch_profile_string(result);
                    bitmap_profile_pic=getProfileImage(profile_pic_string);
                  //  user_image.setImageBitmap(BitmapFactory.decodeFile(bitmap_profile_pic));
                    user_image.setImageBitmap(bitmap_profile_pic);
                    Log.d("profile_pic3",profile_pic_string);
                    //Log.d("profile_pic",c.getString(0));
                }
                else
                {
                    Toast.makeText(ctx, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        @Override
        protected void onPreExecute() {
            disable_all_buttons();
        }
    }

    public Bitmap getProfileImage(String u_profile_pic)
    {
        byte[] decodedString = Base64.decode(u_profile_pic, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
    public void add_groups_list(String myJSON) {
        int  count = 0;
        Log.d("get_group1",myJSON);
        JSONObject jsonObject;
        JSONArray jsonArray;

        try {
            jsonObject = new JSONObject(myJSON);
            jsonArray = jsonObject.getJSONArray("result");
            String user_name,username,mobile_no,gender,date,followers,following,respect_points,follow_status,user_bio;
            Log.d("length1", jsonArray.length()+"");


            while (count < jsonArray.length()) {
                JSONObject JO = jsonArray.getJSONObject(count);
                user_name = JO.getString("user_name");
                username = JO.getString("username");
                mobile_no = JO.getString("mobile_no");
                gender = JO.getString("gender");
                date = JO.getString("date");
                followers = JO.getString("followers");
                following = JO.getString("following");
                respect_points = JO.getString("respect_points");
                follow_status=JO.getString("follow_status");
                user_bio=JO.getString("user_bio");

                this.user_name=user_name;
                this.user_number=mobile_no;
                tv_user_name.setText(user_name);
                tv_username.setText("@"+username);
                tv_user_bio.setText(user_bio);
                this.follow_status=follow_status;
                if(follow_status.equals("1"))
                {
                    follow.setText("following");
                }
                else
                {
                    follow.setText("follow");
                }
                count++;
            }
        }
        catch (Exception e)
        {
            Log.d("error","here");
        }
    }
    public String fetch_profile_string(String myJSON) {
        int  count = 0;
        Log.d("get_group1",myJSON);
        JSONObject jsonObject;
        JSONArray jsonArray;
        String profile_pic="";
        try {
            jsonObject = new JSONObject(myJSON);
            jsonArray = jsonObject.getJSONArray("result");

            Log.d("length1", jsonArray.length()+"");

            while (count < jsonArray.length()) {
                JSONObject JO = jsonArray.getJSONObject(count);

                profile_pic=JO.getString("profile_pic");

                count++;
            }
        }
        catch (Exception e)
        {
            Log.d("error","here");
        }
        return profile_pic;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_other, menu);
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
        else if(id==R.id.btn_profile_other_unknown)
        {
            Intent i=new Intent(ctx,Message_Chat_Single_Profile.class);
            i.putExtra("user_number",user_number);
            i.putExtra("user_name",user_name);
            startActivity(i);
            overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onPause()
    {
        if(backGroundTaskFetchGroups!=null)
        {
            backGroundTaskFetchGroups.cancel(true);
        }
        if(backGroundTaskFetchImage!=null)
            backGroundTaskFetchImage.cancel(true);
        super.onPause();
    }
    @Override
    public void onStop()
    {

        if(backGroundTaskFetchGroups!=null)
        {
            backGroundTaskFetchGroups.cancel(true);
        }

        if(backGroundTaskFetchImage!=null)
            backGroundTaskFetchImage.cancel(true);
        super.onStop();
    }
}
