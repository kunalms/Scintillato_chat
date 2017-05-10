package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class Profile_Chat_Single_Recycler_Adapter extends RecyclerView.Adapter<Profile_Chat_Single_Recycler_Adapter.Group_Holder> {


    ArrayList<Profile_Chat_Single_Groups_List> list;
    Context ctx;
    String cur_number,cur_user_id;

    Profile_Chat_Single_Recycler_Adapter(Context ctx,ArrayList<Profile_Chat_Single_Groups_List> list)
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
        TextView group_name, group_members;
        ImageView iv_status;
        TextView time,count;
        CircleImageView profile;
        public Group_Holder(View row) {
            super(row);
            this.view = row;
            group_name = (TextView) row.findViewById(R.id.tv_chat_page_row_name);
            group_members = (TextView) row.findViewById(R.id.tv_chat_page_row_message);
            profile = (CircleImageView) row.findViewById(R.id.iv_chat_page_row_dp);
            count=(TextView)row.findViewById(R.id.tv_chat_page_row_count);
            time=(TextView)row.findViewById((R.id.tv_chat_page_row_time));
            iv_status=(ImageView)row.findViewById(R.id.iv_char_page_row_status);

        }
    }


    @Override
    public Profile_Chat_Single_Recycler_Adapter.Group_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_page_row, parent, false);

        return new Group_Holder(itemView);
    }

    @Override
    public void onBindViewHolder(Profile_Chat_Single_Recycler_Adapter.Group_Holder holder, int position) {
        holder.setIsRecyclable(false);
        Profile_Chat_Single_Groups_List issue_1_list = list.get(position);

        holder.group_name.setText(issue_1_list.getGroup_name());
        holder.time.setVisibility(View.INVISIBLE);
        holder.count.setVisibility(View.INVISIBLE);
        holder.iv_status.setVisibility(View.INVISIBLE);
        String group_id = issue_1_list.getGroup_id();
        fetch_group_profile_pic(group_id, holder.profile);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    void fetch_group_profile_pic(final String group_id, final CircleImageView profile_pic) {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Skim Whim");
        File mediaFile;
        String mImageName = "group" + group_id + ".png";

        Log.d("h1", "h1");
        File file = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        Log.d("h1", "h2");
        if (file.exists()) {
            Toast.makeText(ctx, "File exists in /mnt", Toast.LENGTH_SHORT);
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
            Picasso.with(ctx).load(mediaFile).placeholder(profile_pic.getDrawable()).into(profile_pic, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    Picasso.with(ctx).load("http://scintillato.esy.es/fetch_group_profile_png_id.php?group_id=" + group_id).placeholder(profile_pic.getDrawable()).into(profile_pic, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if (profile_pic != null) {
                                profile_pic.buildDrawingCache();
                                Bitmap bmap = profile_pic.getDrawingCache();
                                if (bmap != null)
                                    storeImageGroup(bmap, group_id);
                            }
                        }

                        @Override
                        public void onError() {

                            Toast.makeText(ctx, "error picaso" + group_id, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError() {


                }
            });
        }}


    private void storeImageGroup(Bitmap image, String group_id) {
        File pictureFile = getOutputMediaFileGroup(group_id);

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

    private File getOutputMediaFileGroup(String group_id) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Skim Whim");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        Log.d("herepath1", mediaStorageDir.getAbsolutePath());

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        String mImageName = "group" + group_id + ".png";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

}
