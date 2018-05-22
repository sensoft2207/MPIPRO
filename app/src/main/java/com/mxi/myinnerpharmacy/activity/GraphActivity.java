package com.mxi.myinnerpharmacy.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.database.SQLiteTD;
import com.mxi.myinnerpharmacy.model.AdvanceWellnessDevelopmentRecord;
import com.mxi.myinnerpharmacy.model.StateChartData;
import com.mxi.myinnerpharmacy.model.heartrate;
import com.mxi.myinnerpharmacy.network.AppController;
import com.mxi.myinnerpharmacy.network.CommanClass;
import com.mxi.myinnerpharmacy.network.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphActivity extends ActionBarActivity {

    CommanClass cc;

    List<AdvanceWellnessDevelopmentRecord> recordsList;
    List<heartrate> recordsList_heart;
    List<heartrate> recordsList_state_cali;
    TextView tv_footer;
    String hours, sugary, play, exercise, stillness, tv, computer, smart_phone, portion, portions;
    String from;
    SQLiteTD db;
    LinearLayout ll_footer;

    ArrayList<StateChartData> stateChartWSList;
    ArrayList<StateChartData> heartRateChartWSList;
    ArrayList<StateChartData> breathRateChartWSList;
    ArrayList<StateChartData> advanceWellnessChartWSList;

    Dialog dialog;
    String[] chartType;
    String selectedType;
    ProgressDialog pDialog;
    FloatingActionButton fab_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        db = new SQLiteTD(this);

        cc = new CommanClass(this);

        ll_footer = (LinearLayout) findViewById(R.id.ll_footer);
        tv_footer= (TextView) findViewById(R.id.tv_footer);
        fab_search = (FloatingActionButton)findViewById(R.id.fab_search);

        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                advanceWelnessDialog();
            }
        });

        from = getIntent().getStringExtra("from");

        if (from.equals("Advanced Wellness Development")) {
            ll_footer.setVisibility(View.GONE);
            recordsList = AdvanceWellnessDevelopmentRecord.listAll(AdvanceWellnessDevelopmentRecord.class);

//                getDataFromIntent();

            BarChart chart = (BarChart) findViewById(R.id.chart);

            BarData data = new BarData(getXAxisValues(), getDataSet());
            chart.setData(data);
            chart.setDescription("Advance Wellness Development Chart");
            chart.animateXY(2000, 2000);
            chart.invalidate();
        }else if (from.equals("advance_chart")) {

            fab_search.setVisibility(View.VISIBLE);

            advanceWelnessDialog();

        } else if (from.equals("adwd")) {
            ll_footer.setVisibility(View.GONE);
            recordsList = AdvanceWellnessDevelopmentRecord.listAll(AdvanceWellnessDevelopmentRecord.class);


            getDataFromIntent();

            BarChart chart = (BarChart) findViewById(R.id.chart);

            BarData data = new BarData(getXAxisValues(), getDataSet());
            chart.setData(data);
            chart.setDescription("Advance Wellness Development Chart");
            chart.animateXY(2000, 2000);
            chart.invalidate();


        } else if (from.equals("Heart Rate Monitor")) {

            String string_list = "(Low) <60, 60-90 (Normal), 90> (High)";
            ll_footer.setVisibility(View.VISIBLE);
            tv_footer.setText(string_list);

            heartRateChartWSList = new ArrayList<StateChartData>();
            heartRateChartWSList = (ArrayList<StateChartData>) getIntent().getSerializableExtra("heartListChart");

            recordsList_heart = new ArrayList<heartrate>();

            for (int i = 0; i <heartRateChartWSList.size() ; i++) {


                StateChartData sc = heartRateChartWSList.get(i);

                String date = sc.getDateChart();
                String data = sc.getState_calibration();

                heartrate rt = new heartrate();
                rt.setDate(date);
                rt.setHeart_rate(data);
                recordsList_heart.add(rt);

                Log.e("stateChartDate",sc.getDateChart());
                Log.e("stateChartData",sc.getState_calibration());

            }


            BarChart chart = (BarChart) findViewById(R.id.chart);

            BarData data = new BarData(getXAxisValues_Heart(), getDataSet_Heart());
            chart.setData(data);
            chart.setDescription("Heart Rate Chart");
            chart.animateXY(2000, 2000);
            chart.invalidate();

        } else if (from.equals("State Calibration")) {

            ll_footer.setVisibility(View.VISIBLE);


            stateChartWSList = new ArrayList<StateChartData>();
            stateChartWSList = (ArrayList<StateChartData>) getIntent().getSerializableExtra("stateListChart");

            recordsList_state_cali = new ArrayList<heartrate>();


            for (int i = 0; i <stateChartWSList.size() ; i++) {


                StateChartData sc = stateChartWSList.get(i);

                String date = sc.getDateChart();
                String data = sc.getState_calibration();

                heartrate rt = new heartrate();
                rt.setDate(date);
                rt.setHeart_rate(data);
                recordsList_state_cali.add(rt);

                Log.e("stateChartDate",sc.getDateChart());
                Log.e("stateChartData",sc.getState_calibration());

            }


            Log.e("@@LENGTH", recordsList_state_cali.size() + "");


            BarChart chart = (BarChart) findViewById(R.id.chart);

            BarData data = new BarData(getXAxisValues_State(), getDataSet_StateCali());

            chart.setData(data);
            chart.setDescription("State Calibration Chart");
            chart.animateXY(2000, 2000);
            chart.invalidate();

        }else if (from.equals("Breath Rate Monitor")) {


            breathRateChartWSList = new ArrayList<StateChartData>();
            breathRateChartWSList = (ArrayList<StateChartData>) getIntent().getSerializableExtra("breathListChart");

            recordsList_state_cali = new ArrayList<heartrate>();


            for (int i = 0; i <breathRateChartWSList.size() ; i++) {


                StateChartData sc = breathRateChartWSList.get(i);

                String date = sc.getDateChart();
                String data = sc.getState_calibration();

                heartrate rt = new heartrate();
                rt.setDate(date);
                rt.setHeart_rate(data);
                recordsList_state_cali.add(rt);

                Log.e("breathChartDate",sc.getDateChart());
                Log.e("breathChartData",sc.getState_calibration());

            }


            Log.e("@@LENGTH", recordsList_state_cali.size() + "");


            BarChart chart = (BarChart) findViewById(R.id.chart);

            BarData data = new BarData(getXAxisValues_Breath(), getDataSet_Breathrate());

            chart.setData(data);
            chart.setDescription("Breathing Rate Chart");
            chart.animateXY(2000, 2000);
            chart.invalidate();

        }
    }

    private ArrayList<String> getXAxisValues_Heart() {

        ArrayList<String> xAxis = new ArrayList<>();

        for (int i = 0; i < recordsList_heart.size(); i++) {

            heartrate rt = recordsList_heart.get(i);

            String date = rt.getDate();

            xAxis.add(date);
        }
        return xAxis;
    }

    private ArrayList<String> getXAxisValues_State() {

        ArrayList<String> xAxis = new ArrayList<>();

        for (int i = 0; i < recordsList_state_cali.size(); i++) {

            heartrate rt = recordsList_state_cali.get(i);

            String date = rt.getDate();

            xAxis.add(date);
        }


        return xAxis;
    }

    private ArrayList<String> getXAxisValues_Breath() {

        ArrayList<String> xAxis = new ArrayList<>();

        for (int i = 0; i < recordsList_state_cali.size(); i++) {

            heartrate rt = recordsList_state_cali.get(i);

            String date = rt.getDate();

            xAxis.add(date);
        }


        return xAxis;
    }

    private ArrayList<BarDataSet> getDataSet_Heart() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();


        Log.e("LENGTH", recordsList_heart.size() + "");
        for (int i = 0; i < recordsList_heart.size(); i++) {

            heartrate record = recordsList_heart.get(i);


//        BarEntry v1e1 = new BarEntry(Float.parseFloat(hours), i); // Jan
            BarEntry v1e1 = new BarEntry(Float.parseFloat(record.getHeart_rate()), i); // Jan
//      Set for loop here
            valueSet1.add(v1e1);

        }
//hours,sugary,play,exercise,stillness,tv,computer,smart_phone,portion,portions
        BarDataSet barDataSet1;
        if (from.equals("State Calibration")) {
            barDataSet1 = new BarDataSet(valueSet1, "State Calibration");
            barDataSet1.setColor(Color.rgb(255, 150, 255));
        } else {
            barDataSet1 = new BarDataSet(valueSet1, "Heart Rate");
            barDataSet1.setColor(Color.rgb(102, 102, 255));
        }


        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);

        return dataSets;
    }

    private ArrayList<BarDataSet> getDataSet_StateCali() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();


        Log.e("LENGTH", recordsList_state_cali.size() + "");
        for (int i = 0; i < recordsList_state_cali.size(); i++) {

            heartrate record = recordsList_state_cali.get(i);


            BarEntry v1e1 = new BarEntry(Float.parseFloat(record.getHeart_rate()), i); // Jan

            valueSet1.add(v1e1);

        }

        BarDataSet barDataSet1;
        if (from.equals("State Calibration")) {
            barDataSet1 = new BarDataSet(valueSet1, "State Calibration");
            barDataSet1.setColor(Color.rgb(255, 150, 255));
        } else {
            barDataSet1 = new BarDataSet(valueSet1, "Heart Rate");
            barDataSet1.setColor(Color.rgb(102, 102, 255));
        }


        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);

        return dataSets;
    }

    private ArrayList<BarDataSet> getDataSet_Breathrate() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();


        Log.e("LENGTH", recordsList_state_cali.size() + "");
        for (int i = 0; i < recordsList_state_cali.size(); i++) {

            heartrate record = recordsList_state_cali.get(i);


            BarEntry v1e1 = new BarEntry(Float.parseFloat(record.getHeart_rate()), i); // Jan

            valueSet1.add(v1e1);

        }

        BarDataSet barDataSet1;
        if (from.equals("Breath Rate Monitor")) {
            barDataSet1 = new BarDataSet(valueSet1, "Breathing Rate Chart");
            barDataSet1.setColor(Color.rgb(255, 150, 255));
        } else {
            barDataSet1 = new BarDataSet(valueSet1, "Heart Rate");
            barDataSet1.setColor(Color.rgb(102, 102, 255));
        }


        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);

        return dataSets;
    }



    private void getDataFromIntent() {
        Bundle bundle = getIntent().getBundleExtra("bundle");
        hours = bundle.getString("hours");
        sugary = bundle.getString("sugary");
        play = bundle.getString("play");
        exercise = bundle.getString("exercise");
        stillness = bundle.getString("stillness");
        tv = bundle.getString("tv");
        computer = bundle.getString("computer");
        smart_phone = bundle.getString("smart_phone");
        portion = bundle.getString("portion");
        portions = bundle.getString("portions");
    }

    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        ArrayList<BarEntry> valueSet3 = new ArrayList<>();
        ArrayList<BarEntry> valueSet4 = new ArrayList<>();
        ArrayList<BarEntry> valueSet5 = new ArrayList<>();
        ArrayList<BarEntry> valueSet6 = new ArrayList<>();
        ArrayList<BarEntry> valueSet7 = new ArrayList<>();
        ArrayList<BarEntry> valueSet8 = new ArrayList<>();


        Log.e("LENGTH", recordsList.size() + "");
        for (int i = 0; i < recordsList.size(); i++) {

            AdvanceWellnessDevelopmentRecord record = recordsList.get(i);

            Log.e("FIRSTDATA",record.getHours());

//        BarEntry v1e1 = new BarEntry(Float.parseFloat(hours), i); // Jan
            BarEntry v1e1 = new BarEntry(Float.parseFloat(record.getHours()), i); // Jan
//      Set for loop here
            valueSet1.add(v1e1);


            BarEntry v2e1 = new BarEntry(Float.parseFloat(record.getSugary()), i); // Jan
//      Set for loop here
            valueSet2.add(v2e1);


            BarEntry v3e1 = new BarEntry(Float.parseFloat(record.getPlay()), i); // Jan
//      Set for loop here
            valueSet3.add(v3e1);


            BarEntry v4e1 = new BarEntry(Float.parseFloat(record.getExercise()), i); // Jan
//      Set for loop here
            valueSet4.add(v4e1);


            BarEntry v5e1 = new BarEntry(Float.parseFloat(record.getStillness()), i); // Jan
//      Set for loop here
            valueSet5.add(v5e1);


            BarEntry v6e1 = new BarEntry(Float.parseFloat(record.getTv()), i); // Jan
//      Set for loop here
            valueSet6.add(v6e1);


            BarEntry v7e1 = new BarEntry(Float.parseFloat(record.getComputer()), i); // Jan
//      Set for loop here
            valueSet7.add(v7e1);


            BarEntry v8e1 = new BarEntry(Float.parseFloat(record.getSmart_phone()), i); // Jan
//      Set for loop here
            valueSet8.add(v8e1);

        }
//hours,sugary,play,exercise,stillness,tv,computer,smart_phone,portion,portions

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Sleep");
        barDataSet1.setColor(Color.rgb(102, 255, 0));
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Sugary Drinks");
        barDataSet2.setColor(Color.rgb(255, 55, 51));
        BarDataSet barDataSet3 = new BarDataSet(valueSet3, "Play");
        barDataSet3.setColor(Color.rgb(155, 155, 0));
        BarDataSet barDataSet4 = new BarDataSet(valueSet4, "Exercise");
        barDataSet4.setColor(Color.rgb(204, 102, 0));
        BarDataSet barDataSet5 = new BarDataSet(valueSet5, "Stillness");
        barDataSet5.setColor(Color.rgb(51, 255, 153));
        BarDataSet barDataSet6 = new BarDataSet(valueSet6, "Television");
        barDataSet6.setColor(Color.rgb(102, 102, 255));
        BarDataSet barDataSet7 = new BarDataSet(valueSet7, "Computer");
        barDataSet7.setColor(Color.rgb(255, 150, 255));
        BarDataSet barDataSet8 = new BarDataSet(valueSet8, "Smart Phone");
        barDataSet8.setColor(Color.rgb(0, 160, 150));

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        dataSets.add(barDataSet3);
        dataSets.add(barDataSet4);
        dataSets.add(barDataSet5);
        dataSets.add(barDataSet6);
        dataSets.add(barDataSet7);
        dataSets.add(barDataSet8);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();

        for (int i = 0; i < recordsList.size(); i++) {
            xAxis.add(recordsList.get(i).getDate());
        }
        return xAxis;
    }

    private void advanceWelnessDialog() {

        dialog = new Dialog(GraphActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.advalce_wel_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout((6 * width) / 7, ActionBar.LayoutParams.WRAP_CONTENT);


        LinearLayout ln_submit = (LinearLayout)dialog.findViewById(R.id.ln_submit);
        LinearLayout ln_cancle = (LinearLayout)dialog.findViewById(R.id.ln_cancle);
        Spinner sp_chart_type = (Spinner)dialog.findViewById(R.id.sp_chart_type);

        chartType = new String[]{"Sleep Hours","Sugar Drink","Play","Exercise","Stillnes","Tv Watch","Computer","SmartPhone"};

        ArrayAdapter<String> chartTypeArray = new ArrayAdapter<String>(GraphActivity.this,android.R.layout.simple_spinner_item,chartType);
        chartTypeArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_chart_type.setAdapter(chartTypeArray);

        sp_chart_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                selectedType = (String) parent.getItemAtPosition(pos);

                Log.e("SpinnerValueSelected",selectedType);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ln_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getAdvanceWelnessChart(selectedType);

            }
        });

        ln_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });


        dialog.show();

    }

    private void getAdvanceWelnessChart(final String selectedType) {

        pDialog = new ProgressDialog(GraphActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_advance_welness_chart,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("Url_advance_wel_chart", response);

                        advanceWellnessChartWSList = new ArrayList<>();

                        recordsList_state_cali = new ArrayList<heartrate>();

                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonArray = jsonObject.optJSONArray("data_array");

                            for(int i=0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                StateChartData sd = new StateChartData();
                                sd.setDateChart(jsonObject1.getString("date"));
                                sd.setState_calibration(jsonObject1.getString("data"));
                                advanceWellnessChartWSList.add(sd);
                            }

                            ll_footer.setVisibility(View.VISIBLE);
                            tv_footer.setText(selectedType);

                            for (int i = 0; i <advanceWellnessChartWSList.size() ; i++) {


                                StateChartData sc = advanceWellnessChartWSList.get(i);

                                String date = sc.getDateChart();
                                String data = sc.getState_calibration();

                                heartrate rt = new heartrate();
                                rt.setDate(date);
                                rt.setHeart_rate(data);
                                recordsList_state_cali.add(rt);

                                Log.e("adChartDate",sc.getDateChart());
                                Log.e("adChartData",sc.getState_calibration());

                            }


                            BarChart chart = (BarChart) findViewById(R.id.chart);

                            BarData data = new BarData(getXAxisValues_advance(), getDataSet_advanceWelness());

                            chart.setData(data);
                            chart.setDescription("Advance Welness Chart");
                            chart.animateXY(2000, 2000);
                            chart.invalidate();

                            dialog.dismiss();


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


                if (selectedType.equals("Sleep Hours")){

                    params.put("type","sleep_hours");

                }else if (selectedType.equals("Sugar Drink")){

                    params.put("type","sugar_drink");

                }else if (selectedType.equals("Play")){

                    params.put("type","play");

                }else if (selectedType.equals("Exercise")){

                    params.put("type","exercise");

                }else if (selectedType.equals("Stillnes")){

                    params.put("type","stillnes");

                }else if (selectedType.equals("Tv Watch")){

                    params.put("type","tv_watch");

                }else if (selectedType.equals("Computer")){

                    params.put("type","computer");

                }else if (selectedType.equals("SmartPhone")){

                    params.put("type","smart_phone");
                }else {

                    params.put("type","sleep_hours");
                }


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

    private ArrayList<String> getXAxisValues_advance() {

        ArrayList<String> xAxis = new ArrayList<>();

        for (int i = 0; i < recordsList_state_cali.size(); i++) {

            heartrate rt = recordsList_state_cali.get(i);

            String date = rt.getDate();

            xAxis.add(date);
        }


        return xAxis;
    }

    private ArrayList<BarDataSet> getDataSet_advanceWelness() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();


        Log.e("LENGTH", recordsList_state_cali.size() + "");
        for (int i = 0; i < recordsList_state_cali.size(); i++) {

            heartrate record = recordsList_state_cali.get(i);


            BarEntry v1e1 = new BarEntry(Float.parseFloat(record.getHeart_rate()), i); // Jan

            valueSet1.add(v1e1);

        }

        BarDataSet barDataSet1;
        barDataSet1 = new BarDataSet(valueSet1, "Advance Wellness Chart");
        barDataSet1.setColor(Color.rgb(255, 150, 255));


        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);

        return dataSets;
    }

}
