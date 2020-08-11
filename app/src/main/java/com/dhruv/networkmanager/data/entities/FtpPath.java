package com.dhruv.networkmanager.data.entities;

public class FtpPath {
    private int position;
    private String path;
    private String name;

    public FtpPath(int position, String path, String name) {
        this.position = position;
        this.path = path;
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
