package com.dhruv.networkmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dhruv.networkmanager.R;
import com.dhruv.networkmanager.data.entities.Package;
import com.dhruv.networkmanager.fragments.AppUsageFragment;
import com.dhruv.networkmanager.utils.UnitOfMeasurement;

import java.util.List;

public class AppUsageAdapter extends ArrayAdapter<Package> {

    private Context mContext;
    private List<Package> usageList;

    private int mode;
    private long maxUsage;

    public AppUsageAdapter(Context context, List<Package> usageList, int mode) {
        super(context, R.layout.adapter_app_usage, usageList);
        this.usageList = usageList;
        this.mContext = context;
        this.mode = mode;
        if(!usageList.isEmpty())
            calculateMax();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        Package packageItem = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.adapter_app_usage, parent, false);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.appIcon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.appName);
            viewHolder.data = (TextView) convertView.findViewById(R.id.dataUsage);
            viewHolder.dataBar = (ProgressBar) convertView.findViewById(R.id.dataBar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.icon.setImageDrawable(packageItem.getIcon());
        viewHolder.name.setText(packageItem.getName());
        long total = packageItem.getTotalBytes();
        long mobile = packageItem.getMobileBytes();
        long wifi = packageItem.getWifiBytes();

        switch (mode) {
            case AppUsageFragment.MOBILE:
                viewHolder.data.setText(UnitOfMeasurement.usageB(mobile));
                viewHolder.dataBar.setProgress(percent(mobile));
                break;

            case AppUsageFragment.WIFI:
                viewHolder.data.setText(UnitOfMeasurement.usageB(wifi));
                viewHolder.dataBar.setSecondaryProgress(percent(wifi));
                break;

            case AppUsageFragment.BOTH:
                viewHolder.data.setText(UnitOfMeasurement.usageB(total));
                viewHolder.dataBar.setSecondaryProgress(percent(total));
                viewHolder.dataBar.setProgress(percent(mobile));
                break;

        }

        return convertView;
    }

    public void calculateMax() {
        switch (mode) {
            case AppUsageFragment.MOBILE:
                maxUsage = usageList.get(0).getMobileBytes();break;
            case AppUsageFragment.WIFI:
                maxUsage = usageList.get(0).getWifiBytes();break;
            case AppUsageFragment.BOTH:
                maxUsage = usageList.get(0).getTotalBytes();
        }
    }

    private int percent(long used) {
        return (int) (used * 100.0 / maxUsage + 0.5);
    }

    @Override
    public int getCount() {
        return usageList.size();
    }

    static class ViewHolder {
        public ImageView icon;
        public TextView name;
        public TextView data;
        public ProgressBar dataBar;
    }
}
