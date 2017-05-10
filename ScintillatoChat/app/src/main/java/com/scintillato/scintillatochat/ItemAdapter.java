package com.scintillato.scintillatochat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ItemAdapter extends BaseAdapter {
    protected List<?> bsItems;
    protected LayoutInflater inflater;

    static class ViewHolder{
        ImageView image;
        TextView text;
    }

    public ItemAdapter(Context context, List<Item> bsItems){
        this.bsItems = bsItems;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return bsItems.size();
    }

    @Override
    public Object getItem(int position) {
        return bsItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        Item item = (Item) bsItems.get(position);
        return item.getImage();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Item item = (Item) bsItems.get(position);

        if( convertView == null ){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.bootom_sheet_items, parent, false);
            convertView.setTag( holder );

            holder.image = (ImageView) convertView.findViewById(R.id.bs_image);
            holder.text = (TextView) convertView.findViewById(R.id.bs_text);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.image.setImageResource(item.getImage());
        holder.text.setText(item.getText());

        return convertView;
    }
}
