package com.example.android.nomaklerapp.Modal.EstateJson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressComponent {
    private static final String POSTAL_CODE_KEY = "postal_code";
    @JsonProperty("long_name") private String name;
    @JsonProperty("types") private String type;

    public AddressComponent() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPostalCodeComponent() {
        return type.equalsIgnoreCase(POSTAL_CODE_KEY);
    }
}
