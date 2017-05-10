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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home_Page_Adapter extends ArrayAdapter{

	private List<Chat_Page_List> list=new ArrayList<Chat_Page_List>();

	Context ctx;
	public Home_Page_Adapter(Context context, int resource) {
		super(context, resource);
		ctx=context;
		// TODO Auto-generated constructor stub
	}

	public void add(Chat_Page_List object)
	{
		super.add(object);
		list.add(object);
	}
	public void remove(Chat_Page_List object)
	{
		super.remove(object);
		list.remove(object);
	}
	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override 
	public Object getItem(int position)
	{
		return list.get(position);
	}

	@Override 
	public View getView(int position, View convertView,ViewGroup parent)
	{
		View row;
		row=convertView;
		 final Home_Page_holder home_page_holder;
		if(row==null)
		{
			LayoutInflater layoutinflator=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row=layoutinflator.inflate(R.layout.home_page_row,parent,false );
			home_page_holder= new Home_Page_holder();
			home_page_holder.name=(TextView)row.findViewById(R.id.tv_home_page_row_name);
			home_page_holder.message=(TextView)row.findViewById(R.id.tv_home_page_row_message);
			home_page_holder.count=(TextView)row.findViewById(R.id.tv_home_page_row_count);
			home_page_holder.time=(TextView)row.findViewById((R.id.tv_home_page_row_time));
			home_page_holder.dp=(CircleImageView)row.findViewById(R.id.iv_home_page_row_dp);
			row.setTag(home_page_holder);
		}
		else
		{
			home_page_holder=(Home_Page_holder)row.getTag();
		}
	
		Chat_Page_List issue_1_list=(Chat_Page_List)this.getItem(position);

		if(issue_1_list.get_flag().equals("1"))
		{

            fetch_group_profile_pic(issue_1_list.get_group_id(),home_page_holder.dp);
		}
		else
		{
            fetch_single_profile_pic(issue_1_list.get_opposite_person_number(),home_page_holder.dp);
		}
		home_page_holder.name.setText(issue_1_list.get_name());	
		home_page_holder.message.setText(issue_1_list.get_messaage());
		home_page_holder.count.setText(issue_1_list.getMessage_count());
		String incoming_date= getdatemilli(issue_1_list.getmillisec());
		String today_date=gettoday();
		String yesterday =getyesterday();
		home_page_holder.time.setText(incoming_date);
		if(incoming_date.equals(today_date))
		{
			home_page_holder.time.setText(getdatemilli_hour(issue_1_list.getmillisec()));
		}
		else if(incoming_date.equals(yesterday))
		{
			home_page_holder.time.setText("yesterday");
		}
		else
		{
			home_page_holder.time.setText(incoming_date);
		}

		return row;
	}
	int flag;

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
        Log.d("h1","h4"+num);

      /*  String number_trunc = num.substring(1);
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
        });*/

    }
    void fetch_group_profile_pic(final String group_id,final CircleImageView profile_pic)
    {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),"Skim Whim");
        File mediaFile;
        String mImageName="group"+group_id +".png";

        Log.d("h1","h1");
        File file = new File(mediaStorageDir.getPath()+File.separator+mImageName);
        Log.d("h1","h2");
        if(file.exists()){
            Toast.makeText(ctx, "File exists in /mnt", Toast.LENGTH_SHORT);
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
            Picasso.with(ctx).load(mediaFile).placeholder(profile_pic.getDrawable()).into(profile_pic,new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    Picasso.with(ctx).load("http://scintillato.esy.es/fetch_group_profile_png_id.php?group_id=" + group_id).placeholder(profile_pic.getDrawable()).into(profile_pic, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if(profile_pic!=null) {
                                profile_pic.buildDrawingCache();
                                Bitmap bmap = profile_pic.getDrawingCache();
                                if(bmap!=null)
                                    storeImageGroup(bmap, group_id);
                            }
                        }

                        @Override
                        public void onError() {

                            Toast.makeText(ctx,"error picaso"+group_id,Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError() {


                }
            });
        }

        /*
        Picasso.with(ctx).load("http://scintillato.esy.es/fetch_group_profile_png_id.php?group_id=" + group_id).placeholder(profile_pic.getDrawable()).into(profile_pic, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                if(profile_pic!=null) {
                    profile_pic.buildDrawingCache();
                    Bitmap bmap = profile_pic.getDrawingCache();
                    if(bmap!=null)
                        storeImageGroup(bmap, group_id);
                }
            }

            @Override
            public void onError() {

                Toast.makeText(ctx,"error picaso"+group_id,Toast.LENGTH_SHORT).show();
            }
        });
*/
    }


    private void storeImageGroup(Bitmap image,String group_id) {
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
    private  File getOutputMediaFileGroup(String group_id){
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
	static class Home_Page_holder
	{
		TextView name,message,count,time;
		CircleImageView dp;

	}

	String getdatemilli_hour(long milli)
	{
		Date date= new Date(milli);

		DateFormat df;
		df = new SimpleDateFormat("HH:mm");
		String dat=df.format(date);
		return String.valueOf(dat);
	}

	String getdatemilli(long milli)
	{
		Date date= new Date(milli);

		DateFormat df;
		df = new SimpleDateFormat("dd/MM/yyyy");
		String dat=df.format(date);
		return String.valueOf(dat);
	}
	String gettoday()
	{
		Date today = Calendar.getInstance().getTime();
		DateFormat df;
		df = new SimpleDateFormat("dd/MM/yyyy");
		String dat=df.format(today);
		return  String.valueOf(dat);
	}
	String getyesterday()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		Date yesterday = cal.getTime();
		DateFormat df;
		df = new SimpleDateFormat("dd/MM/yyyy");
		String dat=df.format(yesterday);
		return  String.valueOf(dat);
	}
}
