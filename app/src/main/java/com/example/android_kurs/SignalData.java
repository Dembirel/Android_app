package com.example.android_kurs;

public class SignalData {
    private int rsrp;
    private int rsrq;
    private int rssi;
    private int asuLevel;
    private int level;
    private String operator;
    private String mnc;
    private String mcc;
    private String bandwidth;
    private double latitude;
    private double longitude;

    // Конструкторы, геттеры и сеттеры
    public SignalData(int rsrp, int rsrq, int rssi, int asuLevel, int level, String operator, String mnc, String mcc, String bandwidth, double latitude, double longitude) {
        this.rsrp = rsrp;
        this.rsrq = rsrq;
        this.rssi = rssi;
        this.asuLevel = asuLevel;
        this.level = level;
        this.operator = operator;
        this.mnc = mnc;
        this.mcc = mcc;
        this.bandwidth = bandwidth;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Геттеры и сеттеры для всех полей
    public int getRsrp() {
        return rsrp;
    }

    public void setRsrp(int rsrp) {
        this.rsrp = rsrp;
    }

    public int getRsrq() {
        return rsrq;
    }

    public void setRsrq(int rsrq) {
        this.rsrq = rsrq;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getAsuLevel() {
        return asuLevel;
    }

    public void setAsuLevel(int asuLevel) {
        this.asuLevel = asuLevel;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(String bandwidth) {
        this.bandwidth = bandwidth;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
