package com.example.android.nomaklerapp.test.xxx;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import com.example.android.nomaklerapp.Modal.Contracts;
import com.example.android.nomaklerapp.Modal.Estate.Estate;
import com.example.android.nomaklerapp.Modal.Estate_User.UserAndEstate;
import com.example.android.nomaklerapp.ObserverPattern.OnSreenEstateCursor.OnScreenEstateCursorObserver;
import com.example.android.nomaklerapp.R;
import com.example.android.nomaklerapp.Utils.ContentProviderUtils;
import com.example.android.nomaklerapp.Utils.ContextUtils;
import com.example.android.nomaklerapp.test.SliderPagerAdapter;
import com.example.android.nomaklerapp.ui.MainActivity;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class BlackRVAdapter extends RecyclerView.Adapter<BlackRVAdapter.BlankViewHolder> implements  OnScreenEstateCursorObserver{
    private Context context;
    private List<Estate> estates;
    private String currentUserToken;

    private Cursor onScreenEstateCursor;

    public BlackRVAdapter(Context context) {
        this.context = context;
        MainActivity mainActivity = ContextUtils.getMainActivity(context);
        if(mainActivity != null){
            currentUserToken = mainActivity.getCurrentUserToken();
        }
    }

    @Override
    public BlankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BlankViewHolder(
                LayoutInflater.from(context).inflate(R.layout.view_holder, parent, false)
        );
    }



    @Override
    public void onBindViewHolder(BlankViewHolder holder, int position) {
        if(estates == null || estates.isEmpty()){
            return;
        }
        final Estate estate = estates.get(position);
        holder.bindData(estate);



        holder.whole_estate_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EstateDetailActivity.class);
                intent.putExtra("viewHolderEstate", estate);
                if(context instanceof AppCompatActivity){
                    AppCompatActivity appCompatActivity = (AppCompatActivity) context;
                    appCompatActivity.startActivityForResult(intent, MainActivity.REQUEST_CODE_FOR_DETAIL_ACTIVITY);
                }
            }
        });

        holder.heartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!(context instanceof MainActivity)){
                    return;
                }
                MainActivity mainActivity = (MainActivity) context;
                String currentUserToken = mainActivity.getCurrentUserToken();
                if(currentUserToken == null){
                    Toast.makeText(context, context.getString(R.string.sign_in_warning), Toast.LENGTH_SHORT).show();
                    return;
                }

                Uri returnedUri =context.getContentResolver().insert(
                        Contracts.UserAndEstate.CONTENT_URI,
                        ContentProviderUtils.getContentValue(
                                new UserAndEstate(currentUserToken, estate.getPlaceID())
                        )
                );


            }
        });
    }

    @Override
    public int getItemCount() {
        return estates == null? 0 : estates.size();
    }



    @Override
    public void update(Cursor onScreenEstateCursor) {
        this.onScreenEstateCursor = onScreenEstateCursor;
        notifyDataSetChanged();
    }


    /**
     * @BlankViewHolder
     */
    class BlankViewHolder extends RecyclerView.ViewHolder{
        ViewPager viewPager;
        TextView textViewAddress;
        TextView textViewPrice;
        View whole_estate_layout;
        SliderPagerAdapter sliderPagerAdapter;
        ImageView heartIcon;
        public BlankViewHolder(View itemView) {
            super(itemView);
            textViewAddress = (TextView) itemView.findViewById(R.id.text_view_address);
            heartIcon = (ImageView) itemView.findViewById(R.id.heart_icon);
            whole_estate_layout = itemView.findViewById(R.id.whole_estate_layout);

            viewPager = (ViewPager) itemView.findViewById(R.id.view_pager);
            sliderPagerAdapter = new SliderPagerAdapter(context, R.layout.view_holder_slider_for_cardview, null);
            viewPager.setAdapter(sliderPagerAdapter);


        }

        public void bindData(Estate estate) {
            textViewAddress.setText(estate.getAddress());


            int imageResourse  = R.drawable.heart_outline;;
            if(currentUserToken != null){
                Cursor cursor = context.getContentResolver().query(
                        Contracts.UserAndEstate.CONTENT_URI,
                        null,
                        Contracts.UserAndEstate.USER_TOKEN + "=? AND "
                        + Contracts.UserAndEstate.ESTATE_TOKEN + "=? ",
                        new String[]{currentUserToken, estate.getPlaceID()},
                        null,
                        null
                );
                if(cursor.moveToNext()){
                    imageResourse = R.drawable.heart_ful;
                }
            }

            heartIcon.setImageResource(imageResourse);

            Cursor estateAndImageCursor = context.getContentResolver().query(
                    Contracts.EstateAndImage.CONTENT_URI,
                    null,
                    Contracts.EstateAndImage.ESTATE_PRIMARY + "=?",
                    new String[]{estate.getPlaceID()},
                    null,
                    null
            );

            sliderPagerAdapter.setEstateAndImageCursor(estateAndImageCursor);



        }


    }


}
