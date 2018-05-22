package com.mxi.myinnerpharmacy.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.adapter.SubscriptionPlanAdapter;
import com.mxi.myinnerpharmacy.model.SubscriptionPlan;
import com.mxi.myinnerpharmacy.network.AppController;
import com.mxi.myinnerpharmacy.network.CommanClass;
import com.mxi.myinnerpharmacy.network.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SubscriptionPlanActivity extends AppCompatActivity {

    RecyclerView rv_subscription;
    CommanClass cc;
    ProgressDialog pDialog;
    ArrayList<SubscriptionPlan> planArrayList;
    SubscriptionPlanAdapter subscriptionPlanAdapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_plan);
        rv_subscription = (RecyclerView) findViewById(R.id.rv_subscription);
        cc = new CommanClass(this);
        pDialog = new ProgressDialog(SubscriptionPlanActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.show();
        makeJsonGetPlans();

    }


    private void makeJsonGetPlans() {

        StringRequest jsonObjReq = new StringRequest(Request.Method.GET, URL.Url_get_subscription_plan,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("subscription_plan", response);
                        jsonParseMatchList(response);
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();

            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");

    }

    private void jsonParseMatchList(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            pDialog.dismiss();
            if (jsonObject.getString("status").equals("200")) {
                planArrayList = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    SubscriptionPlan subscriptionPlan = new SubscriptionPlan();

                    subscriptionPlan.setId(jsonObject1.getString("id"));
                    subscriptionPlan.setPlan_name(jsonObject1.getString("plan_name"));
                    subscriptionPlan.setPeriod(jsonObject1.getString("period"));
                    subscriptionPlan.setUnit(jsonObject1.getString("unit"));
                    subscriptionPlan.setPrice(jsonObject1.getString("price"));
                    subscriptionPlan.setDiscount(jsonObject1.getString("discount"));

                    planArrayList.add(subscriptionPlan);
                }

                if (planArrayList != null) {

                    if (planArrayList.size() > 0) {

                        subscriptionPlanAdapter = new SubscriptionPlanAdapter(SubscriptionPlanActivity.this, planArrayList);
                        linearLayoutManager = new LinearLayoutManager(SubscriptionPlanActivity.this);
                        rv_subscription.setLayoutManager(linearLayoutManager);
                        rv_subscription.setAdapter(subscriptionPlanAdapter);

                    }
                }

            } else {

                cc.showToast(jsonObject.getString("message"));
            }

        } catch (JSONException e) {
            //e.printStackTrace();
        }
    }
}
