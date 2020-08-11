package com.dhruv.networkmanager.data.entities;

import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.Nullable;

public class Package implements Comparable<Package>{
    private String name;
    private String packageName;
    private Drawable icon;
    private int uid;
    private long wifiBytes;
    private long mobileBytes;
    private long totalBytes;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getWifiBytes() {
        return wifiBytes;
    }

    public void setWifiBytes(long wifiBytes) {
        this.wifiBytes = wifiBytes;
    }

    public long getMobileBytes() {
        return mobileBytes;
    }

    public void setMobileBytes(long mobileBytes) {
        this.mobileBytes = mobileBytes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Package){
            return ((Package) obj).uid==uid;
        }
        return false;
    }

    @Override
    public int compareTo(Package o) {
        if(this.totalBytes==o.totalBytes)
            return 0;
        else if(this.totalBytes>o.totalBytes)
            return -1;
        else
            return 1;
    }
}
