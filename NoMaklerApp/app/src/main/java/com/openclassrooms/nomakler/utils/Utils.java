package com.openclassrooms.nomakler.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.openclassrooms.nomakler.models.Property;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.support.constraint.Constraints.TAG;



public class Utils {
    /** $/€ conversion rate */
    public static final double dollarEuro = 0.812;
    /** Date parse pattern */
    public static final String dateFormat = "dd/MM/yyyy";

    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars
     * @return
     */
    public static double convertDollarToEuro(double dollars){
        return dollars * dollarEuro;
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return
     */

    public static String getTodayDate(){
        DateFormat df = new SimpleDateFormat(dateFormat, Locale.getDefault());
        return df.format(new Date());
    }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param context
     * @return
     */
    public static Boolean isInternetAvailable(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /** Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /** Returns the real storage path from the uri */
    public static String getRealPathFromUri(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        try (Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null)) {
            assert cursor != null;
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        } catch (Exception e) {
            Log.e(TAG, "getRealPathFromURI Exception : " + e.toString());
            return "";
        }
    }

    /** Converts firestore snapshots list to a properties list */
    public static ArrayList<Property> documentsToPropertyList(List<DocumentSnapshot> res) {
        ArrayList<Property> list = new ArrayList<>();
        for(DocumentSnapshot doc : res){
            Property prop = doc.toObject(Property.class);
            if(prop != null){
                // Add properties to the list
                list.add(prop);
            }
        }
        return list;
    }

    /** Converts GeoPoint object to LatLng object */
    public static LatLng geoPointToLatLng(GeoPoint geoPoint) {
        return new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
    }
}
