package com.scintillato.scintillatochat;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Following_Fragment extends Fragment {


    public Following_Fragment() {
        // Required empty public constructor
    }
    private ListView listView;
    private Recommend_User_Adapter adapter;
    private ArrayList<Recommend_User_List> recommend_user_lists;
    private String myJSON,last_fetch_user_id="-1",cur_number;
    private ProgressBar progressBar;
    private Context ctx;
    private boolean last=false;
    private BackGroundTaskRegister backGroundTaskRegister;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.activity_recommend_user, container, false);
        ctx=getActivity();
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        //progressBar=(ProgressBar)findViewById(R.id.progress_bar_feed);
        listView=(ListView)view.findViewById(R.id.lv_recommend_user);
        adapter=new Recommend_User_Adapter(ctx,R.layout.recommend_user_row);
        listView.setAdapter(adapter);
        progressBar=(ProgressBar)view.findViewById(R.id.progress_bar_recommended_user);
        //progressBar.setVisibility(View.VISIBLE);
        recommend_user_lists=new ArrayList<>();


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (listView.getLastVisiblePosition() == listView.getAdapter().getCount() -1 &&
                        listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight())
                {
                    if(last==false) {
                        //progressBar.setVisibility(View.VISIBLE);
                        fetch_recommend_user();                    }
                }

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(ctx,"abc",Toast.LENGTH_SHORT).show();
            }
        });
        fetch_recommend_user();
        return view;
    }
    void  fetch_recommend_user()
    {
        String user_id="";
        My_Details_Execute obj=new My_Details_Execute(ctx,cur_number);
        Cursor cursor=obj.get_my_details(obj);
        if (cursor.getCount()>0)
        {
            cursor.moveToFirst();
            user_id=cursor.getString(0);
        }
        last=true;
        backGroundTaskRegister=new BackGroundTaskRegister();
        backGroundTaskRegister.execute(user_id,last_fetch_user_id);

    }

    int flag;

    class BackGroundTaskRegister extends AsyncTask<String, Void, String> {
        int flag1=1;
        BackGroundTaskRegister()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String user_id=params[0];
            String last_fetch_user_id=params[1];

            String register_url="http://scintillato.esy.es/fetch_recommend_user.php";

            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+
                        URLEncoder.encode("last_fetch_user_id","UTF-8")+"="+URLEncoder.encode(last_fetch_user_id,"UTF-8");

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
            //  Toast.makeText(ctx,result,Toast.LENGTH_LONG);
            progressBar.setVisibility(View.GONE);
            if(flag1==0)
            {
                Toast.makeText(ctx,result,Toast.LENGTH_LONG);
            }
            else
            {

                if(flag==1)
                {
                    //               progressBar.setVisibility(View.GONE);

                    myJSON=result;

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


        try {
            jsonObject=new JSONObject(myJSON);
            jsonArray=jsonObject.getJSONArray("result");
            String name,fetch_user_id,follower;
            while(count<jsonArray.length())
            {

                JSONObject JO=jsonArray.getJSONObject(count);
                fetch_user_id=JO.getString("fetch_user_id");
                name=JO.getString("user_name");
                follower=JO.getString("followers");
                Recommend_User_List list=new Recommend_User_List(name,follower,fetch_user_id);
                adapter.add(list);
                adapter.notifyDataSetChanged();
                recommend_user_lists.add(list);
                last=false;
                if (count==jsonArray.length()-1) {
                    if (last_fetch_user_id != fetch_user_id) {
                        last_fetch_user_id = fetch_user_id;
                    }
                    else
                        last=true;
                }

                count++;
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
