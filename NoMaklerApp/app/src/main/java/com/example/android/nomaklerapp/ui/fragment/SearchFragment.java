package com.example.android.nomaklerapp.ui.fragment;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.SharedPreferences;
import com.example.android.nomaklerapp.ObserverPattern.PredicitonPublisher;
import com.example.android.nomaklerapp.ObserverPattern.PredictionObserver;
import com.example.android.nomaklerapp.R;
import com.example.android.nomaklerapp.Utils.Constants;
import com.example.android.nomaklerapp.Utils.LocationUtils;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

public class SearchFragment extends BaseFragment implements PredictionObserver, GoogleApiClient.OnConnectionFailedListener {
    private static final String LOG_TAG = SearchFragment.class.getSimpleName();

    private PredictionsAdapter predictionsAdapter;
    private RecyclerView recyclerView;
    private GoogleApiClient googleApiClient;
    private SearchView searchView;
    private AutocompletePredictionBuffer autocompletePredictionBuffer;


    @Override
    protected void initializeFragment() {

        PredicitonPublisher.getInstance().registerObserver(this);

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .build();


        predictionsAdapter = new PredictionsAdapter(getContext());

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.tool_bar);
        toolbar.setTitle("Search");
        if(getContext() instanceof AppCompatActivity){
            AppCompatActivity appCompatActivity = (AppCompatActivity) getContext();
            appCompatActivity.setSupportActionBar(toolbar);
            if(appCompatActivity.getSupportActionBar() != null){
                appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        setHasOptionsMenu(true);


        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_places);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        if(predictionsAdapter != null){
            recyclerView.setAdapter(predictionsAdapter);
        }

        showInput();

    }

    @Override
    protected int setLayoutResource() {
        return R.layout.fragment_search;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void update(AutocompletePrediction autocompletePrediction) {
        searchView.setQuery(autocompletePrediction.getFullText(null), true);
        LocationUtils.getPlaceBufferPendingResult(googleApiClient, autocompletePrediction).setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(@NonNull PlaceBuffer places) {
                if(places == null || places.getCount() <= 0){
                    Log.d(LOG_TAG, "places == null ");
                    getActivity().finish();
                }

                Place place = places.get(0);
                LatLng latLng = place.getLatLng();

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.INIT_LAT_KEY, latLng.latitude + "");
                editor.putString(Constants.INIT_LNG_KEY, latLng.longitude + "");
                editor.commit();

                //--autocompletePredictionBuffer.release()
                if(autocompletePredictionBuffer != null){
                    autocompletePredictionBuffer.release();
                }
                getActivity().finish();


            }
        });
    }

    private void showInput() {
        getInputMethodManager().toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
        );
    }

    private void hideInput() {
    }

    private InputMethodManager getInputMethodManager() {
        return (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search_activity, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getActivity().getComponentName());
        searchView.setSearchableInfo(searchableInfo);
        searchView.setIconified(false);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                predictionsAdapter.setPredictions(null);
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                PendingResult<AutocompletePredictionBuffer> result =
                        Places.GeoDataApi.getAutocompletePredictions(googleApiClient, newText, null, null);
                result.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
                    @Override
                    public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
                        if(autocompletePredictions == null || autocompletePredictions.getCount() <= 0){
                            return;
                        }

                        autocompletePredictionBuffer = autocompletePredictions;
                        predictionsAdapter.setPredictions(autocompletePredictions);
                    }
                });
                return true;
            }
        });
    }
}
