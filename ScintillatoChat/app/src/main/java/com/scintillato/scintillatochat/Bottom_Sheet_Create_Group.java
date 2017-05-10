package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.app.Activity;


import java.util.ArrayList;

public class Bottom_Sheet_Create_Group extends BottomSheetDialog {

    private Context context;

    public Bottom_Sheet_Create_Group(Context context){
        super(context);

        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        setContentView(view);

        ArrayList<Item> items=new ArrayList<Item>();
        items.add( new Item(R.drawable.ic_launcher, "Create a private group") );
        items.add( new Item(R.drawable.ic_launcher, "Join a public group") );
        items.add(new Item(R.drawable.ic_launcher,"One to One Discussion"));

        ItemAdapter adapter = new ItemAdapter( this.context, items );

        //ListView for the items
       ListView listView = (ListView) view.findViewById(R.id.list_items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                

                switch(position){
                    case 0:
                        Intent intent=new Intent(context,Group_create_contacts.class);
                        intent.putExtra("one_to_one_flag","0");
                        ((Activity)context).startActivity(intent);
    	  		        break;
                    case 1:
                        intent=new Intent(context,Group_create_contacts.class);
                        intent.putExtra("one_to_one_flag","0");
    	  		        ((Activity)context).startActivity(intent);

                        break;
                    case 2:
                        intent=new Intent(context,Group_create_contacts.class);
                        intent.putExtra("one_to_one_flag","1");
                        ((Activity)context).startActivity(intent);

                }

            }
        });

        //GridView for the items
        /*GridView gridView = (GridView) view.findViewById(R.id.grid_items);
        gridView.setAdapter( adapter );
        gridView.setNumColumns(2);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent it = null;

                switch(position){
                    case 0:
                        it = new Intent(Intent.ACTION_VIEW);
                        it.setData(Uri.parse("http://www.whatsapp.com"));
                        break;
                    case 1:
                        it = new Intent(Intent.ACTION_VIEW);
                        it.setData(Uri.parse("http://www.facebook.com"));
                        break;
                    case 2:
                        it = new Intent(Intent.ACTION_VIEW);
                        it.setData(Uri.parse("http://plus.google.com"));
                        break;
                    case 3:
                        it = new Intent(Intent.ACTION_VIEW);
                        it.setData(Uri.parse("http://www.twitter.com"));
                        break;
                    case 4:
                        it = new Intent(Intent.ACTION_VIEW);
                        it.setData(Uri.parse("http://www.youtube.com"));
                        break;
                    case 5:
                        it = new Intent(Intent.ACTION_VIEW);
                        it.setData(Uri.parse("http://www.instagram.com"));
                        break;
                    case 6:
                        it = new Intent(Intent.ACTION_VIEW);
                        it.setData(Uri.parse("http://www.stackoverflow.com"));
                        break;
                }

                context.startActivity(it);
            }
        });*/

    }
}
