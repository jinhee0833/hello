package com.helloants.mm.helloants1.adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.helloants.mm.helloants1.R;
import com.helloants.mm.helloants1.activity.content.ContentDetailActivity;

import java.util.List;

/**
 * Created by park on 2016-03-04.
 */
public class ApplicationAdapter extends ArrayAdapter<ContentDetailActivity.AppInfo> {
    private List<ApplicationInfo> appsList = null;
    private List<ContentDetailActivity.AppInfo> appList = null;
    private Context context;
    private PackageManager packageManager;

    public ApplicationAdapter(Context context, int textViewResourceId,List<ContentDetailActivity.AppInfo> appList) {
        super(context, textViewResourceId, appList);
        this.context = context;
        this.appList = appList;
        packageManager = context.getPackageManager();
    }
    @Override
    public int getCount() {
        return ((null != appList) ? appList.size() : 0);
    }
    @Override
    public ContentDetailActivity.AppInfo getItem(int position) {
        return ((null != appList) ? appList.get(position) : null);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (null == view) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.snippet_list, null);
        }
        ContentDetailActivity.AppInfo data = appList.get(position);
        if (null != data) {
            TextView appName = (TextView) view.findViewById(R.id.app_name);

            ImageView iconview = (ImageView) view.findViewById(R.id.app_icon);

            appName.setText(data.appname);
            iconview.setImageDrawable(data.icon);
        }
        return view;
    }
};
