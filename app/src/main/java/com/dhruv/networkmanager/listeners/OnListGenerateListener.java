package com.dhruv.networkmanager.listeners;

import com.dhruv.networkmanager.data.entities.FtpFile;

import java.util.List;

public interface OnListGenerateListener {
    void onFetched(List<FtpFile> files);
}
