package com.helloants.mm.helloants1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.helloants.mm.helloants1.R;

import java.util.ArrayList;

/**
 * Created by JJ on 2016. 5. 13..
 */
public class SpinerAdapter2 extends SpinerAdapter {
    private ArrayList mList;

    public SpinerAdapter2(){
        mList = new ArrayList();
    }

    public void setList(ArrayList list){
        mList = list;
    }

    public ArrayList getList(){
        return mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        ViewHolder holder;

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_bschoice_spinner, parent, false);
            holder = new ViewHolder();
            holder.txtItem = (TextView)convertView.findViewById(R.id.txv_bsname_spinner);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        String name = mList.get(position).toString();
        holder.txtItem.setText(name.substring(0,name.length()-1));
        holder.txtItem.setTextSize(15);
        holder.txtItem.setPadding(20,20,20,20);
        return convertView;
    }

    private class ViewHolder {
        TextView txtItem;
    }
}