package com.mxi.myinnerpharmacy.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.activity.PaymentActivity;
import com.mxi.myinnerpharmacy.model.SubscriptionPlan;
import com.mxi.myinnerpharmacy.network.CommanClass;

import java.util.ArrayList;

/**
 * Created by aksahy on 24/11/17.
 */

public class SubscriptionPlanAdapter extends RecyclerView.Adapter<SubscriptionPlanAdapter.MyViewHolder> {


    private LayoutInflater inflater;
    private Context context;
    ArrayList<SubscriptionPlan> planArrayList;

    CommanClass cc;

    public SubscriptionPlanAdapter(Context context, ArrayList<SubscriptionPlan> planArrayList) {
        this.context = context;

        inflater = LayoutInflater.from(context);
        this.planArrayList= planArrayList;

        cc = new CommanClass(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder;

        View view = inflater.inflate(R.layout.row_subscription_plan, parent, false);
        holder = new MyViewHolder(view, viewType);

        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if (planArrayList.get(position).getDiscount().equals("0")){
            holder.tvDiscount.setVisibility(View.GONE);
            holder.tvDiscountText.setVisibility(View.GONE);
        }



        holder.tvPlanName.setText(planArrayList.get(position).getPlan_name());

        String month=planArrayList.get(position).getPeriod();
        String price=planArrayList.get(position).getPrice();
        String planText="Subscription for "+month+" month at just $"+price;

        holder.tvPlanText.setText(planText);
        String discount=planArrayList.get(position).getDiscount()+"% off";
        holder.tvDiscount.setText(discount);

        int disc= Integer.parseInt(planArrayList.get(position).getDiscount());
        float pricee= Float.parseFloat(price);

        float d_price=(pricee*disc)/100;
        float final_d_price=pricee-d_price;

        holder.tvDiscount.setText(discount);
        String fDP=String.format("%.2f", final_d_price);
        holder.tvDiscountText.setText("Discount Price: $"+fDP+"");

        holder.ll_subscription_row_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, PaymentActivity.class);
                intent.putExtra("plan_id", planArrayList.get(position).getId());
                intent.putExtra("price", planArrayList.get(position).getPrice());
                intent.putExtra("plan_name",planArrayList.get(position).getPlan_name());
                intent.putExtra("validity",planArrayList.get(position).getPeriod());

                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return planArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvPlanName;
        private TextView tvPlanText;
        private TextView tvDiscountText;
        private TextView tvDiscount;
        LinearLayout ll_subscription_row_main;

        public MyViewHolder(View itemView, int ViewType) {
            super(itemView);

            tvPlanName = (TextView) itemView.findViewById(R.id.tv_plan_name);
            tvPlanText = (TextView) itemView.findViewById(R.id.tv_plan_text);
            tvDiscountText = (TextView) itemView.findViewById(R.id.tv_discount_text);
            tvDiscount = (TextView) itemView.findViewById(R.id.tv_discount);
            ll_subscription_row_main= (LinearLayout) itemView.findViewById(R.id.ll_subscription_row_main);
        }
    }

}
