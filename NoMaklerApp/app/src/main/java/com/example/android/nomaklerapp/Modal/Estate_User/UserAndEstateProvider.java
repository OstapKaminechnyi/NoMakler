package com.example.android.nomaklerapp.Modal.Estate_User;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import com.example.android.nomaklerapp.Modal.AppDBHelper;
import com.example.android.nomaklerapp.Modal.Contracts;
import com.example.android.nomaklerapp.Utils.ContentProviderUtils;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


public class UserAndEstateProvider extends ContentProvider {
    private static final String LOG_TAG = UserAndEstateProvider.class.getSimpleName();

    public static final String AUTHORITY = UserAndEstateProvider.class.getName();
    private static final int MATCHER_CODE_TABLE = 122;
    private static final int MATCHER_CODE_USER = 123;
    private static final int MATCHER_CODE_ESTATE = 124;

    private AppDBHelper helper;
    private UriMatcher matcher;

    @Override
    public boolean onCreate() {
        helper = new AppDBHelper(getContext());

        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, Contracts.UserAndEstate.TABLE , MATCHER_CODE_TABLE);
        matcher.addURI(AUTHORITY, Contracts.UserAndEstate.TABLE + "/user/*", MATCHER_CODE_USER);
        matcher.addURI(AUTHORITY, Contracts.UserAndEstate.TABLE +"/estate/*", MATCHER_CODE_ESTATE);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int matcherID = matcher.match(uri);
        String[] tokens = uri.toString().split("/");
        switch (matcherID){
            case MATCHER_CODE_TABLE:
                break;
            case MATCHER_CODE_USER:
                String userToken = tokens[tokens.length - 1];
                selection = Contracts.UserAndEstate.USER_TOKEN + "=?";
                selectionArgs = new String[]{userToken};
                break;
            case MATCHER_CODE_ESTATE:
                String estateToken = tokens[tokens.length - 1];
                selection = Contracts.UserAndEstate.ESTATE_TOKEN + "=?";
                selectionArgs = new String[]{estateToken};
                break;
            default:
                Log.d(LOG_TAG, "invalid uri");
                return null;
        }

        if(projection == null){
            projection = Contracts.UserAndEstate.DEFAULT_PROJECTION;
        }

        Cursor cursor = helper.getReadableDatabase().query(
                Contracts.UserAndEstate.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }



    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if(!ContentProviderUtils.isValidContentValues(values, Contracts.UserAndEstate.DEFAULT_PROJECTION)){
            Log.d(LOG_TAG, "Invalid Cotent values");
            return null;
        }

        int matcherID = matcher.match(uri);
        switch (matcherID){
            case MATCHER_CODE_TABLE:
                String userToken = values.getAsString(Contracts.UserAndEstate.USER_TOKEN);
                String estateToken = values.getAsString(Contracts.UserAndEstate.ESTATE_TOKEN);

                String selection = Contracts.UserAndEstate.USER_TOKEN  + "=? AND " + Contracts.UserAndEstate.ESTATE_TOKEN  + "=?";
                String[] selectors = new String[]{userToken, estateToken};

                Cursor queryCursor = helper.getReadableDatabase().query(
                        Contracts.UserAndEstate.TABLE,
                        Contracts.UserAndEstate.DEFAULT_PROJECTION,
                        selection,
                        selectors,
                        null,
                        null,
                        null
                );

                if(queryCursor.moveToFirst()){
                    Log.d(LOG_TAG, "This user already liked this estate");
                    delete(uri, selection, selectors);
                    return Contracts.UserAndEstate.CONTENT_URI;
                }

                helper.getWritableDatabase().insert(
                        Contracts.UserAndEstate.TABLE,
                        null,
                        values
                );
                Log.d(LOG_TAG,"inserted success");
                getContext().getContentResolver().notifyChange(Contracts.UserAndEstate.CONTENT_URI, null);
                Uri returnedUri = Uri.parse(Contracts.UserAndEstate.CONTENT_URI.toString()
                        + "/"
                        + Contracts.UserAndEstate.USER_TOKEN
                        + "/"
                        + userToken
                        + "/"
                        + Contracts.UserAndEstate.ESTATE_TOKEN
                        + estateToken
                );
                return returnedUri;
            default:
                Log.d(LOG_TAG, "Invalid URI");
                return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (matcher.match(uri)){
            case MATCHER_CODE_TABLE:
                break;
            default:
                Log.d(LOG_TAG, "invalid uri");
                return 0;
        }
        int deletedRows = helper.getWritableDatabase().delete(
                Contracts.UserAndEstate.TABLE,
                selection,
                selectionArgs
        );
        Log.d(LOG_TAG, "Delted rows: " + deletedRows);
        getContext().getContentResolver().notifyChange(Contracts.UserAndEstate.CONTENT_URI, null);
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if(ContentProviderUtils.isValidContentValues(values, Contracts.UserAndEstate.DEFAULT_PROJECTION)){
            Log.d(LOG_TAG, "invalid uri");
            return 0;
        }
        switch (matcher.match(uri)){
            case MATCHER_CODE_TABLE:
                break;
            default:
                Log.d(LOG_TAG, "invalid uri");
                return 0;
        }

        int updatedRows = helper.getWritableDatabase().update(
                Contracts.UserAndEstate.TABLE,
                values,
                selection,
                selectionArgs
        );
        Log.d(LOG_TAG, "updatedRows: " + updatedRows);
        getContext().getContentResolver().notifyChange(Contracts.UserAndEstate.CONTENT_URI, null);
        return updatedRows;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


}
