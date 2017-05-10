package com.scintillato.scintillatochat;
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


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Ask_Question_Category_Adapter extends ArrayAdapter{

    public List<Ask_questions_category_list> list=new ArrayList<Ask_questions_category_list>();
    Context ctx;
    public Ask_Question_Category_Adapter(Context context, int resource) {
        super(context, resource);
        ctx=context;
        // TODO Auto-generated constructor stub
    }

    public void add(Ask_questions_category_list object)
    {
        super.add(object);
        list.add(object);
    }
    public void clear()
    {
        list.clear();
    }
    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView,ViewGroup parent)
    {
        View row;
        row=convertView;
        final Ask_Category_Holder ask_category_holder;
        if(row==null)
        {
            LayoutInflater layoutinflator=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutinflator.inflate(R.layout.ask_question_category_row,parent,false );
            ask_category_holder= new Ask_Category_Holder();
            ask_category_holder.selected=(CheckBox)row.findViewById(R.id.cb_ask_question_category);
            ask_category_holder.name=(TextView)row.findViewById(R.id.tv_ask_question_category_row_category);


            row.setTag(ask_category_holder);
        }
        else
        {
            ask_category_holder=(Ask_Category_Holder) row.getTag();
        }

        final Ask_questions_category_list issue_1_list=(Ask_questions_category_list)this.getItem(position);
        ask_category_holder.name.setText(issue_1_list.get_name());
        final String name=issue_1_list.get_name();
        ask_category_holder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked)
                {
                    issue_1_list.set_selected(true);
                }
                else
                {
                    issue_1_list.set_selected(false);
                }
            }
        });
        return row;
    }

    static class Ask_Category_Holder
    {
        TextView name;
        CheckBox selected;

    }
}
