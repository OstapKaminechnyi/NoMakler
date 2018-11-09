package com.example.android.nomaklerapp.Modal.Estate_Image;

import com.example.android.nomaklerapp.Modal.AppDBHelper;
import com.example.android.nomaklerapp.Modal.Contracts;
import com.example.android.nomaklerapp.Modal.ThirdTableProvider;


public class EstateAndImageProvider extends ThirdTableProvider {
    public static final String AUTHOTIRY = "com.example.android.nomaklerapp.Modal.Estate_Image.EstateAndImageProvider";
    @Override
    public boolean onCreate() {
        initialize(
                AUTHOTIRY,
                new AppDBHelper(getContext()),
                Contracts.EstateAndImage.CONTENT_URI,
                Contracts.EstateAndImage.DEFAULT_PROJECTION,
                Contracts.EstateAndImage.ESTATE_PRIMARY,
                Contracts.EstateAndImage.IMAGE_PRIMARY

        );
        return true;
    }
}
