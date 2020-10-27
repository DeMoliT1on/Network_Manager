package com.dhruv.networkmanager.asynchronous;

import android.Manifest;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.dhruv.networkmanager.R;
import com.dhruv.networkmanager.data.entities.Package;
import com.dhruv.networkmanager.data.helper.DatabaseHelper;
import com.dhruv.networkmanager.fragments.AppUsageFragment;
import com.dhruv.networkmanager.listeners.OnPackageListListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageAsyncTask extends AsyncTask<Void, Void, List<Package>> {

    long startTime;
    long endTime;
    private String[] columns = {
            DatabaseHelper.COLUMN_PACKAGE_UID,
            DatabaseHelper.COLUMN_PACKAGE_NAME,
            DatabaseHelper.COLUMN_NAME
    };

    List<Package> list;
    List<ListItem> toAdd;
    Map<Integer, ListItem> map;

    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    NetworkStatsManager networkStatsManager;
    NetworkStats mobileStats;
    NetworkStats wifiStats;
    NetworkStats.Bucket bucket;
    TelephonyManager tm;
    Context mContext;
    OnPackageListListener listener;
    PackageManager pm;

    public PackageAsyncTask(long startTime, long endTime, Context mContext,OnPackageListListener listener) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.mContext = mContext;
        this.listener=listener;
    }


    public Drawable getIcon(String packageName) {
        try {
            return pm.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return mContext.getDrawable(R.mipmap.ic_launcher);
    }

    public String getName(String packageName) {
        ApplicationInfo ai = null;
        try {
            ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return pm.getApplicationLabel(ai).toString();
        } catch (PackageManager.NameNotFoundException e) {
            return packageName;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        tm = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            cancel(true);
            return;
        }

        pm = mContext.getPackageManager();
        String subscriberID = tm.getSubscriberId();
        networkStatsManager = mContext.getSystemService(NetworkStatsManager.class);
        try {
            mobileStats = networkStatsManager.querySummary(ConnectivityManager.TYPE_MOBILE, subscriberID, startTime, endTime);
            wifiStats = networkStatsManager.querySummary(ConnectivityManager.TYPE_WIFI, "", startTime, endTime);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }


    @Override
    protected List<Package> doInBackground(Void... voids) {if (isCancelled())
            return null;

        getList();
        Package packageItem;
        bucket = new NetworkStats.Bucket();
        list = new ArrayList<>();

        while (mobileStats.hasNextBucket()) {
            mobileStats.getNextBucket(bucket);
            packageItem = new Package();
            packageItem.setUid(bucket.getUid());
            packageItem.setMobileBytes(bucket.getRxBytes() + bucket.getTxBytes());
            int index = list.lastIndexOf(packageItem);
            if (index != -1) {
                long bytes = list.get(index).getMobileBytes() + packageItem.getMobileBytes();
                list.remove(index);
                packageItem.setMobileBytes(bytes);
            }
            list.add(packageItem);
        }
        while (wifiStats.hasNextBucket()) {
            wifiStats.getNextBucket(bucket);
            packageItem = new Package();
            packageItem.setUid(bucket.getUid());
            packageItem.setWifiBytes(bucket.getRxBytes() + bucket.getTxBytes());
            int index = list.lastIndexOf(packageItem);
            if (index != -1) {
                long mobileBytes = list.get(index).getMobileBytes();
                long wifiBytes = list.get(index).getWifiBytes() + packageItem.getWifiBytes();
                list.remove(index);
                packageItem.setMobileBytes(mobileBytes);
                packageItem.setWifiBytes(wifiBytes);
            }
            list.add(packageItem);
        }
        toAdd = new ArrayList<>();
        for (Package item : list) {
            int uid = item.getUid();
            item.setTotalBytes(item.getMobileBytes() + item.getWifiBytes());
            ListItem listItem = map.get(uid);
            if (listItem == null) {
                String packageName = pm.getNameForUid(uid);
                String name;
                if (packageName == null) {
                    switch (uid) {
                        case NetworkStats.Bucket.UID_REMOVED:
                            item.setName("Removed apps");
                            item.setIcon(mContext.getDrawable(R.drawable.ic_default));
                            break;
                        case NetworkStats.Bucket.UID_TETHERING:
                            item.setName("Tethering and Hotspot");
                            item.setIcon(mContext.getDrawable(R.drawable.ic_default));
                            break;
                        case 0:
                            item.setName("System");
                            item.setIcon(mContext.getDrawable(R.drawable.ic_default));
                            break;
                        default:
                            item.setName("" + uid);
                            item.setIcon(mContext.getDrawable(R.drawable.ic_default));

                    }
                } else {
                    packageName = packageName.split(":")[0];
                    name = getName(packageName);
                    item.setName(name);
                    item.setIcon(getIcon(packageName));
                    toAdd.add(new ListItem(uid, packageName, name));
                }
                continue;
            }
            item.setName(listItem.name);
            item.setIcon(getIcon(listItem.packageName));
        }
        Collections.sort(list);
        return list;
    }

    @Override
    protected void onPostExecute(List<Package> packages) {
        listener.onPackageList(packages);
        db = dbHelper.getWritableDatabase();
        for (ListItem item : toAdd) {
            ContentValues values=new ContentValues();
            values.put(DatabaseHelper.COLUMN_PACKAGE_UID,item.uid);
            values.put(DatabaseHelper.COLUMN_PACKAGE_NAME,item.packageName);
            values.put(DatabaseHelper.COLUMN_NAME,item.name);
            db.insert(DatabaseHelper.TABLE_PACKAGE,null,values);
        }
        db.close();
    }

    public void getList() {
        dbHelper = new DatabaseHelper(mContext);
        db = dbHelper.getWritableDatabase();

        map = new HashMap<>();
        ListItem listItem;
        Cursor cursor = db.query(DatabaseHelper.TABLE_PACKAGE, columns,
                null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            listItem = new ListItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            map.put(listItem.uid, listItem);
            cursor.moveToNext();
        }

        db.close();
    }

    public class ListItem {
        public int uid;
        public String packageName;
        public String name;

        public ListItem(int uid, String packageName, String name) {
            this.uid = uid;
            this.packageName = packageName;
            this.name = name;
        }
    }
}
