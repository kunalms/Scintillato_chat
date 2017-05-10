package com.scintillato.scintillatochat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class Group_Details_Public extends AppCompatActivity {


    private EditText group_name,group_title,group_description;
    private CircleImageView group_pic;
    private ListView members;
    private String jsonString_members,cur_number,selected_category_id;
    private int member_count;
    private String[] category_all_list;
    private Context ctx;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private Uri mImageCaptureUri;
    private ProgressDialog loading;
    private int flag_image;
    private Cursor cursor_members;
    private Spinner category;

    private int status_value=1;
    private Group_Details_Adapter adapter;
    private Selected_Memebers_Execute obj;
    private BackGroundTaskRegister backGroundTaskRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_details_public);
        ctx=this;
        category_all_list = getResources().getStringArray(R.array.Categories);
        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        members=(ListView)findViewById(R.id.lv_group_details_public_members);
        group_name=(EditText)findViewById(R.id.et_group_details_public_name);
        group_title=(EditText)findViewById(R.id.et_group_details_public_topic);
        group_description=(EditText)findViewById(R.id.et_group_details_public_description);
        group_pic=(CircleImageView)findViewById(R.id.iv_group_details_public_image);
        category=(Spinner)findViewById(R.id.spinner_group_details_public_categories);
        adapter=new Group_Details_Adapter(getApplicationContext(), R.layout.group_details_row);
        members.setAdapter(adapter);

        final String [] items			= new String [] {"Take from camera", "Select from gallery"};
        ArrayAdapter<String> adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder		= new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int item ) { //pick from camera
                if (item == 0) {
                    Intent intent 	 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                    try {
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else { //pick from file
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        } );

        final AlertDialog dialog = builder.create();
        group_pic.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {
                flag_image=1;
                dialog.show();
            }
        });
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

                selected_category_id=position+1+"";

            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

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
            if(cursor_members.getCount()>0)
            {
                SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
                String number = sharedpreferences.getString("number", "");
                cursor_members.moveToFirst();
                do{

                    object=new JSONObject();
                    try {
                        object.put("number", cursor_members.getString(0));
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
        getMenuInflater().inflate(R.menu.group_details_public, menu);
        return true;
    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );

        int size = list.size();

        if (size == 0) {
            Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();

            return;
        } else {
            intent.setData(mImageCaptureUri);

            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            if (size == 1) {
                Intent i 		= new Intent(intent);
                ResolveInfo res	= list.get(0);

                i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title 	= getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon		= getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent= new Intent(intent);

                    co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Crop App");
                builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int item ) {
                        startActivityForResult( cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
                    }
                });

                builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel( DialogInterface dialog ) {

                        if (mImageCaptureUri != null ) {
                            getContentResolver().delete(mImageCaptureUri, null, null );
                            mImageCaptureUri = null;
                        }
                    }
                } );

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
    }
    private String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case PICK_FROM_CAMERA:
                doCrop();

                break;

            case PICK_FROM_FILE:
                mImageCaptureUri = data.getData();

                doCrop();

                break;

            case CROP_FROM_CAMERA:
                Bundle extras = data.getExtras();

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");

                    group_pic.setImageBitmap(photo);
                }

                File f = new File(mImageCaptureUri.getPath());

                if (f.exists()) f.delete();

                break;

        }
    }
    void private_group_create(String group_id)
    {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");;
        String currentDateandTime = sdf.format(new Date());
        /*Group_Execute obj1 = new Group_Execute(getApplicationContext(),cur_number);
        obj1.putinfo_groups(obj1, group_name.getText().toString(), group_title.getText().toString(), member_count+1 + "", group_description.getText().toString(), currentDateandTime,"1",group_id);

        if (cursor_members.getCount() > 0) {
            cursor_members.moveToFirst();
            do {
                obj1.putinfo_group_members(obj1, cursor_members.getString(1), cursor_members.getString(0), 0 + "", group_id);

            } while (cursor_members.moveToNext());
        }
        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        String name = sharedpreferences.getString("name", "");
        String number = sharedpreferences.getString("number", "");
        //if (!(number.equals("") && name.equals(""))) {
        obj1.putinfo_group_members(obj1, name, number, 1 + "", group_id);
        //   obj1.putinfo_recentchats(obj1,group_id,"0",currentDateandTime,"1");
        if(obj1.recent_chats_group_exists(obj1,group_id))
        {
            Log.d("sentupdate",group_id);
            obj1.update_recent_chats(obj1,group_id,"0","1",currentDateandTime,"Group Created","0");
        }
        else
        {
            Log.d("sentinsert",group_id);
            obj1.putinfo_recentchats(obj1,group_id,"0",currentDateandTime,"1","Group Created","0");
        }

        //}

        obj.delete_selected_members_temp();
        */
        group_pic.buildDrawingCache();
        Bitmap bmap = group_pic.getDrawingCache();
        storeImage(bmap,group_id);

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

        if(id==R.id.btn_group_details_public_create)
        {
            public_group_create(1);
        }


        return super.onOptionsItemSelected(item);
    }

    void public_group_create(int x)
    {
        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        String number = sharedpreferences.getString("number", "");
        String group_pic_string;
        group_pic.buildDrawingCache();
        Bitmap bmap = group_pic.getDrawingCache();
        group_pic_string=getStringImage(bmap);
                backGroundTaskRegister = new BackGroundTaskRegister();
                backGroundTaskRegister.execute(group_name.getText().toString(), group_title.getText().toString(), group_description.getText().toString(), member_count + 1 + "", group_pic_string, x + "", number, jsonString_members,selected_category_id);

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
            String category_id=params[8];
            Log.d("mmemem",members);
            String register_url="http://www.scintillato.esy.es/group_insert_public.php";


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
                        URLEncoder.encode("members","UTF-8")+"="+URLEncoder.encode(members,"UTF-8")+"&"+
                        URLEncoder.encode("category_id","UTF-8")+"="+URLEncoder.encode(category_id,"UTF-8");

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
            loading.cancel();
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
                    Log.d("sentsent",result);
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
            loading = ProgressDialog.show(ctx, "Status", "Creating Group...",true,false);
        }
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


    @Override
    protected void onPause() {
        if(backGroundTaskRegister!=null)
        {
            backGroundTaskRegister.cancel(true);
        }
        super.onPause();
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
