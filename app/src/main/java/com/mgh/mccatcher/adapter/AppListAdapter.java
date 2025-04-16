package com.mgh.mccatcher.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.mgh.mccatcher.R;

import java.util.List;

public class AppListAdapter extends ArrayAdapter<PackageInfo> {

    private int RESOURCE_ID;
    private final PackageManager pm;

    public AppListAdapter(Context context, int resource, List<PackageInfo> apps) {
        super(context, resource, apps);
        pm = context.getPackageManager();
        this.RESOURCE_ID = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView;
        ViewHolder holder;
        // 加载子项布局
        if (convertView == null) {
            itemView = LayoutInflater.from(getContext())
                    .inflate(RESOURCE_ID, parent, false);
            holder = new ViewHolder(itemView);
            itemView.setTag(holder);// 将其保存至itemView中，下次可直接取出, 防止多次调用findViewById
        }else{
            itemView = convertView;// 之前已加载的子项布局缓存
            holder = (ViewHolder) itemView.getTag();
        }

        PackageInfo appInfo = getItem(position);
        if (appInfo != null) {
            holder.icon.setImageDrawable(appInfo.applicationInfo.loadIcon(pm));
            holder.name.setText(appInfo.applicationInfo.loadLabel(pm));
            holder.pkg.setText(appInfo.packageName);
            holder.version.setText("版本: " + appInfo.versionName);
        }

        return itemView;
    }

    private static class ViewHolder{
        ImageView icon;
        TextView name;
        TextView pkg;
        TextView version;

        public ViewHolder(View itemView){
            icon = itemView.findViewById(R.id.app_icon);
            name = itemView.findViewById(R.id.app_name);
            pkg = itemView.findViewById(R.id.app_package);
            version = itemView.findViewById(R.id.app_version);
        }
    }
}