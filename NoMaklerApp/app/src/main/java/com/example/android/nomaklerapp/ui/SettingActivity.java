package com.example.android.nomaklerapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toolbar;

import com.example.android.nomaklerapp.R;
import com.example.android.nomaklerapp.Utils.ContextUtils;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
    }
}
