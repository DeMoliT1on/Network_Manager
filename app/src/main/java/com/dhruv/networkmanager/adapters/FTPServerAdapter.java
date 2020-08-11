package com.dhruv.networkmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dhruv.networkmanager.R;
import com.dhruv.networkmanager.data.entities.FtpServer;
import com.dhruv.networkmanager.listeners.OnServerClickListener;

import java.util.List;

public class FTPServerAdapter extends RecyclerView.Adapter<FTPServerAdapter.ListViewHolder> {

    List<FtpServer> serverList;
    OnServerClickListener listener;

    public FTPServerAdapter(List<FtpServer> serverList, OnServerClickListener listener) {
        this.serverList = serverList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_ftp_server, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        FtpServer ftpServer = serverList.get(position);
        holder.serverName.setText(ftpServer.getName());
    }


    @Override
    public int getItemCount() {
        return serverList.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView serverName;

        public ListViewHolder(View itemView) {
            super(itemView);
            serverName = itemView.findViewById(R.id.server_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(serverList.get(getAdapterPosition()));
        }
    }

    public void addToList(FtpServer ftpServer) {
        serverList.add(ftpServer);
        notifyItemInserted(getItemCount() - 1);
    }

}
