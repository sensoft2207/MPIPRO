package com.mxi.myinnerpharmacy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.network.CommanClass;

public class BreathAnalysis extends AppCompatActivity {

    Toolbar toolbar;

    TextView tv_skip,tv_next;

    CommanClass cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_breathing);

        cc = new CommanClass(this);

        tv_skip = (TextView) findViewById(R.id.tv_skip);
        tv_next = (TextView) findViewById(R.id.tv_1);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (cc.loadPrefString("Skip").equals("skip")){

            tv_skip.setVisibility(View.INVISIBLE);

            cc.savePrefString("Skip","");

        }else {
            tv_skip.setVisibility(View.VISIBLE);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(BreathAnalysis.this, HeartRate.class));
                cc.savePrefString("display_skip","skipp");
                cc.savePrefString("prevent_final_wscall","pfw");
            }
        });

        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(BreathAnalysis.this, BreathTimerActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
