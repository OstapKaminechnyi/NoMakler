package com.example.android.nomaklerapp.Utils;

import com.example.android.nomaklerapp.Modal.Contracts;


public class Constants {
    public static final String DB_CLAUSE_QUERY = " =? ";
    public static final String DB_CLAUSE_OR = " OR ";
    public static final String DB_CLAUSE_END = " AND ";
    public static final String DB_CLAUSE_IS_NULL = " is null ";
    /**
     * @SQLite
     */
    public static final String SQLITE_CLAUSE_ALTER = "ALTER TABLE ";
    public static final String SQLITE_CLAUSE_RENAME = " RENAME TO ";
    /**
     * @GeoCodingApi
     */
    public static final String GEO_CODING_place_id = "place_id";
    public static final String GEO_CODING_results = "results";

    public static final String ESTATE_EXTRA_KEY_LATITUDE = Contracts.Estate.LATITUDE;
    public static final String ESTATE_EXTRA_KEY_LONGTITUDE = Contracts.Estate.LONGTITUDE;

    /**
     * @MainActivity
     * @initLat
     * @initLng
     */
    public static final String MAIN_ACTIVITY_INIT_LAT_KEY = "MAIN_ACTIVITY_INIT_LAT_KEY";
    public static final String MAIN_ACTIVITY_INIT_LNG_KEY = "MAIN_ACTIVITY_INIT_LNG_KEY";

    public static final String BUNDLE_KEY_NORTH_LAT = "BUNDLE_KEY_NORTH_LAT";
    public static final String BUNDLE_KEY_NORTH_LNG = "BUNDLE_KEY_NORTH_LNG";
    public static final String BUNDLE_KEY_SOUTH_LAT = "BUNDLE_KEY_SOUTH_LAT";
    public static final String BUNDLE_KEY_SOUTH_LNG = "BUNDLE_KEY_SOUTH_LNG";


    public static final String INIT_LAT_KEY = "INIT_LAT_KEY";
    public static final String INIT_LNG_KEY = "INIT_LNG_KEY";
    public static final String CURRENT_USER_TOKEN = "CURRENT_USER_TOKEN";
}
