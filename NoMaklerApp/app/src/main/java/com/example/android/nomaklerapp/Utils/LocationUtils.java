package com.example.android.nomaklerapp.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import com.example.android.nomaklerapp.R;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;


public class LocationUtils {
    private static final String LOG_TAG = LocationUtils.class.getSimpleName();

    private static Location getCurrentLocation(Activity activity, GoogleApiClient googleApiClient, int requestCodePermission) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, requestCodePermission);

        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        return location;
    }

    public static LatLng getCurretnLatLng(Activity activity, GoogleApiClient googleApiClient, int requestCodePermission){
        Location location = getCurrentLocation(activity, googleApiClient, requestCodePermission);
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

//    public static String getAddressString(Context context, LatLng latLng){
//        return getAddress(context, latLng).getAddressLine(0);
//    }
//
//    public static String getPostalCode(Context context,LatLng latLng){
//        return getAddress(context, latLng).getPostalCode();
//    }

    public static Address getAddress(Context context, LatLng latLng){
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 10);
            if(addresses == null || addresses.isEmpty()){
                return null;
            }
            for(Address address: addresses){
                String postalCode = address.getPostalCode();
                if(postalCode != null){
                    return address;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static PendingResult<PlaceBuffer> getPlaceBufferPendingResult(GoogleApiClient googleApiClient, AutocompletePrediction prediction){
        if(googleApiClient == null || prediction == null){
            return null;
        }
        String placeId = prediction.getPlaceId();
        return Places.GeoDataApi.getPlaceById(googleApiClient ,placeId);

    }

    public static String getPlaceID(Context context, double lat, double lng){
        String addressQuery = lat + "," + lng;
        String baseURL = "https://maps.googleapis.com/maps/api/geocode/json?";

        Uri uri = Uri.parse(baseURL);
        Uri.Builder uriBuilder = uri.buildUpon();
        uriBuilder.appendQueryParameter("address", addressQuery);
        uriBuilder.appendQueryParameter("key", context.getString(R.string.goog_e_api_key));
        String urlString = uriBuilder.toString();

        String jsonResponse = JsonUtil.getJasonResponse(urlString);
        if(jsonResponse == null){
            Log.d(LOG_TAG, "jsonResponse == null");
            return null;
        }
        String placeId = null;
        try {
            JSONObject rootObj = new JSONObject(jsonResponse);
            JSONObject resultObj = rootObj.optJSONArray(Constants.GEO_CODING_results).getJSONObject(0);
            placeId = resultObj.getString(Constants.GEO_CODING_place_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return placeId;
    }

}
