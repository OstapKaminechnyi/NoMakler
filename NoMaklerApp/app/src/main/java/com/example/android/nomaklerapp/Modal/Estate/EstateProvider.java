package com.example.android.nomaklerapp.Modal.Estate;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.android.nomaklerapp.Modal.AppDBHelper;
import com.example.android.nomaklerapp.Modal.Contracts;
import com.example.android.nomaklerapp.Utils.ContentProviderUtils;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


public class EstateProvider extends ContentProvider {
    private static final String LOG_TAG = EstateProvider.class.getSimpleName();

    public static final String AUTHOTITY = "com.example.android.nomaklerapp.Modal.Estate.EstateProvider";
    private static final int URI_MATCHER_CODE_TABLE = 4354;
    private static final int URI_MATCHER_CODE_ROW= 4355;

    private AppDBHelper dbHelper;
    private SQLiteDatabase readableDB, writableDB;
    private UriMatcher uriMatcher;

    @Override
    public boolean onCreate() {

        dbHelper = new AppDBHelper(getContext());
        readableDB = dbHelper.getReadableDatabase();
        writableDB = dbHelper.getWritableDatabase();

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHOTITY, Contracts.Estate.TABLLE, URI_MATCHER_CODE_TABLE);
        uriMatcher.addURI(AUTHOTITY, Contracts.Estate.TABLLE + "/#", URI_MATCHER_CODE_ROW);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        switch (uriMatcher.match(uri)){
            case URI_MATCHER_CODE_TABLE:
                break;
            case URI_MATCHER_CODE_ROW:
                String rowId = ContentUris.parseId(uri) + "";
                selection = Contracts.Estate._ID + "=?";
                selectionArgs = new String[]{rowId};
                break;
            default:
                return null;
        }

        if(projection == null){
            projection = Contracts.Estate.DEFAULT_PROJECTION;
        }
        Cursor cursor = readableDB.query(
                Contracts.Estate.TABLLE,
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
        if(values == null){
            Log.d(LOG_TAG, "content value is null");
            return null;
        }
        if(!ContentProviderUtils.isValidContentValues(values, Contracts.Estate.DEFAULT_PROJECTION)){
            Log.d(LOG_TAG, "Content values has some invalid keys");
            return null;
        }

        if(uriMatcher.match(uri) == UriMatcher.NO_MATCH || uriMatcher.match(uri) == URI_MATCHER_CODE_ROW ){
            Log.d(LOG_TAG, "uri != estateUri");
            return null;
        }

        Estate estate = Estate.newInstance(values);
        Cursor cursor = readableDB.query(Contracts.Estate.TABLLE, null, null, null, null, null, Contracts.Estate.PLACE_ID + " ASC");
        while (cursor.moveToNext()){
            Estate currentEstate = Estate.newInstance(cursor);
            if(estate.equals(currentEstate)){
                Log.d(LOG_TAG, "this estate already exist");
                return null;
            }
        }




        long rowId = writableDB.insert(Contracts.Estate.TABLLE, null,  values);
        Uri returnedUri = Uri.withAppendedPath(Contracts.Estate.CONTENT_URI, rowId+"");
        getContext().getContentResolver().notifyChange(returnedUri, null);
        Log.d(LOG_TAG, returnedUri.toString());
        Log.d(LOG_TAG, "added: " + estate.toString());
        return returnedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)){
            case URI_MATCHER_CODE_TABLE:
                break;
            case URI_MATCHER_CODE_ROW:
                selection = Contracts.Estate._ID +"=?";
                long rowId = ContentUris.parseId(uri);
                selectionArgs = new String[]{rowId + ""};
                break;
            default:
                return 0;
        }

        int deletedRows = writableDB.delete(Contracts.Estate.TABLLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if(!ContentProviderUtils.isValidContentValues(values, Contracts.Estate.DEFAULT_PROJECTION)){
            return 0;
        }
        switch (uriMatcher.match(uri)){
            case URI_MATCHER_CODE_TABLE:
                break;
            case URI_MATCHER_CODE_ROW:
                selection = Contracts.Estate._ID + "=?";
                long rowId = ContentUris.parseId(uri);
                selectionArgs = new String[]{
                        String.valueOf(rowId)
                };
                break;
            default:
                return 0;
        }
        int updatedRows = writableDB.update(Contracts.Estate.TABLLE, values, selection, selectionArgs);
        Log.d(LOG_TAG, "updatedRows: " + updatedRows);
        getContext().getContentResolver().notifyChange(uri, null);
        return updatedRows;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
