package com.example.android.nomaklerapp.Utils;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;


public final class JsonUtil {
    private static final String LOG_TAG = JsonUtil.class.getSimpleName();

    private JsonUtil(){}

    private static URL createUrl(String urlString){
        URL url = null;
        try {
            url = new URL(urlString);
            Log.d(LOG_TAG, url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static String getJsonResponse(URL url){
        HttpsURLConnection httpsURLConnection = null;
        InputStream inputStream = null;

        try{
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.connect();
            inputStream = httpsURLConnection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\a");
            String jsonResponse = scanner.next();
            Log.d(LOG_TAG, jsonResponse);
            return  jsonResponse;
        }catch (IOException ex){

        }finally {
            if(httpsURLConnection != null){
                httpsURLConnection.disconnect();
            }
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static String getJasonResponse(String urlString){
        return getJsonResponse(
                createUrl(urlString)
        );
    }

}
