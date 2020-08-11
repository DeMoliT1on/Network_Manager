package com.dhruv.networkmanager.listeners;

import com.dhruv.networkmanager.data.entities.FtpServer;

import org.apache.commons.net.ftp.FTPClient;

public interface OnConnectionListener {
    void onConnect(FtpServer ftpServer);
    void onConnect(FTPClient ftpClient);
}
