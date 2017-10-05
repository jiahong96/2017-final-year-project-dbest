package com.example.cheahhong.dbest;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class InquiryActivity extends AppCompatActivity {

    static  ArrayList<Item> itemList;
    private RecyclerView    recyclerView;
    private ItemAdapter     mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    View previousView;
    Utility utility;
    ImageProcessing imgProcessor;

    TextInputLayout lay_InqTitle;
    ImageView imgBearing,imgBearing2;
    EditText editTxtCode,editTxtHeight,editTxtComment,editTxtTitle;
    Button btnAddBearing,btnCreateBearing;

    private String selectedImagePath = "",captureImagePath="";
    private static final int RC_TAKE_PICTURE = 101,RC_CAPTURE_PICTURE = 102;
    int count,count2,savedPosition,errorCount;
    private static final String KEY_FILE_URI = "key_file_uri";
    private static final String KEY_DOWNLOAD_URL = "key_download_url";

    private BroadcastReceiver mBroadcastReceiver;
    ProgressDialog mProgressDialog;

    private Uri imgFileUri = null,imgDownloadUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("InquiryForm");
        }

        count=0;
        count2=0;

        utility = new Utility();
        imgProcessor = new ImageProcessing();

        itemList = new ArrayList<>();
        itemList.add(new Item());
        lay_InqTitle = (TextInputLayout) findViewById(R.id.inqLayout);
        btnAddBearing = (Button) findViewById(R.id.btn_add);
        btnCreateBearing = (Button) findViewById(R.id.btn_create);
        editTxtTitle = (EditText) findViewById(R.id.inquiryTitle);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new ItemAdapter(itemList);

        recyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        // Restore instance state
        if (savedInstanceState != null) {
            Log.d("savedInstance?", "yes");
            imgFileUri = savedInstanceState.getParcelable(KEY_FILE_URI);
            imgDownloadUri = savedInstanceState.getParcelable(KEY_DOWNLOAD_URL);
            savedPosition = savedInstanceState.getInt("position");
            count = savedInstanceState.getInt("count1");
            count2 = savedInstanceState.getInt("count2");
        }
        onNewIntent(getIntent());

        // Local broadcast receiver
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("InquiryActivity", "onReceive:" + intent);

                switch (intent.getAction()) {
                    case MyUploadService.UPLOAD_COMPLETED:
                        //imgDownloadUri = intent.getParcelableExtra(MyUploadService.EXTRA_DOWNLOAD_URL);
                    case MyUploadService.UPLOAD_ERROR:
                        onUploadResultIntent(intent);
                        break;
                }
            }
        };

        editTxtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void afterTextChanged(Editable edt) {
                if (editTxtTitle.getText().length() > 0) {
                    lay_InqTitle.setError(null);
                    lay_InqTitle.setErrorEnabled(false);
                }
            }
        });

        btnAddBearing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorCount = 0;

                validateBearings();

                if(errorCount == 0) {
                    if(itemList.size()<5){
                        itemList.add(itemList.size(),new Item());
                        mAdapter.notifyItemInserted(itemList.size()-1);
                    }else{
                        Toast.makeText(InquiryActivity.this, "Cannot exceed maximum amount of 5 bearings", Toast.LENGTH_LONG).show();
                    }
                }

                Log.d("bearingSize", String.valueOf(itemList.size()));
            }
        });

        btnCreateBearing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorCount = 0;

                if(!validate()){
                    errorCount++;
                }else{
                    validateBearings();
                }

                if(errorCount == 0){
                    uploadFromUri();
                }
            }
        });

        mAdapter.setOnItemClickListener(new ItemAdapter.ClickListener() {
            @Override
            public void onTakeImgClick(Item item, View v, int position) {
                savedPosition = position;
                Log.d("Takepicposition", String.valueOf(savedPosition));
                takePicture();
            }

            @Override
            public void onSelectImgClick(Item item, View v, int position) {
                savedPosition = position;
                Log.d("selectPIcposition", String.valueOf(savedPosition));
                launchPhotoStorage();
            }

            @Override
            public void onViewLongClick(View v, int position) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to cancel this inquiry?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        InquiryActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        Log.d("OnSave","yes");
        out.putParcelable(KEY_FILE_URI, imgFileUri);
        out.putParcelable(KEY_DOWNLOAD_URL, imgDownloadUri);
        out.putInt("position", savedPosition);
        out.putInt("count1",count);
        out.putInt("count2",count2);
    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Check if this Activity was launched by clicking on an upload notification
        if (intent.hasExtra(MyUploadService.EXTRA_DOWNLOAD_URL)) {
            onUploadResultIntent(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Register receiver for uploads and downloads
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mBroadcastReceiver, MyUploadService.getIntentFilter());
    }

    @Override
    public void onStop() {
        super.onStop();

        // Unregister download receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_TAKE_PICTURE) {
            Log.d("PhotoSelected", "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
            if (resultCode == RESULT_OK) {
                imgFileUri = data.getData();
                imgBearing = (ImageView)mLayoutManager.findViewByPosition(savedPosition).findViewById(R.id.imgView);
                //hiddenImgBearing = (ImageView)mLayoutManager.findViewByPosition(savedPosition).findViewById(R.id.hiddenImgView);
                Log.d("PhotoSelected", "resultok");
                Log.d("savedBearingPosition", String.valueOf(savedPosition));
                if (imgFileUri != null) {
                    itemList.get(savedPosition).setImageFileUri(imgFileUri.toString());
                    selectedImagePath = getAbsolutePath(imgFileUri);
                    itemList.get(savedPosition).setImageFileUrl(selectedImagePath);
                    try {
                        imgBearing.setImageBitmap(imgProcessor.handleSamplingAndRotationBitmap(this,imgFileUri,selectedImagePath));
                        //hiddenImgBearing.setImageBitmap(handleSamplingAndRotationBitmap(this,imgFileUri,selectedImagePath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.w("PhotoFileNull", "File URI is null");
                }
            } else {
                Toast.makeText(this, "No picture selected", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == RC_CAPTURE_PICTURE){
            Log.d("PhotoCaptured", "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
            if (resultCode == RESULT_OK) {
                Log.d("PhotoCaptured", "resultok");
                Log.d("savedBearingPosition", String.valueOf(savedPosition));

                imgBearing = (ImageView)mLayoutManager.findViewByPosition(savedPosition).findViewById(R.id.imgView);
                //hiddenImgBearing = (ImageView)mLayoutManager.findViewByPosition(savedPosition).findViewById(R.id.hiddenImgView);
                if (imgFileUri != null) {
                    itemList.get(savedPosition).setImageFileUri(imgFileUri.toString());
                    itemList.get(savedPosition).setImageFileUrl(captureImagePath);
                    Log.d("imgFIleURI", String.valueOf(imgFileUri));
                    try {
                        imgBearing.setImageBitmap(imgProcessor.handleSamplingAndRotationBitmap(this,imgFileUri,captureImagePath));
                        //hiddenImgBearing.setImageBitmap(handleSamplingAndRotationBitmap(this,imgFileUri,captureImagePath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.w("PhotoFileNull", "File URI is null");
                }
            } else {
                Toast.makeText(this, "No picture captured", Toast.LENGTH_SHORT).show();
                Log.d("PhotoCaptured", captureImagePath);
                File file = new File(captureImagePath);
                boolean deleted = file.delete();
                Log.d("PhotoDeleted", String.valueOf(deleted));
            }
        }
    }

    private void launchPhotoStorage() {
        Log.d("PhotoStorage", "launch");

        // Pick an image from storage
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, RC_TAKE_PICTURE);
    }

    private void takePicture() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(InquiryActivity.this,  new String[]{Manifest.permission.CAMERA}, RC_CAPTURE_PICTURE);
        }else{

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                Log.d("camera?","yes");
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    Log.d("created","yes");
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Log.d("created","yes");
                    imgFileUri = Uri.fromFile(photoFile);
                    Log.d("imgFIleURI", String.valueOf(imgFileUri));
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, RC_CAPTURE_PICTURE);
                }
            }
        }
    }

    public boolean validate() {
        Boolean valid = true;

        String inquiry_title = editTxtTitle.getText().toString();
        if (inquiry_title.isEmpty()) {
            lay_InqTitle.setErrorEnabled(true);
            lay_InqTitle.setError("Inquiry title must not be empty");
            valid = false;
        }else if(inquiry_title.length()>18){
            lay_InqTitle.setErrorEnabled(true);
            lay_InqTitle.setError("between 0 to 18 characters");
            valid = false;
        }

        return valid;
    }

    public void validateBearings(){
        for(int i=0;i<itemList.size();i++){

            previousView = mLayoutManager.findViewByPosition(i);
 
            editTxtCode = (EditText)previousView.findViewById(R.id.code);
            editTxtHeight = (EditText)previousView.findViewById(R.id.quantity);
            //editTxtDiameterI = (EditText)previousView.findViewById(R.id.diameterI);
            //editTxtDiameterO = (EditText)previousView.findViewById(R.id.diameterO);
            editTxtComment = (EditText)previousView.findViewById(R.id.extraComment);

            imgBearing2 = (ImageView)previousView.findViewById(R.id.imgView);

            if(editTxtHeight.getText().toString().equals("")){
                Toast.makeText(InquiryActivity.this, "Please provide item quantity at form "+(i+1), Toast.LENGTH_LONG).show();
                errorCount++;
                break;
            }else{
                if(editTxtCode.getText().toString().equals("") && imgBearing2.getDrawable() == null ){
                    Toast.makeText(InquiryActivity.this, "Please provide Serial No or Image at form "+(i+1), Toast.LENGTH_LONG).show();
                    errorCount++;
                    break;
                }else{
                    itemList.get(i).setItemName(editTxtCode.getText().toString());
                    itemList.get(i).setQuantity(editTxtHeight.getText().toString());
                    itemList.get(i).setExtraComment(editTxtComment.getText().toString());
                }
            }
        }
    }

    private void uploadFromUri() {
        //Log.d("MainActivity", "uploadFromUri:src:" + imgFileUri.toString());
        //imgDownloadUri = null;

        // Start MyUploadService to upload the file, so that the file is uploaded
        // even if this Activity is killed or put in the background
        for(int i=0;i<itemList.size();i++){
            previousView = mLayoutManager.findViewByPosition(i);
            imgBearing2 = (ImageView)previousView.findViewById(R.id.imgView);
            if(imgBearing2.getDrawable()!=null){
                if(!utility.isNetworkAvailable(InquiryActivity.this)){
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Show loading spinner
                showProgressDialog(getString(R.string.progress_uploading));
                btnAddBearing.setEnabled(false);
                btnCreateBearing.setEnabled(false);
                count++;
                startService(new Intent(this, MyUploadService.class)
                        .putExtra(MyUploadService.EXTRA_BEARING_POSITION, i)
                        .putExtra(MyUploadService.EXTRA_FILE_URI, itemList.get(i).getImageFileUri())
                        .setAction(MyUploadService.ACTION_UPLOAD_BEARING));
            }

        }

        if(count==0){
            setActivityResult();
        }
    }

    private void onUploadResultIntent(Intent intent) {
        // Got a new intent from MyUploadService with a success or failure
        Log.d("uploadSuccess", String.valueOf(intent.getIntExtra(MyUploadService.EXTRA_BEARING_POSITION,0)));
        imgDownloadUri = intent.getParcelableExtra(MyUploadService.EXTRA_DOWNLOAD_URL);
        itemList.get(intent.getIntExtra(MyUploadService.EXTRA_BEARING_POSITION,0)).setImageUrl(imgDownloadUri.toString());
        count2++;
        if(count==count2){
            Log.d("setActivityResult", "yes");
            hideProgressDialog();
            setActivityResult();
        }
        //imgFileUri = intent.getParcelableExtra(MyUploadService.EXTRA_FILE_URI);
    }

    private void setActivityResult (){
        //data return
        Log.d("bearingSizeSet", String.valueOf(itemList.size()));
        Intent returnIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("bearingList", itemList);
        returnIntent.putExtras(bundle);
        returnIntent.putExtra("inquiryTitle",editTxtTitle.getText().toString());

        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    private void showProgressDialog(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(InquiryActivity.this);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(caption);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        if(Build.VERSION.SDK_INT >= 23){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        captureImagePath =  image.getAbsolutePath();
        return image;
    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String result = cursor.getString(column_index);
            cursor.close();
            return result;
        } else
            return null;
    }
}