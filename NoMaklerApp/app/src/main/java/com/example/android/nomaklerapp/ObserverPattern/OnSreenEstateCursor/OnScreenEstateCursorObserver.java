package com.example.android.nomaklerapp.ObserverPattern.OnSreenEstateCursor;

import android.database.Cursor;


public interface OnScreenEstateCursorObserver {
    void update(Cursor onScreenEstateCursor);
}
