package com.example.android.nomaklerapp.Modal.Estate_User;

import android.database.Cursor;
import com.example.android.nomaklerapp.Modal.Contracts;


public class UserAndEstate {

    public static UserAndEstate newInstance(Cursor cursor){
        String userToken = cursor.getString(Contracts.UserAndEstate.INDEX_USER_TOKEN);
        String estateToken = cursor.getString(Contracts.UserAndEstate.INDEX_ESTATE_TOKEN);
        return new UserAndEstate(userToken, estateToken);
    }

    private String userToken;
    private String estateToken;

    public UserAndEstate(String userToken, String estateToken) {
        this.userToken = userToken;
        this.estateToken = estateToken;
    }

    public String getUserToken() {
        return userToken;
    }

    public String getEstateToken() {
        return estateToken;
    }

    @Override
    public String toString() {
        return "user's token: " + userToken + " - Estate's token: " + estateToken;
    }
}
