package com.dhruv.networkmanager.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.dhruv.networkmanager.R;
import com.dhruv.networkmanager.data.entities.FtpServer;
import com.dhruv.networkmanager.asynchronous.FTPConnectAsyncTask;
import com.dhruv.networkmanager.fragments.FtpServerFragment;

public class FtpDialog extends AppCompatDialogFragment{

    EditText server;
    EditText port;
    EditText username;
    EditText password;
    EditText name;
    CheckBox anonymous;
    FtpServer ftpServer;
    FtpServerFragment fragment;

    public static FtpDialog newInstance(FtpServerFragment fragment){
        FtpDialog dialog=new FtpDialog();
        dialog.setFragment(fragment);
        return dialog;
    }

    public void setFragment(FtpServerFragment fragment) {
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_ftp, null);

        ftpServer = new FtpServer();
        initViews(view);
        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (server.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "Error: Server host cannot be empty", Toast.LENGTH_SHORT).show();
                    } else if (port.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "Error: Port cannot be empty", Toast.LENGTH_SHORT).show();
                    } else if (!anonymous.isChecked() && username.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "Error: Username cannot be empty", Toast.LENGTH_SHORT).show();
                    } else if (!anonymous.isChecked() && password.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "Error: Password cannot be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        getData();
                        new FTPConnectAsyncTask(FtpDialog.this,ftpServer,fragment,getActivity()).execute();
                    }
                }
            });
        }
    }

    public void initViews(View view) {

        server = view.findViewById(R.id.server);
        port = view.findViewById(R.id.port);
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        name = view.findViewById(R.id.display);
        anonymous = view.findViewById(R.id.anonymous);
        anonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (anonymous.isChecked()) {
                    username.setEnabled(false);
                    password.setEnabled(false);
                } else {
                    username.setEnabled(true);
                    password.setEnabled(true);
                }
            }
        });

    }

    public void getData() {
        ftpServer.setHost(server.getText().toString());
        ftpServer.setPort(Integer.parseInt(port.getText().toString()));
        ftpServer.setLogin(username.getText().toString());
        ftpServer.setPassword(password.getText().toString());
        ftpServer.setAnonymous(anonymous.isChecked());
        if(name.getText().toString().equals("")){
            ftpServer.setName(ftpServer.getHost());
            return;
        }
        ftpServer.setName(name.getText().toString());
    }
}

