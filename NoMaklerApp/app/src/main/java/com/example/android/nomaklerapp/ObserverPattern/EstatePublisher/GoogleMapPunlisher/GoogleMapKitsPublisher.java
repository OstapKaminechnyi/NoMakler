package com.example.android.nomaklerapp.ObserverPattern.EstatePublisher.GoogleMapPunlisher;

import com.example.android.nomaklerapp.GoogleCallBack.GoogleMapKits;
import com.example.android.nomaklerapp.ObserverPattern.Publisher;


public class GoogleMapKitsPublisher extends Publisher<GoogleMapKitsObserver> {

    private static volatile GoogleMapKitsPublisher instance;
    public static GoogleMapKitsPublisher getInstance(){
        if(instance == null){
            synchronized (GoogleMapKitsPublisher.class){
                instance = new GoogleMapKitsPublisher();
            }
        }
        return  instance;
    }

    private GoogleMapKits googleMapKits;
    @Override
    protected void updateObserver(GoogleMapKitsObserver googleMapKitsObserver) {
        googleMapKitsObserver.update(googleMapKits);
    }

    public void setGoogleMapKits(GoogleMapKits googleMapKits) {
        this.googleMapKits = googleMapKits;
    }
}
