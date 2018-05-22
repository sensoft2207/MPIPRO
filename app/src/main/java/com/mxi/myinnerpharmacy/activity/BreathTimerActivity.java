package com.mxi.myinnerpharmacy.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mxi on 1/2/18.
 */

public class BreathTimerActivity extends AppCompatActivity {

    CommanClass cc;

    ImageView iv_start_breath;
    TextView tv_timer,tv_pause,tv_ratedone;

    LinearLayout ln_pause_btn,ln_done_btn;

    Toolbar toolbar;

    private long startTime = 0L;
    private Handler myHandler = new Handler();
    long timeInMillies = 0L;
    long timeSwap = 0L;
    long finalTime = 0L;


    boolean isStartTimer = false;
    boolean isStartTimerFirstTime = false;

    ProgressDialog pDialog;

    Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.breath_timer);

        cc = new CommanClass(this);

        init();

    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        iv_start_breath = (ImageView)findViewById(R.id.iv_start_breath);
        tv_timer = (TextView) findViewById(R.id.tv_timer);
        tv_pause = (TextView) findViewById(R.id.tv_pause);
        tv_ratedone = (TextView) findViewById(R.id.tv_ratedone);

        ln_pause_btn = (LinearLayout)findViewById(R.id.ln_pause_btn);
        ln_done_btn = (LinearLayout)findViewById(R.id.ln_done_btn);

        ln_pause_btn.setVisibility(View.INVISIBLE);
        ln_done_btn.setVisibility(View.INVISIBLE);

        clickListner();
    }

    private void clickListner() {

        iv_start_breath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isStartTimerFirstTime == false){

                    startTime = SystemClock.uptimeMillis();
                    myHandler.postDelayed(updateTimerMethod, 0);
                    Log.e("TimerStart","........");

                    isStartTimerFirstTime = true;

                    ln_pause_btn.setVisibility(View.VISIBLE);
                    ln_done_btn.setVisibility(View.VISIBLE);

                }


            }
        });

        tv_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isStartTimerFirstTime == true){

                    if (isStartTimer == false){

                        timeSwap += timeInMillies;
                        myHandler.removeCallbacks(updateTimerMethod);

                        isStartTimer = true;

                        tv_pause.setText("Play");

                    }else {

                        startTime = SystemClock.uptimeMillis();
                        myHandler.postDelayed(updateTimerMethod, 0);

                        isStartTimer = false;

                        tv_pause.setText("Pause");
                    }

                }

            }
        });

        tv_ratedone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isStartTimerFirstTime == true){

                    doneDialog();

                }

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private Runnable updateTimerMethod = new Runnable() {

        public void run() {
            timeInMillies = SystemClock.uptimeMillis() - startTime;
            finalTime = timeSwap + timeInMillies;

            int seconds = (int) (finalTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliseconds = (int) (finalTime % 1000);
            tv_timer.setText("" + minutes + ":"
                    + String.format("%02d", seconds));

            tv_timer.setTextSize(40);

            myHandler.postDelayed(this, 0);
        }

    };

    private void doneDialog() {

        dialog = new Dialog(BreathTimerActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.breathing_timer_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout((6 * width) / 7, ActionBar.LayoutParams.WRAP_CONTENT);


        LinearLayout ln_done = (LinearLayout) dialog.findViewById(R.id.ln_done);
        LinearLayout ln_continue = (LinearLayout) dialog.findViewById(R.id.ln_continue);


        timeSwap += timeInMillies;
        myHandler.removeCallbacks(updateTimerMethod);

        ln_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cc.isConnectingToInternet()) {

                    String time = tv_timer.getText().toString(); //mm:ss
                    String[] units = time.split(":"); //will break the string up into an array
                    int minutes = Integer.parseInt(units[0]); //first element
                    int seconds = Integer.parseInt(units[1]); //second element
                    int duration = 60 * minutes + seconds / 5;


                    Log.e("BREATHTIMESECOND", String.valueOf(duration));

                    cc.savePrefString("breath_data",String.valueOf(duration));

                    postJsonBreathTimerValue(String.valueOf(duration));
                } else {
                    cc.showToast(getString(R.string.no_internet));
                }

            }
        });

        ln_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startTime = SystemClock.uptimeMillis();
                myHandler.postDelayed(updateTimerMethod, 0);

                dialog.dismiss();
            }
        });



        dialog.show();

    }

    private void postJsonBreathTimerValue(final String valueTimer) {

        pDialog = new ProgressDialog(BreathTimerActivity.this);
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

//                                cc.showToast(jsonObject.getString("message"));

                                if (cc.loadPrefString("p_steps").equals("psteps")){

                                    if (cc.loadPrefString("to_main_activity").equals("tma")){

                                        startActivity(new Intent(BreathTimerActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        cc.savePrefString("p_steps","");
                                        cc.savePrefString("to_main_activity","");
                                    }else {

                                        startActivity(new Intent(BreathTimerActivity.this, HeartRate.class));
//                                        cc.savePrefString("p_steps","");
                                        cc.savePrefString("display_skip","skipp");
                                    }

                                }else {
                                    finish();
                                }


                                Log.e("TIMERVALUE",tv_timer.getText().toString());
                                dialog.dismiss();

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
                params.put("activity_name","BRITHING");
                params.put("value", valueTimer);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cc.savePrefString("p_steps","psteps");
    }
}
