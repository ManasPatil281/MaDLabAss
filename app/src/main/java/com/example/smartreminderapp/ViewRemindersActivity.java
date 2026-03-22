package com.example.smartreminderapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * ViewRemindersActivity - Display all stored reminders using RecyclerView
 * Roll No: 41
 * Retrieves data from reminder_41 table in ReminderDB_41
 */
public class ViewRemindersActivity extends AppCompatActivity {

    private RecyclerView recyclerViewReminders;
    private TextView tvReminderCount;
    private ReminderDatabaseHelper dbHelper;
    private ReminderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reminders);

        // Initialize views
        recyclerViewReminders = findViewById(R.id.recyclerViewReminders);
        tvReminderCount = findViewById(R.id.tvReminderCount);

        // Initialize database helper
        dbHelper = new ReminderDatabaseHelper(this);

        // Setup RecyclerView
        recyclerViewReminders.setLayoutManager(new LinearLayoutManager(this));

        // Load and display reminders
        loadReminders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReminders();
    }

    /**
     * Load all reminders from database and display in RecyclerView
     */
    private void loadReminders() {
        List<Reminder> reminderList = dbHelper.getAllReminders();

        if (reminderList.isEmpty()) {
            tvReminderCount.setText("No reminders found. Add one!");
        } else {
            tvReminderCount.setText(reminderList.size() + " reminder(s) stored in ReminderDB_41");
        }

        adapter = new ReminderAdapter(reminderList);
        recyclerViewReminders.setAdapter(adapter);
    }
}
