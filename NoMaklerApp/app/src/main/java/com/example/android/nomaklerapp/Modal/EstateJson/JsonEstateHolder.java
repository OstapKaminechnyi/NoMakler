package com.example.android.nomaklerapp.Modal.EstateJson;

import com.example.android.nomaklerapp.Modal.Estate.Estate;
import com.example.android.nomaklerapp.Utils.Utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonEstateHolder {
    @JsonProperty("results") private JsonEstate[] jsonEstates;

    public JsonEstateHolder() {

    }

    public JsonEstate[] getJsonEstates() {
        return jsonEstates;
    }

    public void setJsonEstates(JsonEstate[] jsonEstates) {
        this.jsonEstates = jsonEstates;
    }

    public Estate getFirstEstate() {
        if(jsonEstates != null && jsonEstates.length > 0) {
            JsonEstate jsonEstate = jsonEstates[0];
            Estate estate = new Estate.Builder()
                    .setLat( jsonEstate.getGeometry().getNativeLocation().getLat())
                    .setLng( jsonEstate.getGeometry().getNativeLocation().getLng())
                    .setPrice(Utils.getRandom(Estate.MIN_PRICE, Estate.MAX_PRICE))
                    .setPlaceId(jsonEstate.getPlaceId())
                    .setAddress(jsonEstate.getAddress())
                    .setPostalCode(jsonEstate.getPostalCode())
                    .build();
            return estate;
        } else {
            return null;
        }
    }
}
