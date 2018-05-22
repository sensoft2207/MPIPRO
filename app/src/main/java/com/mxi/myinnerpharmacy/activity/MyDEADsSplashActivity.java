package com.mxi.myinnerpharmacy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.network.CommanClass;

public class MyDEADsSplashActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView tv_watch, tv_skip;
    CommanClass cc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_deads_splash);
        cc=new CommanClass(this);

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

        tv_watch = (TextView) findViewById(R.id.tv_watch);
        tv_skip = (TextView) findViewById(R.id.tv_skip);

        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyDEADsSplashActivity.this, MyDeads.class));
                cc.savePrefString("talk_ac","talk");

            }
        });

        tv_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cc.showToast("Video is coming soon");
            }
        });
    }
}
