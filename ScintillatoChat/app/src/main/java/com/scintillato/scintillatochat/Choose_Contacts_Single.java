package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.util.ArrayList;

public class Choose_Contacts_Single extends AppCompatActivity {

    private ListView list_contacts;
    private String user_number;
    private Choose_Contacts_Single_Adapter adapter;
    private String cur_number;
    private int index;
    private ArrayList<String> user_number_list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_contacts_single);
        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        list_contacts=(ListView)findViewById(R.id.lv_choose_contacts_single_contacts);
        adapter=new Choose_Contacts_Single_Adapter(getApplicationContext(),R.layout.activity_choose_contacts_single);
        list_contacts.setAdapter(adapter);
        list_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Choose_Contacts_Single_List list=(Choose_Contacts_Single_List)adapter.getItem(position);
                user_number=list.get_number();
                Intent i=new Intent(getApplicationContext(),Chat_Message_Single.class);
                i.putExtra("user_number",user_number);
                startActivity(i);
                overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);


            }
        });

        fetch_contacts();
        fetch_profile_pic(user_number_list.get(0));

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
                    Choose_Contacts_Single_List contacts_single_list = new Choose_Contacts_Single_List(cr.getString(0), cr.getString(1));
                    adapter.add(contacts_single_list);
                    adapter.notifyDataSetChanged();
                    user_number_list.add(cr.getString(1));

                } while (cr.moveToNext());
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.choose_contacts_single_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==R.id.btn_choose_contacts_unknown)
        {
            Intent i=new Intent(getApplicationContext(),New_Unknown_User_Send.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }


//    BackGroundTaskFetchImage backGroundTaskFetchImage;
    void fetch_profile_pic(String user_number)
    {
        Log.d("profile_pic41",user_number);
       // backGroundTaskFetchImage=new BackGroundTaskFetchImage();
        //backGroundTaskFetchImage.execute(user_number);
    }

    class BackGroundTaskFetchImage extends AsyncTask<String, Void, String>
    {
        String number;
        int flag1=1,flag;
        BackGroundTaskFetchImage()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String user_number=params[0];
            this.number=params[0];
            String register_url="http://scintillato.esy.es/fetch_user_profile_pic1.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("user_number","UTF-8")+"="+URLEncoder.encode(user_number,"UTF-8");

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

                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                fetch_profile_pic(user_number_list.get(index));
            }
            else
            {

                if(flag==1)
                {
                    String profile_pic_string=fetch_profile_string(result);
                    Bitmap bitmap_profile_pic=getProfileImage(profile_pic_string);

                    Contacts_Unregistered_Execute obj=new Contacts_Unregistered_Execute(getApplicationContext(),cur_number);
                    obj.update_profile_pic_registered(obj,profile_pic_string,number);
                    //  user_image.setImageBitmap(BitmapFactory.decodeFile(bitmap_profile_pic));
                    storeImage(bitmap_profile_pic,number);
                    Log.d("profile_pic4",profile_pic_string);
                    index=index+1;
                    if(index<user_number_list.size())
                    fetch_profile_pic(user_number_list.get(index));
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        @Override
        protected void onPreExecute() {
        }
    }
    private void storeImage(Bitmap image,String number) {
        File pictureFile = getOutputMediaFile(number);

        if (pictureFile == null) {
            Log.d("herepath",pictureFile.getAbsolutePath());
            Log.d("","Error creating media file, check storage permissions: ");// e.getMessage());
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
    private  File getOutputMediaFile(String number){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),"Skim Whim");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        Log.d("herepath1",mediaStorageDir.getAbsolutePath());
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            Log.d("here1","here1");
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        Log.d("here2","here2");
        // Create a media file name
        File mediaFile;
        String mImageName=number +".png";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
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

    public Bitmap getProfileImage(String u_profile_pic)
    {
        byte[] decodedString = Base64.decode(u_profile_pic, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    @Override
    protected void onPause()
    {
   //     if(backGroundTaskFetchImage!=null)
     //       backGroundTaskFetchImage.cancel(true);

        super.onPause();
    }


    @Override
    protected void onStop()
    {
//        if(backGroundTaskFetchImage!=null)
  //          backGroundTaskFetchImage.cancel(true);

        super.onStop();
    }
}
