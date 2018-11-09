package com.example.android.nomaklerapp.ObserverPattern.LatLngBoundBunder;

import com.example.android.nomaklerapp.ObserverPattern.Subject;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;


public class LatLngBoundBundlePublisher implements Subject<LatLngBoundBundleObserver>{

    private static LatLngBoundBundlePublisher instance;
    public static LatLngBoundBundlePublisher getInstance(){
        if(instance == null){
            synchronized (LatLngBoundBundlePublisher.class){
                instance = new LatLngBoundBundlePublisher();
            }
        }
        return instance;
    }

    private List<LatLngBoundBundleObserver> observers;
    private Bundle latLngBoundBundle;
    public LatLngBoundBundlePublisher(){
        observers = new ArrayList<>();
    }

    @Override
    public void registerObserver(LatLngBoundBundleObserver observer) {
        if(observer != null){
            observers.add(observer);
        }
    }

    @Override
    public void unregister(LatLngBoundBundleObserver observer) {
        if(observer != null){
            observers.remove(observer);
        }
    }

    @Override
    public void notifyData() {
        if(!observers.isEmpty()){
            for(LatLngBoundBundleObserver observer: observers){
                observer.update(latLngBoundBundle);
            }
        }
    }

    public void setLatLngBoundBundle(Bundle latLngBoundBundle) {
        this.latLngBoundBundle = latLngBoundBundle;
    }
}
