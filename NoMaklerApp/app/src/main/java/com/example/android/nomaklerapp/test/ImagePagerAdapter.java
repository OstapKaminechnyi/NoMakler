package com.example.android.nomaklerapp.test;

import android.content.Context;
import com.example.android.nomaklerapp.R;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ImagePagerAdapter extends PagerAdapter {
    private static final String LOG_TAG = ImagePagerAdapter.class.getSimpleName();
    private List<String> urlStrings ;
    private View rootView;
    private Context context;
    private int layoutResource;

    public ImagePagerAdapter(Context context, int layoutResource) {
        this.context = context;
        this.layoutResource = layoutResource;
        urlStrings = new ArrayList<>();
        urlStrings.add("http://thumbs.trulia-cdn.com/pictures/thumbs_3/ps.101/e/f/1/8/picture-uh=8a4f86ed792c1f6d256e7c20a729f4e-ps=ef184274f06c1a1c6775c7288e484ba.jpg");
        urlStrings.add("http://thumbs.trulia-cdn.com/pictures/thumbs_3/ps.92/8/8/4/4/picture-uh=fac184283584919c7cb0ecb80ac7d2-ps=884494f039b6b7a9f19af3fd9fbfb5.jpg");
        urlStrings.add("http://thumbs.trulia-cdn.com/pictures/thumbs_3/ps.96/b/f/7/c/picture-uh=42a2d1f25c6c2a5de1850c7168ab048-ps=bf7c42a2abffb9d157181bd16595.jpg");
        urlStrings.add("http://thumbs.trulia-cdn.com/pictures/thumbs_3/ps.99/5/7/5/3/picture-uh=2b3e59228cba5b6a423e87189b475-ps=5753d55d82aea182531d20f4f9ae4ace.jpg");
        urlStrings.add("http://thumbs.trulia-cdn.com/pictures/thumbs_3/ps.99/8/3/d/8/picture-uh=e1dacee8bb91a5b980726edc8f1e5d0-ps=83d841d7aaed2e45542eccc777d5b3bb.jpg");
        urlStrings.add("http://thumbs.trulia-cdn.com/pictures/thumbs_3/ps.99/8/1/8/e/picture-uh=a319c44d73a8c6205b8d74bbb2963dc6-ps=818eba3758763cca6d83b8b1914fae2.jpg");
        urlStrings.add("http://thumbs.trulia-cdn.com/pictures/thumbs_3/ps.99/8/1/8/e/picture-uh=a319c44d73a8c6205b8d74bbb2963dc6-ps=818eba3758763cca6d83b8b1914fae2.jpg");
        urlStrings.add("http://thumbs.trulia-cdn.com/pictures/thumbs_3/ps.101/9/2/1/d/picture-uh=b4a757994ace866740c5bf0451699a4-ps=921d4270a44cbd558c4965f18a82a5d9.jpg");
        urlStrings.add("http://thumbs.trulia-cdn.com/pictures/thumbs_3/ps.101/7/9/1/d/picture-uh=1c4897dd42f6e4c546cdc87e1fceafd-ps=791d14ef8c7ffc40a18bd3338dbc8ba.jpg");
        urlStrings.add("http://thumbs.trulia-cdn.com/pictures/thumbs_3/ps.97/9/1/b/a/picture-uh=47a66e2fff45cd3a717d44efbe8ae2a-ps=91ba99fcaab423c8b7e0f55ed9f82351.jpg");
        Collections.shuffle(urlStrings);
    }

    @Override
    public int getCount() {
        return urlStrings == null? 0 : urlStrings.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if(urlStrings == null || urlStrings.isEmpty()){
            return super.instantiateItem(container, position);
        }
        rootView = LayoutInflater.from(context).inflate(layoutResource, container, false);
        Log.d(LOG_TAG, rootView == null ? "rootView == null ": "rootView != null ");

        ImageView imageView = (ImageView) rootView.findViewById(R.id.image_view);

        String currentUrl = urlStrings.get(position);
        Log.d(LOG_TAG, currentUrl);

        Picasso.with(context).load(currentUrl).into(imageView);

        container.addView(rootView);
        return rootView;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }


}
