# Smart Reminder App - MaD Lab Assignment

An advanced Android application that helps users manage reminders with precise GPS location information and scheduled notifications.

## 📱 Features

-   **Add Reminders**: Create reminders with a title, description, and status.
-   **GPS Location Tracking**: Fetches high-accuracy GPS coordinates (Latitude & Longitude) using Google Play Services.
-   **Local Database**: Stores all reminders persistently using an SQLite database (`ReminderDB_41`).
-   **Background Processing**: Uses Android WorkManager to handle periodic checks and tasks in the background.
-   **Push Notifications**: Notifies the user about reminders even when the app is not actively in use.
-   **Dynamic Status**: Track the lifecycle of reminders (e.g., Pending, Completed).

## 🛠️ Tech Stack

-   **Language**: Java
-   **Database**: SQLite (Local storage)
-   **Background Services**: Android WorkManager (Periodic & One-time requests)
-   **Location API**: Google Play Services Fused Location Provider
-   **Architecture**: Material Design Components

## 📂 Project Structure

-   `MainActivity.java`: The primary dashboard with navigation to all features and background service management.
-   `AddReminderActivity.java`: UI and logic for creating new reminders, including GPS coordinate fetching.
-   `ViewRemindersActivity.java`: Displays list of all saved reminders stored in the local database.
-   `ReminderDatabaseHelper.java`: Manages the SQLite database creation, upgrades, and CRUD operations.
-   `ReminderWorker.java`: Handles background operations and sends notifications to the user.
-   `Reminder.java`: Data model class for a Reminder object.

## 🚀 Setup & Installation

1.  **Clone the project** to your local machine.
2.  Open the project in **Android Studio**.
3.  Ensure the following dependencies are synced (listed in `app/build.gradle`):
    -   `androidx.work:work-runtime`
    -   `com.google.android.gms:play-services-location`
    -   `com.google.android.material:material`
4.  **Permissions**:
    -   Make sure to grant **Location** and **Notification** permissions when prompted on the device.
    -   The app requires `ACCESS_FINE_LOCATION` for accurate GPS fetching.

## 📝 Implementation Details (Roll No: 41)

-   **Database Name**: `ReminderDB_41`
-   **Table Name**: `reminder_41`
-   **Extra Field**: `status` (Based on Roll No calculation: 41 % 3 = 2)
-   **Background Interval**: Set to 15 minutes (WorkManager minimum) to ensure periodic checks.
-   **Notification Channel**: `reminder_channel_41`

---
*Created as part of the Mobile Application Development (MaD) Laboratory course.*
