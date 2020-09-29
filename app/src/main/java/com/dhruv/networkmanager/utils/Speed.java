package com.dhruv.networkmanager.utils;

import android.content.Context;

import com.dhruv.networkmanager.R;

public class Speed {

    public String speed;
    public String speedUnit;

    private Context context;

    public Speed(Context context) {
        this.context = context;
    }

    public void calcSpeed(long usedBytes, long usedTime) {
        double value;
        if (usedBytes > Math.pow(10, 6)) {
            value = (double) (usedBytes * 1000) / (Math.pow(10, 6) * usedTime);
            speed = String.format("%.1f", value);
            speedUnit = context.getString(R.string.MBps);
        } else if (usedBytes > Math.pow(10, 3)) {
            value = (double) (usedBytes * 1000) / (Math.pow(10, 3) * usedTime);
            Math.round(value);
            speed = String.format("%.0f", value);
            speedUnit = context.getString(R.string.KBps);
        } else {
            speed = Long.toString((usedBytes * 1000) / (usedTime));
            speedUnit = context.getString(R.string.Bps);
        }
    }
}
