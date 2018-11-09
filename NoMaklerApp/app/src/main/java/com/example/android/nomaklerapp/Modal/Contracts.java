package com.example.android.nomaklerapp.Modal;

import com.example.android.nomaklerapp.Modal.Estate.EstateProvider;
import com.example.android.nomaklerapp.Modal.Estate_Image.EstateAndImageProvider;
import com.example.android.nomaklerapp.Modal.Estate_User.UserAndEstateProvider;
import com.example.android.nomaklerapp.Modal.Image.ImageProvider;
import com.example.android.nomaklerapp.Modal.User.UserProvider;
import android.net.Uri;
import android.provider.BaseColumns;


public final class Contracts {
    public static final String DB_NAME = "estates.db";
    public static final int DB_CURRENT_VERSION = 15;
    private Contracts(){}

    public static final class Estate implements BaseColumns {
        private Estate(){}

        public static final String TABLLE = "estates";
        public static final String ADDRESS = "address";
        public static final String LATITUDE = "lat";
        public static final String LONGTITUDE = "lng";
        public static final String PLACE_ID = "placeID";
        public static final String POSTAL_CODE = "postalCode";
        public static final String PRICE = "price";

        public static final String[] DEFAULT_PROJECTION = new String[]{
                ADDRESS,
                PRICE,
                POSTAL_CODE,
                PLACE_ID,
                LATITUDE,
                LONGTITUDE
        };
        public static final int INDEX_ADDRESS = 0;
        public static final int INDEX_PRICE = 1;
        public static final int INDEX_POSTAL_CODE = 2;
        public static final int INDEX_PLACE_ID = 3;
        public static final int INDEX_LATITUDE  = 4;
        public static final int INDEX_LONGTITUDE  = 5;
        private static final Uri BASE_URI = Uri.parse("content://" + EstateProvider.AUTHOTITY );
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, TABLLE);

    }

    public static  class User implements BaseColumns {
        public static final String TABLE = "users";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String TOKEN = "token";
        public static final String JOINED_DATE_MILLIS = "joined_date_millis";

        public static final String[] DEFAUKT_PROJECTION =  new String[]{
                TOKEN,
                NAME,
                EMAIL,
                JOINED_DATE_MILLIS
        };

        public static final int TOKEN_INDEX = 0;
        public static final int NAME_INDEX = 1;
        public static final int EMAIL_INDEX = 2;
        public static final int JOINED_DATE_MILLIS_INDEX = 4;

        public static final Uri CONTENT_URI = Uri.parse("content://" + UserProvider.AUTHORITY + "/" + TABLE);
    }

    public static class UserAndEstate implements BaseColumns {
        public static final String TABLE = "user_estate";
        public static final String USER_TOKEN = "user_token";
        public static final String ESTATE_TOKEN = "estate_token";

        public static final String[] DEFAULT_PROJECTION = new String[]{
                USER_TOKEN,
                ESTATE_TOKEN
        };
        public static final int INDEX_USER_TOKEN = 0;
        public static final int INDEX_ESTATE_TOKEN = 1;

        public static final Uri CONTENT_URI = Uri.parse("content://" + UserAndEstateProvider.AUTHORITY + "/" + TABLE);
        public static final Uri USER_CONTENT_URI = Uri.parse("content://" + UserAndEstateProvider.AUTHORITY + "/" + TABLE + "/user/");
        public static final Uri ESTATE_CONTENT_URI = Uri.parse("content://" + UserAndEstateProvider.AUTHORITY + "/" + TABLE + "/estate/");

    }

    public static class Image implements BaseColumns {
        public static final String TABLE = "images";

        public static final String LINK = "link";

        public static final String[] DEFAULT_PROJECTION = new String[]{
                LINK
        };

        public static final int INDEX_LINK = 0;

        public static final Uri CONTENT_URI = Uri.parse(
                "content://" + ImageProvider.AUTHORITY + "/" + TABLE
        );

    }

    public static class EstateAndImage implements BaseColumns {
        public static final String TABLE = "estate_image";

        public static  final String ESTATE_PRIMARY = Estate.PLACE_ID;
        public static  final String IMAGE_PRIMARY = Image.LINK;

        public static final String[] DEFAULT_PROJECTION = new String[]{
                ESTATE_PRIMARY,
                IMAGE_PRIMARY
        };

        public static final int INDEX_ESTATE_PRIMARY = 0;
        public static final int INDEX_IMAGE_PRIMARY = 1;

        public static final Uri CONTENT_URI = Uri.parse(
                "content://" + EstateAndImageProvider.AUTHOTIRY  + "/" + TABLE
        );
    }

}
