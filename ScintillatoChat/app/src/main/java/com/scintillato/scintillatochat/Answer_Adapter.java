package com.scintillato.scintillatochat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
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


public class Answer_Adapter extends ArrayAdapter {

    Context ctx;
    private List<Answer_List> list =new ArrayList<Answer_List>();
    String cur_user_id;
    Answer_Adapter(Context context,int resources) {
        super(context, resources);
        ctx = context;
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        String cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(ctx,cur_number);
        Cursor c=obj.get_my_details(obj);
        c.moveToFirst();
        if (c.getCount()>0)
            cur_user_id=c.getString(0);
    }
    public void add(Answer_List object)
    {
        super.add(object);
        list.add(object);
    }

    public void insert(Answer_List obj,int pos)
    {
        super.insert(obj,pos);
        list.add(pos,obj);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        row=convertView;
        final Answer_Holder answer_holder;

        if(row==null)
        {
            LayoutInflater layoutinflator=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutinflator.inflate(R.layout.answer_row,parent,false );
            answer_holder= new Answer_Holder();
            answer_holder.comment=(ImageButton)row.findViewById(R.id.bt_answer_row_comment);
            answer_holder.like=(ImageButton)row.findViewById(R.id.bt_answer_row_like);
            answer_holder.answer=(TextView)row.findViewById(R.id.tv_answer_row_answer);
            answer_holder.username=(TextView) row.findViewById(R.id.tv_answer_row_user);
            answer_holder.time=(TextView)row.findViewById(R.id.tv_answer_row_time);
            answer_holder.tv_like=(TextView)row.findViewById(R.id.tv_answer_row_like);
            answer_holder.tv_comment=(TextView)row.findViewById(R.id.tv_answer_row_comments);
            answer_holder.box=(RelativeLayout)row.findViewById(R.id.rl_answer_row);
            answer_holder.profile=(CircleImageView)row.findViewById(R.id.iv_answer_profile);
            row.setTag(answer_holder);
        }

        else
        {
            answer_holder= (Answer_Holder) row.getTag();
        }

        final Answer_List answer_list =(Answer_List)this.getItem(position);
        //answer_holder.category.setText(answer_list.getCategory());
        answer_holder.username.setText(answer_list.getUser());
        String time_enlapsed;
        long minutes=getDateDiff(answer_list.getmillisec(), TimeUnit.MINUTES);
        if(minutes/(60*24*30*12)>=1)
        {
            int tmp= (int) (minutes/(60*24*30*12));
            if(tmp==1)
                time_enlapsed=(tmp+ " Year ago");
            else
                time_enlapsed=(tmp +" Years ago");
        }
        else if(minutes/(60*24*30)>=1)
        {
            int tmp= (int) (minutes/(60*24*30));
            if(tmp==1)
                time_enlapsed=(tmp+ " Month ago");
            else
                time_enlapsed=(tmp +" Months ago");
        }
        else if(minutes/(60*24*7)>=1)
        {
            int tmp= (int) (minutes/(60*24*7));
            if(tmp==1)
                time_enlapsed=(tmp+ " Week ago");
            else
                time_enlapsed=(tmp +" Weeks ago");
        }
        else if(minutes/(60*24)>=1)
        {
            int tmp= (int) (minutes/(60*24));
            if(tmp==1)
                time_enlapsed=(tmp+ " Day ago");
            else
                time_enlapsed=(tmp +" Days ago");
        }
        else if(minutes/60>=1)
        {
            int tmp= (int) (minutes/60);
            if(tmp==1)
                time_enlapsed=(tmp+ " Hour ago");
            else
                time_enlapsed=(tmp +" Hours ago");
        }
        else
        {
            if(minutes==1)
            {
                time_enlapsed=(minutes+" Minute ago");
            }
            else
                time_enlapsed=(minutes+" Minutes ago");
        }
        answer_holder.time.setText(time_enlapsed);
        answer_holder.tv_comment.setText("Comments:"+answer_list.getComment_count());
        answer_holder.tv_like.setText("Likes:"+answer_list.getLike_count());

        Picasso.with(ctx).load("http://scintillato.esy.es/fetch_profile_pic_png_id.php?user_id=" + answer_list.getUser_id()).placeholder(answer_holder.profile.getDrawable()).into(answer_holder.profile);

        if(answer_list.getLike_stat().equals("0"))
            answer_holder.like.setImageResource(R.drawable.like30);
        else
            answer_holder.like.setImageResource(R.drawable.likefilled30);

        answer_holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(answer_list.getUser_id().equals(cur_user_id))
                {
                    Intent i = new Intent(ctx, Self_Profile.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(i);
                    //((Activity)ctx).overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

                }
                else {
                    Intent i = new Intent(ctx, Profile_Other.class);
                    i.putExtra("user_id", answer_list.getUser_id());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(i);
                   // ((Activity)ctx).overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

                }
            }
        });
        Toast.makeText(ctx,"stats"+answer_list.getLike_count(),Toast.LENGTH_LONG).show();
        answer_holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //like button
                Log.d("datta",answer_list.getUser_id()+" "+answer_list.getQuestion_id());
                like_unlike_question(answer_list.getQuestion_id(),cur_user_id,answer_list.getLike_stat(),answer_list.getAnswer_id());
                if(answer_list.getLike_stat().equals("0"))
                {
                    answer_list.setLike_stat("1");
                    answer_holder.like.setImageResource(R.drawable.likefilled30);
                    answer_list.setLike_count(Integer.parseInt(answer_list.getLike_count())+1+"");
                    answer_holder.tv_like.setText("Likes:"+answer_list.getLike_count());
                }
                else
                {
                    answer_list.setLike_stat("0");
                    answer_holder.like.setImageResource(R.drawable.like30);
                    answer_list.setLike_count(Integer.parseInt(answer_list.getLike_count())-1+"");
                    answer_holder.tv_like.setText("Likes:"+answer_list.getLike_count());
                }

            }
        });
        answer_holder.answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //answer button
            }
        });
        answer_holder.box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //box
            }
        });
        answer_holder.tv_like.setText("Likes: "+answer_list.getLike_count());//mangwana hai.
        answer_holder.answer.setText(answer_list.getAnswer());
        answer_holder.tv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ctx,Comment_Feed_Community.class);
                i.putExtra("answer_id",answer_list.getAnswer_id());
                Log.d("answer_id",answer_list.getAnswer_id());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
               // ((Activity)ctx).overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

            }
        });
        answer_holder.comment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {

                Intent i=new Intent(ctx,Comment_Answer_Community.class);
                i.putExtra("user_id",answer_list.getUser_id());
                i.putExtra("answer_id",answer_list.getAnswer_id());
                i.putExtra("user_name",answer_list.getUser());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
               // ((Activity)ctx).overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);

                Log.d("abc","abc");

            }
        });
        return row;
    }


    void like_unlike_question(String question_id,String user_id,String status,String answer_id)
    {
        if(status.equals("0"))
        {
            like_question(question_id,user_id,answer_id);
        }
        else {

            unlike_question(question_id,user_id,answer_id);
        }
    }
    void like_question(String question_id,String user_id,String answer_id)
    {

        Log.d("here","like");
        if(backGroundTaskRegister!=null)
            backGroundTaskRegister.cancel(true);
        register_url="http://scintillato.esy.es/like_answer_community.php";
        backGroundTaskRegister=new BackGroundTaskRegister();
        backGroundTaskRegister.execute(user_id,question_id,answer_id);

    }
    void unlike_question(String question_id,String user_id,String answer_id)
    {

        Log.d("here","unlike");
        if(backGroundTaskRegister!=null)
            backGroundTaskRegister.cancel(true);
        register_url="http://scintillato.esy.es/unlike_answer_community.php";
        backGroundTaskRegister=new BackGroundTaskRegister();
        backGroundTaskRegister.execute(user_id,question_id,answer_id);
    }
    String register_url;

    int flag;

    BackGroundTaskRegister backGroundTaskRegister;
    class BackGroundTaskRegister extends AsyncTask<String, Void, String> {
        int flag1=1;
        BackGroundTaskRegister()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String user_id=params[0];
            String question_id=params[1];
            String answer_id=params[2];


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                        URLEncoder.encode("question_id","UTF-8")+"="+URLEncoder.encode(question_id,"UTF-8")+"&"+
                        URLEncoder.encode("answer_id","UTF-8")+"="+URLEncoder.encode(answer_id,"UTF-8");

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
                    flag=0;
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
            Log.d("1",flag+"");
            //  Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG);

            if(flag1==0)
            {
                Toast.makeText(ctx,result+"0",Toast.LENGTH_LONG);
            }
            else
            {

                if(flag==1)
                {
                    Toast.makeText(ctx,"done",Toast.LENGTH_LONG);

                    //Toast.makeText(Create_Page_3.this,result,Toast.LENGTH_LONG).show();
                }
            }
        }
        @Override
        protected void onPreExecute() {

        }
    }

    static class Answer_Holder
    {
        TextView username,answer,time,tv_like,tv_comment;
        ImageButton like,comment;
        RelativeLayout box;
        CircleImageView profile;
    }

    public static long getDateDiff(long date_incoming, TimeUnit timeUnit) {
        Date date1=new Date(date_incoming);
        Date date2 = Calendar.getInstance().getTime();
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
}

