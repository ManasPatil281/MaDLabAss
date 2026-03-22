package com.example.smartreminderapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite Database Helper for Smart Reminder App
 * Roll No: 41
 * Database: ReminderDB_41
 * Table: reminder_41
 * Fields: id, title, description, time, location, status (41 % 3 = 2 → status)
 */
public class ReminderDatabaseHelper extends SQLiteOpenHelper {

    // Database name as per roll number 41
    private static final String DATABASE_NAME = "ReminderDB_41";
    private static final int DATABASE_VERSION = 1;

    // Table name as per roll number 41
    private static final String TABLE_NAME = "reminder_41";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_LOCATION = "location";
    // Extra field: status (Roll No 41, 41 % 3 = 2 → status)
    private static final String COLUMN_STATUS = "status";

    // Create table SQL
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_TITLE + " TEXT NOT NULL, "
                    + COLUMN_DESCRIPTION + " TEXT, "
                    + COLUMN_TIME + " TEXT, "
                    + COLUMN_LOCATION + " TEXT, "
                    + COLUMN_STATUS + " TEXT"
                    + ")";

    public ReminderDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Insert a new reminder into the database
     */
    public long insertReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, reminder.getTitle());
        values.put(COLUMN_DESCRIPTION, reminder.getDescription());
        values.put(COLUMN_TIME, reminder.getTime());
        values.put(COLUMN_LOCATION, reminder.getLocation());
        values.put(COLUMN_STATUS, reminder.getStatus());

        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    /**
     * Get all reminders from the database
     */
    public List<Reminder> getAllReminders() {
        List<Reminder> reminderList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder();
                reminder.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                reminder.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                reminder.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                reminder.setTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)));
                reminder.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)));
                reminder.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));

                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return reminderList;
    }

    /**
     * Get the count of reminders
     */
    public int getReminderCount() {
        String countQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    /**
     * Delete a reminder by ID
     */
    public void deleteReminder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
