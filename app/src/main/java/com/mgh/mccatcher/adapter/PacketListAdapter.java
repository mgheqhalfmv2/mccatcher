package com.mgh.mccatcher.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mgh.mccatcher.MyVpnService;
import com.mgh.mccatcher.R;
import com.mgh.mccatcher.network.RakNet;

import java.util.List;

public class PacketListAdapter extends ArrayAdapter<PacketItem> {

    private int RESOURCE_ID;

    private List<PacketItem> items;

    public PacketListAdapter(Context context, int resource, List<PacketItem> items){
        super(context, resource, items);
        this.RESOURCE_ID = resource;
        this.items = items;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Log.d(MyVpnService.TAG, "packetAdapter::getView");
        View itemView;
        ViewHolder holder;

        if(convertView == null){
            itemView = LayoutInflater.from(getContext()).inflate(
                    RESOURCE_ID, parent, false
            );
            holder = new ViewHolder(itemView);
            itemView.setTag(holder);
        }else{
            itemView = convertView;
            holder = (ViewHolder) itemView.getTag();
        }

        PacketItem item = getItem(position);
        if(item != null){
            holder.tvTitle.setText(item.getTitle());
            holder.tvFrom.setText("来自" + item.getFrom());
            holder.tvTo.setText(", 目的" + item.getTo());
            holder.tvSize.setText(", 大小" + item.getSize() + "字节");
        }

        return itemView;
    }

    public void addItem(PacketItem item){
        items.add(item);
        notifyDataSetChanged();
    }

    public void clearAllItems(){
        items.clear();
        notifyDataSetChanged();
    }

    private static class ViewHolder{

        TextView tvTitle;
        TextView tvFrom;

        TextView tvTo;

        TextView tvSize;

        ViewHolder(View itemView){
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvFrom = itemView.findViewById(R.id.tv_from);
            tvTo = itemView.findViewById(R.id.tv_to);
            tvSize = itemView.findViewById(R.id.tv_size);
        }


    }
}