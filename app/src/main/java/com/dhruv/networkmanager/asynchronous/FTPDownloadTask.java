package com.dhruv.networkmanager.asynchronous;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import com.dhruv.networkmanager.data.entities.FtpFile;
import com.dhruv.networkmanager.utils.ProgressDialog;
import com.dhruv.networkmanager.utils.UnitOfMeasurement;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class FTPDownloadTask extends AsyncTask<Void, Integer, Void> {

    private FTPClient ftpClient;
    private Context context;
    private AppCompatActivity activity;
    private List<FtpFile> files;
    private String source;
    private DocumentFile destFile;
    private byte[] BUFFER;
    private ProgressDialog progressDialog;
    private String currentFile;
    private long totalBytes;
    private String strTotalBytes;
    private long currentRead;
    private long totalRead;
    private long currentTotal;
    private String strCurrentTotal;

    public FTPDownloadTask(FTPClient ftpClient, AppCompatActivity activity, List<FtpFile> files, String source, DocumentFile destFile) {
        this.ftpClient = ftpClient;
        this.files = files;
        this.source = source;
        this.destFile = destFile;
        this.context = activity;
        this.activity = activity;
        BUFFER = new byte[1024 * 1024];
        totalBytes = 0;
        totalRead = 0;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(activity);
        progressDialog.start();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            for (FtpFile file : files) {
                if (progressDialog.isCancelled()) {
                    cancel(true);
                    return null;
                }
                if (file.isDirectory()) {
                    calculateFiles(source + file.getName() + "/");
                } else {
                    totalBytes += file.getSize();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        strTotalBytes = UnitOfMeasurement.usageB(totalBytes);
        publishProgress(0);
        for (FtpFile file : files) {
            if (progressDialog.isCancelled()) {
                cancel(true);
                return null;
            }
            if (file.isDirectory()) {
                DocumentFile dir = destFile.createDirectory(file.getName());
                downloadDirectory(source + file.getName() + "/", dir);
            } else {
                currentFile = file.getName();
                currentTotal = file.getSize();
                publishProgress(1);
                DocumentFile createdFile = destFile.createFile(null, file.getName());
                downloadFile(source + file.getName(), createdFile.getUri());
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        progressDialog.end();
        Toast.makeText(context, "Download completed successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCancelled(Void aVoid) {
        Toast.makeText(context, "Download cancelled", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        switch (values[0]) {
            case 0:
                progressDialog.initialise("Downloading");
                return;
            case 1:
                strCurrentTotal = UnitOfMeasurement.usageB(currentTotal);
            case 2:
                int currentPercent = percent(currentRead, currentTotal);
                int totalPercent = percent(totalRead, totalBytes);
                progressDialog.update(currentFile,
                        UnitOfMeasurement.usageB(currentRead) + "/" + strCurrentTotal,
                        UnitOfMeasurement.usageB(totalRead) + "/" + strTotalBytes,
                        currentPercent,
                        totalPercent);

        }
    }


    private void calculateFiles(String source) {
        if (progressDialog.isCancelled()) {
            cancel(true);
            return;
        }
        try {
            System.out.println(source);
            FTPFile[] files = ftpClient.listFiles(source);
            for (FTPFile file : files) {
                if (file.isDirectory()) {
                    calculateFiles(source + file.getName() + "/");
                } else {
                    totalBytes += file.getSize();
                }
            }
        } catch (Exception e) {

        }

    }

    private void downloadFile(String source, Uri dest) {
        try {
            InputStream in = ftpClient.retrieveFileStream(source);
            BufferedInputStream inputStream = new BufferedInputStream(in);
            OutputStream outputStream = context.getContentResolver().openOutputStream(dest, "rwt");
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(BUFFER)) != -1) {
                if (progressDialog.isCancelled()) {
                    inputStream.close();
                    outputStream.close();
                    ftpClient.completePendingCommand();
                    cancel(true);
                    return;
                }
                outputStream.write(BUFFER, 0, bytesRead);
                currentRead += bytesRead;
                totalRead += bytesRead;
                publishProgress(2);
            }
            currentRead = 0;
            inputStream.close();
            outputStream.close();
            ftpClient.completePendingCommand();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadDirectory(String sourceDir, DocumentFile destDir) {
        if (progressDialog.isCancelled()) {
            cancel(true);
            return;
        }
        try {
            FTPFile[] subFiles = ftpClient.listFiles(sourceDir);
            for (FTPFile file : subFiles) {
                if (file.isDirectory()) {
                    DocumentFile newDir = destDir.createDirectory(file.getName());
                    downloadDirectory(sourceDir + file.getName() + "/", newDir);
                } else {
                    currentFile = file.getName();
                    currentTotal = file.getSize();
                    publishProgress(1);
                    DocumentFile createdFile = destDir.createFile(null, file.getName());
                    downloadFile(sourceDir + file.getName(), createdFile.getUri());
                }
            }
        } catch (Exception e) {

        }
    }

    private int percent(long read, long total) {
        return (int) (read * 100.0 / total + 0.5);
    }


}
