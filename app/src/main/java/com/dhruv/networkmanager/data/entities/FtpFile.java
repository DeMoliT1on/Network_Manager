package com.dhruv.networkmanager.data.entities;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

public class FtpFile implements Comparable<FtpFile>{

    public static final int FOLDER=0;
    public static final int AUDIO=1;
    public static final int VIDEO=2;
    public static final int IMAGE=3;
    public static final int TEXT=4;
    public static final int CODE=5;

    private String name;
    private String timeStamp;
    private Drawable icon;
    private boolean checked;
    private long size;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    private boolean isDirectory;

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return getName().equals(((FtpFile) obj).name);
    }

    @Override
    public int compareTo(FtpFile o) {
        if (isDirectory&&!o.isDirectory())
            return -1;
        else if(!isDirectory&&o.isDirectory())
            return 1;
        else
            return 0;
    }
}
