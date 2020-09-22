package com.dhruv.networkmanager.fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dhruv.networkmanager.R;
import com.dhruv.networkmanager.adapters.AppUsageAdapter;
import com.dhruv.networkmanager.data.entities.Package;
import com.dhruv.networkmanager.listeners.OnDateSetListener;
import com.dhruv.networkmanager.listeners.OnPackageListListener;
import com.dhruv.networkmanager.asynchronous.PackageAsyncTask;
import com.dhruv.networkmanager.utils.DatePickerDialog;
import com.dhruv.networkmanager.utils.UnitOfMeasurement;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppUsageFragment extends Fragment implements OnPackageListListener, OnDateSetListener, View.OnClickListener {

    ListView usageListView;
    View headerView;
    View mobileUsage;
    TextView mobileData;
    View wifiUsage;
    TextView wifiData;
    TextView totalData;
    TextView dateView;
    View datePicker;
    ProgressBar loading;

    List<Package> list;
    List<Package> mobileList;
    List<Package> wifiList;
    Calendar calendar;

    AppUsageAdapter adapter;
    private static final String ARG_PARAM1 = "Position";

    public static final int MOBILE = 0;
    public static final int WIFI = 1;
    public static final int BOTH = 2;

    public static final int DAY = 0;
    public static final int MONTH = 1;
    public static final int YEAR = 2;

    private int position;
    private int currentMode;
    private long startTime;
    private long endTime;
    private long mobile;
    private long wifi;
    private long total;
    private boolean _hasLoadedOnce= false;

    public AppUsageFragment() {

    }

    public static AppUsageFragment newInstance(int position) {
        AppUsageFragment fragment = new AppUsageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        position = getArguments().getInt(ARG_PARAM1);
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        currentMode = BOTH;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_app_usage, container, false);
        usageListView = v.findViewById(R.id.appUsageList);
        loading = v.findViewById(R.id.loading);
        headerView = getLayoutInflater().inflate(R.layout.header_list, null);
        initHeaderView();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(list==null){
            getData();

        }
        else{
            setAdapter();
        }
    }

    @Override
    public void onPackageList(List<Package> list) {
        this.list = list;
        adapter=new AppUsageAdapter(getActivity(),list,currentMode);

        setAdapter();

        separateList();
    }

    private void getData() {
        switch (position) {
            case 0:
                onDateSet(null, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                break;
            case 1:
                onMonthSet(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
                break;
            case 2:
                onYearSet(calendar.get(Calendar.YEAR));
        }
    }

    private void setAdapter(){
        usageListView.setAdapter(adapter);
        if (usageListView.getHeaderViewsCount() == 0)
            usageListView.addHeaderView(headerView);

        loading.setVisibility(View.GONE);

        fillHeaderView();
    }

    private void separateList() {
        mobileList = new ArrayList<>();
        wifiList = new ArrayList<>();
        mobile = 0;
        wifi = 0;
        total = 0;
        for (Package item : list) {
            total += item.getTotalBytes();
            if (item.getMobileBytes() != 0) {
                mobile += item.getMobileBytes();
                mobileList.add(item);
            }
            if (item.getWifiBytes() != 0) {
                wifi += item.getWifiBytes();
                wifiList.add(item);
            }
        }
        Collections.sort(mobileList, new Comparator<Package>() {
            @Override
            public int compare(Package o1, Package o2) {
                if (o1.getMobileBytes() == o2.getMobileBytes())
                    return 0;
                if (o1.getMobileBytes() > o2.getMobileBytes())
                    return -1;
                return 1;
            }
        });
        Collections.sort(wifiList, new Comparator<Package>() {
            @Override
            public int compare(Package o1, Package o2) {
                if (o1.getWifiBytes() == o2.getWifiBytes())
                    return 0;
                if (o1.getWifiBytes() > o2.getWifiBytes())
                    return -1;
                return 1;
            }
        });

        fillHeaderView();
    }
    public void fillHeaderView(){
        mobileData.setText(UnitOfMeasurement.usageB(mobile));
        wifiData.setText(UnitOfMeasurement.usageB(wifi));
        totalData.setText(UnitOfMeasurement.usageB(total));
    }
    public void initHeaderView() {
        mobileUsage = headerView.findViewById(R.id.mobileUsage);
        mobileData = (TextView) headerView.findViewById(R.id.mobileData);
        wifiUsage = headerView.findViewById(R.id.wifiUsage);
        wifiData = (TextView) headerView.findViewById(R.id.wifiData);
        totalData = (TextView) headerView.findViewById(R.id.totalData);
        dateView = (TextView) headerView.findViewById(R.id.dateView);
        datePicker = headerView.findViewById(R.id.datePicker);

        mobileUsage.setOnClickListener(this);
        wifiUsage.setOnClickListener(this);
        datePicker.setOnClickListener(this);


    }

    private void changeCurrentMode(int mode){
        switch (mode){
            case MOBILE:currentMode = MOBILE;
                wifiUsage.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                mobileUsage.setBackgroundColor(getResources().getColor(R.color.orange));break;
            case WIFI:currentMode = WIFI;
                mobileUsage.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                wifiUsage.setBackgroundColor(getResources().getColor(R.color.grey));break;
            case BOTH:currentMode = BOTH;
                mobileUsage.setBackgroundColor(getResources().getColor(R.color.orange));
                wifiUsage.setBackgroundColor(getResources().getColor(R.color.grey));
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.add(Calendar.DAY_OF_MONTH,1);
        endTime=calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        startTime=calendar.getTimeInMillis();
        new PackageAsyncTask(startTime, endTime, getActivity(), this).execute();
        changeCurrentMode(BOTH);
        dateView.setText(new SimpleDateFormat("MMM dd, yyyy").format(calendar.getTime()));
    }

    @Override
    public void onMonthSet(int month, int year) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, 1);
        endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);
        startTime = calendar.getTimeInMillis();
        new PackageAsyncTask(startTime, endTime, getActivity(), this).execute();
        changeCurrentMode(BOTH);
        dateView.setText(new SimpleDateFormat("MMM yyyy").format(calendar.getTime()));
    }

    @Override
    public void onYearSet(int year) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.YEAR, 1);
        endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        startTime = calendar.getTimeInMillis();
        new PackageAsyncTask(startTime, endTime, getActivity(), this).execute();
        changeCurrentMode(BOTH);
        dateView.setText(new SimpleDateFormat("yyyy").format(calendar.getTime()));
    }

    @Override
    public void onClick(View v) {
        if (v == datePicker) {
            DialogFragment datePicker = new DatePickerDialog(this, position, Calendar.getInstance());
            datePicker.show(getActivity().getSupportFragmentManager(), "Date picker");
        } else if (v == mobileUsage) {
            if (currentMode == BOTH || currentMode == MOBILE) {
                changeCurrentMode(WIFI);
                usageListView.setAdapter(new AppUsageAdapter(getActivity(), wifiList, currentMode));
            } else {
                changeCurrentMode(BOTH);
                usageListView.setAdapter(new AppUsageAdapter(getActivity(), list, currentMode));
            }

        } else if (v == wifiUsage) {
            if (currentMode == BOTH || currentMode == WIFI) {
                changeCurrentMode(MOBILE);
                usageListView.setAdapter(new AppUsageAdapter(getActivity(), mobileList, currentMode));
            } else {
                changeCurrentMode(BOTH);
                usageListView.setAdapter(new AppUsageAdapter(getActivity(), list, currentMode));
            }
        }
    }
}
