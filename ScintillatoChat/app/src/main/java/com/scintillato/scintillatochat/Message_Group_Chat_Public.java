package com.scintillato.scintillatochat;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Message_Group_Chat_Public extends Fragment {


    public Message_Group_Chat_Public() {
        // Required empty public constructor
    }
    private FloatingActionButton send;
    private EditText message;
    private RecyclerView chat;
    private ArrayList<Message_Chat_List> list_chat;
    private boolean next=false,first_done=false;
    private BackGroundFetchMessages backGroundFetchMessages;

    private Context ctx;
    private String group_id,group_name,group_image;
    private String message_string,cur_number,numbers_json;
    private Message_Group_Chat_Public_Adapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.message_chat, container, false);

        super.onCreate(savedInstanceState);
        send = (FloatingActionButton) v.findViewById(R.id.btn_message_chat_send);
        send.setImageResource(R.drawable.ic_send24);
        message = (EditText) v.findViewById(R.id.et_message_chat_message);
        chat = (RecyclerView) v.findViewById(R.id.lv_message_chat_chat);

        Intent i = getActivity().getIntent();
        Bundle b = i.getExtras();
        group_id = b.getString("group_id");
        Log.d("group_id11", group_id);
        group_image = "Unknown";
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        ctx = getActivity();
        Chat_Database_Execute obj=new Chat_Database_Execute(getActivity(),cur_number);
        Cursor c=obj.fetch_group_selected(obj,group_id);
        if(c.getCount()>0)
        {
            c.moveToFirst();
            do {
                group_name=c.getString(1);
            }while (c.moveToNext());
            ((Message_Chat_Public_Group_Main)getActivity()).getSupportActionBar().setTitle(group_name);
        }
        else
        {

        }


        list_chat=new ArrayList<>();
       /* Cursor cr = obj.fetch_group_members(obj, group_id);
        JSONArray jsonarray = new JSONArray();
        if (cr.getCount() > 0) {
            cr.moveToFirst();
            do {
                Log.d("cursor", cr.getString(3) + cr.getString(2));
                if (cr.getString(0).equals(cur_number) == false) {
                    jsonarray.put(cr.getString(0));
                }

            } while (cr.moveToNext());
        }
        //JSONObject number_object=new JSONObject();
        numbers_json = jsonarray + "";
        Log.d("json_members", jsonarray + "");
*/
        Toast.makeText(getActivity(),numbers_json,Toast.LENGTH_SHORT).show();
        adapter = new Message_Group_Chat_Public_Adapter(ctx, list_chat);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        chat.setLayoutManager(mLayoutManager);
        chat.setItemAnimator(new DefaultItemAnimator());
        chat.setAdapter(adapter);
        ((LinearLayoutManager)chat.getLayoutManager()).setReverseLayout(true);

        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(message.getText().toString().equals("")==false) {
                    message_string = message.getText().toString();
                    String message_id = save_message();
                    ((LinearLayoutManager)chat.getLayoutManager()).scrollToPositionWithOffset(0,0);
                    send_message(message_string, message_id);
                }
            }
        });

        chat.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
       fetch_first_messages();
    return  v;
    }

    String get_time()
    {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//yyyy-MM-dd
        String formattedDate = df.format(c.getTime());
        return formattedDate;
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
                    next=true;

                }
            }
        }
        @Override
        protected void onPreExecute() {
            //   loading = ProgressDialog.show(ctx, "Status", "Registering...",true,false);

        }


    }
    String save_message()
    {
        String formattedDate=get_time();
        Chat_Database_Execute obj=new Chat_Database_Execute(ctx,cur_number);
        String message_id=obj.insert_message_group(obj,formattedDate,message_string,"0",cur_number,group_id,"0","0","0");
        obj.insert_unsend_message_group(obj,message_id,formattedDate,message_string,"0",cur_number,group_id,"0","0","0");

        Message_Chat_List message_chat_list=new Message_Chat_List("You",message_string,group_id,cur_number,formattedDate,message_id);
        message_chat_list.set_ismine(true);
        list_chat.add(0,message_chat_list);

        adapter.notifyDataSetChanged();
        message.setText("");
        chat.smoothScrollToPosition(list_chat.size()-1);
        if(obj.recent_chats_group_exists(obj,group_id)==true)
        {
            obj.update_recent_chats_groups(obj,group_id,formattedDate);
            Cursor c=obj.get_recent_chats(obj);
            if(c.getCount()>0)
            {
                //  String[] coloumns={.FLAG,.GROUP_ID,.OPPOSITE_PERSON_NUMBER,.SENDER,.LAST_UPDATED};
                c.moveToFirst();
                do{
                    if(c.getString(0).equals("1"))
                    {
                        Log.d("recent_single",c.getString(2)+""+c.getString(4));
                    }
                    else
                    {
                        Log.d("recent_group",c.getString(1)+""+c.getString(4));
                    }
                }while (c.moveToNext());
            }
        }
        else {
            obj.insert_recent_chats(obj, "0", group_id, "-1", cur_number, formattedDate);
            Toast.makeText(getActivity(),"inserted"+formattedDate,Toast.LENGTH_SHORT).show();
        }
        return message_id;
    }

    private void fetch_next_messages()
    {
            Chat_Database_Execute obj = new Chat_Database_Execute(ctx,cur_number);
            SharedPreferences sharedpreferences = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
            String last_id = sharedpreferences.getString("last_id", "");
            //String last_id=obj.get_last_message_id(obj,group_id);
            Log.d("last_id", last_id);
            Cursor cr=obj.fetch_message_chat_next_group(obj,group_id,last_id);
            if(cr.getCount()>0)
            {
                cr.moveToFirst();
                do {

                    if(cr.getString(4).equals(cur_number))
                    {
                        Message_Chat_List message_chat_list = new Message_Chat_List("You",cr.getString(1),group_id,cr.getString(4),cr.getString(2),cr.getString(0));
                        message_chat_list.set_ismine(true);
                        Log.d("message","id:"+cr.getString(0)+"mes:"+cr.getString(1));
                        list_chat.add(message_chat_list);
                        last_id = cr.getString(0);

                    }
                    else {
                        Message_Chat_List message_chat_list = new Message_Chat_List(cr.getString(4),cr.getString(1),group_id,cr.getString(4),cr.getString(2),cr.getString(0));
                        message_chat_list.set_ismine(false);
                        list_chat.add(message_chat_list);
                        Log.d("message","id:"+cr.getString(0)+"mes:"+cr.getString(1));
                        last_id = cr.getString(0);

                    }

                }while (cr.moveToNext());
            }
            sharedpreferences=getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedpreferences.edit();

            editor.putString("last_id", last_id);
            editor.commit();
            Log.d("last_id_updated", last_id);
    }
    private void fetch_first_messages()
    {
        Chat_Database_Execute obj=new Chat_Database_Execute(ctx,cur_number);
        String last_id="";
        Cursor cr=obj.fetch_message_group_first(obj,group_id);
        if(cr.getCount()>0)
        {
            Log.d("left3","");

            cr.moveToFirst();
            do {

                Log.d("left4","");

                if(cr.getString(4).equals(cur_number))
                {
//0MESSAGE_ID,1MESSAGE,2.DATE_TIME,3.SEND_RECIEVE,4.SENDER,5.GROUP_ID,6.STATUS,7.IMAGE_LOC,8.VIDEO_LOC,9._MEMBER,10.NEW_NAME,11.ADD,12.NAME_CHANGE,13.REMOVE,14.LEFT,15.ICON_CHANGE};

                    Log.d("left2",cr.getInt(14)+"");
                    if(cr.getInt(11)>0)//add
                    {
                        Message_Chat_List message_chat_list=new Message_Chat_List(group_id);
                        message_chat_list.setMember_added(true);
                        message_chat_list.set_member(cr.getString(9));
                        list_chat.add(message_chat_list);
                        adapter.notifyDataSetChanged();
                    }
                    else if(cr.getInt(12)>0)//name_change
                    {
                        Message_Chat_List message_chat_list=new Message_Chat_List(group_id);
                        message_chat_list.setGroup_name_change(true);
                        message_chat_list.setGroup_new_name(cr.getString(10));
                        list_chat.add(message_chat_list);
                        adapter.notifyDataSetChanged();
                    }
                    else if(cr.getInt(13)>0)//remove
                    {
                        Message_Chat_List message_chat_list=new Message_Chat_List(group_id);
                        message_chat_list.setMember_removed(true);
                        message_chat_list.set_member(cr.getString(9));
                        list_chat.add(message_chat_list);
                        adapter.notifyDataSetChanged();
                    }
                    else if(cr.getInt(14)>0)//left
                    {
                        Message_Chat_List message_chat_list=new Message_Chat_List(group_id);
                        message_chat_list.setMember_left(true);
                        message_chat_list.set_member(cr.getString(9));
                        Log.d("left1","left");
                        list_chat.add(message_chat_list);
                        adapter.notifyDataSetChanged();

                    }
                    else if(cr.getInt(15)>0)//icon_change
                    {
                        Message_Chat_List message_chat_list=new Message_Chat_List(group_id);
                        message_chat_list.setImage_icon_change(true);
                        list_chat.add(message_chat_list);
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        Message_Chat_List message_chat_list = new Message_Chat_List("You", cr.getString(1), group_id, cr.getString(4), cr.getString(2), cr.getString(0));
                        message_chat_list.set_ismine(true);
                        list_chat.add(message_chat_list);
                        adapter.notifyDataSetChanged();
                        last_id = cr.getString(0);
                        Log.d("message", "id:" + cr.getString(0) + "mes:" + cr.getString(1));
                    }

                }
                else {
                    Log.d("left2",cr.getInt(14)+"");
                    if(cr.getInt(11)>0)//add
                    {
                        Message_Chat_List message_chat_list=new Message_Chat_List(group_id);
                        message_chat_list.setMember_added(true);
                        message_chat_list.set_member(cr.getString(9));
                        list_chat.add(message_chat_list);
                        adapter.notifyDataSetChanged();
                    }
                    else if(cr.getInt(12)>0)//name_change
                    {
                        Message_Chat_List message_chat_list=new Message_Chat_List(group_id);
                        message_chat_list.setGroup_name_change(true);
                        message_chat_list.setGroup_new_name(cr.getString(10));
                        list_chat.add(message_chat_list);
                        adapter.notifyDataSetChanged();
                    }
                    else if(cr.getInt(13)>0)//remove
                    {
                        Message_Chat_List message_chat_list=new Message_Chat_List(group_id);
                        message_chat_list.setMember_removed(true);
                        message_chat_list.set_member(cr.getString(9));
                        list_chat.add(message_chat_list);
                        adapter.notifyDataSetChanged();
                    }
                    else if(cr.getInt(14)>0)//left
                    {
                        Message_Chat_List message_chat_list=new Message_Chat_List(group_id);
                        message_chat_list.setMember_left(true);
                        Log.d("left1","left");
                        message_chat_list.set_member(cr.getString(9));
                        list_chat.add(message_chat_list);
                        adapter.notifyDataSetChanged();
                    }
                    else if(cr.getInt(15)>0)//icon_change
                    {
                        Message_Chat_List message_chat_list=new Message_Chat_List(group_id);
                        message_chat_list.setImage_icon_change(true);
                        list_chat.add(message_chat_list);
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        Message_Chat_List message_chat_list = new Message_Chat_List(cr.getString(4), cr.getString(1), group_id, cr.getString(4), cr.getString(2), cr.getString(0));
                        message_chat_list.set_ismine(false);
                        list_chat.add(message_chat_list);
                        adapter.notifyDataSetChanged();
                        last_id = cr.getString(0);
                        Log.d("message", "id:" + cr.getString(0) + "mes:" + cr.getString(1));
                    }

                }
                if(chat.getAdapter().getItemCount()>0)
                chat.smoothScrollToPosition(chat.getAdapter().getItemCount()-1);

            }while (cr.moveToNext());
        }

        SharedPreferences sharedpreferences=getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedpreferences.edit();

        editor.putString("last_id", last_id);
        editor.commit();
        next=true;
        first_done=true;

    }
    private BackGroundTaskSend backgroudsend;
    void send_message(String message,String message_id)
    {

        Log.d("g_insert","g_insert");
        backgroudsend=new BackGroundTaskSend(message_id);
        backgroudsend.execute(message,cur_number,group_id);
    }
    int flag;
    class BackGroundTaskSend extends AsyncTask<String, Void, String> {
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
            //String number_json=params[1];
            String sender=params[1];
            String group_id=params[2];


            String register_url="http://www.scintillato.esy.es/message_send_group.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("message","UTF-8")+"="+URLEncoder.encode(message,"UTF-8")+"&"+
                        URLEncoder.encode("s_mobile_no","UTF-8")+"="+URLEncoder.encode(sender,"UTF-8")+"&"+
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
                    for(int i=0;i<list_chat.size();i++)
                    {
                        if(list_chat.get(i).get_message_id().equals(message_id))
                        {
                            list_chat.get(i).setStatus("1");
                            adapter.notifyDataSetChanged();
                            ((LinearLayoutManager)chat.getLayoutManager()).scrollToPositionWithOffset(0,0);
                            break;
                        }
                    }
                    Chat_Database_Execute obj=new Chat_Database_Execute(ctx,cur_number);
                    obj.delete_message_unsend_group_selected(message_id);
                }
            }
        }
        @Override
        protected void onPreExecute() {

        }
    }

    void update_message_status(String message_id,String status){
        Chat_Database_Execute obj=new Chat_Database_Execute(getActivity(),cur_number);
        obj.update_status_message_group(obj,message_id,status);
    }

    /*
    chat.setOnScrollListener(new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {


        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(firstVisibleItem == 0 && listIsAtTop()){

           //     Log.d("last_last_last","last_last");
             //   fetch_next_messages();

            }else{
              //  swipeRefreshLayout.setEnabled(false);
            }
        }
    });
    fetch_first_messages();
    Log.d("here","here");
}
    private boolean listIsAtTop()   {
        if(chat.getChildCount() == 0) return true;
        return chat.getChildAt(0).getTop() == 0;
    }

    private BackGroundTaskSend backgroudsend;
    void send_message(String message,String numbers_json,String message_id)
    {

        Log.d("g_insert","g_insert");
        backgroudsend=new BackGroundTaskSend(message_id);
        backgroudsend.execute(message,numbers_json,cur_number,group_id);
    }
    int flag;
    class BackGroundTaskSend extends AsyncTask<String, Void, String> {
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
            String number_json=params[1];
            String sender=params[2];
            String group_id=params[3];
            Log.d("came22",number_json);


            String register_url="http://www.scintillato.esy.es/message_send_group.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("message","UTF-8")+"="+URLEncoder.encode(message,"UTF-8")/*+"&"+
                        URLEncoder.encode("r_mobile_no","UTF-8")+"="+URLEncoder.encode(number_json,"UTF-8")+"&"+
                        URLEncoder.encode("s_mobile_no","UTF-8")+"="+URLEncoder.encode(sender,"UTF-8")+"&"+
                        URLEncoder.encode("group_id","UTF-8")+"="+URLEncoder.encode(group_id,"UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();

                OS.close();
*/

    //Must unregister onPause()

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String code=intent.getStringExtra("code");
            String group_id1 = intent.getStringExtra("group_id");

            Calendar c = Calendar.getInstance();
            System.out.println("Current time => "+c.getTime());

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//yyyy-MM-dd
            String formattedDate = df.format(c.getTime());
            if(code.equals("1") && group_id.equals(group_id1)==true) {
                String message = intent.getStringExtra("message");
                String sender = intent.getStringExtra("sender");
                String message_id=intent.getStringExtra("message");
                String sender_name;
                Contacts_Unregistered_Execute obj1=new Contacts_Unregistered_Execute(getActivity(),cur_number);
                sender_name=obj1.get_name_message_table(obj1,sender);
                Message_Chat_List message_chat_list = new Message_Chat_List(sender_name, message, group_id, sender, formattedDate,message_id);
                list_chat.add(message_chat_list);
                adapter.notifyDataSetChanged();
                ((LinearLayoutManager)chat.getLayoutManager()).scrollToPositionWithOffset(0,0);
            }
            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        }
    };


    @Override
    public void onPause() {

        ctx.unregisterReceiver(mMessageReceiver);


        super.onPause();
    }
    @Override
    public void onStop() {


        super.onStop();
    }
    @Override
    public void onResume() {
        super.onResume();
        ctx.registerReceiver(mMessageReceiver, new IntentFilter("message_group"));
    }
}
