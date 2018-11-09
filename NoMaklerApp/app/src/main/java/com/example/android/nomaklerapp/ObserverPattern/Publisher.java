package com.example.android.nomaklerapp.ObserverPattern;

import java.util.ArrayList;
import java.util.List;


public abstract class Publisher<T> implements Subject<T> {
    List<T> observers;
    public Publisher(){
        observers = new ArrayList<T>();
    }
    @Override
    public void registerObserver(T t) {
        if(t != null){
            observers.add(t);
        }
    }

    @Override
    public void unregister(T t) {
        if(t != null){
            observers.remove(t);
        }
    }

    @Override
    public void notifyData() {
        for(T t: observers){
            updateObserver(t);
        }
    }

    protected abstract void updateObserver(T t);
}
