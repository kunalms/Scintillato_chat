package com.scintillato.scintillatochat;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_Chat_Single_Adapter extends ArrayAdapter {

    private List<Profile_Chat_Single_Groups_List> list = new ArrayList<Profile_Chat_Single_Groups_List>();

    Context ctx;

    public Profile_Chat_Single_Adapter(Context context, int resource) {
        super(context, resource);
        ctx = context;
        // TODO Auto-generated constructor stub
    }

    public void add(Profile_Chat_Single_Groups_List object) {
        super.add(object);
        list.add(object);
    }

    public void remove(Profile_Chat_Single_Groups_List object) {
        super.remove(object);
        list.remove(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        row = convertView;
        final Group_Holder group_holder;
        if (row == null) {
            LayoutInflater layoutinflator = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutinflator.inflate(R.layout.chat_page_row, parent, false);
            group_holder = new Group_Holder();
            group_holder.group_name = (TextView) row.findViewById(R.id.tv_profile_chat_single_row_name);
            group_holder.group_members = (TextView) row.findViewById(R.id.tv_chat_page_row_message);
            group_holder.profile = (CircleImageView) row.findViewById(R.id.iv_chat_page_row_dp);
            row.setTag(group_holder);
        } else {
            group_holder = (Group_Holder) row.getTag();
        }

        Profile_Chat_Single_Groups_List issue_1_list = (Profile_Chat_Single_Groups_List) this.getItem(position);

        group_holder.group_name.setText(issue_1_list.getGroup_name());
        String group_id = issue_1_list.getGroup_id();
        fetch_group_profile_pic(group_id, group_holder.profile);
        return row;
    }


    static class Group_Holder {
        TextView group_name, group_members;
        CircleImageView profile;
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
