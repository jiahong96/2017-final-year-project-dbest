package com.developer.cheahhong.dbest;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

/**
 * Base class for Services that keep track of the number of active jobs and self-stop when the
 * count is zero.
 */
public abstract class MyBaseTaskService extends Service {

    static final int PROGRESS_NOTIFICATION_ID = 0;
    static final int FINISHED_NOTIFICATION_ID = 1;

    private static final String TAG = "MyBaseTaskService";
    private int mNumTasks = 0;

    public void taskStarted() {
        changeNumberOfTasks(1);
    }

    public void taskCompleted() {
        changeNumberOfTasks(-1);
    }

    private synchronized void changeNumberOfTasks(int delta) {
        //Log.d(TAG, "changeNumberOfTasks:" + mNumTasks + ":" + delta);
        mNumTasks += delta;

        // If there are no tasks left, stop the service
        if (mNumTasks <= 0) {
            //Log.d(TAG, "stopping");
            stopSelf();
        }
    }

    /**
     * Show notification with a progress bar.
     */
    protected void showProgressNotification(String caption, long completedUnits, long totalUnits) {
        int percentComplete = 0;
        if (totalUnits > 0) {
            percentComplete = (int) (100 * completedUnits / totalUnits);
        }

//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.ic_check_white)
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText(caption)
//                .setProgress(100, percentComplete, false)
//                .setOngoing(true)
//                .setAutoCancel(false);
//
//        NotificationManager manager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        manager.notify(PROGRESS_NOTIFICATION_ID, builder.build());
    }

    /**
     * Show notification that the activity finished.
     */
    protected void showFinishedNotification(String caption, Intent intent, boolean success) {
        // Make PendingIntent for notification
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* requestCode */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //int icon = success ? R.drawable.ic_check_white : R.drawable.ic_check_white;

        //NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
        //        .setSmallIcon(icon)
        //        .setContentTitle(getString(R.string.app_name))
        //        .setContentText(caption)
        //        .setAutoCancel(true)
        //        .setContentIntent(pendingIntent);

        //NotificationManager manager =
        //        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //manager.notify(FINISHED_NOTIFICATION_ID, builder.build());
    }

    /**
     * Dismiss the progress notification.
     */
    protected void dismissProgressNotification() {
        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.cancel(PROGRESS_NOTIFICATION_ID);
    }
}
