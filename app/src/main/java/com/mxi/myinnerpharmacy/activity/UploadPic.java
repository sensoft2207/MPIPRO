package com.mxi.myinnerpharmacy.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.model.ImageInfo;
import com.mxi.myinnerpharmacy.network.CommanClass;
import com.mxi.myinnerpharmacy.network.ImageFilePath;
import com.mxi.myinnerpharmacy.network.RegistrationCommanClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class UploadPic extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    ImageView iv_upload_image;
    TextView tv_choose_photo;
    LinearLayout ll_linear;
    TextView btn_next, btn_back;
//    ImageBitmapAdapter imageBitmapAdapter;
    private static final int SELECT_PICTURE = 1;
    private static final int RESULT_CROP = 2;
    private String selectedImagePath = "";
    long totalSize = 0;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 10;
    //Permision code that will be checked in the method onRequestPermissionsResult
    private int STORAGE_PERMISSION_CODE = 23;
    CommanClass cc;
   // String question_level, question_achive;
    RegistrationCommanClass rcc;
    ArrayList<ImageInfo> selectedImagesBitmapList;
   // RecyclerView rv_imageList;
    LinearLayoutManager llm;

    Uri selectedImageUri;

    private boolean zoomOut =  false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pic);
        cc = new CommanClass(UploadPic.this);
        rcc = new RegistrationCommanClass(UploadPic.this);

        iv_upload_image = (ImageView) findViewById(R.id.iv_upload_image);

        tv_choose_photo = (TextView) findViewById(R.id.tv_choose_photo);
        ll_linear = (LinearLayout) findViewById(R.id.ll_linear);
        btn_next = (TextView) findViewById(R.id.btn_next);
        btn_back = (TextView) findViewById(R.id.btn_back);
    //    rv_imageList = (RecyclerView) findViewById(R.id.rv_imageList);
        llm=new LinearLayoutManager(UploadPic.this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);

        try {
            //question_achive = getIntent().getStringExtra("question_achive");
           // question_level = getIntent().getStringExtra("question_level");
        } catch (Exception e) {
            e.printStackTrace();
        }

        tv_choose_photo.setOnTouchListener(this);
        btn_next.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        selectedImagesBitmapList =new ArrayList<ImageInfo>();
//        imageBitmapAdapter=new ImageBitmapAdapter(UploadPic.this,selectedImagesBitmapList);
//
//        rv_imageList.setAdapter(imageBitmapAdapter);
        //rv_imageList.setLayoutManager(llm);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
              /*  if (selectedImagePath.equals("")) {
                    cc.showSnackbar(ll_linear, getString(R.string.select_image));
                } else {

                    rcc.savePrefBoolean("isUploadPic", true);
                    startActivity(new Intent(UploadPic.this, AfterUploadPic.class).
                            putExtra("question_achive", question_achive).
                            putExtra("question_level", question_level)
                            .putExtra("achieve_image", selectedImagePath));
                    finish();
                }*/


                if (cc.loadPrefBoolean2("dialog")){

                    startActivity(new Intent(UploadPic.this, MainActivity.class));
                    finish();

                }else {


                    startActivity(new Intent(UploadPic.this, ReminderFrequency.class)


                            .putExtra("current_image", selectedImagePath));
                    finish();

                }
                       // putExtra("question_achive", question_achive).
//                        .putExtra("achieve_image", selectedImagePath));

                break;
            case R.id.btn_back:
                startActivity(new Intent(UploadPic.this, QuestionLevel.class));
                finish();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.tv_choose_photo:

                selectfile();

                break;

        }


        return false;
    }

    private void selectfile() {
        try {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, SELECT_PICTURE);


        } catch (Exception e) {
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == UploadPic.this.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                selectedImageUri = data.getData();

                Log.e("selectedImageUri", String.valueOf(selectedImageUri));

                try {
                    selectedImagePath = getPath(selectedImageUri);

                    performCrop(selectedImagePath );
//                    Log.e("Selected File", selectedImagePath);
//
//                    ExifInterface ei = null;
//                    Bitmap mybitmap = null;
//                    Bitmap retVal = null;
//                    try {
//                        ei = new ExifInterface(selectedImagePath);
//                        mybitmap = BitmapFactory.decodeFile(selectedImagePath);
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                    Matrix matrix = new Matrix();
//                    int orientation = ei.getAttributeInt(
//                            ExifInterface.TAG_ORIENTATION,
//                            ExifInterface.ORIENTATION_UNDEFINED);
//                    Log.e("Oriention", orientation + "");
//
//                    switch (orientation) {
//                        case ExifInterface.ORIENTATION_NORMAL:
//                            matrix.postRotate(0);
//                            retVal = Bitmap.createBitmap(mybitmap, 0, 0,
//                                    mybitmap.getWidth(), mybitmap.getHeight(),
//                                    matrix, true);
//                            break;
//
//                        case ExifInterface.ORIENTATION_ROTATE_90:
//
//                            matrix.postRotate(90);
//                            retVal = Bitmap.createBitmap(mybitmap, 0, 0,
//                                    mybitmap.getWidth(), mybitmap.getHeight(),
//                                    matrix, true);
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_180:
//
//                            matrix.postRotate(180);
//                            retVal = Bitmap.createBitmap(mybitmap, 0, 0,
//                                    mybitmap.getWidth(), mybitmap.getHeight(),
//                                    matrix, true);
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_270:
//
//                            matrix.postRotate(270);
//                            retVal = Bitmap.createBitmap(mybitmap, 0, 0,
//                                    mybitmap.getWidth(), mybitmap.getHeight(),
//                                    matrix, true);
//                            break;
//
//                    }
//
//                    File file = new File(selectedImagePath);
//                    long fileSizeInBytes = file.length();
//
//                    long fileSizeInKB = fileSizeInBytes / 1024;
//
//                    long fileSizeInMB = fileSizeInKB / 1024;
//
//                    if (fileSizeInMB > 10) {
//                        selectedImagePath = "";
//                        new AlertDialog.Builder(UploadPic.this)
//                                .setMessage("You can't upload more than 10 MB file")
//                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                })
//
//                                .show();
//                    } else {
//                        Uri uri = Uri.fromFile(new File(selectedImagePath));
//
//                        Glide.with(UploadPic.this)
//                                .load(uri)
//                                //.placeholder(R.drawable.ic_profile)
//                                .into(iv_upload_image);
//
//                        if (orientation != 0) {
//
//                            GenerateImage(retVal);
//
//                        } else {
//                            // new UploadFileToServer().execute();
//                        }
//
//                    }


                } catch (URISyntaxException e) {

                    e.printStackTrace();
                }
            }else if (requestCode == RESULT_CROP ) {
                if(resultCode == Activity.RESULT_OK){



                    Bitmap selectedBitmap = null;
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        selectedBitmap = (Bitmap) data.getExtras().get("data");
//                        selectedBitmap= BitmapFactory.decodeFile(path);
//                        Bundle extras = data.getExtras();
//                        selectedBitmap = extras.getParcelable("data");
                    }
                    else{
//                        Bundle extras = data.getExtras();
//                        selectedBitmap = extras.getParcelable("data");
                       // Uri selectedImageUri = data.getData();
                        String path= ImageFilePath.getPath(UploadPic.this, selectedImageUri);
                        selectedBitmap= BitmapFactory.decodeFile(path);

                    }

                    // Set The Bitmap Data To ImageView
                    iv_upload_image.setImageBitmap(selectedBitmap);
                    ImageInfo imageInfo=new ImageInfo();
                    imageInfo.setImageBitmap(selectedBitmap);
                    imageInfo.setImageUrl(selectedImagePath);
                    selectedImagesBitmapList.add(imageInfo);
                    //imageBitmapAdapter.notifyDataSetChanged();
                    iv_upload_image.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }



        }
    }


    private void performCrop(String picUri) {
        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, RESULT_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public String getPath(Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = UploadPic.this.getContentResolver().query(uri,
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

    private void GenerateImage(Bitmap bm) {

        OutputStream fOut = null;
        Uri outputFileUri;
        try {
            File path = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File file = new File(path, "MIP.jpg");
            outputFileUri = Uri.fromFile(file);
            fOut = new FileOutputStream(file);
        } catch (Exception e) {

        }
        try {
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
        }

        File path = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(path, "MIP.jpg");
        selectedImagePath = file.toString();

        //new UploadFileToServer().execute();
    }

    private boolean isReadStorageAllowed() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Displaying a toast
                // Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                // Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }




//    public class ImageBitmapAdapter extends RecyclerView.Adapter<ImageBitmapAdapter.CustomViewHolder> {
//
//        private Context context;
//        private ArrayList<ImageInfo> arrayList;
//
//        public ImageBitmapAdapter(Context context, ArrayList<ImageInfo> list) {
//            this.context = context;
//            arrayList = ((ArrayList) list);
//
//        }
//
//
//        @Override
//        public int getItemViewType(int position) {
//            return position;
//        }
//
//        @Override
//        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//
//            View view = LayoutInflater.
//                    from(viewGroup.getContext()).
//                    inflate(R.layout.raw_image_selector, viewGroup, false);
//
//            CustomViewHolder viewHolder = new CustomViewHolder(view);
//            return viewHolder;
//        }
//
//        @Override
//        public void onBindViewHolder(final CustomViewHolder holder, final int i) {
//
//            ImageInfo imageInfo= arrayList.get(i);
//
//            holder.iv_selection.setImageBitmap(imageInfo.getImageBitmap());
//
//            holder.iv_selection.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ImageInfo infoImage= arrayList.get(i);
//                    iv_upload_image.setImageBitmap(infoImage.getImageBitmap());
//                    selectedImagePath=infoImage.getImageUrl();
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return (null != arrayList ? arrayList.size() : 0);
//        }
//
//        public class CustomViewHolder extends RecyclerView.ViewHolder {
//
//            ImageView iv_selection;
//
//
//            public CustomViewHolder(View convertView) {
//                super(convertView);
//
//                iv_selection = (ImageView) convertView.findViewById(R.id.iv_selector);
//
//            }
//        }
//    }


}
