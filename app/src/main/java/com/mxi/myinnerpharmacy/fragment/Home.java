package com.mxi.myinnerpharmacy.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mxi.myinnerpharmacy.AutoScrollViewPager;
import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.activity.AdvancedWellness;
import com.mxi.myinnerpharmacy.activity.BreathAnalysis;
import com.mxi.myinnerpharmacy.activity.HeartRate;
import com.mxi.myinnerpharmacy.activity.MainActivity;
import com.mxi.myinnerpharmacy.activity.MyDEADsSplashActivity;
import com.mxi.myinnerpharmacy.activity.PauseTask;
import com.mxi.myinnerpharmacy.activity.PrescriptionCabinet;
import com.mxi.myinnerpharmacy.activity.PrescriptionStep;
import com.mxi.myinnerpharmacy.activity.RapidResponse;
import com.mxi.myinnerpharmacy.activity.ReminderFrequency;
import com.mxi.myinnerpharmacy.activity.ResourceJournalSplashActivity;
import com.mxi.myinnerpharmacy.activity.StateCalibrationNext;
import com.mxi.myinnerpharmacy.activity.StatisticsActivity;
import com.mxi.myinnerpharmacy.activity.UpliftingMusic;
import com.mxi.myinnerpharmacy.adapter.SlidingImage_Adapter;
import com.mxi.myinnerpharmacy.model.PrescriptionTag;
import com.mxi.myinnerpharmacy.network.AppController;
import com.mxi.myinnerpharmacy.network.CommanClass;
import com.mxi.myinnerpharmacy.network.URL;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Home extends Fragment implements View.OnClickListener {

    private TextView tvMainNewPrescription;
    private TextView tvMainPrescriptionCabinet;
    private TextView tvMainRapidResponse;
    private TextView tvMainMyEmotionalChart;
    private TextView tvMainUpliftingMusic;
    private TextView tvMainStateCalibration;
    private TextView tvMainPauseTask;
    private TextView tvMainRoutineTracking;
    private TextView tvMainMySelfTalk;
    private TextView tvMainBreathingAnalysis;
    private TextView tvMainReminder;
    private TextView tvMainHeartRate;
    private TextView tvMainResourceJournal;

    ImageView iv_book, iv_redbtn, iv_resource_journal, iv_my_deads;
//    TextView prescriptions,remienders, tv_rapide_response, pause_task, current_prescription, my_self_talk, state_calibration, heartrate_monitor, brething_analysis, resource_journal, uplifting_music, advance_wellense, statidtics, reminders;
    ProgressDialog pDialog;
    CommanClass cc;
    ArrayList<PrescriptionTag> prescriptionlist;
    LinearLayout ll_linear;
    CirclePageIndicator indicator;
    int position=0;
    private static AutoScrollViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private static final Integer[] IMAGES= {R.drawable.slider_image_one,R.drawable.slider_image_two,R.drawable.slider_image_three};

    String[] titlePager;

    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        cc = new CommanClass(getContext());

//

//        remienders=(TextView)rootView.findViewById(R.id.remienders);
//        my_self_talk = (TextView) rootView.findViewById(R.id.my_self_talk);
//        prescriptions = (TextView) rootView.findViewById(R.id.prescription);
//        tv_rapide_response = (TextView) rootView.findViewById(R.id.rv_rapid_prescription);
//        pause_task = (TextView) rootView.findViewById(R.id.pause_task);
//        current_prescription = (TextView) rootView.findViewById(R.id.current_request);
//        state_calibration = (TextView) rootView.findViewById(R.id.state_calibratioj);
//        heartrate_monitor = (TextView) rootView.findViewById(R.id.heart_monitor);
//        brething_analysis = (TextView) rootView.findViewById(R.id.breathing_analysis);
//        resource_journal = (TextView) rootView.findViewById(R.id.resource_journal);
//        uplifting_music = (TextView) rootView.findViewById(R.id.uplifting_music);
//        advance_wellense = (TextView) rootView.findViewById(R.id.advance_wellness);


        tvMainNewPrescription = (TextView) rootView.findViewById(R.id.tv_main_new_prescription);
        tvMainPrescriptionCabinet = (TextView) rootView.findViewById(R.id.tv_main_prescription_cabinet);
        tvMainRapidResponse = (TextView) rootView.findViewById(R.id.tv_main_rapid_response);
        tvMainMyEmotionalChart = (TextView) rootView.findViewById(R.id.tv_main_my_emotional_chart);
        tvMainUpliftingMusic = (TextView) rootView.findViewById(R.id.tv_main_uplifting_music);
        tvMainStateCalibration = (TextView) rootView.findViewById(R.id.tv_main_state_calibration);
        tvMainPauseTask = (TextView) rootView.findViewById(R.id.tv_main_pause_task);
        tvMainRoutineTracking = (TextView) rootView.findViewById(R.id.tv_main_routine_tracking);
        tvMainMySelfTalk = (TextView) rootView.findViewById(R.id.tv_main_my_self_talk);
        tvMainBreathingAnalysis = (TextView) rootView.findViewById(R.id.tv_main_breathing_analysis);
        tvMainReminder = (TextView) rootView.findViewById(R.id.tv_main_reminder);
        tvMainHeartRate = (TextView) rootView.findViewById(R.id.tv_main_heart_rate);
        tvMainResourceJournal = (TextView) rootView.findViewById(R.id.tv_main_resource_journal);

        mPager = (AutoScrollViewPager)rootView.findViewById(R.id.pager_introduction);
        indicator = (CirclePageIndicator) rootView.findViewById(R.id.indicator);
//        st_statics = (TextView) rootView.findViewById(R.id.st);
        //  reminders = (TextView) rootView.findViewById(R.id.remienders);
        ll_linear = (LinearLayout) rootView.findViewById(R.id.ll_linear);
        initLsiteners();


        mPager.startAutoScroll();
        mPager.setInterval(3000);
        mPager.setCycle(true);
        mPager.setStopScrollWhenTouch(true);

        for(int i=0;i<IMAGES.length;i++)
            ImagesArray.add(IMAGES[i]);

        titlePager = new String[] { "Master Your Emotional State", "Prescribe To Yourself Feel Good Inner Pharmacy Prescription", "Improve Your Emotional Intelligence"};

        mPager.setAdapter(new SlidingImage_Adapter(getActivity(),ImagesArray,titlePager));
        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;


        indicator.setRadius(5 * density);
        NUM_PAGES =IMAGES.length;
        init();

        return rootView;
    }
    private void init() {


        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });
    }

    private void initLsiteners() {


        tvMainNewPrescription.setOnClickListener(this);
        tvMainPrescriptionCabinet.setOnClickListener(this);
        tvMainRapidResponse.setOnClickListener(this);
        tvMainMyEmotionalChart.setOnClickListener(this);
        tvMainUpliftingMusic.setOnClickListener(this);
        tvMainStateCalibration.setOnClickListener(this);
        tvMainPauseTask.setOnClickListener(this);
        tvMainRoutineTracking.setOnClickListener(this);
        tvMainMySelfTalk.setOnClickListener(this);
        tvMainBreathingAnalysis.setOnClickListener(this);
        tvMainReminder.setOnClickListener(this);
        tvMainHeartRate.setOnClickListener(this);
        tvMainResourceJournal.setOnClickListener(this);

//        tv_rapide_response.setOnClickListener(this);
//        pause_task.setOnClickListener(this);
//        current_prescription.setOnClickListener(this);
//        state_calibration.setOnClickListener(this);
//        heartrate_monitor.setOnClickListener(this);
//        brething_analysis.setOnClickListener(this);
//        resource_journal.setOnClickListener(this);
//        uplifting_music.setOnClickListener(this);
//        advance_wellense.setOnClickListener(this);
//        prescriptions.setOnClickListener(this);
//        remienders.setOnClickListener(this);
//        st_statics.setOnClickListener(this);
//        statidtics.setOnClickListener(this);
        //  reminders.setOnClickListener(this);
//        my_self_talk.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_main_prescription_cabinet:
//
//                    startMedicalCabinateActivity(0);
//
//                    startMedicalCabinateActivity(1);
//                } else if (position == 2) {
//                    startMedicalCabinateActivity(2);
//                } else if (position == 3) {
//                    startMedicalCabinateActivity(3);
//                } else if (position == 4) {
//                    startMedicalCabinateActivity(4);
//                } else if (position == 5) {
//                    startMedicalCabinateActivity(5);
//                } else if (position == 6) {
//                    startMedicalCabinateActivity(6);
//                } else if (position == 7) {
//                    startMedicalCabinateActivity(7);
//                } else if (position == 8) {
//                    startMedicalCabinateActivity(8);
//                } else if (position == 9) {
//                    startMedicalCabinateActivity(9);
//                } else if (position == 10) {
//                    startMedicalCabinateActivity(10);
//                }

//                startMedicalCabinateActivity(0);
//                startMedicalCabinateActivity(1);
//                startMedicalCabinateActivity(2);
//                startMedicalCabinateActivity(3);
//                startMedicalCabinateActivity(4);
//                startMedicalCabinateActivity(5);
//                startMedicalCabinateActivity(6);
//                startMedicalCabinateActivity(7);
//                startMedicalCabinateActivity(8);
//                startMedicalCabinateActivity(9);
//                startMedicalCabinateActivity(10);

                Intent intentPrescriptionCabinet = new Intent(getActivity(), PrescriptionCabinet.class);
                startActivity(intentPrescriptionCabinet);

//                Toast.makeText(getContext(), "Prescription Cabinet", Toast.LENGTH_SHORT).show();
                break;


            case R.id.tv_main_my_self_talk:
                startActivity(new Intent(getActivity(), MyDEADsSplashActivity.class));
                cc.savePrefString("menu_selfi_click","msc");
                cc.savePrefString("mydead_back","mydead");

                // Toast.makeText(getContext(), "Top", Toast.LENGTH_SHORT).show();
                break;

            case R.id.tv_main_new_prescription:
//                startActivity(new Intent(getActivity(), MyDEADsSplashActivity.class));
                startActivity(new Intent(getActivity(), PrescriptionStep.class));
                cc.savePrefString("p_steps","psteps");
                cc.savePrefString("menu_selfi_click","");
                cc.savePrefString("mydead_back","");
                break;
            case R.id.tv_main_rapid_response:
                startActivity(new Intent(getActivity(), RapidResponse.class));
                // Toast.makeText(getContext(), "Top", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_main_pause_task:
                startActivity(new Intent(getActivity(), PauseTask.class));
                //  Toast.makeText(getContext(), "FirstCenter", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_main_my_emotional_chart:
                startActivity(new Intent(getActivity(), StatisticsActivity.class));
                // Toast.makeText(getContext(), "SecondCenter1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_main_state_calibration:
                startActivity(new Intent(getActivity(), StateCalibrationNext.class));
                cc.savePrefString("new_prescription_state","");
                //  Toast.makeText(getContext(), "SecondCenter2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_main_heart_rate:
                startActivity(new Intent(getActivity(), HeartRate.class));
                // Toast.makeText(getContext(), "Bottom Most", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_main_breathing_analysis:
                startActivity(new Intent(getActivity(), BreathAnalysis.class));
                cc.savePrefString("Skip","skip");
                cc.savePrefString("to_main_activity","tma");
                // startActivity(new Intent(getActivity(), ResourceJournalSplashActivity.class));
//                startActivity(new Intent(getActivity(), MedicalCabinets.class));
                //  Toast.makeText(getContext(), "red Button", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_main_resource_journal:
//                startActivity(new Intent(getActivity(), ResourceJournal.class));
                startActivity(new Intent(getActivity(), ResourceJournalSplashActivity.class));
                cc.savePrefString("menu_current_self","mcss");
                // Toast.makeText(getContext(), "Resource Journal", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_main_uplifting_music:
//                startActivity(new Intent(getActivity(), MyDeads.class));
                startActivity(new Intent(getActivity(), UpliftingMusic.class));
                // Toast.makeText(getContext(), "My D.E.A.D's", Toast.LENGTH_SHORT).show();
                break;

//            case R.id.st:
//                startActivity(new Intent(getActivity(), AdvancedWellness.class));
////                startActivity(new Intent(getActivity(), MedicalCabinets.class));
//                //  Toast.makeText(getContext(), "red Button", Toast.LENGTH_SHORT).show();
//                break;

            case R.id.tv_main_routine_tracking:
                startActivity(new Intent(getActivity(), AdvancedWellness.class));
//                startActivity(new Intent(getActivity(), MedicalCabinets.class));
                //  Toast.makeText(getContext(), "red Button", Toast.LENGTH_SHORT).show();
                break;
///
            case R.id.tv_main_reminder:
//                startActivity(new Intent(getActivity(), MyDeads.class));
                startActivity(new Intent(getActivity(), ReminderFrequency.class));

                // Toast.makeText(getContext(), "My D.E.A.D's", Toast.LENGTH_SHORT).show();
                break;
        }

    }

//    private void startMedicalCabinateActivity(int position) {
//
//
//        Intent intent = new Intent(getActivity(), MedicalCabinets.class);
//        intent.putExtra("firstPrescription", prescriptionlist.get(0).getPrescription_name());
//        intent.putExtra("firstPrescription_id",  prescriptionlist.get(0).getTag_id());
//
//
//        intent.putExtra("secondPrescription", prescriptionlist.get(1).getPrescription_name());
//        intent.putExtra("secondPrescription_id", prescriptionlist.get(1).getTag_id());
//
//        intent.putExtra("thirdPrescription", prescriptionlist.get(2).getPrescription_name());
//        intent.putExtra("thirdPrescription_id", prescriptionlist.get(2).getTag_id());
//
//        intent.putExtra("fourPrescription", prescriptionlist.get(3).getPrescription_name());
//        intent.putExtra("fourPrescription_id", prescriptionlist.get(3).getTag_id());
//
//
//        intent.putExtra("fivePrescription", prescriptionlist.get(4).getPrescription_name());
//        intent.putExtra("fivePrescription_id", prescriptionlist.get(4).getTag_id());
//
//
//        intent.putExtra("sixPrescription", prescriptionlist.get(5).getPrescription_name());
//        intent.putExtra("sixPrescription_id", prescriptionlist.get(5).getTag_id());
//
//
//        intent.putExtra("sevenPrescription", prescriptionlist.get(6).getPrescription_name());
//        intent.putExtra("sevenPrescription_id", prescriptionlist.get(6).getTag_id());
//
//
//        intent.putExtra("EightPrescription", prescriptionlist.get(7).getPrescription_name());
//        intent.putExtra("EightPrescription_id", prescriptionlist.get(7).getTag_id());
//
//
//
//        intent.putExtra("ninePrescription", prescriptionlist.get(8).getPrescription_name());
//        intent.putExtra("ninePrescription_id", prescriptionlist.get(8).getTag_id());
////
//
//
//        intent.putExtra("tenPrescription", prescriptionlist.get(9).getPrescription_name());
//        intent.putExtra("tenPrescription_id", prescriptionlist.get(9).getTag_id());
//
//
//        intent.putExtra("elevenPrescription", prescriptionlist.get(10).getPrescription_name());
//        intent.putExtra("elevenPrescription_id", prescriptionlist.get(10).getTag_id());
//        startActivity(intent);
//    }






    @Override
    public void onResume() {
        super.onResume();

        try {
            MainActivity.isSkip = false;
            MainActivity.StepLastColibration = false;
            if (cc.isConnectingToInternet()) {
                pDialog = new ProgressDialog(getContext());
                pDialog.setMessage("Please wait...");
                pDialog.show();
                makeJsonGetPrescription();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void makeJsonGetPrescription() {
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_get_prescription_tags,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("Url_get_pres_tags", response);
                        jsonParsePauseTask(response);
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                cc.showSnackbar(ll_linear, getString(R.string.ws_error));

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

    private void jsonParsePauseTask(String response) {
        try {
            pDialog.dismiss();


            prescriptionlist = new ArrayList<PrescriptionTag>();
            JSONObject jsonObject = new JSONObject(response);


            cc.savePrefString("subscribe_days",jsonObject.getString("subscribe_days"));

            Intent intent = new Intent("custom-event-name");
            intent.putExtra("message",jsonObject.getString("subscribe_days"));
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

            Log.e("@@SUBSCRIBEDAY",cc.loadPrefString("subscribe_days"));

            if (jsonObject.getString("status").equals("200")) {

                JSONArray prescription = jsonObject.getJSONArray("prescription");

                for (int i = 0; i < prescription.length(); i++) {
                    JSONObject jsonObject1 = prescription.getJSONObject(i);

                    PrescriptionTag pk = new PrescriptionTag();//prescription_id,text
                    pk.setTag_id(jsonObject1.getString("prescription_id"));
                    pk.setPrescription_name(jsonObject1.getString("text"));

                    prescriptionlist.add(pk);
                    Log.d("response",response+"");
                }

            } else {

                cc.showToast(jsonObject.getString("message"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
