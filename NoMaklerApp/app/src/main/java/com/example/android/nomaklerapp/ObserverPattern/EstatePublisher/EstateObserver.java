package com.example.android.nomaklerapp.ObserverPattern.EstatePublisher;

import com.example.android.nomaklerapp.Modal.Estate.Estate;

import java.util.List;


public interface EstateObserver {
    void update(List<Estate> estates);
}
