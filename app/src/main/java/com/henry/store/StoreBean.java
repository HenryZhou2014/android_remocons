package com.henry.store;

import com.henry.view.MeterTextView;
import com.henry.view.SecondAreaButton;
import com.henry.view.SpecButton;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Henry on 2015/8/19.
 */
public class StoreBean implements Serializable{
    private int threeStatus[];
    private String[] threeMeter;
    private int twoStatus[];
    private String[] twoMeter;
    private String[] meters;
    private int oneStatus[];
    private String[] oneMeter;
    public String[] getThreeMeter() {
        return threeMeter;
    }

    public void setThreeMeter(String[] threeMeter) {
        this.threeMeter = threeMeter;
    }

    public int[] getTwoStatus() {
        return twoStatus;
    }

    public void setTwoStatus(int[] twoStatus) {
        this.twoStatus = twoStatus;
    }

    public int[] getThreeStatus() {
        return threeStatus;
    }

    public void setThreeStatus(int[] threeStatus) {
        this.threeStatus = threeStatus;
    }

    public String[] getMeters() {
        return meters;
    }

    public void setMeters(String[] meters) {
        this.meters = meters;
    }

    public String[] getTwoMeter() {
        return twoMeter;
    }

    public void setTwoMeter(String[] twoMeter) {
        this.twoMeter = twoMeter;
    }

    public String[] getOneMeter() {
        return oneMeter;
    }

    public void setOneMeter(String[] oneMeter) {
        this.oneMeter = oneMeter;
    }

    public int[] getOneStatus() {
        return oneStatus;
    }

    public void setOneStatus(int[] oneStatus) {
        this.oneStatus = oneStatus;
    }
}
