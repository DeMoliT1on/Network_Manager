package com.dhruv.networkmanager.utils;

import android.content.Context;

public class Used {

    public String used;
    public String usedUnit;

    private Context context;

    public Used(Context context) {
        this.context = context;
    }

    public void calcUsed(long usedBytes) {
        double value;
        if (usedBytes > Math.pow(2, 30)) {
            value = (double) (usedBytes) / (Math.pow(2, 30));
            used = String.format("%.2f", value);
            usedUnit = "GiB";
        } else if (usedBytes > Math.pow(2, 20)) {
            value = (double) (usedBytes) / (Math.pow(2, 20));
            used = String.format("%.2f", value);
            usedUnit = "MiB";
        } else if (usedBytes > Math.pow(2, 10)) {
            value = (double) (usedBytes) / (Math.pow(2, 10));
            used = String.format("%.2f", value);
            usedUnit = "KiB";
        } else {
            value = usedBytes;
            used = Double.toString(value);
            usedUnit = "B";
        }
    }

}
