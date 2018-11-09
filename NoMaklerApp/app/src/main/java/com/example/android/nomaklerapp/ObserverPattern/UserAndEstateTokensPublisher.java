package com.example.android.nomaklerapp.ObserverPattern;

import java.util.ArrayList;
import java.util.List;


public class UserAndEstateTokensPublisher implements Subject<UserAndEstateTokensObserver> {
    private List<UserAndEstateTokensObserver> observers;
    private String userToken;
    private String estateToken;
    public UserAndEstateTokensPublisher(){
        observers = new ArrayList<>();
    }
    @Override
    public void registerObserver(UserAndEstateTokensObserver observer) {
        if(observer == null){
            return;
        }
        observers.add(observer);
    }

    @Override
    public void unregister(UserAndEstateTokensObserver observer) {
        if(observer == null){
            return;
        }
        observers.remove(observer);
    }

    @Override
    public void notifyData() {
        for(UserAndEstateTokensObserver observer: observers){
            observer.update(userToken, estateToken);
        }
    }

    public void setTokens(String userToken, String estateToken){
        this.userToken = userToken;
        this.estateToken = estateToken;
    }
}
