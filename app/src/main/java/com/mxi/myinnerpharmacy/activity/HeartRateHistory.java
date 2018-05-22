package com.mxi.myinnerpharmacy.activity;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
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
import com.mxi.myinnerpharmacy.adapter.AdapterHeartRateHistory;
import com.mxi.myinnerpharmacy.adapter.HeartHistoryAdaptor;
import com.mxi.myinnerpharmacy.database.SQLiteTD;
import com.mxi.myinnerpharmacy.model.HertRateHistory;
import com.mxi.myinnerpharmacy.model.heartrate;
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
import java.util.List;
import java.util.Map;

public class HeartRateHistory extends AppCompatActivity {

//    Toolbar toolbar;
    RecyclerView rv_recyclerview;
    ArrayList<heartrate> heartlist;
    TextView tv_history;
    SQLiteTD db;

    CommanClass cc;

    ProgressDialog pDialog;

    HeartHistoryAdaptor mAdapter;

    ImageView iv_back;

    String date_ws,time_ws,datetimee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate_history);
        db = new SQLiteTD(HeartRateHistory.this);

        cc = new CommanClass(this);

        iv_back = (ImageView)findViewById(R.id.iv_back);

        rv_recyclerview = (RecyclerView) findViewById(R.id.rv_recyclerview);
        rv_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        rv_recyclerview.setItemAnimator(new DefaultItemAnimator());



        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        tv_history = (TextView) findViewById(R.id.tv_history);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                // startActivity(new Intent(HeartRate.this, ));
            }
        });*/
        tv_history.setText(Html.fromHtml("<font color=#FFFFFF><u>" + "Heart Rate History" + "</u></font>"));
        //===================================SET ADAPTER========================================================================
        //rememberMe();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
        
        getHeartRateHistoryData();
    }

    private void getHeartRateHistoryData() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please Wait");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.GET, URL.Url_heartrate_history,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.e("historyresponse",response);

                        List<HertRateHistory> data_history_list = new ArrayList<>();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("200")) {


                                JSONArray jsonArray = jsonObject.optJSONArray("data");

                                for(int i=0; i < jsonArray.length(); i++){

                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                    HertRateHistory items = new HertRateHistory();


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

                                    items.setDatee(str);
                                    items.setTimee(time_ws);
                                    items.setId(jsonObject1.getString("id"));
                                    items.setBreathing_rate(jsonObject1.getString("breathing"));
                                    items.setCalibration_state_name(jsonObject1.getString("calibration_state_name"));
                                    items.setHeart_rate(jsonObject1.getString("heart_rate"));
                                    items.setSelf_talk_id(jsonObject1.getString("self_talk_id"));
                                    items.setPrescriptionname(jsonObject1.getString("prescription_name"));


                                    data_history_list.add(items);

                                }

                                mAdapter = new HeartHistoryAdaptor(data_history_list,R.layout.ow_heartrate_history,getApplicationContext());
                                rv_recyclerview.setAdapter(mAdapter);

                                pDialog.dismiss();

                            } else {

                                jsonObject.getString("message");

                                pDialog.dismiss();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("data Error",e.toString());
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();

                cc.showToast("Something went wrong");

                //AndyUtils.showToast(ArticlesActivity.this,getString(R.string.ws_error));
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

   /* private void rememberMe() {

        try {
            Cursor c = db.getHeartrate();
            heartlist = new ArrayList<heartrate>();
            if (c.getCount() != 0 && c != null) {
                c.moveToFirst();
                do {
                    heartrate rt = new heartrate();
                    rt.setId(c.getInt(0));
                    rt.setDate(c.getString(1));
                    rt.setHeart_rate(c.getString(2));
                    heartlist.add(rt);

                } while (c.moveToNext());

            }

            rv_recyclerview.setHasFixedSize(true);
            rv_recyclerview.setAdapter(new AdapterHeartRateHistory(HeartRateHistory.this, heartlist));
            rv_recyclerview.setLayoutManager(new LinearLayoutManager(HeartRateHistory.this, LinearLayoutManager.VERTICAL, false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
