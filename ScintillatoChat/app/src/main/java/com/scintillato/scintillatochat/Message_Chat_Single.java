package com.scintillato.scintillatochat;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class Message_Chat_Single extends AppCompatActivity {
    private FloatingActionButton send;
    private EditText message;
    private StickyListHeadersListView chat;
    private Context ctx;
    private boolean next=true;
    private boolean top=false,cont=true,typing=false,typing_flag=false;
    private  String last_id="0";
    private String user_name,user_number,message_id,formattedDate;
    private String message_string,cur_number,numbers_json;
    private Message_Chat_Single_Adapter adapter;

    private TextView title,subtitle;
    private RelativeLayout relativeLayout;
    private ImageView back;
    private CircleImageView profile;
    private Calendar c;
    private SimpleDateFormat df;
 //   private Button temp_btn;
    private BackGroundTaskFetchImage backGroundTaskFetchImage;
    private BackGroundTaskFetch backGroundTaskFetch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_chat_single);
        getWindow().setBackgroundDrawableResource(R.drawable.chatback);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.custom_actionbar_layout);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        View activityName =getSupportActionBar().getCustomView();
        profile=(CircleImageView)activityName.findViewById(R.id.circleimageview_menu);
        title=(TextView)activityName.findViewById(R.id.tv_custom_actionbar_layout_title);
        subtitle=(TextView)activityName.findViewById(R.id.tv_custom_actionbar_layout_subtitle);
        relativeLayout=(RelativeLayout)activityName.findViewById(R.id.rl_custom_actionbar_layout);
        back=(ImageView)activityName.findViewById(R.id.iv_custom_actionbar_layout_back);

        send=(FloatingActionButton) findViewById(R.id.btn_message_chat_single_send);
        send.setImageResource(R.drawable.ic_send24);
        message=(EditText)findViewById(R.id.et_message_chat_single_message);
        chat=(StickyListHeadersListView)findViewById(R.id.lv_message_chat_single_chat);
        adapter=new Message_Chat_Single_Adapter(getApplicationContext(), R.layout.message_chat_single_row);
        chat.setAdapter(adapter);
    //    temp_btn=(Button)findViewById(R.id.tem_btn);
       // chat.requestLayout();
        c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//yyyy-MM-dd
        formattedDate = df.format(c.getTime());

        SharedPreferences sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number=sharedpreferences.getString("number", "");
        Intent i=getIntent();
        Bundle b=i.getExtras();
        user_number=b.getString("user_number");
        put_status(cur_number,"1");//online
       /* temp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Profile_Chat_Single.class);
                i.putExtra("user_number",user_number);
                startActivity(i);
            }
        });*/
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
                if(typing_flag==false){
                    typing_flag=true;
                    put_status(cur_number,"2");
                }
                typing=true;
            }
        });
        Contacts_Unregistered_Execute ob=new Contacts_Unregistered_Execute(getApplicationContext(),cur_number);
        String user_exists=ob.number_exists(ob,user_number);
        if(user_exists.equals("1")==true) {
            user_name=ob.get_name_message_table(ob,user_number);
        }
        else {
            user_name = user_number;
        }
        title.setText(user_name);
        title.setTextColor(this.getResources().getColor(R.color.white));
        subtitle.setTextColor(this.getResources().getColor(R.color.white));
        profile.setImageResource(R.drawable.userprofile100);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        ctx=this;

        Contacts_Unregistered_Execute obj=new Contacts_Unregistered_Execute(getApplicationContext(),cur_number);
        Cursor c=obj.getinfo_reg(obj);

        chat.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        chat.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = chat.getCheckedItemCount();
                mode.setTitle(checkedCount + " Selected");
                adapter.toggleSelection(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.contex_menu_chat, menu);
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
                        SparseBooleanArray selected = adapter.getSelectedIds();
                        // Captures all selected ids with a loop
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                Message_Chat_Single_List selecteditem = (Message_Chat_Single_List) adapter.getItem(selected.keyAt(i));
                                // Remove selected items following the ids
                                adapter.remove(selecteditem);
                            }
                        }
                        mode.finish();
                        return true;
                    case R.id .btn_chat_copy:
                        String texttocopy="";
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                        SparseBooleanArray selected1 = adapter.getSelectedIds();
                        for (int i = 0; i < selected1.size(); i++) {
                            if (selected1.valueAt(i)) {
                                Message_Chat_Single_List selecteditem = (Message_Chat_Single_List) adapter.getItem(selected1.keyAt(i));
                                String appendtext = selecteditem.get_messaage();
                                texttocopy+=appendtext+"\n";
                            }
                        }
                        ClipData clip = ClipData.newPlainText("copied text", texttocopy);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(),"Text copied",Toast.LENGTH_SHORT).show();
                        mode.finish();
                        return true;
                        
                    default:
                        return false;
                }

            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                adapter.removeSelection();

            }
        });

      //  fetch_user_status();     //important, to be inserted later
        send.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(message.getText().toString().equals("")==false) {
                    Contacts_Unregistered_Execute ob = new Contacts_Unregistered_Execute(getApplicationContext(), cur_number);
                    String user_exists = ob.number_exists(ob, user_number);
                    if (user_exists.equals("1") == true) {
                        user_name = ob.get_name_message_table(ob, user_number);
                    }
                    else {
                        ob.putinfo_reg(ob, user_number, user_number);
                        user_name = user_number;
                    }
                    message_string = message.getText().toString();
                    message_id = save_message();

                    /*Group_Execute obj = new Group_Execute(getApplicationContext(), cur_number);
                    formattedDate=get_datetime();
                    if (obj.recent_chats_single_exists(obj, user_number) == true)
                        obj.update_recent_chats(obj, "0", user_number, "0", formattedDate, message_string, "0");
                    else {
                        obj.putinfo_recentchats(obj, "0", user_number, formattedDate, "0", message_string, "0");
                    }
                    send_message(message_id);*/
                }

            }
        });
        chat.setTranscriptMode(View.OVER_SCROLL_NEVER);
        chat.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    // check if we reached the top or bottom of the list
                    View v = chat.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the top:

                        if(chat.getCount()==0)
                            fetch_first_messages();
                        else {
                            Log.d("last_id","herre");
                            if(next==true) {
                                next=false;
                                //int index = chat.getFirstVisiblePosition()+10;
                          //      Parcelable state = chat.onSaveInstanceState();
                                fetch_next_messages();
                            //    chat.onRestoreInstanceState(state);
                                //chat.setSelectionFromTop(index, offset);

                            }
                        }
                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem){
                    View v =  chat.getChildAt(totalItemCount-1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the bottom:
                        return;
                    }
                }
            }
        });

    }

    void fetch_user_status()
    {
        backGroundTaskFetch=new BackGroundTaskFetch();
        backGroundTaskFetch.execute(user_number);
    }
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private int count;
    private boolean listIsAtTop()   {
        if(chat.getChildCount() == 0) return true;
        return chat.getChildAt(0).getTop() == 0;
    }
    private void fetch_first_messages()
    {
       /* Group_Execute obj=new Group_Execute(getApplicationContext(),cur_number);
        //String last_id=obj.get_last_message_id_single(obj,user_number);
        //Log.d("last_id",last_id);
        Cursor cr=obj.get_message_chat_first_single(obj,user_number);
        Log.d("single_mes1",cr.getCount()+"");

        if(cr.getCount()>0)
        {
            cr.moveToLast();
            last_id = cr.getString(0);
            do {
                Log.d("single_mes",cr.getString(2));
                if(cr.getString(2).equals(cur_number))
                {
                    Log.d("time1",cr.getString(3));

                    Message_Chat_Single_List message_chat_list = new Message_Chat_Single_List("You", cr.getString(1), cr.getString(4), cr.getString(2), cr.getString(3), cr.getString(0));
                    message_chat_list.set_ismine(true);

                    adapter.add(message_chat_list);
                    adapter.notifyDataSetChanged();
                 //   chat.setSelection(adapter.getCount() - 1);

                }
                else {
                    Log.d("single_mes",cr.getString(2));

                    Message_Chat_Single_List message_chat_list = new Message_Chat_Single_List(cr.getString(2), cr.getString(1), cr.getString(4), cr.getString(2), cr.getString(3), cr.getString(0));
                    message_chat_list.set_ismine(false);
                    adapter.add(message_chat_list);
                    adapter.notifyDataSetChanged();
                   // chat.setSelection(adapter.getCount() - 1);
                }

            }while (cr.moveToPrevious());
            top=true;
        }
        Log.d("last_id1",last_id);*/

        SharedPreferences sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedpreferences.edit();

        editor.putString("last_id", last_id);
        editor.commit();

    }
    private void fetch_next_messages()
    {
     /*   Group_Execute obj=new Group_Execute(getApplicationContext(),cur_number);
        //String last_id=obj.get_last_message_id_single(obj,user_number);
        //Log.d("last_id2",last_id);
        Log.d("last_id2",last_id);

        Cursor cr=obj.get_message_chat_next_single(obj,user_number,last_id);
        Log.d("single_mes3",cr.getCount()+"");

        if(cr.getCount()>0)
        {
            cr.moveToLast();
            last_id=cr.getString(0);
            do {
                Log.d("single_mes",cr.getString(2));
                if(cr.getString(2).equals(cur_number))
                {
                    Log.d("single_mes1",cr.getString(2));

                    Message_Chat_Single_List message_chat_list = new Message_Chat_Single_List("You", cr.getString(1), cr.getString(4), cr.getString(2), cr.getString(3), cr.getString(0));
                    message_chat_list.set_ismine(true);
                    adapter.insert(message_chat_list,0);
                    adapter.notifyDataSetChanged();
                //    chat.setSelection(adapter.getCount() - 1);

                }
                else {
                    Log.d("single_mes2",cr.getString(2));

                    Message_Chat_Single_List message_chat_list = new Message_Chat_Single_List(cr.getString(2), cr.getString(1), cr.getString(4), cr.getString(2), cr.getString(3), cr.getString(0));
                    message_chat_list.set_ismine(false);
                    adapter.insert(message_chat_list,0);
                    adapter.notifyDataSetChanged();
                 //   chat.setSelection(adapter.getCount() - 1);
                }
            }while (cr.moveToPrevious());
            top=true;
            Log.d("last_id3",last_id);

            next=true;
        }


        SharedPreferences sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedpreferences.edit();

        editor.putString("last_id", last_id);
        editor.commit();
*/
    }

    String get_datetime()
    {
        c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//yyyy-MM-dd
        formattedDate = df.format(c.getTime());
        return formattedDate;
    }
    String save_message()
    {
        formattedDate=get_datetime();
      /*  Group_Execute obj=new Group_Execute(getApplicationContext(),cur_number);

        String message_id1=obj.putinfo_messages(obj,formattedDate,message_string,"1","1",cur_number,"0","0",user_number,"0");
        obj.putinfo_messages_unsend(obj,formattedDate,message_string,"1","1",cur_number,"0","0",user_number,"0",message_id1);
        Message_Chat_Single_List message_chat_list = new Message_Chat_Single_List("You",message_string,user_number,cur_number,formattedDate,message_id1);
        adapter.add(message_chat_list);
        adapter.notifyDataSetChanged();
        message.setText("");
        message_chat_list.set_ismine(true);
        return message_id1;*/
      return null;

    }
    BackGroundTaskRegister backGroundTaskRegister;
    void send_message(String message_id)
    {
        formattedDate=get_datetime();
        backGroundTaskRegister=new BackGroundTaskRegister(message_id);
        backGroundTaskRegister.execute(message_string,user_number,cur_number,formattedDate);
        Log.d("numbers",user_number+" "+cur_number);

    }
    int flag;
    class BackGroundTaskRegister extends AsyncTask<String, Void, String> {

        int flag1=1;
        String message_id;
        BackGroundTaskRegister(String message_id)
        {
            this.message_id=message_id;
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String message=params[0];
            String number=params[1];
            String sender=params[2];
            String date=params[3];

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
                 /*   Group_Execute obj=new Group_Execute(ctx,cur_number);

                    boolean a=obj.deletemessage_unsend(message_id);
                    if(a==true)
                    {
                        Toast.makeText(getApplicationContext(),message_id+" deleted",Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();*/
                }
            }
        }
        @Override
        protected void onPreExecute() {

        }
    }
    class BackGroundTaskFetch extends AsyncTask<String, Void, String> {
        int flag1=1;
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


    class BackGroundTaskInsert extends AsyncTask<String, Void, String> {
        int flag1=1;
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
    void update_message_status(String message_id,String status){
        /*Group_Execute obj=new Group_Execute(getApplicationContext(),cur_number);
        obj.update_messasge_status(obj,message_id,status);
        Toast.makeText(getApplicationContext(),"updated"+message_id,Toast.LENGTH_LONG).show();*/
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String code=intent.getStringExtra("code");
            formattedDate=get_datetime();
            if(code.equals("3")) {
                String message = intent.getStringExtra("message");
                String sender = intent.getStringExtra("sender");
                String message_id=intent.getStringExtra("message_id");
                String user_name=intent.getStringExtra("user_name");
                if(sender.equals(user_number)) {
                    Message_Chat_Single_List message_chat_list = new Message_Chat_Single_List(user_name, message, sender, sender, formattedDate, message_id);
                    adapter.add(message_chat_list);
                    adapter.notifyDataSetChanged();
                }
            }
            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        }
    };
    @Override
    public void onBackPressed()
    {
       // put_status(cur_number,"0");//unneccessary
        Intent i=new Intent(getApplicationContext(),Start_Page.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        // finish();
    }


    void fetch_profile_pic(String user_number)
    {
        Log.d("profile_pic41",user_number);
        backGroundTaskFetchImage=new BackGroundTaskFetchImage();
        backGroundTaskFetchImage.execute(user_number);
    }
    class BackGroundTaskFetchImage extends AsyncTask<String, Void, String>
    {
        String number;
        int flag1=1,flag;
        BackGroundTaskFetchImage()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String user_number=params[0];
            this.number=params[0];
            String register_url="http://scintillato.esy.es/fetch_user_profile_pic1.php";


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
                Log.d("get_group", line);

                if(line.equals("")==true)
                {
                    flag=0;
                }
                else
                {
                    flag=1;
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
            //	Log.e("1",result+"");
            //	Toast.makeText(ctx,flag+"",Toast.LENGTH_LONG).show();

            if(flag1==0)
            {

                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
            }
            else
            {

                if(flag==1)
                {
                    String profile_pic_string=fetch_profile_string(result);
                    Bitmap bitmap_profile_pic=getProfileImage(profile_pic_string);
                    //set picture
                    Contacts_Unregistered_Execute obj=new Contacts_Unregistered_Execute(getApplicationContext(),cur_number);
                    obj.update_profile_pic_registered(obj,profile_pic_string,number);
                    //  user_image.setImageBitmap(BitmapFactory.decodeFile(bitmap_profile_pic));
                    storeImage(bitmap_profile_pic,number);
                    Log.d("profile_pic4",profile_pic_string);

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        @Override
        protected void onPreExecute() {
        }
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
            Log.d("here1","here1");
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        Log.d("here2","here2");
        // Create a media file name
        File mediaFile;
        String mImageName=number +".png";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
    public String fetch_profile_string(String myJSON) {
        int  count = 0;
        Log.d("get_group1",myJSON);
        JSONObject jsonObject;
        JSONArray jsonArray;
        String profile_pic="";
        try {
            jsonObject = new JSONObject(myJSON);
            jsonArray = jsonObject.getJSONArray("result");

            Log.d("length1", jsonArray.length()+"");

            while (count < jsonArray.length()) {
                JSONObject JO = jsonArray.getJSONObject(count);

                profile_pic=JO.getString("profile_pic");

                count++;
            }
        }
        catch (Exception e)
        {
            Log.d("error","here");
        }
        return profile_pic;
    }

    public Bitmap getProfileImage(String u_profile_pic)
    {
        byte[] decodedString = Base64.decode(u_profile_pic, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_single_menu,menu);
        return true;
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
    }

    @Override
    protected void onPause() {

        if(backGroundTaskFetch!=null)
            backGroundTaskFetch.cancel(true);

        this.unregisterReceiver(mMessageReceiver);


        super.onPause();
    }


}
