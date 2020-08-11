package com.dhruv.networkmanager.asynchronous;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.dhruv.networkmanager.R;
import com.dhruv.networkmanager.data.entities.FtpFile;
import com.dhruv.networkmanager.listeners.OnListGenerateListener;
import com.dhruv.networkmanager.utils.UnitOfMeasurement;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FTPFilesListTask extends AsyncTask<String,Void,Void> {

    OnListGenerateListener listener;
    FTPClient ftpClient;
    List<FtpFile> files;
    String path;
    SimpleDateFormat format;
    Resources resources;
    AssetManager assets;
    JSONObject jsonObject;
    public FTPFilesListTask(OnListGenerateListener listener, FTPClient ftpClient,Context mContext) {
        this.listener = listener;
        this.ftpClient=ftpClient;
        resources=mContext.getResources();
        assets=mContext.getAssets();
        files=new ArrayList<>();
        format=new SimpleDateFormat("MMM dd,yyyy HH:mm");
    }

    @Override
    protected void onPreExecute() {
        try{
            InputStream is=assets.open("fileType.json");
            int size=is.available();
            byte[] buffer=new byte[size];
            is.read(buffer);
            is.close();
            String json=new String(buffer,"UTF-8");
            jsonObject=new JSONObject(json);
        }catch (Exception e){

        }
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            path=strings[0];
            FtpFile tmp;
            FTPFile[] ftpFiles=ftpClient.listFiles(path);
            for (FTPFile file:ftpFiles){
                tmp=new FtpFile();
                Date date=file.getTimestamp().getTime();
                tmp.setTimeStamp(format.format(date));
                tmp.setName(file.getName());
                if(file.isDirectory()){
                    tmp.setDirectory(true);
                    Drawable icon=resources.getDrawable(R.drawable.fsn_folder);
                    tmp.setIcon(icon);
                }
               else{
                    tmp.setIcon(loadIconFromName(tmp.getName()));
                    tmp.setSize(file.getSize());
                }
                files.add(tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Drawable loadIconFromName(String name){
        if(name.contains(".")){
            try {
                String extension=name.substring(name.lastIndexOf('.'));
                int type=jsonObject.getInt(extension);
                switch (type){
                    case FtpFile.AUDIO :  return resources.getDrawable(R.drawable.fsn_audio);
                    case FtpFile.VIDEO :  return resources.getDrawable(R.drawable.fsn_video);
                    case FtpFile.IMAGE :  return resources.getDrawable(R.drawable.fsn_picture);
                    case FtpFile.TEXT :  return resources.getDrawable(R.drawable.fsn_text);
                    case FtpFile.CODE :  return resources.getDrawable(R.drawable.fsn_code);
                }
            } catch (JSONException e) {
                return resources.getDrawable(R.drawable.fsn_unknown);
            }
        }
        return resources.getDrawable(R.drawable.fsn_unknown);
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        Collections.sort(files);
        listener.onFetched(files);
    }
}
