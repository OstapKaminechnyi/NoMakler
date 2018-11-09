package com.example.android.nomaklerapp.ui.user_fav;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import com.example.android.nomaklerapp.Modal.Contracts;
import com.example.android.nomaklerapp.Modal.Estate.Estate;
import com.example.android.nomaklerapp.ObserverPattern.UserAndEstateTokensPublisher;
import com.example.android.nomaklerapp.R;
import com.example.android.nomaklerapp.Utils.Constants;
import com.example.android.nomaklerapp.Utils.ContextUtils;
import com.example.android.nomaklerapp.test.SliderPagerAdapter;
import com.example.android.nomaklerapp.test.xxx.EstateDetailActivity;
import com.example.android.nomaklerapp.ui.MainActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;


public class UserEstatesAdapter extends RecyclerView.Adapter<UserEstatesAdapter.UserEstateViewHolder> {
    private static final String LOG_TAG = Contracts.UserAndEstate.class.getSimpleName();
    private Context context;
    private Cursor userEstateCursor;
    private String currentUserToken;
    private UserAndEstateTokensPublisher tokensPublisher;

    public UserEstatesAdapter(Context context, Cursor userEstateCursor) {
        this.context = context;
        this.userEstateCursor = userEstateCursor;

        MainActivity mainActivity = ContextUtils.getMainActivity(context);
        if(mainActivity != null){
            currentUserToken = mainActivity.getCurrentUserToken();
        }


        tokensPublisher = new UserAndEstateTokensPublisher();
    }

    @Override
    public UserEstateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserEstateViewHolder(context, parent, viewType);
    }

    @Override
    public void onBindViewHolder(UserEstateViewHolder holder, int position) {
        if(userEstateCursor == null || !userEstateCursor.moveToPosition(position) ){
            return;
        }



        final String placeID = userEstateCursor.getString(Contracts.UserAndEstate.INDEX_ESTATE_TOKEN);
        Cursor cursor = context.getContentResolver().query(
                Contracts.Estate.CONTENT_URI,
                null,
                Contracts.Estate.PLACE_ID + "=?",
                new String[]{placeID},
                null

        );
        if(cursor.moveToFirst()){
            holder.bindData(Estate.newInstance(cursor));
        }
        cursor.close();

    }

    @Override
    public int getItemCount() {
        return userEstateCursor == null? 0 : userEstateCursor.getCount();
    }

    /**
     * @setUserEstateCursor
     */
    public void setUserEstateCursor(Cursor userEstateCursor) {
        this.userEstateCursor = userEstateCursor;
        notifyDataSetChanged();

    }




    /**
     * @getTokensPublisher
     */
    public UserAndEstateTokensPublisher getTokensPublisher() {
        return tokensPublisher;
    }



    /**
     * @Class
     * @UserEstateViewHolder
     */
    class UserEstateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ViewGroup rootView;
        TextView textViewAddress;
        TextView textViewPrice;
        View wholeEstateLayout;
        View heartIcon;
        ViewPager viewPager;
        Estate viewHodlerEstate;
        SliderPagerAdapter sliderPagerAdapter;
        public UserEstateViewHolder(Context context, ViewGroup container, int viewType) {
            super(LayoutInflater.from(context).inflate(R.layout.view_holder_user_fav, container, false));
            rootView = (ViewGroup) itemView.findViewById(R.id.root_view);
            textViewAddress = (TextView) itemView.findViewById(R.id.text_view_address);
            textViewPrice = (TextView) itemView.findViewById(R.id.text_view_price);
            wholeEstateLayout = itemView.findViewById(R.id.whole_estate_layout);
            heartIcon = itemView.findViewById(R.id.heart_icon);

            viewPager = (ViewPager) itemView.findViewById(R.id.view_pager);
            sliderPagerAdapter = new SliderPagerAdapter(context, R.layout.view_holder_slider_for_cardview, null);
            viewPager.setAdapter(sliderPagerAdapter);

        }

        public void bindData(Estate estate){
            viewHodlerEstate = estate;

            textViewAddress.setText(estate.getAddress());
            textViewPrice.setText(
                    new DecimalFormat("$#,###").format(estate.getPrice())
            );

            Cursor estateAndImageCursor = context.getContentResolver().query(
                    Contracts.EstateAndImage.CONTENT_URI,
                    null,
                    Contracts.EstateAndImage.ESTATE_PRIMARY + "=?",
                    new String[]{estate.getPlaceID()},
                    null,
                    null
            );
           sliderPagerAdapter.setEstateAndImageCursor(estateAndImageCursor);

            heartIcon.setOnClickListener(this);
            wholeEstateLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.whole_estate_layout:
                    Log.d(LOG_TAG, "clicked");
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.INIT_LAT_KEY, viewHodlerEstate.getLat() + "");
                    editor.putString(Constants.INIT_LNG_KEY, viewHodlerEstate.getLng() + "");
                    editor.commit();

                    Intent intent = new Intent(context, EstateDetailActivity.class);
                    intent.putExtra("heartIconResource", R.drawable.heart_ful);
                    intent.putExtra("viewHolderEstate", viewHodlerEstate);

                    context.startActivity(intent);
                    break;
                case R.id.heart_icon:
                    getTokensPublisher().setTokens(currentUserToken, viewHodlerEstate.getPlaceID());
                    tokensPublisher.notifyData();
                    break;
                default:
                    break;
            }
        }
    }
}
