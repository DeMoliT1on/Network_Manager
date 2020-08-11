package com.dhruv.networkmanager.listeners;

import com.dhruv.networkmanager.data.entities.Package;

import java.util.List;

public interface OnPackageListListener {
    void onPackageList(List<Package> list);
}
