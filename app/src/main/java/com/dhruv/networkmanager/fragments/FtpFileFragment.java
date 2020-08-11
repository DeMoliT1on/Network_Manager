package com.dhruv.networkmanager.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dhruv.networkmanager.R;
import com.dhruv.networkmanager.activities.FTPServer;
import com.dhruv.networkmanager.adapters.FTPFilesAdapter;
import com.dhruv.networkmanager.adapters.FTPPathAdapter;
import com.dhruv.networkmanager.asynchronous.FTPDownloadTask;
import com.dhruv.networkmanager.data.entities.FtpFile;
import com.dhruv.networkmanager.data.entities.FtpServer;
import com.dhruv.networkmanager.listeners.OnBackPressListener;
import com.dhruv.networkmanager.listeners.OnConnectionListener;
import com.dhruv.networkmanager.listeners.OnListGenerateListener;
import com.dhruv.networkmanager.listeners.OnPathClickListener;
import com.dhruv.networkmanager.asynchronous.FTPConnectAsyncTask;
import com.dhruv.networkmanager.asynchronous.FTPFilesListTask;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FtpFileFragment extends Fragment implements OnConnectionListener, OnListGenerateListener,
        ListView.OnItemClickListener,OnBackPressListener, OnPathClickListener {

    private FtpServer ftpServer;
    private ListView filesListView;
    private RecyclerView pathView;
    private FTPFilesAdapter adapter;
    private FTPPathAdapter pathAdapter;
    private FTPClient ftpClient;
    private String path;
    private AbsListView.MultiChoiceModeListener modeListener;
    private boolean isMultiChoice;
    private List<FtpFile> files;
    private List<FtpFile> selectedList;
    private TextView goBack;
    private FTPServer activity;

    private static final int OPEN_CODE=0;

    public FtpFileFragment(FtpServer ftpServer,FTPServer activity) {
        this.ftpServer = ftpServer;
        this.activity=activity;
        ftpClient=new FTPClient();
        path = "/";
        isMultiChoice=false;
        initActionMode();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        new FTPConnectAsyncTask(ftpServer, this, getActivity()).execute();
        View view = inflater.inflate(R.layout.fragment_ftp_file, container, false);
        filesListView = view.findViewById(R.id.ftpFileList);
        pathView = view.findViewById(R.id.pathView);
        filesListView.setOnItemClickListener(this);
        filesListView.setMultiChoiceModeListener(modeListener);
        filesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        goBack=view.findViewById(R.id.servers);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                path="/";
                onBackPressed();
                getActivity().onBackPressed();
            }
        });
        return view;
    }

    @Override
    public void onConnect(FtpServer ftpServer) {
        TypedValue value = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, value, true);
        pathAdapter = new FTPPathAdapter(ftpServer.getName(), value.resourceId, this);
        pathView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        pathView.setAdapter(pathAdapter);
    }

    @Override
    public void onConnect(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
        new FTPFilesListTask(this, ftpClient, getActivity().getApplicationContext()).execute(path);
    }

    @Override
    public void onFetched(List<FtpFile> files) {
        if (adapter == null) {
            adapter = new FTPFilesAdapter(getContext(), files);
            filesListView.setAdapter(adapter);
        } else {
            adapter.refresh(files);
        }
        this.files=files;
        activity.fileFab.show();
        selectedList=new ArrayList<>();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       if(!isMultiChoice){
           FtpFile ftpFile = adapter.getItem(position);
           if (ftpFile.isDirectory()) {
               String name = ftpFile.getName();
               path = path.concat(name + "/");
               pathAdapter.add(name, path);
               pathView.scrollToPosition(pathAdapter.getItemCount() - 1);
               new FTPFilesListTask(this, ftpClient, getActivity().getApplicationContext()).execute(path);
           }
       }
    }


    @Override
    public boolean onBackPressed() {

        if (path.equals("/")) {
            new Runnable() {
                @Override
                public void run() {
                    try {
                        ftpClient.disconnect();
                    } catch (IOException e) {

                    }
                }
            }.run();
            return true;
        } else {
            path = path.substring(0, path.length() - 1);
            path = path.substring(0, path.lastIndexOf("/") + 1);
            pathAdapter.remove();
            new FTPFilesListTask(this, ftpClient, getActivity().getApplicationContext()).execute(path);
        }
        return false;
    }

    @Override
    public void onPathClicked(String path) {
        this.path = path;
        new FTPFilesListTask(this, ftpClient, getActivity().getApplicationContext()).execute(path);
    }

    private void initActionMode(){
        modeListener=new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if(checked){
                    selectedList.add(files.get(position));
                }
                else{
                    selectedList.remove(files.get(position));
                }
                adapter.check(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.action_mode_menu,menu);
                isMultiChoice=true;
                selectedList.clear();
                activity.fileFab.hide();
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int id=item.getItemId();
                switch (id){
                    case R.id.ftp_download:
                        Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        i.addCategory(Intent.CATEGORY_DEFAULT);
                        startActivityForResult(Intent.createChooser(i, "Choose location"),OPEN_CODE);
                        mode.finish();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                isMultiChoice=false;
                adapter.unCheckAll();
                activity.fileFab.show();
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_CODE
                && resultCode == Activity.RESULT_OK) {
            Uri uri = null;

            if (data != null) {
                uri = data.getData();
                DocumentFile destFile=DocumentFile.fromTreeUri(activity.getApplicationContext(),uri);
                new FTPDownloadTask(ftpClient,activity,selectedList,path,destFile).execute();
            }
        }
    }


}
