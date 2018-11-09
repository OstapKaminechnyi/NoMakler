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


public abstract class BaseProvider extends ContentProvider {
    protected  static final int MATCHER_ID_WHOLE_TABLE = 101;
    protected  static final int MATCHER_ID_ROW_PRIMARY_KEY = 102;

    protected String LOG_TAG;

    protected String authority;
    protected String table;
    protected Uri tableUri;
    protected UriMatcher uriMatcher;
    protected SQLiteOpenHelper dbHelper;
    protected String selection, orderBy;
    protected String[] defaultProjection, selectors;
    protected String primaryKey;




    protected void initialize(String authority, String table, Uri tableUri, SQLiteOpenHelper dbHelper, String primaryKey, String[] defaultProjection){
        this.authority = authority;
        this.tableUri = tableUri;
        this.dbHelper = dbHelper;
        this.primaryKey = primaryKey;
        this.table = table;
        this.defaultProjection = defaultProjection;

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(authority, table, MATCHER_ID_WHOLE_TABLE);
        uriMatcher.addURI(authority, table + "/*", MATCHER_ID_ROW_PRIMARY_KEY);



    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        this.orderBy = sortOrder;
        if(!isValidUri(uri)){
            return null;
        }

        setSQLCondition(
                uriMatcher.match(uri),
                uri,
                selection,
                selectionArgs
        );

        Cursor cursor = dbHelper.getReadableDatabase().query(
                table,
                defaultProjection,
                this.selection,
                this.selectors,
                null,
                null,
                this.orderBy
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
            Log.d(LOG_TAG, "invalid ContentValues");
            return null;
        }
        String primaryKeyValue = values.getAsString(primaryKey);
        Cursor cursor = query(
                Uri.withAppendedPath(tableUri, primaryKeyValue),
                null,
                null,
                null,
                null
        );

        if(cursor != null && cursor.moveToFirst()){
            Log.d(LOG_TAG, "This data already exist");
            return Uri.withAppendedPath(tableUri, primaryKeyValue);
        }

        dbHelper.getWritableDatabase().insert(table, null, values);
        return Uri.withAppendedPath(tableUri, primaryKeyValue);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if(!isValidUri(uri)){
            return 0;
        }
        setSQLCondition(
                uriMatcher.match(uri),
                uri,
                selection,
                selectionArgs
        );

        int deletedRow = dbHelper.getWritableDatabase().delete(table, this.selection, this.selectors);
        Log.d(LOG_TAG, "deletedRow: " + deletedRow);
        return deletedRow;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if(!isValidUri(uri)){
            return 0;
        }

        if(!ContentProviderUtils.isValidContentValues(values, defaultProjection)){
            Log.d(LOG_TAG, "invalid ContentValues");
            return 0;
        }

        setSQLCondition(
                uriMatcher.match(uri),
                uri,
                selection,
                selectionArgs
        );

        int updatedRows = dbHelper.getWritableDatabase().update(table, values, this.selection, this.selectors);
        Log.d(LOG_TAG, "updatedRows: " + updatedRows);
        return updatedRows;
    }

    protected void setSQLCondition(int matcherID, Uri uri, String selection, String[] selectors){

        switch (matcherID){
            case MATCHER_ID_WHOLE_TABLE:
                this.selection = selection;
                this.selectors = selectors;
                break;
            default: MATCHER_ID_ROW_PRIMARY_KEY:
                this.selection = primaryKey + "=?";
                String[] tokens = uri.toString().split("/");
                int tokenLength = tokens.length;
                String primaryKeyValue = tokens[tokenLength - 1];
                this.selectors = new String[]{primaryKeyValue};
                break;


        }

    }

    protected boolean isValidUri(Uri uri){
        int matcherId = uriMatcher.match(uri);
        if(matcherId == UriMatcher.NO_MATCH){
            Log.d(LOG_TAG, "invalid Uri");
            return false;
        }
        return true;
    }
}
