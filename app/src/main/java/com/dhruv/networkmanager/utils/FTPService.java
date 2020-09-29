package com.dhruv.networkmanager.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.dhruv.networkmanager.R;
import com.dhruv.networkmanager.activities.MainActivity;

import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.dhruv.networkmanager.activities.FTPSharing.CHANNEL_ID;


public class FTPService extends Service implements Runnable {

    public static final String DEFAULT_PATH = "/storage/emulated/0/";
    private FtpServer server;
    protected static Thread serverThread = null;


    boolean wifi = true;
    boolean hotspot = true;

    public enum FtpReceiverActions {
        STARTED,
        STOPPED,
        FAILED_TO_START
    }


    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter wifiFilter = new IntentFilter();
        wifiFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        wifiFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        registerReceiver(wifiStateReceiver, wifiFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serverThread = new Thread(this);
        serverThread.start();
        String input = intent.getStringExtra("URL");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("FTP Service Running")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_share)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(2, notification);
        return START_STICKY;
    }


    @Override
    public void run() {
        FtpServerFactory serverFactory = new FtpServerFactory();
        ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
        connectionConfigFactory.setAnonymousLoginEnabled(true);
        serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());

        BaseUser user = new BaseUser();
        user.setName("anonymous");
        user.setHomeDirectory(DEFAULT_PATH);

        List<Authority> list = new ArrayList<>();
        list.add(new WritePermission());
        user.setAuthorities(list);
        try {
            serverFactory.getUserManager().save(user);
        } catch (FtpException e) {
            e.printStackTrace();
        }
        ListenerFactory fac = new ListenerFactory();
        fac.setPort(3721);
        serverFactory.addListener("default", fac.createListener());
        try {
            server = serverFactory.createServer();
            server.start();
            EventBus.getDefault().post(FtpReceiverActions.STARTED);
        } catch (Exception e) {
            EventBus.getDefault().post(FtpReceiverActions.FAILED_TO_START);
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serverThread.interrupt();
        try {
            serverThread.join(10000); // wait 10 sec for server thread to finish
        } catch (InterruptedException ignored) {
        }
        server.stop();
        unregisterReceiver(wifiStateReceiver);
    }

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN) % 10;
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                wifi = mWifi.isConnected();
            } else if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(intent.getAction())) {
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        hotspot = true;
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        hotspot = false;
                }
            }
            if (!(wifi || hotspot)) {
                EventBus.getDefault().post(FtpReceiverActions.STOPPED);
                stopSelf();
            }
        }
    };

}
