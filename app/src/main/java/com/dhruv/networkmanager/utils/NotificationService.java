package com.dhruv.networkmanager.utils;

import android.app.KeyguardManager;
import android.app.Service;
import android.app.usage.NetworkStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;

import java.util.Calendar;

public class NotificationService extends Service {

    private KeyguardManager keyguardManager;

    IntentFilter filter;

    private long lastBytes = 0;
    private long lastTime = 0;
    private long currentBytes = 0;
    private long currentTime = 0;
    private long usedBytes = 0;
    private long usedTime = 0;
    private long mobileUsedBytes = 0;
    private long wifiUsedBytes = 0;
    private Speed speed;
    private Used used;
    private NotificationGenerator notificationGenerator;

    Calendar cal;

    private boolean notificationCreated = false;

    private boolean notificationOnLockScreen;

    final private Handler handler = new Handler();

    private NetworkStatsHelper networkStatsHelper;

    private NetworkStatsManager networkStatsManager;

    private final Runnable speedRunnable = new Runnable() {
        @Override
        public void run() {

            currentBytes = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
            usedBytes = currentBytes - lastBytes;
            currentTime = System.currentTimeMillis();
            usedTime = currentTime - lastTime;

            lastBytes = currentBytes;
            lastTime = currentTime;

            speed.calcSpeed(usedBytes, usedTime);

            notificationGenerator.updateNotification(speed, used);

            handler.postDelayed(this, 1000);
        }
    };

    private final Runnable usageRunnable = new Runnable() {
        @Override
        public void run() {

            cal = Calendar.getInstance();

            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            mobileUsedBytes = networkStatsHelper.getAllRxBytesMobile(getApplicationContext(), cal.getTimeInMillis(), System.currentTimeMillis()) + networkStatsHelper.getAllTxBytesMobile(getApplicationContext(), cal.getTimeInMillis(), System.currentTimeMillis());

            wifiUsedBytes = networkStatsHelper.getAllBytesWifi(cal.getTimeInMillis(), System.currentTimeMillis());

            used.calcUsed(mobileUsedBytes, 0);

            used.calcUsed(wifiUsedBytes, 1);

            notificationGenerator.updateNotification(speed, used);

            handler.postDelayed(this, 60000);
        }
    };


    private final BroadcastReceiver screenBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }

            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                pauseNotifying();
                if (!notificationOnLockScreen) {
                    notificationGenerator.hideNotification();
                }
                notificationGenerator.updateNotification(speed, used);
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                if (notificationOnLockScreen || !keyguardManager.isKeyguardLocked()) {
                    notificationGenerator.showNotification();
                    restartNotifying();
                }
            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                notificationGenerator.showNotification();
                restartNotifying();
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onDestroy() {
        super.onDestroy();

        pauseNotifying();
        unregisterReceiver(screenBroadcastReceiver);

        removeNotification();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        lastBytes = TrafficStats.getTotalTxBytes() + TrafficStats.getTotalRxBytes();
        lastTime = System.currentTimeMillis();

        speed = new Speed(this);

        used = new Used(this);

        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        notificationGenerator = new NotificationGenerator(this);

        networkStatsManager = (NetworkStatsManager) getApplicationContext().getSystemService(Context.NETWORK_STATS_SERVICE);

        networkStatsHelper = new NetworkStatsHelper(networkStatsManager);

        filter = new IntentFilter();


        registerReceiver(screenBroadcastReceiver, filter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotification();

        restartNotifying();

        return START_REDELIVER_INTENT;

    }

    private void createNotification() {
        if (!notificationCreated) {
            notificationGenerator.start(this);
            notificationCreated = true;
        }
    }

    private void removeNotification() {
        if (notificationCreated) {
            notificationGenerator.stop(this);
            notificationCreated = false;
        }
    }

    private void pauseNotifying() {
        handler.removeCallbacks(speedRunnable);
        handler.removeCallbacks(usageRunnable);
    }

    private void restartNotifying() {
        handler.removeCallbacks(speedRunnable);
        handler.removeCallbacks(usageRunnable);
        handler.post(speedRunnable);
        handler.post(usageRunnable);
    }


}
