package com.example.android.nomaklerapp.ui.fragment;

import android.app.Activity;
import android.content.Context;
import com.example.android.nomaklerapp.ObserverPattern.PredicitonPublisher;
import com.example.android.nomaklerapp.ObserverPattern.PredictionObserver;
import com.example.android.nomaklerapp.R;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;

import java.util.ArrayList;
import java.util.List;


public class PredictionsAdapter extends RecyclerView.Adapter<PredictionsAdapter.PredictionViewHolder>  {

    private static final String LOG_TAG = PredictionsAdapter.class.getSimpleName();
    private Context context;
    private AutocompletePredictionBuffer predictions;
    private AutocompletePrediction clickedAutocompletePrediction;

    List<PredictionObserver> predictionObservers;



    public PredictionsAdapter(Context context){
        this.context = context;
        predictionObservers = new ArrayList<>();
    }
    public PredictionsAdapter(Context context, AutocompletePredictionBuffer predictions) {
        this.context = context;
        this.predictions = predictions;
        predictionObservers = new ArrayList<>();
    }

    @Override
    public PredictionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PredictionViewHolder(LayoutInflater.from(context).inflate(R.layout.view_holder_rv_search, parent, false));
    }

    @Override
    public void onBindViewHolder(PredictionViewHolder holder, int position) {
        if(predictions == null){
            return;
        }
        final AutocompletePrediction currentPrediction = predictions.get(position);
        holder.bindData(currentPrediction);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof Activity){
                    Activity activity = (Activity) context;
                    if(activity.getCurrentFocus() !=null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                    }
                }
                clickedAutocompletePrediction = currentPrediction;
                PredicitonPublisher.getInstance().setAutocompletePrediction(currentPrediction);
                PredicitonPublisher.getInstance().notifyData();
            }
        });

    }

    @Override
    public int getItemCount() {
        return predictions == null? 0 : predictions.getCount();
    }

    public void setPredictions(AutocompletePredictionBuffer predictions) {
        this.predictions = predictions;
        notifyDataSetChanged();
    }



    /**
     * @PredictionViewHolder
     */
      class PredictionViewHolder extends RecyclerView.ViewHolder{
        TextView textViewPlace;
        public PredictionViewHolder(View itemView) {
            super(itemView);

            textViewPlace = (TextView) itemView.findViewById(R.id.text_view_place);
        }

        void bindData(AutocompletePrediction prediction){
            textViewPlace.setText(prediction.getFullText(null));
        }


    }
}
