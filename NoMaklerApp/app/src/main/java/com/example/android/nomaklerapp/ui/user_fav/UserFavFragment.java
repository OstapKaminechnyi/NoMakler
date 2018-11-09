package com.example.android.nomaklerapp.ui.user_fav;

import android.content.Intent;
import android.database.Cursor;
import com.example.android.nomaklerapp.Modal.Contracts;
import com.example.android.nomaklerapp.ObserverPattern.UserAndEstateTokensObserver;
import com.example.android.nomaklerapp.R;
import com.example.android.nomaklerapp.Utils.ContentProviderUtils;
import com.example.android.nomaklerapp.Utils.ContextUtils;
import com.example.android.nomaklerapp.Utils.CursorUtils;
import com.example.android.nomaklerapp.ui.MainActivity;
import com.example.android.nomaklerapp.ui.fragment.BaseFragment;
import com.example.android.nomaklerapp.ui.sign_in_up.SignInActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

public class UserFavFragment extends BaseFragment implements UserAndEstateTokensObserver{
    private static final String LOG_TAG = UserFavFragment.class.getSimpleName();

    private Toolbar toolbar;
    private RecyclerView userEstatesRecyclerView;
    private UserEstatesAdapter userEstatesAdapter;
    private String currentUserToken;

    @Override
    protected int setLayoutResource() {
        return R.layout.fragment_user_fav;
    }



    @Override
    protected void initializeFragment() {
        MainActivity mainActivity = ContextUtils.getMainActivity(getContext());
        if(mainActivity != null){
            currentUserToken = mainActivity.getCurrentUserToken();
        }

        if(currentUserToken == null){
            Intent intent = new Intent(getContext(), SignInActivity.class);
            startActivity(intent);
            return;
        }


        /**
         * @Toolbar
         */
        toolbar = (Toolbar) rootView.findViewById(R.id.tool_bar);
        ContextUtils.setupToolbar(getContext(), toolbar);
        toolbar.setTitle("Your favorite houses");


        /**
         * @RecyclerView
         */
        Cursor userEstateCursor = getContext().getContentResolver().query(
                Contracts.UserAndEstate.CONTENT_URI,
                null,
                Contracts.UserAndEstate.USER_TOKEN + "=?",
                new String[]{currentUserToken},
                null
        );


        userEstatesAdapter = new UserEstatesAdapter(getContext(), userEstateCursor);
        // Register tokens observer
        userEstatesAdapter.getTokensPublisher().registerObserver(this);


        userEstatesRecyclerView = (RecyclerView) rootView.findViewById(R.id.user_estates_recycler_view);
        userEstatesRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.VERTICAL,
                false
        ));
        userEstatesRecyclerView.setAdapter(userEstatesAdapter);
//        userEstatesRecyclerView.setAdapter(new OnScreenEstateRVAdapter(getContext(), null));



    }

    @Override
    public void update(String userToken, String estateToken) {
        if(estateToken == null){
            return;
        }

        ContentProviderUtils.deleteRows_3rdTable(
                getContext(),
                Contracts.UserAndEstate.CONTENT_URI,
                Contracts.UserAndEstate.USER_TOKEN,
                Contracts.UserAndEstate.ESTATE_TOKEN,
                userToken,
                estateToken
        );

        userEstatesAdapter.setUserEstateCursor(
                CursorUtils.getCursor_UserAndEstate(getContext(), userToken, null)
        );
    }
}
