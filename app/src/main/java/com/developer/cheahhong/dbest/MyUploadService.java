package com.developer.cheahhong.dbest;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Service to handle uploading files to Firebase Storage.
 */
public class MyUploadService extends MyBaseTaskService {

    private static final String TAG = "MyUploadService";
    String imageUri;
    String inquiryID;
    /** Intent Actions **/
    public static final String ACTION_UPLOAD_BEARING = "action_upload_bearing";
    public static final String ACTION_UPLOAD = "action_upload";
    public static final String UPLOAD_COMPLETED = "upload_completed";
    public static final String UPLOAD_ERROR = "upload_error";

    /** Intent Extras **/
    public static final String EXTRA_BEARING_POSITION = "extra_bearing_position";
    public static final String EXTRA_FILE_URI = "extra_file_uri";
    public static final String EXTRA_INQ_ID = "extra_inq_id";
    public static final String EXTRA_DOWNLOAD_URL = "extra_download_url";

    // [START declare_ref]
    private StorageReference mStorageRef;
    // [END declare_ref]

    @Override
    public void onCreate() {
        super.onCreate();

        // [START get_storage_ref]
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // [END get_storage_ref]
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.d(TAG, "onStartCommand:" + intent + ":" + startId);

        if (ACTION_UPLOAD_BEARING.equals(intent.getAction())) {
            //Uri fileUri = intent.getParcelableExtra(EXTRA_FILE_URI);
            int position = intent.getIntExtra(EXTRA_BEARING_POSITION,0);
            //Log.d("serviceStartPosition", String.valueOf(position));
            imageUri = intent.getStringExtra(EXTRA_FILE_URI);
            uploadFromUri(position);
        }else if(ACTION_UPLOAD.equals(intent.getAction())){
            imageUri = intent.getStringExtra(EXTRA_FILE_URI);
            inquiryID = intent.getStringExtra(EXTRA_INQ_ID);
            uploadFromUri(1000);
        }

        return START_REDELIVER_INTENT;
    }

    // [START upload_from_uri]
    private void uploadFromUri(final int bearingPosition) {
        ////Log.d(TAG, "uploadFromUri:src:" + imageUri);

        // [START_EXCLUDE]
        taskStarted();
        // showProgressNotification(getString(R.string.progress_uploading), 0, 0);
        // [END_EXCLUDE]


        // [START get_child_ref]
        StorageReference photoRef = mStorageRef.child("photos")
                .child(Uri.parse(imageUri).getLastPathSegment());
        // Get a reference to store file at photos/<FILENAME>.jpg
        if(bearingPosition==1000){
            photoRef = mStorageRef.child("photos").child(inquiryID)
                    .child(Uri.parse(imageUri).getLastPathSegment());
        }

        // [END get_child_ref]

        // Upload file to Firebase Storage
        ////Log.d(TAG, "uploadFromUri:dst:" + photoRef.getPath());
//        addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                showProgressNotification(getString(R.string.progress_uploading),
//                        taskSnapshot.getBytesTransferred(),
//                        taskSnapshot.getTotalByteCount());
//            }
//        })
        photoRef.putFile(Uri.parse(imageUri))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Upload succeeded
                        ////Log.d(TAG, "uploadFromUri:onSuccess");

                        // Get the public download URL
                        Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                        //Log.d("onSuccessDownloadUri",downloadUri.toString());
                        //Log.d("onSuccessPosition", String.valueOf(bearingPosition));
                        // [START_EXCLUDE]
                        broadcastUploadFinished(downloadUri, Uri.parse(imageUri),bearingPosition);
                        showUploadFinishedNotification(downloadUri, Uri.parse(imageUri));
                        taskCompleted();
                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        Log.w(TAG, "uploadFromUri:onFailure", exception);

                        // [START_EXCLUDE]
                        broadcastUploadFinished(null, Uri.parse(imageUri),bearingPosition);
                        showUploadFinishedNotification(null, Uri.parse(imageUri));
                        taskCompleted();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END upload_from_uri]

    /**
     * Broadcast finished upload (success or failure).
     * @return true if a running receiver received the broadcast.
     */
    private boolean broadcastUploadFinished(@Nullable Uri downloadUrl, @Nullable Uri fileUri,@Nullable int bPosition) {
        boolean success = downloadUrl != null;

        //Log.d("finishBearingPosition", String.valueOf(bPosition));
        String action = success ? UPLOAD_COMPLETED : UPLOAD_ERROR;
        Intent broadcast = new Intent(action)
                .putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(EXTRA_FILE_URI, fileUri)
                .putExtra(EXTRA_BEARING_POSITION,bPosition);
        return LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(broadcast);
    }

    /**
     * Show a notification for a finished upload.
     */
    private void showUploadFinishedNotification(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
        // Hide the progress notification
        dismissProgressNotification();

        // Make Intent to MainActivity
        Intent intent = new Intent(this, MainActivity.class)
                .putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(EXTRA_FILE_URI, fileUri)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        boolean success = downloadUrl != null;
        String caption = success ? getString(R.string.upload_success) : getString(R.string.upload_failure);
        //showFinishedNotification(caption, intent, success);
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPLOAD_COMPLETED);
        filter.addAction(UPLOAD_ERROR);

        return filter;
    }

}
