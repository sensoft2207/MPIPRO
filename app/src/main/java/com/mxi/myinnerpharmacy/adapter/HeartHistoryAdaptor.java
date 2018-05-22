package com.mxi.myinnerpharmacy.adapter;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.model.HertRateHistory;
import com.mxi.myinnerpharmacy.network.CommanClass;

import java.util.Collections;
import java.util.List;

/**
 * Created by vishal on 20/2/18.
 */

public class HeartHistoryAdaptor extends RecyclerView.Adapter<HeartHistoryAdaptor.ViewHolder> {


    CommanClass cc;



    public static String id = "", name = "";

    private List<HertRateHistory> countries = Collections.emptyList();;
    private int rowLayout;
    public static Context mContext;
    public static Fragment newFragment;

    public HeartHistoryAdaptor(List<HertRateHistory> countries, int rowLayout, Context context) {

        cc = new CommanClass(context);

        this.countries = countries;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }
    @Override
    public int getItemCount() {
        return countries == null ? 0 : countries.size();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        HertRateHistory myItem = countries.get(i);

        viewHolder.tv_heartrate.setText(myItem.getHeart_rate());
        viewHolder.tv_breathing_rate.setText(myItem.getBreathing_rate());
        viewHolder.tv_prescription.setText(myItem.getPrescriptionname());
        viewHolder.tv_self_talk.setText(myItem.getSelf_talk_id());
        viewHolder.tv_state_calibration.setText(myItem.getCalibration_state_name());
        viewHolder.tv_date.setText(myItem.getDatee());
        viewHolder.tv_time.setText(myItem.getTimee());

    }

    private void doClick(int adapterPosition) {

        HertRateHistory model = countries.get(adapterPosition);


    }
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_heartrate,tv_date,tv_time,tv_breathing_rate,tv_prescription,tv_self_talk,tv_state_calibration;

        public ViewHolder(View itemView) {
            super(itemView);


            tv_heartrate = (TextView) itemView.findViewById(R.id.tv_heartrate);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_breathing_rate = (TextView) itemView.findViewById(R.id.tv_breathing_rate);
            tv_prescription = (TextView) itemView.findViewById(R.id.tv_prescription);
            tv_self_talk = (TextView) itemView.findViewById(R.id.tv_self_talk);
            tv_state_calibration = (TextView) itemView.findViewById(R.id.tv_state_calibration);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }}
