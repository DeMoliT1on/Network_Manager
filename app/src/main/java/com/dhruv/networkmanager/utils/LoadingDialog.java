package com.dhruv.networkmanager.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.dhruv.networkmanager.R;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog dialog;
    public TextView title;
    public TextView msg;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    public void start(){
        AlertDialog.Builder builder= new AlertDialog.Builder(activity);

        LayoutInflater inflater=activity.getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_loading,null);
        title=view.findViewById(R.id.title);
        msg=view.findViewById(R.id.msg);

        builder.setView(view);
        builder.setCancelable(false);

        dialog=builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void end(){
        dialog.dismiss();
    }
}
