package com.mxi.myinnerpharmacy.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.adapter.PauseTaskAdapter;
import com.mxi.myinnerpharmacy.model.AdvanceWellnessDevelopmentRecord;
import com.mxi.myinnerpharmacy.model.Pausetask;
import com.mxi.myinnerpharmacy.model.StateChartData;
import com.mxi.myinnerpharmacy.network.AppController;
import com.mxi.myinnerpharmacy.network.CommanClass;
import com.mxi.myinnerpharmacy.network.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    RecyclerView rv_statistics;
    CommanClass cc;
    ArrayList<Pausetask>arrayList;
    PauseTaskAdapter pauseTaskAdapter;
    LinearLayoutManager llm;
    Toolbar toolbar;


    ProgressDialog pDialog;

    ArrayList<StateChartData> stateChartWSList;
    ArrayList<StateChartData> heartRateChartWSList;
    ArrayList<StateChartData> breathRateChartWSList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        cc=new CommanClass(this);
        rv_statistics=(RecyclerView)findViewById(R.id.rv_statistics);
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

        final String TITLES[] = getResources().getStringArray(R.array.statistics_items);

        arrayList=new ArrayList<Pausetask>();
        for (int i = 0; i < TITLES.length; i++) {
            Pausetask pausetask=new Pausetask();
            pausetask.setTask_name(TITLES[i]);
            arrayList.add(pausetask);
        }
        llm=new LinearLayoutManager(this);
        pauseTaskAdapter=new PauseTaskAdapter(this,arrayList);
        rv_statistics.setLayoutManager(llm);
        rv_statistics.setAdapter(pauseTaskAdapter);

        rv_statistics.addOnItemTouchListener(new RecyclerTouchListener(this, rv_statistics, new PauseTask.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                cc.showToast(TITLES[position]);
//                Log.e("position",position+"");
//                Intent intent=new Intent(StatisticsActivity.this,GraphActivity.class);
//                intent.putExtra("from",TITLES[position]);
//                startActivity(intent);

                if (position==0){

                    stateCalibrationChartWS();

                }else  if (position==1){



                    heartRateMonitorChartWS();

                }else  if (position==2){

                    Intent intent=new Intent(StatisticsActivity.this,GraphActivity.class);
                    intent.putExtra("from","advance_chart");
                    startActivity(intent);


                }else  if (position==3){

                    Intent intent=new Intent(StatisticsActivity.this,MyDeads.class);
                    cc.savePrefString("mydead_back","mydead");
                    startActivity(intent);

                }else  if (position==4){

                   /* Intent intent=new Intent(StatisticsActivity.this,BreathAnalysis.class);
                    startActivity(intent);*/

                    breathingRateChartWS();

                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void breathingRateChartWS() {

        pDialog = new ProgressDialog(StatisticsActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_breath_rate_chart,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("Url_breath_rate_chart", response);

                        breathRateChartWSList = new ArrayList<>();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonArray = jsonObject.optJSONArray("data_array");

                            for(int i=0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                StateChartData sd = new StateChartData();
                                sd.setDateChart(jsonObject1.getString("date"));
                                sd.setState_calibration(jsonObject1.getString("data"));
                                breathRateChartWSList.add(sd);

                            }


                            Intent intent = new Intent(StatisticsActivity.this, GraphActivity.class);
                            intent.putExtra("from","Breath Rate Monitor");
                            intent.putExtra("breathListChart", breathRateChartWSList);
                            startActivity(intent);

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

    private void heartRateMonitorChartWS() {

        pDialog = new ProgressDialog(StatisticsActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_heart_rate_chart,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("Url_heart_rate_chart", response);

                        heartRateChartWSList = new ArrayList<>();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonArray = jsonObject.optJSONArray("data_array");

                            for(int i=0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                StateChartData sd = new StateChartData();
                                sd.setDateChart(jsonObject1.getString("date"));
                                sd.setState_calibration(jsonObject1.getString("data"));
                                heartRateChartWSList.add(sd);

                            }


                            Intent intent = new Intent(StatisticsActivity.this, GraphActivity.class);
                            intent.putExtra("from","Heart Rate Monitor");
                            intent.putExtra("heartListChart", heartRateChartWSList);
                            startActivity(intent);

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

    private void stateCalibrationChartWS() {

        pDialog = new ProgressDialog(StatisticsActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_state_calibration_chart,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("Url_state_cali_chart", response);

                        stateChartWSList = new ArrayList<>();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonArray = jsonObject.optJSONArray("data_array");

                            for(int i=0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                StateChartData sd = new StateChartData();
                                sd.setDateChart(jsonObject1.getString("date"));
                                sd.setState_calibration(jsonObject1.getString("data"));
                                stateChartWSList.add(sd);

                            }


                            Intent intent = new Intent(StatisticsActivity.this, GraphActivity.class);
                            intent.putExtra("from","State Calibration");
                            intent.putExtra("stateListChart", stateChartWSList);
                            startActivity(intent);




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

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private PauseTask.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final PauseTask.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
