package com.scintillato.scintillatochat;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class Group_Question extends Fragment {


    private FloatingActionButton fab;
    private BackGroundTaskRegister backGroundTaskRegister;
    private int flag;
    private ProgressBar progressBar;
    private boolean last;
    private Context ctx;

    private ListView listView_question;
    private String myJSON,last_question_id="-1",cur_user_id,cur_number;
    private Group_Question_Adapter adapter;
    public Group_Question() {
        // Required empty public constructor
    }
    private String group_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.group_question, container, false);
        ctx=getActivity();
        fab=(FloatingActionButton)v.findViewById(R.id.fab_group_question);
        progressBar=(ProgressBar)v.findViewById(R.id.progress_bar_group_questions);
        listView_question=(ListView)v.findViewById(R.id.lv_group_question_questions);
        Intent i = getActivity().getIntent();
        Bundle b = i.getExtras();
        group_id = b.getString("group_id");
        Log.d("group_id11", group_id);
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(ctx,cur_number);
        Cursor c=obj.get_my_details(obj);
        c.moveToFirst();
        if (c.getCount()>0)
            cur_user_id=c.getString(0);

        adapter=new Group_Question_Adapter(getActivity(),R.layout.group_question_row);
        listView_question.setAdapter(adapter);
        fab.setIcon(R.drawable.create_white_24);
        fab.setColorNormalResId(R.color.cyan);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("aaaaa","a");
                Intent i=new Intent(getActivity(),Ask_Question_Group.class);
                i.putExtra("group_id",group_id);
                startActivity(i);
            }
        });
        progressBar.setVisibility(View.GONE);
        fetch_questions();
        listView_question.setOnScrollListener(new AbsListView.OnScrollListener(){
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
            }
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                if (listView_question.getLastVisiblePosition() == listView_question.getAdapter().getCount() -1 &&
                        listView_question.getChildAt(listView_question.getChildCount() - 1).getBottom() <= listView_question.getHeight())
                {
                    if(last==false) {
                        progressBar.setVisibility(View.VISIBLE);
                        fetch_questions();
                    }
                }
            }
        });
        return v;
    }

    void fetch_questions()
    {
        backGroundTaskRegister=new BackGroundTaskRegister();
        Log.d("list_inside_group",group_id+last_question_id);
        backGroundTaskRegister.execute(group_id,last_question_id,cur_user_id);
    }
    class BackGroundTaskRegister extends AsyncTask<String, Void, String> {
        int flag1=1;
        BackGroundTaskRegister()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String group_id=params[0];
            String last_question_id=params[1];
            String user_id=params[2];

            String register_url="http://scintillato.esy.es/fetch_question_group.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("group_id","UTF-8")+"="+URLEncoder.encode(group_id,"UTF-8")+"&"+
                        URLEncoder.encode("last_question_id","UTF-8")+"="+URLEncoder.encode(last_question_id,"UTF-8")+"&"+
                        URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8");


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
                    Log.d("list_inside",line);

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
            progressBar.setVisibility(View.GONE);

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
                    progressBar.setVisibility(View.GONE);

                    myJSON=result;
                    Log.d("list_inside_here","inside"+myJSON);

                    encode_json(myJSON);
                }
            }
        }
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }
    }


    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private int count;
    public void encode_json(String myJSON)
    {
        count=0;

        Log.d("list_inside","inside");

        try {
            jsonObject=new JSONObject(myJSON);
            jsonArray=jsonObject.getJSONArray("result");
            String question_id=null,question,like_count,anonymous,answer_count,question_date,user_id,username,mobile_no,user_name,stat;

            if(jsonArray.length()>0) {
                while (count < jsonArray.length()) {
                    Log.d("list_inside", "inside" + myJSON);


                    JSONObject JO = jsonArray.getJSONObject(count);
                    question = JO.getString("question");
                    question_id = JO.getString("question_id");
                    user_id = JO.getString("user_id");
                    user_name = JO.getString("user_name");
                    username = JO.getString("username");
                    answer_count = JO.getString("answer_count");
                    question_date = JO.getString("question_date");
                    like_count = JO.getString("like_count");
                    anonymous = JO.getString("anonymous");
                    mobile_no = JO.getString("mobile_no");
                    stat=JO.getString("stat");
                    String[] a = {"abc", "bcd"};

                    Group_Question_List list = new Group_Question_List(question_id, question, like_count, anonymous, answer_count, question_date, user_id, username, mobile_no, user_name,group_id,stat);
                    Log.d("list1", list.getQuestion());
                    adapter.add(list);
                    adapter.notifyDataSetChanged();
                    //  feedList.add(list);


                    if (count==jsonArray.length()-1) {
                        if (last_question_id != question_id) {
                            last_question_id = question_id;

                        }
                        else
                            last=true;
                    }
                    count++;
                }
            }


        }
        catch (JSONException e)
        {

        }
    }
    @Override
    public void onPause()
    {
        if(backGroundTaskRegister!=null)
        {
            backGroundTaskRegister.cancel(true);
        }
        super.onPause();
    }
    @Override
    public void onStop()
    {
        if(backGroundTaskRegister!=null)
        {
            backGroundTaskRegister.cancel(true);
        }
        super.onStop();
    }

}
