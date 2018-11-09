package com.example.android.nomaklerapp.test.xxx;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import com.example.android.nomaklerapp.GoogleCallBack.GoogleMapKits;
import com.example.android.nomaklerapp.Modal.Contracts;
import com.example.android.nomaklerapp.Modal.Estate.Estate;
import com.example.android.nomaklerapp.ObserverPattern.EstatePublisher.GoogleMapPunlisher.GoogleMapKitsObserver;
import com.example.android.nomaklerapp.ObserverPattern.EstatePublisher.GoogleMapPunlisher.GoogleMapKitsPublisher;
import com.example.android.nomaklerapp.ObserverPattern.OnSreenEstateCursor.OnScreenEstateCursorObserver;
import com.example.android.nomaklerapp.ObserverPattern.OnSreenEstateCursor.OnScreenEstateCursorPublisher;
import com.example.android.nomaklerapp.R;
import com.example.android.nomaklerapp.Utils.Constants;
import com.example.android.nomaklerapp.test.SliderPagerAdapter;
import com.example.android.nomaklerapp.ui.MainActivity;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class OnScreenEstateRVAdapter extends RecyclerView.Adapter<OnScreenEstateRVAdapter.OnScreenEstateViewHolder>
        implements OnScreenEstateCursorObserver, GoogleMapKitsObserver {
    private static final String LOG_TAG = OnScreenEstateRVAdapter.class.getSimpleName();
    private Context context;
    private String currentUserToken;
    private Cursor onScreeenEstateCursor;
    private List<Estate> rvEstates;





    /**
     * @Consntructor
     * @param context
     */
    public OnScreenEstateRVAdapter(Context context, Cursor onScreeenEstateCursor) {
        this.context = context;
        this.onScreeenEstateCursor = onScreeenEstateCursor;

        if(context instanceof MainActivity){
            MainActivity mainActivity = (MainActivity) context;
            currentUserToken = mainActivity.getCurrentUserToken();
        }

        OnScreenEstateCursorPublisher.getInstance().registerObserver(this);
        GoogleMapKitsPublisher.getInstance().registerObserver(this);

        rvEstates = new ArrayList<>();


    }


    @Override
    public OnScreenEstateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OnScreenEstateViewHolder(
                LayoutInflater.from(context).inflate(R.layout.view_holder, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(OnScreenEstateViewHolder holder, int position) {
        if(onScreeenEstateCursor == null){
            return;
        }

        if(!rvEstates.isEmpty()){
            rvEstates.clear();
        }
        if(onScreeenEstateCursor.moveToPosition(position)) {
            Estate estate = Estate.newInstance(onScreeenEstateCursor);
            rvEstates.add(estate);
            holder.bindData(estate);

        }
    }

    @Override
    public int getItemCount() {
        return onScreeenEstateCursor == null? 0 : onScreeenEstateCursor.getCount();
    }

    public void setOnScreeenEstateCursor(Cursor onScreeenEstateCursor) {
        this.onScreeenEstateCursor = onScreeenEstateCursor;
        notifyDataSetChanged();
    }

    @Override
    public void update(Cursor onScreeenEstateCursor) {
        setOnScreeenEstateCursor(onScreeenEstateCursor);
    }

    private GoogleMapKits googleMapKits;

    @Override
    public void onViewDetachedFromWindow(OnScreenEstateViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.rvMarker.remove();
        googleMapKits.getRvMarkers().remove(holder.rvMarker);

    }


    @Override
    public void onViewAttachedToWindow(OnScreenEstateViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.rvMarker = googleMapKits.getmMap().addMarker(
                new MarkerOptions().position(new LatLng(holder.viewHolderEstate.getLat(), holder.viewHolderEstate.getLng()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))

        );
        googleMapKits.getRvMarkers().add(holder.rvMarker);

    }

    @Override
    public void update(GoogleMapKits googleMapKits) {
        this.googleMapKits = googleMapKits;
    }


    /**
     * @OnScreenEstateViewHolder
     */
    class OnScreenEstateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textViewAddress;
        TextView textViewPrice;
        ViewPager viewPager;
        SliderPagerAdapter sliderPagerAdapter;
        ImageView heartIcon;
        Estate viewHolderEstate;
        ViewGroup wholeEstateLayout;
        int heartIconResource;
        Marker rvMarker;

        /**
         * @COntrcuctor
         * @param itemView
         */
        public OnScreenEstateViewHolder(View itemView) {
            super(itemView);
            if(!googleMapKits.getRvMarkers().isEmpty()){
            for(Marker rvMarker: googleMapKits.getRvMarkers()){
                rvMarker.remove();
            }
            googleMapKits.getRvMarkers().clear();
            }


        }

        /**
         * @bindData
         * @param estate
         */
        public void bindData(Estate estate){

            if(estate == null){
                return;
            }

            //--marker
            viewHolderEstate = estate;



            //--------------
            textViewAddress = (TextView) itemView.findViewById(R.id.text_view_address);
            textViewPrice = (TextView) itemView.findViewById(R.id.text_view_price);

            heartIcon = (ImageView) itemView.findViewById(R.id.heart_icon);

            viewPager = (ViewPager) itemView.findViewById(R.id.view_pager);
            sliderPagerAdapter = new SliderPagerAdapter(context,R.layout.view_holder_slider_for_cardview, null);
            viewPager.setAdapter(sliderPagerAdapter);


            wholeEstateLayout = (ViewGroup) itemView.findViewById(R.id.whole_estate_layout);
            wholeEstateLayout.setOnClickListener(this);
            heartIconResource = R.drawable.heart_outline;
            //--textViewAddress
            textViewAddress.setText(estate.getAddress());
            textViewPrice.setText(
                    new DecimalFormat("$#,###").format(estate.getPrice())
            );

            //--SliderAdapter
            String placeID = estate.getPlaceID();
            Cursor estateImageCursor = context.getContentResolver().query(
                    Uri.parse(
                            Contracts.EstateAndImage.CONTENT_URI.toString()
                                    + "/"
                                    + Contracts.EstateAndImage.ESTATE_PRIMARY
                                    + "/"
                                    + placeID
                    ),
                    null,
                    null,
                    null,
                    null
            );

            sliderPagerAdapter.setEstateAndImageCursor(estateImageCursor);

            //--Set resource for heartIcon
            int src = R.drawable.heart_outline;
            if(currentUserToken != null){
                String selection = Contracts.UserAndEstate.USER_TOKEN + "=? AND "
                        + Contracts.UserAndEstate.ESTATE_TOKEN + "=?";
                String[] selectors = new String[]{currentUserToken, placeID};
                Cursor userEstaeCursor = context.getContentResolver().query(
                        Contracts.UserAndEstate.CONTENT_URI,
                        null,
                        selection,
                        selectors,
                        null
                );

                if(userEstaeCursor.moveToFirst()){
                    src = R.drawable.heart_ful;
                }
            }
            heartIconResource = src;
            heartIcon.setImageResource(heartIconResource);

            heartIcon.setOnClickListener(this);


        }

        /**
         * @onClick
         * @param v
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.heart_icon:
                    heartIconAction();
                    break;

                case R.id.whole_estate_layout:
                    wholeEstateLayoutAction();
                    break;

                default:
                    break;
            }
        }

        private void wholeEstateLayoutAction() {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.INIT_LAT_KEY, viewHolderEstate.getLat() + "");
            editor.putString(Constants.INIT_LNG_KEY, viewHolderEstate.getLng() + "");
            editor.commit();

            Intent intent = new Intent(context, EstateDetailActivity.class);
            intent.putExtra("viewHolderEstate", viewHolderEstate);
            intent.putExtra("currentUserToken", currentUserToken);
            intent.putExtra("heartIconResource",heartIconResource);
            context.startActivity(intent);
        }

        /**
         * @heartIconAction
         */

        private void heartIconAction(){
            if(currentUserToken == null){
                Toast.makeText(context, "You need to sign in", Toast.LENGTH_SHORT).show();
                return;
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contracts.UserAndEstate.USER_TOKEN, currentUserToken);
            contentValues.put(Contracts.UserAndEstate.ESTATE_TOKEN, viewHolderEstate.getPlaceID());
            Uri uri = context.getContentResolver().insert(
                    Contracts.UserAndEstate.CONTENT_URI, contentValues
            );
            int src = R.drawable.heart_outline;
            if(!uri.toString().equalsIgnoreCase(Contracts.UserAndEstate.CONTENT_URI.toString())){
                src = R.drawable.heart_ful;
            }
            heartIconResource = src;
            heartIcon.setImageResource(heartIconResource);

        }
    }
}
