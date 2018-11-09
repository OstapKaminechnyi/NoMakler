package com.example.android.nomaklerapp.Utils;

import android.content.ContentValues;
import android.content.Context;
import com.example.android.nomaklerapp.GenerateEstateLoader;
import com.example.android.nomaklerapp.Modal.Contracts;
import com.example.android.nomaklerapp.Modal.Estate.Estate;
import com.example.android.nomaklerapp.Modal.User.User;
import android.location.Address;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;


public final class ContentProviderUtils {
    private ContentProviderUtils(){}

    public static boolean isValidContentValues(ContentValues values, String[] projections){
        if(values == null || projections == null){
        return false;
        }

        for(int i = 0; i < projections.length; i++){
            String key = projections[i];
            if(!values.containsKey(key)){
                return false;
            }
        }
        return true;
    }

    public static LatLng getLatLng(ContentValues values){
        if(!(values.containsKey(Contracts.Estate.LATITUDE) && values.containsKey(Contracts.Estate.LONGTITUDE))){
            return null;
        }

        return new LatLng(
                values.getAsDouble(Contracts.Estate.LATITUDE),
                values.getAsDouble(Contracts.Estate.LONGTITUDE)
        );

    }

    public static ContentValues createContentValue(Estate estate){
        if(estate == null){
            return null;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(Contracts.Estate.ADDRESS, estate.getAddress());
        contentValues.put(Contracts.Estate.PRICE, estate.getPrice());
        contentValues.put(Contracts.Estate.PLACE_ID, estate.getPlaceID());
        contentValues.put(Contracts.Estate.POSTAL_CODE, estate.getPostalCode());
        contentValues.put(Contracts.Estate.LATITUDE, estate.getLat());
        contentValues.put(Contracts.Estate.LONGTITUDE, estate.getLng());
        return contentValues;
    }

    public static ContentValues createContentValue(Context context, Address address) {
        if (address != null) {
            double actualLat = address.getLatitude();
            double actualLng = address.getLongitude();
            long price = (long) Utils.getRandom(GenerateEstateLoader.MIN_PRICE, GenerateEstateLoader.MAX_PRICE);
            String placeID = LocationUtils.getPlaceID(context, actualLat, actualLng);
            String addressString = address.getAddressLine(0);
            String postalCode = address.getPostalCode();

            ContentValues values = new ContentValues();
            values.put(Contracts.Estate.ADDRESS, addressString);
            values.put(Contracts.Estate.LATITUDE, actualLat);
            values.put(Contracts.Estate.LONGTITUDE, actualLng);
            values.put(Contracts.Estate.POSTAL_CODE, postalCode);
            values.put(Contracts.Estate.PLACE_ID, placeID);
            values.put(Contracts.Estate.PRICE, price);

            return values;

        }
        return null;
    }

    public static ContentValues getContentValues(User user){
        if(user == null){
            return null;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(Contracts.User.EMAIL, user.getEmail());
        contentValues.put(Contracts.User.NAME, user.getUserName());
        contentValues.put(Contracts.User.TOKEN, user.getToken());
        contentValues.put(Contracts.User.JOINED_DATE_MILLIS, user.getJoinTime_millis());
        return contentValues;
    }

    public static ContentValues getContentValue(com.example.android.nomaklerapp.Modal.Estate_User.UserAndEstate userAndEstate){
        if(userAndEstate == null){
            return null;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(Contracts.UserAndEstate.ESTATE_TOKEN, userAndEstate.getEstateToken());
        contentValues.put(Contracts.UserAndEstate.USER_TOKEN, userAndEstate.getUserToken());
        return contentValues;
    }

    public static int deleteRows_3rdTable(Context context, Uri tableUri, String token1Slection, String token2Selection, String token1, String token2){
        String selection = token1Slection + "=? AND " + token2Selection + "=?";
        String[] selectors = new String[]{
                token1,
                token2
        };

        return context.getContentResolver().delete(
                tableUri,
                selection,
                selectors
        );

    }

    public static String getLastString(Uri uri){
        String[] tokens = uri.toString().split("/");
        return tokens[tokens.length - 1];
    }

    public static Uri getBothPrimaryUri(Uri tableUri, String firstPrimaryKey, String firstPrimaryValue, String secondPrimaryKey, String secondPrimaryValue){
        StringBuilder builder = new StringBuilder(tableUri.toString());
        builder.append("/")
                .append(firstPrimaryKey)
                .append("/")
                .append(firstPrimaryValue)
                .append("/")
                .append(secondPrimaryKey)
                .append("/")
                .append(secondPrimaryValue);
        return Uri.parse(builder.toString());
    }

}
