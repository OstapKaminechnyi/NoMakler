package com.example.android.nomaklerapp.Modal.User;

import android.content.ContentProvider;
import android.content.ContentUris;
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


public class UserProvider extends ContentProvider {
    public static final String AUTHORITY = UserProvider.class.getName();
    private static final String LOG_TAG = UserProvider.class.getSimpleName();

    private static final int URI_MATCHER_CODE_TABLE = 31;
    private static final int URI_MATCHER_CODE_ROW_WITH_ID = 32;
    private static final int URI_MATCHER_CODE_ROW_WITH_EMAIL = 33;


    private AppDBHelper userDBHelper;
    private UriMatcher uriMatcher;

    @Override
    public boolean onCreate() {
        userDBHelper = new AppDBHelper(getContext());

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, Contracts.User.TABLE, URI_MATCHER_CODE_TABLE);
        uriMatcher.addURI(AUTHORITY, Contracts.User.TABLE + "/#", URI_MATCHER_CODE_ROW_WITH_ID);
        uriMatcher.addURI(AUTHORITY, Contracts.User.TABLE + "/*", URI_MATCHER_CODE_ROW_WITH_EMAIL);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int matcherID = uriMatcher.match(uri);

        switch (matcherID) {
            case URI_MATCHER_CODE_TABLE:
                break;
            case URI_MATCHER_CODE_ROW_WITH_ID:
                selection = Contracts.User._ID + "=?";
                long rowID = ContentUris.parseId(uri);
                selectionArgs = new String[]{String.valueOf(rowID)};
                break;
            case URI_MATCHER_CODE_ROW_WITH_EMAIL:
                String queryEmail = uri.getQuery();
                Log.d(LOG_TAG, "queryEmail: " + queryEmail);
                selection = Contracts.User.EMAIL + "=?";
                selectionArgs = new String[]{queryEmail};
                break;
            default:
                Log.d(LOG_TAG, "Invalid uri");
                return null;
        }

        if(projection == null){
            projection = Contracts.User.DEFAUKT_PROJECTION;
        }

        Cursor returnedCursor = userDBHelper.getReadableDatabase().query(
                Contracts.User.TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        returnedCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnedCursor;

    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if(!ContentProviderUtils.isValidContentValues(values, Contracts.User.DEFAUKT_PROJECTION)){
            Log.d(LOG_TAG, "invalid ContentValues");
            return null;
        }

        int matcherID = uriMatcher.match(uri);
        if(matcherID != URI_MATCHER_CODE_TABLE){
            Log.d(LOG_TAG, "matcherID != URI_MATCHER_CODE_TABLE");
            return null;
        }

        String insertEmail = values.getAsString(Contracts.User.EMAIL);

        Cursor cursor = userDBHelper.getReadableDatabase().query(
               Contracts.User.TABLE,
               Contracts.User.DEFAUKT_PROJECTION,
               Contracts.User.EMAIL + "=?",
                new String[]{insertEmail},
                null,
                null,
                null
        );


        if(cursor.moveToFirst()){
            String userToken =cursor.getString(Contracts.User.TOKEN_INDEX);
            Uri returnedUri = Uri.withAppendedPath(Contracts.User.CONTENT_URI, userToken);
            Log.d(LOG_TAG, "this email is alrealdy used!");
            return returnedUri;
        }

        userDBHelper.getWritableDatabase().insert(Contracts.User.TABLE, null,  values);
        getContext().getContentResolver().notifyChange(uri, null);

        String userToken = values.getAsString(Contracts.User.TOKEN);
        Uri returnedUri = Uri.withAppendedPath(Contracts.User.CONTENT_URI, userToken);
        Log.d(LOG_TAG, uri.toString());
        return returnedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)){
            case URI_MATCHER_CODE_TABLE:
                break;
            default:
                Log.d(LOG_TAG, "invalid uri");
                return 0;
        }

        int deletedRows = userDBHelper.getWritableDatabase().delete(
                Contracts.User.TABLE,
                selection,
                selectionArgs
        );
        getContext().getContentResolver().notifyChange(Contracts.User.CONTENT_URI, null);
        Log.d(LOG_TAG, "deletedRows: " + deletedRows);
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if(!ContentProviderUtils.isValidContentValues(values, Contracts.User.DEFAUKT_PROJECTION)){
            Log.d(LOG_TAG, "invalid uri");
            return 0;
        }
        switch (uriMatcher.match(uri)){
            case URI_MATCHER_CODE_TABLE:
                break;
            default:
                Log.d(LOG_TAG, "invalid uri");
                return 0;
        }

        int updatedRows = userDBHelper.getWritableDatabase().update(
                Contracts.User.TABLE,
                values,
                selection,
                selectionArgs
        );
        getContext().getContentResolver().notifyChange(Contracts.User.CONTENT_URI, null);
        Log.d(LOG_TAG, "updatedRows: " + updatedRows);
        return updatedRows;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    private Uri getUriWithEmail(String email){
        return Uri.withAppendedPath(Contracts.User.CONTENT_URI, email);
    }
}
