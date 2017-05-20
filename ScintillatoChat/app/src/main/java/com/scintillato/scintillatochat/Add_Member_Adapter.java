package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by VIVEK on 13-03-2017.
 */
public class Add_Member_Adapter extends RecyclerView.Adapter<Add_Member_Adapter.Holder> {


    private List<Choose_Contacts_Single_List> list=new ArrayList<>();

    public class Holder extends RecyclerView.ViewHolder {
        public final View view;
        TextView name,number;
        CheckBox select;
        CircleImageView profile_pic;
        
        public Holder(View row) {
            super(row);
            this.view = row;
            name=(TextView)row.findViewById(R.id.tv_chose_contacts_row_name);
            number=(TextView)row.findViewById(R.id.tv_chose_contacts_row_number);
            select=(CheckBox)row.findViewById(R.id.checkBox_choose_contacts_row_select);
            profile_pic=(CircleImageView)row.findViewById(R.id.iv_chose_contacts_row_dp);
        }
    }

    private Context ctx;
    private String cur_number,cur_user_id;
    public Add_Member_Adapter(Context ctx, List<Choose_Contacts_Single_List> list) {
        this.list = list;
        this.ctx=ctx;
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(ctx,cur_number);
        Cursor c=obj.get_my_details(obj);
        c.moveToFirst();
        if(c.getCount()>0)
            cur_user_id=c.getString(0);
    }
    @Override
    public Add_Member_Adapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.choose_contacts_row, parent, false);

        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(final Add_Member_Adapter.Holder holder, int position) {
        holder.setIsRecyclable(false);
        Choose_Contacts_Single_List contacts_single_list=list.get(position);
        holder.name.setText(contacts_single_list.get_name());
        holder.number.setText(contacts_single_list.get_number());
        final String num=contacts_single_list.get_number();
        final String name=contacts_single_list.get_name();

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),"Skim Whim");
        File mediaFile;
        String mImageName=num +".png";

        Log.d("h1","h1");
        File file = new File(mediaStorageDir.getPath()+File.separator+mImageName);
        Log.d("h1","h2");
        if(file.exists()){
            Toast.makeText(ctx, "File exists in /mnt", Toast.LENGTH_SHORT);
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
            Picasso.with(ctx).load(mediaFile).placeholder(holder.profile_pic.getDrawable()).into(holder.profile_pic);
            Log.d("h1","h3");
        }

        Log.d("h1","h4"+num);

        String number_trunc = num.substring(1);
        Picasso.with(ctx).load("http://scintillato.esy.es/fetch_profile_pic_png_number.php?user_number=" + number_trunc).placeholder(holder.profile_pic.getDrawable()).into(holder.profile_pic, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                if(holder.profile_pic!=null) {
                    holder.profile_pic.buildDrawingCache();
                    Bitmap bmap = holder.profile_pic.getDrawingCache();
                    if(bmap!=null)
                        storeImage(bmap, num);
                }
            }

            @Override
            public void onError() {

                Toast.makeText(ctx,"error picaso"+num,Toast.LENGTH_SHORT).show();
            }
        });

        holder.select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
        if(isChecked)
        {
            Selected_Memebers_Execute obj=new Selected_Memebers_Execute(ctx,cur_number);
            obj.putinfo_selected_members_temp(obj, num,name);
            Log.e("selected", num);
        }
        else
        {
            Selected_Memebers_Execute obj=new Selected_Memebers_Execute(ctx,cur_number);
            boolean bool=obj.delete_selected_members_temp_row(num);
            if(bool==true)
            {
                Log.e("deleted", "deleted");
            }
            Cursor c=obj.getinfo_selected_members_temp(obj);
            if(c.getCount()>0)
            {
                c.moveToFirst();
                do
                {
                    Log.e("number:fetch:",c.getString(0));
                }while(c.moveToNext());
            }
        }
        }
        }
        );
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


    @Override
    public int getItemCount() {
        return list.size();
    }


   
}