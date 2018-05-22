package com.mxi.myinnerpharmacy.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.adapter.PauseTaskAdapter;
import com.mxi.myinnerpharmacy.adapter.PreCabinetAdapter;
import com.mxi.myinnerpharmacy.model.Pausetask;
import com.mxi.myinnerpharmacy.model.PreCabinetData;
import com.mxi.myinnerpharmacy.network.AppController;
import com.mxi.myinnerpharmacy.network.CommanClass;
import com.mxi.myinnerpharmacy.network.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vishal on 23/2/18.
 */

public class PrescriptionCabinet extends AppCompatActivity {

    CommanClass cc;

    LinearLayoutManager mLayoutManager;

    RecyclerView rc_prescription_cabinet;

    ProgressDialog pDialog;

    ArrayList<PreCabinetData> preCabinetList;

    String date_ws,time_ws,datetimee;

    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prescription_cabinet);

        cc = new CommanClass(this);

        init();
    }

    private void init() {

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

        rc_prescription_cabinet = (RecyclerView)findViewById(R.id.rc_prescription_cabinet);

    }

    @Override
    protected void onResume() {
        super.onResume();

        getPrescriptionCabinet();
    }

    private void getPrescriptionCabinet() {

        pDialog = new ProgressDialog(PrescriptionCabinet.this);
        pDialog.setMessage("Please wait...");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_get_user_activity_prescriptions,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("Url_pres_cabinet", response);
                        try {

                            preCabinetList = new ArrayList<PreCabinetData>();

                            JSONObject jsonObject = new JSONObject(response);

                            String videoPath = jsonObject.getString("prescription_videopath");
                            String audioPath = jsonObject.getString("prescription_audiopath");

                            cc.savePrefString("videoPath",videoPath);
                            cc.savePrefString("audioPath",audioPath);

                            if (jsonObject.getString("status").equals("200")) {

                                JSONArray cabinetArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < cabinetArray.length(); i++) {

                                    JSONObject jsonObject1 = cabinetArray.getJSONObject(i);

                                    PreCabinetData pc = new PreCabinetData();

                                    datetimee = jsonObject1.getString("datetime");

                                    String[] splitStr = datetimee.split("\\s+");

                                    date_ws = splitStr[0];
                                    time_ws = splitStr[1];

                                    String inputPattern = "yyyy-MM-dd";
                                    String outputPattern = "dd-MM-yyyy";
                                    SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                                    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                                    Date date = null;
                                    String str = null;

                                    try {
                                        date = inputFormat.parse(date_ws);
                                        str = outputFormat.format(date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    Log.e("DateWS",str);
                                    Log.e("TimeWS",time_ws);


                                    pc.setDate(str);
                                    pc.setTime(time_ws);
                                    pc.setId(jsonObject1.getString("id"));
                                    pc.setActivity_name(jsonObject1.getString("activity_name"));
                                    pc.setValue(jsonObject1.getString("value"));
                                    pc.setMedia_id(jsonObject1.getString("media_id"));
                                    pc.setMedia_type(jsonObject1.getString("media_type"));
                                    pc.setMedia_name(jsonObject1.getString("media_name"));
                                    pc.setMedia_file(jsonObject1.getString("media_file"));


                                    preCabinetList.add(pc);

                                }
                                if (!preCabinetList.isEmpty()) {

                                    rc_prescription_cabinet.setHasFixedSize(true);
                                    mLayoutManager = new LinearLayoutManager(PrescriptionCabinet.this, LinearLayoutManager.VERTICAL, false);
                                    rc_prescription_cabinet.setLayoutManager(mLayoutManager);
                                    PreCabinetAdapter mAdapter = new PreCabinetAdapter(PrescriptionCabinet.this, preCabinetList);
                                    rc_prescription_cabinet.setAdapter(mAdapter);

                                }


                                pDialog.dismiss();

                            } else {
                                pDialog.dismiss();

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
