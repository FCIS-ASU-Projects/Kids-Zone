package com.example.kidszone.model;

import java.util.Date;
import java.util.List;

public class BlockProfile {
    List<String> packageName;
    Date hourStartTime;//Timer Sara
    String dayStartTime;//Timer Sara
    Date hourEndTime;//Timer Sara
    String dayEndTime;//Timer Sara

    public BlockProfile(List<String> packageName, Date hourStartTime, String dayStartTime, Date hourEndTime, String dayEndTime) {
        this.packageName = packageName;
        this.hourStartTime = hourStartTime;
        this.dayStartTime = dayStartTime;
        this.hourEndTime = hourEndTime;
        this.dayEndTime = dayEndTime;
    }

    public List<String> getPackageName() {
        return packageName;
    }

    public void setPackageName(List<String> packageName) {
        this.packageName = packageName;
    }

    public Date getHourStartTime() {
        return hourStartTime;
    }

    public void setHourStartTime(Date hourStartTime) {
        this.hourStartTime = hourStartTime;
    }

    public String getDayStartTime() {
        return dayStartTime;
    }

    public void setDayStartTime(String dayStartTime) {
        this.dayStartTime = dayStartTime;
    }

    public Date getHourEndTime() {
        return hourEndTime;
    }

    public void setHourEndTime(Date hourEndTime) {
        this.hourEndTime = hourEndTime;
    }

    public String getDayEndTime() {
        return dayEndTime;
    }

    public void setDayEndTime(String dayEndTime) {
        this.dayEndTime = dayEndTime;
    }
}
