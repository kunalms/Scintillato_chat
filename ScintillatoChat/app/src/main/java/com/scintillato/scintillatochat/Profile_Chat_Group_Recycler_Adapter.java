package com.scintillato.scintillatochat;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by VIVEK on 21-03-2017.
 */

public class Profile_Chat_Group_Recycler_Adapter extends RecyclerView.Adapter<Profile_Chat_Group_Recycler_Adapter.Group_Holder> {


    ArrayList<Choose_Contacts_Single_List> list;
    Context ctx;
    String cur_number,cur_user_id;

    Profile_Chat_Group_Recycler_Adapter(Context ctx, ArrayList<Choose_Contacts_Single_List> list)
    {
        this.list = list;
        this.ctx=ctx;
        /*SharedPreferences sharedpreferences = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(ctx,cur_number);
        Cursor c=obj.get_my_details(obj);
        c.moveToFirst();
        if(c.getCount()>0)
            cur_user_id=c.getString(0);*/
    }
    public class Group_Holder extends RecyclerView.ViewHolder {
        public final View view;
        TextView name,number;
        CircleImageView profile_pic;
        public Group_Holder(View row) {
            super(row);
            this.view = row;
            name=(TextView)row.findViewById(R.id.tv_chose_contacts_single_row_name);
            number=(TextView)row.findViewById(R.id.tv_chose_contacts_single_row_number);
            profile_pic=(CircleImageView)row.findViewById(R.id.iv_chose_contacts_single_row_dp);


        }
    }
    @Override
    public Profile_Chat_Group_Recycler_Adapter.Group_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_chose_contacts_single_row, parent, false);

        return new Group_Holder(itemView);
    }

    @Override
    public void onBindViewHolder(Profile_Chat_Group_Recycler_Adapter.Group_Holder holder, int position) {
        holder.setIsRecyclable(false);
        Choose_Contacts_Single_List issue_1_list = list.get(position);
        holder.name.setText(issue_1_list.get_name());
        holder.number.setText(issue_1_list.get_number());
        fetch_single_profile_pic(issue_1_list.get_number(),holder.profile_pic);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    void fetch_single_profile_pic(final String num,final CircleImageView profile_pic)
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
