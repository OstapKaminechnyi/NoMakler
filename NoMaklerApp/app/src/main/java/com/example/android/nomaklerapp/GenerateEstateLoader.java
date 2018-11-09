package com.example.android.nomaklerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.example.android.nomaklerapp.Modal.Contracts;
import com.example.android.nomaklerapp.Modal.Estate.Estate;
import com.example.android.nomaklerapp.Modal.EstateGenerator;
import com.example.android.nomaklerapp.Utils.Constants;
import com.example.android.nomaklerapp.Utils.Utils;
import com.example.android.nomaklerapp.ui.MainActivity;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Random;


public class GenerateEstateLoader extends AsyncTaskLoader<Object> {
    private static final String LOG_TAG = GenerateEstateLoader.class.getSimpleName();
    private static final int MAX_ESTATES = 5;
    public static long MIN_PRICE =  100000;
    public static long MAX_PRICE =  5000000;

    private Bundle latLngBoundBundle;
    private double minLat, maxLat, minLng, maxLng;
    private List<Estate> onScreeenEstates ;

    public GenerateEstateLoader(Context context, Bundle latLngBoundBundle) {
        super(context);
        this.latLngBoundBundle = latLngBoundBundle;
        if(context instanceof MainActivity){
            MainActivity mainActivity = (MainActivity) context;
            onScreeenEstates = mainActivity.getOnScreeenEstates();
        }
        forceLoad();
    }



    @Override
    public Cursor loadInBackground() {
        if(latLngBoundBundle == null){
            Log.d(LOG_TAG, "latLngBoundBundle == null");
            return null;
        }
        Log.d(LOG_TAG, "latLngBoundBundle != null");
        //--Find max and min latlng
        setupNorthAndSouthLatLng();

        //--Query all estate in minmax latlng range;
        String selection = Contracts.Estate.LATITUDE +" >=? AND "
                + Contracts.Estate.LATITUDE +" <=? AND "
                + Contracts.Estate.LONGTITUDE +" >=? AND "
                + Contracts.Estate.LONGTITUDE +" <=?  ";

        String[] selectors = new String[]{
                String.valueOf(minLat),
                String.valueOf(maxLat),
                String.valueOf(minLng),
                String.valueOf(maxLng),
        };

        Cursor initCursor = getContext().getContentResolver().query(
                Contracts.Estate.CONTENT_URI,
                null,
                selection,
                selectors,
                null
        );

        if(initCursor.getCount() < MAX_ESTATES){
            generateNewEsates(initCursor);
            initCursor = getContext().getContentResolver().query(
                    Contracts.Estate.CONTENT_URI,
                    null,
                    selection,
                    selectors,
                    null
            );

        }

        return initCursor;
    }


    /**
     * @setupNorthAndSouthLatLng
     */
    private void setupNorthAndSouthLatLng(){
        double latNortheast = latLngBoundBundle.getDouble(Constants.BUNDLE_KEY_NORTH_LAT);
        double lngNortheast = latLngBoundBundle.getDouble(Constants.BUNDLE_KEY_NORTH_LNG);
        double latSouthwest = latLngBoundBundle.getDouble(Constants.BUNDLE_KEY_SOUTH_LAT);
        double lngSouthwest = latLngBoundBundle.getDouble(Constants.BUNDLE_KEY_SOUTH_LNG);

        minLat = Math.min(latNortheast, latSouthwest);
        maxLat = Math.max(latNortheast, latSouthwest);

        minLng = Math.min(lngNortheast, lngSouthwest);
        maxLng = Math.max(lngNortheast, lngSouthwest);
    }


    /**
     * @generateNewEsates
     * @param initCursor
     */
    private void generateNewEsates(Cursor initCursor){
        int needEstatesCount = MAX_ESTATES - initCursor.getCount();
        for(int i = 0; i < needEstatesCount; i++){
            double randLat = Utils.getRandom(minLat, maxLat);
            double randLng = Utils.getRandom(maxLng, minLng);
            Estate estate = null;
            int missed = 0;
            while (estate == null && missed < 3) {
                estate = EstateGenerator.generateEstate(new LatLng(randLat, randLng));
                Log.d(LOG_TAG, estate == null ? "ranomEstate == null": estate.toString());
                if( estate == null ) {
                    missed++;
                }
            }

            if (estate != null) {

                insertEstateIntoTable(estate);
                //--Generate 2 random images for estate
                Cursor imagesCursor = getContext().getContentResolver().query(
                        Contracts.Image.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
                generate2RandomImagesForEstate(estate.getPlaceID(), imagesCursor);
            }



        }
    }

    /**
     * @generate2RandomImagesForEstate
     * @param placeID
     */
    private void generate2RandomImagesForEstate(String placeID, Cursor imagesCursor){
        int imagesCount = imagesCursor.getCount();
        int random1 = -1;
        int random2 = -1;
        while (random1 == random2) {
            random1 = new Random().nextInt(imagesCount - 1);
            random2 = new Random().nextInt(imagesCount - 1);
        }
        String link1 = "";
        String link2 = "";

        if(imagesCursor.moveToPosition(random1)){
            link1 = imagesCursor.getString(Contracts.Image.INDEX_LINK);
            ContentValues values = new ContentValues();
            values.put(Contracts.EstateAndImage.ESTATE_PRIMARY, placeID);
            values.put(Contracts.EstateAndImage.IMAGE_PRIMARY, link1);
            getContext().getContentResolver().insert(
                    Contracts.EstateAndImage.CONTENT_URI,
                    values
            );
        }
        if(imagesCursor.moveToPosition(random2)){
            link2 = imagesCursor.getString(Contracts.Image.INDEX_LINK);
            ContentValues values = new ContentValues();
            values.put(Contracts.EstateAndImage.ESTATE_PRIMARY, placeID);
            values.put(Contracts.EstateAndImage.IMAGE_PRIMARY, link2);
            getContext().getContentResolver().insert(
                    Contracts.EstateAndImage.CONTENT_URI,
                    values
            );
        }

    }

    private void insertEstateIntoTable(Estate estate) {
        if(estate == null) {
            return;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contracts.Estate.ADDRESS, estate.getAddress());
            contentValues.put(Contracts.Estate.LATITUDE, estate.getLat());
            contentValues.put(Contracts.Estate.LONGTITUDE, estate.getLng());
            contentValues.put(Contracts.Estate.POSTAL_CODE, estate.getPostalCode());
            contentValues.put(Contracts.Estate.PLACE_ID, estate.getPlaceID());
            contentValues.put(Contracts.Estate.PRICE, estate.getPrice());

            getContext().getContentResolver().insert(
                    Contracts.Estate.CONTENT_URI,
                    contentValues
            );
        }
    }
}
