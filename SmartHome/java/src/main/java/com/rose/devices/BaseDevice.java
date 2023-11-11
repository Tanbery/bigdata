package com.rose.devices;

public abstract class BaseDevice {
    public abstract String getCategory();

    public  abstract String getDeviceId();
    
    public abstract String toJSON();
    
    public abstract String toString();

    // public static abstract  SmartDevice CreateRandom();
}
