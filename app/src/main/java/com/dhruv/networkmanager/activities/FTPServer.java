package com.dhruv.networkmanager.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.dhruv.networkmanager.R;
import com.dhruv.networkmanager.data.entities.FtpServer;
import com.dhruv.networkmanager.utils.FtpDialog;
import com.dhruv.networkmanager.fragments.FtpFileFragment;
import com.dhruv.networkmanager.fragments.FtpServerFragment;
import com.dhruv.networkmanager.listeners.OnBackPressListener;
import com.dhruv.networkmanager.listeners.OnServerClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class FTPServer extends AppCompatActivity implements OnServerClickListener {
    FloatingActionButton ftpFab;
    public FloatingActionButton fileFab;
    FtpServerFragment ftpServerFragment;
    FtpFileFragment ftpFileFragment;
    OnBackPressListener listener;

    private static final int OPEN_CODE=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftp_server);

        setUpToolbar();
        initFab();
        if(savedInstanceState==null){
            ftpServerFragment=FtpServerFragment.newInstance(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.ftpFragment_viewer,ftpServerFragment).commit();
        }
    }

    private void setUpToolbar(){
        Toolbar toolbar;
        toolbar = findViewById(R.id.ftpServerToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }


    public void initFab(){
        ftpFab=findViewById(R.id.ftp_fab);
        fileFab=findViewById(R.id.file_fab);
        ftpFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FtpDialog dialog=FtpDialog.newInstance(ftpServerFragment);
                dialog.show(getSupportFragmentManager(),"Ftp Dialog");
            }
        });

        fileFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(Intent.createChooser(i, "Choose location"),OPEN_CODE);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(listener==null){
            super.onBackPressed();
            finish();
        }
        else if(listener.onBackPressed()){
            getSupportFragmentManager().beginTransaction().replace(R.id.ftpFragment_viewer,ftpServerFragment).commit();
            listener=null;
            ftpFab.show();
            fileFab.hide();
        }
    }

    @Override
    public void onClick(FtpServer ftpServer) {
        ftpFileFragment=new FtpFileFragment(ftpServer,this);
        getSupportFragmentManager().beginTransaction().replace(R.id.ftpFragment_viewer,ftpFileFragment).commit();
        listener=ftpFileFragment;
        ftpFab.hide();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_CODE
                && resultCode == Activity.RESULT_OK) {
            Uri uri = null;

            if (data != null) {

            }
        }
    }
}

