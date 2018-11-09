package com.example.android.nomaklerapp.Modal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import java.util.List;


public class AppDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "estates.db";
    private static int DB_CURRENT_VERSION = 8;


    private static final double MIN_PRICE = 100000;
    private static final double MAX_PRICE = 5000000;
    private static final String LOG_TAG = AppDBHelper.class.getSimpleName();


    public static final String TEMP_TABLE = "tempTable";

    private static final String CREATE_STATEMENT_ESTATE = "CREATE TABLE " + Contracts.Estate.TABLLE + "("
            + Contracts.Estate._ID + " INT AUTO INCREMENT,"
            + Contracts.Estate.ADDRESS + " TEXT NOT NULL,"
            + Contracts.Estate.PRICE + " REAL NULL, "
            + Contracts.Estate.POSTAL_CODE + " REAL NULL, "
            + Contracts.Estate.PLACE_ID + " TEXT PRIMARY_KEY NOT NULL , "
            + Contracts.Estate.LATITUDE + " REAL NOT NULL,"
            + Contracts.Estate.LONGTITUDE + " REAL NOT NULL"
            + ");";
    private static final String CREATE_STAEMENT_USER = "CREATE TABLE " + Contracts.User.TABLE
            + "( "
            + Contracts.User._ID + " INT AUTO INCREMENT, "
            + Contracts.User.TOKEN + " TEXT PRIMARY_KEY NOT NULL, "
            + Contracts.User.NAME + " TEXT , "
            + Contracts.User.EMAIL + " TEXT NOT NULL, "
            + Contracts.User.JOINED_DATE_MILLIS + " REAL NOT NULL "
            + ");" ;
    private static final String CREATE_STATEMENT_USER_ESTATE = "CREATE TABLE " + Contracts.UserAndEstate.TABLE
            + "("
            + Contracts.UserAndEstate._ID + " INT AUTO INCREMENT, "
            + Contracts.UserAndEstate.USER_TOKEN + " TEXT NOT NULL, "
            + Contracts.UserAndEstate.ESTATE_TOKEN + " TEXT NOT NULL"
            + ");";
    private static final String CREATE_STATEMENT_IMAGE = "CREATE TABLE " + Contracts.Image.TABLE
            + "("
            + Contracts.Image._ID + " INT AUTO INCREMENT, "
            + Contracts.Image.LINK + " TEXT PRIMARY_KEY "
            + ");";
    private static final String CREATE_STATEMENT_ESTATEandIMAGE = "CREATE TABLE " + Contracts.EstateAndImage.TABLE
            + "("
            + Contracts.EstateAndImage._ID + " INT AUTO INCREMENT, "
            + Contracts.EstateAndImage.ESTATE_PRIMARY + " TEXT ,"
            + Contracts.EstateAndImage.IMAGE_PRIMARY + " TEXT "
            + ");";

    private static final String DROP_TABLE_STATEMENT = "DROP TABLE " + Contracts.Estate.TABLLE;
    private Context context;
    public AppDBHelper(Context context) {
        super(context, DB_NAME, null, DB_CURRENT_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STATEMENT_ESTATE);
        db.execSQL(CREATE_STAEMENT_USER);
        db.execSQL(CREATE_STATEMENT_USER_ESTATE);
        db.execSQL(CREATE_STATEMENT_IMAGE);
        db.execSQL(CREATE_STATEMENT_ESTATEandIMAGE);


    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CREATE_STATEMENT_ESTATEandIMAGE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        List<Pair<String, String>> list = db.getAttachedDbs();

        for(Pair<String, String> pair: list){
            Log.d(LOG_TAG, pair.toString());
        }
    }





}
