package com.dhruv.networkmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dhruv.networkmanager.R;
import com.dhruv.networkmanager.data.entities.FtpFile;
import com.dhruv.networkmanager.utils.UnitOfMeasurement;

import java.text.SimpleDateFormat;
import java.util.List;

public class FTPFilesAdapter extends ArrayAdapter<FtpFile> {

    List<FtpFile> files;
    Context mContext;
    SimpleDateFormat format;

    public FTPFilesAdapter(Context context, List<FtpFile> files) {
        super(context, R.layout.adapter_ftp_file, files);
        this.mContext = context;
        this.files = files;
    }

    public void refresh(List<FtpFile> files) {
        this.files.clear();
        this.files.addAll(files);
        notifyDataSetChanged();
    }

    public void check(int position) {
        FtpFile file=files.get(position);
        if(file.isChecked()){
            files.get(position).setChecked(false);
        }
        else{
            files.get(position).setChecked(true);
        }
        notifyDataSetChanged();
    }

    public void unCheckAll(){
        for(FtpFile file:files){
            file.setChecked(false);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        FtpFile file = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new FTPFilesAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.adapter_ftp_file, parent, false);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.fileIcon);
            viewHolder.check = (ImageView) convertView.findViewById(R.id.fileCheck);
            viewHolder.name = (TextView) convertView.findViewById(R.id.fileName);
            viewHolder.itemSize = (TextView) convertView.findViewById(R.id.itemSize);
            viewHolder.date = (TextView) convertView.findViewById(R.id.dateModified);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FTPFilesAdapter.ViewHolder) convertView.getTag();
        }
        if(file.isChecked()){
            viewHolder.check.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.check.setVisibility(View.INVISIBLE);
        }
        viewHolder.name.setText(file.getName());
        viewHolder.icon.setImageDrawable(file.getIcon());
        viewHolder.date.setText(file.getTimeStamp());
        viewHolder.itemSize.setText(file.isDirectory()?"<dir>": UnitOfMeasurement.usageB(file.getSize()));
        return convertView;
    }

    @Override
    public int getCount() {
        return files.size();
    }

    static class ViewHolder {
        public ImageView icon;
        public ImageView check;
        public TextView name;
        public TextView date;
        public TextView itemSize;
    }
}
