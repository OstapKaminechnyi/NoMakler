package com.example.android.nomaklerapp.ObserverPattern.Network;



public interface NetworkStateObserver {
    void notify(boolean isOnline);
}
