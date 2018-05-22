package com.mxi.myinnerpharmacy.heartrate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.activity.BreathTimerActivity;
import com.mxi.myinnerpharmacy.activity.HeartRate;
import com.mxi.myinnerpharmacy.activity.MainActivity;
import com.mxi.myinnerpharmacy.activity.PrescriptionDetail;
import com.mxi.myinnerpharmacy.activity.StateCalibration;
import com.mxi.myinnerpharmacy.database.SQLiteTD;
import com.mxi.myinnerpharmacy.network.AppController;
import com.mxi.myinnerpharmacy.network.CommanClass;
import com.mxi.myinnerpharmacy.network.URL;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


public class HeartRateMonitor extends AppCompatActivity {

    private static final String TAG = "HeartRateMonitor";
    private static final AtomicBoolean processing = new AtomicBoolean(false);
    private static SeekBar seekBar;
    private static SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private static View image = null;
    private static TextView text = null;
    private static String beatsPerMinuteValue = "";
    private static WakeLock wakeLock = null;
    TextView tv_title;
    private static TextView mTxtVwStopWatch;
    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];
    public static CountDownTimer timer;

    private static Context parentReference = null;

    public static SQLiteTD db;

    public static enum TYPE {
        GREEN, RED
    }

    private static TYPE currentType = TYPE.GREEN;

    public static TYPE getCurrent() {
        return currentType;
    }

    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    private static double beats = 0;
    private static long startTime = 0;
    private static Vibrator v;

    static AlertDialog alert;
     static ProgressDialog pDialog;

    static CommanClass cc;


    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.heart_rate);
        db = new SQLiteTD(HeartRateMonitor.this);

        cc = new CommanClass(this);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        parentReference = this;

        preview = (SurfaceView) findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        mTxtVwStopWatch = (TextView) findViewById(R.id.txtvwStopWatch);
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        image = findViewById(R.id.image);
        text = (TextView) findViewById(R.id.text);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(Html.fromHtml("<u>" + getResources().getString(R.string.heart_text) + "</u>"));

        mTxtVwStopWatch.setVisibility(View.VISIBLE);

        prepareCountDownTimer();

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        wakeLock.acquire();
        camera = Camera.open();
        startTime = System.currentTimeMillis();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        text.setText("---");
        camera = null;
    }

    private static PreviewCallback previewCallback = new PreviewCallback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();

            if (!processing.compareAndSet(false, true)) return;

            int width = size.width;
            int height = size.height;

            int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width);
            //   Log.e("hand_press", imgAvg + "");
            // Log.i(TAG, "imgAvg="+

            if (imgAvg == 0 || imgAvg == 255) {
                processing.set(false);
                return;
            }

            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int i = 0; i < averageArray.length; i++) {
                if (averageArray[i] > 0) {
                    averageArrayAvg += averageArray[i];
                    averageArrayCnt++;
                }
            }

            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
            TYPE newType = currentType;
            if (imgAvg >= 200) {
                mTxtVwStopWatch.setText("DETECTING PULSE..");
                if (imgAvg < rollingAverage) {
                    newType = TYPE.RED;
                    if (newType != currentType) {
                        beats++;
                        // Log.d(TAG, "BEAT!! beats="+beats);
                    }
                } else if (imgAvg > rollingAverage) {
                    newType = TYPE.GREEN;
                }
            } else {
                mTxtVwStopWatch.setText("PLACE FINGER ON CAMERA LENS");
                newType = TYPE.RED;
            }
            if (averageIndex == averageArraySize) averageIndex = 0;
            averageArray[averageIndex] = imgAvg;
            averageIndex++;

            // Transitioned from one state to another to the same
            if (newType != currentType) {
                currentType = newType;
                image.postInvalidate();
            }

            if (imgAvg >= 200) {
                //  timer.start();
                long endTime = System.currentTimeMillis();
                double totalTimeInSecs = (endTime - startTime) / 1000d;
                if (totalTimeInSecs >= 10) {
                    double bps = (beats / totalTimeInSecs);
                    int dpm = (int) (bps * 60d);
                    if (dpm < 30 || dpm > 180) {
                        startTime = System.currentTimeMillis();
                        beats = 0;
                        processing.set(false);
                        return;
                    }

                    if (beatsIndex == beatsArraySize) beatsIndex = 0;
                    beatsArray[beatsIndex] = dpm;
                    beatsIndex++;

                    int beatsArrayAvg = 0;
                    int beatsArrayCnt = 0;
                    for (int i = 0; i < beatsArray.length; i++) {
                        if (beatsArray[i] > 0) {
                            beatsArrayAvg += beatsArray[i];
                            beatsArrayCnt++;
                        }
                    }
                    int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
                    text.setText(String.valueOf(beatsAvg));
                    seekBar.setProgress(beatsAvg);
                    beatsPerMinuteValue = String.valueOf(beatsAvg);
                    makePhoneVibrate();
//                dispatchPubNubEvent(String.valueOf(beatsAvg));
                    mTxtVwStopWatch.setVisibility(View.GONE);
                    showReadingCompleteDialog();
                    startTime = System.currentTimeMillis();
                    beats = 0;
                }
            }
            processing.set(false);
        }
    };


    private static void makePhoneVibrate() {
        v.vibrate(500);
    }

    private static SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                Log.e("surfaceCallback", "Exception in setPreviewDisplay()", t);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            List<String> supportedFlashModes = parameters.getSupportedFlashModes();
            if (supportedFlashModes == null || supportedFlashModes.isEmpty() || supportedFlashModes.size() == 1 && supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF)) {

                new AlertDialog.Builder(parentReference).setTitle("HeartRate")
                        .setMessage("Sorry! Due to unavailability of Flash light you are not allowed to use this functionality.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                               /* if (MainActivity.isSkip) {
                                    parentReference.startActivity(new Intent(parentReference, PrescriptionDetail.class));
                                    ((Activity) parentReference).finish();
                                } else {
                                    parentReference.startActivity(new Intent(parentReference, HeartRate.class));
                                    ((Activity) parentReference).finish();
                                }*/
                                dialog.dismiss();
                            }
                        })
                        .show();
            } else {
                Camera.Size size = getSmallestPreviewSize(width, height, parameters);
                if (size != null) {
                    parameters.setPreviewSize(size.width, size.height);
                    Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
                }
                camera.setParameters(parameters);
                camera.startPreview();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // Ignore
        }
    };

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
    }

    private static void prepareCountDownTimer() {
        mTxtVwStopWatch.setText("---");
        timer = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                // mTxtVwStopWatch.setText("seconds remaining: " + (millisUntilFinished) / 1000);
            }

            public void onFinish() {
                // mTxtVwStopWatch.setText("done!");
            }
        }.start();
    }


    private static void showReadingCompleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(parentReference);
        builder.setTitle("HeartRate");
        builder.setMessage("Reading taken Successfully at- " + beatsPerMinuteValue + " beats per minute.")
                .setCancelable(false)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        heartBitPostJsonWS(beatsPerMinuteValue);

                        cc.savePrefString("heartrate_data",beatsPerMinuteValue);


                    }
                });
/*                .setNegativeButton("Take Another", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        text.setText("---");
                        prepareCountDownTimer();
                        dialog.cancel();
                    }
                });*/
        alert = builder.create();
        alert.show();
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

//        Log.e("db_heart_rate", currentDateTimeString + "/" + beatsPerMinuteValue);
        Log.e("db_heart_rate",beatsPerMinuteValue);



       // db.inseartHeartRateData(currentDateTimeString, beatsPerMinuteValue);
    }

    private static void heartBitPostJsonWS(final String beatsPerMinuteValue) {


        final ProgressDialog  pDialog = new ProgressDialog(parentReference);
        pDialog.setMessage("Please wait...");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_user_activity,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("Url_userrate_activity", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("200")) {


                                if (MainActivity.isSkip) {

                                    parentReference.startActivity(new Intent(parentReference, StateCalibration.class));
                                    ((Activity) parentReference).finish();
                                } else {


                                    parentReference.startActivity(new Intent(parentReference, HeartRate.class));
                                    ((Activity) parentReference).finish();
                                }

//                                cc.showToast("Heart Rate Added Successfully");


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
                cc.showToast("Oops! something went wrong");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", cc.loadPrefString("mip-token"));
                params.put("activity_name","HEARTRATE");
                params.put("value", beatsPerMinuteValue);
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
    public void onBackPressed() {

        /*if (!MainActivity.isSkip) {

            startActivity(new Intent(HeartRateMonitor.this, HeartRate.class));
        } else {

        }*/

        finish();
    }
}

