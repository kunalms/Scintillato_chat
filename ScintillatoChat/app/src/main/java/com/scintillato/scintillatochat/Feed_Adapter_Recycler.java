package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by VIVEK on 12-03-2017.
 */

public class Feed_Adapter_Recycler extends RecyclerView.Adapter<Feed_Adapter_Recycler.Question_Holder> {

private List<Feed_List> feed_list;

public class Question_Holder extends RecyclerView.ViewHolder {
    public final View view;
    ImageView que_image;
    Feed_List mItem;
    TextView user_name,question,time,tv_like,tv_answer;
    ImageButton like,tag,answer;
    RelativeLayout box;
    CircleImageView profile;
    CardView cardView;
    public Question_Holder(View row) {
        super(row);
        this.view = row;
        answer=(ImageButton)row.findViewById(R.id.bt_question_row_answer);
        like=(ImageButton)row.findViewById(R.id.bt_question_row_like);
        tag=(ImageButton)row.findViewById(R.id.bt_question_row_tag);
        user_name=(TextView) row.findViewById(R.id.tv_question_row_user);
        time=(TextView)row.findViewById(R.id.tv_question_row_time);
        question=(TextView)row.findViewById(R.id.tv_question_row_question);
        tv_like=(TextView)row.findViewById(R.id.tv_question_row_like);
        tv_answer=(TextView)row.findViewById(R.id.tv_question_row_answers);
        profile=(CircleImageView)row.findViewById(R.id.iv_feed_row_profile);
        que_image=(ImageView) row.findViewById(R.id.iv_feed_row_que_image);
        box=(RelativeLayout)row.findViewById(R.id.rl_question_row);
        cardView=(CardView)row.findViewById(R.id.cardview);
    }
}


    private Context ctx;
    private String cur_number,cur_user_id;
    public Feed_Adapter_Recycler(Context ctx, List<Feed_List> feed_list) {
        this.feed_list = feed_list;
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
    public Question_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_row, parent, false);

        return new Question_Holder(itemView);
    }

    @Override
    public void onBindViewHolder(final Question_Holder question_holder, int position) {
      //  final Feed_List question_holder.mItem = feed_list.get(position);
        question_holder.mItem=feed_list.get(position);
        question_holder.setIsRecyclable(false);
        if(question_holder.mItem.getAnonymous().equals("0"))
            question_holder.user_name.setText(question_holder.mItem.getUser());
        else
            question_holder.user_name.setText("Anonymous User");

        String time_enlapsed;
        long minutes=getDateDiff(question_holder.mItem.getmillisec(), TimeUnit.MINUTES);
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
        question_holder.box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //box
            }
        });
        question_holder.tv_like.setText("Likes: "+question_holder.mItem.getLike_count());
        question_holder.tv_answer.setText(" Answers :"+question_holder.mItem.getNo_answer());
        question_holder.question.setText(question_holder.mItem.getQuestion());
        if(question_holder.mItem.getImage_status().equals("1")) {

            Picasso.with(ctx).load("http://scintillato.esy.es/fetch_question_image_id.php?question_id=" + question_holder.mItem.getQuestion_id()).placeholder(question_holder.que_image.getDrawable()).into(question_holder.que_image);
        }
        else
        {
            question_holder.que_image.setVisibility(View.GONE);
        }
        if(question_holder.mItem.getAnonymous().equals("0"))
            Picasso.with(ctx).load("http://scintillato.esy.es/fetch_profile_pic_png_id.php?user_id=" + question_holder.mItem.getUser_id()).placeholder(question_holder.profile.getDrawable()).into(question_holder.profile);
        else
            question_holder.profile.setImageDrawable(ctx.getResources().getDrawable(R.drawable.userprofile100));
        Log.d("status_like1",question_holder.mItem.getLike_status());
        if(question_holder.mItem.getLike_status().equals("0"))
            question_holder.like.setImageResource(R.drawable.like30);
        else
            question_holder.like.setImageResource(R.drawable.likefilled30);

       question_holder.user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(question_holder.mItem.getAnonymous().equals("1"))
                    Toast.makeText(ctx,"Anonymous User",Toast.LENGTH_SHORT).show();
                else {
                    if (question_holder.mItem.getUser_id().equals(cur_user_id)) {
                        Intent i = new Intent(ctx, Self_Profile.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ctx.startActivity(i);
                    } else {
                        Intent i = new Intent(ctx, Profile_Other.class);
                        i.putExtra("user_id", question_holder.mItem.getUser_id());
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ctx.startActivity(i);
                    }
                }
            }
        });
        question_holder.tv_answer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {

                Intent i=new Intent(ctx,Answer_Feed.class);
                i.putExtra("question_id",question_holder.mItem.getQuestion_id());
                i.putExtra("question",question_holder.mItem.getQuestion());
                i.putExtra("user_name",question_holder.mItem.getUser());
                i.putExtra("like_count",question_holder.mItem.getLike_count());
                i.putExtra("answer_count",question_holder.mItem.getNo_answer());
                i.putExtra("like_stat",question_holder.mItem.getLike_status());
                i.putExtra("anonymous",question_holder.mItem.getAnonymous());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
                Log.d("abc","abc");

            }
        });

        question_holder.answer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {

                Intent i=new Intent(ctx,Answer_Community_Question.class);
                i.putExtra("question_id",question_holder.mItem.getQuestion_id());
                i.putExtra("question",question_holder.mItem.getQuestion());
                i.putExtra("user_name",question_holder.mItem.getUser());
                i.putExtra("like_count",question_holder.mItem.getLike_count());
                i.putExtra("answer_count",question_holder.mItem.getNo_answer());
                i.putExtra("like_stat",question_holder.mItem.getLike_status());
                i.putExtra("anonymous",question_holder.mItem.getAnonymous());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Log.d("check",question_holder.mItem.getQuestion_id()+question_holder.mItem.getUser()+question_holder.mItem.getLike_count()+question_holder.mItem.getNo_answer());
                // Toast.makeText(ctx,"click",Toast.LENGTH_SHORT).show();
                ctx.startActivity(i);
                Log.d("abc","abc");

            }
        });

        final Question_Holder holder=question_holder;
        question_holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                like_unlike_question(question_holder.mItem.getQuestion_id(),cur_user_id,question_holder.mItem.getLike_status());
                if(question_holder.mItem.getLike_status().equals("0"))
                {
                    question_holder.mItem.setLike_status("1");
                    holder.like.setImageResource(R.drawable.likefilled30);
                    question_holder.mItem.setLike_count(Integer.parseInt(question_holder.mItem.getLike_count())+1+"");
                    holder.tv_like.setText("Likes:"+question_holder.mItem.getLike_count());
                }
                else
                {
                    question_holder.mItem.setLike_status("0");
                    holder.like.setImageResource(R.drawable.like30);
                    question_holder.mItem.setLike_count(Integer.parseInt(question_holder.mItem.getLike_count())-1+"");
                    holder.tv_like.setText("Likes:"+question_holder.mItem.getLike_count());
                }

            }
        });


        SharedPreferences sharedpreferences = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        String refresh_flag_feed = sharedpreferences.getString("refresh_flag_feed", "0");

        if(refresh_flag_feed.equals("1"))
        {
             sharedpreferences=ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedpreferences.edit();
            editor.putString("refresh_flag_feed","0");
            editor.commit();

            if(position==(getItemCount()-1))
            {
                sharedpreferences=ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
                editor=sharedpreferences.edit();
                editor.putString("refresh_flag_feed","1");
                editor.commit();
            }
        }
        else {

            Animation animation = AnimationUtils.loadAnimation(ctx,
                    R.anim.up_from_bottom);
            question_holder.itemView.startAnimation(animation);
        }


    }

    @Override
    public int getItemCount() {
        return feed_list.size();
    }
    void like_unlike_question(String question_id,String user_id,String status)
    {
        if(status.equals("0"))
        {
            like_question(question_id,user_id);
        }
        else {
            unlike_question(question_id,user_id);
        }
    }
    void like_question(String question_id,String user_id)
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


    public static long getDateDiff(long date_incoming, TimeUnit timeUnit) {
        Date date1=new Date(date_incoming);
        Date date2 = Calendar.getInstance().getTime();
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

}