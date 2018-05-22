package com.mxi.myinnerpharmacy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mxi.myinnerpharmacy.R;

public class MyDeadsOptionActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    TextView tv_4, tv_3, tv_2, tv_1,tv_5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_deads_option);

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

        tv_4 = (TextView) findViewById(R.id.tv_4);
        tv_5 = (TextView) findViewById(R.id.tv_5);
        tv_3 = (TextView) findViewById(R.id.tv_3);
        tv_2 = (TextView) findViewById(R.id.tv_2);
        tv_1 = (TextView) findViewById(R.id.tv_1);

        tv_1.setOnClickListener(this);
        tv_5.setOnClickListener(this);
        tv_2.setOnClickListener(this);
        tv_3.setOnClickListener(this);
        tv_4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        TextView tv = (TextView) v;
        Intent intent = new Intent(MyDeadsOptionActivity.this, AddResourceJournal.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from", "MyDeads");
        intent.putExtra("MyDead_cat", tv.getText().toString());
        startActivity(intent);

    }
}
