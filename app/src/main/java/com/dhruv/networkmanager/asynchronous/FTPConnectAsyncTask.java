package com.dhruv.networkmanager.asynchronous;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dhruv.networkmanager.R;
import com.dhruv.networkmanager.data.entities.FtpServer;
import com.dhruv.networkmanager.utils.FtpDialog;
import com.dhruv.networkmanager.fragments.FtpFileFragment;
import com.dhruv.networkmanager.fragments.FtpServerFragment;
import com.dhruv.networkmanager.listeners.OnConnectionListener;
import com.dhruv.networkmanager.utils.LoadingDialog;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;

public class FTPConnectAsyncTask extends AsyncTask<Void,Void,Boolean>{
    int reply;
    FTPClient ftpClient;
    LoadingDialog dialog;
    FtpDialog ftpDialog;
    FtpServer ftpServer;
    OnConnectionListener listener;
    Activity activity;

    public FTPConnectAsyncTask(FtpServer ftpServer,OnConnectionListener listener,Activity activity) {
        this.ftpServer = ftpServer;
        this.listener=listener;
        this.activity=activity;
    }

    public FTPConnectAsyncTask(FtpDialog ftpDialog, FtpServer ftpServer, OnConnectionListener listener, Activity activity) {
        this.ftpDialog = ftpDialog;
        this.ftpServer = ftpServer;
        this.listener = listener;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        dialog = new LoadingDialog(activity);
        dialog.start();
        dialog.title.setText("Please wait");
        dialog.msg.setText(R.string.msg);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        ftpClient=new FTPClient();
        try {
            ftpClient.setConnectTimeout(5000);
            ftpClient.connect(ftpServer.getHost(),ftpServer.getPort());
            boolean success;
            if(ftpServer.isAnonymous()){
                success=ftpClient.login("anonymous","password");
            }
            else{
                success=ftpClient.login(ftpServer.getLogin(),ftpServer.getPassword());
            }
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setBufferSize(1024*1024);
            reply=ftpClient.getReplyCode();
            return success;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if(!FTPReply.isPositiveCompletion(reply)){
            Toast.makeText(activity.getApplicationContext(),"Failed connection to "+ftpServer.getHost(),Toast.LENGTH_SHORT).show();
        }
        else{
            if (!success){
                Toast.makeText(activity.getApplicationContext(),"Failed login to server",Toast.LENGTH_SHORT).show();
            }
            else {
                if(ftpDialog!=null)
                    ftpDialog.dismiss();
                if(listener instanceof FtpServerFragment){
                    try {
                        ftpClient.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(listener instanceof FtpFileFragment){
                    listener.onConnect(ftpClient);
                }
                listener.onConnect(ftpServer);
            }
        }
        dialog.end();
    }

}
