package com.example.android.nomaklerapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import com.example.android.nomaklerapp.Modal.Contracts;
import com.example.android.nomaklerapp.ui.SettingActivity;

import com.example.android.nomaklerapp.Modal.Estate.Estate;
import com.example.android.nomaklerapp.Modal.User.User;
import com.example.android.nomaklerapp.ObserverPattern.Network.NetworkStateObserver;
import com.example.android.nomaklerapp.ObserverPattern.Network.NetworkStatePublisher;
import com.example.android.nomaklerapp.ObserverPattern.OnSreenEstateCursor.OnScreenEstateCursorObserver;
import com.example.android.nomaklerapp.ObserverPattern.OnSreenEstateCursor.OnScreenEstateCursorPublisher;
import com.example.android.nomaklerapp.R;
import com.example.android.nomaklerapp.Utils.Constants;
import com.example.android.nomaklerapp.Utils.ContentProviderUtils;
import com.example.android.nomaklerapp.Utils.NetworkHelper;
import com.example.android.nomaklerapp.ui.fragment.PredictionsAdapter;
import com.example.android.nomaklerapp.ui.sign_in_up.SignInActivity;
import com.example.android.nomaklerapp.ui.user_fav.UserFavFragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnScreenEstateCursorObserver, GoogleApiClient.OnConnectionFailedListener, NetworkStateObserver{
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final int ESTATE_LOADER_ID = 531;

    public static final int REQUEST_CODE_FOR_DETAIL_ACTIVITY = 215;
    public static final int REQUEST_CODE_FOR_SEARCH_ACTIVITY = 216;
    public static final int REQUEST_CODE_FOR_GOOGLE_SIGN_IN = 217;
    private double initLat, initLng;
    private GoogleApiClient googleApiClient;
    private PredictionsAdapter predictionsAdapter;

    private MapFragment mapFragment;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String currentUserToken;

    private Cursor onScreenEstateCursor;

    private List<Estate> onScreeenEstates;

    public List<Estate> getOnScreeenEstates() {
        return onScreeenEstates;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkStatePublisher.getInstance().registerObserver(this);
        if(!NetworkHelper.isOnline(this)){
            startActivity(
                    new Intent(this, NetworkInfoActivty.class)
            );
        }
        setContentView(R.layout.activity_main);



        onScreeenEstates = new ArrayList<>();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder()
                .requestIdToken(getString(R.string.goog_e_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .build();


        /**
         * @BottomNavigationView
         */
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bootom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.navigation_home:
                        fragment = new MapFragment();
                        break;
                    case R.id.navigation_heart:
                        if(currentUser == null){
                            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        fragment = new UserFavFragment();
                        break;
                    default:
                        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent);
                        return true;
                                    }

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_holer, fragment);
                fragmentTransaction.commit();

                return true;
            }
        });

        OnScreenEstateCursorPublisher.getInstance().registerObserver(this);


    }


    /**
     * @getUserToken
     */
    private String findUserToken(){
        if(currentUser == null){
            return null;
        }
        Uri returnedUri = getContentResolver().insert(
                Contracts.User.CONTENT_URI,
                ContentProviderUtils.getContentValues(
                        new User(currentUser.getDisplayName(), currentUser.getEmail())
                )
        );

        if(returnedUri == null){
            return null;
        }

        String[] tokens = returnedUri.toString().split("/");
        int count = tokens.length;
        return tokens[count - 1];
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    /**
     *
     * @getCurrentUserToken
     */
    public String getCurrentUserToken() {
        return currentUserToken;
    }

    @Override
    protected void onResume() {
        super.onResume();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserToken = findUserToken();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.CURRENT_USER_TOKEN, currentUserToken);
        editor.commit();

        Log.d(LOG_TAG, currentUser == null? "currentUser == null": currentUserToken);
        predictionsAdapter = new PredictionsAdapter(this);

        /**
         * ******
         */

        Cursor imageCursor = getContentResolver().query(
                Contracts.Image.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        Log.d(LOG_TAG, "imagecursor: " + imageCursor.getCount());


    }

    @Override
    protected void onPause() {
        super.onPause();
        if(onScreenEstateCursor != null){
            Log.d(LOG_TAG, "onScreenEstateCursor != null");
            onScreenEstateCursor.close();
        }else{
            Log.d(LOG_TAG, "onScreenEstateCursor == null");
        }
    }

    @Override
    public void update(Cursor onScreenEstateCursor) {
        this.onScreenEstateCursor = onScreenEstateCursor;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, connectionResult.getErrorMessage());
    }

    @Override
    public void notify(boolean isOnline) {
        if(!isOnline){
            startActivity(
                    new Intent(this, NetworkInfoActivty.class)
            );
        }
    }


}

