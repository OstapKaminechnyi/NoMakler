package com.example.android.nomaklerapp.Modal.Estate;

import android.content.ContentValues;
import android.database.Cursor;
import com.example.android.nomaklerapp.Modal.Contracts;
import com.example.android.nomaklerapp.Utils.ContentProviderUtils;

import java.io.Serializable;


public class Estate implements Serializable {
    public static final double MIN_PRICE = 100000;
    public static final double MAX_PRICE = 5000000;
    private String address;
    private double lat, lng;
    private String postalCode;
    private String placeID;
    private double price;

    private Estate(String address, double price, String postalCode, String placeID, double lat, double lng) {
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.placeID = placeID;
        this.price = price;
        this.postalCode = postalCode;
    }



    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPlaceID() {
        return placeID;
    }

    public double getPrice() {
        return price;
    }


    @Override
    public boolean equals(Object object) {
        if(!(object instanceof Estate) || object == null){
            return false;
        }

        Estate otherEstate = (Estate) object;
        return this.placeID.equalsIgnoreCase(otherEstate.getPlaceID());

    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(address + " - price: ")
                .append(price + " - postalCode: ")
                .append(postalCode + " - placeID: ")
                .append(placeID + " - lat: ")
                .append(lat + " - long: ")
                .append(lng)
                .toString();
    }

    public static Estate newInstance(ContentValues contentValues){
        if(!ContentProviderUtils.isValidContentValues(contentValues, Contracts.Estate.DEFAULT_PROJECTION)){
            return null;
        }

        return  new Estate(
                contentValues.getAsString(Contracts.Estate.ADDRESS),
                contentValues.getAsDouble(Contracts.Estate.PRICE),
                contentValues.getAsString(Contracts.Estate.POSTAL_CODE),
                contentValues.getAsString(Contracts.Estate.PLACE_ID),
                contentValues.getAsDouble(Contracts.Estate.LATITUDE),
                contentValues.getAsDouble(Contracts.Estate.LONGTITUDE)
        );
    }

    public static Estate newInstance(Cursor estatesCursor){
        if(estatesCursor == null){
            return null;
        }
        int addressIndex = estatesCursor.getColumnIndex(Contracts.Estate.ADDRESS);
        int placeIdIndex = estatesCursor.getColumnIndex(Contracts.Estate.PLACE_ID);
        int priceIndex = estatesCursor.getColumnIndex(Contracts.Estate.PRICE);
        int postalCaodeIndex = estatesCursor.getColumnIndex(Contracts.Estate.POSTAL_CODE);
        int latIndex = estatesCursor.getColumnIndex(Contracts.Estate.LATITUDE);
        int lngIndex = estatesCursor.getColumnIndex(Contracts.Estate.LONGTITUDE);

        String address = estatesCursor.getString(addressIndex);
        double price = estatesCursor.getDouble(priceIndex);
        String postalCode = estatesCursor.getString(postalCaodeIndex);
        String placeId = estatesCursor.getString(placeIdIndex);
        double lat = estatesCursor.getDouble(latIndex);
        double lng= estatesCursor.getDouble(lngIndex);

        return new Estate(address, price, postalCode, placeId, lat, lng);

    }

    /**
     * @BUILDER
     */
    public static class Builder{
        private String address;
        private double price;
        private String postalCode;
        private String placeID;
        private double lat, lng;

        public Builder setAddress(String address){
            this.address = address;
            return this;
        }

        public Builder setLat(double lat){
            this.lat = lat;
            return this;
        }
        public Builder setLng(double lng){
            this.lng = lng;
            return this;
        }

        public Builder setPrice(double price){
            this.price = price;
            return this;
        }

        public Builder setPostalCode(String postalCode){
            this.postalCode = postalCode;
            return this;
        }

        public Builder setPlaceId(String placeId){
            this.placeID = placeId;
            return this;
        }

        public Estate build(){
            return new Estate(address, price, postalCode, placeID, lat, lng);
        }
    }
}
