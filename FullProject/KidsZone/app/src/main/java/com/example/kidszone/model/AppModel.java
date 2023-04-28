package com.example.kidszone.model;

import android.graphics.drawable.Drawable;

public class AppModel {
    // int age;
    String appName;
    Drawable icon;
    int status;
    String packageName;
    int ageRating;

    public AppModel(String appName, Drawable icon, int status, String packageName,int ageRating) {
        this.appName = appName;
        this.icon = icon;
        this.status = status;
        this.packageName = packageName;
        this.ageRating=ageRating;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getStatus() {
        return status;
    }

    public void setAgeRating(int ageRating) {
        this.ageRating = ageRating;
    }

    public int getAgeRating() {
        return ageRating;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int compareTo(AppModel app) {
        return this.getAppName().compareTo(app.appName);
    }
}
