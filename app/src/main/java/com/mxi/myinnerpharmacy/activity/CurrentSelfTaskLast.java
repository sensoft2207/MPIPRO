package com.mxi.myinnerpharmacy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.adapter.PrescriptionListAdapter;
import com.mxi.myinnerpharmacy.adapter.RapidResponseAdapter;
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

/**
 * Created by mxi on 5/2/18.
 */

public class CurrentSelfTaskLast extends AppCompatActivity {


    ArrayList<PrescriptionDetails> audioList, videoList, textList;
    PrescriptionListAdapter audioAdapter, videoAdapter, textAdapter;
    RecyclerView rv_text, rv_audio, rv_video;
    LinearLayoutManager llm1, llm2, llm3;

    ImageView iv_video_add, iv_audio_add, iv_text_add;
    TextView tv_done, tv_vid, tv_aud, tv_text,tv_current_self;
    CommanClass cc;
    Toolbar toolbar;

    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rapid_response_prescription);
        cc = new CommanClass(CurrentSelfTaskLast.this);
        init();

    }

    private void init() {

        iv_video_add = (ImageView) findViewById(R.id.iv_video_add);
        iv_audio_add = (ImageView) findViewById(R.id.iv_audio_add);
        iv_text_add = (ImageView) findViewById(R.id.iv_text_add);
        tv_done = (TextView) findViewById(R.id.tv_done);
        tv_vid = (TextView) findViewById(R.id.tv_1_rapid_response);
        tv_aud = (TextView) findViewById(R.id.tv_2_rapid_response);
        tv_text = (TextView) findViewById(R.id.tv_3_rapid_response);
        tv_current_self = (TextView) findViewById(R.id.tv_current_self);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        rv_audio = (RecyclerView) findViewById(R.id.rv_audio_rapid_res);
        rv_video = (RecyclerView) findViewById(R.id.rv_video_rapid_res);
        rv_text = (RecyclerView) findViewById(R.id.rv_text_rapid_res);

        llm1 = new LinearLayoutManager(this);
        llm2 = new LinearLayoutManager(this);
        llm3 = new LinearLayoutManager(this);

        if (cc.loadPrefString("menu_selfi_click").equals("msc")){

        }else {

            tv_current_self.setText("My Current Self Talk Topics");
        }

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //

                if (cc.loadPrefString("media_id").isEmpty()){

                   cc.showToast("Done activity first");

                }else {

                    Log.e("@@media_id",cc.loadPrefString("media_id"));

                    callUserActivityWS(cc.loadPrefString("media_id"));

                    cc.savePrefString("prescription_media_data",cc.loadPrefString("media_id"));
                }


            }
        });

        getPrescriptionBottle();


    }

    private void callUserActivityWS(final String valueID) {

        pDialog = new ProgressDialog(CurrentSelfTaskLast.this);
        pDialog.setMessage("Please wait...");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_user_activity,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("Url_user_activity", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("200")) {

                                cc.showToast(jsonObject.getString("message"));

                                if (cc.loadPrefString("menu_selfi_click").equals("msc")){

                                    startActivity(new Intent(CurrentSelfTaskLast.this, MyDeads.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                                    cc.savePrefString("menu_selfi_click","");

                                }else {

                                    startActivity(new Intent(CurrentSelfTaskLast.this, StateCalibrationNext.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    cc.savePrefString("reverse_state","rs");
                                    cc.savePrefString("self_topic_last","stl");

                                    cc.savePrefString("media_id","");

                                }



                            } else {

                                cc.showToast(jsonObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pDialog.dismiss();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                cc.showToast(getString(R.string.ws_error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", cc.loadPrefString("mip-token"));
                params.put("activity_name","PRESCRIPTION");
                params.put("value", valueID);
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

    private void getPrescriptionBottle() {

        pDialog = new ProgressDialog(CurrentSelfTaskLast.this);
        pDialog.setMessage("Please wait...");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_get_prescription_bottles,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("Url_get_pre_bottle", response);


                        jsonGetPrescriptionTask(response);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                cc.showToast(getString(R.string.ws_error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

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
            audioList = new ArrayList<PrescriptionDetails>();
            videoList = new ArrayList<PrescriptionDetails>();
            textList = new ArrayList<PrescriptionDetails>();
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
                        audioList.add(pk);
                    } else if (media.equals("Video")) {
                        // pk.setPrescription_videopath(video_path);
                        pk.setMedia_file(video_path + jsonObject1.getString("media_file"));
                        videoList.add(pk);
                    } else if (media.equals("Text")) {
                        //pk.setPrescription_textpath(text_path);
                        pk.setMedia_file(text_path + jsonObject1.getString("media_file"));
                        textList.add(pk);
                    }

                }

                Log.e("audioPrescriptionList", audioList.size() + "");
                Log.e("videoPrescriptionList", videoList.size() + "");
                Log.e("textPrescriptionList", textList.size() + "");

                if (!audioList.isEmpty()) {
                    rv_text.setVisibility(View.VISIBLE);
                    audioAdapter = new PrescriptionListAdapter(CurrentSelfTaskLast.this, audioList);
                } else {
                    rv_text.setVisibility(View.GONE);
                }

                if (!videoList.isEmpty()) {
                    rv_video.setVisibility(View.VISIBLE);
                    videoAdapter = new PrescriptionListAdapter(CurrentSelfTaskLast.this, videoList);
                } else {
                    rv_video.setVisibility(View.GONE);
                }

                if (!textList.isEmpty()) {
                    rv_text.setVisibility(View.VISIBLE);
                    textAdapter = new PrescriptionListAdapter(CurrentSelfTaskLast.this, textList);
                } else {
                    rv_text.setVisibility(View.GONE);
                }

                rv_audio.setLayoutManager(llm1);
                rv_video.setLayoutManager(llm2);
                rv_text.setLayoutManager(llm3);

                rv_audio.setAdapter(audioAdapter);
                rv_video.setAdapter(videoAdapter);
                rv_text.setAdapter(textAdapter);



            } else {

                cc.showToast(jsonObject.getString("message"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

