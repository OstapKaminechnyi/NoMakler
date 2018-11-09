package com.example.android.nomaklerapp.Modal;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.nomaklerapp.Utils.ContentProviderUtils;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


public abstract class ThirdTableProvider extends ContentProvider {

    protected SQLiteOpenHelper dbHelper;
    protected UriMatcher uriMatcher;

    protected String LOG_TAG;
    protected String table;
    protected Uri tableUri;
    protected String authority;

    protected String[] defaultProjection, selectors;
    protected String selection, sortOrder;

    protected String firstPrimaryKey;
    protected String secondPrimaryKey;

    protected static final int MATCHER_CODE_TABLE = 101;
    protected static  final int MATCHER_CODE_PRIMARY = 102;
    protected static final int MATCHER_CODE_BOTH_PRIMARY = 104;


    protected  void initialize(String authority, SQLiteOpenHelper dbHelper, Uri tableUri, String[] defaultProjection, String firstPrimaryKey, String secondPrimaryKey){
        LOG_TAG = this.getClass().getSimpleName();

        this.authority = authority;
        this.dbHelper = dbHelper;
        this.tableUri = tableUri;

        table = ContentProviderUtils.getLastString(tableUri);

        this.defaultProjection = defaultProjection;

        this.firstPrimaryKey = firstPrimaryKey;
        this.secondPrimaryKey = secondPrimaryKey;

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(authority, table , MATCHER_CODE_TABLE);
        uriMatcher.addURI(authority, table + "/" + firstPrimaryKey + "/*", MATCHER_CODE_PRIMARY);
        uriMatcher.addURI(authority, table + "/" + secondPrimaryKey + "/*", MATCHER_CODE_PRIMARY);
        uriMatcher.addURI(authority, table + "/" + firstPrimaryKey + "/*/" + secondPrimaryKey + "/*"  , MATCHER_CODE_BOTH_PRIMARY);
        uriMatcher.addURI(authority, table + "/" + secondPrimaryKey + "/*/" + firstPrimaryKey + "/*"  , MATCHER_CODE_BOTH_PRIMARY);

    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        this.sortOrder = sortOrder;
        if(!isValidUri(uri)){
            return null;
        }

        setSQLCondition(
                uri,
                selection,
                selectionArgs,
                sortOrder
        );

        Cursor cursor = dbHelper.getReadableDatabase().query(
                table,
                this.defaultProjection,
                this.selection,
                this.selectors,
                null,
                null,
                this.sortOrder
        );

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if(!isValidUri(uri)){
            return null;
        }

        if(!ContentProviderUtils.isValidContentValues(values, defaultProjection)){
            Log.d(LOG_TAG, "Invalid ContentValues");
            return null;
        }
        String firstPrimaryValue = values.getAsString(firstPrimaryKey);
        String secondPrimaryValue = values.getAsString(secondPrimaryKey);
        Cursor cursor = query(
                ContentProviderUtils.getBothPrimaryUri(tableUri, firstPrimaryKey, firstPrimaryValue, secondPrimaryKey, secondPrimaryValue),
                this.defaultProjection,
                this.selection,
                this.selectors,
                this.sortOrder
        );

        if(cursor.moveToFirst()){
            Log.d(LOG_TAG, "this data already exist!");
            return ContentProviderUtils.getBothPrimaryUri(tableUri, firstPrimaryKey, firstPrimaryValue, secondPrimaryKey, secondPrimaryValue);
        }

        dbHelper.getWritableDatabase().insert(table, null, values);
        Uri returnedURi = ContentProviderUtils.getBothPrimaryUri(
                tableUri,
                firstPrimaryKey,
                firstPrimaryValue,
                secondPrimaryKey,
                secondPrimaryValue
        );

        Log.d(LOG_TAG, returnedURi.toString());
        return  returnedURi;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if(!isValidUri(uri)){
            return 0;
        }

        setSQLCondition(
                uri,
                selection,
                selectionArgs,
                null
        );

        int deletedRows = dbHelper.getWritableDatabase().delete(
                table,
                this.selection,
                this.selectors
        );

        Log.d(LOG_TAG, "deletedRows: " + deletedRows);
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if(!isValidUri(uri)){
            return 0;
        }

        if(!ContentProviderUtils.isValidContentValues(values, defaultProjection)){
            Log.d(LOG_TAG, "invalid ContentValue");
            return 0;
        }

        setSQLCondition(
                uri,
                selection,
                selectionArgs,
                null
        );

        int updatedRows = dbHelper.getWritableDatabase().update(
                table,
                values,
                this.selection,
                this.selectors
        );

        Log.d(LOG_TAG, "updatedRows: " + updatedRows);
        return updatedRows;
    }

    protected boolean isValidUri(Uri uri){
        if(uriMatcher.match(uri) == UriMatcher.NO_MATCH){
            Log.d(LOG_TAG, "invalid uri");
            return false;
        }
        return true;
    }

    protected void setSQLCondition(Uri uri, String selection, String[] selectionArgs, String sortOrder){
        switch (uriMatcher.match(uri)){
            case MATCHER_CODE_TABLE:
                this.selection = selection;
                this.selectors = selectionArgs;
                break;
            case MATCHER_CODE_PRIMARY:
                this.selection = (uri.toString().contains(firstPrimaryKey)? firstPrimaryKey : secondPrimaryKey)  + "=?";
                this.selectors = new String[]{ContentProviderUtils.getLastString(uri)};
                break;
            default:
                this.selection = firstPrimaryKey + " =? AND " + secondPrimaryKey + " =?";
                String[] tokens = uri.toString().split("/");
                String firstPrimaryValue  = "";
                String secondPrimaryValue  = "";
                for(int i = 0; i < tokens.length; i++){
                    String text= tokens[i];
                    if(text.equalsIgnoreCase(firstPrimaryKey)){
                        firstPrimaryValue = tokens[i+1];
                    }

                    if(text.equalsIgnoreCase(secondPrimaryKey)){
                        secondPrimaryValue = tokens[i+1];
                    }
                }
                this.selectors = new String[]{firstPrimaryValue, secondPrimaryValue};
                break;
        }
    }
}
