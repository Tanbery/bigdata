package com.rose.devices;

import java.util.Random;

public class Freezer extends BaseDevice {
    private int       homeId;
    private  String deviceId;
    private String  brand;
    private String  model;
    private int       level;

    public String getCategory() {
        return "Freezer";
    }
    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public int getHomeId() {
        return homeId;
    }
    public void setHomeId(int homeId) {
        this.homeId = homeId;
    }
    
    public String toJSON(){
        return  "{category:" + getCategory() + 
                    ",homeId: " + getHomeId() +
                    ",deviceId: " + getDeviceId() +
                    ",brand: "    +  getBrand() +
                    ",model: "    + getModel() +
                    ",level: "      + getLevel()  +
                    "}";
    }
    
    public String toString(){
        return  "category:" + getCategory() + 
                    "|homeId: " + getHomeId() +
                    "|deviceId: " + getDeviceId() +
                    "|brand: "    +  getBrand() +
                    "|model: "    + getModel() +
                    "|level: "      + getLevel();
    }

    public static BaseDevice CreateRandom(){
        Freezer freezer = new Freezer();
        Random rnd =new  Random();
        freezer.setHomeId(rnd.nextInt(5));
        freezer.setDeviceId(Integer.toString(rnd.nextInt(100)));
        freezer.setBrand("Buzdolabi");
        freezer.setModel("Buzdolabi");
        freezer.setLevel(rnd.nextInt(5));
        return freezer;
    }
}
