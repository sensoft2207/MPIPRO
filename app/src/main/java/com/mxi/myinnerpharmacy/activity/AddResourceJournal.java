package com.mxi.myinnerpharmacy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.model.PrescriptionDetails;
import com.mxi.myinnerpharmacy.network.AndroidMultiPartEntity;
import com.mxi.myinnerpharmacy.network.AppController;
import com.mxi.myinnerpharmacy.network.CommanClass;
import com.mxi.myinnerpharmacy.network.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddResourceJournal extends AppCompatActivity implements View.OnTouchListener {

    EditText et_title, et_add_description;
    String myDead_subcategory="";
    TextView tv_add_file, tv_upload, tv_header,tv_sub_cat;
    View bottom_view;
    CommanClass cc;
    LinearLayout ll_linear,ll_audio_main;
    Toolbar toolbar;
    private static final int PICKFILE_RESULT_CODE = 2;
    private String selectedPath = "";
    TextView tv_recording;

    long totalSize = 0;
    ProgressDialog pDialog;
    boolean fromMyDeads = false;
    public static String outputFile = "";


    public static ArrayList<PrescriptionDetails> audioPrescriptionList;
    public static ArrayList<PrescriptionDetails> textPrescriptionList;
    public static ArrayList<PrescriptionDetails> videoPrescriptionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resource_journal);
        cc = new CommanClass(AddResourceJournal.this);

        String intentString = getIntent().getStringExtra("from");

        bottom_view = (View) findViewById(R.id.view_bottom_last);
        et_title = (EditText) findViewById(R.id.et_title_add_resource_journal);
        et_add_description = (EditText) findViewById(R.id.et_description_add_resource_journal);
        tv_add_file = (TextView) findViewById(R.id.tv_add_file);
        tv_header = (TextView) findViewById(R.id.tv_lable_add_resource_journal);
        tv_upload = (TextView) findViewById(R.id.tv_upload);
        tv_sub_cat= (TextView) findViewById(R.id.tv_sub_cat);
        ll_linear = (LinearLayout) findViewById(R.id.ll_linear);
        ll_audio_main = (LinearLayout) findViewById(R.id.ll_audio_main);
        tv_recording = (TextView) findViewById(R.id.tv_recording);


        if (intentString.equals("MyDeads")) {
            tv_sub_cat.setVisibility(View.VISIBLE);
            myDead_subcategory=getIntent().getStringExtra("MyDead_cat");
            fromMyDeads = true;
            tv_add_file.setVisibility(View.GONE);
            tv_header.setText("Add My Self Talk Topics");


            if (cc.loadPrefString("et_problem").isEmpty()){
                et_title.setHint("About Who or What?");
            }else {
                et_title.setText(cc.loadPrefString("et_problem"));
                cc.savePrefString("et_problem","");
            }

            et_add_description.setHint("Describe Specifics of What You Did and How");
            bottom_view.setVisibility(View.GONE);
            ll_audio_main.setVisibility(View.GONE);
            tv_sub_cat.setText("Enter your "+myDead_subcategory);
        }

        tv_add_file.setOnTouchListener(this);
        tv_upload.setOnTouchListener(this);
        tv_recording.setOnTouchListener(this);

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            //if (!outputFile.equals(""))
            if (cc.loadPrefBoolean("audioReco"))
                tv_recording.setText(outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (v.getId()) {
            case R.id.tv_upload:
                Log.e("outputFile", outputFile);
                String title = et_title.getText().toString().trim();
                String des = et_add_description.getText().toString().trim();
                if (!cc.isConnectingToInternet()) {
                    cc.showSnackbar(ll_linear, getString(R.string.no_internet));
                } else if (title.equals("")) {
                    cc.showSnackbar(ll_linear, getString(R.string.enter_title));
                } else if (des.equals("")) {
                    cc.showSnackbar(ll_linear, getString(R.string.enter_desc));
                }  else {
                    if (!selectedPath.equals("")) {
                        outputFile = "";
                    } else if (!outputFile.equals("")) {
                        selectedPath = "";
                    }
                    new UploadFileToServer(title, des).execute();
                    //cc.showSnackbar(ll_linear,"complete");
                }
                break;

            case R.id.tv_add_file:

                selectfile();

                break;
            case R.id.tv_recording:

                startActivity(new Intent(AddResourceJournal.this, AudioRecording.class));
                break;
        }

        return false;
    }

    private void selectfile() {
        try {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, PICKFILE_RESULT_CODE);


        } catch (Exception e) {
        }
    }

    public String getPath(Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = AddResourceJournal.this.getContentResolver().query(uri,
                        projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == AddResourceJournal.this.RESULT_OK) {
            if (requestCode == PICKFILE_RESULT_CODE) {
                Uri selectedUri = data.getData();

                try {
                    selectedPath = getPath(selectedUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                Log.e("Selected File", selectedPath);
                tv_add_file.setText(selectedPath);

                   /* File file = new File(selectedPath);
                    long fileSizeInBytes = file.length();

                    long fileSizeInKB = fileSizeInBytes / 1024;

                    long fileSizeInMB = fileSizeInKB / 1024;*/

                    /*if (fileSizeInMB > 10) {
                        selectedPath = "";
                        new AlertDialog.Builder(AddResourceJournal.this)
                                .setMessage("You can't upload more than 10 MB file")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })

                                .show();
                    } else {
                        // Uri uri = Uri.fromFile(new File(selectedImagePath));

                        tv_add_file.setText(selectedPath);

                    }*/

            }
        }
    }


    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        HttpClient httpclient;
        HttpPost httppost;
        String title, des;

        public UploadFileToServer(String title, String des) {
            this.title = title;
            this.des = des;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddResourceJournal.this);
            pDialog.show();
            pDialog.setCancelable(false);

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            pDialog.setMessage(String.valueOf("Loading..." + progress[0])
                    + " %");

        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            httpclient = new DefaultHttpClient();
            if (fromMyDeads) {
                httppost = new HttpPost(URL.Url_add_my_dead);
            } else {
                httppost = new HttpPost(URL.Url_add_resource_journal);
            }

            httppost.addHeader("mip-token", cc.loadPrefString("mip-token"));

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                if (fromMyDeads) {

                    entity.addPart("my_dead_title", new StringBody(title));
                    entity.addPart("my_dead_text", new StringBody(des));

                } else {

                    entity.addPart("resorce_journal_title", new StringBody(title));
                    entity.addPart("resorce_journal_text", new StringBody(des));

                    if (!selectedPath.equals("")) {
                        File sourceMusicFile = new File(selectedPath);
                        entity.addPart("resorce_journal_audio", new FileBody(sourceMusicFile));
                    } else if (!outputFile.equals("")) {
                        File sourceAudioFile = new File(outputFile);
                        entity.addPart("resorce_journal_audio", new FileBody(sourceAudioFile));

                    }

                }
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                    return responseString;
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("Register: result", "Response from server: " + result);
            try {
                pDialog.dismiss();
                JSONObject jObject = new JSONObject(result);
                outputFile = "";
                if (jObject.getString("status").equals("200")) {

                    cc.savePrefString("self_talk_data",jObject.getString("userid"));

//                    cc.showToast(jObject.getString("message"));
//                    finish();

                    if (cc.loadPrefString("menu_current_self").equals("mcs")){

                        startActivity(new Intent(AddResourceJournal.this, ResourceJournal.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                        cc.savePrefString("menu_current_self","");

                    }else {

                        if (cc.loadPrefString("mydead_back").equals("mydead")){

                            startActivity(new Intent(AddResourceJournal.this, MyDeads.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                            cc.savePrefString("mydead_back","");

                        }else {

                            startActivity(new Intent(AddResourceJournal.this, CurrentSelfTaskLast.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }
                    }

//                    getPrescriptionBottle();


                } else {
                    cc.showToast(jObject.getString("message"));
                }

            } catch (JSONException e) {
                Log.e("Error : Exception", e.getMessage());
            }
        }

    }

   /* private void getPrescriptionBottle() {

        pDialog = new ProgressDialog(AddResourceJournal.this);
        pDialog.setMessage("Please wait...");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_get_prescription_bottles,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("Url_get_pre_bottle", response);
                        *//*try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("200")) {

                                cc.showToast(jsonObject.getString("message"));



                            } else {

                                cc.showToast(jsonObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*//*

                        jsonGetPrescriptionTask(response);

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


    private void jsonGetPrescriptionTask(String response) {
        try {
            audioPrescriptionList = new ArrayList<PrescriptionDetails>();
            videoPrescriptionList = new ArrayList<PrescriptionDetails>();
            textPrescriptionList = new ArrayList<PrescriptionDetails>();
            JSONObject jsonObject = new JSONObject(response);
            pDialog.dismiss();
            if (jsonObject.getString("status").equals("200")) {


                String audio_path = jsonObject.getString("prescription_audiopath");
                String video_path = jsonObject.getString("prescription_videopath");
                String text_path = jsonObject.getString("prescription_textpath");

                JSONArray prescription = jsonObject.getJSONArray("prescriptionBottles");

                for (int i = 0; i < prescription.length(); i++) {
                    JSONObject jsonObject1 = prescription.getJSONObject(i);

                    PrescriptionDetails pk = new PrescriptionDetails();
                    pk.setMedia_type(jsonObject1.getString("media_type"));
                    pk.setMedia_name(jsonObject1.getString("media_name"));
                    pk.setMedia_id(jsonObject1.getString("media_id"));

                    Log.e("media_type", jsonObject1.getString("media_type"));
                    String media = jsonObject1.getString("media_type");
                    if (media.equals("Audio")) {
                        // pk.setPrescription_audiopath(audio_path);
                        pk.setMedia_file(audio_path + jsonObject1.getString("media_file"));
                        audioPrescriptionList.add(pk);
                    } else if (media.equals("Video")) {
                        // pk.setPrescription_videopath(video_path);
                        pk.setMedia_file(video_path + jsonObject1.getString("media_file"));
                        videoPrescriptionList.add(pk);
                    } else if (media.equals("Text")) {
                        //pk.setPrescription_textpath(text_path);
                        pk.setMedia_file(text_path + jsonObject1.getString("media_file"));
                        textPrescriptionList.add(pk);
                    }

                }

                Log.e("audioPrescriptionList", audioPrescriptionList.size() + "");
                Log.e("videoPrescriptionList", videoPrescriptionList.size() + "");
                Log.e("textPrescriptionList", textPrescriptionList.size() + "");



            } else {

                cc.showToast(jsonObject.getString("message"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cc.savePrefString("menu_current_self","");
        cc.savePrefString("mydead_back","");
    }
}
