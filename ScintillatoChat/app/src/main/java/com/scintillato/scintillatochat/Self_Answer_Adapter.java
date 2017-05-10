package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
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
import java.util.List;
import java.util.jar.Pack200;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by adikundiv on 12-01-2017.
 */

public class Self_Answer_Adapter extends ArrayAdapter {
    Context ctx;
    private List<Self_Answer_List> list =new ArrayList<Self_Answer_List>();
    private String cur_user_id,cur_number;
    private Bitmap bitmap_profile_pic;
    Self_Answer_Adapter(Context context,int resources)
    {
        super(context,resources);
        ctx=context;
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(ctx,cur_number);
        Cursor c=obj.get_my_details(obj);
        c.moveToFirst();
        if(c.getCount()>0)
            cur_user_id=c.getString(0);

        c=obj.get_my_profile_pic(obj);
        if(c.getCount()>0) {
            c.moveToFirst();
            bitmap_profile_pic=getProfileImage(c.getString(0));
            //user_image.setImageBitmap(BitmapFactory.decodeFile(c.getString(0)));
            Log.d("profile_pic2",c.getString(0));
            //.setImageBitmap(bitmap_profile_pic);
        }
    }

    public Bitmap getProfileImage(String u_profile_pic)
    {
        byte[] decodedString = Base64.decode(u_profile_pic, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
    public ArrayList<Self_Answer_List> getList()
    {
        return (ArrayList<Self_Answer_List>) list;
    }
    public void add(ArrayList<Self_Answer_List> object )
    {
        super.add(object);
        list=object;
    }
    public void add(Self_Answer_List object)
    {
        super.add(object);
        list.add(object);
    }public void insert(Self_Answer_List obj,int pos)
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
        final Answer_Holder Answer_Holder;

        if(row==null)
        {
            LayoutInflater layoutinflator=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutinflator.inflate(R.layout.self_answer_row,parent,false );
            Answer_Holder= new Answer_Holder();
            Answer_Holder.comment=(ImageButton)row.findViewById(R.id.btn_answer_row_comment);
            Answer_Holder.like=(ImageButton)row.findViewById(R.id.bt_self_answer_row_like);
            Answer_Holder.user_name=(TextView) row.findViewById(R.id.tv_self_answer_row_user);
            Answer_Holder.answer_time=(TextView)row.findViewById(R.id.tv_self_answer_row_time);
            Answer_Holder.question=(TextView)row.findViewById(R.id.tv_self_answer_row_question);
            Answer_Holder.tv_like_count=(TextView)row.findViewById(R.id.tv_self_answer_row_like);
            Answer_Holder.tv_answer=(TextView)row.findViewById(R.id.tv_self_answer_row_answer);
            Answer_Holder.profile=(CircleImageView)row.findViewById(R.id.iv_self_answer_row_profile);
            Answer_Holder.tv_answer_count=(TextView)row.findViewById(R.id.tv_self_answer_row_question_answers);
            Answer_Holder.tv_comment_count=(TextView)row.findViewById(R.id.tv_self_answer_row_comments);
            Answer_Holder.answer_image=(ImageView)row.findViewById(R.id.iv_self_answer_row_answer_image);

            row.setTag(Answer_Holder);
        }

        else
        {
            Answer_Holder= (Answer_Holder)row.getTag();
        }

        final Self_Answer_List que =(Self_Answer_List)this.getItem(position);
        //Answer_Holder.category.setText(que.getCategory());
        Answer_Holder.user_name.setText(que.getUser_name());
        Answer_Holder.answer_time.setText(que.getAnswer_time());

        if(que.getUser_id().equals(cur_user_id)==true)
        {
            Log.d("herehere","herehe2");
            Answer_Holder.profile.setImageBitmap(bitmap_profile_pic);
        }
        else
        {
            Log.d("herehere","herehe1");
            Picasso.with(ctx).load("http://scintillato.esy.es/fetch_profile_pic_png_number.php?user_id=" + que.getUser_id()).placeholder(Answer_Holder.profile.getDrawable()).into(Answer_Holder.profile);
        }
        Answer_Holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //like button
            }
        });


        Answer_Holder.tv_answer.setText(que.getAnswer());
        Answer_Holder.tv_like_count.setText("Likes: "+que.getAnswer_like_count());
        Answer_Holder.tv_answer_count.setText(" Answers :"+que.getAnswer_count());
        Answer_Holder.tv_comment_count.setText("Comments:"+que.getComment_count());
        Answer_Holder.question.setText(que.getQuestion());
        Answer_Holder.profile.setImageResource(R.drawable.ic_launcher);

        Log.d("status_like1",que.getLike_stat());
        if(que.getAnswer_image_status().equals("0"))
            Answer_Holder.answer_image.setVisibility(View.GONE);
        else
            Picasso.with(ctx).load("http://scintillato.esy.es/fetch_answer_image_answer_id.php?answer_id=" + que.getAnswer_id()).placeholder(Answer_Holder.answer_image.getDrawable()).into(Answer_Holder.answer_image);

        if(que.getAnswer_like_count().equals("0"))
            Answer_Holder.like.setImageResource(R.drawable.like30);
        else
            Answer_Holder.like.setImageResource(R.drawable.likefilled30);

        Answer_Holder.tv_answer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {

                Intent i=new Intent(ctx,Answer_Feed.class);
                i.putExtra("question_id",que.getQuestion_id());
                i.putExtra("question",que.getQuestion());
                i.putExtra("user_name",que.getUser_name());
                i.putExtra("like_count",que.getAnswer_like_count());
                i.putExtra("answer_count",que.getAnswer_count());
                i.putExtra("like_stat",que.getLike_stat());
                i.putExtra("anonymous","0");

                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // Toast.makeText(ctx,"click",Toast.LENGTH_SHORT).show();
                ctx.startActivity(i);
                Log.d("abc","abc");

            }
        });

        Answer_Holder.comment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {

                Intent i=new Intent(ctx,Comment_Answer_Community.class);
                i.putExtra("user_id",que.getUser_id());
                i.putExtra("answer_id",que.getAnswer_id());
                i.putExtra("user_name",que.getUser_name());

                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                ctx.startActivity(i);
                Log.d("abc","abc");

            }
        });
        Answer_Holder.tv_comment_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ctx,Comment_Feed_Community.class);
                i.putExtra("answer_id",que.getAnswer_id());
                Log.d("answer_id",que.getAnswer_id());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
            }
        });

        Answer_Holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                like_unlike_question(que.getQuestion_id(),cur_user_id,que.getLike_stat(),que.getAnswer_id());
                if(que.getLike_stat().equals("0"))
                {
                    que.setLike_stat("1");
                    Answer_Holder.like.setImageResource(R.drawable.likefilled30);
                    que.setAnswer_like_count(Integer.parseInt(que.getAnswer_like_count())+1+"");
                    Answer_Holder.tv_like_count.setText("Likes:"+que.getAnswer_like_count());
                }
                else
                {
                    que.setLike_stat("0");
                    Answer_Holder.like.setImageResource(R.drawable.like30);
                    que.setAnswer_like_count(Integer.parseInt(que.getAnswer_like_count())-1+"");
                    Answer_Holder.tv_like_count.setText("Likes:"+que.getAnswer_like_count());
                }

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
        TextView user_name,question,answer_time,tv_like_count,tv_answer,tv_answer_count,tv_comment_count;
        ImageButton like,comment;
        CircleImageView profile;
        ImageView answer_image;
    }
}
