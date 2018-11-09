package com.example.android.nomaklerapp.test;

import android.content.Context;
import android.database.Cursor;
import com.example.android.nomaklerapp.Modal.Contracts;
import com.example.android.nomaklerapp.R;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class SliderPagerAdapter extends PagerAdapter {
    private static final String LOG_TAG = SliderPagerAdapter.class.getSimpleName();
    private Context context;
    private Cursor estateAndImageCursor;
    private int layoutResource;

    public SliderPagerAdapter(Context context, int layoutResource, Cursor estateAndImageCursor) {
        this.context = context;
        this.layoutResource = layoutResource;
        this.estateAndImageCursor = estateAndImageCursor;
    }

    @Override
    public int getCount() {
        return estateAndImageCursor == null? 0: estateAndImageCursor.getCount();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View rootView = LayoutInflater.from(context).inflate(layoutResource, container, false);
        container.addView(rootView);
        if(estateAndImageCursor == null){
            Log.d(
                    LOG_TAG,
                    "estateAndImageCursor == null || ! estateAndImageCursor.moveToPosition(" + position+ ")"
            );
            return rootView;
        }

        if(estateAndImageCursor.moveToPosition(position)){
            String link = estateAndImageCursor.getString(Contracts.EstateAndImage.INDEX_IMAGE_PRIMARY);
            Cursor imageCursor = context.getContentResolver().query(
                    Contracts.Image.CONTENT_URI,
                    null,
                    Contracts.Image.LINK + "=?",
                    new String[]{link},
                    null
            );

            if(imageCursor.moveToFirst()){
                String actualink = imageCursor.getString(Contracts.Image.INDEX_LINK).replace("*","/");
                Log.d(LOG_TAG, actualink);
                ImageView imageView = (ImageView) rootView.findViewById(R.id.image_view);
                Picasso.with(context).load(actualink).into(imageView);
            }else {
                Log.d(LOG_TAG, "! imageCursor.moveToFirst()");

            }

        }else{
            Log.d(
                    LOG_TAG,
                    "! estateAndImageCursor.moveToPosition(" + position+ ")"
            );
        }


        return rootView;

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setEstateAndImageCursor(Cursor estateAndImageCursor) {
        this.estateAndImageCursor = estateAndImageCursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }
}
