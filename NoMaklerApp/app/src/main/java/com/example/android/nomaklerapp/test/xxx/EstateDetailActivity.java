package com.example.android.nomaklerapp.test.xxx;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import com.example.android.nomaklerapp.Modal.Contracts;
import com.example.android.nomaklerapp.Modal.Estate.Estate;
import com.example.android.nomaklerapp.R;
import com.example.android.nomaklerapp.Utils.Constants;
import com.example.android.nomaklerapp.test.SliderPagerAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class EstateDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = EstateDetailActivity.class.getSimpleName();
    private Estate currentEstate;
    private ViewPager viewPager;
    private SliderPagerAdapter sliderPagerAdapter;
    private String currentUserToken;
    private FloatingActionButton fab;
    private int heartIconResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estate_detail);

        Intent intent = getIntent();
        currentEstate = (Estate) intent.getSerializableExtra("viewHolderEstate");
        currentUserToken = PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.CURRENT_USER_TOKEN, null);
        heartIconResource = intent.getIntExtra("heartIconResource", R.drawable.heart_outline);

        if(currentEstate != null){
            Log.d(LOG_TAG, currentEstate.toString());
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        setUPViews();
        setUpViewPager();




    }


    private void setUPViews(){
        TextView textViewAddress = (TextView) findViewById(R.id.text_view_address);
        textViewAddress.setText(currentEstate.getAddress());

        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        TextView textViewPrice = (TextView) findViewById(R.id.text_view_price);
        textViewPrice.setText("$" + decimalFormat.format(currentEstate.getPrice()));

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(heartIconResource);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabAction();
            }
        });

    }
    private void setUpViewPager(){
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        Cursor estateImafeCursor = getContentResolver().query(
                Contracts.EstateAndImage.CONTENT_URI,
                null,
                Contracts.EstateAndImage.ESTATE_PRIMARY + "=?",
                new String[]{currentEstate.getPlaceID()},
                null
        );
        SliderPagerAdapter sliderPagerAdapter = new SliderPagerAdapter(this, R.layout.view_holder_slider_for_cardview, estateImafeCursor);
        viewPager.setAdapter(sliderPagerAdapter);
    }

    private void fabAction(){
        if(currentUserToken == null){
            Toast.makeText(this, "You need to sign in", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(Contracts.UserAndEstate.USER_TOKEN, currentUserToken);
        values.put(Contracts.UserAndEstate.ESTATE_TOKEN, currentEstate.getPlaceID());

        Uri returnedUri = getContentResolver().insert(
                Contracts.UserAndEstate.CONTENT_URI,
                values
        );

        if(returnedUri.toString().equalsIgnoreCase(Contracts.UserAndEstate.CONTENT_URI.toString())){
            heartIconResource = R.drawable.heart_outline;
        }else{
            heartIconResource = R.drawable.heart_ful;
        }

        fab.setImageResource(heartIconResource);
    }


}
