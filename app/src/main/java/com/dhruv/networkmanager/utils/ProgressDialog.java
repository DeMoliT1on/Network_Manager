package com.dhruv.networkmanager.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dhruv.networkmanager.R;

public class ProgressDialog {

    private Activity activity;
    private AlertDialog dialog;

    private TextView currentFile;
    private TextView currentSize;
    private TextView currentPercent;
    private ProgressBar currentProgress;

    private TextView totalPercent;
    private TextView totalSize;
    private ProgressBar totalProgress;

    private boolean isCancelled;

    public ProgressDialog(Activity activity) {
        this.activity = activity;
    }

    public void start(){
        AlertDialog.Builder builder= new AlertDialog.Builder(activity);

        LayoutInflater inflater=activity.getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_progress,null);

        builder.setView(view)
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isCancelled=true;
            }
        });
        dialog=builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Calculating");
        dialog.show();

        initViews();
    }

    public void end(){
        dialog.dismiss();
    }

    private void initViews(){
        currentFile=dialog.findViewById(R.id.currentFile);
        currentSize=dialog.findViewById(R.id.currentSize);
        currentPercent=dialog.findViewById(R.id.currentPercent);
        currentProgress=dialog.findViewById(R.id.currentProgress);

        totalSize=dialog.findViewById(R.id.totalSize);
        totalPercent=dialog.findViewById(R.id.totalPercent);
        totalProgress=dialog.findViewById(R.id.totalProgress);
    }

    public void initialise(String title){
        dialog.setTitle(title);
        currentProgress.setIndeterminate(false);
        totalProgress.setIndeterminate(false);
        currentProgress.setProgress(0);
        totalProgress.setProgress(0);
    }

    public void update(String currentFile,String current,String total,int curPercent,int totalPercent){
        this.currentFile.setText(currentFile);
        currentSize.setText(current);
        totalSize.setText(total);
        currentProgress.setProgress(curPercent);
        totalProgress.setProgress(totalPercent);
        currentPercent.setText(curPercent+"%");
        this.totalPercent.setText(totalPercent+"%");
    }

    public boolean isCancelled(){
        return isCancelled;
    }
}
