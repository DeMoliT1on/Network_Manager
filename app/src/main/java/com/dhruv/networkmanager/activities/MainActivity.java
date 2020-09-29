package com.dhruv.networkmanager.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.Toast;

import com.dhruv.networkmanager.asynchronous.InitialDatabaseTask;
import com.dhruv.networkmanager.R;
import com.dhruv.networkmanager.utils.NotificationService;
import com.google.android.material.navigation.NavigationView;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mainActivityDrawerLayout;
    private NavigationView mainActivityNavigationView;
    private Toolbar mainActivityToolbar;
    private Switch indicatorSwitch;
    private static final int REQUEST_CODE = 15;
    private static final int PREFERENCE_CODE=28;
    private AlertDialog requestDialog;
    private AlertDialog infoDialog;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean nightMode=preferences.getBoolean("night",false);
        if (nightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean isListPresent = preferences.getBoolean("list", false);

        if (!isListPresent) {
            new InitialDatabaseTask(getApplicationContext(), this).execute();
            editor = preferences.edit();
            editor.putBoolean("list", true);
            editor.apply();
        }

        mainActivityDrawerLayout = findViewById(R.id.drawer_layout);
        mainActivityNavigationView = findViewById(R.id.navigation_view);
        mainActivityToolbar = findViewById(R.id.mainToolbar);

        setUpDrawer();

        initDialogs();

        initSwitch();

        checkAndRequestPermissions();

    }


    private void initDialogs() {
        requestDialog = new AlertDialog.Builder(this)
                .setTitle("Permissions Needed")
                .setMessage(R.string.permission)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions();
                    }
                })
                .setCancelable(false)
                .create();

        infoDialog = new AlertDialog.Builder(this)
                .setTitle("Permissions Denied")
                .setMessage(R.string.permission_denied)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent,REQUEST_CODE);
                    }
                })
                .setCancelable(false)
                .create();

        requestDialog.setCanceledOnTouchOutside(false);
        infoDialog.setCanceledOnTouchOutside(false);
    }

    private void initSwitch() {
        indicatorSwitch = (Switch) findViewById(R.id.indicatorSwitch);

        indicatorSwitch.setChecked(isServiceRunning());

        indicatorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            Intent intent = new Intent(getApplicationContext(), NotificationService.class);

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(intent);
                } else {
                    stopService(intent);
                }

            }

        });
    }

    private boolean isServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {

            if (runningServiceInfo.service.getClassName().equals(NotificationService.class.getName())) {
                return true;
            }
        }

        return false;
    }

    public void setUpDrawer() {

        setSupportActionBar(mainActivityToolbar);
        getSupportActionBar().setTitle("Network Manager");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mainActivityDrawerLayout, mainActivityToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mainActivityDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mainActivityNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                mainActivityDrawerLayout.closeDrawer(GravityCompat.START);
                Intent intent;
                switch (menuItem.getItemId()) {

                    case R.id.nav_usage:
                        intent = new Intent(MainActivity.this, AppUsage.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_share:
                        intent = new Intent(MainActivity.this, FTPSharing.class);
                        startActivityForResult(intent, PREFERENCE_CODE);
                        return true;
                    case R.id.nav_ftp:
                        intent = new Intent(MainActivity.this, FTPServer.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_prefs:
                        intent=new Intent(MainActivity.this,Preferences.class);
                        startActivityForResult(intent,PREFERENCE_CODE);
                        return true;


                }

                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mainActivityDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mainActivityDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private boolean checkPermissions(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED;
    }

    private void checkAndRequestPermissions() {
        if (checkPermissions()) {
            requestPermissions();
        }
        if (!hasPermissionToReadNetworkHistory()) {
            requestReadNetworkHistoryAccess();
        }
    }

    private boolean hasPermissionToReadNetworkHistory() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        final AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps != null ? appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName()) : 0;
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true;
        }
        appOps.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS,
                getApplicationContext().getPackageName(),
                new AppOpsManager.OnOpChangedListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onOpChanged(String op, String packageName) {
                        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                                android.os.Process.myUid(), getPackageName());
                        if (mode != AppOpsManager.MODE_ALLOWED) {
                            return;
                        }
                        appOps.stopWatchingMode(this);
                    }
                });

        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
    }

    private boolean showRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] + grantResults[1] + grantResults[2] == PackageManager.PERMISSION_GRANTED) {

            } else {
                if (showRationale()) {
                    requestDialog.show();
                } else {
                    infoDialog.show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==REQUEST_CODE){
            if(checkPermissions()&&!showRationale()){
                infoDialog.show();
            }
        }
        else if(requestCode==PREFERENCE_CODE){
            boolean nightMode=preferences.getBoolean("night",false);
            if (nightMode){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }

    private void requestReadNetworkHistoryAccess() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }
}
