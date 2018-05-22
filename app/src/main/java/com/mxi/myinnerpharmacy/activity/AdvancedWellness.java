package com.mxi.myinnerpharmacy.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.model.AdvanceWellnessDevelopmentRecord;
import com.mxi.myinnerpharmacy.network.AppController;
import com.mxi.myinnerpharmacy.network.CommanClass;
import com.mxi.myinnerpharmacy.network.URL;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdvancedWellness extends AppCompatActivity implements View.OnTouchListener {

    CommanClass cc;
    LinearLayout ll_advance_wellness;
    TextView tv_hours, tv_sugary, tv_play, tv_exercise, tv_stillness, tv_tv, tv_computer, tv_smart_phone, tv_portion, tv_portions, tv_date, tv_wellness_done;

    Toolbar toolbar;
    final String[] sleephours = {"0","0.5","1","1.5", "2", "2.5", "3", "3.5", "4", "4.5", "5", "5.5", "6", "6.5", "7", "7.5", "8", "8.5","9","9.5","10"};
    final String[] sugary = {"0","1", "2", "3", "4", "5", "6", "7", "8","9","10"};
    final String[] play = {"0", "0.5", "1", "1.5", "2", "2.5", "3", "3.5","4","4.5","5","5.5","6","6.5","7","7.5","8","8.5","9","9.5","10"};
    final String[] exercise = {"0", "0.5", "1", "1.5", "2", "2.5", "3", "3.5","4","4.5","5","5.5","6","6.5","7","7.5","8","8.5","9","9.5","10"};
    final String[] stillness = {"0", "0.5", "1", "1.5", "2", "2.5", "3", "3.5","4","4.5","5","5.5","6","6.5","7","7.5","8","8.5","9","9.5","10"};
    final String[] tv = {"0", "0.5", "1", "1.5", "2", "2.5", "3", "3.5","4","4.5","5","5.5","6","6.5","7","7.5","8","8.5","9","9.5","10"};
    final String[] computer = {"0", "0.5", "1", "1.5", "2", "2.5", "3", "3.5","4","4.5","5","5.5","6","6.5","7","7.5","8","8.5","9","9.5","10"};
    final String[] smart_phone ={"0", "0.5", "1", "1.5", "2", "2.5", "3", "3.5","4","4.5","5","5.5","6","6.5","7","7.5","8","8.5","9","9.5","10"
            ,"11","11.5", "12","12.5", "13","13.5", "14","14.5", "15","15.5", "16","16.5", "17","17.5", "18","18.5","19","19.5","20"};
    final String[] portion = {"0", "0.5", "1", "1.5", "2", "2.5", "3", "3.5","4","4.5","5","5.5","6","6.5","7","7.5","8","8.5","9","9.5","10"};
    final String[] portions ={"0", "0.5", "1", "1.5", "2", "2.5", "3", "3.5","4","4.5","5","5.5","6","6.5","7","7.5","8","8.5","9","9.5","10"};

    String sleephoursTypeTitle = "Select Type: ";
    //String sugaryTitle = "Select Sugary: ";

    String date,playMinutes,exerciseMinutes,stillnessMinutes,sugaryDrinks,sleepHour,tvHour,computerHour,phoneHour,portionn,portionns;

    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_wellness);
        cc=new CommanClass(this);
        ll_advance_wellness=(LinearLayout)findViewById(R.id.ll_advance_wellness);
        tv_hours = (TextView) findViewById(R.id.tv_hours);
        tv_sugary = (TextView) findViewById(R.id.tv_sugary);
        tv_play = (TextView) findViewById(R.id.tv_play);
        tv_exercise = (TextView) findViewById(R.id.tv_exercise);
        tv_stillness = (TextView) findViewById(R.id.tv_stillness);
        tv_tv = (TextView) findViewById(R.id.tv_tv);
        tv_computer = (TextView) findViewById(R.id.tv_computer);
        tv_smart_phone = (TextView) findViewById(R.id.tv_smart_phone);
        tv_portion = (TextView) findViewById(R.id.tv_portion);
        tv_portions = (TextView) findViewById(R.id.tv_portions);

        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_wellness_done = (TextView) findViewById(R.id.tv_wellness_done);

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

        tv_hours.setOnTouchListener(this);
        tv_sugary.setOnTouchListener(this);
        tv_play.setOnTouchListener(this);
        tv_exercise.setOnTouchListener(this);
        tv_stillness.setOnTouchListener(this);
        tv_tv.setOnTouchListener(this);
        tv_computer.setOnTouchListener(this);
        tv_smart_phone.setOnTouchListener(this);
        tv_portion.setOnTouchListener(this);
        tv_portions.setOnTouchListener(this);

        tv_wellness_done.setOnTouchListener(this);

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());

        String texm_text = "<font color=#FFFFFF><u>" + getString(R.string.date) + " " + ":" + " " + formattedDate + "</u></font>";
        tv_date.setText(Html.fromHtml(texm_text));

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {

            case R.id.tv_hours:

                showPoupupMenu(getString(R.string.hours), sleephoursTypeTitle, sleephours);
                break;
            case R.id.tv_sugary:

                showPoupupMenu(getString(R.string.sugary_drunk), sleephoursTypeTitle, sugary);
                break;
            case R.id.tv_play:

                showPoupupMenu(getString(R.string.play), sleephoursTypeTitle, play);
                break;
            case R.id.tv_exercise:

                showPoupupMenu(getString(R.string.exercise), sleephoursTypeTitle, exercise);
                break;
            case R.id.tv_stillness:

                showPoupupMenu(getString(R.string.stillness), sleephoursTypeTitle, stillness);
                break;
            case R.id.tv_tv:

                showPoupupMenu(getString(R.string.tv), sleephoursTypeTitle, tv);
                break;
            case R.id.tv_computer:

                showPoupupMenu(getString(R.string.computer), sleephoursTypeTitle, computer);
                break;
            case R.id.tv_smart_phone:

                showPoupupMenu(getString(R.string.smart_phone), sleephoursTypeTitle, smart_phone);
                break;
            case R.id.tv_portion:

                showPoupupMenu(getString(R.string.portions), sleephoursTypeTitle, portion);
                break;
            case R.id.tv_portions:

                showPoupupMenu(getString(R.string.portions_of), sleephoursTypeTitle, portions);
                break;
            case R.id.tv_wellness_done:
/*
hours,sugary,play,exercise,stillness,tv,computer,smart_phone,portion,portions

                String hours = tv_hours.getText().toString().trim();
                String sugary = tv_sugary.getText().toString().trim();
                String play = tv_play.getText().toString().trim();
                String exercise = tv_exercise.getText().toString().trim();
                String stillness = tv_stillness.getText().toString().trim();
                String tv = tv_tv.getText().toString().trim();
                String computer = tv_computer.getText().toString().trim();
                String smart_phone = tv_smart_phone.getText().toString().trim();
                String portion = tv_portion.getText().toString().trim();
                String portions = tv_portions.getText().toString().trim();
*/

                if (!cc.isConnectingToInternet()) {
                    cc.showSnackbar(ll_advance_wellness, getString(R.string.no_internet));
                } else if (tv_hours.getText().toString().trim().equals("")) {
                    cc.showSnackbar(ll_advance_wellness, getString(R.string.enter_hours_sleep));
                } else if (tv_sugary.getText().toString().trim().equals("")) {
                    cc.showSnackbar(ll_advance_wellness, getString(R.string.enter_sugary_drinks));
                } else if (tv_play.getText().toString().trim().equals("")) {
                    cc.showSnackbar(ll_advance_wellness, getString(R.string.enter_play));
                } else if (tv_exercise.getText().toString().trim().equals("")) {
                    cc.showSnackbar(ll_advance_wellness, getString(R.string.enter_exercise));
                } else if ( tv_stillness.getText().toString().trim().equals("")) {
                    cc.showSnackbar(ll_advance_wellness, getString(R.string.enter_stillness));
                } else if (tv_tv.getText().toString().trim().equals("")) {
                    cc.showSnackbar(ll_advance_wellness, getString(R.string.enter_tv));
                } else if (tv_computer.getText().toString().trim().equals("")) {
                    cc.showSnackbar(ll_advance_wellness, getString(R.string.enter_computer));
                } else if (tv_smart_phone.getText().toString().trim().equals("")) {
                    cc.showSnackbar(ll_advance_wellness, getString(R.string.enter_phone));
                } else if ( tv_portion.getText().toString().trim().equals("")) {
                    cc.showSnackbar(ll_advance_wellness, getString(R.string.enter_fruits));
                } else if (tv_portions.getText().toString().trim().equals("")) {
                    cc.showSnackbar(ll_advance_wellness, getString(R.string.enter_veggies));
                } else {

//                    AdvanceWellnessDevelopmentRecord.deleteAll(AdvanceWellnessDevelopmentRecord.class);
                    Bundle bundle=new Bundle();

                    date = tv_date.getText().toString().trim();
                    sugaryDrinks=tv_sugary.getText().toString().trim();
                    sleepHour=tv_hours.getText().toString().trim();
                    playMinutes=convertMinToHour(tv_play.getText().toString().trim());
                    exerciseMinutes= convertMinToHour(tv_exercise.getText().toString().trim());
                    stillnessMinutes=convertMinToHour(tv_stillness.getText().toString().trim());
                    tvHour = tv_tv.getText().toString().trim();
                    computerHour=tv_computer.getText().toString().trim();
                    phoneHour=tv_smart_phone.getText().toString().trim();
                    portionn=tv_portion.getText().toString().trim();
                    portionns=tv_portions.getText().toString().trim();
                    /*if (tv_sugary.getText().toString().isEmpty()){

                        sugaryDrinks="0.0";

                    }else {
                        sugaryDrinks=tv_sugary.getText().toString().trim();
                    }

                    if (tv_hours.getText().toString().isEmpty()){

                        sleepHour="0.0";

                    }else {
                        sleepHour=tv_hours.getText().toString().trim();
                    }




                    if (tv_play.getText().toString().isEmpty()){

                         playMinutes="0.0";

                    }else {
                         playMinutes=convertMinToHour(tv_play.getText().toString().trim());
                    }

                    if (tv_exercise.getText().toString().isEmpty()){

                       exerciseMinutes= "0.0";

                    }else {
                        exerciseMinutes= convertMinToHour(tv_exercise.getText().toString().trim());
                    }

                    if (tv_stillness.getText().toString().isEmpty()){

                         stillnessMinutes= "0.0";

                    }else {
                         stillnessMinutes=convertMinToHour(tv_stillness.getText().toString().trim());
                    }

                    if (tv_tv.getText().toString().isEmpty()){

                        tvHour = "0.0";

                    }else {
                        tvHour = tv_tv.getText().toString().trim();
                    }

                    if (tv_computer.getText().toString().isEmpty()){

                        computerHour = "0.0";

                    }else {
                        computerHour=tv_computer.getText().toString().trim();
                    }

                    if (tv_smart_phone.getText().toString().isEmpty()){

                        phoneHour = "0.0";

                    }else {
                        phoneHour=tv_smart_phone.getText().toString().trim();
                    }

                    if (tv_portion.getText().toString().isEmpty()){

                        portionn = "0.0";

                    }else {
                        portionn=tv_portion.getText().toString().trim();
                    }

                    if (tv_portions.getText().toString().isEmpty()){

                        portionns = "0.0";

                    }else {
                        portionns=tv_portions.getText().toString().trim();
                    }*/


                    if (!cc.isConnectingToInternet()) {
                        cc.showToast(getString(R.string.no_internet));
                    } else {

                        postAWDJsonData(sleepHour,sugaryDrinks,playMinutes,exerciseMinutes,stillnessMinutes,tvHour,
                                computerHour,phoneHour,portionn,portionns);

                    }



                    /*AdvanceWellnessDevelopmentRecord record= new AdvanceWellnessDevelopmentRecord();
                    record.setDate(date);
                    record.setHours(sleepHour);
                    record.setSugary(sugaryDrinks);
                    record.setPlay(playMinutes);
                    record.setExercise(exerciseMinutes);
                    record.setStillness(stillnessMinutes);
                    record.setTv(tvHour);
                    record.setComputer(computerHour);
                    record.setSmart_phone(phoneHour);
                    record.setPortion(portionn);
                    record.setPortions(portionns);
                    record.save();

                    bundle.putString("hours",sleepHour);
                    bundle.putString("sugary",sugaryDrinks);
                    bundle.putString("play",playMinutes);
                    bundle.putString("exercise" ,exerciseMinutes);
                    bundle.putString("stillness", stillnessMinutes);
                    bundle.putString("tv",tvHour);
                    bundle.putString("computer" ,computerHour);
                    bundle.putString("smart_phone" ,phoneHour);
                    bundle.putString("portion" ,portionn);
                    bundle.putString("portions", portionns);

                    Log.e("WELLNESS",date);
                    Log.e("WELLNESS",sugaryDrinks);
                    Log.e("WELLNESS",sleepHour);
                    Log.e("WELLNESS",playMinutes);
                    Log.e("WELLNESS",exerciseMinutes);
                    Log.e("WELLNESS",stillnessMinutes);
                    Log.e("WELLNESS",tvHour);
                    Log.e("WELLNESS",computerHour);
                    Log.e("WELLNESS",phoneHour);
                    Log.e("WELLNESS",portionn);
                    Log.e("WELLNESS",portionns);

                    Intent intent=new Intent(AdvancedWellness.this,GraphActivity.class);
                    intent.putExtra("bundle",bundle);
                    intent.putExtra("from","adwd");
                    startActivity(intent);
                    finish();*/
                }

                break;
        }

        return false;
    }

    private void postAWDJsonData(final String sleepHour, final String sugaryDrinks, final String playMinutes, final String exerciseMinutes, final String stillnessMinutes, final String tvHour, final String computerHour, final String phoneHour, final String portionn, final String portionns) {

        pDialog = new ProgressDialog(AdvancedWellness.this);
        pDialog.setMessage("Please wait...");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_awd,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("Url_awd", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("200")) {

                                cc.showToast(jsonObject.getString("message"));

                                finish();

                                pDialog.dismiss();

                            } else {
                                pDialog.dismiss();
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

                params.put("hours",sleepHour);
                params.put("sugary",sugaryDrinks);
                params.put("play",playMinutes);
                params.put("exercise",exerciseMinutes);
                params.put("stillness",stillnessMinutes);
                params.put("tv",tvHour);
                params.put("computer",computerHour);
                params.put("smart_phone",phoneHour);
                params.put("portion",portionn);
                params.put("portions",portionns);

                Log.e("request_pa", String.valueOf(params));

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

    public String convertMinToHour(final String min){
//        float minute = Integer.parseInt(min);
        float minute = Float.parseFloat(min);

        minute=minute/60;

        return minute+"";
    }

    private void showPoupupMenu(final String popupDialogType, final String title, final String[] items) {
        //List of items to be show in  alert Dialog are stored in array of strings/char sequences
        AlertDialog.Builder builder = new AlertDialog.Builder(AdvancedWellness.this);
        //set the title for alert dialog
        builder.setTitle(title);
        //set items to alert dialog. i.e. our array , which will be shown as list view in alert dialog
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                // setting the button text to the selected itenm from the list
                if (popupDialogType.equalsIgnoreCase(getString(R.string.hours))) {
                    tv_hours.setText(items[item]);
                } else if (popupDialogType.equalsIgnoreCase(getString(R.string.sugary_drunk))) {
                    tv_sugary.setText(items[item]);
                } else if (popupDialogType.equalsIgnoreCase(getString(R.string.play))) {
                    tv_play.setText(items[item]);
                } else if (popupDialogType.equalsIgnoreCase(getString(R.string.exercise))) {
                    tv_exercise.setText(items[item]);
                } else if (popupDialogType.equalsIgnoreCase(getString(R.string.stillness))) {
                    tv_stillness.setText(items[item]);
                } else if (popupDialogType.equalsIgnoreCase(getString(R.string.tv))) {
                    tv_tv.setText(items[item]);
                } else if (popupDialogType.equalsIgnoreCase(getString(R.string.computer))) {
                    tv_computer.setText(items[item]);
                } else if (popupDialogType.equalsIgnoreCase(getString(R.string.smart_phone))) {
                    tv_smart_phone.setText(items[item]);
                } else if (popupDialogType.equalsIgnoreCase(getString(R.string.portions))) {
                    tv_portion.setText(items[item]);
                } else if (popupDialogType.equalsIgnoreCase(getString(R.string.portions_of))) {
                    tv_portions.setText(items[item]);
                }
            }
        });
        //Creating CANCEL button in alert dialog, to dismiss the dialog box when nothing is selected
        builder.setCancelable(false)
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //When clicked on CANCEL button the dalog will be dismissed
                        dialog.dismiss();
                    }
                });
        // Creating alert dialog
        AlertDialog alert = builder.create();
        //Showing alert dialog
        alert.show();
    }
}
