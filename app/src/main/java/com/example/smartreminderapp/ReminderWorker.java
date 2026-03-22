package com.example.smartreminderapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

/**
 * WorkManager Worker for background reminder checking
 * Roll No: 41
 * Last digit: 1 → Notification Message: "Reminder: Check your scheduled task"
 * 41 % 4 = 1 → Interval: 10 minutes
 */
public class ReminderWorker extends Worker {

    private static final String TAG = "ReminderWorker";
    private static final String CHANNEL_ID = "reminder_channel_41";
    private static final String CHANNEL_NAME = "Reminder Notifications";

    // Roll No 41, last digit = 1 (0-3 range)
    // Notification message: "Reminder: Check your scheduled task"
    private static final String NOTIFICATION_MESSAGE = "Reminder: Check your scheduled task";

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "ReminderWorker doWork() started");

        // Ensure notification channel exists
        createNotificationChannel();

        // Check if there are any reminders in the database
        ReminderDatabaseHelper dbHelper = new ReminderDatabaseHelper(getApplicationContext());
        List<Reminder> reminders = dbHelper.getAllReminders();

        Log.d(TAG, "Found " + reminders.size() + " reminder(s)");

        if (!reminders.isEmpty()) {
            // Check notification permission on Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.w(TAG, "POST_NOTIFICATIONS permission not granted, skipping notification");
                    return Result.success();
                }
            }

            // Send notification with the personalized message
            sendNotification(
                    NOTIFICATION_MESSAGE,
                    "You have " + reminders.size() + " reminder(s) stored. Tap to view."
            );
            Log.d(TAG, "Notification sent successfully");
        } else {
            Log.d(TAG, "No reminders found, skipping notification");
        }

        return Result.success();
    }

    /**
     * Create notification channel (idempotent - safe to call multiple times)
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for Smart Reminder App (Roll No 41)");
            channel.enableVibration(true);
            channel.setShowBadge(true);

            NotificationManager notificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Create and send a notification
     */
    private void sendNotification(String title, String message) {
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        // Use System.currentTimeMillis() as notification ID so each notification is unique
        int notificationId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        notificationManager.notify(notificationId, builder.build());
    }
}
