package com.dhruv.networkmanager.asynchronous;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.dhruv.networkmanager.data.helper.DatabaseHelper;
import com.dhruv.networkmanager.utils.LoadingDialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class InitialDatabaseTask extends AsyncTask<Void, Void, Void> {

    Context context;
    Activity activity;
    PackageManager pm;
    DatabaseHelper dbHelper;
    SQLiteDatabase database;
    LoadingDialog dialog;


    public InitialDatabaseTask(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        dialog=new LoadingDialog(activity);
        dialog.start();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        pm = context.getPackageManager();
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        List<PackageInfo> list = pm.getInstalledPackages(PackageManager.GET_META_DATA);
        Collections.sort(list, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo o1, PackageInfo o2) {
                return o1.applicationInfo.uid - o2.applicationInfo.uid;
            }
        });
        ListIterator<PackageInfo> iterator = list.listIterator();
        String packageName = null;
        String name = null;
        while (iterator.hasNext()) {
            PackageInfo packageInfo = iterator.next();
            packageName = packageInfo.packageName;
            int Uid = packageInfo.applicationInfo.uid;
            int flag = 0;
            if(pm.checkPermission(Manifest.permission.INTERNET,packageName)==PackageManager.PERMISSION_DENIED)
                continue;
            if (packageInfo.sharedUserId != null) {
                int uid = packageInfo.applicationInfo.uid;
                int iFlag = 0;
                do {
                    if (iterator.hasNext() && iFlag == 1) {
                        packageInfo = iterator.next();
                        continue;
                    }
                    if (packageInfo.sharedUserLabel != 0) {
                        name = pm.getText(packageName, packageInfo.sharedUserLabel, packageInfo.applicationInfo).toString();
                        iFlag = 1;
                        flag = 1;
                    }
                    if (iterator.hasNext()) {
                        packageInfo = iterator.next();
                    }
                } while (packageInfo.applicationInfo.uid == uid);
                if (flag == 0) {
                    iterator.previous();
                    packageInfo=iterator.previous();
                    Uid=packageInfo.applicationInfo.uid;
                    packageName=packageInfo.packageName;
                    name=pm.getApplicationLabel(packageInfo.applicationInfo).toString();
                    iterator.next();
                }

            } else {
                name = pm.getApplicationLabel(packageInfo.applicationInfo).toString();
            }
            ContentValues values=new ContentValues();
            values.put(DatabaseHelper.COLUMN_PACKAGE_UID,Uid);
            values.put(DatabaseHelper.COLUMN_PACKAGE_NAME,packageName);
            values.put(DatabaseHelper.COLUMN_NAME,name);

            database.insert(DatabaseHelper.TABLE_PACKAGE,null,values);
        }
        database.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        dialog.end();
    }
}
