package com.mythleds.ikigai.Class;

public class Ble_devices {
    String device_name,device_id;

    public Ble_devices() {
    }

    public Ble_devices(String device_name, String device_id) {
        this.device_name = device_name;
        this.device_id = device_id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
