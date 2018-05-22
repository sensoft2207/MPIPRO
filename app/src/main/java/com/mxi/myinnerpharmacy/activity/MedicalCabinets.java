package com.mxi.myinnerpharmacy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.model.PrescriptionDetails;
import com.mxi.myinnerpharmacy.network.AppController;
import com.mxi.myinnerpharmacy.network.CommanClass;
import com.mxi.myinnerpharmacy.network.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MedicalCabinets extends AppCompatActivity implements View.OnClickListener {

    public static ArrayList<PrescriptionDetails> audioPrescriptionList;
    public static ArrayList<PrescriptionDetails> textPrescriptionList;
    public static ArrayList<PrescriptionDetails> videoPrescriptionList;
    public static String priscriptionLable;
    Toolbar toolbar;
    TextView tv_stand_1, tv_stand_2, tv_stand_3;
    ImageView iv_bottle_1_1, iv_bottle_1_2, iv_bottle_1_3,iv_bottle_1_4;
    ImageView iv_bottle_2_1, iv_bottle_2_2, iv_bottle_2_3,iv_bottle_2_4;
    ImageView iv_bottle_3_1, iv_bottle_3_2, iv_bottle_3_3,iv_bottle_3_4;
    LinearLayout ll_stack_1, ll_stack_2, ll_stack_3;
    String prescription_1, prescription_2, prescription_3,prescription_4, prescription_5, prescription_6,prescription_7, prescription_8, prescription_9, prescription_10,prescription_11;
    String prescription_1_id, prescription_2_id, prescription_3_id,
            prescription_4_id,prescription_5_id, prescription_6_id, prescription_7_id,
            prescription_8_id, prescription_9_id, prescription_10_id,prescription_11_id;

    CommanClass cc;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_cabinets);
        cc = new CommanClass(this);




        prescription_11 = getIntent().getStringExtra("elevenPrescription");
        prescription_11_id = getIntent().getStringExtra("elevenPrescription_id");

        prescription_1 = getIntent().getStringExtra("firstPrescription");
        prescription_1_id = getIntent().getStringExtra("firstPrescription_id");

        prescription_2 = getIntent().getStringExtra("secondPrescription");
        prescription_2_id = getIntent().getStringExtra("secondPrescription_id");

        prescription_3 = getIntent().getStringExtra("thirdPrescription");
        prescription_3_id = getIntent().getStringExtra("thirdPrescription_id");

        prescription_4 = getIntent().getStringExtra("fourPrescription");
        prescription_4_id = getIntent().getStringExtra("fourPrescription_id");

        prescription_5 = getIntent().getStringExtra("fivePrescription");
        prescription_5_id = getIntent().getStringExtra("fivePrescription_id");

        prescription_6 = getIntent().getStringExtra("sixPrescription");
        prescription_6_id = getIntent().getStringExtra("sixPrescription_id");


        prescription_7 = getIntent().getStringExtra("sevenPrescription");
        prescription_7_id = getIntent().getStringExtra("sevenPrescription_id");

        prescription_8 = getIntent().getStringExtra("EightPrescription");
        prescription_8_id = getIntent().getStringExtra("EightPrescription_id");

        prescription_9 = getIntent().getStringExtra("ninePrescription");
        prescription_9_id = getIntent().getStringExtra("ninePrescription_id");

        prescription_10 = getIntent().getStringExtra("tenPrescription");
        prescription_10_id = getIntent().getStringExtra("tenPrescription_id");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        tv_stand_1 = (TextView) findViewById(R.id.tv_stand_1);
//        tv_stand_2 = (TextView) findViewById(R.id.tv_stand_2);
//        tv_stand_3 = (TextView) findViewById(R.id.tv_stand_3);

        iv_bottle_1_1 = (ImageView) findViewById(R.id.iv_bottle_1_1);
        iv_bottle_2_1 = (ImageView) findViewById(R.id.iv_bottle_2_1);
        iv_bottle_3_1 = (ImageView) findViewById(R.id.iv_bottle_3_1);
        iv_bottle_1_2 = (ImageView) findViewById(R.id.iv_bottle_1_2);
        iv_bottle_1_4 = (ImageView) findViewById(R.id.iv_bottle_1_4);
        iv_bottle_2_4 = (ImageView) findViewById(R.id.iv_bottle_2_4);
        iv_bottle_3_4 = (ImageView) findViewById(R.id.iv_bottle_3_4);

        iv_bottle_2_2 = (ImageView) findViewById(R.id.iv_bottle_2_2);
        iv_bottle_3_2 = (ImageView) findViewById(R.id.iv_bottle_3_2);
        iv_bottle_1_3 = (ImageView) findViewById(R.id.iv_bottle_1_3);
        iv_bottle_2_3 = (ImageView) findViewById(R.id.iv_bottle_2_3);
        iv_bottle_3_3 = (ImageView) findViewById(R.id.iv_bottle_3_3);

        ll_stack_1 = (LinearLayout) findViewById(R.id.ll_bottle_stack_1);
        ll_stack_2 = (LinearLayout) findViewById(R.id.ll_bottle_stack_2);
        ll_stack_3 = (LinearLayout) findViewById(R.id.ll_bottle_stack_3);
//
//        tv_stand_1.setText(prescription_1);
//        tv_stand_2.setText(prescription_2);
//        tv_stand_3.setText(prescription_3);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                // startActivity(new Intent(HeartRate.this, ));
            }
        });*/

        initListener();
    }


    private void initListener() {

        iv_bottle_1_4.setOnClickListener(this);
        iv_bottle_2_4.setOnClickListener(this);
        iv_bottle_3_4.setOnClickListener(this);

        iv_bottle_1_1.setOnClickListener(this);
        iv_bottle_2_1.setOnClickListener(this);
        iv_bottle_3_1.setOnClickListener(this);

        iv_bottle_1_2.setOnClickListener(this);
        iv_bottle_2_2.setOnClickListener(this);
        iv_bottle_3_2.setOnClickListener(this);

        iv_bottle_1_3.setOnClickListener(this);
        iv_bottle_2_3.setOnClickListener(this);
        iv_bottle_3_3.setOnClickListener(this);


    }

    public void callWs(String prescription_id) {

        try {
            if (cc.isConnectingToInternet()) {
                pDialog = new ProgressDialog(MedicalCabinets.this);
                pDialog.setMessage("Please wait...");
                pDialog.show();
                makeJsonGetPrescriptions(prescription_id);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        // put your code here...


    }

    private void makeJsonGetPrescriptions(final String prescription_id) {
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_get_prescription_bottles,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("Url_get_prescription", response);
                        jsonGetPrescriptionTask(response);
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
//                cc.showSnackbar(recycler_view, getString(R.string.ws_error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("prescription_id", prescription_id);

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


    private void jsonGetPrescriptionTask(String response) {
        try {
            audioPrescriptionList = new ArrayList<PrescriptionDetails>();
            videoPrescriptionList = new ArrayList<PrescriptionDetails>();
            textPrescriptionList = new ArrayList<PrescriptionDetails>();
            JSONObject jsonObject = new JSONObject(response);
            pDialog.dismiss();
            if (jsonObject.getString("status").equals("200")) {


                String audio_path = jsonObject.getString("prescription_audiopath");
                String video_path = jsonObject.getString("prescription_videopath");
                String text_path = jsonObject.getString("prescription_textpath");

                JSONArray prescription = jsonObject.getJSONArray("prescriptionBottles");

                for (int i = 0; i < prescription.length(); i++) {
                    JSONObject jsonObject1 = prescription.getJSONObject(i);

                    PrescriptionDetails pk = new PrescriptionDetails();
                    pk.setMedia_type(jsonObject1.getString("media_type"));
                    pk.setMedia_name(jsonObject1.getString("media_name"));
                    pk.setMedia_id(jsonObject1.getString("media_id"));

                    Log.e("media_type", jsonObject1.getString("media_type"));
                    String media = jsonObject1.getString("media_type");
                    if (media.equals("Audio")) {
                        // pk.setPrescription_audiopath(audio_path);
                        pk.setMedia_file(audio_path + jsonObject1.getString("media_file"));
                        audioPrescriptionList.add(pk);
                    } else if (media.equals("Video")) {
                        // pk.setPrescription_videopath(video_path);
                        pk.setMedia_file(video_path + jsonObject1.getString("media_file"));
                        videoPrescriptionList.add(pk);
                    } else if (media.equals("Text")) {
                        //pk.setPrescription_textpath(text_path);
                        pk.setMedia_file(text_path + jsonObject1.getString("media_file"));
                        textPrescriptionList.add(pk);
                    }

                }

                Log.e("audioPrescriptionList", audioPrescriptionList.size() + "");
                Log.e("videoPrescriptionList", videoPrescriptionList.size() + "");
                Log.e("textPrescriptionList", textPrescriptionList.size() + "");

                Intent intent = new Intent(MedicalCabinets.this, PrescriptionStep.class);
                startActivity(intent);
                finish();

            } else {

                cc.showToast(jsonObject.getString("message"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.iv_bottle_1_1 ) {
            callWs(getIntent().getStringExtra("firstPrescription_id"));
            priscriptionLable = prescription_1;


        } else if (v.getId() == R.id.iv_bottle_1_2 ) {

            callWs(getIntent().getStringExtra("secondPrescription_id"));
            priscriptionLable = prescription_2;

        } else if (v.getId() == R.id.iv_bottle_1_3 ) {

            callWs(getIntent().getStringExtra("thirdPrescription_id"));
            priscriptionLable = prescription_3;

        }else if (v.getId() == R.id.iv_bottle_1_4 ) {

            callWs(getIntent().getStringExtra("fourPrescription_id"));
            priscriptionLable = prescription_4;

        }
        else if (v.getId() == R.id.iv_bottle_2_1 ) {


            callWs(getIntent().getStringExtra("fivePrescription_id"));
            priscriptionLable = prescription_5;
        }
        else if (v.getId() == R.id.iv_bottle_2_2 ) {

            callWs(getIntent().getStringExtra("sixPrescription_id"));
            priscriptionLable = prescription_6;
            //Log.e("six", getIntent().getStringExtra("sixPrescription_id"));
        }
        else if (v.getId() == R.id.iv_bottle_2_3 ) {

            callWs(getIntent().getStringExtra("sevenPrescription_id"));
            priscriptionLable = prescription_7;
        }
        else if (v.getId() == R.id.iv_bottle_2_4 ) {

            callWs(getIntent().getStringExtra("EightPrescription_id"));
            priscriptionLable = prescription_8;
        }  else if (v.getId() == R.id.iv_bottle_3_1 ) {

            callWs(getIntent().getStringExtra("ninePrescription_id"));
            priscriptionLable = prescription_9;
        }
        else if (v.getId() == R.id.iv_bottle_3_2 ) {

            callWs(getIntent().getStringExtra("tenPrescription_id"));
            priscriptionLable = prescription_10;
        }
        else if (v.getId() == R.id.iv_bottle_3_3 ) {

            callWs(getIntent().getStringExtra("elevenPrescription_id"));
            priscriptionLable = prescription_11;
        }
        else if (v.getId() == R.id.iv_bottle_3_4 ) {

            callWs(getIntent().getStringExtra("thirdPrescription_id"));
            priscriptionLable = prescription_2;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent i=new Intent(MedicalCabinets.this,MainActivity.class);
            startActivity(i);


            return true;
        } else {


            return super.onKeyDown(keyCode, event);
        }
    }
}
