package com.hemanth.embeddedrf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.List;

public class SensorAdapter extends BaseAdapter {
    public SensorAdapter(Context context, List<Sensor> itemlist) {
        this.context = context;
        this.itemlist = itemlist;
    }

    private Context context;
    private List<Sensor> itemlist;
    @Override
    public int getCount() {
        return itemlist.size();
    }

    @Override
    public Object getItem(int position) {
        return itemlist.get(0);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


            ViewHolder holder=new ViewHolder();
            convertView=LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
            holder.name=convertView.findViewById(R.id.name);
            holder.value=convertView.findViewById(R.id.text);
            convertView.setTag(holder);


        holder.name.setText(itemlist.get(position).getName());
        holder.value.setText(itemlist.get(position).getValue());
        return convertView;
    }
    class ViewHolder{
         TextView name,value;


    }
}
