package com.example.android.nomaklerapp.ui;


import android.content.Intent;
import com.example.android.nomaklerapp.GoogleCallBack.GoogleMapKits;
import com.example.android.nomaklerapp.R;
import com.example.android.nomaklerapp.ui.fragment.BaseFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.SupportMapFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends BaseFragment {



    private static final String LOG_TAG = MapFragment.class.getSimpleName();
    public static final int REQUEST_CODE_CURRENT_LAT_LONG = 894;
    private SupportMapFragment supportMapFragment;
    private GoogleMapKits googleMapKits;



    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    protected void initializeFragment() {
        ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        /**
         * @supportMapFragment
         */
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.support_map_fragment);
        googleMapKits = new GoogleMapKits(getContext(), progressBar);
        supportMapFragment.getMapAsync(googleMapKits);



        /**
         * @FloatingActionButton
         */
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE_FOR_SEARCH_ACTIVITY);
            }
        });


    }


    @Override
    protected int setLayoutResource() {
        return R.layout.fragment_map;
    }


}
