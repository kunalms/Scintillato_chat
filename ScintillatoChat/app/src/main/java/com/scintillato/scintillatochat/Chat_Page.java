package com.scintillato.scintillatochat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;

public class Chat_Page extends AppCompatActivity {

    private RecyclerView chat_RecyclerView;
    private ArrayList<Chat_Page_List> chat_page_list;
    private Chat_Page_Adapter adapter;
    private FloatingActionsMenu fab;
    private String cur_number;
    private Button logout;
    private com.getbase.floatingactionbutton.FloatingActionButton act_1,act_2,act_3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_page);

        logout=(Button)findViewById(R.id.btn_chat_page_logout);

        fab = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        chat_RecyclerView=(RecyclerView)findViewById(R.id.rv_chat_page_feed);
        chat_page_list=new ArrayList<>();
        adapter=new Chat_Page_Adapter(getApplicationContext(),chat_page_list);
        chat_RecyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        chat_RecyclerView.setLayoutManager(mLayoutManager);
        chat_RecyclerView.setItemAnimator(new DefaultItemAnimator());
        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");

        act_1 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.action_a);
        act_1.setColorNormalResId(R.color.cyan);
        act_1.setIcon(R.drawable.privategroup33);
        act_1.setSize(com.getbase.floatingactionbutton.FloatingActionButton.SIZE_MINI);
        act_2= (com.getbase.floatingactionbutton.FloatingActionButton)findViewById(R.id.action_b);
        act_2.setColorNormalResId(R.color.cyan);
        act_2.setIcon(R.drawable.publicgroup33);
        act_2.setSize(com.getbase.floatingactionbutton.FloatingActionButton.SIZE_MINI);
        act_3= (com.getbase.floatingactionbutton.FloatingActionButton)findViewById(R.id.action_c);
        act_3.setColorNormalResId(R.color.cyan);
        act_3.setIcon(R.drawable.singlechat33 );
        act_3.setSize(com.getbase.floatingactionbutton.FloatingActionButton.SIZE_MINI);

        act_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Group_create_contacts.class);
                intent.putExtra("one_to_one_flag","0");
                startActivity(intent);
            }
        });


        act_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Group_create_contacts.class);
                intent.putExtra("one_to_one_flag","2");
                startActivity(intent);
            }
        });
        act_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Group_create_contacts.class);
                intent.putExtra("one_to_one_flag","1");
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences sharedpreferences=getSharedPreferences("User", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedpreferences.edit();
                editor.putString("number","0");
                editor.putInt("flag", 0);
                editor.putInt("contacts_update",0);
                editor.commit();
                //		Contacts_Unregistered_Execute obj=new Contacts_Unregistered_Execute(getApplicationContext(),number);
                //		obj.delete_unreg();
                //		obj.delete_reg();
                My_Details_Execute obj1=new My_Details_Execute(getApplicationContext(),cur_number);
                obj1.delete_muy_details();
                Selected_Memebers_Execute obj2=new Selected_Memebers_Execute(getApplicationContext(),cur_number);
                obj2.delete_selected_members_temp();
                //	Group_Execute obj3=new Group_Execute(getApplicationContext(),number);
                //		obj3.delete_all_recent_chats();
                //		obj3.delete_group_members();
                //		obj3.delete_groups();
                //		obj3.delete_messages();
                Intent i=new Intent(getApplicationContext(),Launch_Activity.class);
                finish();
                startActivity(i);

            }
        });

            chat_RecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), chat_RecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        Chat_Page_List list=chat_page_list.get(position);
                        if(list.get_flag().equals("1")) {
                            Intent i = new Intent(getApplicationContext(),Chat_Message_Single.class );
                            i.putExtra("user_number",list.get_opposite_person_number());
                            startActivity(i);
                        }
                        else {
                            Intent i = new Intent(getApplicationContext(),Message_Chat_Public_Group_Main.class );
                            i.putExtra("group_name",list.get_name());
                            i.putExtra("group_id",list.get_group_id());
                            startActivity(i);
                        }
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
        fetch_chats();


    }
    void fetch_chats()
    {
        chat_page_list.clear();
        adapter.notifyDataSetChanged();
        adapter=new Chat_Page_Adapter(getApplicationContext(),chat_page_list);
        Chat_Database_Execute obj=new Chat_Database_Execute(getApplicationContext(),cur_number);

        Cursor c;
        c=obj.get_recent_chats(obj);

        if(c.getCount()>0)
        {
            c.moveToFirst();
            do {
                if(c.getString(0).equals("1"))//single_chat
                {
                    String user_number=c.getString(2),user_name,message="",count,status="0",send_recieve="-1";
                    Contacts_Unregistered_Execute ob = new Contacts_Unregistered_Execute(getApplicationContext(), cur_number);
                    final String user_exists = ob.number_exists(ob, user_number);
                    if (user_exists.equals("1") == true) {
                        user_name = ob.get_name_message_table(ob, user_number);
                    }
                    else {
                        user_name = user_number;
                    }
                    Cursor c_message=obj.fetch_last_message_chat_single(obj,user_number);
                    if(c_message.getCount()>0)
                    {
                        c_message.moveToFirst();
                        do {
                            message=c_message.getString(1);
                            send_recieve=c_message.getString(3);
                            if(c_message.getString(3).equals("0"))
                            {
                                status=c_message.getString(2);
                            }
                        }while (c_message.moveToNext());
                    }
                    String[] message_count=obj.get_count_unread_message_single(obj,user_number);
                  //  Toast.makeText(getApplicationContext(),"time_single"+c.getString(4),Toast.LENGTH_SHORT).show();
                    Chat_Page_List chatPageList = new Chat_Page_List(user_name,message,user_number,c.getString(0),message_count[0],c.getString(4),status,send_recieve);
                    chat_page_list.add(chatPageList);
                    adapter.notifyDataSetChanged();
                }
                else//group
                {
                    String group_id=c.getString(1);
                    Cursor cursor_group=obj.fetch_group_selected(obj,group_id);
                    if(cursor_group.getCount()>0) {
                        cursor_group.moveToFirst();
                        do {
                       //     Toast.makeText(getApplicationContext(),"time_group"+cursor_group.getString(4),Toast.LENGTH_SHORT).show();
                            Chat_Page_List chatPageList = new Chat_Page_List(cursor_group.getString(1), "message", group_id, c.getString(0), "0", c.getString(4), "0", "0");
                            chat_page_list.add(chatPageList);
                            adapter.notifyDataSetChanged();
                        }while (cursor_group.moveToNext());
                    }
                }
            }while (c.moveToNext());
        }

    }
    private BroadcastReceiver mMessageReceiver_message_single= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("here11","here");
            String code=intent.getStringExtra("code");
            if(code.equals("3")) {
                Log.d("here11","here1");

                chat_page_list.clear();
                adapter.notifyDataSetChanged();
                adapter=new Chat_Page_Adapter(getApplicationContext(),chat_page_list);
                String message = intent.getStringExtra("message");
                String sender = intent.getStringExtra("sender");
                String user_number = intent.getStringExtra("user_name");
                String message_id = intent.getStringExtra("message_id");
                String opposite_message_id=intent.getStringExtra("opposite_message_id");
                adapter.notifyDataSetChanged();
                fetch_chats();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        //this.registerReceiver(mMessageReceiver, new IntentFilter("new_group"));
        //this.registerReceiver(mMessageReceiver_message,new IntentFilter("message"));
        this.registerReceiver(mMessageReceiver_message_single,new IntentFilter("message_single"));
    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
      // this.unregisterReceiver(mMessageReceiver);
       // this.unregisterReceiver(mMessageReceiver_message);
        this.unregisterReceiver(mMessageReceiver_message_single);
    }


}
