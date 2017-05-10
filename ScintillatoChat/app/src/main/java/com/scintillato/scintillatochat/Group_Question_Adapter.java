package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
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
import java.util.jar.Pack200;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by adikundiv on 12-01-2017.
 */

public class Group_Question_Adapter extends ArrayAdapter {
    Context ctx;
    private List<Group_Question_List> list =new ArrayList<Group_Question_List>();

    private String cur_user_id,cur_number;
    Group_Question_Adapter(Context context,int resources)
    {
        super(context,resources);
        ctx=context;
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(ctx,cur_number);
        Cursor c=obj.get_my_details(obj);
        c.moveToFirst();
        if (c.getCount()>0)
            cur_user_id=c.getString(0);
    }

    public ArrayList<Group_Question_List> getList()
    {
        return (ArrayList<Group_Question_List>) list;
    }
    public void add(ArrayList<Group_Question_List> object )
    {
        super.add(object);
        list=object;
    }
    public void add(Group_Question_List object)
    {
        super.add(object);
        list.add(object);
    }public void insert(Group_Question_List obj,int pos)
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
        final Question_Holder question_holder;

        if(row==null)
        {
            LayoutInflater layoutinflator=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutinflator.inflate(R.layout.group_question_row,parent,false );
            question_holder= new Question_Holder();
            question_holder.answer=(ImageButton)row.findViewById(R.id.ibtn_group_question_row_answer);
            question_holder.like=(ImageButton)row.findViewById(R.id.ibtn_group_question_row_like);
            question_holder.tag=(ImageButton)row.findViewById(R.id.ibtn_group_question_row_category);
            question_holder.user=(TextView) row.findViewById(R.id.tv_group_question_row_user);
            question_holder.time=(TextView)row.findViewById(R.id.tv_group_questions_row_time);
            question_holder.question=(TextView)row.findViewById(R.id.tv_group_question_row_question);
            question_holder.like_count=(TextView)row.findViewById(R.id.tv_group_question_row_like);
            question_holder.answer_count=(TextView)row.findViewById(R.id.tv_group_question_row_answer);
            question_holder.profile=(CircleImageView)row.findViewById(R.id.iv_group_question_row_profile);
            row.setTag(question_holder);
        }

        else
        {
            question_holder= (Question_Holder)row.getTag();
        }

        final Group_Question_List que =(Group_Question_List)this.getItem(position);
        //question_holder.category.setText(que.getCategory());
        Picasso.with(ctx).load("http://scintillato.esy.es/fetch_profile_pic_png_id.php?user_id=" + que.getUser_id()).placeholder(question_holder.profile.getDrawable()).into(question_holder.profile);
        question_holder.user.setText(que.getUser());
        String time_enlapsed;
        long minutes=getDateDiff(que.getmillisec(), TimeUnit.MINUTES);
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
        question_holder.time.setText(time_enlapsed);
        question_holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //like button
            }
        });
        question_holder.tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tag button
            }
        });
        question_holder.answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //answer button
            }
        });
        question_holder.like_count.setText("Likes: "+que.getLike_count());
        question_holder.answer_count.setText(" Answers :"+que.getNo_answer());
        question_holder.question.setText(que.getQuestion());
        if(que.getLike_status().equals("0"))
            question_holder.like.setImageResource(R.drawable.like30);
        else
            question_holder.like.setImageResource(R.drawable.likefilled30);

        question_holder.answer_count.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {

                Intent i=new Intent(ctx,Answer_Group_Feed.class);
                i.putExtra("question_id",que.getQuestion_id());
                i.putExtra("user_name",que.getUser_name());
                i.putExtra("time",que.getTime());
                i.putExtra("user_id",que.getUser_id());
                i.putExtra("question",que.getQuestion());
                i.putExtra("like_count",que.getLike_count());
                i.putExtra("like_stat",que.getLike_status());
                i.putExtra("answer_count",que.getNo_answer());
                i.putExtra("group_id",que.getGroup_id());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // Toast.makeText(ctx,"click",Toast.LENGTH_SHORT).show();
                ctx.startActivity(i);
                Log.d("abc","abc");

            }
        });

        question_holder.answer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {

                Intent i=new Intent(ctx,Group_Answer.class);
                i.putExtra("question_id",que.getQuestion_id());
                i.putExtra("group_id",que.getGroup_id());
                i.putExtra("user_id",que.getUser_id());
                i.putExtra("question",que.getQuestion());
                i.putExtra("user_name",que.getUser());
                i.putExtra("cur_user_id",cur_user_id);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Toast.makeText(ctx,"click",Toast.LENGTH_SHORT).show();
                ctx.startActivity(i);

            }
        });

        question_holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                like_unlike_question(que.getQuestion_id(),cur_user_id,que.getLike_status(),que.getGroup_id());
                if(que.getLike_status().equals("0"))
                {
                    que.setLike_status("1");
                    question_holder.like.setImageResource(R.drawable.likefilled30);
                    que.setLike_count(Integer.parseInt(que.getLike_count())+1+"");
                    question_holder.like_count.setText("Likes:"+que.getLike_count());
                }
                else
                {
                    que.setLike_status("0");
                    question_holder.like.setImageResource(R.drawable.like30);
                    que.setLike_count(Integer.parseInt(que.getLike_count())-1+"");
                    question_holder.like_count.setText("Likes:"+que.getLike_count());
                }

            }
        });

        return row;
    }
   /* void like_question(String question_id,String user_id)
    {

        Log.d("here","like");
        if(backGroundTaskRegister!=null)
            backGroundTaskRegister.cancel(true);
        register_url="http://scintillato.esy.es/like_question_community.php";
        backGroundTaskRegister=new BackGroundTaskRegister();
        backGroundTaskRegister.execute(user_id,question_id);

    }
    void unlike_question(String question_id,String user_id)
    {

        Log.d("here","unlike");
        if(backGroundTaskRegister!=null)
            backGroundTaskRegister.cancel(true);
        register_url="http://scintillato.esy.es/unlike_question_community.php";
        backGroundTaskRegister=new BackGroundTaskRegister();
        backGroundTaskRegister.execute(user_id,question_id);
    }*/
   void like_unlike_question(String question_id,String user_id,String status,String group_id)
   {
       if(status.equals("0"))
       {
           like_question(question_id,user_id,group_id);
       }
       else {
           unlike_question(question_id,user_id,group_id);
       }
   }
    void like_question(String question_id,String user_id,String group_id)
    {

        Log.d("here","like");
        if(backGroundTaskLike!=null)
            backGroundTaskLike.cancel(true);
        register_url="http://scintillato.esy.es/like_question_group.php";
        backGroundTaskLike=new BackGroundTaskLike();
        backGroundTaskLike.execute(user_id,question_id,group_id);

    }
    void unlike_question(String question_id,String user_id,String group_id)
    {

        Log.d("here","unlike");
        if(backGroundTaskLike!=null)
            backGroundTaskLike.cancel(true);
        register_url="http://scintillato.esy.es/unlike_question_group.php";
        backGroundTaskLike=new BackGroundTaskLike();
        backGroundTaskLike.execute(user_id,question_id,group_id);
    }

    String register_url;



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



            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                        URLEncoder.encode("question_id","UTF-8")+"="+URLEncoder.encode(question_id,"UTF-8");

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
                Toast.makeText(ctx,result,Toast.LENGTH_LONG);
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



    int flag;

    BackGroundTaskLike backGroundTaskLike;
    class BackGroundTaskLike extends AsyncTask<String, Void, String> {
        int flag1=1;
        BackGroundTaskLike()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String user_id=params[0];
            String question_id=params[1];
            String group_id=params[2];



            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                        URLEncoder.encode("question_id","UTF-8")+"="+URLEncoder.encode(question_id,"UTF-8")+"&"+
                        URLEncoder.encode("group_id","UTF-8")+"="+URLEncoder.encode(group_id,"UTF-8");

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
            Log.d("abbbbbbbcccc",result);
            //  Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG);

            if(flag1==0)
            {
                Toast.makeText(ctx,result,Toast.LENGTH_LONG);
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



    static class Question_Holder
    {
        TextView user,time,like_count,answer_count,question;
        ImageButton like,tag,answer;
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
