package com.dhruv.networkmanager.utils;

import android.content.Context;

public class Used {

    public String mobileUsed;
    public String mobileUsedUnit;
    public String wifiUsed;
    public String wifiUsedUnit;

    private Context context;

    public Used(Context context) {
        this.context = context;
    }

    public void calcUsed(long usedBytes, int pos) {
        double value;
        if (pos == 0) {
            if (usedBytes > Math.pow(2, 30)) {
                value = (double) (usedBytes) / (Math.pow(2, 30));
                mobileUsed = String.format("%.2f", value);
                mobileUsedUnit = "GiB";
            } else if (usedBytes > Math.pow(2, 20)) {
                value = (double) (usedBytes) / (Math.pow(2, 20));
                mobileUsed = String.format("%.2f", value);
                mobileUsedUnit = "MiB";
            } else if (usedBytes > Math.pow(2, 10)) {
                value = (double) (usedBytes) / (Math.pow(2, 10));
                mobileUsed = String.format("%.2f", value);
                mobileUsedUnit = "KiB";
            } else {
                value = usedBytes;
                mobileUsed = Double.toString(value);
                mobileUsedUnit = "B";
            }
        } else {
            if (usedBytes > Math.pow(2, 30)) {
                value = (double) (usedBytes) / (Math.pow(2, 30));
                wifiUsed = String.format("%.2f", value);
                wifiUsedUnit = "GiB";
            } else if (usedBytes > Math.pow(2, 20)) {
                value = (double) (usedBytes) / (Math.pow(2, 20));
                wifiUsed = String.format("%.2f", value);
                wifiUsedUnit = "MiB";
            } else if (usedBytes > Math.pow(2, 10)) {
                value = (double) (usedBytes) / (Math.pow(2, 10));
                wifiUsed = String.format("%.2f", value);
                wifiUsedUnit = "KiB";
            } else {
                value = usedBytes;
                wifiUsed = Double.toString(value);
                wifiUsedUnit = "B";
            }
        }

    }

}
