package com.soundboard;

public class AppSettings {
    private String primaryDevice;
    private String secondaryDevice;

    public AppSettings() {
        this.primaryDevice = "";
        this.secondaryDevice = "";
    }

    public AppSettings(String primaryDevice, String secondaryDevice) {
        this.primaryDevice = primaryDevice;
        this.secondaryDevice = secondaryDevice;
    }

    public String getPrimaryDevice() { return primaryDevice; }
    public void setPrimaryDevice(String primaryDevice) { this.primaryDevice = primaryDevice; }

    public String getSecondaryDevice() { return secondaryDevice; }
    public void setSecondaryDevice(String secondaryDevice) { this.secondaryDevice = secondaryDevice; }
}