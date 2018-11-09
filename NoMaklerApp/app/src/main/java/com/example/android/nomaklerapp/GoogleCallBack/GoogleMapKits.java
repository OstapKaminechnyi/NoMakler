package com.example.android.nomaklerapp.GoogleCallBack;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import com.example.android.nomaklerapp.AppLoaderCallBack;
import com.example.android.nomaklerapp.Modal.Contracts;
import com.example.android.nomaklerapp.ObserverPattern.EstatePublisher.GoogleMapPunlisher.GoogleMapKitsPublisher;
import com.example.android.nomaklerapp.ObserverPattern.OnSreenEstateCursor.OnScreenEstateCursorObserver;
import com.example.android.nomaklerapp.ObserverPattern.OnSreenEstateCursor.OnScreenEstateCursorPublisher;
import com.example.android.nomaklerapp.R;
import com.example.android.nomaklerapp.Utils.Constants;
import com.example.android.nomaklerapp.ui.MainActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class GoogleMapKits implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener
        , GoogleMap.OnCameraMoveListener
        ,OnScreenEstateCursorObserver {
    private static final String LOG_TAG = GoogleMapKits.class.getSimpleName();

    private static float MARKER_DEFAULT_COLOR = BitmapDescriptorFactory.HUE_ROSE;
    private static final LatLng DEFAULT_LATLNG = new LatLng(49.843970, 24.026339);
    private static final int DEFAULT_CAMERA_ZOOM = 15;
    public static final int MAX_MARKERS = 5;

    private GoogleMap mMap;
    private Circle movableCircle;

    private Context context;
    private AppCompatActivity appCompatActivity;
    private List<Marker> markers;
    private List<Marker> rvMarkers;

    private LatLng currentLatLng;

    private ProgressBar progressBar;


    private LoaderManager.LoaderCallbacks<Object> appLoaderCallback;

    public GoogleMapKits(Context context, ProgressBar progressBar) {
        this.progressBar = progressBar;

        this.context = context;
        if(context instanceof AppCompatActivity){
            appCompatActivity = (AppCompatActivity) context;
        }
        //--markers
        markers = new ArrayList<>();
        rvMarkers = new ArrayList<>();


        //--appLoaderCallback
        appLoaderCallback = new AppLoaderCallBack(context);

        //--OnScreenEstateCursorPublisher
        OnScreenEstateCursorPublisher.getInstance().registerObserver(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(context instanceof Activity){
                    Activity activity = (Activity) context;
                    if(activity.getCurrentFocus() !=null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                    }
                }

                if(movableCircle != null){
                    movableCircle.remove();
                }

                movableCircle = mMap.addCircle(
                        new CircleOptions().center(latLng)
                                            .radius(70)

                );

            }
        });
        currentLatLng = DEFAULT_LATLNG;
//        if(context instanceof MainActivity){
//            MainActivity mainActivity= (MainActivity) context;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            String initlat = sharedPreferences.getString(Constants.INIT_LAT_KEY, null);
            String initLng= sharedPreferences.getString(Constants.INIT_LNG_KEY, null);
            if(initlat != null && initLng != null) {
                Log.d(LOG_TAG, "initlat : " + initlat + "initLng : " + initLng);
                currentLatLng = new LatLng(Double.valueOf(initlat), Double.valueOf(initLng));
            }else{
                Log.d(LOG_TAG, initlat == null ? "initlat == null " : "");
                Log.d(LOG_TAG, initLng== null ? "initLng == null " : "");
                Log.d(LOG_TAG, sharedPreferences.contains(Constants.INIT_LAT_KEY)? "sharedPreferences.contains(Constants.INIT_LAT_KEY)": "! sharedPreferences.contains(Constants.INIT_LAT_KEY)");
                Log.d(LOG_TAG, sharedPreferences.contains(Constants.INIT_LNG_KEY)? "sharedPreferences.contains(Constants.INIT_LNG_KEY)": "! sharedPreferences.contains(Constants.INIT_LNG_KEY)");
            }
//        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_CAMERA_ZOOM));
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveListener(this);


    }

    @Override
    public void onCameraIdle() {
        progressBar.setVisibility(View.VISIBLE);


        LatLngBounds screenLatLngBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        if(appCompatActivity != null ){
            LatLng northeastLatLng = screenLatLngBounds.northeast;
            LatLng southwestLatLng = screenLatLngBounds.southwest;

            double latNortheast = northeastLatLng.latitude;
            double lngNortheast = northeastLatLng.longitude;
            double latSouthwest = southwestLatLng.latitude;
            double lngSouthwest = southwestLatLng.longitude;

            Bundle bundle = new Bundle();
            bundle.putDouble(Constants.BUNDLE_KEY_NORTH_LAT, latNortheast);
            bundle.putDouble(Constants.BUNDLE_KEY_NORTH_LNG, lngNortheast);
            bundle.putDouble(Constants.BUNDLE_KEY_SOUTH_LAT, latSouthwest);
            bundle.putDouble(Constants.BUNDLE_KEY_SOUTH_LNG, lngSouthwest);
            appCompatActivity.getSupportLoaderManager().restartLoader(MainActivity.ESTATE_LOADER_ID, bundle, appLoaderCallback);
        }
    }

    @Override
    public void onCameraMove() {
        if(appCompatActivity != null){
            appCompatActivity.getSupportLoaderManager().destroyLoader(MainActivity.ESTATE_LOADER_ID);
        }
    }

    @Override
    public void update(Cursor onScreenEstateCursor) {
        if(onScreenEstateCursor == null || onScreenEstateCursor.getCount() == 0){
            Log.d(LOG_TAG, onScreenEstateCursor == null ? "onScreenEstateCursor == null" : "onScreenEstateCursor.getCount() == 0" );
            return;
        }

        Log.d(LOG_TAG, "onScreenEstateCursor.getCount(): " +  onScreenEstateCursor.getCount());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String markerColorString = sharedPreferences.getString(
                context.getString(R.string.pref_marker_color_key),
                MARKER_DEFAULT_COLOR + ""
        );
        float markerColor = Float.parseFloat(markerColorString);

        //-- Remove old markers
        if(markers != null || !markers.isEmpty()){
            for(Marker marker: markers){
                marker.remove();
            }
            markers.clear();
        }


        //-- Add new markers
        for(int i = 0; i < onScreenEstateCursor.getCount(); i++){
            if(onScreenEstateCursor.moveToPosition(i)){
                double lat = onScreenEstateCursor.getDouble(Contracts.Estate.INDEX_LATITUDE);
                double lng = onScreenEstateCursor.getDouble(Contracts.Estate.INDEX_LONGTITUDE);
                String address = onScreenEstateCursor.getString(Contracts.Estate.INDEX_ADDRESS);

                Log.d(LOG_TAG, "lat: " + lat + " - lng:" + lng);
                if(mMap != null) {
                    Marker marker = mMap.addMarker(
                            new MarkerOptions().title(address)
                                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                                    .position(new LatLng(lat, lng))
                                    .alpha(0.8f)
                    );
                    markers.add(marker);
                    GoogleMapKitsPublisher.getInstance().setGoogleMapKits(this);
                    GoogleMapKitsPublisher.getInstance().notifyData();
                }
            }
        }
        Log.d(LOG_TAG, "markers.size(): " + markers.size());

        progressBar.setVisibility(View.GONE);

    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public List<Marker> getMarkers() {
        return markers;
    }

    public List<Marker> getRvMarkers() {
        return rvMarkers;
    }
}
