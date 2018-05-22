package com.mxi.myinnerpharmacy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.network.AppController;
import com.mxi.myinnerpharmacy.network.CommanClass;
import com.mxi.myinnerpharmacy.network.URL;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {
    private ImageView ivBack;
    private TextView tvToolbarTitle;
    private TextView tvPlanName;
    private TextView tvPrice, tvPlanValidity;
    private CardInputWidget stripeCardView;
    EditText et_user_name;
    Button btnPay;
    String userName;
    CommanClass cc;
    ProgressDialog pDialog;
    String PUBLISHABLE_KEY = "pk_test_Pkx00KDpN8R5JJMiDEW5gboH";
    String planName, planId, price, validity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        planName = getIntent().getStringExtra("plan_name");
        price = getIntent().getStringExtra("price");
        planId = getIntent().getStringExtra("plan_id");
        validity = getIntent().getStringExtra("validity");


        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvToolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        tvPlanName = (TextView) findViewById(R.id.tvPlanName);
        tvPlanValidity = (TextView) findViewById(R.id.tvPlanValidity);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        stripeCardView = (CardInputWidget) findViewById(R.id.stripe_cardView);
        et_user_name = (EditText) findViewById(R.id.et_user_name);
        btnPay = (Button) findViewById(R.id.btnPay);
        cc = new CommanClass(this);

        tvPlanName.setText(planName);
        tvPrice.setText("$" + price);
        tvPlanValidity.setText(validity + " Month");

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et_user_name.getText().toString().equals("")) {
                    userName = et_user_name.getText().toString();
                    if (stripeCardView == null) {
                        cc.showToast("Invalid Card Data");
                    } else {
                        saveCreditCard(stripeCardView.getCard(), userName);
                    }
                } else {
                    cc.showToast("Please Enter Name");
                }
            }
        });
    }

    public void saveCreditCard(Card card, String Name) {
        pDialog = new ProgressDialog(PaymentActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        System.out.println(card);
//
//  Log.e("cvv_number",card+"");

        card.setName(Name);

        boolean validation = card.validateCard();
        if (validation) {
            Stripe stripe = new Stripe(PaymentActivity.this, PUBLISHABLE_KEY);
            stripe.createToken(
                    card,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            // Send token to your server

                            Log.e("Token final", token.getId());
                            Log.e("Token", token.toString());

                            makeJsonCallForCreatePayment(token.getId());

                        }

                        public void onError(Exception error) {
                            // Show localized error message
                            cc.showToast(error.getLocalizedMessage());
                            if (pDialog.isShowing()) {
                                pDialog.dismiss();
                            }
                        }
                    }
            );
        }
    }


    private void makeJsonCallForCreatePayment(final String token) {


        final StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_create_payment,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("Url_create_payment", response);
                        if (pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
//                        jsonParseMatchList(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String message = jsonObject.getString("message");
                            if (jsonObject.getString("status").equals("200")) {
                                startActivity(new Intent(PaymentActivity.this, MainActivity.class));
                                finish();
                            }
                            cc.showToast(message);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("plan_id", planId);
                Log.i("request login", params.toString());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("mip-token", cc.loadPrefString("mip-token"));
                Log.i("request header", headers.toString());
                return headers;

            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");
    }


}
