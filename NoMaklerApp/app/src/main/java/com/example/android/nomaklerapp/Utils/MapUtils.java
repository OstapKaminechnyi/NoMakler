package com.example.android.nomaklerapp.Utils;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;


public final class MapUtils {
    private MapUtils(){}

    private void setCameraForMap(GoogleMap map, Place place, float zoom){
        if(map == null || place == null){
            return;
        }
        LatLng latLng = place.getLatLng();
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
}
