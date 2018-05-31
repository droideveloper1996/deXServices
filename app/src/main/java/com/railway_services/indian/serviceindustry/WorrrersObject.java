package com.railway_services.indian.serviceindustry;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Abhishek on 05-04-2018.
 */

public class WorrrersObject {

    public ArrayList<String> getWorkerName() {
        return workerName;
    }

    public void setWorkerName(ArrayList<String> workerName) {
        this.workerName = workerName;
    }

    public ArrayList<String> getWorkerMobile() {
        return workerMobile;
    }

    public void setWorkerMobile(ArrayList<String> workerMobile) {
        this.workerMobile = workerMobile;
    }

    public ArrayList<LatLng> getCoorDinates() {
        return coorDinates;
    }

    public void setCoorDinates(ArrayList<LatLng> coorDinates) {
        this.coorDinates = coorDinates;
    }

    ArrayList<String> workerName;
    ArrayList<String> workerMobile;
    ArrayList<LatLng> coorDinates;
}
