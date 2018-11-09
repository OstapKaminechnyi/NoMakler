package com.example.android.nomaklerapp.Utils;

import android.content.Context;
import com.example.android.nomaklerapp.ui.MainActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


public  final class ContextUtils {
    private ContextUtils(){}

    /**
     * @getMainActivity
     */
    public static MainActivity getMainActivity(Context context){
        if(context instanceof MainActivity){
            MainActivity mainActivity = (MainActivity) context;
            return mainActivity;
        }
        return null;
    }

    /**
     * @getAppCompatActivity
     */
    public static AppCompatActivity getAppCompatActivity(Context context){
        if(context instanceof AppCompatActivity){
            AppCompatActivity appCompatActivity = (AppCompatActivity) context;
            return  appCompatActivity;
        }
        return null;
    }

    /**
     * @setupToolbarForChildActivity
     */
    public static void setupToolbar(Context context, Toolbar toolbar){
        if(toolbar == null || context == null){
            return;
        }


        AppCompatActivity appCompatActivity = getAppCompatActivity(context);
        if(appCompatActivity == null){
            return;
        }
        appCompatActivity.setSupportActionBar(toolbar);
        if(appCompatActivity.getSupportActionBar() != null){
            if(appCompatActivity.getSupportParentActivityIntent() != null) {
                appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    /**
     * @getStatusBarHeight
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
