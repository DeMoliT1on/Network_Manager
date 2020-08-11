package com.dhruv.networkmanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dhruv.networkmanager.R;
import com.dhruv.networkmanager.adapters.FTPServerAdapter;
import com.dhruv.networkmanager.data.entities.FtpServer;
import com.dhruv.networkmanager.data.repository.ServerRepository;
import com.dhruv.networkmanager.listeners.OnConnectionListener;
import com.dhruv.networkmanager.listeners.OnServerClickListener;

import org.apache.commons.net.ftp.FTPClient;

import java.util.ArrayList;
import java.util.List;

public class FtpServerFragment extends Fragment implements OnConnectionListener{

    RecyclerView ftpServerList;
    ServerRepository serverRepository;
    FTPServerAdapter adapter;
    List<FtpServer> list=new ArrayList<>();
    OnServerClickListener listener;

    public static FtpServerFragment newInstance(OnServerClickListener listener){
        FtpServerFragment frag=new FtpServerFragment();
        frag.setListener(listener);
        return frag;
    }

    private void setListener(OnServerClickListener listener){
        this.listener=listener;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_ftp_server,container,false);
        ftpServerList=v.findViewById(R.id.ftpServerList);
        serverRepository=new ServerRepository(getContext());
        list=serverRepository.getServers();
        adapter=new FTPServerAdapter(list,listener);
        ftpServerList.setLayoutManager(new GridLayoutManager(getContext(),3));
        ftpServerList.setAdapter(adapter);
        return v;
    }

    @Override
    public void onConnect(FtpServer ftpServer) {
        Toast.makeText(getContext(),"New Server : "+ftpServer.getHost()+" added",Toast.LENGTH_SHORT).show();
        serverRepository.addServer(ftpServer);
        adapter.addToList(ftpServer);
    }

    @Override
    public void onConnect(FTPClient ftpClient) {

    }

}
