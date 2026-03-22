package com.example.smartreminderapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.button.MaterialButton;

import java.util.concurrent.TimeUnit;

/**
 * MainActivity - Home screen of Smart Reminder App
 * Roll No: 41
 * Features: Navigate to Add/View Reminders, Start Background Service
 */
public class MainActivity extends AppCompatActivity {

    private MaterialButton btnAddReminder, btnViewReminders, btnStartService, btnTestNotification;

    private static final String CHANNEL_ID = "reminder_channel_41";
    private static final String CHANNEL_NAME = "Reminder Notifications";

    // Launcher for POST_NOTIFICATIONS permission (Android 13+)
    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "✅ Notification permission granted!", Toast.LENGTH_SHORT).show();
                    // Now start the worker since permission is granted
                    scheduleWorkerAndTestNotification();
                } else {
                    Toast.makeText(this, "❌ Notification permission denied. Notifications will not work.", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create notification channel early (required for Android 8+)
        createNotificationChannel();

        // Initialize views
        btnAddReminder = findViewById(R.id.btnAddReminder);
        btnViewReminders = findViewById(R.id.btnViewReminders);
        btnStartService = findViewById(R.id.btnStartService);
        btnTestNotification = findViewById(R.id.btnTestNotification);

        // Navigate to Add Reminder screen
        btnAddReminder.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddReminderActivity.class);
            startActivity(intent);
        });

        // Navigate to View Reminders screen
        btnViewReminders.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewRemindersActivity.class);
            startActivity(intent);
        });

        // Start Background Service using WorkManager
        btnStartService.setOnClickListener(v -> {
            startReminderWorker();
        });

        // Test Notification button - sends a notification immediately
        btnTestNotification.setOnClickListener(v -> {
            // Check permission first on Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                    return;
                }
            }
            sendTestNotification();
            Toast.makeText(this, "🔔 Test notification sent!", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Create the notification channel (must be done before sending any notification)
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

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Start periodic background work using WorkManager
     * Roll No 41: 41 % 4 = 1 → 10 minutes interval
     * (Note: WorkManager minimum periodic interval is 15 minutes)
     */
    private void startReminderWorker() {
        // On Android 13+, check and request POST_NOTIFICATIONS permission first
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request notification permission
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return; // Will continue in the callback after permission is granted
            }
        }

        // Permission already granted or not needed (below Android 13)
        scheduleWorkerAndTestNotification();
    }

    /**
     * Schedule the periodic worker AND fire an immediate one-time worker + test notification
     */
    private void scheduleWorkerAndTestNotification() {
        // 1. Schedule periodic work request (10 minutes, but WorkManager enforces 15 min minimum)
        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(ReminderWorker.class, 15, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(
                        "ReminderWork_41",
                        ExistingPeriodicWorkPolicy.REPLACE,
                        periodicWorkRequest
                );

        // 2. Also fire an IMMEDIATE one-time work request so user sees it right away
        OneTimeWorkRequest immediateWorkRequest =
                new OneTimeWorkRequest.Builder(ReminderWorker.class)
                        .build();

        WorkManager.getInstance(this).enqueue(immediateWorkRequest);

        // 3. Send an immediate test notification to confirm everything works
        sendTestNotification();

        Toast.makeText(this,
                "✅ Background service started!\nNotification sent & checking every 10 min",
                Toast.LENGTH_LONG).show();
    }

    /**
     * Send an immediate test notification to verify notifications are working
     */
    private void sendTestNotification() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Reminder: Check your scheduled task")
                .setContentText("Smart Reminder background service is now active (Roll No 41)")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        notificationManager.notify(100, builder.build());
    }
}
