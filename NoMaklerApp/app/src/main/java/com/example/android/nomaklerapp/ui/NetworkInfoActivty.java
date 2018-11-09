package com.example.android.nomaklerapp.ui;

import android.content.Intent;
import com.example.android.nomaklerapp.ObserverPattern.Network.NetworkStateObserver;
import com.example.android.nomaklerapp.ObserverPattern.Network.NetworkStatePublisher;
import com.example.android.nomaklerapp.R;
import com.example.android.nomaklerapp.Utils.ContextUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class NetworkInfoActivty extends AppCompatActivity implements NetworkStateObserver{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_info_activty);
        NetworkStatePublisher.getInstance().registerObserver(this);
        ContextUtils.setupToolbar(this, (Toolbar) findViewById(R.id.tool_bar));

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    public void notify(boolean isOnline) {
        if(isOnline){
            startActivity(
                    new Intent(this, MainActivity.class)
            );
        }
    }
}
