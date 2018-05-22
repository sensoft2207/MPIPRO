package com.mxi.myinnerpharmacy.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.database.SQLiteTD;
import com.mxi.myinnerpharmacy.network.AppController;
import com.mxi.myinnerpharmacy.network.CommanClass;
import com.mxi.myinnerpharmacy.network.RegistrationCommanClass;
import com.mxi.myinnerpharmacy.network.URL;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends AppCompatActivity implements View.OnTouchListener, CompoundButton.OnCheckedChangeListener {

    EditText et_email, et_password;
    TextView tv_forgot_password, tv_login, tv_register;
//    ImageView /*iv_facebook,*/ iv_twitter/*, iv_google*/;
    ImageButton iv_facebook,iv_google,iv_twitter;
    ProgressDialog pDialog;
    CommanClass cc;
    LinearLayout ll_linear;
    RegistrationCommanClass rcc;
    GoogleCloudMessaging gcmObj;
    String regId = "";
    SQLiteTD db;
    CheckBox cb_remember_me;
    public boolean checked;
    String rememberMe;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    //Facebook Integration
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static final int FacebookSing = 2;
    LoginButton loginButton;
    CallbackManager mCallbackManager;
    private AccessTokenTracker accessTokenTracker;
    String name;
    private ProfileTracker profileTracker;
    private FirebaseAuth mAuth;
    public FirebaseAuth firebaseAuth;


    //Gmail Integration

    public static final String TAG2 = "Login";
    public static final int RequestSignInCode = 1;
    public GoogleApiClient googleApiClient;
    com.google.android.gms.common.SignInButton signInButton;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;


    //Twitter
    private TwitterLoginButton mLoginButton;


    String photo_url;
    String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        TwitterAuthConfig authConfig =  new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();

        Twitter.initialize(twitterConfig);

        setContentView(R.layout.activity_login_screen);
        cc = new CommanClass(LoginScreen.this);
        rcc = new RegistrationCommanClass(LoginScreen.this);
        db = new SQLiteTD(LoginScreen.this);

        firebaseAuth = FirebaseAuth.getInstance();

        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        mCallbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
//                nextActivity(newProfile);
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        tv_forgot_password = (TextView) findViewById(R.id.tv_forgot_password);
        tv_login = (TextView) findViewById(R.id.tv_login);
        tv_register = (TextView) findViewById(R.id.tv_register);

        iv_facebook = (ImageButton) findViewById(R.id.iv_facebook);
        iv_twitter = (ImageButton) findViewById(R.id.iv_twitter);
        iv_google = (ImageButton) findViewById(R.id.iv_google);

        ll_linear = (LinearLayout) findViewById(R.id.ll_linear);
        cb_remember_me = (CheckBox) findViewById(R.id.cb_remember_me);

        tv_forgot_password.setOnTouchListener(this);
        tv_register.setOnTouchListener(this);
        tv_login.setOnTouchListener(this);

        // et_email.setText("sonali@mxicoders.com");
        //  et_password.setText("mxi123");
        String texm_text = "<font color=#FFFFFF><u>" + getString(R.string.new_user_login) + "</u></font>";
        tv_register.setText(Html.fromHtml(texm_text));

        String device_id = Settings.Secure.getString(LoginScreen.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        cc.savePrefString("device_id", device_id);

        cb_remember_me.setOnCheckedChangeListener(this);



        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Creating and Configuring Google Api Client.
        googleApiClient = new GoogleApiClient.Builder(LoginScreen.this)
                .enableAutoManage(LoginScreen.this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();



        android_id = Settings.Secure.getString(LoginScreen.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.e("Success", "facebook:onSuccess:" + loginResult);

                loginResult.getAccessToken();

                handleFacebookAccessToken(loginResult.getAccessToken()); //  Toast.makeText(MainActivity.this, "Token:"+loginResult.getAccessToken(), Toast.LENGTH_SHORT).show();

                getUserDetails(loginResult);

                Log.e("getToken", String.valueOf(loginResult.getAccessToken()));

            }

            @Override
            public void onCancel() {
                Log.e("Cancle", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("Error", "facebook:onError", error);
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
                    Log.e("Signin", "onAuthStateChanged:signed_in:" + mUser.getUid());
                } else {
                    // User is signed out
                    Log.e("Signout", "onAuthStateChanged:signed_out");
                }

            }
        };

        mLoginButton = (TwitterLoginButton)findViewById(R.id.button_twitter_login);
        mLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                Log.d("TwitterSuccess", "twitterLogin:success" + result);
                handleTwitterSession(result.data);

                TwitterSession user = result.data;

                handleTwitterSignInResult(user);



            }

            @Override
            public void failure(TwitterException exception) {
                Log.w("TwitterFailure", "twitterLogin:failure", exception);

            }
        });




        //================================ Remember me========================================================================
        rememberMe();


        iv_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginButton.performClick();

                cc.savePrefString("from_facebook","ff");
            }
        });

        iv_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent AuthIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(AuthIntent, RequestSignInCode);

                cc.savePrefString("from_gmail","fg");
            }
        });

        iv_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginButton.performClick();
                cc.savePrefString("from_twitter","ft");
            }
        });

    }

    private void handleTwitterSignInResult(TwitterSession result) {

        TwitterCore.getInstance().getApiClient(result).getAccountService().verifyCredentials(false,true,true).enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> userResult) {
                try {
                    User user = userResult.data;
                    String fullname = user.name;
                    String idd = String.valueOf(user.getId());
                    String email = user.email;
                    String photo_url = user.profileImageUrl;
                    String name = user.screenName;

                    Log.e("TName",name);
                    Log.e("TId",idd);
                    Log.e("TPhotoUrl",photo_url);
                    Log.e("TEmail",email);

                    if (!cc.isConnectingToInternet()) {
                        cc.showToast(getResources().getString(R.string.no_internet));
                    } else {

                        socialSignUpWS(name,email,idd,photo_url,android_id);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void failure(TwitterException e) {
            }
        });

    }

    //Facebook Integration
    private void handleFacebookAccessToken(final AccessToken token) {
        Log.e("Token", "handleFacebookAccessToken:" + token);



        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("Success2", "signInWithCredential:success");

                        } else {

                            Log.e("Failure", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestSignInCode) {

            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (googleSignInResult.isSuccess()) {

                GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();

                FirebaseUserAuth(googleSignInAccount);

                handleGmailSignInResult(googleSignInResult);
            }

        } else if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            mLoginButton.onActivityResult(requestCode, resultCode, data);

        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Gmail Integration
    public void FirebaseUserAuth(final GoogleSignInAccount googleSignInAccount) {

        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);


        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(LoginScreen.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task AuthResultTask) {

                        if (AuthResultTask.isSuccessful()){

                            // Getting Current Login user details.
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        }else {
                            Toast.makeText(LoginScreen.this,"Something Went Wrong",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void handleGmailSignInResult(GoogleSignInResult result) {
        Log.d("handle", "handleGmailSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();


            String name = acct.getDisplayName();
            String photo_url = acct.getPhotoUrl().toString();
            String email = acct.getEmail();
            String idd = acct.getId();

            Log.e("GEmail",email);
            Log.e("GName",name);
            Log.e("GPhoto",photo_url);
            Log.e("GId",idd);

            if (!cc.isConnectingToInternet()) {
                cc.showToast(getResources().getString(R.string.no_internet));
            } else {

                socialSignUpWS(name,email,idd,photo_url,android_id);
            }


        } else {

        }
    }

    //Twitter Integration
    private void handleTwitterSession(final TwitterSession session) {
        Log.e("Tsession", "handleTwitterSession:" + session);

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("TSuccess", "signInWithCredential:success");


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TFailure", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }


    protected void getUserDetails(LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {


                        try {


                            String name = json_object.get("name").toString();
                            String email = json_object.get("email").toString();
                            String idd = json_object.get("id").toString();

                            Log.e("name",json_object.get("name").toString());
                            Log.e("email",json_object.get("email").toString());
                            Log.e("id",json_object.get("id").toString());

                            Profile profile = Profile.getCurrentProfile();
                            String id = profile.getId();
                            String link = profile.getLinkUri().toString();
                            Log.i("Link",link);

                            if (Profile.getCurrentProfile()!=null)
                            {

                                photo_url = String.valueOf(Profile.getCurrentProfile().getProfilePictureUri(200, 200));

                                Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                            }

                            if (!cc.isConnectingToInternet()) {
                                cc.showToast(getResources().getString(R.string.no_internet));
                            } else {

                                socialSignUpWS(name,email,idd,photo_url,android_id);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        Log.e("FinalSuccess",".........");
                    }

                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

    }

    private void socialSignUpWS(final String name, final String email, final String idd, final String photo_url, final String android_id) {

        pDialog = new ProgressDialog(LoginScreen.this);
        pDialog.setMessage("Please wait...");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_SocialLogin,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("Url_sociallog", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("200")) {

                                JSONArray userdetail = jsonObject.getJSONArray("userdetail");
                                JSONObject data = userdetail.getJSONObject(0);

                                cc.savePrefString("name", data.getString("name"));
                                cc.savePrefString("email", data.getString("email"));
                                cc.savePrefString("gender", data.getString("gender"));
                                cc.savePrefString("dob", data.getString("dob"));
                                cc.savePrefString("avatar", data.getString("avatar"));
                                cc.savePrefString("height", data.getString("height"));
                                cc.savePrefString("weight", data.getString("weight"));
                                cc.savePrefString("sigupfrom", data.getString("sigupfrom"));

                                cc.savePrefBoolean("islogin", true);

                                rcc.savePrefBoolean("ISLOGIN", true);

                                db.inseartLogin(cc.loadPrefString("email"), et_password.getText().toString().trim(), rememberMe);

                                Intent mIntent = new Intent(LoginScreen.this,
                                        Questionnair.class);
                                startActivity(mIntent);
                                finish();

                            } else {

                                cc.showToast(jsonObject.getString("message"));
                            }                        } catch (JSONException e) {
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

                params.put("email",email);
                params.put("name",name);

                if (cc.loadPrefString("from_facebook").equals("ff")){

                    params.put("social_media_type","Facebook");

                    cc.savePrefString("from_facebook","");

                }else if (cc.loadPrefString("from_gmail").equals("fg")){

                    params.put("social_media_type","Gmail");

                    cc.savePrefString("from_gmail","");

                }else {

                    params.put("social_media_type","Twitter");

                }
                params.put("social_media_id",idd);
                params.put("login_with","android");
                params.put("gcm_id","dsfsfd");
                params.put("device_id",android_id);
                params.put("login_by","user");
                params.put("photo_url",photo_url);

                return params;
            }
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                try {
                    String mip_token = response.headers.get("mip-token");
                    Log.e("mip-token", mip_token);
                    cc.savePrefString("mip-token", mip_token);
                } catch (Exception e) {
                    Log.e("Error In Volly", e.toString());
                }

                return super.parseNetworkResponse(response);

            }

        };
        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_remember_me:
                cb_remember_me.setChecked(isChecked);
                checked = isChecked;
                if (checked) {
                    rememberMe = "Yes";
                    Log.e("checkbox_false", rememberMe + "");

                } else {
                    rememberMe = "No";
                    Log.e("checkbox_true", rememberMe + "");

                }
                break;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {

            case R.id.tv_forgot_password:

                showForgotPasswordDialog();
                break;
            case R.id.tv_register:

                startActivity(new Intent(LoginScreen.this, Register.class));
                break;

            case R.id.tv_login:
                login();
                break;
        }
        return false;
    }

    private void rememberMe() {

        try {
            Cursor c = db.getLogin();
            if (c.getCount() != 0 && c != null) {
                c.moveToFirst();
                do {

                    // c.getString(0);
                    if (c.getString(3).equals("Yes")) {
                        checked = true;
                        cb_remember_me.setChecked(true);
                        et_email.setText(c.getString(1));
                        et_password.setText(c.getString(2));
                    } else {
                        checked = false;
                        cb_remember_me.setChecked(false);
                        et_email.setText("");
                        et_password.setText("");
                    }

                } while (c.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void login() {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();


        if (!cc.isConnectingToInternet()) {
            cc.showSnackbar(ll_linear, getString(R.string.no_internet));
        } else if (email.equals("")) {
            cc.showSnackbar(ll_linear, getString(R.string.enter_email));
        } else if (password.equals("")) {
            cc.showSnackbar(ll_linear, getString(R.string.enter_password));
        } else {
            pDialog = new ProgressDialog(LoginScreen.this);
            pDialog.setMessage("Please wait...");
            pDialog.show();

            if (checkPlayServices()) {
                // Register Device in GCM Server
                registerInBackground(email, password);
            }

        }


    }

    //--------------Forgot Password-----------------------------------------------------------------------------------------
    private void showForgotPasswordDialog() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginScreen.this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//getLayoutInflater();
        builder.setCancelable(true);
        final View dialogView = inflater.inflate(R.layout.forgot_password, null);

        builder.setView(dialogView);
        final AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final EditText et_email = (EditText) dialogView.findViewById(R.id.et_email);
        ImageView iv_close = (ImageView) dialogView.findViewById(R.id.iv_close);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        Button btn_send = (Button) dialogView.findViewById(R.id.btn_send);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = et_email.getText().toString().trim();
                if (!cc.isConnectingToInternet()) {

                    cc.showToast(getString(R.string.no_internet));
                } else if (email.equals("")) {
                    cc.showToast(getString(R.string.enter_forgot_email));
                } else {
                    alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    pDialog = new ProgressDialog(LoginScreen.this);
                    pDialog.setMessage("Please wait...");
                    pDialog.show();
                    makeJsonForgotPassword(email);
                    alert.dismiss();
                }
            }
        });

        alert.show();
    }


    // Check if Google Playservices is installed in Device or not
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        // When Play services not found in device
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // Show Error dialog to install Play services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        LoginScreen.this,
                        "This device doesn't support Play services, App will not work normally",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        } /*else {
            Toast.makeText(
                    LoginScreen.this,
                    "This device supports Play services, App will work normally",
                    Toast.LENGTH_LONG).show();
        }*/
        return true;
    }

    // AsyncTask to register Device in GCM Server
    private void registerInBackground(final String email, final String password) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging
                                .getInstance(LoginScreen.this);
                    }
                    regId = gcmObj
                            .register(URL.GOOGLE_PROJ_ID);
                    msg = regId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

                if (msg.isEmpty()) {
                    pDialog.dismiss();
                    cc.showToast(getString(R.string.ws_error));
                } else {
                    // regId = msg;
                    Log.e("regisId", msg);
                    makeJsonlogin(email, password, msg);
                }

            }
        }.execute(null, null, null);
    }

    private void makeJsonlogin(final String email, final String password, final String msg) {

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_login,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("login", response);
                        jsonParseMatchList(response);
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.i("error", error.toString());
                pDialog.dismiss();
                cc.showSnackbar(ll_linear, getString(R.string.ws_error));
            }
        }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email_id", email);
                    params.put("password", password);
                    params.put("device_id", cc.loadPrefString("device_id"));
                    params.put("login_with", "android");
                    params.put("gcm_id", msg);
                    params.put("login_by", "User");

                    Log.i("request login", params.toString());

                    return params;
                }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                try {
                    String mip_token = response.headers.get("mip-token");
                    Log.e("mip-token", mip_token);
                    cc.savePrefString("mip-token", mip_token);
                } catch (Exception e) {
                    Log.e("Error In Volly", e.toString());
                }

                return super.parseNetworkResponse(response);

            }

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");

    }

    private void jsonParseMatchList(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            pDialog.dismiss();
            if (jsonObject.getString("status").equals("200")) {

                JSONArray userdetail = jsonObject.getJSONArray("userdetail");
                JSONObject data = userdetail.getJSONObject(0);

                cc.savePrefString("name", data.getString("name"));
                cc.savePrefString("email", data.getString("email"));
                cc.savePrefString("gender", data.getString("gender"));
                cc.savePrefString("dob", data.getString("dob"));
                cc.savePrefString("avatar", data.getString("avatar"));
                cc.savePrefString("height", data.getString("height"));
                cc.savePrefString("weight", data.getString("weight"));
                cc.savePrefString("sigupfrom", data.getString("sigupfrom"));

                cc.savePrefBoolean("islogin", true);

                rcc.savePrefBoolean("ISLOGIN", true);

                db.inseartLogin(cc.loadPrefString("email"), et_password.getText().toString().trim(), rememberMe);
                /*if (!rcc.loadPrefBoolean("isKey")) {
                    Intent mIntent = new Intent(LoginScreen.this,
                            KeyText.class);
                    startActivity(mIntent);
                    finish();
                } else  {
                    Intent mIntent = new Intent(LoginScreen.this,
                            MainActivity.class);
                    startActivity(mIntent);
                    finish();

                }*/
                Intent mIntent = new Intent(LoginScreen.this,
                        Questionnair.class);
                startActivity(mIntent);
                finish();

               /* else if (cc.loadPrefBoolean("islogin") && rcc.loadPrefBoolean("isReminderFreq")) {
                    Intent mIntent = new Intent(LoginScreen.this,
                            MainActivity.class);
                    startActivity(mIntent);
                    finish();

                }*/

            } else {

                cc.showToast(jsonObject.getString("message"));
            }

        } catch (JSONException e) {
            //e.printStackTrace();
        }
    }


    //---------------------------------Forgot Password--------------------------------------------------

    private void makeJsonForgotPassword(final String email) {

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_forgotpassword,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("login", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            pDialog.dismiss();
                            if (jsonObject.getString("status").equals("200")) {

                                cc.showToast(jsonObject.getString("message"));

                            } else {

                                cc.showToast(jsonObject.getString("message"));
                            }

                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }
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
                params.put("email_id", email);

                Log.i("request login", params.toString());

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");

    }

    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }


}
