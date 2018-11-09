package com.example.android.nomaklerapp.Modal.EstateJson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonEstate {
//    @JsonProperty("address_components") private AddressComponentHolder addressComponentHolder;
    @JsonProperty("geometry") private Geometry geometry;
    @JsonProperty("formatted_address") private String address;
    @JsonProperty("place_id") private String placeId;

    public JsonEstate() {

    }

//    public AddressComponentHolder getAddressComponentHolder() {
//        return addressComponentHolder;
//    }
//
//    public void setAddressComponentHolder(AddressComponentHolder addressComponentHolder) {
//        this.addressComponentHolder = addressComponentHolder;
//    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPostalCode() {
//        return addressComponentHolder.getPostalCode();
        return null;
    }

}
