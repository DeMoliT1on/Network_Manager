package com.dhruv.networkmanager.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dhruv.networkmanager.R;
import com.dhruv.networkmanager.utils.FTPService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class FTPSharing extends AppCompatActivity implements View.OnClickListener {

    TableLayout display;
    Button toggle;
    TextView wifiStatus;
    TextView ipAddress;
    TextView networkType;
    TextView url;

    boolean isRunning;
    boolean wifi = false;
    boolean hotspot = false;
    String ip, URL;
    public static final String CHANNEL_ID = "ftpNotification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftp_sharing);

        initViews();

        setUpToolbar();
    }

    private void initViews() {
        display = findViewById(R.id.display);
        toggle = findViewById(R.id.startBtn);
        wifiStatus = findViewById(R.id.wifiStatus);
        ipAddress = findViewById(R.id.ipAddress);
        networkType = findViewById(R.id.networkType);
        url = findViewById(R.id.url);
        toggle.setOnClickListener(this);
    }

    private void setUpToolbar() {
        Toolbar toolbar;
        toolbar = findViewById(R.id.ftpSharingToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }

    @Override
    public void onClick(View v) {
        if (isRunning) {
            EventBus.getDefault().post(FTPService.FtpReceiverActions.STOPPED);
            stopService();
        } else {
            startService();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = isServiceRunning();
        toggle.setText(isRunning ? R.string.stop_btn : R.string.start_btn);
        display.setVisibility(isRunning ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter wifiFilter = new IntentFilter();
        wifiFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        wifiFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        registerReceiver(wifiStateReceiver, wifiFilter);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(wifiStateReceiver);
        EventBus.getDefault().unregister(this);
    }


    private boolean isServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {

            if (runningServiceInfo.service.getClassName().equals(FTPService.class.getName())) {
                return true;
            }
        }

        return false;
    }

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (mWifi.isConnected()) {
                    wifi = true;
                } else {
                    wifi = false;
                }
            } else if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(intent.getAction())) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN) % 10;
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        hotspot = true;
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        hotspot = false;
                }
            }

            if (!(wifi || hotspot)) {
                notConnected();
            } else {
                connected();
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onFtpReceiveActions(FTPService.FtpReceiverActions signal) {
        switch (signal) {
            case STARTED:
                Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_SHORT).show();
                toggle.setText(R.string.stop_btn);
                display.setVisibility(View.VISIBLE);
                break;
            case FAILED_TO_START:
                Toast.makeText(getApplicationContext(), "Service Failed to start", Toast.LENGTH_SHORT).show();
                toggle.setText(R.string.start_btn);
                display.setVisibility(View.GONE);
                break;
            case STOPPED:
                Toast.makeText(getApplicationContext(), "Service Stopped", Toast.LENGTH_SHORT).show();
                toggle.setText(R.string.start_btn);
                display.setVisibility(View.GONE);
                isRunning = false;
                break;
        }
    }

    private void startService() {
        Intent serviceIntent = new Intent(this, FTPService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serviceIntent.putExtra("URL", URL);
            startForegroundService(serviceIntent);
            isRunning = true;
        }
    }

    private void stopService() {
        Intent serviceIntent = new Intent(this, FTPService.class);
        stopService(serviceIntent);
        isRunning = false;
    }

    private void connected() {
        wifiStatus.setText("Connected");
        do {
            ip = getIPAddress();
        } while (hotspot && ip == null);
        ipAddress.setText(ip);
        URL = "ftp://" + ip + ":2121";
        url.setText(URL);
        networkType.setText(wifi ? "Wifi" : "Wifi AP");
        toggle.setEnabled(true);
    }

    private void notConnected() {
        wifiStatus.setText("Disconnected");
        ipAddress.setText("NA");
        networkType.setText("NA");
        toggle.setEnabled(false);
    }

    public static String getIPAddress() {

        try {
            NetworkInterface netInterface = NetworkInterface.getByName("wlan0");
            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (address instanceof Inet4Address) {
                    return address.getHostAddress();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
