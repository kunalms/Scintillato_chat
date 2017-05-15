package com.scintillato.scintillatochat;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

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
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class Chat_Message_Single extends AppCompatActivity implements Chat_Message_Adapter.Chat_Holder.ClickListener{

    private FloatingActionButton send;
    private ProgressDialog loading;
    private EditText message;
    private RecyclerView chat_RecyclerView;
    private Context ctx;
    private boolean next=true,exist_user=false;
    private boolean cont=true,typing=false,typing_flag=false,first_done=false;
    private  String last_id="0",unseen_message_id;
    private String user_name,user_number,message_id,formattedDate;
    private String message_string,cur_number,numbers_json;
    private Chat_Message_Adapter adapter;
    private ArrayList<Chat_Message_Single_List> chat_list;
    private TextView title,subtitle;
    private RelativeLayout relativeLayout;
    private ImageView back;
    private CircleImageView profile;
    private ArrayList<String> other_message_unseen_list;
    private String other_message_unseen_list_string;
    private Calendar c;
    private int flag_first_message=0,unseen_count=0;
    private SimpleDateFormat df;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;

    BackGroundTaskSend backGroundTaskSend;
    BackGroundFetchMessages backGroundFetchMessages;
   //private BackGroundTaskFetchImage backGroundTaskFetchImage;
   private BackGroundTaskFetch backGroundTaskFetch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_message_single);
        getWindow().setBackgroundDrawableResource(R.drawable.chatback);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.custom_actionbar_layout);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        ctx=this;

        View activityName =getSupportActionBar().getCustomView();
        profile=(CircleImageView)activityName.findViewById(R.id.circleimageview_menu);
        title=(TextView)activityName.findViewById(R.id.tv_custom_actionbar_layout_title);
        subtitle=(TextView)activityName.findViewById(R.id.tv_custom_actionbar_layout_subtitle);
        relativeLayout=(RelativeLayout)activityName.findViewById(R.id.rl_custom_actionbar_layout);
        back=(ImageView)activityName.findViewById(R.id.iv_custom_actionbar_layout_back);
        send=(FloatingActionButton) findViewById(R.id.btn_message_chat_single_send);
        send.setImageResource(R.drawable.ic_send24);
        message=(EditText)findViewById(R.id.et_message_chat_single_message);
        chat_list=new ArrayList<>();
        chat_RecyclerView=(RecyclerView)findViewById(R.id.rv_message_chat_single_chat);
        adapter=new Chat_Message_Adapter(this,chat_list);
        adapter.notifyDataSetChanged();
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        chat_RecyclerView.setLayoutManager(mLayoutManager);
        chat_RecyclerView.setItemAnimator(new DefaultItemAnimator());
        chat_RecyclerView.setAdapter(adapter);
        ((LinearLayoutManager)chat_RecyclerView.getLayoutManager()).setReverseLayout(true);
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        chat_RecyclerView.addItemDecoration(headersDecor);
        chat_RecyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(adapter));
        chat_RecyclerView.post(new Runnable() {
            @Override
            public void run() {
                chat_RecyclerView.invalidateItemDecorations();
                headersDecor.invalidateHeaders();
            }
        });

        SharedPreferences sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number=sharedpreferences.getString("number", "");
        put_status(cur_number,"1");//online
        Intent i=getIntent();
        Bundle b=i.getExtras();
        user_number=b.getString("user_number");

        Contacts_Unregistered_Execute ob = new Contacts_Unregistered_Execute(getApplicationContext(), cur_number);
        final String user_exists = ob.number_exists(ob, user_number);
        if (user_exists.equals("1") == true) {
            user_name = ob.get_name_message_table(ob, user_number);
            exist_user=true;
        }
        else {
            exist_user=false;
            user_name = user_number;
        }
        title.setText(user_name);
        title.setTextColor(this.getResources().getColor(R.color.white));
        subtitle.setTextColor(this.getResources().getColor(R.color.white));
        profile.setImageResource(R.drawable.userprofile100);
        fetch_single_profile_pic(user_number,profile);
        other_message_unseen_list=get_unseen_other_message_id();
       // unseen_count=get_unseen_count();
        unseen_message_id=get_unseen_message_id();
        other_message_unseen_list_string="";
        for(int j=other_message_unseen_list.size()-1;j>=0;j--)
        {
            if(j!=0)
            {
                other_message_unseen_list_string+=other_message_unseen_list.get(j)+",";
            }
            else
            {
                other_message_unseen_list_string+=other_message_unseen_list.get(j);
            }
            Log.d("other",other_message_unseen_list.get(j));
        }

        Toast.makeText(getApplicationContext(),other_message_unseen_list_string,Toast.LENGTH_SHORT).show();
        send_seen_receipt(other_message_unseen_list_string);
        fetch_message_first();
        update_message_seen();
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Profile_Chat_Single.class);
                i.putExtra("user_number",user_number);
                startActivity(i);
                Toast.makeText(getApplicationContext(),"Actionbar clicked",Toast.LENGTH_SHORT).show();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Image clicked",Toast.LENGTH_SHORT).show();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"back button clicked",Toast.LENGTH_SHORT).show();
            }
        });

        message.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                typing=false;
                // you can call or do what you want with your EditText here
                /*if(typing_flag==false) {
                    put_status(cur_number, "1");
                    typing_flag = false;
                }*/
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                flag_first_message++;
                if(typing_flag==false && flag_first_message!=1){
                    typing_flag=true;
                    put_status(cur_number,"2");
                }
                typing=true;
            }
        });

        chat_RecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
                @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                Log.d("here","here");
                if (!recyclerView.canScrollVertically(-1)) {
                    Log.d("here","here1"+next+first_done);

                    if(next==true && first_done==true) {
                        next=false;
                        Log.d("insi","inis1");
                       backGroundFetchMessages=new BackGroundFetchMessages();
                        backGroundFetchMessages.execute();

                    }
                }
                else if (!recyclerView.canScrollVertically(1)) {
                    Log.d("here","here2");
                    //onScrolledToBottom();
                } else if (dy < 0) {
                    Log.d("here","here3");
                  //  onScrolledUp();
                } else if (dy > 0) {
                    Log.d("here","here4");
                  //  onScrolledDown();
                }
            }
        });
        //  fetch_user_status();     //important, to be inserted later

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(message.getText().toString().equals(""))
                {

                }
                else
                {
                    if(exist_user==false)
                    {
                        Contacts_Unregistered_Execute ob = new Contacts_Unregistered_Execute(getApplicationContext(), cur_number);
                        ob.putinfo_reg(ob, user_number, user_number);
                        exist_user=true;
                    }
                    message_string = message.getText().toString();
                    String message_id=save_message(message_string);
                    Log.d("message_id>",message_id);
                    send_message(message_id,message_string,cur_number,user_number);
                    ((LinearLayoutManager)chat_RecyclerView.getLayoutManager()).scrollToPositionWithOffset(0,0);
                }
            }
        });

        if(chat_RecyclerView.getAdapter().getItemCount()>0)
        chat_RecyclerView.smoothScrollToPosition(chat_RecyclerView.getAdapter().getItemCount()-1);
    }
    void send_message(String message_id,String message_string,String sender,String receiver)
    {
        backGroundTaskSend=new BackGroundTaskSend(message_id);
        backGroundTaskSend.execute(message_string,receiver,sender,message_id);

    }


    void send_seen_receipt(String message_id_list)
    {
        BackGroundTaskSentReceiptMultiple backGroundTaskSentReceiptMultiple=new BackGroundTaskSentReceiptMultiple(getApplicationContext());
        backGroundTaskSentReceiptMultiple.execute("1",user_number,cur_number,message_id_list);
    }

    String get_unseen_message_id()
    {
        Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
        String []x=obj.get_count_unread_message_single(obj,user_number);
        unseen_count=Integer.parseInt(x[0]);
        return x[1];
    }
    /*int get_unseen_count()
    {
        Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
        String []x=obj.get_count_unread_message_single(obj,user_number);
        return Integer.parseInt(x[0]);
    }*/

    ArrayList<String> get_unseen_other_message_id()
    {
        Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
        return obj.get_array_other_message_id_unread_message_single(obj,user_number);
    }

    void update_message_seen()
    {
        Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
        obj.update_status_message_single_seen(obj,"1");
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

    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
        return true;
    }

    class BackGroundFetchMessages extends AsyncTask<String, Void, String> {
        int flag1 = 1;

        int flag=0;
        BackGroundFetchMessages() {
            flag = 0;
        }

        @Override
        protected String doInBackground(String... params) {

            try {
               fetch_next_messages();

                flag=1;
                return "1";
            } catch (Exception e) {
                flag1 = 0;
                Log.e("error1",e+"");
                return "Check Internet Connection!";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            //  Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG);
           // loading.dismiss();
            if(flag1==0)
            {

               // fetch_next_messages();
                Toast.makeText(ctx,result,Toast.LENGTH_LONG);
            }
            else
            {
                if(flag==1) {
                   adapter.notifyDataSetChanged();
                    //((LinearLayoutManager)chat_RecyclerView.getLayoutManager()).scrollToPositionWithOffset(0,0);
                    next=true;

                }
            }
        }
        @Override
        protected void onPreExecute() {
         //   loading = ProgressDialog.show(ctx, "Status", "Registering...",true,false);

        }


    }

    void fetch_next_messages()
    {

        Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
        Cursor cr=obj.fetch_message_chat_next_single(obj,user_number,last_id);
        if(cr.getCount()>0)
        {
            cr.moveToFirst();
            do {
                if(cr.getString(4).equals(cur_number))
                {
                    Log.d("time1",cr.getString(3));
                    Chat_Message_Single_List chat_message_single_list=new Chat_Message_Single_List("You",cr.getString(1),cr.getString(4),cr.getString(2),cr.getString(0),cr.getString(6),cr.getString(7),cr.getString(10));
                    chat_message_single_list.set_ismine(true);
                    Log.d("message"+cr.getString(0),cr.getString(1));
                   chat_list.add(chat_message_single_list);
                    //adapter.notifyDataSetChanged();

                    //  adapter.notifyItemInserted(0);
                    //adapter.notifyItemRangeChanged(1, chat_list.size());

                }
                else {
                    Log.d("single_mes",cr.getString(2));

                    Chat_Message_Single_List chat_message_single_list=new Chat_Message_Single_List(cr.getString(1),cr.getString(1),cr.getString(4),cr.getString(2),cr.getString(0),cr.getString(6),cr.getString(7),cr.getString(10));
                    chat_message_single_list.set_ismine(false);
                   chat_list.add(chat_message_single_list);
                    //adapter.notifyDataSetChanged();
                    //  adapter.notifyItemInserted(0);
                    //adapter.notifyItemRangeChanged(1, chat_list.size());
                }
                last_id = cr.getString(0);

            }while (cr.moveToNext());

        }
        SharedPreferences sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedpreferences.edit();

        editor.putString("last_id", last_id);
        editor.commit();

        next=true;
        for(int i=0;i<chat_list.size();i++)
        {
            Log.d("message"+(i+1),chat_list.get(i).getMessage());
        }

    }
    void fetch_message_first()
    {

        Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
        Cursor cr=obj.fetch_message_single_first(obj,user_number);
        if(cr.getCount()>0)
        {
            cr.moveToFirst();
            do {
                if(cr.getString(4).equals(cur_number))
                {
                    Log.d("time1",cr.getString(3));

                    Chat_Message_Single_List chat_message_single_list=new Chat_Message_Single_List("You",cr.getString(1),cr.getString(4),cr.getString(2),cr.getString(0),cr.getString(6),cr.getString(7),cr.getString(10));

                    chat_message_single_list.set_ismine(true);
                    chat_message_single_list.setUnseen(false);
                    chat_list.add(chat_message_single_list);
                }
                else {

                    Log.d("single_mes",cr.getString(2));
                    Chat_Message_Single_List chat_message_single_list=new Chat_Message_Single_List(cr.getString(1),cr.getString(1),cr.getString(4),cr.getString(2),cr.getString(0),cr.getString(6),cr.getString(7),cr.getString(10));
                    chat_message_single_list.set_ismine(false);
                    chat_message_single_list.setUnseen(false);
                    chat_list.add(chat_message_single_list);

                    if(unseen_message_id!=null) {
                        Log.d("message_id", unseen_message_id);

                        if (cr.getString(0).equals(unseen_message_id)) {
                            Log.d("message_idunseen", unseen_message_id);
                            String date_time=get_datetime();
                            Chat_Message_Single_List chat_message_single_list1 = new Chat_Message_Single_List(true, unseen_count + "",date_time);
                            chat_list.add(chat_message_single_list1);
                        }
                    }
                }
                last_id = cr.getString(0);


            }while (cr.moveToNext());

            adapter.notifyDataSetChanged();
            ((LinearLayoutManager)chat_RecyclerView.getLayoutManager()).scrollToPositionWithOffset(0,0);


        }
        SharedPreferences sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedpreferences.edit();

        editor.putString("last_id", last_id);
        editor.commit();
        first_done=true;
        next=true;

        Log.d("here","next="+next+"first_done="+first_done);
    }
    String save_message(String message_string)
    {
        formattedDate=get_datetime();//yyyy-MM-dd HH:mm:ss
        Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
        String message_id=obj.insert_message_single(obj,formattedDate,message_string,"0",cur_number,user_number,user_number,"0","0","0","0");//last three are for image and video and othermeessageid
        obj.insert_message_unsend_single(obj,formattedDate,message_string,"0",cur_number,user_number,user_number,"0","0","0",message_id);//last two are for image and video and othermeessageid
        Chat_Message_Single_List message_chat_list = new Chat_Message_Single_List("You",message_string,cur_number,formattedDate,message_id,user_number,"0","0");
        chat_list.add(0,message_chat_list);
        adapter.notifyDataSetChanged();
        ((LinearLayoutManager)chat_RecyclerView.getLayoutManager()).scrollToPositionWithOffset(0,0);
        message.setText("");
        message_chat_list.set_ismine(true);

        if(obj.recent_chats_single_exists(obj,user_number)==true)
        {
            obj.update_recent_chats_single(obj,user_number,formattedDate);

        }
        else
        {
            obj.insert_recent_chats(obj,"1","-1",user_number,cur_number,formattedDate);
        }
        return message_id;
    }
    String get_datetime()
    {
        java.util.Calendar c;
        java.text.SimpleDateFormat df;
        c = java.util.Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//yyyy-MM-dd
        formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    void fetch_user_status()
    {
        backGroundTaskFetch=new BackGroundTaskFetch();
        backGroundTaskFetch.execute(user_number);
    }
    class BackGroundTaskFetch extends AsyncTask<String, Void, String> {
        int flag1=1,flag;
        BackGroundTaskFetch()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String user_number=params[0];
            String register_url="http://www.scintillato.esy.es/get_status_online.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("user_number","UTF-8")+"="+URLEncoder.encode(user_number,"UTF-8");

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
                    //tv_status.setText("failure");
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
            // loading.dismiss();
            Log.d("1",flag+"");
            Toast.makeText(ctx,result,Toast.LENGTH_LONG);

            if(flag1==0)
            {

                Toast.makeText(ctx,result,Toast.LENGTH_LONG);
            }
            else
            {

                if(flag==1)
                {
                    // decode_status(result);
                }
            }
            // fetch_user_status();
        }
        @Override
        protected void onPreExecute() {

        }
    }

    public void decode_status(String myJSON) {
        int  count = 0;
        Log.d("get_group1",myJSON);
        JSONObject jsonObject;
        JSONArray jsonArray;
        String last_scene,online;
        try {
            jsonObject = new JSONObject(myJSON);
            jsonArray = jsonObject.getJSONArray("result");

            Log.d("length1", jsonArray.length()+"");

            while (count < jsonArray.length()) {
                JSONObject JO = jsonArray.getJSONObject(count);

                last_scene=JO.getString("last_scene");
                online=JO.getString("online");
                if(online.equals("1"))
                {
                    //getSupportActionBar().setSubtitle("Online");
                    subtitle.setText("Online");
                }
                else if(online.equals("2")){
                    //getSupportActionBar().setSubtitle("typing...");
                    subtitle.setText(("Typing..."));
                }
                else
                {
                    //getSupportActionBar().setSubtitle(last_scene);
                    subtitle.setText(last_scene);
                }
                count++;
            }
        }
        catch (Exception e)
        {
            Log.d("error","here");
        }
    }

    private BackGroundTaskInsert backGroundTaskInsert;
    void put_status(String user_number,String status)
    {
        formattedDate=get_datetime();
        if(backGroundTaskInsert!=null)
            backGroundTaskInsert.cancel(true);
        backGroundTaskInsert=new BackGroundTaskInsert();
        backGroundTaskInsert.execute(user_number,status,formattedDate);
    }


    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate (R.menu.contex_menu_chat, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.btn_chat_delete:
                    // Calls getSelectedIds method from ListViewAdapter Class
                    mode.finish();
                    return true;
                case R.id .btn_chat_copy:
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.removeSelection();
            actionMode = null;
        }
    }

    class BackGroundTaskInsert extends AsyncTask<String, Void, String> {
        int flag1=1,flag;

        BackGroundTaskInsert()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String user_number=params[0];
            String status=params[1];
            String date=params[2];
            String register_url="http://www.scintillato.esy.es/update_status_online.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("user_number","UTF-8")+"="+URLEncoder.encode(user_number,"UTF-8")+"&"+
                        URLEncoder.encode("status","UTF-8")+"="+URLEncoder.encode(status,"UTF-8")+"&"+
                        URLEncoder.encode("datetime","UTF-8")+"="+URLEncoder.encode(date,"UTF-8");

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
            // loading.dismiss();
            Log.d("1",flag+"");
            Toast.makeText(ctx,result,Toast.LENGTH_LONG);
            typing_flag=false;
            if(flag1==0)
            {
                Toast.makeText(ctx,result,Toast.LENGTH_LONG);
            }
            else
            {

                if(flag==1)
                {
                    typing_flag=false;
                    put_status(cur_number,"1");
                }
            }
        }
        @Override
        protected void onPreExecute() {
        }
    }

    class BackGroundTaskSend extends AsyncTask<String, Void, String> {
        int flag;
        int flag1=1;
        String message_id;
        BackGroundTaskSend(String message_id)
        {
            this.message_id=message_id;
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String message=params[0];
            String number=params[1];
            String sender=params[2];
            String message_id=params[3];

            String register_url="http://www.scintillato.esy.es/message_send_single.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("message","UTF-8")+"="+URLEncoder.encode(message,"UTF-8")+"&"+
                        URLEncoder.encode("r_mobile_no","UTF-8")+"="+URLEncoder.encode(number,"UTF-8")+"&"+
                        URLEncoder.encode("s_mobile_no","UTF-8")+"="+URLEncoder.encode(sender,"UTF-8")+"&"+
                        URLEncoder.encode("message_id","UTF-8")+"="+URLEncoder.encode(message_id,"UTF-8");
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
                    //Log.d("inside","1");
                    //tv_status.setText(line);

                }
                else
                {
                    //Log.d("outside","1");
                    flag=0;
                    //tv_status.setText("failure");
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
            // loading.dismiss();
            Log.d("1",flag+"");
            Toast.makeText(ctx,result,Toast.LENGTH_LONG);

            if(flag1==0)
            {
                Toast.makeText(ctx,result,Toast.LENGTH_LONG);
            }
            else
            {

                if(flag==1)
                {
                    update_message_status(message_id,"1");
                    for(int i=0;i<chat_list.size();i++)
                    {
                        if(chat_list.get(i).getMessage_id().equals(message_id))
                        {
                            chat_list.get(i).setStatus("1");
                            adapter.notifyDataSetChanged();
                            ((LinearLayoutManager)chat_RecyclerView.getLayoutManager()).scrollToPositionWithOffset(0,0);
                            break;
                        }
                    }
                    Chat_Database_Execute obj=new Chat_Database_Execute(ctx,cur_number);
                    obj.delete_message_unsend_single_selected(message_id);

                }
            }
        }
        @Override
        protected void onPreExecute() {

        }
    }


    void update_message_status(String message_id,String status){
        Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);
        obj.update_status_message_single(obj,message_id,status);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            String code=intent.getStringExtra("code");
            String sender = intent.getStringExtra("sender");
            formattedDate=get_datetime();
            if(code.equals("3") && sender.equals(user_number)) {
                String message = intent.getStringExtra("message");
                String message_id=intent.getStringExtra("message_id");
                String user_name=intent.getStringExtra("user_name");
                String opposite_message_id=intent.getStringExtra("opposite_message_id");
                Chat_Database_Execute obj = new Chat_Database_Execute(getApplicationContext(),cur_number);
                obj.update_status_message_single(obj,message_id,"1");
                BackGroundTaskSentReceipt backGroundTaskSentReceipt=new BackGroundTaskSentReceipt(message_id,getApplicationContext());
                backGroundTaskSentReceipt.execute("1",user_number,cur_number,opposite_message_id);

                if(sender.equals(user_number)) {
                    Chat_Message_Single_List chat_message_single_list = new Chat_Message_Single_List(user_name,message,sender,formattedDate,message_id,sender,"1",opposite_message_id);
                    chat_list.add(0,chat_message_single_list);
                    adapter.notifyDataSetChanged();
                    chat_RecyclerView.smoothScrollToPosition(0);

                }
            }

            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        }
    };
    private BroadcastReceiver mMessageReceiverReceipt = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            String code=intent.getStringExtra("code");
            String sender = intent.getStringExtra("sender");
            formattedDate=get_datetime();
           if(code.equals("4") && sender.equals(user_number)) {

                String message_id=intent.getStringExtra("message_id");
                String status=intent.getStringExtra("status");
               Log.d("status1",status);
                for(int i=0;i<chat_list.size();i++) {
                    if (chat_list.get(i).getMessage_id().equals(message_id)) {
                        if(status.equals("0"))
                            chat_list.get(i).setStatus("2");
                        else
                            chat_list.get(i).setStatus("3");


                        Log.d("status2", chat_list.get(i).getStatus());

                        adapter.notifyDataSetChanged();
                        ((LinearLayoutManager)chat_RecyclerView.getLayoutManager()).scrollToPositionWithOffset(0,0);
                        break;
                    }
                }

            }
            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        }
    };
    private BroadcastReceiver mMessageReceiverMultipleReceipt = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String code=intent.getStringExtra("code");
            String sender = intent.getStringExtra("sender");
            formattedDate=get_datetime();
            if(code.equals("5") && sender.equals(user_number)) {

                String message_id=intent.getStringExtra("message_id");
                String status=intent.getStringExtra("status");
                List<String> other_message_id_list = Arrays.asList(message_id.split(","));
                int count=other_message_id_list.size()-1;
                Log.d("status1",status);
                for(int i=0;i<chat_list.size() && count>=0;i++) {

                    if(chat_list.get(i).getMessage_id()!=null) {
                        if (chat_list.get(i).getMessage_id().equals(other_message_id_list.get(count))) {
                            if (status.equals("0"))
                                chat_list.get(i).setStatus("2");
                            else
                                chat_list.get(i).setStatus("3");
                            Log.d("status2", chat_list.get(i).getStatus());

                            adapter.notifyDataSetChanged();
                            ((LinearLayoutManager) chat_RecyclerView.getLayoutManager()).scrollToPositionWithOffset(0, 0);
                            count--;
                        }
                    }
                }

            }
            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        }
    };


    @Override
    public void onBackPressed()
    {
         put_status(cur_number,"0");//unneccessary
        Intent i=new Intent(getApplicationContext(),Start_Page.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        // finish();
    }
    @Override
    protected void onStop() {


        if(backGroundTaskFetch!=null)
            backGroundTaskFetch.cancel(true);


        super.onStop();
    }
    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(mMessageReceiver, new IntentFilter("message_single"));
        this.registerReceiver(mMessageReceiverReceipt, new IntentFilter("message_single_receipt"));
        this.registerReceiver(mMessageReceiverMultipleReceipt, new IntentFilter("message_single_multiple_receipt"));

    }

    @Override
    protected void onPause() {

        if(backGroundTaskFetch!=null)
            backGroundTaskFetch.cancel(true);

        this.unregisterReceiver(mMessageReceiver);
        this.unregisterReceiver(mMessageReceiverReceipt);
        this.unregisterReceiver(mMessageReceiverMultipleReceipt);


        super.onPause();
    }




}
