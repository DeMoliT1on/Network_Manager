package com.dhruv.networkmanager.adapters;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dhruv.networkmanager.R;
import com.dhruv.networkmanager.data.entities.FtpPath;
import com.dhruv.networkmanager.listeners.OnPathClickListener;

import java.util.ArrayList;
import java.util.List;

public class FTPPathAdapter extends RecyclerView.Adapter<FTPPathAdapter.PathViewHolder> {

    private List<FtpPath> paths;
    private int resId;
    private int count;
    private OnPathClickListener listener;
    public FTPPathAdapter(String name, int resId, OnPathClickListener listener) {
        paths = new ArrayList<>();
        count=1;
        paths.add(new FtpPath(0, "/", name));
        this.resId=resId;
        this.listener=listener;
    }

    public void add(String name, String path) {
        paths.add(count, new FtpPath(count, path, name));
        notifyItemInserted(count);
        notifyItemChanged(count-1);
        count++;
    }

    public void remove(){
        count--;
        paths.remove(count);
        notifyItemRemoved(count);
        notifyItemChanged(count-1);
    }

    public void removeFrom(int position){
        position++;
        paths.subList(position,count).clear();
        notifyItemRangeRemoved(count,count-position);
        count=position;
        notifyItemChanged(count-1);
    }

    @NonNull
    @Override
    public PathViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.adapter_ftp_path, parent, false);
        return new PathViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PathViewHolder holder, int position) {
        FtpPath item = paths.get(position);
        holder.name.setText(item.getName());

        if (position == 0) {
            holder.arrow.setVisibility(View.GONE);
        }
        else{
            holder.arrow.setVisibility(View.VISIBLE);
        }
        if (position == getItemCount() - 1) {
            holder.name.setBackgroundResource(0);
            holder.name.setTypeface(null, Typeface.BOLD);
        } else {
            holder.name.setBackgroundResource(resId);
            holder.name.setTypeface(null, Typeface.NORMAL);
        }
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    public class PathViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView arrow;
        public TextView name;

        public PathViewHolder(@NonNull View itemView) {
            super(itemView);
            arrow = itemView.findViewById(R.id.path_arrow);
            name = itemView.findViewById(R.id.path_name);
            name.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position=getAdapterPosition();
            FtpPath item=paths.get(position);
            String path=item.getPath();
            listener.onPathClicked(path);
            removeFrom(position);
        }
    }

}
