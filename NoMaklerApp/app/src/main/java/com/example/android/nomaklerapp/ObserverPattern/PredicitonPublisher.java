package com.example.android.nomaklerapp.ObserverPattern;

import android.util.Log;

import com.google.android.gms.location.places.AutocompletePrediction;

import java.util.ArrayList;
import java.util.List;


public class PredicitonPublisher implements Subject<PredictionObserver> {

    private static transient PredicitonPublisher instance;
    public static PredicitonPublisher getInstance(){
        if(instance == null){
            synchronized (PredicitonPublisher.class){
                instance = new PredicitonPublisher();
            }
        }
        return instance;
    }


    private static final String LOG_TAG = PredicitonPublisher.class.getSimpleName();
    private List<PredictionObserver> observers;
    private AutocompletePrediction autocompletePrediction;

    private PredicitonPublisher(){
        observers = new ArrayList<>();
    }
    @Override
    public void registerObserver(PredictionObserver predictionObserver) {
        if(predictionObserver == null){
            Log.d(LOG_TAG, "predictionObserver == null");
            return;
        }
        observers.add(predictionObserver);
    }

    @Override
    public void unregister(PredictionObserver predictionObserver) {
        if(predictionObserver == null){
            Log.d(LOG_TAG, "predictionObserver == null");
            return;
        }
        observers.remove(observers);
    }

    @Override
    public void notifyData() {
        if(observers.isEmpty()){
            Log.d(LOG_TAG, "observers.isEmpty()");
            return;
        }

        if(autocompletePrediction == null){
            Log.d(LOG_TAG, "autocompletePrediction == null");
            return;
        }

        for(PredictionObserver observer : observers){
            observer.update(autocompletePrediction);
        }
    }

    public void setAutocompletePrediction(AutocompletePrediction autocompletePrediction) {
        this.autocompletePrediction = autocompletePrediction;
    }
}
