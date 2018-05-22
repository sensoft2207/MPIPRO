package com.mxi.myinnerpharmacy.activity;

import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.adapter.NavigationDrawerAdapter;
import com.mxi.myinnerpharmacy.fragment.Home;
import com.mxi.myinnerpharmacy.network.AppController;
import com.mxi.myinnerpharmacy.network.CommanClass;
import com.mxi.myinnerpharmacy.network.RegistrationCommanClass;
import com.mxi.myinnerpharmacy.network.URL;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements /*View.OnTouchListener*/View.OnClickListener {

    DrawerLayout drawer;
    CommanClass cc;
    RegistrationCommanClass rcc;
    CircleImageView iv_profile_image;
    TextView tv_name, tv_logout,tv_subscriberday,tv_sub_btn;
    LinearLayout ll_profile;
    RecyclerView mRecyclerView;
    NavigationDrawerAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    public static Boolean isSkip = false;
    public static Boolean StepLastColibration = false;

    String subscriber_day;

    //Signout
    public FirebaseAuth firebaseAuth;
    public GoogleApiClient googleApiClient;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_main);


        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();

        Twitter.initialize(twitterConfig);

        long totalMilliseconds=0;
        Calendar cal = Calendar.getInstance();
        int year  = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date  = cal.get(Calendar.DATE);
        cal.clear();
        cal.set(year, month, date);

//        Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
//        startActivity(intent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        UsageStatsManager usageStatsManager = (UsageStatsManager)getSystemService(USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, cal.getTimeInMillis(),System.currentTimeMillis());

        for (UsageStats us : queryUsageStats) {

                Log.d("@@@Akshay", us.getPackageName() + " = " + us.getTotalTimeInForeground());
            totalMilliseconds=totalMilliseconds+us.getTotalTimeInForeground();
            }
        }

        firebaseAuth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        long seconds=0;
        long hours=0;
        seconds=totalMilliseconds/1000;
        hours=seconds/3600;
        Log.e("@@@Total Hours",hours+"");
        Log.e("@@@Total Millis",totalMilliseconds+"");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        cc = new CommanClass(MainActivity.this);
        rcc = new RegistrationCommanClass(MainActivity.this);



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);

        tv_logout = (TextView) findViewById(R.id.tv_logout);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_subscriberday = (TextView) findViewById(R.id.tv_subscriberday);
        tv_sub_btn = (TextView) findViewById(R.id.tv_sub_btn);
        iv_profile_image = (CircleImageView) findViewById(R.id.iv_profile_image);
        ll_profile = (LinearLayout) findViewById(R.id.ll_profile);

        tv_name.setText(cc.loadPrefString("name"));

        Glide.with(MainActivity.this)
                .load(cc.loadPrefString("avatar"))
                //.placeholder(R.mipmap.choose_image)
                .into(iv_profile_image);

        tv_logout.setOnClickListener(this);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        String TITLES[] = getResources().getStringArray(R.array.nav_drawer_items);
        TypedArray navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mRecyclerView.setHasFixedSize(true);
        mAdapter = new NavigationDrawerAdapter(MainActivity.this, TITLES, navMenuIcons);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                displayView(position);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        ll_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MyProfile.class));
            }
        });
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, new Home());
        fragmentTransaction.commit();


        /*Pending..............................*/

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));

        Log.e("@@SUBDAY",cc.loadPrefString("subscribe_days"));

        //Signout gmail
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .enableAutoManage(MainActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            subscriber_day = intent.getStringExtra("message");
           // subscriber_day = "3";

            int sub = Integer.parseInt(subscriber_day);

            if (sub <= 5){

                tv_subscriberday.setVisibility(View.INVISIBLE);
                tv_sub_btn.setVisibility(View.VISIBLE);

                tv_sub_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intentSub = new Intent(MainActivity.this,SubscriptionPlanActivity.class);
                        startActivity(intentSub);
                    }
                });

            }else {

                tv_subscriberday.setText("Remaining days :"+" "+sub);
                tv_subscriberday.setVisibility(View.VISIBLE);
                tv_sub_btn.setVisibility(View.INVISIBLE);
            }



            Log.d("receiver", "Got message: " + subscriber_day);
        }
    };

   /* @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.tv_logout:

//                makeJsonLogout();

                break;
        }

        return false;
    }*/

    @SuppressLint("NewApi")
    private void displayView(int menuItem) {
        Fragment fragment = null;
        switch (menuItem) {

            case 0:
                // fragment = new Home();
//                startActivity(new Intent(MainActivity.this, RapidResponse.class));
                startActivity(new Intent(MainActivity.this, PrescriptionStep.class));
                cc.savePrefString("p_steps","psteps");
                cc.savePrefString("menu_selfi_click","");
                cc.savePrefString("mydead_back","");

                break;
            case 1:

//                startActivity(new Intent(MainActivity.this, PauseTask.class));
                startActivity(new Intent(MainActivity.this, RapidResponse.class));

                break;
            case 2:
//                startActivity(new Intent(MainActivity.this, CurrentPrescriptionRequest.class));
                startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
                break;
            case 3:

//                startActivity(new Intent(MainActivity.this, StateCalibration.class));
                startActivity(new Intent(MainActivity.this, PauseTask.class));
                break;
            case 4:

//                startActivity(new Intent(MainActivity.this, HeartRate.class));
                startActivity(new Intent(MainActivity.this, UpliftingMusic.class));
                break;
            case 5:
//                startActivity(new Intent(MainActivity.this, BreathAnalysis.class));
                startActivity(new Intent(MainActivity.this, StateCalibrationNext.class));
                cc.savePrefString("new_prescription_state","");
                break;
            case 6:
//                startActivity(new Intent(MainActivity.this, ResourceJournal.class));
//                startActivity(new Intent(MainActivity.this, ResourceJournalSplashActivity.class));
                startActivity(new Intent(MainActivity.this, HeartRate.class));
                break;

            case 7:

//                startActivity(new Intent(MainActivity.this, UpliftingMusic.class));
                startActivity(new Intent(MainActivity.this, BreathAnalysis.class));
                cc.savePrefString("Skip","skip");
                cc.savePrefString("to_main_activity","tma");
                break;
            case 8:

//                startActivity(new Intent(MainActivity.this, AdvancedWellness.class));
                startActivity(new Intent(MainActivity.this, AdvancedWellness.class));
                break;

            case 9:
//                startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
                startActivity(new Intent(MainActivity.this, ReminderFrequency.class));
                break;

            case 10:


//                    startActivity(new Intent(MainActivity.this, ReminderFrequency.class));
                    startActivity(new Intent(MainActivity.this, ResourceJournalSplashActivity.class));
                    cc.savePrefString("menu_current_self","mcss");

                break;

            case 11:


//                    startActivity(new Intent(MainActivity.this, ReminderFrequency.class));
                startActivity(new Intent(MainActivity.this, MyDEADsSplashActivity.class));
                cc.savePrefString("menu_selfi_click","msc");
                cc.savePrefString("mydead_back","mydead");

                break;

            default:


        }
        try {
            if (fragment != null) {
                drawer.closeDrawers();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("Error", e.getMessage());
            drawer.closeDrawers();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void closeDrawer() {
        drawer.closeDrawers();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_logout:

                makeJsonLogout();

                break;
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
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

    private void makeJsonLogout() {

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_logout,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("logout", response);
                        jsonParseMatchList(response);
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                cc.showSnackbar(mRecyclerView, error.getMessage().toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("device_id", cc.loadPrefString("device_id"));

                Log.i("request logout", params.toString());

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

    private void jsonParseMatchList(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("200")) {

                cc.showToast(jsonObject.getString("message"));

                firebaseAuth.signOut();

                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback() {
                    @Override
                    public void onResult(@NonNull Result result) {

                    }
                });

                LoginManager.getInstance().logOut();
                mAuth.signOut();
               /* CookieSyncManager.createInstance(this);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeSessionCookie();
                TwitterCore.getInstance().getSessionManager().clearActiveSession();*/

                logoutTwitter();

                cc.logoutapp();
                rcc.logoutapp();

//                TwitterCore.getInstance().getSessionManager().clearActiveSession();

                startActivity(new Intent(MainActivity.this, LoginScreen.class).
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                rcc.savePrefBoolean("isDisclaimer",true);

                finish();


            } else {

                cc.showToast(jsonObject.getString("message"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void logoutTwitter() {
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (twitterSession != null) {
            ClearCookies(getApplicationContext());
            TwitterCore.getInstance().getSessionManager().clearActiveSession();
        }
    }

    public static void ClearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}
