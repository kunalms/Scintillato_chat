package com.scintillato.scintillatochat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

public class Category_check extends Activity {
    private ProgressDialog loading;
    private Context ctx;
    private ArrayList<Category> categoryArrayList = new ArrayList<Category>();
    private String category_json;
    MyListAdaper dataAdapter= null;
    ArrayList<String> selected_category=new ArrayList<>();
    ListView lv;
    Button myButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);
        ctx=this;
        lv = (ListView) findViewById(R.id.listView1);
        generateListContent();
        dataAdapter=new MyListAdaper(this, R.layout.category_row, categoryArrayList);
        lv.setAdapter(dataAdapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Category_check.this, "List item was clicked at " + position, Toast.LENGTH_SHORT).show();
            }
        });
        myButton = (Button) findViewById(R.id.btn_category_findSelected);
        myButton.setEnabled(false);
        checkButtonClick();
    }

    private void generateListContent() {
        String []cat=getResources().getStringArray(R.array.Categories);

        Category category;
        for (String i: cat)
        {
            category= new Category(i);
            //category=new Category(i,"1");
            categoryArrayList.add(category);
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    private class MyListAdaper extends ArrayAdapter<Category> {

        private int layout;
        private ArrayList<Category> mObjects;
        private MyListAdaper(Context context, int resource, ArrayList<Category> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Category category = categoryArrayList.get(position);
            ViewHolder mainViewholder = null;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.name = (TextView) convertView.findViewById(R.id.tv_category_name);
                viewHolder.follower = (TextView) convertView.findViewById(R.id.tv_follwers);
                viewHolder.button = (Button) convertView.findViewById(R.id.btn_follow);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();

            if(category.isSelected()==true){
                mainViewholder.button.setText("following");
            }
            else if (category.isSelected()==false){
                mainViewholder.button.setText("follow");
            }
            mainViewholder.button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(category.isSelected()==true)
                    {
                        category.setSelected(false);
                    }
                    else
                    {
                        category.setSelected(true);
                    }

                    updateView();
                    Toast.makeText(getContext(), "Button was clicked for list item " + position, Toast.LENGTH_SHORT).show();
                    if(mObjects.size()>=4){
                        myButton.setEnabled(true);
                    }
                    else{
                        myButton.setEnabled(false);
                    }
                }
            });
            mainViewholder.name.setText(category.getName());
            mainViewholder.follower.setText(category.getNo_followers());


            return convertView;
        }
    }
    public class ViewHolder {
        TextView name,follower;
        Button button;
    }
    private void checkButtonClick() {



        myButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");
                JSONArray jsonArray=new JSONArray();

                ArrayList<Category> categoryArrayList = dataAdapter.mObjects;
                for(int i=0;i<categoryArrayList.size();i++){
                    Category category = categoryArrayList.get(i);
                    if(category.isSelected()){
                        selected_category.add(category.getName());
                        responseText.append("\n" + category.getName());
                            JSONObject object=new JSONObject();
                        try {
                            object.put("category", category.getName());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        jsonArray.put(object);
                        //		JSONObject number_object=new JSONObject();
                        //		number_object.put("result",jsonArray);
                        //
                    }

                }

                category_json=jsonArray.toString();
                follow_category(category_json);
                Log.d("category_json",category_json);
                Toast.makeText(getApplicationContext(),
                        category_json, Toast.LENGTH_LONG).show();

            }
        });

    }
    BackGroundTaskFetch backGroundTaskFetch;
    void follow_category(String category_json)
    {
        String user_id = null;

        SharedPreferences sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        String cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(getApplicationContext(),cur_number);
        Cursor cursor=obj.get_my_details(obj);
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            user_id=cursor.getString(0);
        }
        backGroundTaskFetch=new BackGroundTaskFetch();
        backGroundTaskFetch.execute(user_id,category_json);
    }
    int flag;
    class BackGroundTaskFetch extends AsyncTask<String, Void, String>
    {
        int flag1=1;
        BackGroundTaskFetch()
        {
            flag=0;
        }
        @Override
        protected String doInBackground(String... params) {

            String json=params[1];
            String user_id=params[0];
            String register_url="http://scintillato.esy.es/user_category_register.php";


            try{
                URL url=new URL(register_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data= URLEncoder.encode("category_json","UTF-8")+"="+URLEncoder.encode(json,"UTF-8")+"&"+
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
                //	Log.d("line", line);

                if(line.equals(""))
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
            loading.dismiss();
            //	Log.e("1",result+"");
            //	Toast.makeText(ctx,flag+"",Toast.LENGTH_LONG).show();

            if(flag1==0)
            {
                Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
            }
            else
            {

                if(flag==1)
                {

                    loading.dismiss();
                    Intent i=new Intent(getApplicationContext(),SyncActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Toast.makeText(ctx, "Failure Occured!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        @Override
        protected void onPreExecute() {
            loading = ProgressDialog.show(ctx, "Status", "Syncing Contacts...",true,false);

        }
    }

    @Override
    public void onPause()
    {
        if(backGroundTaskFetch!=null)
        {
            backGroundTaskFetch.cancel(true);
        }
        super.onPause();
    }
    @Override
    public void onStop()
    {

        if(backGroundTaskFetch!=null)
        {
            backGroundTaskFetch.cancel(true);
        }
        super.onStop();

    }


    private void updateView(){
        dataAdapter.notifyDataSetChanged();
    }
}