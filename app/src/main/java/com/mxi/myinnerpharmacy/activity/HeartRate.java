package com.mxi.myinnerpharmacy.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.heartrate.HeartRateMonitor;
import com.mxi.myinnerpharmacy.network.CommanClass;

public class HeartRate extends AppCompatActivity implements View.OnTouchListener {

    TextView tv_next, tv_skip, tv_heart_name, tv_heart_rate_text, tv_heart_rate_history;
    Toolbar toolbar;

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;

    CommanClass cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);

        cc = new CommanClass(this);

        tv_next = (TextView) findViewById(R.id.tv_next);
        tv_skip = (TextView) findViewById(R.id.tv_skip);
        tv_heart_name = (TextView) findViewById(R.id.tv_heart_name);
        tv_heart_rate_text = (TextView) findViewById(R.id.tv_heart_rate_text);
        tv_heart_rate_history = (TextView) findViewById(R.id.tv_heart_rate_history);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!MainActivity.isSkip) {
            tv_skip.setVisibility(View.GONE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cc.loadPrefString("new_prescription_state").equals("nps")){

                        startActivity(new Intent(HeartRate.this, PrescriptionStep.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();

                        cc.savePrefString("new_prescription_state","");

                    }else {

                        startActivity(new Intent(HeartRate.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                    // startActivity(new Intent(HeartRate.this, ));
                }
            });

        } else {
            tv_skip.setVisibility(View.VISIBLE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);

        }
        String skip = "<u>" + getString(R.string.skip) + "</u>";
        tv_skip.setText(Html.fromHtml(skip));

        String heart_name = "<u>" + getString(R.string.heart_process) + "</u>";
        tv_heart_name.setText(Html.fromHtml(heart_name));

        String heart_rate_text = "<u>" + "Heart Rate History" + "</u>";
        tv_heart_rate_history.setText(Html.fromHtml(heart_rate_text));

        tv_next.setOnTouchListener(this);
        tv_skip.setOnTouchListener(this);
        tv_heart_rate_history.setOnTouchListener(this);

        if (cc.loadPrefString("display_skip").equals("skipp")){

            tv_skip.setVisibility(View.VISIBLE);

            tv_skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(new Intent(HeartRate.this, StateCalibration.class));
                    cc.savePrefString("new_prescription_state2","nps");
                    cc.savePrefString("prevent_final_wscall","pfw");
                }
            });

            cc.savePrefString("display_skip","");

        }else {

            tv_skip.setVisibility(View.GONE);

            tv_skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(new Intent(HeartRate.this, MainActivity.class));
                }
            });
        }


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (v.getId()) {
            case R.id.tv_next:

                startActivity(new Intent(HeartRate.this, HeartRateMonitor.class));
                finish();

//

                break;
           /* case R.id.tv_skip:

                if (!MainActivity.isSkip) {
                    startActivity(new Intent(HeartRate.this, MainActivity.class));
                    finish();

                } else {
                    startActivity(new Intent(HeartRate.this, PrescriptionDetail.class));
                    finish();
                }

                break;*/
            case R.id.tv_heart_rate_history:
                startActivity(new Intent(HeartRate.this, HeartRateHistory.class));
                break;
        }
        return false;
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void checkCameraPermission() {

        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
            return;
        } else {
            startActivity(new Intent(HeartRate.this, HeartRateMonitor.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startActivity(new Intent(HeartRate.this, HeartRateMonitor.class));
                    finish();

                } else {

                    checkCameraPermission();
                }
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {

       /* if (!MainActivity.isSkip) {

            Intent launchNextActivity;
            launchNextActivity = new Intent(new Intent(HeartRate.this, MainActivity.class));
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(launchNextActivity);

        } else {

        }*/
        finish();
        super.onBackPressed();


    }

}
