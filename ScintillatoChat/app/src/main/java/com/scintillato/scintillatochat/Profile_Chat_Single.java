package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class Profile_Chat_Single extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton floatingActionButton;
    int mutedColor = R.attr.colorPrimary;
    RecyclerView recyclerView;


    private Profile_Chat_Single_Recycler_Adapter adapter;
    ArrayList<Profile_Chat_Single_Groups_List> list;

    private String user_number,cur_number;

    private Context ctx;
    private ImageView profie_pic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_chat_single);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.fab);
        recyclerView=(RecyclerView)findViewById(R.id.lv_profile_chat_single_groups);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbar.setTitle("Hello");
        collapsingToolbarLayout.setTitle("Demo");

        setSupportActionBar(toolbar);

        //recyclerview
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        profie_pic=(ImageView)findViewById(R.id.iv_profile_chat_single_pic);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int pxWidth = displayMetrics.widthPixels;
        profie_pic.getLayoutParams().width=pxWidth;
        profie_pic.getLayoutParams().height=pxWidth;
        ctx=this;
        list=new ArrayList<>();

        adapter=new Profile_Chat_Single_Recycler_Adapter(getApplicationContext(),list);

        recyclerView.setAdapter(adapter);

        Bundle b=getIntent().getExtras();
        user_number=b.getString("user_number");

        collapsingToolbarLayout.setTitle(user_number);
        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");

        fetch_single_profile_pic(user_number,profie_pic);
        /*Group_Execute obj=new Group_Execute(getApplicationContext(),cur_number);
        Cursor c=obj.get_groups_from_member(obj,user_number);
        tv_group_count.setText(c.getCount()+"");
        if(c.getCount()>0)
        {
            c.moveToFirst();
            do {

                Cursor c1=obj.get_group_details(obj,c.getString(0));
                if(c1.getCount()>0) {
                    c1.moveToFirst();
                    Profile_Chat_Single_Groups_List list = new Profile_Chat_Single_Groups_List(c.getString(0), c1.getString(0));
                    adapter.add(list);
                }
            }while(c.moveToNext());
        }
*/
        fetch_groups();
    }

    void fetch_groups()
    {
        Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
        Cursor c=obj.get_groups_member(obj,user_number);
        if(c.getCount()>0)
        {
            c.moveToFirst();
            do {
                Cursor c1=obj.fetch_group_selected(obj,c.getString(0));
                if(c1.getCount()>0)
                {
                    c1.moveToFirst();
                    do {
                        Profile_Chat_Single_Groups_List list1 = new Profile_Chat_Single_Groups_List(c.getString(0), c1.getString(1));
                        list.add(list1);
                        adapter.notifyDataSetChanged();
                    }while (c1.moveToNext());
                }
            }while (c.moveToNext());
        }

    }
    void fetch_single_profile_pic(final String num,final ImageView profile_pic)
    {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),"Skim Whim");
        File mediaFile;
        String mImageName=num +".png";

        Log.d("h1","h1");
        File file = new File(mediaStorageDir.getPath()+File.separator+mImageName);
        Log.d("h1","h2");
        if(file.exists()){
            Toast.makeText(ctx, "File exists in /mnt", Toast.LENGTH_SHORT);
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
            Picasso.with(ctx).load(mediaFile).placeholder(profile_pic.getDrawable()).into(profile_pic, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    String number_trunc = num.substring(1);
                    Picasso.with(ctx).load("http://scintillato.esy.es/fetch_profile_pic_png_number.php?user_number=" + number_trunc).placeholder(profile_pic.getDrawable()).into(profile_pic, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                            if(profile_pic!=null) {
                                profile_pic.buildDrawingCache();
                                Bitmap bmap = profile_pic.getDrawingCache();
                                if(bmap!=null)
                                    storeImageSingle(bmap, num);
                            }
                        }

                        @Override
                        public void onError() {

                            Toast.makeText(ctx,"error picaso"+num,Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                @Override
                public void onError() {

                    Toast.makeText(ctx,"error picaso"+num,Toast.LENGTH_SHORT).show();
                }
            });
            Log.d("h1","h3");
        }
        else{
            String number_trunc = num.substring(1);
            Picasso.with(ctx).load("http://scintillato.esy.es/fetch_profile_pic_png_number.php?user_number=" + number_trunc).placeholder(profile_pic.getDrawable()).into(profile_pic, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {

                    if(profile_pic!=null) {
                        profile_pic.buildDrawingCache();
                        Bitmap bmap = profile_pic.getDrawingCache();
                        if(bmap!=null)
                            storeImageSingle(bmap, num);
                    }
                }

                @Override
                public void onError() {

                    Toast.makeText(ctx,"error picaso"+num,Toast.LENGTH_SHORT).show();
                }
            });

        }
        Log.d("h1","h4"+num);

    }
    private void storeImageSingle(Bitmap image,String number) {
        File pictureFile = getOutputMediaFileSingle(number);

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
    private  File getOutputMediaFileSingle(String number){
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
        String mImageName=number +".png";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }


}
