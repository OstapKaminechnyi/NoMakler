package com.example.android.nomaklerapp.ObserverPattern;



public interface Subject<T> {
    void registerObserver(T t);
    void unregister(T t);
    void notifyData();
}
