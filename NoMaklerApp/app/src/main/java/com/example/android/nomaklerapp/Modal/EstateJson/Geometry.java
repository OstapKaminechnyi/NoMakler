package com.example.android.nomaklerapp.Modal.EstateJson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Geometry {
    @JsonProperty("location") private NativeLocation nativeLocation;

    public Geometry() {

    }

    public Geometry(NativeLocation nativeLocation) {
        this.nativeLocation = nativeLocation;
    }

    public NativeLocation getNativeLocation() {
        return nativeLocation;
    }

    public void setNativeLocation(NativeLocation nativeLocation) {
        this.nativeLocation = nativeLocation;
    }
}
