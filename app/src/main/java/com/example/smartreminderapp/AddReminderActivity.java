package com.example.smartreminderapp;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

/**
 * AddReminderActivity - Add a new reminder with GPS location
 * Roll No: 41
 * Extra field: status (41 % 3 = 2)
 */
public class AddReminderActivity extends AppCompatActivity {

    private static final String TAG = "AddReminderActivity";

    private TextInputEditText etTitle, etDescription, etTime, etStatus;
    private TextView tvLatitude, tvLongitude;
    private MaterialButton btnGetLocation, btnSaveReminder;
    private FusedLocationProviderClient fusedLocationClient;
    private ReminderDatabaseHelper dbHelper;

    private String currentLatitude = "";
    private String currentLongitude = "";

    // Location callback to receive fresh GPS coordinates
    private LocationCallback locationCallback;

    // Permission launcher for location
    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                if (fineLocationGranted != null && fineLocationGranted) {
                    fetchFreshLocation();
                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    fetchFreshLocation();
                } else {
                    Toast.makeText(this, "❌ Location permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        // Initialize views
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etTime = findViewById(R.id.etTime);
        etStatus = findViewById(R.id.etStatus);
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        btnSaveReminder = findViewById(R.id.btnSaveReminder);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize database helper
        dbHelper = new ReminderDatabaseHelper(this);

        // Set default status
        etStatus.setText("Pending");

        // Initialize location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.w(TAG, "LocationResult is null");
                    Toast.makeText(AddReminderActivity.this,
                            "❌ Could not get location. Make sure GPS is ON.", Toast.LENGTH_LONG).show();
                    btnGetLocation.setText("📍 Get Current Location (GPS)");
                    btnGetLocation.setEnabled(true);
                    return;
                }

                // Get the most recent location from the result
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    currentLatitude = String.valueOf(location.getLatitude());
                    currentLongitude = String.valueOf(location.getLongitude());

                    tvLatitude.setText("Latitude: " + currentLatitude);
                    tvLongitude.setText("Longitude: " + currentLongitude);

                    Log.d(TAG, "Location obtained: " + currentLatitude + ", " + currentLongitude);
                    Toast.makeText(AddReminderActivity.this,
                            "📍 Location fetched successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddReminderActivity.this,
                            "❌ Location is null. Ensure GPS is enabled.", Toast.LENGTH_LONG).show();
                }

                // Stop receiving updates after getting the location (we only need it once)
                fusedLocationClient.removeLocationUpdates(locationCallback);
                btnGetLocation.setText("📍 Get Current Location (GPS)");
                btnGetLocation.setEnabled(true);
            }
        };

        // Time picker - click on time field to show time picker dialog
        etTime.setOnClickListener(v -> showTimePicker());

        // Get current location using GPS
        btnGetLocation.setOnClickListener(v -> checkLocationPermissionAndFetch());

        // Save reminder to database
        btnSaveReminder.setOnClickListener(v -> saveReminder());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Make sure to remove location updates when activity is destroyed
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    /**
     * Show TimePickerDialog to select reminder time
     */
    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfDay) -> {
                    String amPm = hourOfDay >= 12 ? "PM" : "AM";
                    int displayHour = hourOfDay > 12 ? hourOfDay - 12 : (hourOfDay == 0 ? 12 : hourOfDay);
                    String formattedTime = String.format(Locale.getDefault(),
                            "%02d:%02d %s", displayHour, minuteOfDay, amPm);
                    etTime.setText(formattedTime);
                }, hour, minute, false);

        timePickerDialog.show();
    }

    /**
     * Check for location permission and fetch GPS location
     */
    private void checkLocationPermissionAndFetch() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else {
            fetchFreshLocation();
        }
    }

    /**
     * Actively request a FRESH GPS location using LocationRequest.
     * This is much more reliable than getLastLocation() which often returns null
     * on emulators and devices where no app has recently used location.
     */
    private void fetchFreshLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Show loading state on button
        btnGetLocation.setText("📍 Fetching location...");
        btnGetLocation.setEnabled(false);

        Toast.makeText(this, "📡 Requesting GPS location...", Toast.LENGTH_SHORT).show();

        // Create a high-accuracy location request
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 5000)   // 5 second interval
                .setMinUpdateIntervalMillis(2000)          // fastest 2 seconds
                .setMaxUpdates(1)                          // only need 1 location fix
                .setWaitForAccurateLocation(false)         // don't wait too long
                .build();

        // Request location updates (this actively turns on GPS and fetches)
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );

        Log.d(TAG, "Location request sent, waiting for GPS fix...");
    }

    /**
     * Validate input and save reminder to SQLite database
     */
    private void saveReminder() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        String time = etTime.getText() != null ? etTime.getText().toString().trim() : "";
        String status = etStatus.getText() != null ? etStatus.getText().toString().trim() : "";

        // Validation
        if (title.isEmpty()) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }

        if (time.isEmpty()) {
            etTime.setError("Time is required");
            etTime.requestFocus();
            return;
        }

        // Build location string
        String location = "";
        if (!currentLatitude.isEmpty() && !currentLongitude.isEmpty()) {
            location = "Lat: " + currentLatitude + ", Lng: " + currentLongitude;
        }

        // Create reminder object and save to database
        Reminder reminder = new Reminder(title, description, time, location, status);
        long id = dbHelper.insertReminder(reminder);

        if (id > 0) {
            Toast.makeText(this,
                    "✅ Reminder added successfully!\nID: " + id,
                    Toast.LENGTH_LONG).show();

            // Clear fields after saving
            etTitle.setText("");
            etDescription.setText("");
            etTime.setText("");
            etStatus.setText("Pending");
            tvLatitude.setText("Latitude: Not fetched");
            tvLongitude.setText("Longitude: Not fetched");
            currentLatitude = "";
            currentLongitude = "";
        } else {
            Toast.makeText(this, "❌ Error saving reminder", Toast.LENGTH_SHORT).show();
        }
    }
}
